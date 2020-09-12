/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Employer;
import Include.Common;
import static Include.Common.AnimateField;
import static Include.Common.getConnection;
import static Include.Common.initLayout;
import static Include.Common.saveSelectedImage;
import Include.Init;
import static Include.Init.ERROR_SMALL;
import static Include.Init.OKAY;
import static Include.Init.UNKNOWN_ERROR;
import animatefx.animation.AnimationFX;
import animatefx.animation.BounceIn;
import animatefx.animation.Shake;
import animatefx.animation.Tada;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
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
import javafx.scene.control.Button;
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
public class NewEmployerController implements Initializable,Init {

    @FXML Button cancel;
    @FXML JFXButton save;
    @FXML JFXTextField fullname,phone,username;
    @FXML JFXPasswordField password;
    @FXML JFXCheckBox products,users,sells,buys;
    @FXML Label image,fullnameStatus,phoneStatus;
    @FXML JFXToggleButton admin ;
    @FXML private StackPane stackPane;
    @FXML private JFXDialog dialog;    
    
    Employer employer = new Employer();
    
    File selectedFile = null;
    
    public void getEmployer(Employer employer){
        
        this.employer = employer;
        
    }
    
    public void loadDialog(JFXDialogLayout layout, boolean btnIncluded){
        
        stackPane.setVisible(true);
        JFXButton btn = new JFXButton(OKAY);
        btn.setDefaultButton(true);
        save.setDefaultButton(false);
        btn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            btn.setDefaultButton(false);
            save.setDefaultButton(true);
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

    public void customDialog(String title, String body, String icon, boolean btnIncluded){
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, title, body, icon);
            
            loadDialog(layout, btnIncluded);
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
                        selectedFile.toURI().toString(), 192, 160, true, true)));
            }
            catch (Exception e) {
                exceptionLayout(e);
            }
        }

    }     

    private boolean checkInputs()
    {
        if (fullname.getText().trim().equals("")) {
            customDialog(MISSING_FIELDS, MISSING_FIELDS_MSG, ERROR_SMALL, true);
            return false;
        }
        else if(!fullname.getText().matches("^[\\p{L} .'-]+$")){
            customDialog(UNVALID_NAME, UNVALID_NAME_MSG, ERROR_SMALL, true);
            return false;              
        }        
        else if(username.getText().trim().equals("") || password.getText().trim().equals("")){
            customDialog(MISSING_FIELDS, MISSING_FIELDS_MSG, ERROR_SMALL, true);
            return false;            
            
        }
        else if(!username.getText().trim().matches("^[a-zA-Z0-9._-]{5,30}$")){
            customDialog(USERNAME_ERROR, USERNAME_ERROR_MSG, ERROR_SMALL, true);
            return false;              
        }
        else if(!password.getText().trim().matches("^[a-zA-Z0-9._-]{7,30}$")){
            customDialog(PASSWORD_ERROR, PASSWORD_ERROR_MSG, ERROR_SMALL, true);
            return false;              
        }
        else if(!phone.getText().trim().matches("^[5-7]?[0-9]{10}$") && !phone.getText().equals("")){
            customDialog(UNVALID_PHONE, UNVALID_PHONE_MSG, ERROR_SMALL, true);
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
        buys.setDisable(false);
        phone.setText("");
        username.setText("");
        password.setText(""); 
            image.setText("");
            ImageView img = new ImageView(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/user.png"),
                    64, 64, true, true));
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
            exceptionLayout(e);
            return 0;
        }       
       
   } 
    
    @FXML
    public void cancel(ActionEvent event) throws IOException {

                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(employer);
                        mControl.returnMenu("employers");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        //stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.setMinHeight(700);
                        stage.setMinWidth(1000);
                        stage.show();
                        Common.setDraggable(root, stage);
    }

    @FXML
    private void insertEmployer()
    {
        if (checkInputs()) {
            
            if(usernameExist() == 0){
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        customDialog(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, ERROR_SMALL, true);
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
                            JFXDialogLayout layout = new JFXDialogLayout();
                            initLayout(layout, UNKNOWN_ERROR, "Creating key failed, no ID obtained.", ERROR_SMALL);

                            loadDialog(layout, true);                            
                        }
                    }

                        con.close();
                    
                }
                customDialog(USER_ADDED, USER_ADDED_MSG, INFO_SMALL, true);                
                resetWindow();


            }
            catch (NumberFormatException | SQLException e) {
                exceptionLayout(e);
            }
        }
            else{
                customDialog(USERNAME_ERROR, USERNAME_ERROR_MSG_2, INFO_SMALL, true);
            }
        }

    }        
           
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        admin.setOnAction(Action ->{
            
        if(admin.isSelected()){
            
            products.setSelected(true);
            products.setDisable(true);
            new Tada(products).play();
            
            users.setSelected(true);
            users.setDisable(true);
            new Tada(users).play();
            
            sells.setSelected(true);
            sells.setDisable(true);
            new Tada(sells).play();
            
            buys.setSelected(true);
            buys.setDisable(true);
            new Tada(buys).play();
            
        }
        else{
            
            products.setDisable(false);
            users.setDisable(false);
            sells.setDisable(false);
            buys.setDisable(false);
            
        }            
            
        });
        
        image.setOnMouseClicked(Action -> {
            chooseImage();
            new BounceIn(image).play();
        });
        
        cancel.setOnAction(Action ->{
            try {
                cancel(Action);
            } catch (IOException ex) {
                exceptionLayout(ex);
            }
        });
        
        save.setOnAction(Action -> {
            insertEmployer();
        });
        
            image.setText("");
            ImageView img = new ImageView(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/user.png"),
                    64, 64, true, true));
            image.setGraphic(img); 
            
        AnimateField(fullname,fullnameStatus,"^[\\p{L} .'-]+$");
        AnimateField(phone,phoneStatus,"^[5-7]?[0-9]{10}$");
        
        AnimationFX saveBtnAnim = new Shake(save);
        

        save.setOnMouseEntered(value -> {
            saveBtnAnim.play();
        });
        save.setOnMouseExited(value -> {
            saveBtnAnim.stop();
        });  
        
    }    
    
}