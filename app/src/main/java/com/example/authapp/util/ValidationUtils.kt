package com.example.authapp.util

class ValidationUtils {

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6

        fun validateUsername(username: String): ValidationResult {
            return if (username.isBlank()) {
                ValidationResult(false, "请输入用户名")
            } else {
                ValidationResult(true, "")
            }
        }

        fun validatePassword(password: String): ValidationResult {
            return when {
                password.isBlank() -> ValidationResult(false, "请输入密码")
                password.length < MIN_PASSWORD_LENGTH -> ValidationResult(false, "密码至少6位")
                else -> ValidationResult(true, "")
            }
        }

        fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
            return when {
                confirmPassword.isBlank() -> ValidationResult(false, "请确认密码")
                password != confirmPassword -> ValidationResult(false, "两次密码不一致")
                else -> ValidationResult(true, "")
            }
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String
)
