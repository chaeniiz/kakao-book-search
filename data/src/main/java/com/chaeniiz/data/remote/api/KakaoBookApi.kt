package com.chaeniiz.data.remote.api

import com.chaeniiz.data.remote.response.BookSearchResponse
import com.chaeniiz.entity.BookListSortType
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoBookApi {
    @GET("v3/search/book")
    suspend fun searchBooks(
        @Query("query") query: String,
        @Query("sort") sort: String = BookListSortType.ACCURACY.value,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): BookSearchResponse
}
