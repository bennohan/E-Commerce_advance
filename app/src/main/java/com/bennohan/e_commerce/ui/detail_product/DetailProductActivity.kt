package com.bennohan.e_commerce.ui.detail_product

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.database.constant.Const
import com.bennohan.e_commerce.database.product.Product
import com.bennohan.e_commerce.database.review.Review
import com.bennohan.e_commerce.databinding.ActivityDetailProductBinding
import com.bennohan.e_commerce.databinding.ItemReviewBinding
import com.bennohan.e_commerce.ui.cart.CartActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailProductActivity :
    BaseActivity<ActivityDetailProductBinding, DetailProductViewModel>(R.layout.activity_detail_product) {

    //    private var listDataImage = ArrayList<PhotoCarousel?>()
    var product: Product? = null
    private var productId: String? = null
    private var dataReview = ArrayList<Review?>()

    private val adapterProductReview by lazy {
        object : ReactiveListAdapter<ItemReviewBinding, Review>(R.layout.item_review) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemReviewBinding, Review>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)


                item?.let { itm ->
                    holder.binding.data = itm
                    holder.bind(itm)


                }

            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getProduct()
        observe()
        binding.rvReview.adapter = adapterProductReview
        tvAddReview()


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

    private fun tvAddReview() {
        val spannableString = SpannableString("Add Your Review")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                addReviewDialog()
            }
        }
        spannableString.setSpan(
            clickableSpan,
            0,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvAddReview.text = spannableString
        binding.tvAddReview.movementMethod =
            LinkMovementMethod.getInstance() // Required for clickable spans to work


    }

    //Dialog Add Review
    private fun addReviewDialog() {
        val bottomSheetDialog = BottomSheetDialog(this@DetailProductActivity)
        val view = layoutInflater.inflate(R.layout.dialog_add_review, null)

        val tvProduct = view.findViewById<TextView>(R.id.tv_productName)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val etReviewContent = view.findViewById<EditText>(R.id.et_reviewContent)
        val btnAddReview = view.findViewById<Button>(R.id.btn_submitReview)
        var currentRating: Float = 0.0f

        // Find and set up UI components inside the bottom sheet layout
        tvProduct.text = product?.name

        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                // Update a variable to store the current rating
                currentRating = rating
            }
        }

        btnAddReview.setOnClickListener {
            // Check if a rating has been given
            val ratingMessage = "You gave a rating of $currentRating stars!"
            Toast.makeText(this, ratingMessage, Toast.LENGTH_SHORT).show()
        }

        btnAddReview.setOnClickListener {
            val reviewContent = etReviewContent.textOf()
            productId?.let { it1 ->
                viewModel.addProductReview(
                    currentRating.toString(), reviewContent,
                    it1
                )
                loadingDialog.dismiss()
            }

        }

        bottomSheetDialog.dismiss()

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }


    private fun getReview() {
        viewModel.getReview()
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
                                loadingDialog.dismiss()
                                when (it.message) {
                                    "Add to Cart Success" -> {
                                        loadingDialog.dismiss()
                                        binding.root.snacked("Product added to cart")
                                    }
                                    "Add Review Success" -> {
                                        loadingDialog.dismiss()
                                        getReview()
                                        binding.root.snacked("Add Review Success \n Terimakasih sudah memberikan Review")
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
                        getReview()
                        productData?.id?.let { updateFavoriteButtonUI(it) }

                    }
                }
                launch {
                    viewModel.listPhotoProduct.collectLatest {
                    }
                }
                launch {
                    viewModel.listReview.collectLatest { listReview ->
                        dataReview.clear()
                        dataReview.addAll(listReview)
//                        adapterProductReview.submitList(it)
                        adapterProductReview.submitList(listReview.filter {
                            it?.productId == productId
                        })
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
        dialog.dismiss()


        dialog.setContentView(view)
        dialog.show()


    }

    //Favourite UnFavourite
    private fun updateFavoriteButtonUI(productId: String) {
        val isFavorite = Const.PRODUCT.isProductFavorite(this, productId)

        // Replace with your button ID
        val favoriteButton = findViewById<ImageButton>(R.id.btn_favourite)

        if (isFavorite) {
            // Set button color for true state
            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_red)
        } else {
            // Set button color for false state
            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }

        // Add click listener to toggle favorite state
        favoriteButton.setOnClickListener {
            val newFavoriteState = !isFavorite
            Const.PRODUCT.setProductFavorite(this, productId, newFavoriteState)

            // Update button color after toggling favorite state
            updateFavoriteButtonUI(productId)
        }
    }

}