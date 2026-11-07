module org.example.studytracker {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.studytracker to javafx.fxml;
    exports org.example.studytracker;
}
