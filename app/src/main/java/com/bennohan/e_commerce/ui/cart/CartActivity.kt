package com.bennohan.e_commerce.ui.cart

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.database.cart.Cart
import com.bennohan.e_commerce.databinding.ActivityCartBinding
import com.bennohan.e_commerce.databinding.ItemCartBinding
import com.bennohan.e_commerce.ui.home.HomeActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartActivity : BaseActivity<ActivityCartBinding, CartViewModel>(R.layout.activity_cart) {

    private var dataCartProduct = ArrayList<Cart?>()
    private var idCart: String? = null

    private val adapterCart by lazy {
        object : ReactiveListAdapter<ItemCartBinding, Cart>(R.layout.item_cart) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemCartBinding, Cart>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)
                var productId: String?


                item?.let { itm ->
                    holder.binding.data = itm
                    holder.bind(itm)
                    productId = item.productId
                    idCart = item.id

                    var quantity = item.quantity ?: 0 // Initial quantity

                    holder.binding.btnAddQty.setOnClickListener {
                        quantity++
                        // Update the TextView to display the new quantity
                        holder.binding.tvProductQty.text = quantity.toString()
                        item.id?.let { it1 ->
                            productId?.let { it2 ->
                                viewModel.editCart(
                                    it1,
                                    it2, quantity.toString()
                                )
                            }
                            return@setOnClickListener
                        }
                    }

                    holder.binding.btnMinQty.setOnClickListener {
                        if (itm.quantity == 1) {
                            dialogDelete()
                            tos("con")
                            return@setOnClickListener
                        } else {
                            quantity--
                            holder.binding.tvProductQty.text = quantity.toString()
                            item.id?.let { it1 ->
                                productId?.let { it2 ->
                                    viewModel.editCart(
                                        it1,
                                        it2, quantity.toString()
                                    )
                                }
                            }
                            return@setOnClickListener
                        }
                    }

                    //CONDITION
                    when {
                        productId == "34f8c4b9-cbe1-40a3-9e9d-bf2bc09e6eed" -> {

                            Glide
                                .with(this@CartActivity)
                                .load(getString(R.string.urlImage_Ophidia_Mini_GG_Blue))
                                .apply(RequestOptions.centerCropTransform())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(holder.binding.ivProduct)

                        }
                        productId == "366cb383-a213-4c8d-acc0-1b9f82ba7c67" -> {


                            Glide
                                .with(this@CartActivity)
                                .load(getString(R.string.urlImage_Erigo_TShirt_Basic_Series_Feni_Jkt48_Black_Unisex))
                                .apply(RequestOptions.centerCropTransform())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(holder.binding.ivProduct)


                        }
                        productId == "d58efd85-7a8e-4537-b560-fbd3fe5ab0bd" -> {

                            Glide
                                .with(this@CartActivity)
                                .load(getString(R.string.urlImage_Erigo_Hoodie_Fortune_JKT48_Beige))
                                .apply(RequestOptions.centerCropTransform())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(holder.binding.ivProduct)


                        }
                        productId == "e1c46148-84a6-4025-969e-687a6dca4f18" -> {

                            Glide
                                .with(this@CartActivity)
                                .load(getString(R.string.urlImage_Nike_Air_Jordan_1_OG_High_Chicago))
                                .apply(RequestOptions.centerCropTransform())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(holder.binding.ivProduct)


                        }
                        productId == "ef970ef2-4bdb-4253-ac5c-e16f8b6f5f48" -> {

                            Glide
                                .with(this@CartActivity)
                                .load(getString(R.string.urlImage_Erigo_Coach_Jacket_Idaina_Kaeru_Black))
                                .apply(RequestOptions.centerCropTransform())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(holder.binding.ivProduct)

                        }
                        else -> {}
                    }


                }

            }

        }
    }

    private fun dialogDelete() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Remove Product")
        builder.setMessage(R.string.deleteCart_message)
            .setCancelable(false)
            .setPositiveButton("Delete Product") { _, _ ->
                // Delete selected note from database
                idCart?.let { viewModel.deleteCart(it) }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog: AlertDialog = builder.create()

        // Set the color of the positive button text
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.white))
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        com.crocodic.core.R.color.text_red
                    )
                )
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.black))
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.buttonColorYellow))
        }

        // Show the dialog
        dialog.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getListCart()
        observe()
        binding.rvCart.adapter = adapterCart

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnOrderNow.setOnClickListener {
            orderNow()
        }


    }

    private fun orderNow() {
        viewModel.transactionCart()
    }

    @SuppressLint("SetTextI18n")
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
                                    "Transaction Success" -> {
                                        loadingDialog.dismiss()
                                        tos("Transaction Success")
                                        openActivity<HomeActivity> {
                                            finish()
                                        }
                                    }
                                    "Cart Edited" -> {
                                        loadingDialog.dismiss()
                                        getListCart()
                                    }
                                    "Cart Get List" -> {
                                        loadingDialog.dismiss()
                                    }
                                    "Product successfully deleted" -> {
                                        getListCart()
                                    }
                                    else -> {
                                        getListCart()

                                    }
                                }
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

                        if (dataCartProduct.isEmpty()) {
                            binding.tvProductNotFound.visibility = View.VISIBLE
                        }else{
                            binding.tvProductNotFound.visibility = View.GONE
                        }

                    }
                }
                launch {
                    viewModel.totalCost.collect { totalCostData ->
                        binding.tvTotalCost.text = "Total Cost = Rp.${totalCostData}"
                        Log.d("cek total activity", totalCostData.toString())
                    }
                }
            }
        }
    }

    private fun getListCart() {
        viewModel.getIndexCart()
    }
}