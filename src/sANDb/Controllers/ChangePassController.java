/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sANDb.Controllers;

import Data.Employer;
import static Include.Common.getConnection;
import Include.Init;
import Include.SpecialAlert;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

/**
 * FXML Controller class
 *
 * @author med
 */
public class ChangePassController implements Initializable,Init {
    @FXML public PasswordField current,newPass,repeat;
    @FXML public Button save;
    
    private Employer employer = new Employer();
    
    private final SpecialAlert alert = new SpecialAlert();
    
    public void getEmployer(Employer emp){
        this.employer = emp;
    }
    
    private boolean checkPassword(){
    
        Connection con = getConnection();
        String query = "SELECT password FROM user WHERE user_id = ?";

        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(query);
            st.setInt(1, employer.getUserID());
            rs = st.executeQuery();
            
            if (rs.next()) {
                
                return rs.getString("password").equals(current.getText());
                
            }
            con.close();
            
        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return false;
        }
        
        return false;
    
    }
    
    
    private boolean checkInputs()
    {
        if (current.getText().trim().equals("") || newPass.getText().trim().equals("") || repeat.getText().trim().equals("")) {
            alert.show(MISSING_FIELDS, MISSING_FIELDS_MSG, Alert.AlertType.WARNING,false);
            return false;
        }
        else if(!newPass.getText().matches("^[a-zA-Z0-9._-]{7,30}$")){
            alert.show(PASSWORD_ERROR, PASSWORD_ERROR_MSG, Alert.AlertType.WARNING,false);
            return false;              
        }
        else if(!newPass.getText().equals(repeat.getText())){
            alert.show(PASSWORD_ERROR, PASSWORD_ERROR_MSG_2, Alert.AlertType.ERROR,false);
            return false;           
        }
        
        return true;
    }
    
    public void changePassword(){
        
        if (checkInputs()) {
            
            if(checkPassword()){
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        alert.show(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, Alert.AlertType.ERROR,true);
                    }
                    
                    PreparedStatement ps;
                    
                    
                    ps = con.prepareStatement("UPDATE user SET password = ? WHERE user_id = ?");
                    
                    
                    
                    
                    ps.setString(1, newPass.getText());
                    ps.setInt(2, employer.getUserID());
                    
                    ps.executeUpdate();
                }
                
                alert.show(PASSWORD_UPDATED, PASSWORD_UPDATED_MSG, Alert.AlertType.INFORMATION,false);
                save.getScene().getWindow().hide();


            }
            catch (NumberFormatException | SQLException e) {
                alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            }
        }
            else{
                alert.show(WRONG_PASSWORD, WRONG_PASSWORD_MSG, Alert.AlertType.ERROR,false);
            }
        }        
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        
        // TODO
    }    
    
}
