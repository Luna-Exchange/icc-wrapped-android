package com.icc.iccwrapped

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebStorage
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class IccWrappedActivity : AppCompatActivity(), OnJavScriptInterface, IccWebViewInterface {

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
        config = EnvConfig(arguments?.environment ?: Environment.DEVELOPMENT)
    }

    private fun setupViews() {
        progressBar = findViewById(R.id.progress_bar)
        background = findViewById(R.id.constraint_layout)
        arguments = intent.getParcelableExtra(PARAM_EXTRA)
    }

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
    }

    private fun openWrappedExperience() {
        val token = sharedPrefProvider.getAccessToken()
        if (token.isEmpty()) {
            clearWebViewCache()
            val url = config.iccUi
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
        loadUrlWithWebView(config.iccUi)
    }

    private fun loadUrlWithWebView(url: String) {
        webView.loadUrl(url)
    }

    override fun onNavigateBack() {
        finish()
        onAuthenticate?.onNavigateBack()
    }


    override fun onAuthenticateWithIcc() {
        SharedPrefProvider(this).saveState(SdkActions.SIGN_IN)
        onAuthenticate?.signIn()
    }

    override fun onResume() {
        super.onResume()
        if (arguments?.user != null) {
            encodeUser(arguments?.user)
            observeViewModel()
        }
    }

    override fun onClose() {
        finish()
    }

    override fun onPageStarted() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onDeepLinkToStayInGame() {
        val stayInGameUri = arguments?.stayInGameUri.orEmpty()
        val deepLinkUri = Uri.parse(stayInGameUri)
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri)
        startActivity(intent)
    }

    override fun onPageFinished() {
        webView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        background.visibility = View.GONE
        webView.evaluateJavascript(
            """
    javascript:(function() {
     if (!window.listenersAdded) {
      window.listenersAdded = true;
        window.parent.addEventListener('sign-in-with-icc', function(event) {
            Android.receiveSignInEvent(JSON.stringify(event));
        });
        window.parent.addEventListener('goToStayInTheGame', function(event) {
            Android.receiveStayInTheGameEvent(JSON.stringify(event));
        });
        window.parent.addEventListener('closeIccWrapped', function(event) {
            Android.receiveCloseWrappedEvent(JSON.stringify(event));
    });
        }
    })()
    """, null
        )
    }


    companion object {
        const val PARAM_EXTRA = "PARAM_EXTRA"
        private var onAuthenticate: OnAuthenticate? = null

        fun launch(
            context: Activity,
            user: User? = null,
            environment: Environment = Environment.DEVELOPMENT,
            stayInGameUri: String = "",
            onAuthenticate: OnAuthenticate? = null
        ) {
            val param = SdkParam(
                environment = environment,
                stayInGameUri = stayInGameUri
            )
            val sdkParam = if (user != null) {
                param.copy(
                    user = user,
                    action = SdkActions.SIGN_IN,
                    environment = environment
                )
            } else {
                param
            }
            val token = sdkParam.user?.authToken.orEmpty()
            val sharedPrefProvider = SharedPrefProvider(context)
            sharedPrefProvider.saveAccessToken(token)
            this.onAuthenticate = onAuthenticate
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