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
        type: IccFileDownloadType = IccFileDownloadType.DOWNLOAD
    ) {
        if (url.startsWith("data:image")) {
            downloadBase64Image(url, message, type)
        } else {
            Toast.makeText(this, "Unsupported URL format", Toast.LENGTH_SHORT).show()
        }
    }


    private fun downloadBase64Image(base64Data: String, message: String = "", type: IccFileDownloadType) {
        try {
            if (type == IccFileDownloadType.DOWNLOAD) {
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

            if (type == IccFileDownloadType.SHARE) {
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
            onAuthenticate?.signIn()
    }


    override fun onClose() {
        finish()
        onStayInGame?.invoke()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onShareRecapped(message: String, image: String) {
        downloadFile(image, message, type = IccFileDownloadType.SHARE)
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
            Android.receiveShareRecappedEvent(event.detail.message, eventImages);
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

    override fun onBackPressed() {
        super.onBackPressed()
        onAuthenticate?.onNavigateBack()
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
    fun onNavigateBack()
}

enum class IccFileDownloadType {
    DOWNLOAD, SHARE
}