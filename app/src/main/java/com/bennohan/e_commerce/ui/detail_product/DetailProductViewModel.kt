package com.bennohan.e_commerce.ui.detail_product

import androidx.lifecycle.viewModelScope
import com.bennohan.e_commerce.api.ApiService
import com.bennohan.e_commerce.base.BaseViewModel
import com.bennohan.e_commerce.database.UserDao
import com.bennohan.e_commerce.database.constant.Const
import com.bennohan.e_commerce.database.product.PhotoCarousel
import com.bennohan.e_commerce.database.product.Product
import com.bennohan.e_commerce.database.user.User
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailProductViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession,
    private val gson: Gson,
    private val userDao: UserDao
) :  BaseViewModel() {

    private var _product = MutableSharedFlow<Product?>()
    var product = _product.asSharedFlow()

    private var _listImage = MutableSharedFlow<List<PhotoCarousel?>>()
    var listPhotoProduct = _listImage.asSharedFlow()

    fun getProductById(id: Int) = viewModelScope.launch {
        ApiObserver({ apiService.getProductById(id) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<Product>(gson)
                    val dataImage = response.getJSONObject(ApiCode.DATA).getJSONArray("photo_carousel").toList<PhotoCarousel>(gson)
                    _listImage.emit(dataImage)
                    _product.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }


    fun addProduct(
        productId: String,
        quantityProduct: String,
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver({ apiService.addCart(productId, quantityProduct) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

}