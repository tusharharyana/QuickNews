package com.example.quicknews.repository

import com.example.quicknews.api.RetrofitInstance
import com.example.quicknews.db.ArticleDatabase
import com.example.quicknews.models.Article
import java.util.Locale.IsoCountryCode

//Repository: A container in which something is stored.
class NewsRepository(val db: ArticleDatabase) {

    //Present in NewsAPI.kt

    //suspend All these operation will run on background thread.
    suspend fun getHeadlines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getHeadlines(countryCode, pageNumber)
    //Fetch headlines using Retrofit based on countryCode and pageNumber arg.

    suspend fun searchForNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    //Present in ArticleDAO.kt

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getFavouritesNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}