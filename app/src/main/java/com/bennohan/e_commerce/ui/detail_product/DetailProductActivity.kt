package com.bennohan.e_commerce.ui.detail_product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.database.cart.Cart
import com.bennohan.e_commerce.database.constant.Const
import com.bennohan.e_commerce.database.product.PhotoCarousel
import com.bennohan.e_commerce.database.product.Product
import com.bennohan.e_commerce.databinding.ActivityDetailProductBinding
import com.crocodic.core.api.ApiStatus
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailProductActivity :
    BaseActivity<ActivityDetailProductBinding, DetailProductViewModel>(R.layout.activity_detail_product) {

//    private var listDataImage = ArrayList<PhotoCarousel?>()
    val imageList = ArrayList<SlideModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getProduct()
        observe()
        imageSlider()

        binding.btnAddCart.setOnClickListener {
            addCart()
        }

    }

    private fun imageSlider() {
 // Create image list

        imageList.add(SlideModel())

        val imageSlider = findViewById<ImageSlider>(R.id.iv_product)
        imageSlider.setImageList(imageList)
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> {}
                            ApiStatus.SUCCESS -> {}
                            ApiStatus.ERROR -> {
//                                disconnect(it)
//                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> {}
                        }

                    }
                }
                launch {
                    viewModel.product.collectLatest { productData ->
                        binding.data =productData
                    }
                }
                launch {
                    viewModel.listPhotoProduct.collectLatest {
                        imageList.clear()
                        imageList.addAll()
                    }
                }
            }
        }
    }

    private fun getProduct() {
        val productId = intent.getIntExtra(Const.PRODUCT.PRODUCT_ID, 0)
        viewModel.getProductById(productId)

    }

    private fun addCart() {
    }
}