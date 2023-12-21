module com.kursinis.prif4kursinis {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires spring.context;
    requires com.google.gson;
    requires spring.web;
    requires jbcrypt;

    opens com.kursinis.prif4kursinis to javafx.fxml;
    exports com.kursinis.prif4kursinis;
    opens com.kursinis.prif4kursinis.model to javafx.fxml, org.hibernate.orm.core;
    exports com.kursinis.prif4kursinis.model;
    opens com.kursinis.prif4kursinis.fxControllers to javafx.fxml;
    opens com.kursinis.prif4kursinis.fxControllers.tableviewparameters to javafx.base;
    exports com.kursinis.prif4kursinis.fxControllers;
    exports com.kursinis.prif4kursinis.fxControllers.windowControllers;
    opens com.kursinis.prif4kursinis.fxControllers.windowControllers to javafx.fxml;
    exports com.kursinis.prif4kursinis.fxControllers.oldControllers;
    opens com.kursinis.prif4kursinis.fxControllers.oldControllers to javafx.fxml;
    exports com.kursinis.prif4kursinis.fxControllers.regLog;
    opens com.kursinis.prif4kursinis.fxControllers.regLog to javafx.fxml;
    opens com.kursinis.prif4kursinis.fxControllers.createControllers to javafx.fxml;
    exports com.kursinis.prif4kursinis.fxControllers.userWindowControllers;
    opens com.kursinis.prif4kursinis.fxControllers.userWindowControllers to javafx.fxml;
}