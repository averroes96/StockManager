
package App.Controllers;

import Data.Employer;
import Data.Product;
import Data.Sell;
import Include.Common;
import static Include.Common.AnimateField;
import static Include.Common.dateFormatter;
import static Include.Common.getConnection;
import static Include.Common.getDate;
import static Include.Common.getProductByName;
import static Include.Common.getQuantity;
import Include.CommonMethods;
import Include.GDPController;
import Include.Init;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author med
 */
public class UpdateSellController extends GDPController implements Initializable,Init,CommonMethods {

    
    @FXML private JFXButton saveButton;
    @FXML private Button returnBtn;
    @FXML private JFXTextField price,quantity;
    @FXML private JFXDatePicker date;
    @FXML private Label priceStatus,qteStatus;
    @FXML private ChoiceBox nameBox;
    
    ObservableList<String> nameList = null;
    
    Sell sell = new Sell();        

    public void fillFields(Sell selectedSell){
        
        try {
            nameBox.getSelectionModel().select(selectedSell.getProduct().getName());
            quantity.setText(String.valueOf(selectedSell.getSellQuantity()));
            price.setText(String.valueOf(selectedSell.getTotalPrice() / selectedSell.getSellQuantity()));
            date.getEditor().setText(getDate(selectedSell.getSellID(),"sell"));
        } catch (SQLException ex) {
            exceptionLayout(ex, saveButton);
        }

    }
    
    @Override
    public void logOut(ActionEvent event) throws IOException {

                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(employer);
                        mControl.returnMenu("sells");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.setMinHeight(700);
                        stage.setMinHeight(1000);
                        stage.show();                        
            
    }    
    
    
    public void getData(Employer employer,Sell sell){
        
        this.employer = employer;
        this.sell = sell;
        
        fillFields(sell);        
    }         
        

    @Override
    public boolean checkInputs()
    {
        
        try {
            Product oldProduct = getProductByName(this.sell.getSellName());
            Product newProduct = getProductByName(nameBox.getSelectionModel().getSelectedItem().toString());
            
            if (price.getText().trim().equals("") || quantity.getText().trim().equals("") )  {
                customDialog(MISSING_FIELDS, MISSING_FIELDS, INFO_SMALL, true, saveButton);
                return false;
            }
            if(quantity.getText().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(quantity.getText()) > 0){
                if(oldProduct.getName().equals(newProduct.getName())){
                    if(oldProduct.getProdQuantity() + this.sell.getSellQuantity() - Integer.parseInt(quantity.getText())  >= 0){
                        if(price.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(price.getText()) > 0)
                            return true;
                        else{
                            customDialog(INVALID_PRICE, INVALID_PRICE_MSG, INFO_SMALL, true, saveButton);
                            return false;
                        }                     
                    }
                    else{
                        customDialog(NOT_ENOUGH_QUANTITY, NOT_ENOUGH_QUANTITY_MSG, INFO_SMALL, true, saveButton);
                        return false;                    
                    }
                }
                else{
                    if(newProduct.getProdQuantity() - Integer.parseInt(quantity.getText())  >= 0){
                        if(price.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(price.getText()) > 0)
                            return true;
                        else{
                            customDialog(INVALID_PRICE, INVALID_PRICE_MSG, INFO_SMALL, true, saveButton);
                            return false;
                        }                     
                    }
                    else{
                        customDialog(NOT_ENOUGH_QUANTITY, NOT_ENOUGH_QUANTITY_MSG, INFO_SMALL, true, saveButton);
                        return false;
                    }                    
                }
              
            }
            else{
                customDialog(INVALID_QTE, INVALID_QTE_MSG, INFO_SMALL, true, saveButton);
                return false;
            }        
        } catch (SQLException ex) {
            exceptionLayout(ex, saveButton);
            return false;
        }
    }
           
        
    public void updateSell(ActionEvent event) {

        if (checkInputs()) {
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        customDialog(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, INFO_SMALL, true, saveButton);
                    }
                    Product oldProduct = getProductByName(this.sell.getSellName());
                    Product newProduct = getProductByName(nameBox.getSelectionModel().getSelectedItem().toString());
                    PreparedStatement ps;
                    
                    ps = con.prepareStatement("UPDATE sell SET sell_quantity = ?, sell_price_unit = ?, sell_price = ?, sell_date = concat(?,time(sell_date)), user_id = ?, prod_id = ? WHERE sell_id = ?");
                    
                    ps.setInt(5, employer.getUserID());
                    ps.setInt(1, Integer.parseInt(quantity.getText()));
                    ps.setInt(2, Integer.parseInt(price.getText()));
                    ps.setInt(3, Integer.parseInt(price.getText()) * Integer.parseInt(quantity.getText()));
                    ps.setInt(6, newProduct.getProdID());
                    ps.setString(4, date.getEditor().getText() + " ");
                    ps.setInt(7, this.sell.getSellID());
                    ps.executeUpdate();
                    
                    if(oldProduct.getProdID() == newProduct.getProdID()){
                    
                    ps = con.prepareStatement("UPDATE product SET prod_quantity = prod_quantity - ? WHERE prod_id = ?");
                    ps.setInt(1, Integer.parseInt(quantity.getText()) - this.sell.getSellQuantity());
                    ps.setInt(2, oldProduct.getProdID());
                    ps.executeUpdate();
                    
                    }
                    else{
                        ps = con.prepareStatement("UPDATE product SET prod_quantity = prod_quantity + ?, nbrSells = nbrSells - 1 WHERE prod_id = ?");
                        ps.setInt(1, this.sell.getSellQuantity());
                        ps.setInt(2, oldProduct.getProdID());
                        ps.executeUpdate();

                        ps = con.prepareStatement("UPDATE product SET prod_quantity = prod_quantity - ?, nbrSells = nbrSells + 1 WHERE prod_id = ?");
                        ps.setInt(1, Integer.parseInt(quantity.getText()));
                        ps.setInt(2, newProduct.getProdID());
                        ps.executeUpdate();                        
                    }
                }
                
                customDialog(SELL_UPDATED, SELL_UPDATED_MSG, INFO_SMALL, true, saveButton);                

                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(this.employer);
                        mControl.returnMenu("sells");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        //stage.initStyle(StageStyle.TRANSPARENT);                    
                        stage.setScene(scene);
                        stage.setMinHeight(700);
                        stage.setMinWidth(1000);                      
                        stage.show();                
                        ((Node)event.getSource()).getScene().getWindow().hide();                
                
            }
            catch (IOException | NumberFormatException | SQLException e) {
                exceptionLayout(e, saveButton);
            }
        }        
        
    }        

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
               
        nameBox.setItems(nameList);
        
        nameBox.setOnAction(event -> {
            
            try {
                if(getQuantity(nameBox.getSelectionModel().getSelectedItem().toString()) == 0){
                    customDialog(ZERO_QTE, ZERO_QTE_MSG, INFO_SMALL, true, saveButton);
                }
            } catch (SQLException ex) {
                exceptionLayout(ex, saveButton);
            }
            
        });

        saveButton.setOnAction(Action -> {
            updateSell(Action);
        });
        
        returnBtn.setOnAction(value -> {
            try {
                logOut(value);
            } catch (IOException ex) {
                exceptionLayout(ex, saveButton);
            }
        });
        
        date.setConverter(dateFormatter());
        
        AnimateField(price,priceStatus,"^[1-9]?[0-9]{1,7}$");
        AnimateField(quantity,qteStatus,"^[1-9]?[0-9]{1,7}$");
        
        Common.controlDigitField(price);
        Common.controlDigitField(quantity);
        
        
            
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
            if(label.getText().equals(SELL_UPDATED))
                saveButton.getScene().getWindow().hide();
        });
        if(btnIncluded){
            layout.setActions(btn);
        }    
        dialog = new JFXDialog(stackPane, layout , JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.show();
        
    }
    
}
