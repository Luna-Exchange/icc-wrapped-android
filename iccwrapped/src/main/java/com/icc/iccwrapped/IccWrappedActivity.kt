package com.icc.iccwrapped

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebStorage
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.icc.iccwrapped.extensions.getImageUri
import com.icc.iccwrapped.extensions.openShareSheet
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


const val STORAGE_PERMISSION_CODE = 1001

class IccWrappedActivity : AppCompatActivity(), OnJavScriptInterface, IccWebViewInterface {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var background: ConstraintLayout
    private var arguments: SdkParam? = null
    private val viewModel: WrappedViewModel by viewModels()
    private lateinit var config: EnvConfig
    private lateinit var sharedPrefProvider: SharedPrefProvider
    private var shouldRefresh = true


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_icc_wrapped)
        setupAndOpenWrapped()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAndOpenWrapped() {
        setupViews()
        setupConfig()
        setupWebView()
        if (!Environment.isExternalStorageManager()) {
            requestManageExternalStoragePermission()
        }
        openWrappedExperience()
    }

    private fun setupConfig() {
        sharedPrefProvider = SharedPrefProvider(this)
        config = EnvConfig(arguments?.env ?: Env.DEVELOPMENT)
    }

    private fun setupViews() {
        progressBar = findViewById(R.id.progress_bar)
        background = findViewById(R.id.constraint_layout)
        arguments = intent.getParcelableExtra(PARAM_EXTRA)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupWebView() {
        webView = findViewById(R.id.web_view)
        val webSettings = webView.settings
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        webView.webViewClient = IccWebViewClient(this)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                return super.onConsoleMessage(consoleMessage)
            }
        }

        webView.setDownloadListener { url, _, contentDisposition, mimeType, _ ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    requestManageExternalStoragePermission()
                } else {
                    downloadFile(url, contentDisposition)
                }
            } else {
                downloadFile(url, contentDisposition)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun downloadFile(
        url: String,
        message: String,
        type: DownloadType = DownloadType.DOWNLOAD
    ) {
        if (url.startsWith("data:image")) {
            downloadBase64Image(url, message, type)
        } else {
            Toast.makeText(this, "Unsupported URL format", Toast.LENGTH_SHORT).show()
        }
    }


    private fun downloadBase64Image(base64Data: String, message: String = "", type: DownloadType) {
        try {
            if (type == DownloadType.DOWNLOAD) {
                Toast.makeText(this, "downloading...", Toast.LENGTH_SHORT).show()
            }
            val base64Image = base64Data.substringAfter("base64,")
            val decodedBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            val fileName = "icc_recapped_image${System.currentTimeMillis()}.png"
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = Uri.fromFile(file)
            sendBroadcast(mediaScanIntent)

            if (type == DownloadType.SHARE) {
                shareIccWrapped(getImageUri(file), message)
            } else {
                Toast.makeText(this, "Image downloaded", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to download image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareIccWrapped(imageUri: Uri, message: String) {
        this.openShareSheet(imageUri, message)
    }



    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestManageExternalStoragePermission() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    private fun openWrappedExperience() {
        val token = sharedPrefProvider.getAccessToken()
        if (token.isEmpty()) {
            clearWebViewCache()
            val url = "${config.iccUi}?wrapped_access=${token}&icc_client=mobile_app"
            loadUrlWithWebView(url)
        } else {
            encodeUser(arguments?.user)
            observeViewModel()
        }
    }

    private fun observeViewModel() {
        if (!shouldRefresh) return
        lifecycleScope.launch {
            viewModel.token
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect(::loadUrl)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        shouldRefresh = true
        setupAndOpenWrapped()
    }


    private fun encodeUser(user: User?) {
        viewModel.encodeUser(user, config.iccApi)
    }

    private fun loadUrl(result: Result) {
        if (!shouldRefresh) return
        when (result) {
            is Result.Success -> {
                sharedPrefProvider.saveAccessToken(result.token)
                loadUrlBasedOnActions()
            }

            is Result.Failed -> {
                finish()
            }

            is Result.Default -> {}
        }
    }

    private fun loadUrlBasedOnActions() {
        val token = sharedPrefProvider.getAccessToken()
        val url = "${config.iccUi}?wrapped_access=${token}&icc_client=mobile_app"
        loadUrlWithWebView(url)
    }

    private fun loadUrlWithWebView(url: String) {
        webView.loadUrl(url)
    }


    override fun onAuthenticateWithIcc() {
            SharedPrefProvider(this).saveState(SdkActions.SIGN_IN)
            finish()
            onAuthenticate?.signIn()
    }


    override fun onClose() {
        finish()
        onStayInGame?.invoke()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onShareRecapped(message: String, image: String) {
        downloadFile(image, message, type = DownloadType.SHARE)
    }

    override fun onPageStarted() {
        progressBar.visibility = View.VISIBLE
        webView.evaluateJavascript(
            """
    javascript:(function() {
     if (!window.listenersAdded) {
      window.listenersAdded = true;
        window.parent.addEventListener('login-icc', function(event) {
            Android.receiveSignInEvent(JSON.stringify(event));
    });
        window.parent.addEventListener('close-icc-wrapped', function(event) {
            Android.receiveCloseEvent(JSON.stringify(event));
    });
        window.parent.addEventListener('share-icc-wrapped', function(event) {    
            console.log(event.detail.image);
            console.log(typeof event.detail.image);
            let eventImages = event.detail.image
            let eventImage = "data:image/png;base64,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"
            console.log(typeof eventImages);
            console.log(typeof eventImage);
            Android.receiveShareRecappedEvent(event.detail.message, eventImage);
    });
        }
    })()
    """, null
        )
    }

    override fun onPageFinished() {
        webView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        background.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Permission granted. Please retry the download.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied. Enable storage permission in settings.",
                    Toast.LENGTH_SHORT
                ).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                }
            }
        }
    }

    companion object {
        const val PARAM_EXTRA = "PARAM_EXTRA"
        private var onAuthenticate: OnAuthenticate? = null
        private var onStayInGame : (() -> Unit)? = null

        fun launch(
            context: Activity,
            user: User? = null,
            env: Env = Env.DEVELOPMENT,
            onStayInGame : (() -> Unit)? = null,
            onAuthenticate: OnAuthenticate? = null,
        ) {
            val param = SdkParam(env = env)
            val sdkParam = if (user != null) {
                param.copy(
                    user = user,
                    action = SdkActions.SIGN_IN,
                    env = env
                )
            } else {
                param
            }
            val token = sdkParam.user?.authToken.orEmpty()
            val sharedPrefProvider = SharedPrefProvider(context)
            sharedPrefProvider.saveAccessToken(token)
            this.onAuthenticate = onAuthenticate
            this.onStayInGame = onStayInGame
            val intent = Intent(context, IccWrappedActivity::class.java)
            intent.putExtra(PARAM_EXTRA, sdkParam)
            context.startActivity(intent)
        }

    }

    private fun clearWebViewCache() {
        webView.clearCache(true);
        webView.clearFormData()
        webView.clearHistory()
        webView.clearSslPreferences()
        WebStorage.getInstance().deleteAllData()
    }
}


interface OnAuthenticate {
    fun signIn()
}

enum class DownloadType {
    DOWNLOAD, SHARE
}