package com.bennohan.e_commerce.api

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    //AUTH
    //login
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("email_or_phone") phoneOrEmail: String?,
        @Field("password") password: String?,
    ): String

    //Register
    @FormUrlEncoded
    @POST("api/auth/register")
    suspend fun register(
        @Field("name") name: String?,
        @Field("phone_or_email") phoneOrEmail: String?,
        @Field("password") password: String?,
        @Field("confirm_password") confirmPassword: String?,
    ): String



    @POST("api/auth/logout")
    suspend fun logout(
    ): String

    @GET("api/product")
    suspend fun indexProduct(
    ): String

    @GET("api/product/")
    suspend fun indexProductCategory(
    ): String

    @GET("api/product/{id}")
    suspend fun getProductById(
        @Path("id") productId : String?
    ): String

    @POST("api/catalogue/favorite/{id}")
    suspend fun favouriteProduct(
        @Path("id") productId : String?
    ): String

    @POST("/api/catalogue/unfavorite/{id}")
    suspend fun unFavouriteProduct(
        @Path("id") productId : String?
    ): String
    
    //Cart
    @FormUrlEncoded
    @POST("api/cart/add")
    suspend fun addCart(
        @Field("product_id") productId: String?,
        @Field("quantity") quantityProduct: String?,
    ): String

    @GET("api/cart")
    suspend fun getCart(
    ): String
}