package com.example.newsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.example.newsapp.model.Article
import com.example.newsapp.model.NewsResponce
import com.example.newsapp.repository.service.NewsRepository
import com.example.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class NewsViewModel(
    val newsrepository:NewsRepository
) : ViewModel() {
    val breakingNews: MutableLiveData<Resource<NewsResponce>> = MutableLiveData()
    var brekingPageNumebr = 1
    var breakingNewsResponse: NewsResponce? = null
    val searchNews: MutableLiveData<Resource<NewsResponce>> = MutableLiveData()
    var searchPageNumber = 1
    var searchNewsResponse: NewsResponce? = null
    lateinit var articles: LiveData<PagedList<Article>>
    init {
        getBreakingNews("in")
    }
    private fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsrepository.getBreakingNews(countryCode, brekingPageNumebr)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }
    private fun handleBreakingNewsResponse(response: Response<NewsResponce>): Resource<NewsResponce>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                brekingPageNumebr++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    fun getSearchedNews(queryString: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val searchNewsResponse = newsrepository.getSearchNews(queryString, searchPageNumber)
        searchNews.postValue(handleSearchNewsResponse(searchNewsResponse))
    }
    private fun handleSearchNewsResponse(respons: Response<NewsResponce>): Resource<NewsResponce>? {
        if (respons.isSuccessful) {
            respons.body()?.let { resultResponse ->
                searchPageNumber++
                if (searchNewsResponse == null){
                    searchNewsResponse = resultResponse
                }else{
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(respons.message())
    }
    fun insertArticle(article: Article) = viewModelScope.launch {
        newsrepository.upsert(article)
    }
    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsrepository.delete(article)
    }
    fun getSavedArticles() = newsrepository.getAllArticles()
    fun getBreakingNews() : LiveData<PagedList<Article>>{
        return articles
    }
}