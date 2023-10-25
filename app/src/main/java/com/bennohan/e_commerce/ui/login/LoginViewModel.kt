package com.bennohan.e_commerce.ui.login

import androidx.lifecycle.viewModelScope
import com.bennohan.e_commerce.api.ApiService
import com.bennohan.e_commerce.base.BaseViewModel
import com.bennohan.e_commerce.database.user.User
import com.bennohan.e_commerce.database.UserDao
import com.bennohan.e_commerce.database.constant.Const
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession,
    private val gson: Gson,
    private val userDao: UserDao
) :  BaseViewModel() {

    fun login(
        phoneOrEmail: String,
        password: String,
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver({ apiService.login(phoneOrEmail, password) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    val token = response.getJSONObject(ApiCode.DATA).getJSONObject("token").getString("access_token")
                    userDao.insert(data.copy(idRoom = 1))
                    session.setValue(Const.TOKEN.ACCESS_TOKEN,token)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }


}
