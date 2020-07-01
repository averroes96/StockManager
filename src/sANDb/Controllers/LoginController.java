/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sANDb.Controllers;

import Data.Employer;
import static Include.Common.getConnection;
import static Include.Common.setDraggable;
import static Include.Common.updateLastLogged;
import Include.Init;
import static Include.Init.UNKNOWN_ERROR;
import Include.SpecialAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author med
 */
public class LoginController implements Initializable,Init {
    
    @FXML JFXTextField username;
    @FXML JFXPasswordField password;
    @FXML JFXButton loginButton;
    @FXML Label closeButton,reduceButton;
    @FXML JFXProgressBar loginProgress;
    
    SpecialAlert alert = new SpecialAlert();



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
                alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
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
                alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sANDb/FXMLs/Main.fxml"));
                AnchorPane root = (AnchorPane)loader.load();
                MainController mControl = (MainController)loader.getController();
                mControl.getEmployer(getUser(username.getText(), password.getText()));
                Scene scene = new Scene(root);
                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                stage.initStyle(StageStyle.TRANSPARENT);
                scene.getStylesheets().add(getClass().getResource("/sANDb/Layout/custom.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
                setDraggable(root,stage);

                
            }
            else{
                
                alert.show(USER_INFO, USER_INFO_MESSAGE, Alert.AlertType.ERROR,false);
                        
            }
        } catch (SQLException ex) {
            alert.show(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, Alert.AlertType.ERROR,true);
            
        }
        
        
    }
    
    @FXML
    public void minimize(MouseEvent event){
        
        ((Stage)((Label)event.getSource()).getScene().getWindow()).setIconified(true);
        
    }
    
    @FXML
    public void close(MouseEvent event){
        
        System.exit(0);
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        
        loginButton.setOnAction(Action ->{
            try {
                login(Action);
            } catch (IOException ex) {
                alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
            }
        });      
        
    }    
    
}
