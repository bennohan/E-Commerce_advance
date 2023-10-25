package com.bennohan.e_commerce.api

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    //AUTH
    //login
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email_or_phone") phoneOrEmail: String?,
        @Field("password") password: String?,
    ): String

    //Register
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String?,
        @Field("phone_or_email") phoneOrEmail: String?,
        @Field("password") password: String?,
        @Field("confirm_password") confirmPassword: String?,
    ): String


    @POST("auth/logout")
    suspend fun logout(
    ): String

    @GET("product")
    suspend fun indexProduct(
    ): String

    @GET("product/{id}")
    suspend fun getProductById(
        @Path("id") productId : Int?
    ): String


    //Cart
    @GET("cart/")
    suspend fun indexCart(
    ): String

    @FormUrlEncoded
    @POST("cart/add")
    suspend fun addCart(
        @Field("product_id") productId: String?,
        @Field("quantity") quantityProduct: String?,
    ): String
}