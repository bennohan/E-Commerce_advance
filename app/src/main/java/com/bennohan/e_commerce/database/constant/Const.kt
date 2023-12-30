package com.bennohan.e_commerce.database.constant

import android.content.Context
import android.widget.Toast

object Const {
    object TOKEN {
        const val ACCESS_TOKEN = "access_token"
    }

    object PRODUCT {
        const val PRODUCT_ID = "id"
        const val PRODUCT_CATEGORY = "id"
        private const val PREF_NAME = "YourAppPreferences"
        private const val KEY_IS_FAVORITE = "isFavorite"

        fun isProductFavorite(context: Context, productId: String): Boolean {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return preferences.getBoolean(getFavoriteKey(productId), false)
        }

        fun setProductFavorite(context: Context, productId: String, isFavorite: Boolean) {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            preferences.edit().putBoolean(getFavoriteKey(productId), isFavorite).apply()

            // Display a Toast based on the favorite state
            val message = if (isFavorite) "Product Added to favorites" else "Product Removed from favorites"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        private fun getFavoriteKey(productId: String): String {
            return "$KEY_IS_FAVORITE-$productId"
        }


    }

}