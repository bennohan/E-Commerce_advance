package com.bennohan.e_commerce.database.category

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CategoryProduct(
    @Expose
    @SerializedName("category")
    val category: String,
    @Expose
    @SerializedName("created_at")
    val created_at: String,
    @Expose
    @SerializedName("id")
    val id: String,
    @Expose
    @SerializedName("updated_at")
    val updated_at: String
)