package com.icc.iccwrapped

class EnvConfig(private val environment: Environment = Environment.DEVELOPMENT) {

    val iccUi : String
        get() = if (environment == Environment.DEVELOPMENT) "https://icc-wrapped-frontend.vercel.app" else "https://icc-wrapped.insomnialabs.xyz"

    val iccApi : String
        get() = if (environment == Environment.DEVELOPMENT) "https://icc-fan-passport-stg-api.insomnialabs.xyz" else "https://passport-api.icc-cricket.com"

}


enum class Environment {
    DEVELOPMENT,
    PRODUCTION
}

enum class SdkActions {
    DEFAULT,
    SIGN_IN,
}