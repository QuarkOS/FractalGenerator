module org.example.fractalgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;


    opens org.example.fractalgenerator to javafx.fxml;
    exports org.example.fractalgenerator;
}