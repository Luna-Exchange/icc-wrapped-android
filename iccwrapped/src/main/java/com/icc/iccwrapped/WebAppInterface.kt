package com.icc.iccwrapped

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface

class WebAppInterface(private val onJavScriptInterface: OnJavScriptInterface) {

    @JavascriptInterface
    fun receiveSignInEvent(data: String): Boolean {
        onJavScriptInterface.onAuthenticateWithIcc()
        return true
    }

    @JavascriptInterface
    fun receiveCloseEvent(data: String) : Boolean {
        onJavScriptInterface.onClose()
        return true
    }

    @JavascriptInterface
    fun receiveShareRecappedEvent(message: String, image: String) : Boolean {
        Handler(Looper.getMainLooper()).post {
            onJavScriptInterface.onShareRecapped(message, image)
        }
        return true
    }

}

interface OnJavScriptInterface{

    fun onAuthenticateWithIcc()

    fun onClose() {}

    fun onShareRecapped(message: String, image: String)

}