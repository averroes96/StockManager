
package sANDb;

import Data.Employer;
import Include.Common;
import static Include.Common.adminsCount;
import static Include.Common.getConnection;
import static Include.Common.minimize;
import Include.SpecialAlert;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Include.Init;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
/**
 * FXML Controller class
 *
 * @author med
 */
public class UpdateEmployerController implements Initializable,Init {

    @FXML Button update,save,cancel;
    @FXML TextField fullname,phone;
    @FXML Label image,min,fullnameStatus,phoneStatus;
    @FXML CheckBox admin,products,users,sells,buys;
    @FXML VBox privs;
    
    SpecialAlert alert = new SpecialAlert();
    
    Employer employer = new Employer();
    
    Employer selectedEmployer = new Employer();
    
    File selectedFile = null;
    
    private double xOffset = 0;
    private double yOffset = 0;    
   

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

                Common.deleteImage(selectedEmployer.getImage());

                selectedEmployer.setImage(createImagePath);

                image.setText("");
                image.setGraphic(new ImageView(new Image(
                    selectedFile.toURI().toString(), 224, 220, true, true)));

            }
            catch (Exception ex) {
                alert.show(UNKNOWN_ERROR, UPLOAD_IMAGE_FAILED, Alert.AlertType.ERROR,true);
            }
        }

    }
    
    private boolean checkInputs()
    {
        if (fullname.getText().trim().equals("")){
            alert.show(MISSING_FIELDS, MISSING_FIELDS_MSG, Alert.AlertType.WARNING,false);
            return false;
        }
        else if(!fullname.getText().matches("^[\\p{L} .'-]+$")){
            alert.show(UNVALID_NAME, UNVALID_NAME_MSG, Alert.AlertType.WARNING,false);
            return false;              
        }
        else if(!phone.getText().trim().matches("^[5-7]?[0-9]{10}$") && !phone.getText().equals("")){
            alert.show(UNVALID_PHONE, UNVALID_PHONE_MSG, Alert.AlertType.WARNING,false);
            return false;              
        }        
       
        return true;
    }    

    public void updateEmployer(ActionEvent event) {

        if (checkInputs()) {
            if(!admin.isSelected() && adminsCount() == 1 && selectedEmployer.getAdmin() == 1){
            
                alert.show(LAST_ADMIN, "لا يمكن أخذ صلاحيات الأدمن من هذا الحساب لأنه الأدمن الوحيد المتبقي", Alert.AlertType.INFORMATION,false);                            
                
            }
            else{
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        alert.show(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, Alert.AlertType.ERROR,true);
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

                alert.show(EMPLOYER_UPDATED, EMPLOYER_UPDATED_MSG, Alert.AlertType.INFORMATION,false);
                

                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
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
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource("Layout/custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource("Layout/buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.show();                
                        ((Node)event.getSource()).getScene().getWindow().hide();                
                


            }
            catch (IOException | NumberFormatException | SQLException e) {
                alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            }
            }

        }        
        
    }      
  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        fillFields(selectedEmployer);
        
        update.setOnAction(Action -> {
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
            updateEmployer(Action);
        });
        
        cancel.setOnAction(Action -> {
            try {
                ((Node)Action.getSource()).getScene().getWindow().hide();
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                AnchorPane root = (AnchorPane)loader.load();
                MainController mControl = (MainController)loader.getController();
                mControl.getEmployer(employer);
                mControl.returnMenu("employers");
                Scene scene = new Scene(root);
                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                stage.initStyle(StageStyle.TRANSPARENT);
                scene.getStylesheets().add(getClass().getResource("Layout/custom.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("Layout/buttons.css").toExternalForm());
                stage.setScene(scene);  
                stage.show();
                        root.setOnMousePressed((MouseEvent event) -> {
                            xOffset = event.getSceneX();
                            yOffset = event.getSceneY();
                });
                        root.setOnMouseDragged((MouseEvent event) -> {
                            stage.setX(event.getScreenX() - xOffset);
                            stage.setY(event.getScreenY() - yOffset);
                });                
            } catch (IOException ex) {
                alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
            }
        });

        min.setOnMouseClicked(Action ->{
        
            minimize(Action);
        
        });
        
        fullname.setOnKeyPressed(Action -> {
            
        if (fullname.getText().equals("") || !fullname.getText().matches("^[\\p{L} .'-]+$")) {
            fullnameStatus.setVisible(true);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            fullnameStatus.setVisible(false);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }
            
        });
        fullname.setOnKeyTyped(Action -> {
            
        if (fullname.getText().equals("") || !fullname.getText().matches("^[\\p{L} .'-]+$")) {
            fullnameStatus.setVisible(true);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            fullnameStatus.setVisible(false);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }        
            
        });
        fullname.setOnKeyReleased(Action -> {
            
        if (fullname.getText().equals("") || !fullname.getText().matches("^[\\p{L} .'-]+$")) {
            fullnameStatus.setVisible(true);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }        
        else{
            fullnameStatus.setVisible(false);
            fullname.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }            
        });


        phone.setOnKeyPressed(Action -> {
            
        if (!phone.getText().matches("^[5-7]?[0-9]{10}$")) {
            phoneStatus.setVisible(true);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            phoneStatus.setVisible(false);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }
            
        });
        phone.setOnKeyTyped(Action -> {
            
        if (!phone.getText().matches("^[5-7]?[0-9]{10}$")) {
            phoneStatus.setVisible(true);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            phoneStatus.setVisible(false);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }      
            
        });
        phone.setOnKeyReleased(Action -> {
            
        if (!phone.getText().matches("^[5-7]?[0-9]{10}")) {
            phoneStatus.setVisible(true);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:red;-fx-padding:0 40 0 0");
        }
        else{
            phoneStatus.setVisible(false);
            phone.setStyle("-fx-border-width: 2; -fx-border-color:green;-fx-padding:0 40 0 0");
        }           
        });
                 
                


    }    


    
}
