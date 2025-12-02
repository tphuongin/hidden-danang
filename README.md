# Hidden Danang - Discover Da Nang's Hidden Gems

A modern Android application that helps users discover hidden places, attractions, and local gems in Da Nang, Vietnam.

## 📱 Features

### Core Features

- **Interactive Map**: Real-time map with location-based place discovery
- **Place Discovery**: Find nearby restaurants, cafes, attractions with detailed information
- **Favorites System**: Save and manage favorite places
- **Reviews & Ratings**: Read and write reviews for places
- **Turn-by-Turn Navigation**: Get directions with real-time route information
- **User Profiles**: Personalized user accounts with statistics
- **Multi-language Support**: Vietnamese and English interface

### Technical Features

- **Real-time Updates**: Firebase Firestore integration for live data
- **Authentication**: Secure Firebase Auth with session persistence
- **Offline Support**: Cached data for offline browsing
- **AI Integration**: Gemini AI for travel planning assistance
- **Distance Calculation**: Haversine formula for accurate distance measurement
- **Geohashing**: Efficient place search using geohash optimization

## 🏗️ Architecture

### Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Build System**: Gradle with Kotlin DSL
- **Backend**: Firebase (Authentication, Firestore, Cloud Storage)
- **Mapping**: MapLibre GL for interactive maps
- **API Integration**: Goong Maps API for directions and nearby places

### Project Structure

```
app/
├── src/main/
│   ├── java/com/hiddendanang/app/
│   │   ├── api/                 # API clients (Goong, etc.)
│   │   ├── data/
│   │   │   ├── model/           # Data models (Place, Review, User)
│   │   │   ├── remote/          # Firebase data sources
│   │   │   └── repository/      # Repository pattern implementations
│   │   ├── di/                  # Dependency injection
│   │   ├── ui/
│   │   │   ├── screen/          # Composable screens
│   │   │   ├── components/      # Reusable UI components
│   │   │   └── theme/           # Design system
│   │   ├── utils/               # Utility functions
│   │   └── viewmodel/           # ViewModel implementations
│   └── AndroidManifest.xml      # App manifest
├── build.gradle.kts             # App-level build configuration
└── google-services.json         # Firebase configuration
```

## 🚀 Getting Started

### Prerequisites

- Android Studio (Flamingo or later)
- JDK 17+
- Firebase project setup
- Goong Maps API key

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/tphuongin/hidden-danang.git
   cd HiddenDaNang
   ```

2. **Configure Firebase**

   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Download `google-services.json`
   - Place it in the `app/` directory

3. **Set API Keys**

   - Add Goong Maps API key to `app/src/main/res/values/strings.xml`:
     ```xml
     <string name="goong_base_url">https://rsapi.goongmaps.com/</string>
     <string name="map_key">YOUR_GOONG_API_KEY</string>
     ```
   - Add Gemini API key in your build configuration

4. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

## 📋 System Requirements

- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Permissions**: Location, Internet, Camera, Storage

## 🔑 Key Features Explained

### Place Discovery

- Search places by location or category
- View detailed place information (price range, operating hours, contact)
- See photos and ratings
- Read user reviews

### Navigation

- Get turn-by-turn directions
- View route distance and estimated time
- Multiple route options (if available)
- Real-time direction rendering on map

### User Reviews

- Submit ratings and reviews
- Upload photos with reviews
- View community feedback
- Track your review statistics

### Favorites Management

- Save places to favorites
- Organize favorite places
- Quick access from home screen
- Share favorite places

## 🔧 Configuration

### Firebase Setup

The app uses the following Firebase services:

- **Authentication**: Email/password sign up and login
- **Firestore**: Real-time database for places, reviews, and user data
- **Cloud Storage**: Store user photos and place images

### API Integration

- **Goong Maps**: Directions API, Nearby Places API, Geocoding
- **Gemini AI**: Travel planning and recommendations

## 🛠️ Development

### Code Style

- Follows Kotlin style guidelines
- Uses Jetpack Compose for UI
- Reactive programming with Coroutines and Flow
- MVVM architecture pattern

### Building Variants

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

## 📦 Dependencies

### Core Android

- Jetpack Compose UI
- Navigation Compose
- Lifecycle ViewModels
- Coroutines

### Firebase

- Firebase Authentication
- Cloud Firestore
- Cloud Storage

### Mapping & Location

- MapLibre GL Android
- Google Play Services Location

### Networking

- Retrofit for HTTP
- OkHttp for logging

### Other

- Coil for image loading
- Lucide icons for UI
- Google Generative AI (Gemini)

## 🐛 Known Issues

- First app launch may take longer due to data initialization
- Offline mode has limited functionality
- Some location features require GPS enabled

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Team

- **Developer**: Phuong, Nhat, Ky
- **Project**: Hidden Danang - Discover Local Gems

## 📧 Contact & Support

For issues, questions, or suggestions:

- Create an issue on GitHub
- Contact: [Your Contact Information]

## 🎯 Future Enhancements

- [ ] Social sharing features
- [ ] User-generated content (UGC) section
- [ ] Event calendar
- [ ] Travel planning tools
- [ ] Offline maps download
- [ ] Photo gallery enhancement
- [ ] Real-time recommendations
- [ ] Trip history and statistics

## 📊 Statistics

- **Total Places**: Dynamic (Firestore-based)
- **User Reviews**: Community-driven
- **Supported Languages**: Vietnamese, English
- **Map Coverage**: Da Nang and surrounding areas

---

**Last Updated**: December 2, 2025  
**Version**: 1.0  
**Status**: Active Development
