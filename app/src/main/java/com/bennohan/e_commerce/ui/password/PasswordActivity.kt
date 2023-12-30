package com.bennohan.e_commerce.ui.password

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.databinding.ActivityPasswordBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordActivity :
    BaseActivity<ActivityPasswordBinding, PasswordViewModel>(R.layout.activity_password) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        validateEdit()


        binding.btnEditPassword.setOnClickListener {
            editPassword()
        }

        binding.btnBack.setOnClickListener {
            if (binding.etPassword.textOf().isNotEmpty() || binding.etPasswordConfirmation.textOf()
                    .isNotEmpty()
            ) {
                unsavedAlert()
                return@setOnClickListener
            }
            finish()
        }


    }

    private fun isValidPasswordLength(password: String): Boolean {
        return password.length >= 6
    }

    private fun validatePassword() {
        if (!isValidPasswordLength(binding.etPassword.textOf())) {
            binding.tvPasswordLength.visibility = View.VISIBLE
            return
        } else {
            binding.tvPasswordLength.visibility = View.GONE
        }
        if (binding.etPassword.textOf() != binding.etPasswordConfirmation.textOf()) {
            binding.tvPasswordNotMatch.visibility = View.VISIBLE
            return
        } else {
            binding.tvPasswordNotMatch.visibility = View.GONE
        }
    }

    private fun validateEdit() {


        binding.etPassword.doAfterTextChanged {
            validatePassword()
        }

        binding.etPasswordConfirmation.doAfterTextChanged {
            validatePassword()
            if (binding.etPassword.textOf().isEmpty()) {
                binding.etPassword.error = "Password Tidak Boleh Kosong"
                binding.tvPasswordNotMatch.visibility = View.GONE
            }
        }

    }

    private fun editPassword() {

        val newPassword = binding.etPassword.textOf()
        val confirmPassword = binding.etPasswordConfirmation.textOf()

        if (binding.etPassword.isEmptyRequired(
                R.string.mustFillPassword
            ) || binding.etPasswordConfirmation.isEmptyRequired(R.string.mustFillConfirmPassword)
        ) {
            return
        }

        viewModel.editPassword(newPassword, confirmPassword)


    }




    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                binding.root.snacked("Success \n Edit Password Success")
                                finish()

                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.setResponse(it.message ?: return@collect)
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }

                    }
                }
            }
        }
    }

    private fun unsavedAlert() {
        val builder = AlertDialog.Builder(this@PasswordActivity)
        builder.setTitle("Unsaved Changes")
        builder.setMessage("You have unsaved changes. Are you sure you want to Dismiss changes?.")
            .setPositiveButton("Dismiss") { _, _ ->
                this@PasswordActivity.finish()
            }
            .setNegativeButton("Keep Editing") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog: AlertDialog = builder.create()

        // Set the color of the positive button text
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, com.crocodic.core.R.color.text_red))
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        dialog.show()

    }


}