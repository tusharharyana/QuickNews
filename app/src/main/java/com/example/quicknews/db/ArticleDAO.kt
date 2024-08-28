package com.example.quicknews.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quicknews.models.Article
import retrofit2.http.DELETE

//DAO: Database Access Object : having abstract methods to handle CRUD operations.
//Bridge between app data and database.
@Dao
interface ArticleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)//If same primary key is in database then old data is replaced with new data
    suspend fun upsert(article: Article):Long//return long(which is pk of article)
    //suspend function show: this would be call from a coroutine(handle multi tasking)

    @Query("SELECT * from articles")
    fun getAllArticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
    //database op performed on background thread

}