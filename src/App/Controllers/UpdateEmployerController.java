
package App.Controllers;

import Data.Employer;
import Include.Common;
import static Include.Common.AnimateField;
import static Include.Common.adminsCount;
import static Include.Common.controlDigitField;
import static Include.Common.getConnection;
import Include.GDPController;
import Include.Init;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
/**
 * FXML Controller class
 *
 * @author med
 */
public class UpdateEmployerController extends GDPController implements Initializable,Init {

    @FXML Button cancel;
    @FXML JFXTextField fullname,phone;
    @FXML Label image,fullnameStatus,phoneStatus;
    @FXML JFXCheckBox products,users,sells,buys;
    @FXML JFXButton save;
    @FXML VBox privs;
    @FXML JFXToggleButton admin ;
    
    String currentImage = "";
    
    Employer selectedEmployer = new Employer();
    
    File selectedFile = null;
   

    public void getInfo(Employer employer, Employer selected){
        
        this.employer = employer;
        this.selectedEmployer = selected;
        
        if(employer.getAdmin() == 0){
            privs.setDisable(true);
            Tooltip.install(
                    privs, 
                    new Tooltip("لا يمكنك تعديل الصلاحيات لأن حسابك ليس أدمن"));            
        }
    }
    
    public void fillFields(Employer selectedEmployer){       
        
        fullname.setText(selectedEmployer.getFullname());
        
        phone.setText(selectedEmployer.getPhone());
        
        if (selectedEmployer.getImage().equals("")) {
            image.setGraphic(null);
            image.setText(NO_IMAGE_FOUND);

        }
        else {
            image.setText("");
            image.setGraphic(new ImageView(new Image(
                    new File(selectedEmployer.getImage()).toURI().toString(),
                    220, 220, true, true)));
        }

        if(selectedEmployer.getAdmin() != 1)admin.setSelected(false);
        else {
            
            admin.setSelected(true);
            products.setDisable(true);
            users.setDisable(true);
            sells.setDisable(true);
            buys.setDisable(true);
        }
        
        if(selectedEmployer.getProdPrivs() == 1 )products.setSelected(true);
        if(selectedEmployer.getUserPrivs() == 1 )users.setSelected(true);
        if(selectedEmployer.getSellPrivs() == 1 )sells.setSelected(true);
        if(selectedEmployer.getBuyPrivs() == 1 )buys.setSelected(true);

    }

    public void updateImage()
    {

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Select a .JPG .PNG .GIF image", "*.jpg", "*.png", "*.gif")
        );

       
        selectedFile = fileChooser.showOpenDialog(null);

       
        if (selectedFile != null) {
            
            try {
               
                String createImagePath = Common.saveSelectedImage(selectedFile);
                
                currentImage = selectedEmployer.getImage();

                selectedEmployer.setImage(createImagePath);

                image.setText("");
                image.setGraphic(new ImageView(new Image(
                    selectedFile.toURI().toString(), 220, 220, true, true)));

            }
            catch (Exception ex) {
                exceptionLayout(ex, save);
            }
        }

    }
    
    @Override
    public boolean checkInputs()
    {
        if (fullname.getText().trim().equals("")){
            customDialog(MISSING_FIELDS, MISSING_FIELDS_MSG, ERROR_SMALL, true, save);
            return false;
        }
        else if(!fullname.getText().matches("^[\\p{L} .'-]+$")){
            customDialog(UNVALID_NAME, UNVALID_NAME_MSG, ERROR_SMALL, true, save);
            return false;              
        }
        else if(!phone.getText().trim().matches("^[5-7]?[0-9]{10}$") && !phone.getText().equals("")){
            customDialog(UNVALID_PHONE, UNVALID_PHONE_MSG, ERROR_SMALL, true, save);
            return false;              
        }        
       
        return true;
    }    

    public void updateEmployer(ActionEvent event) throws SQLException {

        if (checkInputs()) {
            if(!admin.isSelected() && adminsCount() == 1 && selectedEmployer.getAdmin() == 1){
                customDialog(LAST_ADMIN, LAST_ADMIN_MSG, INFO_SMALL, true, save);                
            }
            else{
                try {

                    try (Connection con = getConnection()) {
                        if(con == null) {
                            customDialog(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, ERROR_SMALL, true, save); 
                        }

                        PreparedStatement ps;


                        ps = con.prepareStatement("UPDATE user SET telephone = ?, admin = ?, fullname = ?, image = ? WHERE user_id = ?");

                        ps.setString(1, phone.getText());
                        if(admin.isSelected()){
                            ps.setInt(2, 1);
                            selectedEmployer.setAdmin(1);
                        }
                        else{
                            ps.setInt(2, 0);
                            selectedEmployer.setAdmin(0);
                        }
                        ps.setString(4, this.selectedEmployer.getImage());
                        ps.setString(3, fullname.getText());
                        ps.setInt(5, this.selectedEmployer.getUserID());
                        ps.executeUpdate();

                                ps = con.prepareStatement("UPDATE privs SET manage_products = ?, manage_users = ?, manage_buys = ?, manage_sells = ? WHERE user_id = ?");

                                ps.setInt(5, this.selectedEmployer.getUserID());
                                ps.setInt(1, products.isSelected()? 1 : 0);
                                ps.setInt(2, users.isSelected()? 1 : 0);
                                ps.setInt(3, buys.isSelected()? 1 : 0);
                                ps.setInt(4, sells.isSelected()? 1 : 0);

                                ps.executeUpdate();

                                if(products.isSelected() || buys.isSelected() || sells.isSelected() || users.isSelected()){
                                    ps = con.prepareStatement("UPDATE user SET active = 1 WHERE user_id = ?");
                                    ps.setInt(1, this.selectedEmployer.getUserID());

                                    ps.executeUpdate();
                                }
                                if(!products.isSelected() && !buys.isSelected() && !sells.isSelected() && !users.isSelected())
                                {
                                    ps = con.prepareStatement("UPDATE user SET active = 2 WHERE user_id = ?");
                                    ps.setInt(1, this.selectedEmployer.getUserID());

                                    ps.executeUpdate();
                                }                            

                                con.close();
                    }

                    if(!currentImage.equals(selectedEmployer.getImage())){
                        Common.deleteImage(currentImage);
                    }

                    customDialog(EMPLOYER_UPDATED, EMPLOYER_UPDATED_MSG, INFO_SMALL, true, save);                

                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"));
                            AnchorPane root = (AnchorPane)loader.load();
                            MainController mControl = (MainController)loader.getController();
                            if(employer.getUsername().equals(selectedEmployer.getUsername())){
                                mControl.getEmployer(selectedEmployer);
                            }
                            else{
                                mControl.getEmployer(employer);    
                            }
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
                            ((Node)event.getSource()).getScene().getWindow().hide();                



                }
                catch (IOException | NumberFormatException | SQLException e) {
                    exceptionLayout(e, save);
                }
            }

        }        
        
    }      
  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        fillFields(selectedEmployer);
        
        controlDigitField(phone);
        
        image.setOnMouseClicked(Action -> {
            updateImage();
        });
        
        admin.setOnAction(Action ->{
            
        if(admin.isSelected()){
            
            products.setSelected(true);
            products.setDisable(true);
            
            users.setSelected(true);
            users.setDisable(true);
            
            sells.setSelected(true);
            sells.setDisable(true);
            
            buys.setSelected(true);
            buys.setDisable(true);
            
        }
        else{
            
            products.setDisable(false);
            users.setDisable(false);
            sells.setDisable(false);
            buys.setDisable(false);
            
        }            
            
        });        
        
        save.setOnAction(Action -> {
            try {
                updateEmployer(Action);
            } catch (SQLException ex) {
                exceptionLayout(ex, save);
            }
        });
        
        cancel.setOnAction(Action -> {
            try {
                ((Node)Action.getSource()).getScene().getWindow().hide();
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"));
                AnchorPane root = (AnchorPane)loader.load();
                MainController mControl = (MainController)loader.getController();
                mControl.getEmployer(employer);
                mControl.returnMenu("employers");
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());
                stage.setScene(scene);
                stage.setMinHeight(700);
                stage.setMinWidth(1000);
                stage.show();
            } catch (IOException ex) {
                exceptionLayout(ex, save);
            }
        });
        
        AnimateField(fullname,fullnameStatus,"^[\\p{L} .'-]+$");
        
        AnimateField(phone,phoneStatus,"^[5-7]?[0-9]{10}$"); 

    }    


    
}
