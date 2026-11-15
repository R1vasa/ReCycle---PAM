# Supabase

Simpan file SQL migrasi di `migrations/`, diberi nomor urut agar dijalankan berurutan:

- `001_profiles.sql` — tabel profiles, trigger handle_new_user, fungsi is_pengelola() (Bersama)
- `002_waste_categories.sql` — Anggota 3
- `003_drop_points.sql` — Anggota 2
- `004_education_tips.sql` — Anggota 4
- `005_pickup_requests.sql` — Anggota 1 (dijalankan terakhir karena punya FK ke 002 dan 003)
- `006_storage.sql` — bucket pickup-photos dan avatars beserta policy (Bersama)

Setiap file berisi CREATE TABLE, constraint, dan policy RLS tabel tersebut (PRD Bagian 5).
