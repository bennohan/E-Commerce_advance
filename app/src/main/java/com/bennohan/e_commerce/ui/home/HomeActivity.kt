package com.bennohan.e_commerce.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.database.constant.Const
import com.bennohan.e_commerce.database.product.Product
import com.bennohan.e_commerce.databinding.ActivityHomeBinding
import com.bennohan.e_commerce.databinding.ProductItemBinding
import com.bennohan.e_commerce.ui.cart.CartActivity
import com.bennohan.e_commerce.ui.detail_product.DetailProductActivity
import com.bennohan.e_commerce.ui.profile.ProfileActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.denzcoskun.imageslider.models.SlideModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

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
                            intent.putExtra(Const.PRODUCT.PRODUCT_ID, item.id)
                        }

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
        imageSlider()
        buttonFilterCategory()


        binding.ivProfile.setOnClickListener {
            openActivity<ProfileActivity>()
        }

        binding.btnCart.setOnClickListener {
            openActivity<CartActivity>()
        }


    }

    private fun buttonFilterCategory() {
        binding.btnAllCategory.setOnClickListener {
            adapterProduct.submitList(dataProduct)
            Log.d("cek dataProduct", dataProduct.toString())
        }
        binding.btnCategoryTopi.setOnClickListener {
            adapterProduct.submitList(dataProduct.filter {
                it?.categoriesId == "420dfa1c-60e1-4cf9-95e5-5207edbe4598"
            })
        }
        binding.btnCategoryTShirt.setOnClickListener {
            adapterProduct.submitList(dataProduct.filter {
                it?.categoriesId == "4630174f-1848-4335-8b03-fcd7296b2846"
            })
        }
        binding.btnCategorySepatu.setOnClickListener {
            adapterProduct.submitList(dataProduct.filter {
                it?.categoriesId == "5a00888e-6c4f-4993-9a6b-b1bb254149ad"
            })
        }
        binding.btnCategoryJacket.setOnClickListener {
            adapterProduct.submitList(dataProduct.filter {
                it?.categoriesId == "8e834ea7-1831-4009-96a5-41d1b5d63514"
            })
        }
        binding.btnCategoryTas.setOnClickListener {
            adapterProduct.submitList(dataProduct.filter {
                it?.categoriesId == "b520d80c-452d-4f10-bcf0-d1ef4df30960"
            })
        }
        binding.btnCategoryHoddie.setOnClickListener {
            adapterProduct.submitList(dataProduct.filter {
                it?.categoriesId == "ea9ef245-c553-4c56-95c2-6a77dfba726a"
            })
        }
    }

    private fun imageSlider() {
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.image_banner_home_discount))
        imageList.add(SlideModel(R.drawable.image_banner_home_discount))
        binding.imageSlider.setImageList(imageList)

    }

    private fun search() {
        binding.searchView.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                val filter = dataProduct.filter {
                    it?.name?.contains(
                        "$text",
                        true
                    ) == true
//                            || it?.content?.contains("$text", true) == true
                }
                adapterProduct.submitList(filter)

//                if (filter.isEmpty()) {
//                    binding!!.tvNoteNotFound.visibility = View.VISIBLE
//                } else {
//                    binding!!.tvNoteNotFound.visibility = View.GONE
//                }
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
                    //TODO The different between collect and collect latest
                    viewModel.listProduct.collectLatest { listProduct ->
                        adapterProduct.submitList(listProduct)
                        dataProduct.clear()
                        dataProduct.addAll(listProduct)
                    }
                }
            }
        }
    }

    private fun getProduct() {
        viewModel.getProduct()
    }


}