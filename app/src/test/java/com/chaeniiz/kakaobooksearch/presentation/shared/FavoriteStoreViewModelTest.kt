package com.chaeniiz.kakaobooksearch.presentation.shared

import app.cash.turbine.test
import com.chaeniiz.domain.usecase.GetFavoriteBooksUseCase
import com.chaeniiz.domain.usecase.ToggleFavoriteUseCase
import com.chaeniiz.entity.Book
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteStoreViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getFavoriteBooksUseCase: GetFavoriteBooksUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var viewModel: FavoriteStoreViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getFavoriteBooksUseCase = mockk()
        toggleFavoriteUseCase = mockk()
        viewModel = FavoriteStoreViewModel(getFavoriteBooksUseCase, toggleFavoriteUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `여러 책의 즐겨찾기를 순차적으로 토글하면 올바른 상태가 유지되어야 한다`() = runTest {
        // Given
        val book1 = Book(
            isbn = "1",
            title = "첫 번째 책",
            authors = listOf("저자1"),
            publisher = "출판사1",
            publicationDate = "2023-01-01",
            price = 10000,
            salePrice = 0,
            thumbnail = "thumbnail1",
            description = "첫 번째 책입니다"
        )

        val book2 = Book(
            isbn = "2",
            title = "두 번째 책",
            authors = listOf("저자2"),
            publisher = "출판사2",
            publicationDate = "2023-01-02",
            price = 20000,
            salePrice = 0,
            thumbnail = "thumbnail2",
            description = "두 번째 책입니다"
        )

        coEvery { toggleFavoriteUseCase(any()) } returns Unit

        // When & Then
        viewModel.favorites.test {
            // 초기 상태 스킵
            awaitItem()

            // 첫 번째 책 즐겨찾기 추가
            viewModel.handleIntent(FavoriteIntent.ToggleFavorite(book1))
            var books = awaitItem()
            assertEquals(1, books.size)
            assertEquals(book1.copy(isFavorite = true), books.first())

            // 두 번째 책 즐겨찾기 추가
            viewModel.handleIntent(FavoriteIntent.ToggleFavorite(book2))
            books = awaitItem()
            assertEquals(2, books.size)
            assertTrue(books.any { it.isbn == "1" })
            assertTrue(books.any { it.isbn == "2" })

            // 첫 번째 책 즐겨찾기 제거
            viewModel.handleIntent(FavoriteIntent.ToggleFavorite(book1.copy(isFavorite = true)))
            books = awaitItem()
            assertEquals(1, books.size)
            assertEquals(book2.copy(isFavorite = true), books.first())
        }
    }

    @Test
    fun `즐겨찾기 토글 시 UseCase가 올바르게 호출되어야 한다`() = runTest {
        // Given
        val book = Book(
            isbn = "123",
            title = "테스트 책",
            authors = listOf("테스트 저자"),
            publisher = "테스트 출판사",
            publicationDate = "2023-01-01",
            price = 20000,
            salePrice = 15000,
            thumbnail = "thumbnail",
            description = "테스트 설명"
        )

        coEvery { toggleFavoriteUseCase(any()) } returns Unit

        // When
        viewModel.handleIntent(FavoriteIntent.ToggleFavorite(book))

        // Then
        // Flow 상태 변화 확인 및 UseCase 호출 검증
        viewModel.favorites.test {
            awaitItem() // 초기 상태
            val books = awaitItem() // 토글 후 상태
            assertEquals(1, books.size)
            
            // UseCase가 정확히 호출되었는지 coVerify로 확인
            coVerify { toggleFavoriteUseCase(book) }
        }
    }
}
