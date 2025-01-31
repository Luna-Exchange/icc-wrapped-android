package com.icc.iccwrapped

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient

class IccWebViewClient(private val onIccWebViewInterface: IccWebViewInterface) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onIccWebViewInterface.onPageStarted()
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        onIccWebViewInterface.onPageFinished()
    }
}


interface IccWebViewInterface {
    fun onPageStarted()

    fun onPageFinished()

}