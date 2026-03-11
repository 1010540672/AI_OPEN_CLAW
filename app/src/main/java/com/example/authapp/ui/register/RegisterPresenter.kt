package com.example.authapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.repository.AuthRepository
import com.example.authapp.util.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String = "",
    val usernameError: String = "",
    val passwordError: String = "",
    val confirmPasswordError: String = ""
)

class RegisterPresenter(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(username: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            // 验证输入
            val usernameValidation = ValidationUtils.validateUsername(username)
            val passwordValidation = ValidationUtils.validatePassword(password)
            val confirmPasswordValidation = ValidationUtils.validateConfirmPassword(
                password,
                confirmPassword
            )

            var hasError = false

            if (!usernameValidation.isValid) {
                _uiState.value = _uiState.value.copy(usernameError = usernameValidation.errorMessage)
                hasError = true
            }

            if (!passwordValidation.isValid) {
                _uiState.value = _uiState.value.copy(passwordError = passwordValidation.errorMessage)
                hasError = true
            }

            if (!confirmPasswordValidation.isValid) {
                _uiState.value = _uiState.value.copy(
                    confirmPasswordError = confirmPasswordValidation.errorMessage
                )
                hasError = true
            }

            if (hasError) return@launch

            // 清除错误
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                usernameError = "",
                passwordError = "",
                confirmPasswordError = ""
            )

            // 执行注册
            val result = repository.register(username, password)

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
            confirmPasswordError = "",
            errorMessage = ""
        )
    }
}
