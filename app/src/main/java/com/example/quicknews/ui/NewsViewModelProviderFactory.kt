package com.example.quicknews.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quicknews.repository.NewsRepository

//A class that instantiate and return ViewModel.
class NewsViewModelProviderFactory(val app: Application, val newsRepository: NewsRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app, newsRepository) as T
    }

}