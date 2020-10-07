/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.User;
import Include.GDPController;
import Include.Init;
import static Include.Init.OKAY;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import java.net.URL;
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
    
    @FXML private JFXPasswordField current,newPass,repeat;
    @FXML private JFXButton save;
    
    private static int TRY_COUNT = 3;
            
    public void getEmployer(User emp){
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
    
    
    @Override
    public boolean checkInputs()
    {
        if (current.getText().trim().equals("") || newPass.getText().trim().equals("") || repeat.getText().trim().equals("")) {
            customDialog(bundle.getString("missing_fields"), bundle.getString("missing_fields_msg"), ERROR_SMALL, true, save);
            return false;
        }
        else if(!newPass.getText().matches("^[a-zA-Z0-9._-]{7,30}$")){
            customDialog(bundle.getString("wrong_password"), bundle.getString("password_error_msg"), ERROR_SMALL, true, save);
            return false;              
        }
        else if(!newPass.getText().equals(repeat.getText())){
            customDialog(bundle.getString("wrong_password"), bundle.getString("passwords_do_not_match"), ERROR_SMALL, true, save);
            return false;           
        }
        
        return true;
    }
    
    public void changePassword(){
        
        if (checkInputs()) {
            try {      
                if(employer.checkPassword(current.getText())){
                    
                    employer.changePassword(newPass.getText());
                    customDialog(bundle.getString("password_updated"), bundle.getString("password_updated_msg"), INFO_SMALL, true, save);
                
                }
                else{
                    customDialog(bundle.getString("wrong_password"), bundle.getString("wrong_password_msg"), ERROR_SMALL, true, save);
                    TRY_COUNT--;
                    System.out.println(TRY_COUNT);
                }
            }
            catch (NumberFormatException | SQLException e) {
                exceptionLayout(e, save);
            }
        }        
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        save.setOnAction(value -> {
            changePassword();
        });
        
    }    
    
}
