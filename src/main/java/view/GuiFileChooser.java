package view;

import dbconnection.DBConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class GuiFileChooser extends Application {

    private List<File> fileList;
    private final Label logLabel = new Label();
    private final Label numberOfInsertedFilesLabel = new Label();

    @Override
    public void start(final Stage stage) {
        stage.setTitle("Save textfiles to DB");

        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter textFilter =
                new FileChooser.ExtensionFilter("Text files (*.txt, *.csv, *.xml)", "*.txt","*.xml","*.csv");
        fileChooser.getExtensionFilters().add(textFilter);

        final Label label = new Label("Choose file(s) you want to store in database!");
        final TextField textField = new TextField();
        final Button openMultipleButton = new Button("..");
        final Button saveButton = new Button("Save to database");
        saveButton.setDisable(true);
        final Button exitButton = new Button("Close app");


        openMultipleButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        textField.clear();
                        fileList = fileChooser.showOpenMultipleDialog(stage);
                        if (fileList != null) {
                            for(File file : fileList) {
                                textField.appendText(file.getName()+ "; ");
                            }
                            saveButton.setDisable(false);
                        }
                    }
                });

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textField.clear();
                logLabel.setText("");
                Task task = new Task<Void>() {
                    @Override
                    public Void call() {
                        passFilesToDatabase(fileList);
                        return null;
                    }
                };
                new Thread(task).start();
            }
        });

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });

        final GridPane inputGridPane = new GridPane();
        final HBox hBoxButtons = new HBox(10);
        final VBox logBox = new VBox();

        GridPane.setConstraints(label, 0, 0);
        GridPane.setConstraints(textField, 0, 1);
        GridPane.setConstraints(openMultipleButton, 1, 1);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(label, textField, openMultipleButton);

        hBoxButtons.getChildren().addAll(saveButton, exitButton);
        logBox.getChildren().addAll(logLabel,numberOfInsertedFilesLabel);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane,hBoxButtons,logBox);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup));
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
                    System.out.println("xml");
                } else if (file.getPath().endsWith(".txt") || file.getPath().endsWith(".csv")) {
                    CSVParser csvParser = new CSVParser(dbConnection);
                    csvParser.parse(file);
                    System.out.println("csv");
                } else {
                    System.out.println("SELECT FILE WITH PROPER EXTENSION!");
                }
                Platform.runLater(() -> this.numberOfInsertedFilesLabel.setText("Inserted "+file.getName()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                dbConnection.closeConnection();
            } catch (SQLException e ){
                System.out.println("Error closing connection");
            }
        }
        fileList = new ArrayList<>();
    }
}