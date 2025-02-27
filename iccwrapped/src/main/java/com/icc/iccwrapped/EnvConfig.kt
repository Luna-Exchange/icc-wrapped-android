package com.icc.iccwrapped

class EnvConfig(private val env: Env = Env.DEVELOPMENT) {

    val iccUi : String
        get() = if (env == Env.DEVELOPMENT) "https://icc-wrapped-frontend.vercel.app" else "https://icc-wrapped.insomnialabs.xyz"

    val iccApi : String
        get() = if (env == Env.DEVELOPMENT) "https://icc-fan-passport-stg-api.insomnialabs.xyz" else "https://passport-api.icc-cricket.com"

}


enum class Env {
    DEVELOPMENT,
    PRODUCTION
}

enum class SdkActions {
    DEFAULT,
    SIGN_IN,
}