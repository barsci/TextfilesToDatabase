package view;

import dbconnection.DBConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parsers.csvparser.CSVParser;
import parsers.xmlparser.XMLParser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class GuiFileChooser extends Application {

    private List<File> fileList;
    private final Label logLabel = new Label();
    private final Label numberOfInsertedFilesLabel = new Label();
    private final TextField textField = new TextField();
    private final Button saveButton =  new Button("Save to database");

    @Override
    public void start(final Stage stage) {
        stage.setTitle("Save textfiles to DB");

        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter textFilter =
                new FileChooser.ExtensionFilter("Text files (*.txt, *.csv, *.xml)", "*.txt","*.xml","*.csv");
        fileChooser.getExtensionFilters().add(textFilter);

        final Label infoLabel = new Label("Choose file(s) you want to store in database!");
        final Button openMultipleButton = new Button("Choose files...");
        saveButton.setDisable(true);
        textField.setDisable(true);

        openMultipleButton.setOnAction( (event) -> {
            textField.clear();
            fileList = fileChooser.showOpenMultipleDialog(stage);
            if (fileList != null) {
                textField.setText(fileList.size() + " file(s) chosen.");
                saveButton.setDisable(false);
            }
        });

        saveButton.setOnAction( (event) -> {
            textField.clear();
            logLabel.setText("");
            Task task = new Task<Void>() {
                @Override
                public Void call() {
                    passFilesToDatabase(fileList);
                    saveButton.setDisable(true);
                    return null;
                }
            };
            new Thread(task).start();
        });

        final GridPane inputGridPane = new GridPane();
        final HBox hBoxButtons = new HBox(30);
        final VBox logBox = new VBox();

        GridPane.setConstraints(infoLabel, 0, 0);
        GridPane.setConstraints(textField, 0, 1);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(infoLabel, textField);

        hBoxButtons.getChildren().addAll(saveButton, openMultipleButton);
        logBox.getChildren().addAll(logLabel,numberOfInsertedFilesLabel);

        final Pane pane = new VBox(12);
        pane.getChildren().addAll(inputGridPane,hBoxButtons,logBox);
        pane.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(pane));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void passFilesToDatabase(List<File> list) {

        DBConnection dbConnection = DBConnection.getInstance();
        try {
            dbConnection.setDatabaseConnection();
            dbConnection.setLabel(logLabel);
            for(File file: list) {
                if (file.getPath().endsWith(".xml")) {
                    XMLParser xmlParser = new XMLParser(dbConnection);
                    xmlParser.parseXML(file);
                }
                if (file.getPath().endsWith(".txt") || file.getPath().endsWith(".csv")) {
                    CSVParser csvParser = new CSVParser(dbConnection);
                    csvParser.parse(file);
                }
                Platform.runLater(() -> this.numberOfInsertedFilesLabel.setText("Inserted "+file.getName()));
            }
        } catch (SQLException | IOException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database connection error!");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                saveButton.setDisable(true);
            });
        } finally {
            try {
                dbConnection.closeConnection();
                textField.setText("Files inserted to database!");
            } catch (SQLException e ){
                System.out.println("Error closing connection");
            }
        }
        fileList = new ArrayList<>();
    }
}