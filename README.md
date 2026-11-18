# Movie Catalogue Jetpack

Aplikasi Android modern untuk menjelajahi katalog film resmi menggunakan TMDB API ([The Movie Database](https://api.themoviedb.org)). Proyek ini mengadopsi standar arsitektur **Clean Architecture**, **MVVM**, dan teknologi Android modern terkini dengan antarmuka bertema sinematik *Dark Crimson*.

---

## 📸 Tampilan Antarmuka (Screenshots)

| Desain Konsep & Mockup | Home & Carousel Slider | Detail Movie & Trailer |
| :---: | :---: | :---: |
| <img src="docs/screenshots/ui_concept_design.jpg" width="260" alt="Concept Design"/> | <img src="docs/screenshots/home_carousel.png" width="260" alt="Home Carousel"/> | <img src="docs/screenshots/movie_detail.jpg" width="260" alt="Movie Detail"/> |

| Pemutar Trailer Resmi (In-App) | Ulasan Pengguna (Expand/Collapse & Rating) |
| :---: | :---: |
| <img src="docs/screenshots/movie_trailer.jpg" width="260" alt="In-App YouTube Trailer"/> | <img src="docs/screenshots/movie_reviews.png" width="260" alt="User Reviews Expand Collapse"/> |

---

## 🚀 Fitur Utama

### 1. 🏠 Home Screen Sinematik
- **Pencarian Film Instan (Search Bar)**: Mencari judul film secara langsung dengan debounce dan tampilan hasil real-time.
- **5-Star Rating Carousel Slider**: Menampilkan 5 film dengan rating tertinggi dalam format kartu carousel horizontal.
- **Interactive Dot / Slider Indicator**: Indikator titik (*capsule & circle*) di bawah carousel yang berpindah secara dinamis mengikuti geseran pengguna dan dapat diklik untuk navigasi langsung ke slide tertentu.
- **Horizontal Category Pills ("Suggested for You")**: Chip genre interaktif untuk memfilter film sesuai kategori favorit.
- **Daftar Film Berdasarkan Genre**: Menggunakan **Jetpack Paging 3** dengan dukungan *infinite scrolling* dan penanganan status pemuatan (*shimmer*, *loading footer*, dan *error retry*).

### 2. 🎬 Detail Film & Pemutar Trailer In-App
- **Informasi Lengkap**: Menampilkan judul, poster, backdrop berkualitas tinggi, tagline, rating bintang emas, durasi (*runtime*), tahun rilis, genre, dan sinopsis resmi.
- **Pemutar Trailer YouTube Langsung di Aplikasi**: Mengintegrasikan pemutar YouTube berbasis WebView yang telah dioptimalkan (*hardware acceleration*, *DOM storage*, dan penanganan *custom origin*) agar trailer resmi dapat diputar langsung di dalam aplikasi tanpa *blank screen*.
- **Opsi Eksternal**: Tombol *"Buka di Aplikasi YouTube"* tetap disediakan bagi pengguna yang ingin menonton melalui aplikasi YouTube.
- **Tombol "Watch Trailer" Cepat**: Melakukan *smooth-scroll* otomatis ke bagian trailer dan langsung memulai pemutaran video.

### 3. 💬 Ulasan Pengguna (Reviews) Interaktif
- **Expand & Collapse Ulasan**: Ulasan panjang dibatasi secara rapi maksimal 3 baris dengan tombol interaktif *"Selengkapnya"* dan *"Lebih sedikit"* serta animasi transisi yang halus.
- **Rating Bintang Pengulas**: Menampilkan deretan 5 bintang emas dan skor numerik (skala 1–10) dari masing-masing pemberi review sesuai data TMDB.
- **Penyimpanan Status Dinamis**: Status buka/tutup review tersimpan rapi berdasarkan ID ulasan sehingga tidak ter-reset saat pengguna menggulir daftar.

### 4. ❤️ Manajemen Film Favorit (Offline Room DB)
- **Bookmark Film**: Menyimpan dan menghapus film favorit ke database lokal secara offline menggunakan **Room Database** dan Kotlin Flow.
- **Realtime Status**: Ikon favorit di halaman detail terhubung langsung dengan status database lokal.

### 5. 🌐 Pengaturan Bahasa (Account Screen)
- **Multi-Bahasa**: Pengaturan bahasa aplikasi dalam Bahasa Indonesia dan English menggunakan standar AndroidX `AppCompatDelegate.setApplicationLocales`.

### 6. 🧭 Bottom Navigation Terpadu
- Menu navigasi bawah terintegrasi: **Home**, **Favorite**, dan **Account**.
- Bottom Navigation secara otomatis disembunyikan saat pengguna masuk ke halaman detail film untuk pengalaman menonton dan membaca yang imersif.

---

## 🏗️ Arsitektur & Pola Desain

Aplikasi ini mengimplementasikan **Clean Architecture** dan mematuhi prinsip **SOLID**:

```
com.jetpack.moviecataloguejetpack/
├── common/
│   └── Resource.kt                   # Sealed class pembungkus UI State (Loading, Success, Error)
├── data/
│   ├── mapper/
│   │   └── DataMapper.kt             # Pemetaan antara DTO, Room Entity, dan Domain Model
│   ├── repository/
│   │   ├── paging/                   # PagingSource (MovieByGenre & MovieReviews)
│   │   ├── MovieRepositoryImpl.kt    # Implementasi Repository Film, Trailer, Top Rated & Search
│   │   └── FavoriteRepositoryImpl.kt # Implementasi Repository Favorit
│   └── source/
│       ├── local/                    # Room Database, DAO, dan Entity Favorit
│       └── remote/                   # Retrofit MovieApiService & Response DTOs
├── domain/
│   ├── model/                        # Model domain murni (Genre, Movie, MovieDetail, Review, Video)
│   ├── repository/                   # Interface repository (IMovieRepository, IFavoriteRepository)
│   └── usecase/                      # Interactors / Business Logic Use Cases
├── presentation/
│   ├── common/                       # LoadingStateAdapter untuk Paging 3
│   ├── home/                         # HomeFragment, HomeViewModel, Carousel & Genre Adapters
│   ├── detail/                       # MovieDetailFragment, MovieDetailViewModel, ReviewPagedAdapter
│   ├── favorite/                     # FavoriteListFragment, FavoriteViewModel, FavoriteMovieAdapter
│   ├── account/                      # AccountFragment, AccountViewModel
│   └── MainActivity.kt               # Single Activity host NavHostFragment & Bottom Navigation
├── di/                               # Modul Koin Dependency Injection
├── utils/                            # Helper: Constants, DateFormatter, ViewExtensions
└── MovieApplication.kt               # Application entry point & inisialisasi Koin
```

### Keunggulan Arsitektur
- **Separation of Concerns (SoC)**: Pemisahan tegas antara Presenter/UI, Domain (Use Cases & Models), dan Data (Repository, Room, Retrofit).
- **Single Activity Pattern**: Menggunakan Jetpack Navigation Component dengan graf navigasi terpusat (`nav_graph.xml`).
- **View Binding**: Type-safe view reference tanpa overhead `findViewById` atau kerentanan `synthetic`.
- **Dependency Injection**: Menggunakan **Koin** yang ringan dan modular.
- **Asynchronous & Reaktif**: Berbasis **Kotlin Coroutines** dan **StateFlow**.

---

## 🛠️ Tech Stack & Library

| Komponen | Versi | Deskripsi |
| :--- | :--- | :--- |
| **Gradle** | `8.14.7` | Build system |
| **Android Gradle Plugin (AGP)** | `8.7.3` | Tooling build Android |
| **Kotlin** | `2.0.21` | Bahasa pemrograman utama |
| **Java Toolchain** | `Java 17 (LTS)` | Runtime kompatibilitas AGP 8.x |
| **Compiler Plugin** | `KSP 2.0.21-1.0.28` | Kotlin Symbol Processing untuk Room & Glide |
| **Dependency Injection** | `Koin 3.5.6` | DI container modular |
| **Paging** | `Jetpack Paging 3 (3.3.5)` | Infinite scrolling dan load state management |
| **Local Database** | `Room 2.6.1` | Penyimpanan lokal film favorit berbasis Flow |
| **Networking** | `Retrofit 2.11.0 + OkHttp 4.12.0` | Komunikasi REST API dengan TMDB |
| **In-App Video Player** | `android-youtube-player 12.1.1` | Pemutar YouTube berbasis WebView teroptimasi |
| **Image Loading** | `Glide 4.16.0` | Pemuatan gambar poster & backdrop |
| **Shimmer Effect** | `Shimmer 0.5.0` | Efek loading skeleton yang elegan |
| **Navigation** | `Jetpack Navigation 2.8.5` | Manajemen alur fragmen & transisi |

---

## 🧪 Pengujian Unit (Unit Testing)

Seluruh komponen ViewModel, Use Cases, dan Repository telah diuji menggunakan **MockK**, **Coroutines Test**, dan **JUnit 4**:

- **`HomeViewModelTest`**: Menguji inisialisasi data carousel, pemuatan genre, dan fungsi pencarian film.
- **`MovieUseCasesTest`**: Menguji use cases baru (`GetTopRatedMoviesUseCase`, `SearchMoviesUseCase`).
- **`MovieDetailViewModelTest`**: Menguji pemuatan detail film, pemfilteran video trailer, dan status favorit.
- **`GenreViewModelTest`**: Menguji emisi sukses dan error pada pemuatan daftar genre.
- **`FavoriteViewModelTest`**: Menguji emisi data film favorit dan skenario daftar kosong.
- **`MovieRepositoryImplTest`**: Menguji pemetaan DTO ke Domain Entity dan isolasi jaringan.

### Menjalankan Unit Test
```bash
./gradlew testDebugUnitTest
```

### Melakukan Build APK Debug
```bash
./gradlew assembleDebug
```
Berkas APK yang dihasilkan akan berada di: `app/build/outputs/apk/debug/app-debug.apk`.

---

## 🔑 Konfigurasi API TMDB

API Key TMDB dikonfigurasi melalui `buildConfigField` pada [app/build.gradle](file:///C:/Users/EKO/StudioProjects/Movie-Catalogue-Jetpack-1/app/build.gradle):
- **Base URL API**: `https://api.themoviedb.org/3/`
- **Base URL Image**: `https://image.tmdb.org/t/p/`
- Nilai API key diakses di seluruh aplikasi melalui [Constants.kt](file:///C:/Users/EKO/StudioProjects/Movie-Catalogue-Jetpack-1/app/src/main/java/com/jetpack/moviecataloguejetpack/utils/Constants.kt).
