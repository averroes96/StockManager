
package App.Controllers;

import Data.Product;
import Data.Sell;
import Data.User;
import Include.Common;
import static Include.Common.AnimateField;
import static Include.Common.dateFormatter;
import static Include.Common.getConnection;
import static Include.Common.getDate;
import Include.CommonMethods;
import Include.SMController;
import Include.Init;
import static Include.Init.FXMLS_PATH;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author med
 */
public class UpdateSellController extends SMController implements Initializable,Init,CommonMethods {

    
    @FXML private JFXButton saveButton;
    @FXML private Button returnBtn;
    @FXML private JFXTextField price,quantity;
    @FXML private JFXDatePicker date;
    @FXML private JFXTimePicker time;
    @FXML private Label priceStatus,qteStatus;
    @FXML private ChoiceBox<Product> productCB;
    
    ObservableList<Product> prodList = null;
    
    Sell sell = new Sell();        

    public void fillFields(Sell selectedSell){
        
        try {
            select(sell.getProduct().getName());
            quantity.setText(String.valueOf(selectedSell.getSellQuantity()));
            price.setText(String.valueOf(selectedSell.getTotalPrice() / selectedSell.getSellQuantity()));
            date.getEditor().setText(getDate(selectedSell.getSellID(),"sell"));
            time.getEditor().setText(sell.getTime());
        } catch (SQLException ex) {
            exceptionLayout(ex, saveButton);
        }

    }
    
    @Override
    public void logOut(ActionEvent event) throws IOException {

        ((Node)event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
        AnchorPane root = (AnchorPane)loader.load();
        MainController mControl = (MainController)loader.getController();
        mControl.getEmployer(employer);
        mControl.returnMenu("sells");
        mControl.getSellStats(date.getEditor().getText());
        mControl.getAllSells(date.getEditor().getText());
        mControl.sellDateField.getEditor().setText(date.getEditor().getText());
        mControl.sellDateField.setValue(date.getValue());
        Common.startStage(root, (int)root.getWidth(), (int)root.getHeight());
        
    }    
    
    
    public void getData(User employer,Sell sell){
        
        this.employer = employer;
        this.sell = sell;
        
        fillFields(sell);        
    }
    
    public void getAllProducts(){
        
        try {
            prodList = Product.getActiveProducts();
        }
        catch (SQLException e) {
            exceptionLayout(e, saveButton);
        } 
        
    }
        

    @Override
    public boolean checkInputs()
    {

        Product oldProduct = sell.getProduct();
        Product newProduct = productCB.getValue();

        if (price.getText().trim().equals("") || quantity.getText().trim().equals("") )  {
            customDialog(bundle.getString("missing_fields"), bundle.getString("missing_fields_msg"), ERROR_SMALL, true, saveButton);
            return false;
        }
        if(quantity.getText().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(quantity.getText()) > 0){
            if(oldProduct.getName().equals(newProduct.getName())){
                if(oldProduct.getProdQuantity() + this.sell.getSellQuantity() - Integer.parseInt(quantity.getText())  >= 0){
                    if(price.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(price.getText()) > 0)
                        return true;
                    else{
                        customDialog(bundle.getString("invalid_price"), bundle.getString("invalid_price_msg"), ERROR_SMALL, true, saveButton);
                        return false;
                    }                     
                }
                else{
                    customDialog(bundle.getString("not_enough_qte"), bundle.getString("not_enough_qte_msg"), INFO_SMALL, true, saveButton);
                    return false;                    
                }
            }
            else{
                if(newProduct.getProdQuantity() - Integer.parseInt(quantity.getText())  >= 0){
                    if(price.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(price.getText()) > 0)
                        return true;
                    else{
                        customDialog(bundle.getString("invalid_price"), bundle.getString("invalid_price_msg"), ERROR_SMALL, true, saveButton);
                        return false;
                    }                     
                }
                else{
                    customDialog(bundle.getString("not_enough_qte"), bundle.getString("not_enough_qte_msg"), INFO_SMALL, true, saveButton);
                    return false;
                }                    
            }

        }
        else{
            customDialog(bundle.getString("invalid_qte"), bundle.getString("invalid_qte_msg"), INFO_SMALL, true, saveButton);
            return false;
        }        
        
    }
           
        
    public void updateSell(ActionEvent event) {

        if (checkInputs()) {
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        customDialog(bundle.getString("connection_error"), bundle.getString("connection_error_msg"), ERROR_SMALL, true, saveButton);
                    }
                    Product oldProduct = sell.getProduct();
                    Product newProduct = productCB.getValue();
                    PreparedStatement ps;
                    
                    ps = con.prepareStatement("UPDATE sell SET sell_quantity = ?, sell_price_unit = ?, sell_price = ?, sell_date = concat(?,?), user_id = ?, prod_id = ? WHERE sell_id = ?");
                    
                    ps.setInt(6, employer.getUserID());
                    ps.setInt(1, Integer.parseInt(quantity.getText()));
                    ps.setInt(2, Integer.parseInt(price.getText()));
                    ps.setInt(3, Integer.parseInt(price.getText()) * Integer.parseInt(quantity.getText()));
                    ps.setInt(7, newProduct.getProdID());
                    ps.setString(4, date.getEditor().getText() + " ");
                    ps.setString(5, time.getEditor().getText());
                    ps.setInt(8, this.sell.getSellID());
                    ps.executeUpdate();
                    
                    if(oldProduct.getProdID() == newProduct.getProdID()){
                        oldProduct.onSell(Integer.parseInt(quantity.getText()) - sell.getSellQuantity(), "-", false);
                    }
                    else{
                        oldProduct.onSell(sell.getSellQuantity(), "+", true);
                        newProduct.onSell(Integer.parseInt(quantity.getText()), "-", true);                        
                    }
                }
                
                customDialog(bundle.getString("sell_updated"), bundle.getString("sell_updated_msg"), INFO_SMALL, true, saveButton);                               
                
            }
            catch (NumberFormatException | SQLException e) {
                exceptionLayout(e, saveButton);
            }
        }        
        
    }        

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        getAllProducts();
               
        productCB.setItems(prodList);
        
        productCB.setOnAction(event -> {
            
            try {
                if(productCB.getValue().isEmpty()){
                    customDialog(bundle.getString("zero_quantity"), bundle.getString("zero_quantity_msg"), INFO_SMALL, true, saveButton);
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
        JFXButton btn = new JFXButton(bundle.getString("okay"));
        btn.setDefaultButton(true);
        defaultBtn.setDefaultButton(false);
        btn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            btn.setDefaultButton(false);
            defaultBtn.setDefaultButton(true);
            Label label = (Label)layout.getHeading().get(0);
            if(label.getText().equals(bundle.getString("sell_updated"))){
                try {
                    saveButton.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
                    AnchorPane root = (AnchorPane)loader.load();
                    MainController mControl = (MainController)loader.getController();
                    mControl.getEmployer(this.employer);
                    mControl.returnMenu("sells");
                    mControl.getSellStats(date.getEditor().getText());
                    mControl.getAllSells(date.getEditor().getText());
                    mControl.sellDateField.getEditor().setText(date.getEditor().getText());
                    mControl.sellDateField.setValue(date.getValue());
                    Common.startStage(root, (int)root.getWidth(), (int)root.getHeight());
                } catch (IOException ex) {
                    exceptionLayout(ex, defaultBtn);
                }
            }
        });
        if(btnIncluded){
            layout.setActions(btn);
        }    
        dialog = new JFXDialog(stackPane, layout , JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.show();
        
    }
    
    private void select(String name){
        
        productCB.getItems().stream().filter((product) -> (product.getName().equals(name))).forEachOrdered((product) -> {
            productCB.getSelectionModel().select(product);
        });
        
    }
    
}
