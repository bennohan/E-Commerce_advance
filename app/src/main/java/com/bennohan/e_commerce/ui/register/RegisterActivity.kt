package com.bennohan.e_commerce.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.databinding.ActivityRegisterBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity :
    BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        validateRegister()
        observe()

        binding.btnRegister.setOnClickListener {
            register()
        }

    }

    private fun validateRegister() {
        binding.etPassword.doAfterTextChanged {
            validatePassword()
        }

        binding.etConfirmPassword.doAfterTextChanged {
            validatePassword()
            if (binding.etPassword.textOf().isEmpty()) {
                binding.etPassword.error = "Password Tidak Boleh Kosong"
                binding.tvPasswordNotMatch.visibility = View.GONE
            }
        }
    }

    private fun tvLogin(){
        val spannableString = SpannableString("Already have an account? Register Now")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                openActivity<RegisterActivity>()
            }
        }
        spannableString.setSpan(clickableSpan, 25, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLoginOption.text = spannableString
        binding.tvLoginOption.movementMethod = LinkMovementMethod.getInstance() // Required for clickable spans to work

    }


    private fun isValidPasswordLength(password: String): Boolean {
        return password.length >= 6
    }

    private fun validatePassword() {
        if (!isValidPasswordLength(binding.etPassword.textOf())) {
            //If Password length is not 6 character
            binding.tvPasswordLength.visibility = View.VISIBLE
            return
        } else {
            binding.tvPasswordLength.visibility = View.GONE
        }
        if (binding.etPassword.textOf() != binding.etConfirmPassword.textOf()) {
            //If Password  is not match
            binding.tvPasswordNotMatch.visibility = View.VISIBLE
            return
        } else {
            binding.tvPasswordNotMatch.visibility = View.GONE
        }
    }


    private fun register() {
        val name = binding.etName.textOf()
        val emailPhone = binding.etPhoneEmail.textOf()
        val password = binding.etPassword.textOf()
        val confirmPassword = binding.etConfirmPassword.textOf()


        if (binding.etName.isEmptyRequired(R.string.mustFillName) || binding.etPhoneEmail.isEmptyRequired(
                R.string.mustFillPhoneEmail
            )
            || binding.etPassword.isEmptyRequired(R.string.mustFillPassword) || binding.etConfirmPassword.isEmptyRequired(
                R.string.mustFillConfirmPassword
            )
        ) {
            return
        }

//        //TODO ADD Email Validation Function
        fun isValidIndonesianPhoneNumber(phoneNumber: String): Boolean {
            val regex = Regex("^\\+62\\d{9,15}$|^0\\d{9,11}$")
            return regex.matches(phoneNumber)
        }
        if (!isValidIndonesianPhoneNumber(emailPhone)) {
            // if Telephone number is not valid
            tos("condition 1")
            binding.etPhoneEmail.error = "Nomor Telephone Tidak Valid"
            return
        }
        if (binding.etPassword.textOf() == binding.etConfirmPassword.textOf()) {
            // If Password is valid
            binding.tvPasswordNotMatch.visibility = View.GONE
            binding.tvPasswordNotMatch.visibility = View.GONE
            tos("condition 2")
        } else {
            // If Password Doesn't Valid
            binding.tvPasswordLength.visibility = View.GONE
            binding.tvPasswordNotMatch.visibility = View.VISIBLE
            tos("condition 3")
            return
        }

//        viewModel.register(name, emailPhone, password, confirmPassword)
        tos("$name,$emailPhone,$password,$confirmPassword")
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Register")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.show("Success \n Register Success")
//                                tos("Register Success")
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

}