package com.bennohan.e_commerce.ui.viewAll

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.database.constant.Const
import com.bennohan.e_commerce.database.product.Product
import com.bennohan.e_commerce.database.user.UserDao
import com.bennohan.e_commerce.databinding.ActivityAllBinding
import com.bennohan.e_commerce.databinding.ProductItemBinding
import com.bennohan.e_commerce.ui.detail_product.DetailProductActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AllActivity : BaseActivity<ActivityAllBinding, AllViewModel>(R.layout.activity_all) {

    @Inject
    lateinit var userDao: UserDao
    private var dataProduct = ArrayList<Product?>()

    private val adapterProduct by lazy {
        object : ReactiveListAdapter<ProductItemBinding, Product>(R.layout.product_item) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ProductItemBinding, Product>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)


                item?.let { itm ->
                    holder.binding.data = itm
                    holder.bind(itm)

                    holder.binding.cardProduct.setOnClickListener {
                        openActivity<DetailProductActivity> {
                            putExtra(Const.PRODUCT.PRODUCT_ID, itm.id)
                            itm.id?.let { it1 -> Log.d("cek item id", it1) }
                        }

                    }


                    val isFavorite =
                        item.id?.let { Const.PRODUCT.isProductFavorite(this@AllActivity, it) }

                    if (isFavorite == true) {
                        holder.binding.viewFavourite.setImageResource(R.drawable.ic_baseline_favorite_red)
                    } else {
                        holder.binding.viewFavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    }


                }

            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getProduct()
        observe()
        search()
        binding.rvProduct.adapter = adapterProduct

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun search() {
        binding.searchView.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                val filter = dataProduct.filter {
                    it?.name?.contains(
                        "$text",
                        true
                    ) == true
                }
                adapterProduct.submitList(filter)

                if (filter.isEmpty()) {
                    binding.tvProductNotFound.visibility = View.VISIBLE
                } else {
                    binding.tvProductNotFound.visibility = View.GONE
                }
            } else {
                adapterProduct.submitList(dataProduct)
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
                                loadingDialog.dismiss()
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.setResponse(it.message ?: return@collect)
                            }
                            else -> {

                            }
                        }

                    }
                }
                launch {
                    viewModel.listProduct.collectLatest { listProduct ->
                        adapterProduct.submitList(listProduct)
                        dataProduct.clear()
                        dataProduct.addAll(listProduct)
                        conditionCategory()
                    }
                }
                launch {
                    userDao.getUser().collectLatest { user ->
                        binding.user = user
                        Log.d("cek user", user.photo.toString())

                    }
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun conditionCategory() {
        val productCategoryId = intent.getStringExtra(Const.PRODUCT.PRODUCT_CATEGORY)
        if (productCategoryId?.isNotEmpty() == true) {
            adapterProduct.submitList(dataProduct.filter {
                it?.categoriesId == productCategoryId
            })
            when (productCategoryId) {
                "420dfa1c-60e1-4cf9-95e5-5207edbe4598" -> {
                    binding.tvCategory.text = "Topi"
                }
                "4630174f-1848-4335-8b03-fcd7296b2846" -> {
                    binding.tvCategory.text = "T-Shirt"
                }
                "5a00888e-6c4f-4993-9a6b-b1bb254149ad" -> {
                    binding.tvCategory.text = "Sepatu"
                }
                "8e834ea7-1831-4009-96a5-41d1b5d63514" -> {
                    binding.tvCategory.text = "Jaket"
                }
                "b520d80c-452d-4f10-bcf0-d1ef4df30960" -> {
                    binding.tvCategory.text = "Tas"
                }
                "ea9ef245-c553-4c56-95c2-6a77dfba726a" -> {
                    binding.tvCategory.text = "Hoodie"
                }
                else -> {
                    binding.tvCategory.text = "Popular"
                }

            }

        }
    }

    private fun getProduct() {
        viewModel.getIndexProduct()
    }


}