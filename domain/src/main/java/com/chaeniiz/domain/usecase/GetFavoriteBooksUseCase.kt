package com.chaeniiz.domain.usecase

import com.chaeniiz.domain.repository.FavoriteRepository
import com.chaeniiz.entity.Book
import javax.inject.Inject

class GetFavoriteBooksUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(): List<Book> {
        return favoriteRepository.getFavoriteBooks()
    }
}
