package com.example.authapp.data.repository

import com.example.authapp.data.model.AuthResult
import com.example.authapp.data.model.User

class AuthRepository {

    // 纯演示：使用内存存储
    private val users = mutableListOf<User>()

    fun register(username: String, password: String): AuthResult {
        // 检查用户名是否已存在
        if (users.any { it.username == username }) {
            return AuthResult(
                success = false,
                message = "用户名已存在"
            )
        }

        // 创建新用户
        val user = User(username, password)
        users.add(user)

        return AuthResult(
            success = true,
            message = "注册成功",
            user = user
        )
    }

    fun login(username: String, password: String): AuthResult {
        // 查找用户
        val user = users.find { it.username == username }

        // 检查用户是否存在
        if (user == null) {
            return AuthResult(
                success = false,
                message = "用户名或密码错误"
            )
        }

        // 检查密码
        if (user.password != password) {
            return AuthResult(
                success = false,
                message = "用户名或密码错误"
            )
        }

        return AuthResult(
            success = true,
            message = "登录成功",
            user = user
        )
    }

    // 演示数据：预置一个测试用户
    init {
        users.add(User("admin", "123456"))
    }
}
