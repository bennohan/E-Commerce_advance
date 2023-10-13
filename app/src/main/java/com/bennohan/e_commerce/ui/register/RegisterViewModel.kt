package com.bennohan.e_commerce.ui.register

import androidx.lifecycle.viewModelScope
import com.bennohan.e_commerce.api.ApiService
import com.bennohan.e_commerce.base.BaseViewModel
import com.bennohan.e_commerce.database.UserDao
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession,
    private val gson: Gson,
    private val userDao: UserDao
) :  BaseViewModel() {

    fun register(
        name: String,
        phoneOrEmail: String,
        password: String,
        confirmPassword: String
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver({ apiService.register(name,phoneOrEmail, password,confirmPassword) },
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
