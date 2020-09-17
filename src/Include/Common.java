/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Include;

import Data.Employer;
import Data.Product;
import Data.Sell;
import static Include.Init.UNKNOWN_ERROR;
import animatefx.animation.AnimationFX;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 *
 * @author med
 */
public class Common implements Init {
    
    
    private static final SpecialAlert alert = new SpecialAlert();
    
    final static String dateFormat = "yyyy-MM-dd";
    
    final static String datetimeFormat = "yyyy-MM-dd HH:mm:ss" ;
    
    private static double xOffset = 0;
    private static double yOffset = 0;
    
    public static void controlDigitField(TextField field){
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Integer.parseInt(newValue);
            }
            catch(NumberFormatException e){
                if(!newValue.isEmpty())
                    field.setText(newValue.substring(0, newValue.length() - 1));
            }
        });
    }
    
    public static ObservableList<String> getAllProducts(int all){
        
        ObservableList<String> names = FXCollections.observableArrayList();
       
        Connection con = getConnection();
        
        String query = "SELECT name FROM product";
        
        if(all == 1){
            names.add("الكل");
        }else{
            query += " WHERE on_hold = 0" ;
        }        

        Statement st;
        ResultSet rs;
        

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {

                names.add(rs.getString("name"));
            }

            con.close();
        }
        catch (SQLException e) {
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);

        }
        
        return names;
        
    }
   
    public static void initLayout(JFXDialogLayout layout, String header, String body, String icon){
                
        Image image = new Image(IMAGES_PATH + icon);
        ImageView imageView = new ImageView(image);
        Label label = new Label(header);
        label.graphicProperty().setValue(imageView);
        layout.setHeading(label);
        layout.setBody(new Text(body));        
        
    }
    
    public static void animateBtn(JFXButton node){
        
        AnimationFX btnAnim = new Shake(node);

        node.setOnMouseEntered(value -> {
            btnAnim.play();
        });
        node.setOnMouseExited(value -> {
            btnAnim.stop();
        });  
        
    }
    

    public static Connection getConnection()
    {
        Connection con;
        try {

            con = DriverManager.getConnection(DB_NAME_WITH_ENCODING, USER, PASSWORD);
            return con;
        }
        catch (SQLException ex) {
            alert.show(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, Alert.AlertType.ERROR,true);
            return null;            
        }
    }
    
    public static String generateImagePath(File selectedFile)
    {

        java.util.Date date = new java.util.Date();
 
        SimpleDateFormat sdf = new SimpleDateFormat("Y-M-d-hh-mm-ss");
        
        String fileExtension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
 
        return UPLOADED_FILE_PATH + sdf.format(date) + fileExtension;
        
        
        
    }
    
    
    public static String saveSelectedImage(File selectedFile)
    {

        String createImagePath = Common.generateImagePath(selectedFile);
        try {

            FileOutputStream out;
            try (FileInputStream in = new FileInputStream(selectedFile)) {
                out = new FileOutputStream(createImagePath);
                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            }
            out.close();
        }
        catch(IOException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        }

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
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);

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
    
    public static int adminsCount(){
    
        try {
            int count;
            try (Connection con = getConnection()) {
                count = 0;
                String query = "SELECT count(*) FROM user WHERE admin = 1";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    
                    count = rs.getInt("count(*)");
                    
                }
            }
            
            return count;
        } catch (SQLException ex) {
            alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
            return 0;
        }
    
    
    }
    
    public static void minimize(MouseEvent event){
        
        ((Stage)((Label)event.getSource()).getScene().getWindow()).setIconified(true);
        
    }

    public static int getPrice(String name){
        
        Connection con = getConnection();
        String query = "SELECT * FROM product WHERE name = ? LIMIT 1";
        int targetedPrice = 0;

        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(query);
            st.setString(1, name);
            rs = st.executeQuery();
            int count = 0;
            while (rs.next()) {
                
                targetedPrice = rs.getInt("sell_price");
                
            }
            
            con.close();
            
            return targetedPrice;


        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return 0;
        }         
        
    }
    
    public static int getQuantity(String name){
        
        Connection con = getConnection();
        String query = "SELECT * FROM product WHERE name = ? LIMIT 1";
        int targetedPrice = 0;

        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(query);
            st.setString(1, name);
            rs = st.executeQuery();
            int count = 0;
            while (rs.next()) {
                
                targetedPrice = rs.getInt("prod_quantity");
                
            }
            
            con.close();
            
            return targetedPrice;


        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return 0;
        }         
        
    }    

    public static boolean refExist(String ref){
        
        Connection con = getConnection();
        String query = "SELECT * FROM product WHERE name = ? LIMIT 1";
        boolean found = false ;

        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(query);
            st.setString(1, ref);
            rs = st.executeQuery();
            int count = 0;
            
            if(rs.next()){
                
                found = true;
            }
            
            con.close();
            
            return found;


        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return false;
        }         
        
    }
    
    public static Product getProductByName(String name){
        
        Product product = new Product();
        Connection con = getConnection();
        String query = "SELECT * FROM product WHERE name = ?";

        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(query);
            st.setString(1, name);
            rs = st.executeQuery();
            int count = 0;
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
            

            con.close();
            if(count == 0)
                return null;
            else
                return product;


        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return null;
        }               
    }
    
    public static Product getProductByID(int ID){
        
        Product product = new Product();
        Connection con = getConnection();
        String query = "SELECT * FROM product WHERE prod_id = ?";

        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(query);
            st.setInt(1, ID);
            rs = st.executeQuery();
            int count = 0;
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
            

            con.close();
            if(count == 0)
                return null;
            else
                return product;


        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return null;
        }               
    }    


    public static Employer getUser(String name){
        
        Employer employer = new Employer();
        Connection con = getConnection();
        String query = "SELECT * FROM user INNER join privs ON user.user_id = privs.user_id WHERE username = ?";

        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(query);
            st.setString(1, name);
            rs = st.executeQuery();
            int count = 0;
            while (rs.next()) {
                
                employer.setAdmin(rs.getInt("admin"));
                employer.setFullname(rs.getString("fullname"));
                if(rs.getString("image") != null){
                employer.setImage(rs.getString("image"));
                }
                employer.setPassword(rs.getString("password"));
                employer.setUserID(rs.getInt("user_id"));
                employer.setPhone(rs.getString("telephone"));
                employer.setUsername(rs.getString("username"));
                employer.setBuyPrivs(rs.getInt("manage_buys"));
                employer.setProdPrivs(rs.getInt("manage_products"));
                employer.setSellPrivs(rs.getInt("manage_sells"));
                employer.setUserPrivs(rs.getInt("manage_users"));
                if(rs.getString("last_logged_in") != null){
                employer.setLastLogged(rs.getTimestamp("last_logged_in").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy h.mm a")));
                }
                ++count;
                
            }
            

            con.close();
            if(count == 0)
                return null;
            else
                return employer;


        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return null;
        }               
    }
    
    public  Sell getSell(int sellID){
        
        Connection con = getConnection();
        String query = "SELECT * FROM sell INNER JOIN product ON sell.prod_id = product.prod_id INNER JOIN user ON user.user_id = sell.user_id WHERE sell.sell_id = ? ORDER BY sell.sell_date";

        PreparedStatement st;
        ResultSet rs;
        Sell sell = new Sell();        

        try {
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

            con.close();
        }
        catch (SQLException e){ 
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);

        }       
        return sell;
        
    }
    
        public static String getDate(int ID, String type){
        
        Connection con = getConnection();
        String query = "SELECT date(" + type + "_date) FROM "+ type +" WHERE "+ type + "."+ type +"_id = ?";

        PreparedStatement st;
        ResultSet rs;  

        try {
            st = con.prepareStatement(query);
            st.setInt(1,ID);
            rs = st.executeQuery();

            while (rs.next()) {

                return rs.getString("date("+ type + "_date)");

            }

            con.close();
        }
        catch (SQLException e){ 
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return null;
        }       
        return null;
    }
    
    public static void updateLastLogged(String username){
        
        try {
            String query = "UPDATE user SET last_logged_in = ? WHERE username = ?" ;
            
            try (Connection con = getConnection()) {
                PreparedStatement ps = con.prepareStatement(query);
                
                java.util.Date date = new java.util.Date();
                
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(datetimeFormat);
                
                String sqlDate = sdf.format(date);
                
                ps.setString(1, sqlDate);
                ps.setString(2, username);
                
                ps.executeUpdate();
            }
            
        } catch (SQLException ex) {
             alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
        }
        
    }
    
    public static ResultSet getAllFrom(String select, String tableName, String additions, String whereClause, String ordering){
        
        Connection con = getConnection();
        String query = "SELECT " + select + " FROM " + tableName + " " + additions + " " + whereClause + " " + ordering;

        PreparedStatement st;
        ResultSet rs;
                

        try {
            st = con.prepareStatement(query);
            return rs = st.executeQuery();
            
        }
        catch (SQLException e){ 
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return null;
        }     
        
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
    
    public static void setDraggable(Pane root, Stage stage){
        
                        root.setOnMousePressed((MouseEvent event) -> {
                            xOffset = event.getSceneX();
                            yOffset = event.getSceneY();
                });
                        root.setOnMouseDragged((MouseEvent event) -> {
                            stage.setX(event.getScreenX() - xOffset);
                            stage.setY(event.getScreenY() - yOffset);
                });        
        
    }
    
    public static void setDraggable(AnchorPane root, Stage stage){
        
                        root.setOnMousePressed((MouseEvent event) -> {
                            xOffset = event.getSceneX();
                            yOffset = event.getSceneY();
                });
                        root.setOnMouseDragged((MouseEvent event) -> {
                            stage.setX(event.getScreenX() - xOffset);
                            stage.setY(event.getScreenY() - yOffset);
                });        
        
    }
     
    
    
}
