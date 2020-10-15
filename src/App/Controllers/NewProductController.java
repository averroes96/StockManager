/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Product;
import Data.User;
import static Include.Common.AnimateField;
import static Include.Common.controlDigitField;
import static Include.Common.getConnection;
import static Include.Common.saveSelectedImage;
import static Include.Common.startStage;
import Include.GDPController;
import Include.Init;
import animatefx.animation.AnimationFX;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;


/**
 * FXML Controller class
 *
 * @author med
 */
public class NewProductController extends GDPController implements Initializable,Init {
    
    
    @FXML private JFXTextField nameField,sellField,qteField;
    @FXML private Label sellStatus,qteStatus;
    @FXML private JFXButton addProduct;
    @FXML private Circle productIV;
        
    File selectedFile = null;

    
    public void getEmployer(User employer){
        this.employer = employer; 
    }
    
    private void chooseImage()
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Select a .JPG .PNG .GIF image", "*.jpg", "*.png", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(addProduct.getScene().getWindow());

        if (selectedFile != null) {
            try {
            productIV.setFill(new ImagePattern(new Image(
                    selectedFile.toURI().toString(),
                    productIV.getCenterX(), productIV.getCenterY(), true, true)));
            }
            catch (Exception e) {
                exceptionLayout(e, addProduct);
            }
        }

    }        
    
    
    @Override
    public boolean checkInputs()
    {
        if (nameField.getText().trim().equals("") || sellField.getText().trim().equals("") || qteField.getText().trim().equals("") ) {
            customDialog(bundle.getString("missing_fields"), bundle.getString("mssing_fields"), INFO_SMALL, true, addProduct);
            return false;
        }
        else if (nameField.getText().length() >= 50) {
            customDialog(bundle.getString("long_name"), bundle.getString("long_name_msg"), INFO_SMALL, true, addProduct);
            return false;
        }
        if(qteField.getText().matches("^[1-9]?[0-9]{1,7}$") || qteField.getText().trim().equals("")){
                if(sellField.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(sellField.getText().trim()) > 0)
                    return true;
                else{
                    customDialog(bundle.getString("invalid_price"), bundle.getString("invalid_price_msg"), INFO_SMALL, true, addProduct);
                    return false;
                }               
            }
            else{
                customDialog(bundle.getString("invalid_qte"), bundle.getString("invalid_qte"), INFO_SMALL, true, addProduct);             
                return false;                
            }
                
    }
    
    private boolean checkProdName(){
        
        try {
            return Product.nameExists(nameField.getText());
        }
        catch (SQLException e) {
            exceptionLayout(e, addProduct);
            return false;
        } 
        
    }
    
    
    private void resetWindow()
    {
        nameField.setText("");
        sellField.setText("");
        qteField.setText(""); 
        productIV.setFill(new ImagePattern(new Image(
            ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/large_product_primary.png"),
            64, 64, false, false)));        
        selectedFile = null;
    }    
  
    @Override
    public void logOut(ActionEvent event) throws IOException {

        ((Node)event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
        AnchorPane root = (AnchorPane)loader.load();
        MainController mControl = (MainController)loader.getController();
        mControl.getEmployer(employer);
        mControl.returnMenu("products");
        startStage(root, 1000, 700);
            
    } 

    private void insertProduct()
    {
        if (checkInputs()) {
            
        if (!checkProdName()) {            
            
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        customDialog(bundle.getString("connection_error"), bundle.getString("connection_error_msg"), USER, true, addProduct);
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
            catch (NumberFormatException | SQLException | IOException e) {
                exceptionLayout(e, addProduct);
            }
            
            customDialog(
                    bundle.getString("product_added"), 
                    bundle.getString("product_added_msg"), 
                    INFO_SMALL, 
                    true, 
                    addProduct);
            
            resetWindow();
        }
        else{
            customDialog(
                    bundle.getString("product_exist"), 
                    bundle.getString("product_exist_msg"), 
                    INFO_SMALL, 
                    true, 
                    addProduct);
        }
        
        }

    }
        // TODO
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        AnimationFX addBtnAnim = new Shake(addProduct);
        
        productIV.setFill(new ImagePattern(new Image(
            ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/large_product_primary.png"),
            64, 64, true, true)));     
        
        productIV.setOnMouseClicked(Action -> {
            chooseImage();
        });

        addProduct.setOnAction(Action -> {
            insertProduct();
        });
        
        AnimateField(sellField,sellStatus,"^[1-9]?[0-9]{1,7}$");
        AnimateField(qteField,qteStatus,"^[1-9]?[0-9]{1,7}$");
        
        controlDigitField(qteField);
        controlDigitField(sellField);
        
        addProduct.setOnMouseEntered(value -> {
            addBtnAnim.play();
        });
        addProduct.setOnMouseExited(value -> {
            addBtnAnim.stop();
        });        
        
    }    
    
}