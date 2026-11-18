package com.example.baza_danych;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Uzytkownik {
    private final IntegerProperty id;
    private final StringProperty imie;
    private final StringProperty email;

    public Uzytkownik(int id, String imie, String email) {
        this.id = new SimpleIntegerProperty(id);
        this.imie = new SimpleStringProperty(imie);
        this.email = new SimpleStringProperty(email);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty imieProperty() { return imie; }
    public StringProperty emailProperty() { return email; }

    public int getId() { return id.get(); }
    public String getImie() { return imie.get(); }
    public String getEmail() { return email.get(); }
}