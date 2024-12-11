import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

class Transaksi {
    protected String noFaktur;
    protected String kodeBarang;
    protected String namaBarang;
    protected double hargaBarang;
    protected int jumlahBeli;

    public Transaksi(String noFaktur, String kodeBarang, String namaBarang, double hargaBarang, int jumlahBeli) {
        this.noFaktur = noFaktur;
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.hargaBarang = hargaBarang;
        this.jumlahBeli = jumlahBeli;
    }

    public double hitungTotal() {
        return hargaBarang * jumlahBeli;
    }
}

class TransaksiValidasi extends Transaksi {
    public TransaksiValidasi(String noFaktur, String kodeBarang, String namaBarang, double hargaBarang, int jumlahBeli) {
        super(noFaktur, kodeBarang, namaBarang, hargaBarang, jumlahBeli);
    }

    public void validasiInput() throws Exception {
        if (hargaBarang < 0) {
            throw new Exception("Harga barang tidak boleh negatif!");
        }
        if (jumlahBeli <= 0) {
            throw new Exception("Jumlah beli harus lebih besar dari 0!");
        }
    }
}

public class ProgramTransaksi {
    static final String DB_URL = "jdbc:mysql://localhost:3306/transaksi_db"; // URL database
    static final String USER = "root"; // Username
    static final String PASS = ""; // Password

    public static String getCurrentDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(new Date());
    }

    // Method untuk memeriksa apakah noFaktur sudah ada di database
    public static boolean isNoFakturExists(String noFaktur) {
        String query = "SELECT COUNT(*) FROM transaksi WHERE noFaktur = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, noFaktur);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    // Method untuk Create (menambahkan transaksi)
    public static void createTransaksi(Transaksi transaksi) {
        String query = "INSERT INTO transaksi (noFaktur, kodeBarang, namaBarang, hargaBarang, jumlahBeli) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, transaksi.noFaktur);
            pstmt.setString(2, transaksi.kodeBarang);
            pstmt.setString(3, transaksi.namaBarang);
            pstmt.setDouble(4, transaksi.hargaBarang);
            pstmt.setInt(5, transaksi.jumlahBeli);
            pstmt.executeUpdate();

            System.out.println("Transaksi berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Method untuk Read (membaca transaksi berdasarkan nomor faktur)
    public static void readTransaksi(String noFaktur) {
        String query = "SELECT * FROM transaksi WHERE noFaktur = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, noFaktur);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("No Faktur: " + rs.getString("noFaktur"));
                System.out.println("Kode Barang: " + rs.getString("kodeBarang"));
                System.out.println("Nama Barang: " + rs.getString("namaBarang"));
                System.out.println("Harga Barang: " + rs.getDouble("hargaBarang"));
                System.out.println("Jumlah Beli: " + rs.getInt("jumlahBeli"));
            } else {
                System.out.println("Transaksi dengan nomor faktur " + noFaktur + " tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Method untuk Update (memperbarui transaksi)
    public static void updateTransaksi(Transaksi transaksi) {
        String query = "UPDATE transaksi SET kodeBarang = ?, namaBarang = ?, hargaBarang = ?, jumlahBeli = ? WHERE noFaktur = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, transaksi.kodeBarang);
            pstmt.setString(2, transaksi.namaBarang);
            pstmt.setDouble(3, transaksi.hargaBarang);
            pstmt.setInt(4, transaksi.jumlahBeli);
            pstmt.setString(5, transaksi.noFaktur);
            pstmt.executeUpdate();

            System.out.println("Transaksi berhasil diperbarui!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Method untuk Delete (menghapus transaksi)
    public static void deleteTransaksi(String noFaktur) {
        String query = "DELETE FROM transaksi WHERE noFaktur = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, noFaktur);
            pstmt.executeUpdate();

            System.out.println("Transaksi berhasil dihapus!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String username = "Trici";
        String password = "101010";
        boolean loginSuccess = false;

        System.out.println("=== LOGIN MODERNMART ===");
        while (!loginSuccess) {
            System.out.print("Username: ");
            String inputUsername = scanner.nextLine();
            System.out.print("Password: ");
            String inputPassword = scanner.nextLine();

            if (inputUsername.equals(username) && inputPassword.equals(password)) {
                loginSuccess = true;
                System.out.println("Login berhasil!");
            } else {
                System.out.println("Login gagal. Silakan coba lagi.");
            }
        }

        try {
            System.out.println("\n=== Transaksi ===");
            System.out.println("Tanggal dan Waktu: " + getCurrentDateTime());

            String noFaktur;
            // Meminta input noFaktur hingga faktur yang dimasukkan tidak ada duplikat
            while (true) {
                System.out.print("Masukkan No Faktur: ");
                noFaktur = scanner.nextLine();
                
                if (isNoFakturExists(noFaktur)) {
                    System.out.println("No Faktur sudah ada. Gunakan nomor faktur yang berbeda.");
                } else {
                    break; // Keluar dari loop jika noFaktur belum ada di database
                }
            }

            System.out.print("Masukkan Kode Barang: ");
            String kodeBarang = scanner.nextLine();

            System.out.print("Masukkan Nama Barang: ");
            String namaBarang = scanner.nextLine();

            System.out.print("Masukkan Harga Barang: ");
            double hargaBarang = scanner.nextDouble();

            System.out.print("Masukkan Jumlah Beli: ");
            int jumlahBeli = scanner.nextInt();

            TransaksiValidasi transaksi = new TransaksiValidasi(noFaktur, kodeBarang, namaBarang, hargaBarang, jumlahBeli);
            transaksi.validasiInput();

            // Menambahkan transaksi ke database
            createTransaksi(transaksi);

            System.out.println("\n=== Selamat Datang Di MODERNMART ===");
            System.out.println("No Faktur       : " + transaksi.noFaktur);
            System.out.println("Kode Barang     : " + transaksi.kodeBarang);
            System.out.println("Nama Barang     : " + transaksi.namaBarang);
            System.out.println("Harga Barang    : " + transaksi.hargaBarang);
            System.out.println("Jumlah Beli     : " + transaksi.jumlahBeli);
            System.out.println("TOTAL           : " + transaksi.hitungTotal());
            System.out.println("Kasir           : Trici");
            System.out.println("=============================================");

        } catch (Exception e) {
            System.out.println("Kesalahan: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
