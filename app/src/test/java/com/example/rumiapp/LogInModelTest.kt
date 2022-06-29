package com.example.rumiapp

import android.app.Activity
import com.example.rumiapp.testing.LogInListener
import com.example.rumiapp.testing.LogInModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor

//@RunWith(MockitoJUnitRunner::class)
class LogInModelTest: LogInListener {
    private lateinit var successTask: Task<AuthResult>
    private lateinit var failureTask: Task<AuthResult>

    @Mock
    private lateinit var mAuth: FirebaseAuth
    private lateinit var logInModel: LogInModel

    private var logInResult = UNDEFINED

    companion object {
        private const val SUCCESS = 1
        private const val FAILURE = -1
        private const val UNDEFINED = 0
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        successTask = object : Task<AuthResult>() {
            override fun isComplete(): Boolean = true

            override fun isSuccessful(): Boolean = true

            override fun addOnCompleteListener(
                p0: Activity,
                p1: OnCompleteListener<AuthResult>
            ): Task<AuthResult> {
                p1.onComplete(successTask)
                return successTask
            }

            override fun addOnCompleteListener(
                p0: Executor,
                p1: OnCompleteListener<AuthResult>
            ): Task<AuthResult> {
                p1.onComplete(successTask)
                return successTask
            }

            override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> {
                return successTask
            }

            override fun addOnFailureListener(
                p0: Activity,
                p1: OnFailureListener
            ): Task<AuthResult> {
                return successTask
            }

            override fun addOnFailureListener(
                p0: Executor,
                p1: OnFailureListener
            ): Task<AuthResult> {
                return successTask
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> {
                return successTask
            }

            override fun addOnSuccessListener(
                p0: Activity,
                p1: OnSuccessListener<in AuthResult>
            ): Task<AuthResult> {
                return successTask
            }

            override fun addOnSuccessListener(
                p0: Executor,
                p1: OnSuccessListener<in AuthResult>
            ): Task<AuthResult> {
                return successTask
            }

            override fun getException(): Exception? {
                return successTask.exception
            }

            override fun getResult(): AuthResult {
                return successTask.result
            }

            override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult {
                return successTask.result
            }

            override fun isCanceled(): Boolean {
                return false
            }
        }

        failureTask = object : Task<AuthResult>() {
            override fun isComplete(): Boolean = true

            override fun isSuccessful(): Boolean = false

            override fun addOnCompleteListener(
                p0: Activity,
                p1: OnCompleteListener<AuthResult>
            ): Task<AuthResult> {
                return failureTask
            }

            override fun addOnCompleteListener(
                p0: Executor,
                p1: OnCompleteListener<AuthResult>
            ): Task<AuthResult> {
                p1.onComplete(failureTask)
                return failureTask
            }

            override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> {
                return failureTask
            }

            override fun addOnFailureListener(
                p0: Activity,
                p1: OnFailureListener
            ): Task<AuthResult> {
                return failureTask
            }

            override fun addOnFailureListener(
                p0: Executor,
                p1: OnFailureListener
            ): Task<AuthResult> {
                return failureTask
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> {
                return failureTask
            }

            override fun addOnSuccessListener(
                p0: Activity,
                p1: OnSuccessListener<in AuthResult>
            ): Task<AuthResult> {
                return failureTask
            }

            override fun addOnSuccessListener(
                p0: Executor,
                p1: OnSuccessListener<in AuthResult>
            ): Task<AuthResult> {
                return failureTask
            }

            override fun getException(): Exception? {
                return java.lang.Exception()
            }

            override fun getResult(): AuthResult {
                return failureTask.result
            }

            override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult {
                return failureTask.result
            }

            override fun isCanceled(): Boolean {
                return false
            }
        }
        logInModel = LogInModel(mAuth, this)
    }

    @Test
    fun logInSuccess_test() {
        val email = "testing1@gmail.com"
        val password = "testing1"
        Mockito.`when`(mAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(successTask)
        logInModel.logIn(email, password)
        assert(logInResult == SUCCESS)
    }

    @Test
    fun logInFailure_test() {
        val email = "testing1@gmail.com"
        val password = "testing1454"
        Mockito.`when`(mAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(failureTask)
        logInModel.logIn(email, password)
        assert(logInResult == FAILURE)
    }

    override fun logInSuccess(email: String, password: String) {
        logInResult = SUCCESS
    }

    override fun logInFailure(exception: Exception, email: String, password: String) {
        logInResult = FAILURE
    }
}