package com.example.dailytaskmanagement.presentation.login_screen

data class SignInState(
    val isLoading: Boolean = false,
    val isSuccess: String? = null,
    val isError: String? = null
)
