# ReCycle

Aplikasi Android untuk pengumpulan dan penyaluran limbah. Spesifikasi lengkap: `PRD_ReCycle_v1.1.docx`.

## Struktur Folder

```
app/src/main/java/com/example/recycle/
├── MainActivity.kt
├── core/                      Bersama
│   ├── data/
│   │   ├── remote/            SupabaseProvider
│   │   └── model/             Data class tiap tabel (satu file per tabel)
│   └── util/                  UiState, ImageCompressor, LocationUtils
├── ui/
│   ├── theme/                 Warna, tipografi, tema
│   ├── navigation/            Routes, AppNavGraph, MainScreen (bottom nav)
│   └── components/            LoadingView, ErrorView
└── feature/
    ├── auth/                  Bersama  — Splash, Login, Register, AuthViewModel
    ├── profile/               Bersama  — Profil, Edit Profil, ProfileViewModel
    ├── home/                  Bersama  — Beranda (menyusun komponen A3 & A4)
    ├── pickup/                Anggota 1 — Pengajuan Jemput Limbah (pickup_requests)
    ├── droppoint/             Anggota 2 — Direktori Bank Sampah (drop_points)
    ├── catalog/               Anggota 3 — Katalog Harga Limbah (waste_categories)
    └── education/             Anggota 4 — Edukasi Pemilahan (education_tips)

supabase/migrations/           File SQL skema, RLS, dan storage
```

Setiap folder fitur berisi `XxxRepository` (akses Supabase), `XxxViewModel` (state UI),
layar-layar Compose, dan subfolder `components/` untuk composable kecil.

## Aturan Kerja Tim

- Ubah hanya file di folder fitur milikmu dan file model tabelmu di `core/data/model/`.
- Perubahan di `core/`, `ui/navigation/`, dan `feature/home/` didiskusikan dulu, karena dipakai semua anggota.
- Anggota 1 hanya membaca `WasteCategory` dan `DropPoint`; jangan ubah file milik Anggota 2 dan 3.
# ReCycle---PAM
