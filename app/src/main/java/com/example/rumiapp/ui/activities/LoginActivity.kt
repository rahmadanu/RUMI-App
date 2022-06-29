package com.example.rumiapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.rumiapp.R
import com.example.rumiapp.databinding.ActivityLoginBinding
import com.example.rumiapp.firestore.FirestoreClass
import com.example.rumiapp.models.User
import com.example.rumiapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), View.OnClickListener {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mUserDetails: User
    private var autoLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_RUMIApp_NoActionBar)
        setContentView(binding.root)

        binding.tvForgotPassword.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)

        userAutoLoggedIn() // -> bug while logging out, can't log in anymore
        /* bug while auto logging in, it doesn't work
        if (autoLoggedIn) {
            userAutoLoggedIn()
        }*/

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.btn_login -> {
                    loginRegisteredUser()
                }
                R.id.tv_register -> {
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this@LoginActivity, "Please enter email.", Toast.LENGTH_SHORT).show()
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this@LoginActivity, "Please enter password.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun loginRegisteredUser() {
        if (validateLoginDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity)
                        FirestoreClass().checkIfEmailVerified(this)

                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        mUserDetails = user

        if (mUserDetails.profileCompleted == 0) {
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
            startActivity(intent)
        } else {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        }
        finish()
    }

   private fun userAutoLoggedIn() {
       autoLoggedIn = false
       val currentUserId = FirestoreClass().getCurrentUserId()

        if (currentUserId.isNotEmpty()) {
            autoLoggedIn = true
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        }
   }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}