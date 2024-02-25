module labs.lab1_game {
    requires javafx.controls;
    requires javafx.fxml;

    opens labs.lab1_game to javafx.fxml;
    exports labs.lab1_game;
}
