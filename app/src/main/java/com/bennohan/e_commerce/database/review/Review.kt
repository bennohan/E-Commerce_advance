package com.bennohan.e_commerce.database.review


import com.bennohan.e_commerce.database.user.User
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Review(
    @Expose
    @SerializedName("content")
    val content: String?,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("product_id")
    val productId: String?,
    @Expose
    @SerializedName("star")
    val star: Int?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?,
    @Expose
    @SerializedName("user")
    val user: User?,
    @Expose
    @SerializedName("user_id")
    val userId: String?,
    @Expose
    @SerializedName("updated_at_formatted")
    val updatedAtFormatted: String?,
    @Expose
    @SerializedName("user_name")
    val userName: String?,
    @Expose
    @SerializedName("user_photo")
    val userPhoto: String?
)