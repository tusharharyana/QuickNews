package com.example.quicknews.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//Serialization: Process of converting object into format that can be easily stored or transmitted.
@Entity(
    tableName = "articles"
)
data class Article(

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    val author: String,
    val content: String?, // Changed to String or other appropriate type
    val description: String?, // Changed to String or other appropriate type
    val publishedAt: String,
    val source: Source, // Stored as JSON using the converter
    val title: String,
    val url: String,
    val urlToImage: String?
):Serializable