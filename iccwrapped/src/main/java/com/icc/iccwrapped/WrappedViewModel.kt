package com.icc.iccwrapped

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber

class WrappedViewModel : ViewModel() {

    private val mToken = MutableStateFlow<Result>(Result.Default)

    val token: StateFlow<Result>
        get() = mToken

    fun encodeUser(user: User?, baseUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (user == null) return@launch
                val service = WrappedApiClient.create(baseUrl)
                val tokenFlow = flow {
                    emit(service.encode(user))
                }.catch {
                        mToken.emit(Result.Failed(it.message ?: ""))
                    }
                tokenFlow.collect {
                    mToken.emit(Result.Success(it.data.token))
                }
            } catch (e: Exception) {
                Timber.tag("TAG").e("unable to encode user ${e.message}")
            }
        }
    }
}


sealed class Result {
    data class Success(val token: String) : Result()
    data class Failed(val message: String) : Result()
    object Default : Result()

}

@Parcelize
data class User(val authToken: String, val name: String, val email: String) : Parcelable

@Parcelize
data class SdkParam(
    var user: User? = null,
    var entryPoint: String = "",
    var env: Env = Env.DEVELOPMENT,
    var action: SdkActions = SdkActions.DEFAULT,
    var stayInGameUri : String = ""
) : Parcelable
