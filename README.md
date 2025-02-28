# ICC Recapped Android SDK

An Android SDK that provides a simple way to launch a web view in Android applications.

---

## 📜 Contents
- [Installation](#installation)
- [Launching the SDK](#launching-the-sdk)
- [Launch Arguments](#launch-arguments)
- [Authentication Flow](#authentication-flow)

---

## 🚀 Installation

### 1️⃣ Add the Dependency

#### **Kotlin DSL**
```kotlin
implementation("com.github.Luna-Exchange:icc-wrapped-android:x.x.x")
```

#### **Groovy DSL**
```groovy
implementation 'com.github.Luna-Exchange:icc-wrapped-android:x.x.x'
```
Replace `x.x.x` with the latest version (`1.0.3`).

### 2️⃣ Configure `settings.gradle`

#### **Kotlin DSL**
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

#### **Groovy DSL**
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 3️⃣ Sync the project
Run a Gradle sync in your Android project.

---

## 📲 Launching the SDK

### **When a user is authenticated**
```kotlin
IccWrappedActivity.launch(this, user)
```

### **When a user is not authenticated**, where onAuthenticate is a callback where ICC can login and relaunch the SDK again.
```kotlin
IccWrappedActivity.launch(
    context = this,
    onAuthenticate = onAuthenticate
)
```

### **Delegate Sign-in to ICC**
If sign-in is required after clicking a `enter recapped` button in the SDK, implement the `OnAuthenticate` interface:

```kotlin
val onAuthenticate = object : OnAuthenticate {
    override fun signIn() {
        val user = User(email, authToken, name)
        IccWrappedActivity.launch(this@MainActivity, user, onAuthenticate)
    }
}
```

### **Delegate Sign-in to ICC**
`onStayInGame` is a function passed into the sdk during initialization it gets invoked when the ICC recapped is completed and the user clicks on the `ICC CRICKET` button as shown below.

![PHOTO-2025-02-27-16-03-15](https://github.com/user-attachments/assets/c5af385a-a0fe-4c55-86ff-22d414fd40c6)

---

## 🏗️ Launch Arguments
The SDK launch function accepts optional arguments:

### **User Data**
```kotlin
val userData = User(
    token = "user_token",
    name = "User Name",
    email = "user@example.com"
)
```

### **Environment**
The `Environment` enum is used to specify the SDK environment:

```kotlin
enum class Environment {
    DEVELOPMENT,
    PRODUCTION
}
```

### **Authentication Delegation**
This interface helps with sign-in delegation when the user attempts to sign in via **IccRecapped**.


### **Stay in Game (onStayInGame) **
This interface closes the SDK and provides a callback that allows the SDK caller to navigate to another section of the ICC app after the recap experience is completed.

---

## 🔐 Authentication Flow
This flow applies to users who use **IccRecapped** without being authenticated via the ICC app.

When calling the SDK, pass an authentication interface as an argument:

```kotlin
val onAuthenticate = object : OnAuthenticate {
    override fun signIn() {
        val param = SdkParam(user)
        IccWrappedActivity.launch(this@MainActivity, param, null)
    }
}
```

In this interface, the `signIn()` function handles authentication and then launches the SDK with the user object.

This flow is triggered when the **`enter recapped` button is clicked on the WebView**, and the user is authenticated on **IccRecapped**.

---
