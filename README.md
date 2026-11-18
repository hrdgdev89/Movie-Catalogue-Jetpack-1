# Movie Catalogue Jetpack

Aplikasi Android modern untuk menjelajahi katalog film resmi menggunakan TMDB API ([The Movie Database](https://api.themoviedb.org)). Proyek ini telah diperbarui dan dioptimalkan dari basis kode lama dengan mengadopsi standar arsitektur dan teknologi Android modern terkini.

---

## 🚀 Fitur Utama

Sesuai dengan spesifikasi dan user stories:

1. **Daftar Genre Resmi Film**: Menampilkan daftar genre film resmi langsung dari endpoint TMDB (`/genre/movie/list`).
2. **Discover Movies Berdasarkan Genre**: Menampilkan daftar film berdasarkan genre yang dipilih (`/discover/movie?with_genres={id}`) dengan dukungan *endless scrolling*.
3. **Detail Informasi Film**: Menampilkan informasi lengkap film seperti judul, rating, poster, backdrop, runtime, genre chips, tagline, dan sinopsis resmi (`/movie/{id}`).
4. **Trailer Resmi YouTube**: Terintegrasi langsung dengan pemutar YouTube berbasis WebView (`android-youtube-player`) untuk memutar trailer/teaser resmi tanpa bergantung pada aplikasi YouTube eksternal (`/movie/{id}/videos`).
5. **Ulasan Pengguna (Reviews)**: Menampilkan review pengguna dengan *endless scrolling* langsung di dalam layar detail film (`/movie/{id}/reviews`).
6. **Endless Scrolling / Paginasi Modern**: Menggunakan **Jetpack Paging 3** dengan penanganan loading state serta error footer yang mendukung aksi *Retry*.
7. **Penyimpanan Lokal (Fitur Favorit)**: Menyimpan dan menandai film favorit pengguna secara offline menggunakan **Room Database** (Flow & Coroutines), dilengkapi dengan:
   - Layar daftar film favorit.
   - Layar detail khusus film favorit.
   - Tombol toggle Bookmark/Favorit di Toolbar dengan pembaruan status realtime.
8. **Positive & Negative Cases**:
   - Penanganan status UI yang komprehensif: Loading Shimmer, Success, Empty State (jika genre/review/trailer kosong), dan Error State (koneksi terputus dengan tombol Coba Lagi).
   - Pengujian Unit Test komprehensif (MockK & Coroutines Test).

---

## 🏗️ Arsitektur & Pola Desain

Aplikasi ini dirancang ulang sepenuhnya menggunakan **Clean Architecture** dan mematuhi prinsip **SOLID**:

```
com.jetpack.moviecataloguejetpack/
├── common/
│   └── Resource.kt                   # Sealed class untuk membungkus State (Loading, Success, Error)
├── data/
│   ├── mapper/
│   │   └── DataMapper.kt             # Pemetaan antara DTO (Response), Entity (Room), dan Domain Model
│   ├── repository/
│   │   ├── paging/                   # PagingSource untuk Paging 3 (MovieByGenre & MovieReviews)
│   │   ├── MovieRepositoryImpl.kt    # Implementasi Repository Movie
│   │   └── FavoriteRepositoryImpl.kt # Implementasi Repository Favorit
│   └── source/
│       ├── local/                    # Room Database, DAO, dan Entity
│       └── remote/                   # Retrofit API Service & Data Transfer Objects (DTO)
├── domain/
│   ├── model/                        # Model domain murni (Genre, Movie, MovieDetail, Review, Video)
│   ├── repository/                   # Kontrak antarmuka repository (IMovieRepository, IFavoriteRepository)
│   └── usecase/                      # Interactors / Business Logic Use Cases
├── presentation/
│   ├── common/                       # LoadingStateAdapter untuk Paging 3
│   ├── detail/                       # MovieDetailFragment, MovieDetailViewModel, ReviewPagedAdapter
│   ├── favorite/                     # FavoriteListFragment, FavoriteDetailFragment, FavoriteViewModel, FavoriteMovieAdapter
│   ├── genre/                        # GenreListFragment, GenreViewModel, GenreAdapter
│   ├── movielist/                    # MovieListByGenreFragment, MovieListViewModel, MoviePagedAdapter
│   └── MainActivity.kt               # Single Activity host NavHostFragment
├── di/                               # Modul-modul Koin Dependency Injection
├── utils/                            # Utility generic: Constants, DateFormatter, ViewExtensions
└── MovieApplication.kt               # Entry point inisialisasi Koin DI
```

### Karakteristik Arsitektur
- **Separation of Concerns (SoC)**: Pemisahan tegas antara UI layer, domain business logic layer, dan data/network/storage layer.
- **Single Activity Pattern**: Satu activity utama (`MainActivity`) sebagai host navigasi dengan Jetpack Navigation Component (`nav_graph.xml`).
- **View Binding**: Menggantikan `kotlinx.android.synthetic` yang sudah deprecated untuk manipulasi XML layout yang aman dan type-safe.
- **Dependency Injection**: Menggunakan **Koin** untuk injeksi dependensi yang ringan dan modular (`networkModule`, `databaseModule`, `repositoryModule`, `useCaseModule`, `viewModelModule`).
- **Asynchronous & Reaktif**: Sepenuhnya menggunakan **Kotlin Coroutines** dan **Flow** (`StateFlow` & `collectLatest`).

---

## 🛠️ Tech Stack & Pembaruan Dependensi

Proyek ini telah diperbarui ke standar ekosistem Android terbaru:

| Komponen | Versi Lama (2019) | Versi Baru (Modern) | Catatan |
| :--- | :--- | :--- | :--- |
| **Gradle** | 5.4.1 | **8.14.7** | Pembaruan wrapper |
| **Android Gradle Plugin (AGP)** | 3.5.1 | **8.7.3** | Build tooling modern |
| **Kotlin** | 1.3.50 | **2.0.21** | Kotlin 2.0 language features |
| **Java / JVM Toolchain** | Java 8 / 11 | **Java 17 (LTS)** | Kompatibilitas AGP 8.x |
| **Compiler Plugin** | Kapt (Legacy) | **KSP 2.0.21-1.0.28** | Pengganti Kapt yang lebih cepat |
| **Dependency Injection** | Manual Instantiation | **Koin 3.5.6** | Dependency Injection modular |
| **Asynchronous** | RxJava 2 / Call | **Coroutines & Flow** | Native Kotlin concurrency |
| **Paging** | Paging 2 | **Jetpack Paging 3 (3.3.5)** | Endless scrolling otomatis |
| **Local Storage** | Room 2.2.2 | **Room 2.6.1 (KSP & Flow)** | Database lokal modern |
| **Networking** | Retrofit 2.6.0 | **Retrofit 2.11.0 + OkHttp 4.12.0** | HTTP client modern |
| **YouTube Player** | - | **android-youtube-player 12.1.1** | Pemutar YouTube berbasis WebView |
| **Image Loader** | Glide 4.10.0 | **Glide 4.16.0** | Image loading dengan transition |
| **View System** | Synthetic Binding | **View Binding** | Type-safe XML binding |

---

## 🧪 Pengujian Unit (Unit Testing)

Unit test dibuat secara terstruktur untuk menguji skenario positif dan negatif:
- **`GenreViewModelTest`**: Menguji emisi sukses daftar genre (`Resource.Success`) serta penanganan error jaringan (`Resource.Error`).
- **`MovieDetailViewModelTest`**: Menguji pemuatan detail film, pemfilteran video trailer YouTube, skenario 404/kegagalan server, dan toggle bookmark favorit.
- **`FavoriteViewModelTest`**: Menguji emisi daftar film favorit, kondisi daftar kosong (*empty state*), dan pemanggilan usecase penghapusan.
- **`MovieRepositoryImplTest`**: Menguji transformasi DTO TMDB ke Domain Entity, isolasi error I/O, serta pemfilteran jenis video trailer.

### Menjalankan Unit Test
```bash
./gradlew test
```

### Melakukan Build APK Debug
```bash
./gradlew assembleDebug
```

---

## 🔑 Konfigurasi API TMDB

API Key TMDB dikonfigurasi secara aman melalui `buildConfigField` pada [app/build.gradle](file:///C:/Users/EKO/StudioProjects/Movie-Catalogue-Jetpack-1/app/build.gradle):
- **Base URL**: `https://api.themoviedb.org/3/`
- **Image Base URL**: `https://image.tmdb.org/t/p/`
- Nilai API key dapat diakses di kode melalui konstanta [Constants.kt](file:///C:/Users/EKO/StudioProjects/Movie-Catalogue-Jetpack-1/app/src/main/java/com/jetpack/moviecataloguejetpack/utils/Constants.kt).
