package com.icc.iccwrapped

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.icc.iccwrapped.extensions.getImageUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File


const val STORAGE_PERMISSION_CODE = 1001

class IccRecappedActivity : AppCompatActivity(), OnJavScriptInterface, IccWebViewInterface {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var background: ConstraintLayout
    private var arguments: SdkParam? = null
    private val viewModel: WrappedViewModel by viewModels()
    private lateinit var config: EnvConfig
    private lateinit var sharedPrefProvider: SharedPrefProvider
    private var shouldRefresh = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_icc_wrapped)
        setupAndOpenWrapped()
    }

    private fun setupAndOpenWrapped() {
        setupViews()
        setupConfig()
        setupWebView()
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

    private fun setupWebView() {
        webView = findViewById(R.id.web_view)

        
        val webSettings = webView.settings
        webSettings.apply {
            javaScriptCanOpenWindowsAutomatically = true
            javaScriptEnabled = true
            domStorageEnabled = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            allowFileAccess = true
            allowContentAccess = true
            databaseEnabled = true
            setGeolocationEnabled(true)
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        webView.webViewClient = IccWebViewClient(this)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.let {
                    Timber.d("WebView Console: ${it.message()}")
                }
                return true
            }

            override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                callback?.invoke(origin, true, false)
            }
        }

        webView.setDownloadListener { url, _, contentDisposition, mimeType, _ ->
            downloadFile(url, contentDisposition)
        }
    }

    private fun downloadFile(
        url: String,
        message: String,
        type: IccFileDownloadType = IccFileDownloadType.DOWNLOAD
    ) {
        if (url.startsWith("data:image")) {
            lifecycleScope.launch {
                downloadBase64Image(url, message, type)
            }
        } else {
            Toast.makeText(this, "Unsupported URL format", Toast.LENGTH_SHORT).show()
        }
    }


    private suspend fun downloadBase64Image(
        base64Data: String,
        message: String = "",
        type: IccFileDownloadType
    ) {
        try {
            withContext(Dispatchers.Main) {
                if (type == IccFileDownloadType.DOWNLOAD) {
                    Toast.makeText(this@IccRecappedActivity, "Downloading...", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            val base64Image = base64Data.substringAfter("base64,")
            val decodedBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            val fileName = "icc_recapped_${System.currentTimeMillis()}.png"
            var savedUri: Uri? = null
            
            withContext(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10 and above - use MediaStore API
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                        put(MediaStore.Downloads.MIME_TYPE, "image/png")
                    }

                    savedUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    savedUri?.let {
                        contentResolver.openOutputStream(it)?.use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        }
                    }
                } else {
                    val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        fileName
                    )
                    file.outputStream().use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }

                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
                        data = Uri.fromFile(file)
                    }
                    sendBroadcast(mediaScanIntent)
                    savedUri = getImageUri(file)
                }
            }

            withContext(Dispatchers.Main) {
                when (type) {
                    IccFileDownloadType.SHARE -> {
                        savedUri?.let { shareIccWrapped(it, message) }
                    }
                    IccFileDownloadType.DOWNLOAD -> Toast.makeText(
                        this@IccRecappedActivity,
                        "Image downloaded",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@IccRecappedActivity,
                    "Failed to download image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun shareIccWrapped(imageUri: Uri, message: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    override fun onStayInTheGame() {
        finish()
        onStayInGame?.invoke()
    }

    private fun openWrappedExperience() {
        if (!isUserValid()) {
            clearWebViewCache()
            loadUrlWithWebView(config.iccUi)
        } else {
            encodeUser(arguments?.user)
            observeViewModel()
        }
    }

    private fun isUserValid() : Boolean {
        return if( arguments?.user?.email.isNullOrEmpty()) {
            false
        } else if (arguments?.user?.authToken.isNullOrEmpty()) {
            false
        } else {
            true
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
                loadAuthenticatedUrl()
            }

            is Result.Failed -> {
                Timber.e("Unable to encode user")
                val url = config.iccUi
                loadUrlWithWebView(url)
            }

            is Result.Default -> {}
        }
    }

    private fun loadAuthenticatedUrl() {
        val token = sharedPrefProvider.getAccessToken()
        val url = "${config.iccUi}?recapped_access=${token}"
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
        window.parent.addEventListener('go-to-stay-in-the-game', function(event) {
            Android.goToStayInTheGame(JSON.stringify(event));
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
            }
        }
    }

    companion object {
        const val PARAM_EXTRA = "PARAM_EXTRA"
        private var onAuthenticate: OnAuthenticate? = null
        private var onStayInGame: (() -> Unit)? = null
        private var onDestroyCalled : (() -> Unit)? = null

        fun launch(
            context: Activity,
            user: User? = null,
            env: Env = Env.DEVELOPMENT,
            onStayInGame: (() -> Unit)? = null,
            onAuthenticate: OnAuthenticate? = null,
            onDestroy: (() -> Unit)? = null
        ) {
            val param = SdkParam(env = env)
            var iccUser = user
            val sdkParam = if (user != null) {
                if (user.name.isEmpty()) {
                    iccUser = user.copy(name = "Your Name")
                } else if (user.email.isEmpty()) {
                    iccUser = user.copy(email = "test@example.com")
                }
                param.copy(
                    user = iccUser,
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
            this.onDestroyCalled = onDestroy
            val intent = Intent(context, IccRecappedActivity::class.java)
            intent.putExtra(PARAM_EXTRA, sdkParam)
            context.startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        onAuthenticate?.onNavigateBack()
    }

    private fun clearWebViewCache() {
        try {
            webView.clearCache(true)
            webView.clearFormData()
            webView.clearHistory()
            webView.clearSslPreferences()
            WebStorage.getInstance().deleteAllData()
        } catch (e: Exception) {
            Timber.e(e, "Error clearing WebView cache")
        }
    }

    override fun onDestroy() {
        onDestroyCalled?.invoke()
        super.onDestroy()
    }
}


interface OnAuthenticate {
    fun signIn()
    fun onNavigateBack()
}

enum class IccFileDownloadType {
    DOWNLOAD, SHARE
}