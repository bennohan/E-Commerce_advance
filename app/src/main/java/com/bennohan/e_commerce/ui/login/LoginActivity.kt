package com.bennohan.e_commerce.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.databinding.ActivityLoginBinding
import com.bennohan.e_commerce.ui.home.HomeActivity
import com.bennohan.e_commerce.ui.register.RegisterActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        tvRegister()

        binding.btnLogin.setOnClickListener {
            login()

        }


    }

    private fun login() {
        val emailPhone = binding.etEmailPhone.textOf()
        val password = binding.etPassword.textOf()

        if (binding.etEmailPhone.isEmptyRequired(R.string.mustFillPhoneEmail) || binding.etPassword.isEmptyRequired(
                R.string.mustFillPassword
            )
        ) {
            return
        }

        viewModel.login(emailPhone, password)

    }

    private fun tvRegister() {
        val spannableString = SpannableString("Don’t have an account? Register Now")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                openActivity<RegisterActivity>()
            }
        }
        spannableString.setSpan(
            clickableSpan,
            23,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvRegisterOption.text = spannableString
        binding.tvRegisterOption.movementMethod =
            LinkMovementMethod.getInstance() // Required for clickable spans to work

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                loadingDialog.show("Success \n Login Success")
                                openActivity<HomeActivity> {
                                    finish()
                                }
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