An Android SDK that provides a simple way to launch a web view in Android applications.

*Contents*

Steps to Install

Add the dependency to your build.gradle.
Kotlin

implementation("com.github.Luna-Exchange:icc-wrapped-android:x.x.x")

Groovy

implementation 'com.github.Luna-Exchange:icc-wrapped-android:Tag

where x.x.x is the latest version 1.0.0

In settings.gradle
Kotlin

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

Groovy

dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
Sync the project.
**Launch this SDK **

When a user is authenticated.
     IccWrappedActivity.launch(this, user)
When a user is not authenticated.
     IccWrappedActivity.launch(context = this, onAuthenticate = onAuthenticate)
To Delegate Sign in to ICC after Signin has been clicked on the SDK

        val onAuthenticate = object : OnAuthenticate {
            override fun signIn()  {
                val user = User(email, authToken, name)
                IccWrappedActivity.launch(this@MainActivity, user, onAuthenticate)
            }
        }

Launch Arguments

This is a function that helps launch the SDK. It accepts optional arguments that include;

context.
user of type User.

val userData = User(
    token: "user_token",
    name: "User Name",
    email: "user@example.com"
)
environment of enum type Environment: To declare the environment.
an interface helps with sign-in delegation when the user attempts to sign in to fanpassport.


Environment DEVELOPMENT, PRODUCTION

Authentication Flow

This flow caters to users who use fan passports without getting authenticated via the ICC app. The expectation is that when calling the Sdk, an interface should be passed as an argument. e.g

  val onAuthenticate = object : OnAuthenticate {
            override fun signIn()  {
                val param = SdkParam(user)
                IccWrappedActivity.launch(this@MainActivity, param, null)
            }
        }
In this interface, a signIn() function handles authentication and then calls the SDK with the user object, as shown above. Therefore, this flow is executed when sign-in is clicked on the WebView, and the user is authenticated on a fan passport.

