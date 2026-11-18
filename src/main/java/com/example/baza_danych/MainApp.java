package com.example.baza_danych;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class MainApp extends Application {

    private final TableView<Uzytkownik> tabela = new TableView<>();
    private final ObservableList<Uzytkownik> daneTabeli = FXCollections.observableArrayList();
    private final Label statusLabel = new Label("Łączenie z bazą...");
    private final Label licznikLabel = new Label("Rekordy: 0");

    @Override
    public void start(Stage stage) {
        konfigurujTabele();

        // 1. Górny nagłówek
        Label tytul = new Label("Baza Danych Użytkowników");
        tytul.getStyleClass().add("title-label");

        Label podtytul = new Label("PostgreSQL • Podgląd i dodawanie rekordów");
        podtytul.getStyleClass().add("subtitle-label");

        VBox tytulyBox = new VBox(3, tytul, podtytul);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        licznikLabel.getStyleClass().add("counter-badge");

        HBox naglowek = new HBox(15, tytulyBox, spacer, licznikLabel);
        naglowek.setAlignment(Pos.CENTER_LEFT);
        naglowek.getStyleClass().add("header-box");

        // 2. Środkowa karta z tabelą
        VBox kartaTabeli = new VBox(tabela);
        VBox.setVgrow(tabela, Priority.ALWAYS);
        kartaTabeli.getStyleClass().add("card");

        // 3. Dolna karta z formularzem i statusem
        TextField poleImie = new TextField();
        poleImie.setPromptText("Imię i nazwisko");

        TextField poleEmail = new TextField();
        poleEmail.setPromptText("Adres e-mail");

        Button btnDodaj = new Button("+ Dodaj");
        btnDodaj.getStyleClass().add("btn-primary");
        btnDodaj.setDefaultButton(true); // Pozwala zatwierdzić klawiszem Enter
        btnDodaj.setOnAction(e -> {
            String imie = poleImie.getText().trim();
            String email = poleEmail.getText().trim();
            if (imie.isEmpty() || email.isEmpty()) {
                ustawStatus("Uzupełnij imię oraz adres e-mail.", true);
                return;
            }
            try {
                DatabaseManager.dodajUzytkownika(imie, email);
                poleImie.clear();
                poleEmail.clear();
                poleImie.requestFocus();
                odswiezDane();
                ustawStatus("Dodano użytkownika: " + imie, false);
            } catch (SQLException ex) {
                ustawStatus("Błąd zapisu: " + ex.getMessage(), true);
            }
        });

        Button btnOdswiez = new Button("Odśwież");
        btnOdswiez.getStyleClass().add("btn-secondary");
        btnOdswiez.setOnAction(e -> odswiezDane());

        HBox pasekAkcji = new HBox(10, poleImie, poleEmail, btnDodaj, btnOdswiez);
        pasekAkcji.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(poleImie, Priority.ALWAYS);
        HBox.setHgrow(poleEmail, Priority.ALWAYS);

        statusLabel.setMaxWidth(Double.MAX_VALUE);

        VBox dolnyPanel = new VBox(12, pasekAkcji, statusLabel);
        dolnyPanel.getStyleClass().add("card");

        // Główny układ okna
        VBox root = new VBox(14, naglowek, kartaTabeli, dolnyPanel);
        root.setPadding(new Insets(18));
        VBox.setVgrow(kartaTabeli, Priority.ALWAYS);

        try {
            DatabaseManager.inicjalizujBaze();
            odswiezDane();
        } catch (SQLException ex) {
            ustawStatus("Błąd połączenia z bazą: " + ex.getMessage(), true);
        }

        Scene scene = new Scene(root, 720, 500);

        URL cssUrl = getClass().getResource("style.css");
        if (cssUrl == null) {
            cssUrl = getClass().getResource("/style.css");
        }
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("JavaFX – Podgląd Bazy Danych");
        stage.setScene(scene);
        stage.show();
    }

    private void konfigurujTabele() {
        TableColumn<Uzytkownik, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
        colId.setMinWidth(60);
        colId.setMaxWidth(80);

        TableColumn<Uzytkownik, String> colImie = new TableColumn<>("Imię i nazwisko");
        colImie.setCellValueFactory(cell -> cell.getValue().imieProperty());

        TableColumn<Uzytkownik, String> colEmail = new TableColumn<>("Adres e-mail");
        colEmail.setCellValueFactory(cell -> cell.getValue().emailProperty());

        tabela.getColumns().addAll(colId, colImie, colEmail);
        tabela.setItems(daneTabeli);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabela.setPlaceholder(new Label("Brak rekordów w bazie danych"));
    }

    private void odswiezDane() {
        try {
            List<Uzytkownik> rekordy = DatabaseManager.pobierzUzytkownikow();
            daneTabeli.setAll(rekordy);
            licznikLabel.setText("Rekordy: " + rekordy.size());
            ustawStatus("Połączono z serwerem PostgreSQL • Dane aktualne", false);
        } catch (SQLException ex) {
            ustawStatus("Błąd odczytu danych: " + ex.getMessage(), true);
        }
    }

    private void ustawStatus(String tekst, boolean blad) {
        statusLabel.setText(tekst);
        statusLabel.getStyleClass().removeAll("status-ok", "status-err");
        statusLabel.getStyleClass().add(blad ? "status-err" : "status-ok");
    }

    public static void main(String[] args) {
        launch(args);
    }
}