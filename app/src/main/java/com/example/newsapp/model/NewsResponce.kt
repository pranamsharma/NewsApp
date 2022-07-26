package com.example.newsapp.model

import com.google.gson.annotations.SerializedName

data class NewsResponce(
    @SerializedName("articles")
    var articles : MutableList<Article>,
    @SerializedName("status")
    var status: String,
    @SerializedName("totalResults")
    var totalResults: Int?
)
