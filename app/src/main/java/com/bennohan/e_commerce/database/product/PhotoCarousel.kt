package com.bennohan.e_commerce.database.product


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhotoCarousel(
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("photo")
    val photo: String?,
    @Expose
    @SerializedName("product_id")
    val productId: String?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?
)