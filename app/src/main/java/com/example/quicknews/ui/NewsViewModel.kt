package com.example.quicknews.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quicknews.models.Article
import com.example.quicknews.models.NewsResponse
import com.example.quicknews.repository.NewsRepository
import com.example.quicknews.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Query
import java.io.IOException
import java.util.Locale.IsoCountryCode

// ?. safe call operator
class NewsViewModel(app: Application, val newsRepository: NewsRepository ):AndroidViewModel(app) {

    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var headlinesPage = 1
    private var headlinesResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null

    private var newSearchQuery: String? = null
    private var oldSearchQuery: String? = null

        init{
            getHeadlines("us")
        }

        fun getHeadlines(countryCode: String) = viewModelScope.launch {
            headlinesInternet(countryCode)
        }

        fun searchNews(searchQuery: String) = viewModelScope.launch {
            searchNewsInternet(searchQuery)
        }

    private fun handleHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {//If network request is successful.

            response.body()?.let { resultResponse ->
                headlinesPage++

                if (headlinesResponse == null) {
                    headlinesResponse = resultResponse
                } else {
                    val oldArticles = headlinesResponse?.articles
                    val newArticles = resultResponse?.articles
                    if (newArticles != null) {
                        oldArticles?.addAll(newArticles)
                    }
                }

                return Resource.Success(headlinesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

        private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
            if (response.isSuccessful) {//If network request is successful.

                response.body()?.let { resultResponse ->
                    if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                        searchNewsPage = 1
                        oldSearchQuery = newSearchQuery
                        searchNewsResponse = resultResponse
                    } else {
                        searchNewsPage++
                        val oldArticles = searchNewsResponse?.articles
                        val newArticles = resultResponse.articles
                            oldArticles?.addAll(newArticles)
                    }
                    return Resource.Success(searchNewsResponse ?: resultResponse)
                }
            }
            return Resource.Error(response.message())
        }


        fun addToFavourites(article: Article) = viewModelScope.launch {
            newsRepository.upsert(article)
        }

        fun getFavouriteNews() = newsRepository.getFavouritesNews()

        fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
        }

        private fun internetConnection(context: Context): Boolean{

            (context.getSystemService(Context.CONNECTIVITY_SERVICE)as ConnectivityManager).apply {
                return getNetworkCapabilities(activeNetwork)?.run {
                    when{
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                } ?: false
            }
        }

        private suspend fun headlinesInternet(countryCode: String){
            headlines.postValue(Resource.Loading())

            try{
                if(internetConnection(this.getApplication())){
                    val response = newsRepository.getHeadlines(countryCode,headlinesPage)
                    headlines.postValue(handleHeadlinesResponse(response))
                }
                else{
                    headlines.postValue(Resource.Error("No internet connection"))
                }
            }
            catch (t: Throwable){
                when(t){
                    is IOException -> headlines.postValue(Resource.Error("Unable to connect"))
                    else -> headlines.postValue(Resource.Error("No signal"))
                }
            }
        }


        private suspend fun searchNewsInternet(searchQuery: String){
            newSearchQuery = searchQuery
            searchNews.postValue(Resource.Loading())
            try{
                if(internetConnection(this.getApplication())){
                    val response = newsRepository.searchForNews(searchQuery ,searchNewsPage)
                    searchNews.postValue(handleSearchNewsResponse(response))
                }
                else{
                    searchNews.postValue(Resource.Error("No internet connection"))
                }
            }
            catch (t: Throwable){
                when(t){
                    is IOException -> searchNews.postValue(Resource.Error("Unable to connect"))
                    else -> searchNews.postValue(Resource.Error("No signal"))
                }
            }
        }

    }
