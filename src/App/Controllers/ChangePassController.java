/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Employer;
import static Include.Common.getConnection;
import Include.GDPController;
import Include.Init;
import static Include.Init.OKAY;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
/**
 * FXML Controller class
 *
 * @author med
 */
public class ChangePassController extends GDPController implements Initializable,Init {
    @FXML public JFXPasswordField current,newPass,repeat;
    @FXML public JFXButton save;
            
    public void getEmployer(Employer emp){
        this.employer = emp;
    }
    
    @Override
    public void loadDialog(JFXDialogLayout layout, boolean btnIncluded, Button defaultBtn){
        
        stackPane.setVisible(true);
        JFXButton btn = new JFXButton(OKAY);
        btn.setDefaultButton(true);
        defaultBtn.setDefaultButton(false);
        btn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            btn.setDefaultButton(false);
            defaultBtn.setDefaultButton(true);
            Label label = (Label)layout.getHeading().get(0);
            if(label.getText().equals(PASSWORD_UPDATED))
                save.getScene().getWindow().hide();
        });
        if(btnIncluded){
            layout.setActions(btn);
        }    
        dialog = new JFXDialog(stackPane, layout , JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.show();
        
    }
    
    private boolean checkPassword(){
        
        try {
    
            try (Connection con = getConnection()) {
                String query = "SELECT password FROM user WHERE user_id = ?";
                
                PreparedStatement st;
                ResultSet rs;
                
                st = con.prepareStatement(query);
                st.setInt(1, employer.getUserID());
                rs = st.executeQuery();
                
                if (rs.next()) {
                    
                    return rs.getString("password").equals(current.getText());
                    
                }
            }
            
        }
        catch (SQLException e) {
            exceptionLayout(e, save);
            return false;
        }
        
        return false;
    
    }
    
    
    @Override
    public boolean checkInputs()
    {
        if (current.getText().trim().equals("") || newPass.getText().trim().equals("") || repeat.getText().trim().equals("")) {
            customDialog(MISSING_FIELDS, MISSING_FIELDS_MSG, INFO_SMALL, true, save);
            return false;
        }
        else if(!newPass.getText().matches("^[a-zA-Z0-9._-]{7,30}$")){
            customDialog(PASSWORD_ERROR, PASSWORD_ERROR_MSG, INFO_SMALL, true, save);
            return false;              
        }
        else if(!newPass.getText().equals(repeat.getText())){
            customDialog(PASSWORD_ERROR, PASSWORD_ERROR_MSG_2, INFO_SMALL, true, save);
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
                        customDialog(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, INFO_SMALL, true, save);
                    }
                    
                    PreparedStatement ps;
                    
                    
                    ps = con.prepareStatement("UPDATE user SET password = ? WHERE user_id = ?");
                    
                    ps.setString(1, newPass.getText());
                    ps.setInt(2, employer.getUserID());
                    
                    ps.executeUpdate();
                }
                
                customDialog(PASSWORD_UPDATED, PASSWORD_UPDATED_MSG, INFO_SMALL, true, save);

            }
            catch (NumberFormatException | SQLException e) {
                exceptionLayout(e, save);
            }
        }
            else{
                customDialog(WRONG_PASSWORD, WRONG_PASSWORD_MSG, INFO_SMALL, true, save);
            }
        }        
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        save.setOnAction(value -> {
            changePassword();
        });
    }    
    
}
