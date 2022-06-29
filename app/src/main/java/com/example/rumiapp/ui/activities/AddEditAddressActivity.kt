package com.example.rumiapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.rumiapp.R
import com.example.rumiapp.databinding.ActivityAddEditAddressBinding
import com.example.rumiapp.firestore.FirestoreClass
import com.example.rumiapp.models.Address
import com.example.rumiapp.utils.Constants

class AddEditAddressActivity : BaseActivity() {

    private var _binding: ActivityAddEditAddressBinding? = null
    private val binding get() = _binding!!

    private var mAddressDetails: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {
                binding.apply {
                    tvTitle.text = resources.getString(R.string.title_edit_address)
                    btnSubmitAddress.text = resources.getString(R.string.btn_lbl_update)

                    etFullName.setText(mAddressDetails?.name)
                    etPhoneNumber.setText(mAddressDetails?.mobileNumber)
                    etAddress.setText(mAddressDetails?.address)
                    etAdditionalNote.setText(mAddressDetails?.additionalNote)

                    when (mAddressDetails?.type) {
                        Constants.HOME -> {
                            rbHome.isChecked = true
                        }
                        Constants.OFFICE -> {
                            rbOffice.isChecked = true
                        }
                        else -> {
                            rbOther.isChecked = true
                            tilOtherDetails.visibility = View.VISIBLE
                            etOtherDetails.setText(mAddressDetails?.otherDetails)
                        }
                    }
                }
            }
        }

        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                binding.tilOtherDetails.visibility = View.VISIBLE
            } else {
                binding.tilOtherDetails.visibility = View.GONE
            }
        }

        binding.btnSubmitAddress.setOnClickListener {
            addAddressToFirestore()
        }
    }

    fun validateAddress(): Boolean {

        binding.apply {
            return when {

                TextUtils.isEmpty(etFullName.text.toString().trim()) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_full_name), true)
                    false
                }

                TextUtils.isEmpty(etPhoneNumber.text.toString().trim()) -> {
                    showErrorSnackBar(
                        resources.getString(R.string.err_msg_please_enter_phone_number),
                        true
                    )
                    false
                }

                TextUtils.isEmpty(etAddress.text.toString().trim()) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                    false
                }

                else -> true
            }
        }
    }

    fun addAddressToFirestore() {

        binding.apply {
            val fullName: String = etFullName.text.toString().trim()
            val phoneNumber: String = etPhoneNumber.text.toString().trim()
            val address: String = etAddress.text.toString().trim()
            val additionalNote: String = etAdditionalNote.text.toString().trim()
            val otherDetails: String = etOtherDetails.text.toString().trim()

            if (validateAddress()) {
                showProgressDialog(resources.getString(R.string.please_wait))

                val addressType: String = when {
                    rbHome.isChecked -> {
                        Constants.HOME
                    }
                    rbOffice.isChecked -> {
                        Constants.OFFICE
                    }
                    else -> {
                        Constants.OTHER
                    }
                }

                val addressModel = Address(
                    FirestoreClass().getCurrentUserId(),
                    fullName,
                    phoneNumber,
                    address,
                    additionalNote,
                    addressType,
                    otherDetails
                )

                if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                    FirestoreClass().updateAddress(this@AddEditAddressActivity, addressModel, mAddressDetails!!.id)
                } else {
                    FirestoreClass().addAddress(this@AddEditAddressActivity, addressModel)
                }
            }
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()

        val notifySuccessMessage: String = if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_address_updated_successfully)
        } else {
            resources.getString(R.string.err_your_address_added_successfully)
        }

        Toast.makeText(this@AddEditAddressActivity, notifySuccessMessage, Toast.LENGTH_SHORT).show()

        setResult(RESULT_OK)
        finish()
    }

    fun setupActionBar() {
        val toolbar = binding.toolbarAddEditAddressActivity

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}