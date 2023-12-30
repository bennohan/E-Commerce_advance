package com.bennohan.e_commerce.ui.detail_product

import androidx.lifecycle.viewModelScope
import com.bennohan.e_commerce.api.ApiService
import com.bennohan.e_commerce.base.BaseViewModel
import com.bennohan.e_commerce.database.product.PhotoCarousel
import com.bennohan.e_commerce.database.product.Product
import com.bennohan.e_commerce.database.review.Review
import com.bennohan.e_commerce.database.user.UserDao
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

    private var _listReview = MutableSharedFlow<List<Review?>>()
    var listReview = _listReview.asSharedFlow()


    fun getProductById(id: String) = viewModelScope.launch {
        ApiObserver({ apiService.getProductById(id) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<Product>(gson)
                    val dataImage = response.getJSONObject(ApiCode.DATA).getJSONArray("photo_carousel").toList<PhotoCarousel>(gson)
//                    val dataReview = response.getJSONObject(ApiCode.DATA).getJSONArray("review").toList<Review>(gson)
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
                    _apiResponse.emit(ApiResponse().responseSuccess("Add to Cart Success"))

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun addProductReview(
        content: String,
        star: String,
        productId: String,
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver({ apiService.addReview(content,star,productId) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess("Add Review Success"))

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun getReview(
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver({ apiService.getReview() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val review = response.getJSONArray(ApiCode.DATA).toList<Review>(gson)
                    _listReview.emit(review)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

}