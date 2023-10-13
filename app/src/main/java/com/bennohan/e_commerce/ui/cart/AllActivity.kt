package com.bennohan.e_commerce.ui.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.databinding.ActivityAllBinding
import com.crocodic.core.base.activity.NoViewModelActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllActivity : NoViewModelActivity<ActivityAllBinding>(R.layout.activity_all) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)
    }
}