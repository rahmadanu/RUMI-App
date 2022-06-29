package com.example.rumiapp.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rumiapp.R
import com.example.rumiapp.databinding.ActivityUserProfileBinding
import com.example.rumiapp.firestore.FirestoreClass
import com.example.rumiapp.models.User
import com.example.rumiapp.utils.Constants
import com.example.rumiapp.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var mUserDetails: User

    private var mSelectedImageUri: Uri? = null
    private var mUserProfileImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        if (mUserDetails.profileCompleted == 0) {
            binding.apply {
                tvTitle.text = resources.getString(R.string.title_complete_profile)

                etFirstName.isEnabled = false
                etFirstName.setText(mUserDetails.firstName)

                etLastName.isEnabled = false
                etLastName.setText(mUserDetails.lastName)

                etEmail.isEnabled = false
                etEmail.setText(mUserDetails.email)
            }
        } else {
            setupActionBar()

            binding.apply {
                tvTitle.text = resources.getString(R.string.title_edit_profile)

                GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, ivUserPhoto)

                etFirstName.setText(mUserDetails.firstName)
                etLastName.setText(mUserDetails.lastName)

                etEmail.isEnabled = false
                etEmail.setText(mUserDetails.email)

                if (mUserDetails.mobile != 0L) {
                    etMobileNumber.setText(mUserDetails.mobile.toString())
                }

                if (mUserDetails.gender == Constants.MALE) {
                    rbMale.isChecked = true
                } else {
                    rbFemale.isChecked = true
                }
            }

            binding.ivUserPhoto.setOnClickListener(this)
            binding.btnSave.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                        Constants.showImageChooser(this@UserProfileActivity)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_save -> {
                    if (validateUserProfileDetails()) {
                        showProgressDialog(resources.getString(R.string.please_wait))

                        if (mSelectedImageUri != null) {
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageUri, Constants.USER_PROFILE_IMAGE)
                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@UserProfileActivity)
            } else {
                showErrorSnackBar(resources.getString(R.string.read_storage_permission_denied), true)
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageUri = data.data!!

                        GlideLoader(this).loadUserPicture(mSelectedImageUri!!, binding.ivUserPhoto)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, resources.getString(R.string.image_selection_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Result Canceled", "Image selection canceled")
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        val firstName = binding.etFirstName.text.toString().trim()
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = binding.etLastName.text.toString().trim()
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val mobileNumber = binding.etMobileNumber.text.toString().trim()

        val gender = if (binding.rbMale.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if (mUserProfileImageUrl.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageUrl
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        if (mUserDetails.profileCompleted == 0) {
            userHashMap[Constants.COMPLETE_PROFILE] = 1
        }

        FirestoreClass().updateUserProfileData(this@UserProfileActivity, userHashMap)
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()

        Toast.makeText(this, resources.getString(R.string.msg_profile_update_success), Toast.LENGTH_SHORT).show()

        //bug
        startActivity(Intent(this@UserProfileActivity, HomeActivity::class.java))
        finish()
    }

    fun imageUploadSuccess(imageUrl: String) {

        mUserProfileImageUrl = imageUrl

        updateUserProfileDetails()
    }

    private fun setupActionBar() {
        val toolbar = binding.toolbarUserProfileActivity
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}