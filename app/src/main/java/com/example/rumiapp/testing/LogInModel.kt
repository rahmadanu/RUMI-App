package com.example.rumiapp.testing

import android.app.Activity
import android.content.Context
import com.example.rumiapp.ui.activities.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class LogInModel(private val mAuth: FirebaseAuth, val logInListener: LogInListener) {

    // Create a new ThreadPoolExecutor with 2 threads for each processor on the
// device and a 60 second keep-alive time.
    private val numCores = Runtime.getRuntime().availableProcessors();

    private val executor: ThreadPoolExecutor = ThreadPoolExecutor(
        numCores * 2, numCores * 2,
        60L, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>()
    )


    fun logIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor, OnCompleteListener {
                if (it.isSuccessful) {
                    logInListener.logInSuccess(email, password)
                } else {
                    logInListener.logInFailure(it.exception!!, email, password)
                }
            })
    }
}