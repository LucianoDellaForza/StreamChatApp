package com.luka.chatappwithstream.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luka.chatappwithstream.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val client: ChatClient  //this should be in repository (client does api calls)
): ViewModel() {

    private val _loginEvent = MutableSharedFlow<LogInEvent>()   //SharedFlow emits events (one time only) unlike StateFlow which emits same value when screen is rotated
    val loginEvent = _loginEvent.asSharedFlow() //.asSharedFlow - to make it non mutable

    private fun isValidUsername(username: String) =
        username.length >= Constants.MIN_USERNAME_LENGTH

    fun connectUser(username: String) {
        val trimmedUsername = username.trim()
        viewModelScope.launch {
            if(isValidUsername(trimmedUsername)) {
                val result = client.connectGuestUser(
                    userId = trimmedUsername,
                    username = trimmedUsername
                ).await()
                if(result.isError) {
                    _loginEvent.emit(LogInEvent.ErrorLogIn(result.error().message ?: "Unknown error"))
                    return@launch
                }
                _loginEvent.emit(LogInEvent.Success)
            } else {
                _loginEvent.emit(LogInEvent.ErrorInputTooShort)
            }
        }
    }

    sealed class LogInEvent {
        object ErrorInputTooShort : LogInEvent()
        data class ErrorLogIn(val error: String) : LogInEvent()
        object Success : LogInEvent()
    }
}