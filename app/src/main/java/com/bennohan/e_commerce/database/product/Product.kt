package com.bennohan.e_commerce.database.product


import com.bennohan.e_commerce.database.review.Review
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Product(
    @Expose
    @SerializedName("categories_id")
    val categoriesId: String?,
    @Expose
    @SerializedName("categories_name")
    val categoriesName: String?,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("created_at_formatted")
    val createdAtFormatted: String?,
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("name")
    val name: String?,
    @Expose
    @SerializedName("photo_carousel")
    val photoCarousel: List<PhotoCarousel?>?,
    @Expose
    @SerializedName("photo_thumbnail")
    val photoThumbnail: String?,
    @Expose
    @SerializedName("price")
    val price: String?,
    @Expose
    @SerializedName("product_description")
    val productDescription: String?,
    @Expose
    @SerializedName("stock")
    val stock: Int?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?,
    @Expose
    @SerializedName("updated_at_formatted")
    val updatedAtFormatted: String?,
    @Expose
    @SerializedName("review")
    val review: List<Review>?
)