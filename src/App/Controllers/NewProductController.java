/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.User;
import static Include.Common.AnimateField;
import static Include.Common.getConnection;
import static Include.Common.initLayout;
import static Include.Common.saveSelectedImage;
import Include.Init;
import static Include.Init.ERROR_SMALL;
import static Include.Init.OKAY;
import static Include.Init.UNKNOWN_ERROR;
import animatefx.animation.AnimationFX;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author med
 */
public class NewProductController implements Initializable,Init {
    
    
        @FXML private JFXTextField nameField,sellField,qteField;
        @FXML private Label imgField,sellStatus,qteStatus;
        @FXML private JFXButton addProduct;
        @FXML private StackPane stackPane;
        @FXML private JFXDialog dialog;
        
        File selectedFile = null;
    
        public User employer = new User();
        
    public void loadDialog(JFXDialogLayout layout, boolean btnIncluded){
        
        stackPane.setVisible(true);
        JFXButton btn = new JFXButton(OKAY);
        btn.setDefaultButton(true);
        addProduct.setDefaultButton(false);
        btn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            btn.setDefaultButton(false);
            addProduct.setDefaultButton(true);
        });
        if(btnIncluded){
            layout.setActions(btn);
        }    
        dialog = new JFXDialog(stackPane, layout , JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.show();
        
    }
    
    public void exceptionLayout(Exception e){
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);
            
            loadDialog(layout, true);
    }

    
    public void getEmployer(User employer){
        
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
                exceptionLayout(e);
            }
        }

    }        
    
    @FXML
    private boolean checkInputs()
    {
        if (nameField.getText().trim().equals("") || sellField.getText().trim().equals("") || qteField.getText().trim().equals("") ) {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, MISSING_FIELDS, MISSING_FIELDS_MSG, ERROR_SMALL);
            
            loadDialog(layout, true);
            return false;
        }
        else if (nameField.getText().length() >= 50) {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, LONG_NAME_ERROR, LONG_NAME_ERROR_MSG, ERROR_SMALL);
            
            loadDialog(layout, true);
            return false;
        }
        
            if(qteField.getText().matches("^[1-9]?[0-9]{1,7}$") || qteField.getText().trim().equals("")){
                    if(sellField.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(sellField.getText().trim()) > 0)
                    return true;
                    else{
                        JFXDialogLayout layout = new JFXDialogLayout();
                        initLayout(layout, INVALID_PRICE, INVALID_PRICE_MSG, ERROR_SMALL);

                        loadDialog(layout, true);
                        return false;
                    }               
            }
            else{
                        JFXDialogLayout layout = new JFXDialogLayout();
                        initLayout(layout, INVALID_QTE, INVALID_QTE_MSG, ERROR_SMALL);

                        loadDialog(layout, true);                
                        return false;                
            }
                
    }
    
    private boolean checkProdName(){
        
        try {
            int count;
            try (Connection con = getConnection()) {
                String query = "SELECT * FROM product WHERE name = ?";
                PreparedStatement st;
                ResultSet rs;
                st = con.prepareStatement(query);
                st.setString(1, nameField.getText());
                rs = st.executeQuery();
                count = 0;
                while (rs.next()) {
                    ++count;
                }  
            }
            return count == 0;


        }
        catch (SQLException e) {
            exceptionLayout(e);
            return false;
        } 
        
    }
    
    
    private void resetWindow()
    {
        nameField.setText("");
        sellField.setText("");
        qteField.setText(""); 
        imgField.setText("لم يتم تحديد أي صورة");
        imgField.setGraphic(null);
        selectedFile = null;
        nameField.requestFocus();
    }    
  
    public void logOut(ActionEvent event) throws IOException {

                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(employer);
                        mControl.returnMenu("products");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.setMinHeight(700);
                        stage.setMinWidth(1000);
                        stage.show(); 
            
    } 

    @FXML
    private void insertProduct()
    {
        if (checkInputs()) {
            
        if (checkProdName()) {            
            
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        JFXDialogLayout layout = new JFXDialogLayout();
                        initLayout(layout, CONNECTION_ERROR, CONNECTION_ERROR, ERROR_SMALL);

                        loadDialog(layout, true);   
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
                exceptionLayout(e);
            }
                        JFXDialogLayout layout = new JFXDialogLayout();
                        initLayout(layout, PRODUCT_ADDED, PRODUCT_ADDED_MSG, ERROR_SMALL);

                        loadDialog(layout, true); 
            
                        resetWindow();
        }
        else{
                        JFXDialogLayout layout = new JFXDialogLayout();
                        initLayout(layout, PRODUCT_EXIST, PRODUCT_EXIST_MSG, ERROR_SMALL);

                        loadDialog(layout, true);
        }
        
        }

    }
        // TODO
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        AnimationFX addBtnAnim = new Shake(addProduct);
        
        imgField.setText("");
        imgField.setGraphic(new ImageView(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/large_product_primary.png"),
                    60, 60, true, true)));       
        
        imgField.setOnMouseClicked(sAction -> {
            chooseImage();
        });

        addProduct.setOnAction(Action -> {
            insertProduct();
        });
        
        AnimateField(sellField,sellStatus,"^[1-9]?[0-9]{1,7}$");
        AnimateField(qteField,qteStatus,"^[1-9]?[0-9]{1,7}$");
        
        addProduct.setOnMouseEntered(value -> {
            addBtnAnim.play();
        });
        addProduct.setOnMouseExited(value -> {
            addBtnAnim.stop();
        });        
        
    }    
    
}