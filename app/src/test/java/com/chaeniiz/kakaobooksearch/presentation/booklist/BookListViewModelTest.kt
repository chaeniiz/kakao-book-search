package com.chaeniiz.kakaobooksearch.presentation.booklist

import app.cash.turbine.test
import com.chaeniiz.domain.usecase.SearchBooksWithFavoriteStatusUseCase
import com.chaeniiz.entity.Book
import com.chaeniiz.entity.BookSearchResult
import com.chaeniiz.entity.BookListSortType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var searchBooksWithFavoriteStatusUseCase: SearchBooksWithFavoriteStatusUseCase
    private lateinit var viewModel: BookListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchBooksWithFavoriteStatusUseCase = mockk()
        viewModel = BookListViewModel(searchBooksWithFavoriteStatusUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `검색어를 입력하면 검색 결과가 표시되어야 한다`() = runTest {
        // Given
        val sampleBooks = listOf(
            Book(
                isbn = "123",
                title = "코틀린 프로그래밍",
                authors = listOf("김영하"),
                publisher = "민음사",
                publicationDate = "2023-01-01",
                price = 20000,
                salePrice = 15000,
                thumbnail = "thumbnail1",
                description = "코틀린 책입니다"
            )
        )

        val searchResult = BookSearchResult(
            books = sampleBooks,
            isLastPage = false,
            currentPage = 1,
            totalCount = 100,
            pageableCount = 20
        )

        coEvery { searchBooksWithFavoriteStatusUseCase(any(), any(), any()) } returns searchResult

        // When
        viewModel.handleIntent(BookListIntent.UpdateQuery("코틀린"))

        // Then
        viewModel.state.test {
            // 초기 상태 스킵
            awaitItem()
            // 검색 결과 확인
            val state = awaitItem()
            assertEquals(sampleBooks, state.books)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `빈 검색 결과일 때 빈 목록이 표시되어야 한다`() = runTest {
        // Given
        val emptyResult = BookSearchResult.empty()
        coEvery { searchBooksWithFavoriteStatusUseCase(any(), any(), any()) } returns emptyResult

        // When
        viewModel.handleIntent(BookListIntent.UpdateQuery("존재하지 않는 책"))

        // Then
        viewModel.state.test {
            // 초기 상태 스킵
            awaitItem()
            // 검색 결과 확인
            val state = awaitItem()
            assertTrue(state.books.isEmpty())
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `정렬을 변경하면 정렬된 결과가 표시되어야 한다`() = runTest {
        // Given
        val sampleBooks = listOf(
            Book(
                isbn = "123",
                title = "코틀린 프로그래밍",
                authors = listOf("김영하"),
                publisher = "민음사",
                publicationDate = "2023-01-01",
                price = 20000,
                salePrice = 15000,
                thumbnail = "thumbnail1",
                description = "코틀린 책입니다"
            )
        )

        val searchResult = BookSearchResult(
            books = sampleBooks,
            isLastPage = false,
            currentPage = 1,
            totalCount = 100,
            pageableCount = 20
        )

        coEvery { searchBooksWithFavoriteStatusUseCase(any(), any(), any()) } returns searchResult

        // When
        viewModel.handleIntent(BookListIntent.UpdateQuery("코틀린"))
        viewModel.handleIntent(BookListIntent.ChangeSort(BookListSortType.ACCURACY))

        // Then
        viewModel.state.test {
            // 초기 상태 스킵
            awaitItem()
            // 정렬 변경 후 상태 확인
            val state = awaitItem()
            assertEquals(BookListSortType.ACCURACY, state.sort)
        }
    }

    @Test
    fun `즐겨찾기가 추가되면 즐겨찾기 상태가 업데이트되어야 한다`() = runTest {
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

        val booksWithFavorite = listOf(book.copy(isFavorite = true))
        val searchResult = BookSearchResult(
            books = booksWithFavorite,
            isLastPage = true,
            currentPage = 1,
            totalCount = 1,
            pageableCount = 1
        )

        coEvery { searchBooksWithFavoriteStatusUseCase(any(), any(), any()) } returns searchResult

        // When
        viewModel.handleIntent(BookListIntent.UpdateQuery("테스트"))
        viewModel.handleIntent(BookListIntent.UpdateFavoriteBooks(booksWithFavorite))

        // Then
        viewModel.state.test {
            // 초기 상태 스킵
            awaitItem()
            // 즐겨찾기 업데이트 후 상태 확인
            val state = awaitItem()
            assertTrue(state.books.isNotEmpty())
            assertTrue(state.books.first().isFavorite)
        }
    }

    @Test
    fun `책을 선택하면 상세 화면으로 이동하는 이벤트가 발생해야 한다`() = runTest {
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

        // When
        viewModel.handleIntent(BookListIntent.ShowBookDetail(book))

        // Then
        viewModel.effect.test {
            val effect = awaitItem()
            assertEquals(BookListEffect.NavigateToDetail(book), effect)
        }
    }
}
