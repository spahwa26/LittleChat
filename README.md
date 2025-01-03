# LittleChat 📱💬  

**LittleChat** is a sleek and feature-rich chat application built for Android using **Jetpack Compose** and the **MVVM architecture**. This app leverages **Firebase** services to provide a real-time messaging experience with modern UI features and robust functionality.  
[APK Link](https://www.dropbox.com/scl/fi/xhkawg8if1xagp275nsgg/LittleChat-v9.apk?rlkey=y2oty7w1bdrcyi9ko9smkg78b&e=1&dl=0)
---

## 🌟 Features  

### 🔐 **Authentication**  
- Secure login with Firebase Email Authentication.  
- Sign up with email verification for added security.  

### 👫 **Friend Management**  
- View and manage your friend list on the **Friends Screen**.  
- Check detailed information on the **Friend's Profile Screen**.   
- Search for friends by name or email.  
- Send and manage friend requests easily.  

### 💬 **Chat Features**  
- **Private Chat**: One-on-one real-time messaging.  
- **Group Chat**:  
  - View group messages and manage participants.  
  - Create, update, and personalize groups.  
  - Access group details in the **Group Info Screen**.  

### ⚙️ **Settings & Customization**  
- Toggle between **light** and **dark modes**.  
- Dynamic theming that adapts to system colors.  
- Update profile details effortlessly.  
- Logout with a single tap.  

### 🔔 **Notifications**  
- Real-time messaging notifications via Firebase Cloud Messaging (FCM).  

---

## 🛠️ Technology Stack  

| **Category**     | **Technology**         |  
|-------------------|------------------------|  
| Frontend         | Jetpack Compose        |  
| Architecture     | MVVM                   |  
| Backend          | Firebase (Auth, Realtime DB, FCM, Cloud Functions) |  
| Programming Lang | Kotlin                 |  

---

## 📷 Screenshots (Optional)  

> If you'd like to showcase the app, add screenshots here.  

| Screen Name           | Screenshot |  
|-----------------------|------------|  
| **Login Screen**      | <img src="readmescreens/login.png" width="200"/> |  
| **Chat Screen**       | <img src="readmescreens/one_to_one_chat.png" width="200"/> |  
| **Settings Screen**   | <img src="readmescreens/settings.png" width="200"/> |  
| **Group Chat Screen** | <img src="readmescreens/group_chat.png" width="200"/> |  

---

## 🚀 How to Run  

### Prerequisites  
- Android Studio **Arctic Fox** or newer.  
- Minimum SDK: **24**.  
- A Firebase project with required services enabled.  

### Steps  

1. Clone the repository:  
   ```bash  
   git clone https://github.com/spahwa26/LittleChat.git  

2. Open the project in Android Studio.

3. Add google-services.json from your Firebase console to the app folder.

4. Build and run the app on an emulator or physical device.

### 🎯 Firebase Cloud Functions -> Custom Cloud Functions are deployed to handle:
- Sending friend requests.
- Managing group chats and data synchronization.
- Sending push notifications via FCM.


### 💡 About -> LittleChat is designed as a personal project to demonstrate expertise in:
- Building scalable Android apps with Jetpack Compose.
- Integrating Firebase for backend services.
- Implementing dynamic themes and real-time messaging.
- This project showcases end-to-end app development, including deploying Firebase Cloud Functions for robust backend functionality.
