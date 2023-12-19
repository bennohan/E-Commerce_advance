package com.bennohan.e_commerce.ui.cart

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.database.cart.Cart
import com.bennohan.e_commerce.database.constant.Const
import com.bennohan.e_commerce.database.product.Product
import com.bennohan.e_commerce.databinding.ActivityCartBinding
import com.bennohan.e_commerce.databinding.ItemCartBinding
import com.bennohan.e_commerce.databinding.ProductItemBinding
import com.bennohan.e_commerce.ui.detail_product.DetailProductActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartActivity : BaseActivity<ActivityCartBinding , CartViewModel>(R.layout.activity_cart) {

    private var dataCartProduct = ArrayList<Cart?>()

    private val adapterCart by lazy {
            object : ReactiveListAdapter<ItemCartBinding, Cart>(R.layout.item_cart) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemCartBinding, Cart>,
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

        getListCart()
        observe()
        binding.rvCart.adapter = adapterCart

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
                            ApiStatus.LOADING -> {
                                loadingDialog.show()
                            }
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                            }
                            ApiStatus.ERROR -> {
//                                disconnect(it)
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> {}
                        }

                    }
                }
                launch {
                    viewModel.listCart.collectLatest { listCart ->
                        adapterCart.submitList(listCart)
                        dataCartProduct.clear()
                        dataCartProduct.addAll(listCart)
                    }
                }
            }
        }
    }

    private fun getListCart() {
        viewModel.getIndexCart()
    }
}