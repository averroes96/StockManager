/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Include;

import animatefx.animation.AnimationFX;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 *
 * @author med
 */
public class Common implements Init {
    
    
    public static String[] getAppLang() throws SQLException{
        
        return getSettingValue("app_language").split("_");
        
    }

    public static String getSettingValue(String setting_name) throws SQLException{
        
            ResultSet rs = Common.getAllFrom("*", "settings", " WHERE setting_name = '" + setting_name + "'", "", "");
            
            while(rs.next()){ 
                return rs.getString("setting_value");         
            }
        
        return null;
    }    
    
    
    public static void controlDigitField(TextField field){
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Integer.parseInt(newValue);
            }
            catch(NumberFormatException e){
                if(field.getText().trim().isEmpty())
                    field.setText("");
                else
                    field.setText(oldValue);
            }
        });
    }
   
    public static void initLayout(JFXDialogLayout layout, String header, String body, String icon){
                
        Image image = new Image(IMAGES_PATH + icon);
        ImageView imageView = new ImageView(image);
        Label label = new Label(header);
        label.graphicProperty().setValue(imageView);
        layout.setHeading(label);
        layout.setBody(new Text(body));        
        
    }
    

    public static Connection getConnection() throws SQLException
    {
        Connection con;

            con = DriverManager.getConnection(DB_NAME_WITH_ENCODING, USER, PASSWORD);
            return con;
    }
    
    public static String generateImagePath(File selectedFile)
    {

        java.util.Date date = new java.util.Date();
 
        SimpleDateFormat sdf = new SimpleDateFormat("Y-M-d-hh-mm-ss");
        
        String fileExtension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
 
        return UPLOADED_FILE_PATH + sdf.format(date) + fileExtension;
                
    }
    
    
    public static String saveSelectedImage(File selectedFile) throws FileNotFoundException, IOException
    {

        String createImagePath = Common.generateImagePath(selectedFile);

            FileOutputStream out;
            try (FileInputStream in = new FileInputStream(selectedFile)) {
                out = new FileOutputStream(createImagePath);
                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            }
            out.close();


        return createImagePath;
    }
    
    public static void deleteImage(String filePath)
    {
        try {
            File imageToDelete = new File(filePath);
            imageToDelete.delete();
        }
        catch(Exception e) {}
    }
    
    public static StringConverter dateFormatter()
    {
        StringConverter converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATEFORMAT);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                }
                return "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                }
                return null;
            }
        };
        return converter;
    }  
    
    public static ResultSet getAllFrom(String select, String tableName, String additions, String whereClause, String ordering) throws SQLException{
        
        Connection con = getConnection();
        String query = "SELECT " + select + " FROM " + tableName + " " + additions + " " + whereClause + " " + ordering;

        PreparedStatement st;
        ResultSet rs;
                
            st = con.prepareStatement(query);
            return rs = st.executeQuery();

    }
    
    public static void AnimateField(JFXTextField field, Label status, String formula, boolean isAnimated){
        
        if(isAnimated){
            AnimationFX animField = new Shake(field);
            animField.setResetOnFinished(true);

            field.setOnKeyPressed(Action -> {

            if (!field.getText().matches(formula)) {
                status.setVisible(true);
                field.setFocusColor(Color.RED);
                animField.play();
            }
            else{
                status.setVisible(false);
                field.setFocusColor(Color.GREEN);
            }

            });
            field.setOnKeyTyped(Action -> {

            if (!field.getText().matches(formula)) {
                status.setVisible(true);
                field.setFocusColor(Color.RED);
                animField.play();
            }
            else{
                status.setVisible(false);
                field.setFocusColor(Color.GREEN);
            }        

            });
            field.setOnKeyReleased(Action -> {

            if (!field.getText().matches(formula)) {
                status.setVisible(true);
                field.setFocusColor(Color.RED);
                animField.play();
            }        
            else{
                status.setVisible(false);
                field.setFocusColor(Color.GREEN);
            }            
            });
        }
    }
    
    public static void startStage(Parent root, int minWidth, int minHeight){
        
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        Stage stage = new Stage();
        stage.getIcons().add(new Image(Common.class.getResourceAsStream(APP_ICON)));
        stage.setScene(scene);
        stage.setMinHeight(minHeight);
        stage.setMinWidth(minWidth);
        stage.show();  
        
    }
    
     
    
    
}
