package com.icc.iccwrapped

class EnvConfig(private val env: Env = Env.DEVELOPMENT) {

    val iccUi : String
        get() = if (env == Env.DEVELOPMENT) "https://iccwrapped-ui-dev.aws.insomnialabs.xyz" else "https://recapped.icc-cricket.com"

    val iccApi : String
        get() = if (env == Env.DEVELOPMENT) "https://iccwrapped-api-dev.aws.insomnialabs.xyz" else "https://recapped-api.icc-cricket.com"

}


enum class Env {
    DEVELOPMENT,
    PRODUCTION
}

enum class SdkActions {
    DEFAULT,
    SIGN_IN,
}