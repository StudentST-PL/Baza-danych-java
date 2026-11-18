module com.example.baza_danych {
    requires javafx.controls;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens com.example.baza_danych to javafx.base;
    exports com.example.baza_danych;
}