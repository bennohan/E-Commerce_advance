package com.bennohan.e_commerce.ui.detail_product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.tos
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailProductActivity :
    BaseActivity<ActivityDetailProductBinding, DetailProductViewModel>(R.layout.activity_detail_product) {

    //    private var listDataImage = ArrayList<PhotoCarousel?>()
    val imageList = ArrayList<SlideModel>()
    private var dataProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getProduct()
        observe()

        binding.btnAddCart.setOnClickListener {
            dataProduct?.let { it1 -> addCart(it1) }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

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
                        binding.data = productData
                        dataProduct = productData
                    }
                }
                launch {
                    viewModel.listPhotoProduct.collectLatest {
                        imageList.clear()
                    }
                }
            }
        }
    }

    private fun getProduct() {
        val productId = intent.getIntExtra(Const.PRODUCT.PRODUCT_ID, 0)
        viewModel.getProductById(productId)

    }

    private fun addCart(data: Product) {
        val bottomSheetDialog = BottomSheetDialog(this@DetailProductActivity)
        val view = layoutInflater.inflate(R.layout.dialog_add_cart, null)
        val tvProductName = findViewById<TextView>(R.id.tv_productName)
        val ivProduct = findViewById<ImageView>(R.id.iv_product)
        val btnMinCart = findViewById<ImageButton>(R.id.btn_minCart)
        val btnAddCart = findViewById<ImageButton>(R.id.btn_addCart)
        val etQty = findViewById<EditText>(R.id.et_qty)
        var currentQuantity = etQty.text.toString().toIntOrNull() ?: 1

        tvProductName.text = data.name

        btnMinCart.setOnClickListener {
            if (currentQuantity > 0) {
                currentQuantity--
                etQty.setText(currentQuantity.toString())
            }else{
                tos("Apakah Kamu Yakin Ingin Menghapus Produk")
            }
        }

        btnAddCart.setOnClickListener {
                currentQuantity++
                etQty.setText(currentQuantity.toString())
        }

//        Glide
//            .with(view.context)
//            .load(data.photoThumbnail)
//            .apply(RequestOptions.centerCropTransform())
//            .placeholder(R.drawable.ic_baseline_person_24)
//            .into(ivProduct)

        bottomSheetDialog.dismiss()

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }
}