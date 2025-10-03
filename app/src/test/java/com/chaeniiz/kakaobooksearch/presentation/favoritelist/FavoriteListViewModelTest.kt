package com.chaeniiz.kakaobooksearch.presentation.favoritelist

import app.cash.turbine.test
import com.chaeniiz.entity.Book
import com.chaeniiz.entity.FavoriteListSortType
import com.chaeniiz.entity.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteListViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: FavoriteListViewModel
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FavoriteListViewModel()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `즐겨찾기 목록 업데이트 시 화면에 모든 책이 표시되어야 한다`() {
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
        
        // When
        viewModel.handleIntent(FavoriteListIntent.UpdateFavoriteBooks(sampleBooks))
        
        // Then
        assertEquals(sampleBooks, viewModel.state.value.books)
        assertEquals(sampleBooks, viewModel.state.value.originalBooks)
    }
    
    @Test
    fun `검색어 입력 시 검색어에 맞는 책만 표시되어야 한다`() {
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
            ),
            Book(
                isbn = "456",
                title = "안드로이드 개발",
                authors = listOf("이철수"),
                publisher = "한빛미디어",
                publicationDate = "2023-02-01",
                price = 25000,
                salePrice = 0,
                thumbnail = "thumbnail2",
                description = "안드로이드 책입니다"
            )
        )
        
        // When
        viewModel.handleIntent(FavoriteListIntent.UpdateFavoriteBooks(sampleBooks))
        viewModel.handleIntent(FavoriteListIntent.UpdateQuery("코틀린"))
        
        // Then
        assertEquals(1, viewModel.state.value.books.size)
        assertEquals("코틀린 프로그래밍", viewModel.state.value.books.first().title)
    }
    
    @Test
    fun `저자명으로 검색 시 해당 저자의 책이 표시되어야 한다`() {
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
        
        // When
        viewModel.handleIntent(FavoriteListIntent.UpdateFavoriteBooks(sampleBooks))
        viewModel.handleIntent(FavoriteListIntent.UpdateQuery("김영하"))
        
        // Then
        assertEquals(1, viewModel.state.value.books.size)
        assertEquals(listOf("김영하"), viewModel.state.value.books.first().authors)
    }
    
    @Test
    fun `출판사명으로 검색 시 해당 출판사의 책이 표시되어야 한다`() {
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
        
        // When
        viewModel.handleIntent(FavoriteListIntent.UpdateFavoriteBooks(sampleBooks))
        viewModel.handleIntent(FavoriteListIntent.UpdateQuery("민음사"))
        
        // Then
        assertEquals(1, viewModel.state.value.books.size)
        assertEquals("민음사", viewModel.state.value.books.first().publisher)
    }
    
    @Test
    fun `제목으로 오름차순 정렬 시 제목 순으로 정렬되어야 한다`() {
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
            ),
            Book(
                isbn = "456",
                title = "안드로이드 개발",
                authors = listOf("이철수"),
                publisher = "한빛미디어",
                publicationDate = "2023-02-01",
                price = 25000,
                salePrice = 0,
                thumbnail = "thumbnail2",
                description = "안드로이드 책입니다"
            )
        )
        
        // When
        viewModel.handleIntent(FavoriteListIntent.UpdateFavoriteBooks(sampleBooks))
        viewModel.handleIntent(FavoriteListIntent.ChangeSort(FavoriteListSortType.TITLE, SortOrder.ASC))
        
        // Then
        assertEquals("안드로이드 개발", viewModel.state.value.books.first().title)
        assertEquals("코틀린 프로그래밍", viewModel.state.value.books.last().title)
    }
    
    @Test
    fun `가격으로 내림차순 정렬 시 가격 순으로 정렬되어야 한다`() {
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
            ),
            Book(
                isbn = "456",
                title = "안드로이드 개발",
                authors = listOf("이철수"),
                publisher = "한빛미디어",
                publicationDate = "2023-02-01",
                price = 25000,
                salePrice = 0,
                thumbnail = "thumbnail2",
                description = "안드로이드 책입니다"
            )
        )
        
        // When
        viewModel.handleIntent(FavoriteListIntent.UpdateFavoriteBooks(sampleBooks))
        viewModel.handleIntent(FavoriteListIntent.ChangeSort(FavoriteListSortType.PRICE, SortOrder.DESC))
        
        // Then
        assertEquals(25000, viewModel.state.value.books.first().effectivePrice)
        assertEquals(15000, viewModel.state.value.books.last().effectivePrice)
    }
    
    @Test
    fun `가격 필터 적용 시 가격 범위에 맞는 책만 표시되어야 한다`() {
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
            ),
            Book(
                isbn = "456",
                title = "안드로이드 개발",
                authors = listOf("이철수"),
                publisher = "한빛미디어",
                publicationDate = "2023-02-01",
                price = 25000,
                salePrice = 0,
                thumbnail = "thumbnail2",
                description = "안드로이드 책입니다"
            )
        )
        
        // When
        viewModel.handleIntent(FavoriteListIntent.UpdateFavoriteBooks(sampleBooks))
        viewModel.handleIntent(FavoriteListIntent.ApplyPriceFilter(10000, 20000))
        
        // Then
        assertEquals(1, viewModel.state.value.books.size)
        assertEquals(15000, viewModel.state.value.books.first().effectivePrice)
    }
    
    @Test
    fun `가격 필터 초기화 시 모든 책이 표시되어야 한다`() {
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
            ),
            Book(
                isbn = "456",
                title = "안드로이드 개발",
                authors = listOf("이철수"),
                publisher = "한빛미디어",
                publicationDate = "2023-02-01",
                price = 25000,
                salePrice = 0,
                thumbnail = "thumbnail2",
                description = "안드로이드 책입니다"
            )
        )
        
        // When
        viewModel.handleIntent(FavoriteListIntent.UpdateFavoriteBooks(sampleBooks))
        viewModel.handleIntent(FavoriteListIntent.ApplyPriceFilter(10000, 20000))
        viewModel.handleIntent(FavoriteListIntent.ClearPriceFilter)
        
        // Then
        assertEquals(2, viewModel.state.value.books.size)
    }
    
    @Test
    fun `빈 즐겨찾기 목록 업데이트 시 빈 목록이 표시되어야 한다`() {
        // Given
        val emptyBooks = emptyList<Book>()
        
        // When
        viewModel.handleIntent(FavoriteListIntent.UpdateFavoriteBooks(emptyBooks))
        
        // Then
        assertTrue(viewModel.state.value.books.isEmpty())
        assertTrue(viewModel.state.value.originalBooks.isEmpty())
    }
    
    @Test
    fun `검색어 지우기 시 모든 책이 표시되어야 한다`() {
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
        
        // When
        viewModel.handleIntent(FavoriteListIntent.UpdateFavoriteBooks(sampleBooks))
        viewModel.handleIntent(FavoriteListIntent.UpdateQuery("코틀린"))
        viewModel.handleIntent(FavoriteListIntent.UpdateQuery(""))
        
        // Then
        assertEquals(1, viewModel.state.value.books.size)
        assertEquals("", viewModel.state.value.query)
    }
    
    @Test
    fun `정렬 다이얼로그 열기 시 다이얼로그가 표시되어야 한다`() {
        // When
        viewModel.handleIntent(FavoriteListIntent.ShowSortDialog)
        
        // Then
        assertTrue(viewModel.state.value.showSortDialog)
    }
    
    @Test
    fun `필터 다이얼로그 열기 시 다이얼로그가 표시되어야 한다`() {
        // When
        viewModel.handleIntent(FavoriteListIntent.ShowFilterDialog)
        
        // Then
        assertTrue(viewModel.state.value.showFilterDialog)
    }
    
    @Test
    fun `다이얼로그 닫기 시 다이얼로그가 숨겨져야 한다`() {
        // Given
        viewModel.handleIntent(FavoriteListIntent.ShowSortDialog)
        
        // When
        viewModel.handleIntent(FavoriteListIntent.HideSortDialog)
        
        // Then
        assertFalse(viewModel.state.value.showSortDialog)
    }
    
    @Test
    fun `책 상세 화면 이동 시 네비게이션 이벤트가 발생해야 한다`() = runTest {
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
        
        // When & Then
        viewModel.effect.test {
            viewModel.handleIntent(FavoriteListIntent.ShowBookDetail(book))
            val effect = awaitItem()
            assertEquals(FavoriteListEffect.NavigateToDetail(book), effect)
        }
    }
}
