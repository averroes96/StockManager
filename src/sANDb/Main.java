/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sANDb;

import Include.Init;
import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author med
 */
public class Main extends Application implements Init {
    
    private double xOffset = 0;
    private double yOffset = 0;    
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Runtime.getRuntime().exec("C:\\xampp\\mysql\\bin\\mysqld.exe");
        
        File directory = new File(UPLOADED_FILE_PATH);
        
        if(!directory.exists()){
            directory.mkdir();
        }
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Layout/custom.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("Layout/buttons.css").toExternalForm());          
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        root.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });        
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
