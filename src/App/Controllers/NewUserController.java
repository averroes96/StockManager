/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.User;
import static Include.Common.AnimateField;
import static Include.Common.controlDigitField;
import static Include.Common.getConnection;
import static Include.Common.saveSelectedImage;
import static Include.Common.startStage;
import Include.Init;
import static Include.Init.ERROR_SMALL;
import Include.SMController;
import animatefx.animation.BounceIn;
import animatefx.animation.Tada;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
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
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
public class NewUserController extends SMController implements Initializable,Init {

    @FXML private Button returnBtn;
    @FXML private JFXButton saveBtn;
    @FXML private JFXTextField fullname,phone,username;
    @FXML private JFXPasswordField password;
    @FXML private JFXCheckBox products,users,sells,buys;
    @FXML private Label fullnameStatus,phoneStatus;
    @FXML private JFXToggleButton admin ;
    @FXML private Circle userIV;
        
    File selectedFile = null;
    
    public void getEmployer(User employer){
        this.employer = employer;
    }
    

    public void chooseImage()
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Select a .JPG .PNG .GIF image", "*.jpg", "*.png", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(saveBtn.getScene().getWindow());

        if (selectedFile != null) {
            try {
                userIV.setFill(new ImagePattern(new Image(
                        selectedFile.toURI().toString(),
                        userIV.getCenterX(), userIV.getCenterY(), false, false)));
                animateNode(new BounceIn(userIV));                
            }
            catch (Exception e) {
                exceptionLayout(e, saveBtn);
            }
        }

    }     

    @Override
    public boolean checkInputs()
    {
        if (fullname.getText().trim().equals("")) {
            customDialog(bundle.getString("missing_fields"), bundle.getString("missing_fields_msg"), ERROR_SMALL, true, saveBtn);
            return false;
        }
        else if(!fullname.getText().matches("^[\\p{L} .'-]+$")){
            customDialog(bundle.getString("invalid_name"), bundle.getString("invalid_name_msg"), ERROR_SMALL, true, saveBtn);
            return false;              
        }        
        else if(username.getText().trim().equals("") || password.getText().trim().equals("")){
            customDialog(bundle.getString("missing_fields"), bundle.getString("missing_fields_msg"), ERROR_SMALL, true, saveBtn);
            return false;            
            
        }
        else if(!username.getText().trim().matches("^[a-zA-Z0-9._-]{5,30}$")){
            customDialog(bundle.getString("invalid_username"), bundle.getString("invalid_username_msg"), ERROR_SMALL, true, saveBtn);
            return false;              
        }
        else if(!password.getText().trim().matches("^[a-zA-Z0-9._-]{7,30}$")){
            customDialog(bundle.getString("invalid_password"), bundle.getString("invalid_password_msg"), ERROR_SMALL, true, saveBtn);
            return false;              
        }
        else if(!phone.getText().trim().matches("^0[5-7][0-9]{8}$") && !phone.getText().equals("")){
            customDialog(bundle.getString("invalid_phone"), bundle.getString("invalid_phone_msg"), ERROR_SMALL, true, saveBtn);
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
        userIV.setFill(new ImagePattern(new Image(
            ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/user.png"),
            96, 96, true, true))); 
        selectedFile = null;
        
    }     
    
    
    @FXML
    @Override
    public void logOut(ActionEvent event) throws IOException {

        ((Node)event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "Main.fxml"), bundle);
        AnchorPane root = (AnchorPane)loader.load();
        MainController mControl = (MainController)loader.getController();
        mControl.getEmployer(employer);
        mControl.returnMenu("employers");
        startStage(root, (int)root.getWidth(), (int)root.getHeight());
    }

    private void insertEmployer() throws SQLException
    {
        if (checkInputs()) {
            
            if(!User.usernameExist(username.getText())){
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        customDialog(bundle.getString("connection_error"), bundle.getString("connection_error_msg"), ERROR_SMALL, true, saveBtn);
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
                            customDialog(bundle.getString("unknown_error"), "Creating key failed, no ID obtained.", ERROR_SMALL, true, saveBtn);
                        }
                    }

                        con.close();
                    
                }
                customDialog(bundle.getString("user_added"), bundle.getString("user_added_msg"), INFO_SMALL, true, saveBtn);                
                resetWindow();


            }
            catch (NumberFormatException | SQLException | IOException e) {
                exceptionLayout(e, saveBtn);
            }
        }
        else{
            customDialog(bundle.getString("username_exists"), bundle.getString("username_exists_msg"), INFO_SMALL, true, saveBtn);
        }
        }

    }        
           
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        try {
            isAnimated();
        } catch (SQLException ex) {
            exceptionLayout(ex, saveBtn);
        }
        
        if(bundle.getLocale().getLanguage().equals("ar"))
            anchorPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        
        userIV.setFill(new ImagePattern(new Image(
            ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/user.png"),
            96, 96, true, true)));         
        
        admin.setOnAction(Action ->{           
            previligesAction();
        });
        
        userIV.setOnMouseClicked(Action -> {
            chooseImage();
        });
        
        returnBtn.setOnAction(Action ->{
            try {
                logOut(Action);
            } catch (IOException ex) {
                exceptionLayout(ex, saveBtn);
            }
        });
        
        saveBtn.setOnAction(Action -> {
            try {
                insertEmployer();
            } catch (SQLException ex) {
                exceptionLayout(ex, saveBtn);
            }
        });
         
            
        AnimateField(fullname, fullnameStatus, "^[\\p{L} .'-]+$", isAnimated);
        AnimateField(phone, phoneStatus, "^0[5-7]?[0-9]{8}$", isAnimated);
                
        controlDigitField(phone);
        
    }    

    private void previligesAction() {
        
            if(admin.isSelected()){

                products.setSelected(true);
                products.setDisable(true);
                animateNode(new Tada(products));

                users.setSelected(true);
                users.setDisable(true);
                animateNode(new Tada(users));

                sells.setSelected(true);
                sells.setDisable(true);
                animateNode(new Tada(sells));

                buys.setSelected(true);
                buys.setDisable(true);
                animateNode(new Tada(buys));

            }
            else{

                products.setDisable(false);
                users.setDisable(false);
                sells.setDisable(false);
                buys.setDisable(false);

            }
            
    }
    
}