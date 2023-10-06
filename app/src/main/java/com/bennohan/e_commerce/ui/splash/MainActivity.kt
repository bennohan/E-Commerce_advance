package com.bennohan.e_commerce.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.databinding.ActivityMainBinding
import com.crocodic.core.base.activity.NoViewModelActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : NoViewModelActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Handler(Looper.getMainLooper()).postDelayed({
//            lifecycleScope.launch {
//                val user = userDao.isLogin()
//                if (!user) {
//                    binding.layoutUiCondition.visibility = View.VISIBLE
//                    binding.layoutUiSplash.visibility = View.GONE
//                    binding.btnLogin.setOnClickListener {
//                        openActivity<LoginActivity> {
//                            finish()
//                        }
//                    }
//                    binding.btnRegister.setOnClickListener {
//                        openActivity<RegisterActivity>{
//                            finish()
//                        }
//                    }
//                } else {
//                    binding.layoutUiCondition.visibility = View.GONE
//                    binding.layoutUiSplash.visibility = View.VISIBLE
//                    openActivity<HomeActivity> {
//                        finish()
//                    }
//                }
//            }

//        }, 4000)

    }
}