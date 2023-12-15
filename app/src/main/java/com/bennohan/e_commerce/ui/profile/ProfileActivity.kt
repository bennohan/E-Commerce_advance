package com.bennohan.e_commerce.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.database.UserDao
import com.bennohan.e_commerce.databinding.ActivityProfileBinding
import com.bennohan.e_commerce.ui.login.LoginActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding,ProfileViewModel>(R.layout.activity_profile) {

    @Inject
    lateinit var userDao : UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()

        binding.btnLogout.setOnClickListener {
            logoutDialog()
        }

    }

    private fun logoutDialog() {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage(R.string.logout_message)
                .setCancelable(false)
                .setPositiveButton("Logout") { _, _ ->
                    // Delete selected note from database
                    logout()
                    openActivity<LoginActivity>()
                    finishAffinity()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog: AlertDialog = builder.create()

            // Set the color of the positive button text
            dialog.setOnShowListener {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setBackgroundColor(ContextCompat.getColor(this, com.crocodic.core.R.color.text_red))
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.black))
                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.buttonColorYellow))
            }

            // Show the dialog
            dialog.show()



    }

    private fun logout() {
        viewModel.logout()
    }

    private fun observe() {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.apiResponse.collect {
                            when (it.status) {
                                //TODO Loading dialog at fragment
                                ApiStatus.LOADING -> {}
                                ApiStatus.SUCCESS -> {}
                                ApiStatus.ERROR -> {}
                                else -> binding?.root?.snacked("error")
                            }

                        }
                    }
                    launch {
                        userDao.getUser().collectLatest { user ->
                            binding.user = user
//                            dataUser = user
//                            userName = user.name
//                            isTextChangedByUser = false

                            Log.d("cek user", user.photo.toString())

                        }
                    }
                }
            }}


}