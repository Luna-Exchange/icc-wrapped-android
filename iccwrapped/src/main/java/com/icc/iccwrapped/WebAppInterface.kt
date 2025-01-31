package com.icc.iccwrapped

import android.webkit.JavascriptInterface

class WebAppInterface(private val onJavScriptInterface: OnJavScriptInterface) {

    @JavascriptInterface
    fun receiveEvent(data: String): Boolean {
        onJavScriptInterface.onNavigateBack()
        return true
    }

    @JavascriptInterface
    fun receiveSignInEvent(data: String): Boolean {
        onJavScriptInterface.onAuthenticateWithIcc()
        return true
    }

    @JavascriptInterface
    fun receiveStayInGameEvent(data: String) : Boolean {
        onJavScriptInterface.onDeepLinkToStayInGame()
        return true
    }

    @JavascriptInterface
    fun receiveCloseEvent(data: String) : Boolean {
        onJavScriptInterface.onClose()
        return true
    }

}

interface OnJavScriptInterface{
    fun onNavigateBack()

    fun onAuthenticateWithIcc()

    fun onDeepLinkToStayInGame() {}

    fun onClose() {}
}