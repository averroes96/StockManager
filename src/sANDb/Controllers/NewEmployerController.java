/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sANDb.Controllers;

import Data.Employer;
import static Include.Common.getConnection;
import static Include.Common.minimize;
import static Include.Common.saveSelectedImage;
import Include.Init;
import Include.SpecialAlert;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author med
 */
public class NewEmployerController implements Initializable,Init {

    @FXML Button upload,sava,cancel;
    @FXML TextField fullname,phone,username;
    @FXML PasswordField password;
    @FXML CheckBox admin,products,users,sells,buys;
    @FXML Label image,min,fullnameStatus,phoneStatus;
    
    Employer employer = new Employer();
    
    SpecialAlert alert = new SpecialAlert();

    File selectedFile = null;

    private double xOffset = 0;
    private double yOffset = 0;
    
    public void getEmployer(Employer employer){
        
        this.employer = employer;
        
    }
    
    @FXML    
    public void chooseImage()
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Select a .JPG .PNG .GIF image", "*.jpg", "*.png", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                image.setText("");
                image.setGraphic(new ImageView(new Image(
                        selectedFile.toURI().toString(), 200, 170, true, true)));
            }
            catch (Exception e) {
                alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            }
        }

    }     
    
    @FXML
    private boolean checkInputs()
    {
        if (fullname.getText().trim().equals("")) {
            alert.show(MISSING_FIELDS, MISSING_FIELDS_MSG, Alert.AlertType.WARNING,false);
            return false;
        }
        else if(!fullname.getText().matches("^[\\p{L} .'-]+$")){
            alert.show(UNVALID_NAME, UNVALID_NAME_MSG, Alert.AlertType.WARNING,false);
            return false;              
        }        
        else if(username.getText().trim().equals("") || password.getText().trim().equals("")){
            alert.show(MISSING_FIELDS, MISSING_FIELDS_MSG, Alert.AlertType.WARNING,false);
            return false;            
            
        }
        else if(!username.getText().trim().matches("^[a-zA-Z0-9._-]{5,30}$")){
            alert.show(USERNAME_ERROR, USERNAME_ERROR_MSG, Alert.AlertType.WARNING,false);
            return false;              
        }
        else if(!password.getText().trim().matches("^[a-zA-Z0-9._-]{7,30}$")){
            alert.show(PASSWORD_ERROR, PASSWORD_ERROR_MSG, Alert.AlertType.WARNING,false);
            return false;              
        }
        else if(!phone.getText().trim().matches("^[5-7]?[0-9]{10}$") && !phone.getText().equals("")){
            alert.show(UNVALID_PHONE, UNVALID_PHONE_MSG, Alert.AlertType.WARNING,false);
            return false;              
        }
        
        return true;

    }
    
    
    private void resetWindow()
    {
        fullname.setText("");
        admin.setSelected(false);
        products.setSelected(false);
        products.setDisable(false);
        users.setSelected(false);
        users.setDisable(false);
        sells.setSelected(false);
        sells.setDisable(false);
        buys.setSelected(false);
        buys.setSelected(false);
        phone.setText("");
        username.setText("");
        password.setText(""); 
            image.setText("");
            ImageView img = new ImageView(new Image(
                    ClassLoader.class.getResourceAsStream("/sANDb/images/user.png"),
                    200, 200, true, true));
            image.setGraphic(img);  
        selectedFile = null;
        
    }     
    
    public int usernameExist(){
       
        Connection con = getConnection();
        String query = "SELECT * FROM user WHERE username = ?";

        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(query);
            st.setString(1, username.getText());
            rs = st.executeQuery();
            int count = 0;
            
            while (rs.next()) {
                ++count;
                
            }
            con.close();
            
            return count;


        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return 0;
        }       
       
   } 
    
    @FXML
    public void cancel(ActionEvent event) throws IOException {

                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sANDb/FXMLs/Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(employer);
                        mControl.returnMenu("employers");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource("/sANDb/Layout/custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource("/sANDb/Layout/buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.show();
                        root.setOnMousePressed((MouseEvent event1) -> {
                            xOffset = event1.getSceneX();
                            yOffset = event1.getSceneY();
                        });
                        root.setOnMouseDragged((MouseEvent event1) -> {
                            stage.setX(event1.getScreenX() - xOffset);
                            stage.setY(event1.getScreenY() - yOffset);
                        });                         
            
    }

    @FXML
    private void insertEmployer()
    {
        if (checkInputs()) {
            
            if(usernameExist() == 0){
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        alert.show(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, Alert.AlertType.ERROR,true);
                    }
                    
                    PreparedStatement ps;
                    
                    if (selectedFile == null) {
                        ps = con.prepareStatement("INSERT INTO user(fullname, telephone, admin, username, password) values(?,?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
                    }
                    else {
                        String createImagePath = saveSelectedImage(selectedFile);
                        
                        ps = con.prepareStatement("INSERT INTO user(fullname, telephone, admin, username, password, image) values(?,?,?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
                        ps.setString(6, createImagePath);
                    }
                    
                    
                    
                    ps.setString(1, fullname.getText());
                    ps.setString(2, phone.getText());
                    if(admin.isSelected())
                        ps.setInt(3, 1);
                    else{
                        ps.setInt(3, 0);
                    }
                    ps.setString(4, username.getText());
                    ps.setString(5, password.getText());
                    
                    ps.executeUpdate();
                    
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            
                            int insertedUserID = generatedKeys.getInt(1) ;
                            ps = con.prepareStatement("INSERT INTO privs (user_id, manage_products, manage_users, manage_buys, manage_sells) VALUES (?,?,?,?,?)");
                            
                            ps.setInt(1, insertedUserID);
                            ps.setInt(2, products.isSelected()? 1 : 0);
                            ps.setInt(3, users.isSelected()? 1 : 0);
                            ps.setInt(4, buys.isSelected()? 1 : 0);
                            ps.setInt(5, sells.isSelected()? 1 : 0);
                            
                            ps.executeUpdate();

                            if(!products.isSelected() && !users.isSelected() && !sells.isSelected() && !buys.isSelected()){
                                
                                ps = con.prepareStatement("UPDATE user SET active = 2 WHERE user_id = ?");
                                ps.setInt(1, insertedUserID);
                                ps.executeUpdate();
                                
                            }
                        }
                        else {
                            alert.show(UNKNOWN_ERROR,"Creating key failed, no ID obtained.",Alert.AlertType.ERROR,true);
                        }
                    }

                        con.close();
                    
                }
                
                alert.show(USER_ADDED, USER_ADDED_MSG, Alert.AlertType.INFORMATION,false);
                
                resetWindow();


            }
            catch (NumberFormatException | SQLException e) {
                alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            }
        }
            else{
                alert.show(USERNAME_ERROR, "إسم المستخدم الذي قمت بإدخاله محجوز لحساب باسم حساب اخر", Alert.AlertType.ERROR,false);
            }
        }

    }        
           
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        min.setOnMouseClicked(Action ->{
        
            minimize(Action);
        
        });
        
        admin.setOnAction(Action ->{
            
        if(admin.isSelected()){
            
            products.setSelected(true);
            products.setDisable(true);
            
            users.setSelected(true);
            users.setDisable(true);
            
            sells.setSelected(true);
            sells.setDisable(true);
            
            buys.setSelected(true);
            buys.setDisable(true);
            
        }
        else{
            
            products.setDisable(false);
            users.setDisable(false);
            sells.setDisable(false);
            buys.setDisable(false);
            
        }            
            
        });
        
        
        
            image.setText("");
            ImageView img = new ImageView(new Image(
                    ClassLoader.class.getResourceAsStream("/sANDb/images/user.png"),
                    200, 170, true, true));
            image.setGraphic(img);        
        
        fullname.setOnKeyPressed(Action -> {
            
        if (fullname.getText().trim().equals("") || !fullname.getText().matches("^[\\p{L} .'-]+$")) {
            fullnameStatus.setVisible(true);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            fullnameStatus.setVisible(false);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }
            
        });
        fullname.setOnKeyTyped(Action -> {
            
        if (fullname.getText().trim().equals("") || !fullname.getText().matches("^[\\p{L} .'-]+$")) {
            fullnameStatus.setVisible(true);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            fullnameStatus.setVisible(false);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }        
            
        });
        fullname.setOnKeyReleased(Action -> {
            
        if (fullname.getText().trim().equals("") || !fullname.getText().matches("^[\\p{L} .'-]+$")) {
            fullnameStatus.setVisible(true);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }        
        else{
            fullnameStatus.setVisible(false);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }            
        });


        phone.setOnKeyPressed(Action -> {
            
        if (!phone.getText().matches("^[5-7]?[0-9]{10}$")) {
            phoneStatus.setVisible(true);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            phoneStatus.setVisible(false);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }
            
        });
        phone.setOnKeyTyped(Action -> {
            
        if (!phone.getText().matches("^[5-7]?[0-9]{10}$")) {
            phoneStatus.setVisible(true);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            phoneStatus.setVisible(false);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }      
            
        });
        phone.setOnKeyReleased(Action -> {
            
        if (!phone.getText().matches("^[5-7]?[0-9]{10}$")) {
            phoneStatus.setVisible(true);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            phoneStatus.setVisible(false);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }           
        });    

        
        
    }    
    
}