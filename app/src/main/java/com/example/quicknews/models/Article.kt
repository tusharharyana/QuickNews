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
    var id:Int? = null,

    val author: String,
    val content: Any,
    val description: Any,
    val publishedAt: String,
    val source: Source,//Database not support obj datatype so type conversion needed
    val title: String,
    val url: String,
    val urlToImage: Any
):Serializable