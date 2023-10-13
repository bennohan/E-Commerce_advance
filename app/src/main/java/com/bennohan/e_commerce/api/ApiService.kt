package com.bennohan.e_commerce.api

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    //AUTH
    //login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("phone_or_email") phoneOrEmail: String?,
        @Field("password") password: String?,
    ): String

    //Register
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String?,
        @Field("phone_or_email") phoneOrEmail: String?,
        @Field("password") password: String?,
        @Field("confirm_password") confirmPassword: String?,
    ): String

}