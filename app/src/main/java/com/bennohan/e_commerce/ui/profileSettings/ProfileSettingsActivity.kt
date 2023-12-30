package com.bennohan.e_commerce.ui.profileSettings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.database.user.User
import com.bennohan.e_commerce.database.user.UserDao
import com.bennohan.e_commerce.databinding.ActivityProfileSettingsBinding
import com.bennohan.e_commerce.helper.ViewBindingHelper.Companion.writeBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.crocodic.core.helper.BitmapHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileSettingsActivity :
    BaseActivity<ActivityProfileSettingsBinding, ProfileSettingsViewModel>(R.layout.activity_profile_settings) {

    @Inject
    lateinit var userDao: UserDao
    private var filePhoto: File? = null
    private var dataUser: User? = null
    private var isUserInput = false // Flag to track user-initiated changes For Edit Text


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()


        binding.btnSave.setOnClickListener {
            editProfile()
        }

        binding.btnBack.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        binding.btnEditPhoto.setOnClickListener {
            openPictureDialog()
        }


    }

    private fun editProfile() {
        val nameInput = binding.etUsername.textOf()
        binding.btnSave.setOnClickListener {
            if (filePhoto == null) {
                nameInput.let { it1 -> viewModel.updateProfile(it1) }
                Log.d("conditon 1", "condition 1")
            } else {
                if (nameInput != null) {
                    viewModel.updateProfilePhoto(nameInput, filePhoto!!)
                    Log.d("conditon 2", "conditon 2")
                } else {
                    viewModel.updateProfilePhoto(nameInput, filePhoto!!)
                    Log.d("conditon 3", "condition 3")

                }
            }
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> {
                                loadingDialog.show()
                            }
                            ApiStatus.SUCCESS -> {
                                when (it.message) {
                                    "Profile Edited" -> {
                                        loadingDialog.dismiss()
                                        tos("Profile Edited")
                                        finish()
                                    }
                                }
                            }
                            ApiStatus.ERROR -> {
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> binding.root.snacked("error")
                        }

                    }
                }
                launch {
                    userDao.getUser().collectLatest { user ->
                        dataUser = user
                        binding.user = user

                        val editableText: Editable =
                            Editable.Factory.getInstance().newEditable(dataUser?.name)
                        binding.etUsername.text = editableText
                        isUserInput = false

                        buttonShowFunction()

                    }
                }
            }
        }

    }

    private fun buttonShowFunction() {
        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this example
                binding.btnSave.visibility = Button.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Show the button only when the user manually inputs text
                if (isUserInput) {
                    Log.d("cek user input", isUserInput.toString())
                    binding.btnSave.visibility = Button.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed for this example
            }
        })

        // Set a flag to mark as user input
        isUserInput = true

    }

    private fun openPictureDialog() {
        val myArray: Array<String> = arrayOf("take from camera 1", "insert from gallery 2")
        MaterialAlertDialogBuilder(this).apply {
            setItems(myArray) { _, which ->
                // The 'which' argument contains the index position of the selected item
                when (which) {
                    0 -> (this@ProfileSettingsActivity).activityLauncher.openCamera(
                        this@ProfileSettingsActivity,
                        "${this@ProfileSettingsActivity.packageName}.fileprovider"
                    )
                    { file, _ ->
                        uploadAvatar(file)
                    }
                    1 -> (this@ProfileSettingsActivity).activityLauncher.openGallery(
                        this@ProfileSettingsActivity
                    ) { file, _ ->
                        uploadAvatar(file)
                    }
                }
            }
        }.create().show()
    }

    private fun uploadAvatar(file: File?) {
        if (file == null) {
            binding.root.snacked("Coba Masukan Gambar Lagi")
            isUserInput = false
            return
        }


        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val resizeBitmap = BitmapHelper.resizeBitmap(bitmap, 512f)

        file.delete()

        //Result Photo
        val uploadFile = this.createImageFile().also { it.writeBitmap(resizeBitmap) }

        //Processing the photo result
        filePhoto = uploadFile
        Log.d("cek isi photo", uploadFile.toString())

        if (uploadFile != null) {
            binding.btnSave.visibility = Button.VISIBLE
            binding.ivProfileNew.visibility = View.VISIBLE
//            binding.btnEditProfile.visibility = View.VISIBLE
            binding.ivProfileNew.let {
                Glide
                    .with(this)
                    .load(uploadFile)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .apply(RequestOptions.circleCropTransform())
                    .into(it)
            }
        } else {
            binding.root.snacked("Ungah Gambar Gagal, Coba Lagi")
//            binding?.ivUserEditedView?.visibility = View.GONE
        }


    }


    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (filePhoto != null || binding.btnSave.visibility == View.VISIBLE) {
            unsavedAlert()
            return
        } else {
            finish()
        }
    }


    private fun unsavedAlert() {
        val builder = AlertDialog.Builder(this@ProfileSettingsActivity)
        builder.setTitle("Unsaved Changes")
        builder.setMessage("You have unsaved changes. Are you sure you want to Dismiss changes?.")
            .setPositiveButton("Dismiss") { _, _ ->
                this@ProfileSettingsActivity.finish()
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