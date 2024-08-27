package com.example.quicknews.db

import androidx.room.TypeConverter
import com.example.quicknews.models.Source

class Converters {

    @TypeConverter //To tell compiler its a converter class.
    fun fromSource(source: Source): String{
        return source.name
    }

    @TypeConverter
    fun toSource(name:String): Source{
        return Source(name,name)
    }

}