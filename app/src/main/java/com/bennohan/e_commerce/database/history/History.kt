package com.bennohan.e_commerce.database.history


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class History(
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("created_by")
    val createdBy: String?,
    @Expose
    @SerializedName("date")
    val date: String?,
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("product_id")
    val productId: String?,
    @Expose
    @SerializedName("quantity")
    val quantity: Int?,
    @Expose
    @SerializedName("status")
    val status: String?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?
)