# ICC Wrapped Android SDK

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
Replace `x.x.x` with the latest version (`1.0.0`).

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

### **When a user is not authenticated**
```kotlin
IccWrappedActivity.launch(
    context = this,
    onAuthenticate = onAuthenticate
)
```

### **Delegate Sign-in to ICC**
If sign-in is required after clicking a login button in the SDK, implement the `OnAuthenticate` interface:

```kotlin
val onAuthenticate = object : OnAuthenticate {
    override fun signIn() {
        val user = User(email, authToken, name)
        IccWrappedActivity.launch(this@MainActivity, user, onAuthenticate)
    }
}
```

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
This interface helps with sign-in delegation when the user attempts to sign in via **IccWrapped**.

---

## 🔐 Authentication Flow
This flow applies to users who use **IccWrapped** without being authenticated via the ICC app.

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

This flow is triggered when the **sign-in button is clicked on the WebView**, and the user is authenticated on **IccWrapped**.

---
