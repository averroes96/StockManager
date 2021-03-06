/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App;

import App.Preloader.MyPreLoader;
import static Include.Common.getAppLang;
import Include.Init;
import com.sun.javafx.application.LauncherImpl;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author med
 */
public class Main extends Application implements Init {
    
    private void loadView(Locale locale, Stage stage) throws IOException {

        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLES_PATH, locale);
        //Parent root = FXMLLoader.load(getClass().getResource(FXMLS_PATH + "Login.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource(FXMLS_PATH + "Login.fxml"), bundle);

        // replace the content
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        stage.setScene(scene);
        stage.setMinHeight(350);
        stage.setMinWidth(450);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream(APP_ICON)));

        stage.show();
            
    }

    @Override
    public void init() throws IOException, SQLException {
        
        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(0));
        
        //Runtime.getRuntime().exec("C:\\xampp\\mysql\\bin\\mysqld.exe");
        
        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(50));
        
        File directory = new File(UPLOADED_FILE_PATH);
        
        if(!directory.exists()){
            directory.mkdir();
        }

        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(100));
        // load all (database start, check update for application, ...and more)
    }    
    
    @Override
    public void start(Stage stage) throws Exception {
        
        init();
        
        loadView(new Locale(getAppLang()[0], getAppLang()[1]), stage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
          LauncherImpl.launchApplication(Main.class, MyPreLoader.class, args);
    }
    
    
}
