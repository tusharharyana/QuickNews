package com.example.quicknews.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.quicknews.models.Article

//Database class(Abstract class): Main access point to underlying SQLite database.
@Database(
    entities = [Article::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class ArticleDatabase:RoomDatabase() {

    abstract fun getArticleDao(): ArticleDAO

    //Companion object: Anything present inside companion object is static and can be access anywhere using its name.
    companion object{

        @Volatile //Change made my one thread are immediately visible to other threads.
        private var instance: ArticleDatabase? = null
        private val LOCK = Any() //Used to synchronized database creation process.

        //Only one thread can execute the code inside block at a time.
        //invoke: operator is used for simplicity when create an object.
        //Double check locking pattern is implemented to ensure Thread safety during database creation process.
        operator fun invoke(context:Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context): Any {

            Room.databaseBuilder(

                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()

        }

    }

}