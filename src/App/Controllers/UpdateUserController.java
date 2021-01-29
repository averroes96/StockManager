
package App.Controllers;

import Data.User;
import Include.Common;
import static Include.Common.AnimateField;
import static Include.Common.controlDigitField;
import static Include.Common.getConnection;
import Include.Init;
import static Include.Init.IMAGES_PATH;
import Include.SMController;
import animatefx.animation.BounceIn;
import animatefx.animation.Tada;
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
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
/**
 * FXML Controller class
 *
 * @author med
 */
public class UpdateUserController extends SMController implements Initializable,Init {

    @FXML private Button returnBtn;
    @FXML private JFXTextField fullname,phone;
    @FXML private Label fullnameStatus,phoneStatus;
    @FXML private JFXCheckBox products,users,sells,buys;
    @FXML private JFXButton saveBtn;
    @FXML private VBox privs;
    @FXML private JFXToggleButton admin ;
    @FXML private Circle userIV;
    
    String currentImage = "";
    
    User selectedEmployer = new User();
    
    File selectedFile = null;
   

    public void getInfo(User employer, User selected){
        
        this.employer = employer;
        this.selectedEmployer = selected;
        
        if(employer.getAdmin() == 0){
            privs.setDisable(true);
            Tooltip.install(
                    privs, 
                    new Tooltip(bundle.getString("not_admin_msg")));            
        }
    }
    
    
    public void fillFields(User selectedEmployer){       
        
        fullname.setText(selectedEmployer.getFullname());
        
        phone.setText(selectedEmployer.getPhone());
        
        if (selectedEmployer.getImage().equals("")) {
            userIV.setFill(new ImagePattern(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/user.png"),
                    64, 64, false, false)));
        }
        else {
            userIV.setFill(new ImagePattern(new Image(
                    new File(selectedEmployer.getImage()).toURI().toString(),
                    userIV.getCenterX(), userIV.getCenterY(), false, false)));
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
       
        selectedFile = fileChooser.showOpenDialog(saveBtn.getScene().getWindow());
       
        if (selectedFile != null) {
            
            try {
               
                String createImagePath = Common.saveSelectedImage(selectedFile);
                
                currentImage = selectedEmployer.getImage();

                selectedEmployer.setImage(createImagePath);

                userIV.setFill(new ImagePattern(new Image(
                        selectedFile.toURI().toString(),
                        userIV.getCenterX(), userIV.getCenterY(), false, false)));
                animateNode(new BounceIn(userIV)); 

            }
            catch (IOException ex) {
                exceptionLayout(ex, saveBtn);
            }
        }

    }
    
    @Override
    public boolean checkInputs()
    {
        if (fullname.getText().trim().equals("")){
            customDialog(bundle.getString("missing_fields"), bundle.getString("missing_fields_msg"), ERROR_SMALL, true, saveBtn);
            return false;
        }
        else if(!fullname.getText().matches("^[\\p{L} .'-]+$")){
            customDialog(bundle.getString("invalid_name"), bundle.getString("invalid_name_msg"), ERROR_SMALL, true, saveBtn);
            return false;              
        }
        else if(!phone.getText().trim().matches("^[5-7]?[0-9]{10}$") && !phone.getText().equals("")){
            customDialog(bundle.getString("invalid_phone"), bundle.getString("invalid_phone"), ERROR_SMALL, true, saveBtn);
            return false;              
        }        
       
        return true;
    }    

    public void updateEmployer(ActionEvent event){

        if (checkInputs()) {
            try {
                if(!admin.isSelected() && User.isLastAdmin() && selectedEmployer.getAdmin() == 1){
                    customDialog(bundle.getString("last_admin"), bundle.getString("last_admin_msg"), INFO_SMALL, true, saveBtn);
                }
                else{
                    try {
                        
                        try (Connection con = getConnection()) {
                            if(con == null) { 
                                customDialog(bundle.getString("connection_error"), bundle.getString("connection_error_msg"), ERROR_SMALL, true, saveBtn);
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
                            ps.setString(4, selectedEmployer.getImage());
                            ps.setString(3, fullname.getText());
                            ps.setInt(5, selectedEmployer.getUserID());
                            ps.executeUpdate();
                            
                            ps = con.prepareStatement("UPDATE privs SET manage_products = ?, manage_users = ?, manage_buys = ?, manage_sells = ? WHERE user_id = ?");
                            
                            ps.setInt(5, this.selectedEmployer.getUserID());
                            ps.setInt(1, products.isSelected()? 1 : 0);
                            ps.setInt(2, users.isSelected()? 1 : 0);
                            ps.setInt(3, buys.isSelected()? 1 : 0);
                            ps.setInt(4, sells.isSelected()? 1 : 0);
                            
                            ps.executeUpdate();
                            
                            if(products.isSelected() || buys.isSelected() || sells.isSelected() || users.isSelected()){
                                selectedEmployer.activate();
                            }
                            if(!products.isSelected() && !buys.isSelected() && !sells.isSelected() && !users.isSelected())
                            {
                                selectedEmployer.unauthorize();
                            }
                            
                            con.close();
                        }
                        
                        if(!currentImage.equals(selectedEmployer.getImage())){
                            Common.deleteImage(currentImage);
                        }
                        
                        customDialog(bundle.getString("user_updated"), bundle.getString("user_updated_msg"), INFO_SMALL, true, saveBtn);
                        
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        if(employer.getUsername().equals(selectedEmployer.getUsername())){
                            mControl.getEmployer(selectedEmployer);
                        }
                        else{
                            mControl.getEmployer(employer);
                        }
                        mControl.returnMenu("employers");
                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Common.startStage(root, (int)root.getWidth(), (int)root.getHeight());
                        
                    }
                    catch (IOException | NumberFormatException | SQLException e) {
                        exceptionLayout(e, saveBtn);
                    }
                }
            } catch (SQLException ex) {
                exceptionLayout(ex, saveBtn);
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
        
        fillFields(selectedEmployer);
        
        controlDigitField(phone);
        
        userIV.setOnMouseClicked(Action -> {
            updateImage();
        });
        
        admin.setOnAction(Action ->{
            adminAction();
        });        
        
        saveBtn.setOnAction(Action -> {
            updateEmployer(Action);
        });
        
        returnBtn.setOnAction(Action -> {
            
            try {
                ((Node)Action.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                MainController mControl = (MainController)loader.getController();
                mControl.getEmployer(employer);
                mControl.returnMenu("employers");
                mControl.showEmployer(selectedEmployer.getUsername());
                Common.startStage(root, (int)root.getWidth(), (int)root.getHeight());
                
            } catch (IOException ex) {
                exceptionLayout(ex, saveBtn);
            }
        });
        
        AnimateField(fullname, fullnameStatus, "^[\\p{L} .'-]+$", isAnimated);
        AnimateField(phone, phoneStatus, "^[5-7]?[0-9]{10}$", isAnimated);
        

    }    

    private void adminAction() {
        
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
