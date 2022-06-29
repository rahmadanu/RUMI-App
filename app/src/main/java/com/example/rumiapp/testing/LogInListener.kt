package com.example.rumiapp.testing

import java.lang.Exception

interface LogInListener {
    fun logInSuccess(email: String, password: String)

    fun logInFailure(exception: Exception, email: String, password: String)
}
