package com.bennohan.e_commerce.ui.cart
import androidx.lifecycle.viewModelScope
import com.bennohan.e_commerce.api.ApiService
import com.bennohan.e_commerce.base.BaseViewModel
import com.bennohan.e_commerce.database.UserDao
import com.bennohan.e_commerce.database.cart.Cart
import com.bennohan.e_commerce.database.product.Product
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
class CartViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession,
    private val gson: Gson,
    private val userDao: UserDao
) :  BaseViewModel() {

    private var _listCart = MutableSharedFlow<List<Cart?>>()
    var listCart = _listCart.asSharedFlow()


    fun getIndexCart(
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.getCart() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).getJSONArray("cart").toList<Cart>(gson)
                    _listCart.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }


}