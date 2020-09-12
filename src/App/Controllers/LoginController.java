/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Employer;
import static Include.Common.getConnection;
import static Include.Common.initLayout;
import static Include.Common.setDraggable;
import static Include.Common.updateLastLogged;
import Include.Init;
import static Include.Init.ERROR_SMALL;
import static Include.Init.UNKNOWN_ERROR;
import animatefx.animation.AnimationFX;
import animatefx.animation.Shake;
import animatefx.animation.ZoomIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author med
 */
public class LoginController implements Initializable,Init {
    
    @FXML private JFXTextField username;
    @FXML private JFXPasswordField password;
    @FXML private JFXButton loginButton;
    @FXML private StackPane stackPane;
    @FXML private JFXDialog dialog;
    @FXML private Label title;
    @FXML private HBox usernameHB, passwordHB;
    
    public void loadDialog(JFXDialogLayout layout){
        
        stackPane.setVisible(true);
        JFXButton btn = new JFXButton(OKAY);
        btn.setDefaultButton(true);
        btn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            btn.setDefaultButton(false);
            loginButton.setDefaultButton(true);
        });
        loginButton.setDefaultButton(false);
        layout.setActions(btn);
        dialog = new JFXDialog(stackPane, layout , JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.show();
        
    }


    public Employer getUser(String username, String password)
    {
        Connection con = getConnection();
        String query = "SELECT * FROM user INNER JOIN privs ON user.user_id = privs.user_id WHERE username = ? AND password = ?";

        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = con.prepareStatement(query);
            ps.setString(1, username);        
            ps.setString(2, password);
            
            rs = ps.executeQuery();
            
                Employer employer = new Employer();            

            while (rs.next()) {

                employer.setUserID(rs.getInt("user_id"));
                employer.setFullname(rs.getString("fullname"));
                employer.setAdmin(rs.getInt("admin"));
                employer.setBuyPrivs(rs.getInt("manage_buys"));
                employer.setProdPrivs(rs.getInt("manage_products"));
                employer.setSellPrivs(rs.getInt("manage_sells"));
                employer.setUserPrivs(rs.getInt("manage_users"));
                employer.setPassword(password);
                employer.setUsername(username);
                
                return employer;
            }

            con.close();
        }
        catch (SQLException e) {
            try {
                Runtime.getRuntime().exec("C:\\xampp\\mysql\\bin\\mysqld.exe"); 
            } catch (IOException ex) {
                
                JFXDialogLayout layout = new JFXDialogLayout();
                initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);                
                
                loadDialog(layout);                  
                //alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            }
        }
        return null;
    }    
    
    
    public boolean exists(String username, String password) throws SQLException{
        
        Connection con = getConnection();
        String query = "SELECT * FROM user WHERE username = ? AND password = ? AND active = 1";

        PreparedStatement stmt;
        ResultSet rs;
     
        try {

            stmt = con.prepareStatement(query);
            stmt.setString(1, username);        
            stmt.setString(2, password);
            
            rs = stmt.executeQuery(); 
            
            return rs.next();
                
  
        } catch (SQLException ex) {
            try {
                Runtime.getRuntime().exec("C:\\xampp\\mysql\\bin\\mysqld.exe"); 
            } catch (IOException e) {
                
                JFXDialogLayout layout = new JFXDialogLayout();
                initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);                
                
                loadDialog(layout);          
                //alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
                
            }
            return false;
        }

    }
    
    @FXML
    public void login(ActionEvent event) throws IOException{
        
        try {
            if(exists(username.getText().trim(),password.getText().trim())){
                updateLastLogged(username.getText().trim());                
                ((Node)event.getSource()).getScene().getWindow().hide();
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"));
                AnchorPane root = (AnchorPane)loader.load();
                MainController mControl = (MainController)loader.getController();
                mControl.getEmployer(getUser(username.getText(), password.getText()));
                Scene scene = new Scene(root);
                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                //stage.initStyle(StageStyle.TRANSPARENT);
                scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                stage.setScene(scene);
                stage.setMinHeight(700);
                stage.setMinWidth(1000);
                stage.show();
                setDraggable(root,stage);

                
            }
            else{
                
                JFXDialogLayout layout = new JFXDialogLayout();
                initLayout(layout, CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, ERROR_SMALL);                
                
                loadDialog(layout);       
                
                //alert.show(USER_INFO, USER_INFO_MESSAGE, Alert.AlertType.ERROR,false);
                        
            }
        } catch (SQLException ex) {
                JFXDialogLayout layout = new JFXDialogLayout();
                initLayout(layout, CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, ERROR_SMALL);                
                
                loadDialog(layout);                
                //alert.show(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, Alert.AlertType.ERROR,true);
        }
        
        
    }
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        new ZoomIn(usernameHB).play();
        new ZoomIn(passwordHB).play();
        new ZoomIn(loginButton).play();
        new ZoomIn(title).play();
        
        AnimationFX loginBtnAnim = new Shake(loginButton);
        
        loginButton.setOnAction(Action ->{
            try {
                login(Action);
            } catch (IOException ex) {

                JFXDialogLayout layout = new JFXDialogLayout();
                initLayout(layout, CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, ERROR_SMALL);                
                
                loadDialog(layout);        
            //alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
            }
        });
        
        loginButton.setOnMouseEntered(value -> {
            loginBtnAnim.play();
        });
        loginButton.setOnMouseExited(value -> {
            loginBtnAnim.stop();
        });
    }    
    
}
