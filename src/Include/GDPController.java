
package Include;

import Data.User;
import static Include.Common.initLayout;
import static Include.Init.ERROR_SMALL;
import static Include.Init.OKAY;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 *
 * @author user
 */
public abstract class GDPController {
    
    @FXML public StackPane stackPane;
    @FXML public JFXDialog dialog;
    
    public User employer = new User();
    public ResourceBundle bundle;
    
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
        });
        if(btnIncluded){
            layout.setActions(btn);
        }    
        dialog = new JFXDialog(stackPane, layout , JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.show();
        
    }
    
    public void exceptionLayout(Exception e, Button defaultBtn){
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, bundle.getString("unknown_error"), e.getLocalizedMessage(), ERROR_SMALL);
            
            loadDialog(layout, true, defaultBtn);
    }

    public void customDialog(String title, String body, String icon, boolean btnIncluded, Button defaultBtn){
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, title, body, icon);
            
            loadDialog(layout, btnIncluded, defaultBtn);
    }
    
    public boolean checkInputs(){
        return false;
    }
    
    public void logOut(ActionEvent event)throws IOException{
        
    };
}
