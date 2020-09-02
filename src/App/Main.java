/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App;

import App.Preloader.MyPreLoader;
import Include.Init;
import com.sun.javafx.application.LauncherImpl;
import java.io.File;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author med
 */
public class Main extends Application implements Init {
    
    private double xOffset = 0;
    private double yOffset = 0;

    private static final int COUNT_LIMIT = 100;

    @Override
    public void init() {
        // load all (database start, check update for application, ...and more)
        for(int i = 0; i < COUNT_LIMIT; i++) {
            double progress = (double)i/100;
            LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(progress));
            try {Thread.sleep(20);} catch(InterruptedException e) {e.printStackTrace();}
        }
    }    
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Runtime.getRuntime().exec("C:\\xampp\\mysql\\bin\\mysqld.exe");
        
        File directory = new File(UPLOADED_FILE_PATH);
        
        if(!directory.exists()){
            directory.mkdir();
        }
        Parent root = FXMLLoader.load(getClass().getResource(FXMLS_PATH + "Login.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());          
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setMinHeight(350);
        stage.setMinWidth(450);        
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
          LauncherImpl.launchApplication(Main.class, MyPreLoader.class, args);
    }
    
}
