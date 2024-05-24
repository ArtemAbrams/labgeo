module org.example.labgeo {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.labgeo to javafx.fxml;
    exports org.example.labgeo;
}