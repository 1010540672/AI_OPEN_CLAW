package com.example.authapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.model.AuthResult
import com.example.authapp.data.repository.AuthRepository
import com.example.authapp.util.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String = "",
    val usernameError: String = "",
    val passwordError: String = ""
)

class LoginPresenter(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            // 验证输入
            val usernameValidation = ValidationUtils.validateUsername(username)
            val passwordValidation = ValidationUtils.validatePassword(password)

            if (!usernameValidation.isValid) {
                _uiState.value = _uiState.value.copy(
                    usernameError = usernameValidation.errorMessage
                )
                return@launch
            }

            if (!passwordValidation.isValid) {
                _uiState.value = _uiState.value.copy(
                    passwordError = passwordValidation.errorMessage
                )
                return@launch
            }

            // 清除错误
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                usernameError = "",
                passwordError = ""
            )

            // 执行登录
            val result = repository.login(username, password)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isSuccess = result.success,
                errorMessage = if (!result.success) result.message else ""
            )
        }
    }

    fun clearErrors() {
        _uiState.value = _uiState.value.copy(
            usernameError = "",
            passwordError = "",
            errorMessage = ""
        )
    }
}
