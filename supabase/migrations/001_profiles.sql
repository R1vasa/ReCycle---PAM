-- 001_profiles.sql  (Pemilik: Bersama)
-- Tabel profiles, trigger pembuat profil saat register, fungsi is_pengelola(), dan RLS.
-- Jalankan di Supabase Dashboard > SQL Editor. Aman dijalankan ulang.
-- Email & password TIDAK disimpan di sini: keduanya ada di auth.users (dikelola Supabase Auth).

-- 1. Tabel ------------------------------------------------------------------
create table if not exists public.profiles (
  id          uuid primary key references auth.users (id) on delete cascade,
  full_name   text not null default '',
  email       text,
  phone       text,
  address     text,
  avatar_url  text,
  role        text not null default 'warga',
  created_at  timestamptz not null default now()
);

-- Untuk tabel profiles yang sudah terlanjur dibuat tanpa kolom ini.
alter table public.profiles add column if not exists email text;
alter table public.profiles add column if not exists role text not null default 'warga';
alter table public.profiles add column if not exists created_at timestamptz not null default now();

-- Kolom role yang sudah ada sebelumnya (nullable, tanpa default) tidak diubah oleh
-- "add column if not exists", jadi default & not null dipasang terpisah.
alter table public.profiles alter column role set default 'warga';
update public.profiles set role = 'warga' where role is null;
alter table public.profiles alter column role set not null;

alter table public.profiles drop constraint if exists profiles_role_check;
alter table public.profiles add constraint profiles_role_check check (role in ('warga', 'pengelola'));

-- 2. Trigger: buat baris profiles otomatis saat register --------------------
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  insert into public.profiles (id, full_name, email)
  values (
    new.id,
    coalesce(new.raw_user_meta_data ->> 'full_name', ''),
    new.email
  )
  on conflict (id) do nothing;
  return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();

-- 3. Fungsi bantu peran (dipakai RLS tabel lain) -----------------------------
create or replace function public.is_pengelola()
returns boolean
language sql
stable
security definer
set search_path = ''
as $$
  select exists (
    select 1 from public.profiles
    where id = auth.uid() and role = 'pengelola'
  );
$$;

-- 4. Row Level Security -----------------------------------------------------
alter table public.profiles enable row level security;

drop policy if exists "profiles_select_authenticated" on public.profiles;
create policy "profiles_select_authenticated"
  on public.profiles for select
  to authenticated
  using (true);

drop policy if exists "profiles_update_own" on public.profiles;
create policy "profiles_update_own"
  on public.profiles for update
  to authenticated
  using (id = auth.uid())
  with check (id = auth.uid());

-- Tidak ada policy insert/delete: baris dibuat oleh trigger, dihapus ikut auth.users.

-- Pengguna hanya boleh mengubah kolom ini; role & email tidak bisa diubah dari aplikasi.
revoke update on public.profiles from authenticated;
grant update (full_name, phone, address, avatar_url) on public.profiles to authenticated;

-- 5. Menjadikan akun sebagai pengelola (jalankan manual, ganti emailnya) ------
-- update public.profiles set role = 'pengelola' where email = 'pengelola@contoh.com';
