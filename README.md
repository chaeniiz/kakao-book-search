# 카카오 도서 검색 앱

카카오 도서 검색 API를 활용한 도서 검색 앱입니다.

## 기술 스택

### 프로덕션
- **언어**: Kotlin
- **UI**: Jetpack Compose
- **아키텍처**: Clean Architecture + MVI Pattern
- **의존성 주입**: Hilt
- **네트워킹**: Retrofit
- **데이터베이스**: Room
- **이미지 로딩**: Coil
- **비동기 처리**: Coroutines
- **빌드 도구**: Gradle with KSP

### 테스트
- **테스트 프레임워크**: JUnit 4
- **모킹**: MockK
- **코루틴 테스트**: Kotlinx Coroutines Test
- **Flow 테스트**: Turbine

## 프로젝트 구조

```
kakao-book-search/
├── app/                    # Application Module
│   └── src/main/java/com/chaeniiz/kakaobooksearch/
│       ├── 📂 base/                           # 기본 클래스들
│       │   ├── BaseViewModel.kt               # 기본 ViewModel
│       │   ├── UiState.kt                     # 마커 인터페이스
│       │   ├── UiIntent.kt                    # 마커 인터페이스
│       │   └── UiEffect.kt                    # 마커 인터페이스
│       │
│       ├── 📂 presentation/                   # 프레젠테이션 레이어
│       │   ├── 📂 main/                       # 메인 화면 관련
│       │   │   ├── MainActivity.kt            # 메인 액티비티
│       │   │   └── NavGraph.kt                # 네비게이션 그래프
│       │   │
│       │   ├── 📂 booklist/                   # 도서 검색 리스트 기능
│       │   │   ├── BookListScreen.kt          # UI
│       │   │   ├── BookListViewModel.kt       # ViewModel
│       │   │   ├── BookListState.kt           # 상태
│       │   │   ├── BookListIntent.kt          # 의도
│       │   │   └── BookListEffect.kt          # 효과
│       │   │
│       │   ├── 📂 favoritelist/               # 즐겨찾기 리스트 기능
│       │   │   ├── FavoriteListScreen.kt      # UI
│       │   │   ├── FavoriteListViewModel.kt   # ViewModel
│       │   │   ├── FavoriteListState.kt       # 상태
│       │   │   ├── FavoriteListIntent.kt      # 의도
│       │   │   └── FavoriteListEffect.kt      # 효과
│       │   │
│       │   ├── 📂 bookdetail/                 # 도서 상세 기능
│       │   │   ├── BookDetailScreen.kt        # UI
│       │   │   ├── BookDetailViewModel.kt     # ViewModel
│       │   │   ├── BookDetailState.kt         # 상태
│       │   │   ├── BookDetailIntent.kt        # 의도
│       │   │   └── BookDetailEffect.kt        # 효과
│       │   │
│       │   └── 📂 shared/                     # 공통 프레젠테이션 요소
│       │       ├── FavoriteStoreViewModel.kt  # 전역 즐겨찾기 상태 관리
│       │       ├── FavoriteStoreState.kt      # 전역 즐겨찾기 상태
│       │       └── FavoriteIntent.kt          # 전역 즐겨찾기 의도
│       │
│       └── 📂 common/                         # 공통 유틸리티
│           ├── 📂 component/                  # 재사용 가능한 UI 컴포넌트
│           │   ├── BookCard.kt                # 도서 카드
│           │   ├── SearchBar.kt               # 검색바
│           │   ├── BottomTabBar.kt            # 하단 탭바
│           │   └── FilterButton.kt            # 필터 버튼
│           │
│           ├── 📂 theme/                      # 테마 관련
│           │   ├── Theme.kt                   # 앱 테마
│           │   ├── Color.kt                   # 색상 정의
│           │   └── Type.kt                    # 타이포그래피
│           │
│           └── 📂 util/                       # 유틸리티
│               └── FormatUtils.kt             # 포맷팅 유틸리티
│
├── data/                   # Data Layer
│   ├── remote/             # API 관련
│   ├── local/              # 로컬 데이터베이스
│   ├── repository/         # Repository 구현체
│   └── di/                 # Data Layer DI
│
├── domain/                 # Domain Layer
│   ├── repository/         # Repository 인터페이스
│   └── usecase/            # Use Case
│
└── entity/                 # Entity Layer
    └── entity/             # 데이터 모델
```

## 주요 기능

### 1. 도서 검색
- 카카오 도서 검색 API를 통한 도서 검색
- 제목, 출판사, 또는 저자명으로 검색 가능
- 정확도순/발간일순 정렬
- 페이징 처리 (20개씩)

### 2. 즐겨찾기
- 도서 즐겨찾기 추가/제거
- 로컬 데이터베이스에 즐겨찾기 저장
- 즐겨찾기 목록에서 검색 및 정렬

### 3. 도서 상세
- 도서 상세 정보 표시
- 도서 이미지, 제목, 저자, 출판사, 가격 등
- 즐겨찾기 토글 기능

## 설정 방법

### 1. API 키 설정

```kotlin
// data/build.gradle.kts
buildConfigField("String", "KAKAO_API_KEY", "\"YOUR_API_KEY\"")
```

> **참고**: 이 프로젝트를 빌드하기 위해서는 카카오 개발자 센터에서 API Key를 발급받아야 합니다.

### 2. 빌드 및 실행

```bash
# 프로젝트 클론
git clone <repository-url>
cd kakao-book-search

# 의존성 동기화 및 빌드
./gradlew build

# 단위 테스트 실행
./gradlew app:testDebugUnitTest

# 앱 실행
./gradlew installDebug
```

## 화면 구성

### 1. 검색 리스트 화면
- 검색바를 통한 도서 검색
- 정렬 옵션 (정확도순/발간일순)
- 도서 카드 형태의 목록 표시
- 즐겨찾기 토글 버튼
- 무한 스크롤 페이징

### 2. 즐겨찾기 리스트 화면
- 즐겨찾기한 도서 목록
- 로컬 검색 기능
- 정렬 및 필터 옵션
- 즐겨찾기 해제 기능

### 3. 도서 상세 화면
- 도서 상세 정보 표시
- 도서 이미지 및 메타데이터
- 책 소개 내용
- 즐겨찾기 토글

## 아키텍처 패턴

### Clean Architecture
- **Presentation Layer**: UI, ViewModel, State 관리 (Feature-based 패키징)
- **Data Layer**: Repository 구현, API, Database
- **Domain Layer**: 비즈니스 로직, Use Case
- **Entity Layer**: 데이터 모델

### MVI Pattern
- **Model**: State 클래스로 UI 상태 관리
- **View**: Compose UI 컴포넌트 (각 화면에서 ViewModel 생성)
- **Intent**: 사용자 액션을 나타내는 클래스
- **Effect**: 일회성 사이드 이펙트 (네비게이션, 토스트 등)

### Feature-based 패키징
- **booklist**: 도서 검색 리스트 기능
- **favoritelist**: 즐겨찾기 리스트 기능  
- **bookdetail**: 도서 상세 기능
- **shared**: 공통 즐겨찾기 상태 공유
- **common**: 공통 컴포넌트 및 유틸리티
- **base**: 기본 클래스들

## 테스트

### 테스트 아키텍처

이 프로젝트는 **포괄적인 단위 테스트**를 통해 코드 품질을 보장합니다.

#### 테스트 대상
- **ViewModel 테스트**: MVI 패턴의 상태 관리 로직 검증
- **UseCase 호출 검증**: MockK를 통한 의존성 상호작용 테스트
- **Flow 상태 변화**: Turbine을 활용한 비동기 상태 변화 테스트
- **코루틴 처리**: Coroutines Test를 통한 비동기 작업 테스트

#### 테스트 스타일

**JUnit 4** 스타일로 작성되어 가독성과 이해도를 높였습니다:

```kotlin
@Test
fun `즐겨찾기 목록을 로드하면 모든 즐겨찾기 책이 표시되어야 한다`() = runTest {
    // Given
    val sampleBooks = listOf(/* 테스트 데이터 */)
    coEvery { getFavoriteBooksUseCase() } returns sampleBooks

    // When
    viewModel.handleIntent(FavoriteIntent.LoadFavorites)

    // Then
    viewModel.favorites.test {
        awaitItem() // 초기 상태
        val books = awaitItem() // 로드 후 상태
        assertEquals(sampleBooks, books)
        
        // UseCase 호출 검증
        coVerify { getFavoriteBooksUseCase() }
    }
}
```

#### 주요 테스트 파일

- **`BookListViewModelTest`**: 도서 검색 기능 테스트
- **`FavoriteListViewModelTest`**: 즐겨찾기 목록 기능 테스트  
- **`FavoriteStoreViewModelTest`**: 전역 즐겨찾기 상태 관리 테스트

### 테스트 실행

```bash
# 모든 단위 테스트 실행
./gradlew app:testDebugUnitTest

# 특정 테스트 클래스 실행
./gradlew app:testDebugUnitTest --tests "*BookListViewModelTest*"

# 테스트 리포트 확인
open app/build/reports/tests/testDebugUnitTest/index.html
```

### 테스트 커버리지

- **ViewModel 로직**: 상태 변화, 사용자 상호작용, 에러 처리
- **UseCase 상호작용**: 정확한 UseCase 호출 및 파라미터 검증
- **Flow 처리**: 비동기 상태 변화 및 사이드 이펙트 검증
- **예외 상황**: 빈 데이터, 네트워크 오류 등 엣지 케이스

## 라이브러리 버전

### 프로덕션
- Kotlin: 2.1.0
- Compose: 2025.08.00
- Hilt: 2.55
- Retrofit: 2.9.0
- Room: 2.7.2
- Coil: 2.7.0
- KSP: 2.1.0-1.0.29

### 테스트
- MockK: 1.13.8
- Turbine: 1.0.0
- Coroutines Test: 1.8.1
