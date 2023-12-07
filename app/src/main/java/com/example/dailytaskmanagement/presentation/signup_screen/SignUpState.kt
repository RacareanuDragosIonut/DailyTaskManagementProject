package com.example.dailytaskmanagement.presentation.signup_screen

data class SignUpState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""

)