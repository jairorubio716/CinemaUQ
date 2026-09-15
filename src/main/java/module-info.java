module co.edu.uniquindio.cinemauq {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.uniquindio.cinemauq to javafx.fxml;
    exports co.edu.uniquindio.cinemauq;
}