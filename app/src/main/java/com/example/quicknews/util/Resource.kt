package com.example.quicknews.util
/*
A sealed class in Kotlin is a type of class that restricts the inheritance hierarchy.
 It allows you to define a closed set of subclasses, meaning that all possible subclasses are known
 and can only be defined within the same file where the sealed class is declared.
 */
sealed class Resource<T>(

    val data:T? = null
    val message:String? = null
)
{
    class Success<T>(data: T):Resource<T>(data)
    class Error<T>(message: String,data: T?= null): Resource<T>(data,message)
    class Loading<T>: Resource<T>()
}