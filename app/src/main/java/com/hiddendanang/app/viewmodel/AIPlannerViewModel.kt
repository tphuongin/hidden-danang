package com.hiddendanang.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.Timestamp
import com.hiddendanang.app.data.model.ChatMessage
import com.hiddendanang.app.data.repository.ChatRepository
import com.hiddendanang.app.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import com.hiddendanang.app.BuildConfig

class AIPlannerViewModel : ViewModel() {

    private val chatRepository = ChatRepository()
    private val locationRepository = LocationRepository()
    
    private val apiKey = BuildConfig.GEMINI_API_KEY

    private var generativeModel: GenerativeModel? = null
    private var chatSession: com.google.ai.client.generativeai.Chat? = null

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentSessionId: String? = null

    init {
        initializeAI()
    }

    private fun initializeAI() {
        viewModelScope.launch {
            // 1. Lấy dữ liệu địa điểm từ DB
            val places = try {
                locationRepository.getAllPlaces().getOrNull() ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            // 2. Tạo context cho AI
            val placesContext = places.joinToString("\n") { 
                "ID: ${it.id} | Tên: ${it.name} | Địa chỉ: ${it.address.district}" 
            }

            // 3. Khởi tạo Model với System Instruction chứa dữ liệu thật
            generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash", // Dùng model stable
                apiKey = apiKey,
                generationConfig = generationConfig {
                    temperature = 0.5f // Giảm creativity để bám sát data
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 2048
                },
                systemInstruction = content { 
                    text("Bạn là trợ lý du lịch của ứng dụng Hidden Danang. " +
                         "Dưới đây là danh sách các địa điểm có trong hệ thống:\n$placesContext\n\n" +
                         "QUY TẮC TRẢ LỜI:\n" +
                         "1. Chỉ gợi ý các địa điểm có trong danh sách trên.\n" +
                         "2. Khi nhắc đến tên địa điểm, BẮT BUỘC dùng định dạng: [ID|Tên địa điểm]. Ví dụ: [123abc|Mỳ Quảng Bà Mua].\n" +
                         "3. Nếu người dùng hỏi địa điểm không có trong danh sách, hãy nói là bạn chưa có thông tin.\n" +
                         "4. Trả lời ngắn gọn, thân thiện, có ích cho việc lên lịch trình.")
                }
            )

            // 4. Load lịch sử chat
            chatRepository.getSessionsStream().collectLatest { sessions ->
                if (sessions.isNotEmpty() && currentSessionId == null) {
                    loadSession(sessions.first().id)
                } else if (sessions.isEmpty() && currentSessionId == null) {
                    createNewSession()
                }
            }
        }
    }

    private fun loadSession(sessionId: String) {
        currentSessionId = sessionId
        viewModelScope.launch {
            chatRepository.getMessagesStream(sessionId).collectLatest { msgs ->
                _messages.value = msgs
                
                if (generativeModel != null) {
                    if (msgs.isNotEmpty()) {
                        try {
                            val history = msgs.map { msg ->
                                content(if (msg.role == "user") "user" else "model") {
                                    text(msg.content)
                                }
                            }
                            chatSession = generativeModel!!.startChat(history)
                        } catch (e: Exception) {
                            chatSession = generativeModel!!.startChat()
                        }
                    } else {
                        chatSession = generativeModel!!.startChat()
                    }
                }
            }
        }
    }

    fun createNewSession() {
        viewModelScope.launch {
            val newId = chatRepository.createSession("Trip Plan ${System.currentTimeMillis()}")
            currentSessionId = newId
            _messages.value = emptyList()
            
            if (generativeModel != null) {
                chatSession = generativeModel!!.startChat()
            }
            
            loadSession(newId)
            
            val greeting = ChatMessage(
                role = "model",
                content = "Chào bạn! \uD83D\uDC4B Tôi là trợ lý Hidden Danang. Tôi có thể gợi ý các địa điểm ăn uống, vui chơi có trong ứng dụng. Bạn cần tìm gì?"
            )
            chatRepository.addMessage(newId, greeting)
        }
    }
    
    fun clearHistory() {
        createNewSession()
    }

    fun sendMessage(userContent: String) {
        val sessionId = currentSessionId ?: return
        val session = chatSession // Capture current session
        
        val userMessage = ChatMessage(
            role = "user",
            content = userContent,
            timestamp = Timestamp.now()
        )
        
        viewModelScope.launch {
            chatRepository.addMessage(sessionId, userMessage)
            
            if (session == null) {
                 val errorMessage = ChatMessage(
                    role = "model",
                    content = "Đang khởi tạo dữ liệu, vui lòng thử lại sau giây lát...",
                    timestamp = Timestamp.now()
                )
                chatRepository.addMessage(sessionId, errorMessage)
                return@launch
            }

            _isLoading.value = true
            
            try {
                val response = session.sendMessage(userContent)
                val aiResponseContent = response.text ?: "Xin lỗi, tôi không có câu trả lời."
                
                val aiMessage = ChatMessage(
                    role = "model",
                    content = aiResponseContent,
                    timestamp = Timestamp.now()
                )
                
                chatRepository.addMessage(sessionId, aiMessage)
                
            } catch (e: Exception) {
                Log.e("AIPlanner", "Error calling Gemini", e)
                val errorMessage = ChatMessage(
                    role = "model",
                    content = "Lỗi kết nối: ${e.localizedMessage}",
                    timestamp = Timestamp.now()
                )
                chatRepository.addMessage(sessionId, errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }
}