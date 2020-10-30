/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Include;

import Data.Product;
import Data.Sell;
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
import javafx.scene.input.MouseEvent;
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
    
    public static void minimize(MouseEvent event){
        
        ((Stage)((Label)event.getSource()).getScene().getWindow()).setIconified(true);
        
    }

    public static int getPrice(String name) throws SQLException{
        
        int targetedPrice;
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM product WHERE name = ? LIMIT 1";
            targetedPrice = 0;
            PreparedStatement st;
            ResultSet rs;
            st = con.prepareStatement(query);
            st.setString(1, name);
            rs = st.executeQuery();
            int count = 0;
            while (rs.next()) {
                
                targetedPrice = rs.getInt("sell_price");
                
            }
        }
            
            return targetedPrice;
      
        
    }
    
    public static int getQuantity(String name) throws SQLException{
        
        int targetedPrice;
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM product WHERE name = ? LIMIT 1";
            targetedPrice = 0;
            PreparedStatement st;
            ResultSet rs;
            st = con.prepareStatement(query);
            st.setString(1, name);
            rs = st.executeQuery();
            int count = 0;
            while (rs.next()) {
                
                targetedPrice = rs.getInt("prod_quantity");
                
            }
        }
            
            return targetedPrice;

    }    

    public static boolean refExist(String ref) throws SQLException{
        
        boolean found;
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM product WHERE name = ? LIMIT 1";
            found = false;
            PreparedStatement st;
            ResultSet rs;
            st = con.prepareStatement(query);
            st.setString(1, ref);
            rs = st.executeQuery();
            int count = 0;
            if(rs.next()){
                
                found = true;
            }
        }
            
            return found;
       
        
    }
    
    
    public static Product getProductByID(int ID) throws SQLException{
        
        Product product = new Product();
        int count;
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM product WHERE prod_id = ?";
            PreparedStatement st;
            ResultSet rs;
            st = con.prepareStatement(query);
            st.setInt(1, ID);
            rs = st.executeQuery();
            count = 0;
            while (rs.next()) {
                product.setAddDate(rs.getDate("add_date").toString());
                product.setName(rs.getString("name"));
                product.setProdID(rs.getInt("prod_id"));
                product.setProdQuantity(rs.getInt("prod_quantity"));
                product.setSellPrice(rs.getInt("sell_price"));
                product.setNbrBuys(rs.getInt("nbrBuys"));
                product.setNbrSells(rs.getInt("nbrSells"));
                ++count;
                
            }
        }
            if(count == 0)
                return null;
            else
                return product;
          
    }    
    
    public  Sell getSell(int sellID) throws SQLException{
        
        Sell sell;
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM sell INNER JOIN product ON sell.prod_id = product.prod_id INNER JOIN user ON user.user_id = sell.user_id WHERE sell.sell_id = ? ORDER BY sell.sell_date";
            PreparedStatement st;
            ResultSet rs;
            sell = new Sell();
            st = con.prepareStatement(query);
            st.setInt(1,sellID);
            rs = st.executeQuery();
            while (rs.next()) {

                sell.setSellID(rs.getInt("sell_id"));
                sell.setSellPrice(rs.getInt("product.sell_price"));
                sell.setSellDate(rs.getDate("sell_date").toString());
                Product product = new Product();
                product.setProdID(rs.getInt("prod_id"));
                product.setName(rs.getString("name"));               
                product.setProdQuantity(rs.getInt("prod_quantity"));
                product.setSellPrice(rs.getInt("product.sell_price"));
                product.setAddDate(rs.getDate("add_date").toString());
                product.setImageURL(rs.getString("image"));
                
                sell.setProduct(product);
                sell.setSellName(rs.getString("name"));

            }
        }
            
        return sell;
        
    }
    
    public static String getDate(int ID, String type) throws SQLException{

    try (Connection con = getConnection()) {
        String query = "SELECT date(" + type + "_date) FROM "+ type +" WHERE "+ type + "."+ type +"_id = ?";

        PreparedStatement st;
        ResultSet rs;

        st = con.prepareStatement(query);
        st.setInt(1,ID);
        rs = st.executeQuery();

        while (rs.next()) {

            return rs.getString("date("+ type + "_date)");

        }
    }

    return null;
    
    }    
        
    public static void updateLastLogged(String username) throws SQLException{
        
            String query = "UPDATE user SET last_logged_in = ? WHERE username = ?" ;
            
            try (Connection con = getConnection()) {
                PreparedStatement ps = con.prepareStatement(query);
                
                java.util.Date date = new java.util.Date();
                
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATETIME_FORMAT);
                
                String sqlDate = sdf.format(date);
                
                ps.setString(1, sqlDate);
                ps.setString(2, username);
                
                ps.executeUpdate();
            }
        
    }
    
    public static ResultSet getAllFrom(String select, String tableName, String additions, String whereClause, String ordering) throws SQLException{
        
        Connection con = getConnection();
        String query = "SELECT " + select + " FROM " + tableName + " " + additions + " " + whereClause + " " + ordering;

        PreparedStatement st;
        ResultSet rs;
                
            st = con.prepareStatement(query);
            return rs = st.executeQuery();

    }
    
    public static void AnimateField(JFXTextField field, Label status, String formula){
        
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
