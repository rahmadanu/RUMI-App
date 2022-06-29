package com.example.rumiapp.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.rumiapp.R
import com.example.rumiapp.firestore.FirestoreClass
import com.example.rumiapp.models.User
import com.example.rumiapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.tvLogin.setOnClickListener {
            onBackPressed()
        }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        if (validateRegisterDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val firstName: String = binding.etFirstName.text.toString().trim()
            val lastName: String = binding.etLastName.text.toString().trim()
            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result.user!!

                        val user = User(
                            firebaseUser.uid,
                            firstName,
                            lastName,
                            email
                        )

                        FirestoreClass().registerUser(this@RegisterActivity, user)

                        FirestoreClass().sendEmailVerification(this@RegisterActivity)

                    } else {
                        hideProgressDialog()

                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }

        }
    }

    private fun validateRegisterDetails(): Boolean {
        binding.apply {
            return when {
                TextUtils.isEmpty(etFirstName.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                    false
                }

                TextUtils.isEmpty(etLastName.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                    false
                }

                TextUtils.isEmpty(etEmail.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                    false
                }

                TextUtils.isEmpty(etEmail.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                    false
                }

                TextUtils.isEmpty(etPassword.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                    false
                }

                TextUtils.isEmpty(etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                    false
                }

                etPassword.text.toString().trim { it <= ' ' } != etConfirmPassword.text.toString()
                    .trim { it <= ' ' } -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                        false
                    }

                !cbTermsAndCondition.isChecked -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                    false
                }

                else -> {
                    //showErrorSnackBar(resources.getString(R.string.register_successful), false)
                    true
                }
            }
        }
    }

    fun userRegisteredSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()

        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarRegisterActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarRegisterActivity.setNavigationOnClickListener { onBackPressed() }
    }

    companion object {
        const val USER_ID = "user_id"
        const val EMAIL = "email"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
    }
}