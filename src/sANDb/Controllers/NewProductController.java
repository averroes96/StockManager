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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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
public class NewProductController implements Initializable,Init {
    
    
        @FXML public TextField nameField,sellField,qteField;
        @FXML public ChoiceBox catField;
        @FXML public Label imgField,minimize,sellStatus,nameStatus,qteStatus;
        @FXML public Button addProduct,addPhoto,cancel;
        

        SpecialAlert alert = new SpecialAlert();

        File selectedFile = null;
    
        public Employer employer = new Employer();
        
        private double xOffset = 0;
        private double yOffset = 0;        

    
    public void getEmployer(Employer employer){
        
        this.employer = employer; 
    }
    
    @FXML    
    private void chooseImage()
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Select a .JPG .PNG .GIF image", "*.jpg", "*.png", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                imgField.setText("");
                imgField.setGraphic(new ImageView(new Image(
                        selectedFile.toURI().toString(), 224, 224, true, true)));
            }
            catch (Exception e) {
                alert.show(UNKNOWN_ERROR, "فشل إضافة الصورة", Alert.AlertType.ERROR,true);
            }
        }

    }        
    
    @FXML
    private boolean checkInputs()
    {
        if (nameField.getText().trim().equals("") && sellField.getText().trim().equals("") && qteField.getText().trim().equals("") ) {
            alert.show("بعض حقول الإدخال غير مملوءة","من فضلك حدد إسم المنتوج وسعر البيع والكمية", Alert.AlertType.WARNING,false);
            return false;
        }
        else if (nameField.getText().trim().equals("")) {
            alert.show("بعض حقول الإدخال غير مملوءة", "من فضلك أدخل إسم المنتوج", Alert.AlertType.WARNING,false);
            return false;
        }
        else if (nameField.getText().length() >= 50) {
            alert.show(LONG_NAME_ERROR, LONG_NAME_ERROR_MSG, Alert.AlertType.WARNING,false);
            return false;
        }        
        else if (sellField.getText().trim().equals("")) {
            alert.show("بعض حقول الإدخال غير مملوءة", "من فضلك قم بإدخال سعر المنتوج", Alert.AlertType.WARNING,false);
            return false;
        }
        
            if(qteField.getText().matches("^[1-9]?[0-9]{1,7}$") || qteField.getText().trim().equals("")){
                    if(sellField.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(sellField.getText().trim()) > 0)
                    return true;
                    else{
                    alert.show(INVALID_PRICE, INVALID_PRICE_MSG, Alert.AlertType.ERROR,false);
                    return false;
                    }               
            }
            else{
                alert.show(INVALID_QTE, INVALID_QTE_MSG, Alert.AlertType.ERROR,false);
                return false;                
            }
                
    }
    
    private boolean checkProdName(){
        Connection con = getConnection();
        String query = "SELECT * FROM product WHERE name = ?";

        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(query);
            st.setString(1, nameField.getText());
            rs = st.executeQuery();
            int count = 0;
            while (rs.next()) {
                ++count;  
            }
            

            con.close();
            return count == 0;


        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return false;
        } 
        
    }
    
    
    private void resetWindow()
    {
        nameField.setText("");
        sellField.setText("");        
        imgField.setText("لم يتم تحديد أي صورة");
        imgField.setGraphic(null);
        selectedFile = null;
        nameField.requestFocus();
    }    
  
    public void logOut(ActionEvent event) throws IOException {

                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sANDb/FXMLs/Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(employer);
                        mControl.returnMenu("products");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource("/sANDb/Layout/custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource("/sANDb/Layout/buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.show(); 
                        root.setOnMousePressed((MouseEvent mevent) -> {
                            xOffset = mevent.getSceneX();
                            yOffset = mevent.getSceneY();
                        });
                        root.setOnMouseDragged((MouseEvent mevent) -> {
                            stage.setX(mevent.getScreenX() - xOffset);
                            stage.setY(mevent.getScreenY() - yOffset);
                        });                           
            
    } 

    @FXML
    private void insertProduct()
    {
        if (checkInputs()) {
            
        if (checkProdName()) {            
            
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        alert.show("خطأ خلال محاولة الإتصال بقاعدة البيانات", "تعذر الإتصال بقاعدة البيانات، تأكد من أن السرفر يعمل وحاول مجددا", Alert.AlertType.ERROR,true);
                    }
                    
                    PreparedStatement ps;
                    
                    if (selectedFile == null) {
                        ps = con.prepareStatement("INSERT INTO product(name, sell_price, prod_quantity, add_date) values(?,?,?,?)");
                    }
                    else {
                        String createImagePath = saveSelectedImage(selectedFile);
                        
                        ps = con.prepareStatement("INSERT INTO product(name, sell_price, prod_quantity, add_date, image_url) values(?,?,?,?,?)");
                        ps.setString(5, createImagePath);
                    }
                    
                    ps.setString(1, nameField.getText());
                    ps.setInt(2, Integer.parseInt(sellField.getText()));
                    ps.setInt(3, qteField.getText().equals("")? 0 : Integer.parseInt(qteField.getText() ));
                    
                    LocalDate todayLocalDate = LocalDate.now();
                    Date sqlDate = Date.valueOf(todayLocalDate);
                    
                    ps.setDate(4, sqlDate);
                    
                    ps.executeUpdate();
                }

                

            }
            catch (NumberFormatException | SQLException e) {
                alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            }
            
            alert.show("تم إضافة المنتوج", "المنتوج متوفر حاليا في قاعدة البيانات", Alert.AlertType.INFORMATION,false);
            resetWindow();
        }
        else
            alert.show("المنتوج موجود في قاعدة البيانات", "إسم هذا المنتوج موجود في قاعدة البيانات", Alert.AlertType.ERROR, true);
        }

    }
        // TODO
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
            imgField.setText("");
            imgField.setGraphic(new ImageView(new Image(
                    ClassLoader.class.getResourceAsStream("/sANDb/images/product_default.png"),
                    60, 60, true, true)));       
        
        addPhoto.setOnAction(Action -> {
            chooseImage();
        });

        addProduct.setOnAction(Action -> {
            insertProduct();
        });
        
        minimize.setOnMouseClicked(Action -> {
        
            minimize(Action);
            
        });

        sellField.setOnKeyReleased(event -> {
            
        if (!sellField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            sellStatus.setVisible(true);
            sellField.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            sellStatus.setVisible(false);
            sellField.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }
        });
        
        sellField.setOnKeyPressed(event -> {

        if (!sellField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            sellStatus.setVisible(true);
            sellField.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            sellStatus.setVisible(false);
            sellField.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }         
            
        });
        
        sellField.setOnKeyTyped(event -> {

        if (!sellField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            sellStatus.setVisible(true);
            sellField.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            sellStatus.setVisible(false);
            sellField.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }         
            
        });      
        
        qteField.setOnKeyReleased(event -> {
            
        if (!qteField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            qteStatus.setVisible(true);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            qteStatus.setVisible(false);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }
        });
        
        qteField.setOnKeyPressed(event -> {

        if (!qteField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            qteStatus.setVisible(true);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            qteStatus.setVisible(false);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }         
            
        });
        
        qteField.setOnKeyTyped(event -> {

        if (!qteField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            qteStatus.setVisible(true);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            qteStatus.setVisible(false);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }         
            
        });           
        
    }    
    
}