package com.bennohan.e_commerce.ui.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.e_commerce.R
import com.bennohan.e_commerce.base.BaseActivity
import com.bennohan.e_commerce.database.constant.Const
import com.bennohan.e_commerce.database.history.History
import com.bennohan.e_commerce.databinding.ActivityHistoryBinding
import com.bennohan.e_commerce.databinding.ItemHistoryBinding
import com.bennohan.e_commerce.ui.detail_product.DetailProductActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryActivity :
    BaseActivity<ActivityHistoryBinding, HistoryViewModel>(R.layout.activity_history) {

    private var dataHistory = ArrayList<History?>()

    private val adapterHistory by lazy {
        object : ReactiveListAdapter<ItemHistoryBinding, History>(R.layout.item_history) {
            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemHistoryBinding, History>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)
                var historyId: String?


                item?.let { itm ->
                    holder.binding.data = itm
                    holder.bind(itm)

                    historyId = item.productId
                    historyId?.let { Log.d("cek id product", it) }


                    holder.binding.btnBuyAgain.setOnClickListener {
                        openActivity<DetailProductActivity> {
                            putExtra(Const.PRODUCT.PRODUCT_ID, itm.productId)
                            itm.id?.let { it1 -> Log.d("cek item id", it1) }
                        }

                    }

                    //CONDITION
                    when {
                        historyId == "34f8c4b9-cbe1-40a3-9e9d-bf2bc09e6eed" -> {

                            holder.binding.tvProductName.text =
                                "Ophidia Mini GG Blue Red Strips Shoulder Bag Beige Ebony"

                            Glide
                                .with(this@HistoryActivity)
                                .load(getString(R.string.urlImage_Ophidia_Mini_GG_Blue))
                                .apply(RequestOptions.centerCropTransform())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(holder.binding.ivProduct)

                        }
                        historyId == "366cb383-a213-4c8d-acc0-1b9f82ba7c67" -> {

                            holder.binding.tvProductName.text =
                                "Erigo T-Shirt Basic Series Feni Jkt48 Black Unisex"


                            Glide
                                .with(this@HistoryActivity)
                                .load(getString(R.string.urlImage_Erigo_TShirt_Basic_Series_Feni_Jkt48_Black_Unisex))
                                .apply(RequestOptions.centerCropTransform())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(holder.binding.ivProduct)


                        }
                        historyId == "d58efd85-7a8e-4537-b560-fbd3fe5ab0bd" -> {
                            holder.binding.tvProductName.text = "Erigo Hoodie Fortune JKT48 Beige"

                            Glide
                                .with(this@HistoryActivity)
                                .load(getString(R.string.urlImage_Erigo_Hoodie_Fortune_JKT48_Beige))
                                .apply(RequestOptions.centerCropTransform())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(holder.binding.ivProduct)


                        }
                        historyId == "e1c46148-84a6-4025-969e-687a6dca4f18" -> {
                            holder.binding.tvProductName.text = "Nike Air Jordan 1 OG High Chicago"

                            Glide
                                .with(this@HistoryActivity)
                                .load(getString(R.string.urlImage_Nike_Air_Jordan_1_OG_High_Chicago))
                                .apply(RequestOptions.centerCropTransform())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(holder.binding.ivProduct)


                        }
                        historyId == "ef970ef2-4bdb-4253-ac5c-e16f8b6f5f48" -> {
                            holder.binding.tvProductName.text =
                                "Erigo Coach Jacket Idaina Kaeru Black"

                            Glide
                                .with(this@HistoryActivity)
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        getHistory()


        binding.rvHistory.adapter = adapterHistory

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getHistory() {
        viewModel.getIndexHistory()
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
                    viewModel.listHistory.collectLatest { listHistory ->
                        adapterHistory.submitList(listHistory)
                        dataHistory.clear()
                        dataHistory.addAll(listHistory)
                    }
                }
            }
        }
    }

}