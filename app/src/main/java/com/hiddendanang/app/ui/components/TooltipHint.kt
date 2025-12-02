package com.hiddendanang.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hiddendanang.app.ui.theme.Dimens
import kotlinx.coroutines.delay

/**
 * Tooltip hint component with chat bubble design and smooth bubble rising animation
 * The bubble rises slowly and smoothly like a real floating balloon, then fades away
 *
 * @param text The hint text to display
 * @param modifier Optional modifier for the component
 * @param visible Controls visibility of the tooltip
 * @param onDismiss Callback when tooltip is dismissed (auto or manual)
 * @param autoDismissDelayMs Duration before auto-dismissing (0 = no auto-dismiss)
 */
@Composable
fun TooltipHint(
    text: String,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onDismiss: () -> Unit = {},
    autoDismissDelayMs: Long = 5000L
) {
    // Track dismissal to prevent multiple callbacks
    var isVisible by remember { mutableStateOf(visible) }
    
    // Bubble rising animation state with random curve
    var offsetY by remember { mutableStateOf(350.dp) } // Start at bottom
    var offsetX by remember { mutableStateOf(320.dp) } // Start at far right (screen width ~360dp)
    var alpha by remember { mutableStateOf(0f) }
    // Randomize path for each bubble
    val randomAmplitude = remember { (30..60).random() }
    val randomFrequency = remember { (1..3).random() }
    val randomPhase = remember { (0..360).random() }
    
    // Auto-dismiss effect
    LaunchedEffect(visible, isVisible) {
        if (isVisible && visible && autoDismissDelayMs > 0) {
            delay(autoDismissDelayMs)
            isVisible = false
            onDismiss()
        }
    }
    
    // Update visibility when external state changes
    LaunchedEffect(visible) {
        isVisible = visible
    }
    
    // Bubble rising animation (slower, random, diagonal)
    LaunchedEffect(isVisible) {
        if (isVisible) {
            // Phase 1: Appear and fly from bottom right to top left (6 seconds) with pronounced curve
            for (i in 0..300) {
                val progress = i / 300f  // 0 to 1 over 6 seconds
                // Curved path: bottom right to top left
                offsetY = (350 - 600 * progress + 40 * kotlin.math.sin(2 * Math.PI * progress)).dp
                offsetX = (320 - 340 * progress + 60 * kotlin.math.sin(3 * Math.PI * progress)).dp
                // Fade in smoothly
                alpha = minOf(1f, progress * 2f)
                delay(20)
            }
            // Phase 2: Float and fade out (6 seconds, even slower fade)
            for (j in 0..300) {
                val progress = j / 300f
                offsetY = (-250 - 100 * progress + 20 * kotlin.math.sin(2 * Math.PI * progress)).dp
                offsetX = (-20 + 20 * kotlin.math.sin(3 * Math.PI * progress)).dp
                alpha = 1f - progress
                delay(20)
            }
            isVisible = false
            onDismiss()
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(999f)
            .wrapContentHeight()
    ) {
            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .alpha(alpha)
                    .width(220.dp)
                    .height(80.dp)
                    .shadow(
                        elevation = Dimens.ElevationXLarge,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(40.dp)
                    )
                    .clip(
                        androidx.compose.foundation.shape.RoundedCornerShape(40.dp)
                    )
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                            )
                        )
                    )
                    .border(
                        width = Dimens.StrokeSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(40.dp)
                    )
                    .clickable {
                        isVisible = false
                        onDismiss()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(Dimens.PaddingMedium)
                        .fillMaxWidth()
                )
            }
    }
}

/**
 * Alternative tooltip hint with custom styling and smooth bubble rising animation
 * The bubble rises slowly and smoothly like a real floating balloon, then fades away
 * 
 * @param text The hint text to display
 * @param modifier Optional modifier for the component
 * @param visible Controls visibility of the tooltip
 * @param onDismiss Callback when tooltip is dismissed
 * @param autoDismissDelayMs Duration before auto-dismissing
 * @param backgroundColor Custom background color
 * @param textColor Custom text color
 */
@Composable
fun TooltipHintStyled(
    text: String,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onDismiss: () -> Unit = {},
    autoDismissDelayMs: Long = 5000L,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    var isVisible by remember { mutableStateOf(visible) }
    
    // Bubble rising animation state with smooth curve
    var offsetY by remember { mutableStateOf(300.dp) }
    var offsetX by remember { mutableStateOf(0.dp) }
    var alpha by remember { mutableStateOf(0f) }
    
    // Auto-dismiss effect
    LaunchedEffect(visible, isVisible) {
        if (isVisible && visible && autoDismissDelayMs > 0) {
            delay(autoDismissDelayMs)
            isVisible = false
            onDismiss()
        }
    }
    
    // Update visibility when external state changes
    LaunchedEffect(visible) {
        isVisible = visible
    }
    
    // Smooth bubble rising animation (like real balloon)
    LaunchedEffect(isVisible) {
        if (isVisible) {
            // Phase 1: Appear and rise up slowly (2.5 seconds) with gentle curve
            for (i in 0..250) {
                val progress = i / 250f  // 0 to 1 over 2.5 seconds
                val sineWave = kotlin.math.sin(progress * Math.PI.toFloat() * 0.5f)
                
                // Vertical movement: rise smoothly from 300dp to -400dp
                offsetY = (300 - 700 * progress).dp
                
                // Horizontal gentle wave motion (narrower, slower)
                offsetX = (sineWave * 40).dp
                
                // Fade in smoothly
                alpha = minOf(1f, progress * 2f)
                
                delay(10)
            }
            
            // Phase 2: Keep visible for a moment while continuing slow rise
            for (j in 0..100) {
                val progress = j / 100f
                val sineWave = kotlin.math.sin((0.5f + progress * 0.5f) * Math.PI.toFloat())
                
                offsetY = (-400 - 100 * progress).dp
                offsetX = (sineWave * 40).dp
                alpha = 1f
                
                delay(20)
            }
            
            // Phase 3: Fade out slowly while continuing to rise (2 seconds)
            for (k in 100 downTo 0) {
                val progress = k / 100f
                val sineWave = kotlin.math.sin(Math.PI.toFloat())
                
                offsetY = (-500 - 200 * (1 - progress)).dp
                offsetX = (sineWave * 40 * progress).dp
                alpha = progress
                
                delay(20)
            }
            
            isVisible = false
            onDismiss()
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingMedium),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .offset(x = offsetX, y = offsetY)
                .alpha(alpha)
                .shadow(
                    elevation = Dimens.ElevationXLarge,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(Dimens.CornerXLarge)
                )
                .clip(
                    androidx.compose.foundation.shape.RoundedCornerShape(Dimens.CornerXLarge)
                )
                .background(backgroundColor.copy(alpha = 0.9f))
                .clickable {
                    isVisible = false
                    onDismiss()
                }
                .padding(
                    horizontal = Dimens.PaddingLarge,
                    vertical = Dimens.PaddingMedium
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
