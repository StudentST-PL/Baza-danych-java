package com.example.baza_danych;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://195.150.230.153:5432/2026_kinzerski_igor";
    private static final String USER = "2026_kinzerski_igor";
    private static final String PASSWORD = "40852";

    public static Connection polacz() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void inicjalizujBaze() throws SQLException {
        String sqlTabela = """
            CREATE TABLE IF NOT EXISTS uzytkownicy (
                id SERIAL PRIMARY KEY,
                imie TEXT NOT NULL,
                email TEXT NOT NULL
            );
            """;

        try (Connection conn = polacz();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlTabela);

            // Dodaj przykładowe dane, jeśli tabela jest pusta
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM uzytkownicy")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("""
                        INSERT INTO uzytkownicy (imie, email) VALUES
                        ('Anna Nowak', 'anna.nowak@example.com'),
                        ('Jan Kowalski', 'jan.kowalski@example.com'),
                        ('Piotr Wiśniewski', 'piotr.w@example.com');
                        """);
                }
            }
        }
    }

    public static List<Uzytkownik> pobierzUzytkownikow() throws SQLException {
        List<Uzytkownik> lista = new ArrayList<>();
        String sql = "SELECT id, imie, email FROM uzytkownicy ORDER BY id ASC";

        try (Connection conn = polacz();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Uzytkownik(
                        rs.getInt("id"),
                        rs.getString("imie"),
                        rs.getString("email")
                ));
            }
        }
        return lista;
    }

    public static void dodajUzytkownika(String imie, String email) throws SQLException {
        String sql = "INSERT INTO uzytkownicy (imie, email) VALUES (?, ?)";

        try (Connection conn = polacz();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, imie);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
    }
}