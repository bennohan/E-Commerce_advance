package com.bennohan.e_commerce.ui.detail_product

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.database.constant.Const
import com.bennohan.e_commerce.database.product.Product
import com.bennohan.e_commerce.databinding.ActivityDetailProductBinding
import com.bennohan.e_commerce.ui.cart.CartActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.tos
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log

@AndroidEntryPoint
class DetailProductActivity :
    BaseActivity<ActivityDetailProductBinding, DetailProductViewModel>(R.layout.activity_detail_product) {

    //    private var listDataImage = ArrayList<PhotoCarousel?>()
    var product: Product? = null
    private var productId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getProduct()
        observe()

        binding.btnFavourite.setOnClickListener {
            favourite()
        }

        binding.btnAddCart.setOnClickListener {
            addCartDialog()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnCart.setOnClickListener {
            openActivity<CartActivity> {
                finish()
            }
        }

    }

    private fun favourite() {
        TODO("Not yet implemented")
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
                                    "Add to Cart Success" -> {
                                        loadingDialog.dismiss()
                                        tos("Product added to cart")
                                    }
                                }
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> {}
                        }

                    }
                }
                launch {
                    viewModel.product.collectLatest { productData ->
                        binding.data = productData
                        product = productData
                        productId = productData?.id
                    }
                }
                launch {
                    viewModel.listPhotoProduct.collectLatest {
                    }
                }
            }
        }
    }

    private fun getProduct() {
        val productId = intent.getStringExtra(Const.PRODUCT.PRODUCT_ID)
        if (productId != null) {
            viewModel.getProductById(productId)
        }
    }

    private fun addCartDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_cart, null)

        val buttonMin = view.findViewById<ImageButton>(R.id.btn_minQty)
        val tvProductName = view.findViewById<TextView>(R.id.tv_productName)
        val ivProduct = view.findViewById<ImageView>(R.id.iv_product)
        val buttonAdd = view.findViewById<ImageButton>(R.id.btn_plusQty)
        val etProductQty = view.findViewById<EditText>(R.id.et_qty)
        val btnAddCart = view.findViewById<Button>(R.id.btn_addCart)

        var currentQuantity = etProductQty.text.toString().toIntOrNull() ?: 1

        tvProductName.text = product?.name

        btnAddCart.setOnClickListener {
            Log.d("cek id", productId.toString())
            productId?.let { it1 -> viewModel.addProduct(it1, currentQuantity.toString()) }
        }

        buttonAdd.setOnClickListener {
            currentQuantity++
            etProductQty.setText(currentQuantity.toString())
            Log.d("cek isi", currentQuantity.toString())
        }

        buttonMin.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                etProductQty.setText(currentQuantity.toString())
            } else {
                dialog.dismiss()
            }

        }


        dialog.setContentView(view)
        dialog.show()


    }


}