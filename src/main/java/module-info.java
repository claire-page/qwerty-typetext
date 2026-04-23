module typetext {

    requires java.datatransfer;
    requires java.sql;
    requires java.xml;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    exports ui to javafx.graphics, javafx.controls;
    exports io.database to java.sql;
    exports core;

}