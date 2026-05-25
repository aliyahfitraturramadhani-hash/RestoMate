# RestoMate: Restaurant Management System

---

## Deskripsi Project

Aplikasi ini adalah sistem manajemen restoran berbasis desktop menggunakan JavaFX dan SQLite.
**PENTING: Aplikasi WAJIB dibangun menggunakan 100% Programmatic UI (seperti VBox, HBox, GridPane). DILARANG KERAS menggunakan FXML.**

Aplikasi dibuat menggunakan:
- Java 21
- JavaFX (Programmatic UI)
- Gradle
- SQLite
- VSCode

Tujuan aplikasi:
- Membantu pegawai restoran mengelola transaksi kasir secara efisien.
- Mengelola daftar menu makanan dan minuman (CRUD) dengan variasi karakteristik yang jelas.
- Mengatur reservasi meja pelanggan dengan sinkronisasi visual.
- Menyimpan riwayat transaksi dan menyajikan laporan pendapatan harian yang aman.

Tema UI:
- Modern, minimalis, dan responsif.
- Warna dominan: Putih (`#FFFFFF`), biru muda (`#E3F2FD` / `#2196F3`), dan abu-abu (`#F5F5F5`).
- Menggunakan *card layout* untuk kebersihan visual daftar menu.
- Menggunakan animasi ringan (efek *hover* pada tombol dan transisi *fade* saat perpindahan halaman).

---

## Penerapan 4 Pilar OOP (Wajib Diimplementasikan)

- **Abstraction & Encapsulation:** 
  Buat kelas abstrak `MenuRestoran` dengan atribut private: `id`, `nama`, `harga`, dan `stok`. Atribut diisolasi secara ketat dan hanya dapat diakses melalui *getter/setter* yang divalidasi logika (misal: harga dan stok tidak boleh bernilai negatif). Menyediakan metode abstrak `public abstract void tampilkanDetail()`.
- **Inheritance & Polymorphism:** 
  - Buat kelas `Makanan` (atribut spesifik tambahan: `tingkatPedas`) yang `extends MenuRestoran`.
  - Buat kelas `Minuman` (atribut spesifik tambahan: `isDingin`) yang `extends MenuRestoran`.
  - Kedua anak kelas wajib melakukan *override* pada metode `tampilkanDetail()` untuk mencetak karakteristik unik masing-masing (Polymorphism). Saat program dijalankan, data gabungan dari database akan ditampung dalam satu `ObservableList<MenuRestoran>`, namun akan berperilaku spesifik sesuai objek aslinya saat metode tersebut dipanggil.

---

## Alur Kerja & Struktur Halaman

### 1. Halaman Autentikasi (Login Page)
- **Alur & Fungsi:** Gerbang masuk hak akses pegawai ke sistem.
- **Mekanisme Teknis:**
  1. Pegawai menginputkan data ke `TextField` Username dan `PasswordField` Password.
  2. Aplikasi melakukan query ke tabel `users` dengan klausa `WHERE username = ? AND password = ?`.
  3. Jika data cocok, aplikasi berpindah ke Dashboard Utama secara *programmatic* dengan mengganti komponen root pada Scene (`stage.getScene().setRoot(dashboardView)`). Jika gagal, memunculkan pesan error via objek `Alert`.

### 2. Dashboard Utama (Navigation Center)
- **Alur & Fungsi:** Menyediakan strukur navigasi antar-halaman yang modular menggunakan struktur *sidebar*.
- **Mekanisme Teknis:**
  - Sisi kiri (`VBox`) berperan sebagai *Sidebar* berisi tombol menu: Kasir, Kelola Menu, Reservasi, Laporan, dan Logout.
  - Sisi kanan (`StackPane` atau `BorderPane` tengah) berperan sebagai kontainer dinamis. Konten halaman akan berganti secara halus sesuai tombol sidebar yang diklik tanpa membuka *window/stage* baru.

### 3. Menu Kasir (Point of Sale)
- **Alur & Fungsi:** Memproses transaksi belanja pelanggan dari pemilihan menu hingga pencetakan struk.
- **Mekanisme Teknis:**
  - Menampilkan daftar menu dalam *Card Layout* (`GridPane` di dalam `ScrollPane`). Karakteristik unik seperti level pedas atau status suhu (Es) ditampilkan langsung pada kartu menu.
  - Untuk efisiensi stok dan kemudahan pengelolaan (CRUD standar), variasi menu yang memiliki level pedas atau suhu berbeda dicatat sebagai **item menu mandiri yang terpisah di database** (contoh: "Ayam Geprek Lv 1", "Ayam Geprek Lv 3").
  - Kasir memilih menu ➜ Item masuk ke `TableView` (Keranjang Belanja) ➜ Aplikasi otomatis mengkalkulasi Total Harga ➜ Kasir memilih metode pembayaran (Cash / QRIS).
  - Saat tombol "Bayar" diklik, sistem menjalankan urutan transaksi: menyimpan data ke tabel `transactions` untuk mendapatkan `transaction_id` ➜ menyimpan detail item ke tabel `transaction_items` ➜ mengeksekusi query `UPDATE` untuk mengurangi jumlah `stok` di tabel `menus`.

### 4. Kelola Menu (Catat Menu - CRUD)
- **Alur & Fungsi:** Manajemen inventaris makanan dan minuman di restoran.
- **Mekanisme Teknis:**
  - Menampilkan visual seluruh menu menggunakan `TableView`.
  - Terdapat form input data (`GridPane`). Komponen form bersifat dinamis: Jika `ComboBox` kategori memilih "MAKANAN", maka field input untuk `tingkat_pedas` akan muncul/aktif. Jika memilih "MINUMAN", field tingkat pedas disembunyikan dan berganti menjadi `CheckBox` "Dingin/Es".

### 5. Catat Reservasi (Real-Time Booking)
- **Alur & Fungsi:** Pencatatan pesanan meja pelanggan serta pemantauan ketersediaan meja secara *real-time*.
- **Mekanisme Teknis (Multithreading):**
  - Menyediakan form input untuk Nama Pelanggan, Nomor Meja, Waktu (`LocalDateTime`), dan Status.
  - **Penerapan Thread:** Aplikasi menjalankan *Background Thread* (`javafx.concurrent.Task`) yang melakukan query berkala (misal tiap 5 detik) ke tabel `reservations` untuk memeriksa status ketersediaan meja.
  - Jika ada perubahan status data, *Background Thread* akan mengirim instruksi pembaruan warna layout visual meja ke UI Thread utama melalui blok kode `Platform.runLater()` guna menghindari kegagalan sistem (`NotOnFXThreadException`).

### 6. Laporan Pendapatan (End of Day Summary)
- **Alur & Fungsi:** Menyajikan rekapitulasi finansial pada hari berjalan tanpa merusak data historis database.
- **Mekanisme Teknis:**
  - Menampilkan akumulasi omset dan jumlah item terjual khusus untuk tanggal hari ini menggunakan filter fungsi `DATE(created_at) = CURRENT_DATE`.
  - Tombol **Cetak Laporan**: Mensimulasikan pembuatan file teks (`.txt`) ekspor laporan harian.
  - Tombol **Tutup Kasir (Reset)**: Berfungsi untuk menandai akhir *shift* kerja hari itu. Fitur ini **DILARANG** menghapus data database (`DELETE`), melainkan hanya mengosongkan akumulasi tampilan angka di UI kembali menjadi nol dan mencatat log waktu tutup toko ke dalam sistem.

---

## Database SQLite

**Nama Database:** `restaurant.db`

### Struktur Tabel (Skema DDL)

```sql
-- 1. Tabel Pengguna / Pegawai
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT, 
    username TEXT NOT NULL UNIQUE, 
    password TEXT NOT NULL
);

-- 2. Tabel Menu (Sinkron dengan Desain OOP Makanan & Minuman)
CREATE TABLE menus (
    id INTEGER PRIMARY KEY AUTOINCREMENT, 
    nama TEXT NOT NULL, 
    harga REAL NOT NULL, 
    kategori TEXT NOT NULL,       -- Nilai: 'MAKANAN' atau 'MINUMAN'
    stok INTEGER DEFAULT 0, 
    gambar TEXT,                  -- Menyimpan path atau URL gambar menu
    tingkat_pedas TEXT,           -- Khusus kategori MAKANAN (kosong/NULL jika minuman)
    is_dingin INTEGER DEFAULT 0   -- Khusus kategori MINUMAN. 1 = Dingin, 0 = Panas (kosong/NULL jika makanan)
);

-- 3. Tabel Transaksi Utama
CREATE TABLE transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT, 
    total REAL NOT NULL, 
    metode_pembayaran TEXT NOT NULL, -- Nilai: 'CASH' atau 'QRIS'
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 4. Tabel Detail Item Transaksi (Relasi Many-to-Many antara Transaksi dan Menu)
CREATE TABLE transaction_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT, 
    transaction_id INTEGER, 
    menu_id INTEGER, 
    qty INTEGER NOT NULL, 
    subtotal REAL NOT NULL,
    FOREIGN KEY(transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
    FOREIGN KEY(menu_id) REFERENCES menus(id)
);

-- 5. Tabel Reservasi Meja
CREATE TABLE reservations (
    id INTEGER PRIMARY KEY AUTOINCREMENT, 
    nama_pelanggan TEXT NOT NULL, 
    nomor_meja INTEGER NOT NULL, 
    waktu_reservasi TEXT NOT NULL,  -- Format ISO: YYYY-MM-DD HH:MM:SS
    status TEXT DEFAULT 'AKTIF'     -- Nilai: 'AKTIF', 'SELESAI', 'BATAL'
);
```

---

## Tambahan Arsitektur & Keamanan (Update)

1. **Keamanan Autentikasi**: Password dalam tabel `users` tidak boleh disimpan dalam plain text. Wajib menggunakan mekanisme *hashing* (seperti SHA-256) untuk menyimpan dan memverifikasi password. Aplikasi akan otomatis membuat *default user* (`admin` / `admin`) pada saat inisialisasi awal.
2. **Manajemen Koneksi & Concurrency**: URL koneksi SQLite menggunakan parameter `busy_timeout=3000` (`jdbc:sqlite:restaurant.db?busy_timeout=3000`) untuk mencegah *Database Locked* akibat *multithreading* pada fitur reservasi. Selalu gunakan blok *Try-With-Resources* untuk `Connection`, `PreparedStatement`, dan `ResultSet`.
3. **Pola Arsitektur (MVC)**: Meskipun tanpa FXML, struktur kode harus dipisah menjadi:
   - `models/`: Mendefinisikan struktur entitas data.
   - `dao/`: Mengisolasi semua operasi query database (Data Access Object).
   - `views/`: Kelas Java murni yang mengembalikan komponen UI JavaFX (`VBox`, `BorderPane`, dll).
   - `controllers/`: Menghubungkan *View* dengan *DAO* serta memuat logika aplikasi.
   - `utils/`: Kelas pembantu seperti konfigurasi koneksi database.
4. **Manajemen Gambar**: Gambar tidak disimpan dalam *absolute path*, melainkan diletakkan pada folder *resources* lokal aplikasi (`src/main/resources/images/`). Database cukup menyimpan nama filenya saja (misal: `kopi.png`).
5. **Penutupan Shift / Tutup Kasir**: Operasi ini murni mereset data kumulatif yang sedang ditampilkan di layar UI ke angka nol, tanpa menjalankan perintah `DELETE` pada database, menjaga integritas history transaksi.