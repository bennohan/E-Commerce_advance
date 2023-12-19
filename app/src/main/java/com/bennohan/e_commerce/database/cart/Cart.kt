package com.bennohan.e_commerce.database.cart


import com.bennohan.e_commerce.database.product.Product
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Cart(
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("created_at_formatted")
    val createdAtFormatted: String?,
    @Expose
    @SerializedName("created_by")
    val createdBy: String?,
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("photo_thumbnail")
    val photoThumbnail: Any?,
    @Expose
    @SerializedName("product_id")
    val productId: String?,
    @Expose
    @SerializedName("quantity")
    val quantity: Int?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?,
    @Expose
    @SerializedName("updated_at_formatted")
    val updatedAtFormatted: String?,
    @Expose
    @SerializedName("product")
    val product: Product?,

    )