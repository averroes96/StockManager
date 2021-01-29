/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Buy;
import Data.Product;
import static Data.Product.getProductByName;
import Data.User;
import Include.Common;
import static Include.Common.AnimateField;
import static Include.Common.dateFormatter;
import static Include.Common.getConnection;
import Include.Init;
import Include.SMController;
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
import javafx.collections.FXCollections;
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
 * @author user
 */
public class UpdateBuyController extends SMController implements Initializable,Init {

    @FXML private JFXButton saveBtn;
    @FXML private Button returnBtn;
    @FXML private JFXTextField price,quantity,supplierTF;
    @FXML private JFXDatePicker date;
    @FXML private JFXTimePicker time;
    @FXML private Label priceStatus,qteStatus;
    @FXML private ChoiceBox<Product> productCB;    
    
    private Buy buy = new Buy();
    
    ObservableList<Product> prodList = FXCollections.observableArrayList();
    

    public void getAllProducts(){
        
        try {
            prodList = Product.getActiveProducts();
        }
        catch (SQLException e) {
            exceptionLayout(e, saveBtn);
        } 
        
    }     
        
    
    public void setRequirements(User user, Buy buy){
        
        this.employer = user;
        this.buy = buy;
        
        fillFields(buy);
        
    }
    
    public void fillFields(Buy buy){
        
        try {
            select(buy.getProduct());
            quantity.setText(String.valueOf(buy.getBuyQte()));
            price.setText(String.valueOf(buy.getBuyPrice()));
            supplierTF.setText(buy.getSupplier());
            date.getEditor().setText(buy.getDate());
            time.getEditor().setText(buy.getTime());
        } catch (SQLException ex) {
            exceptionLayout(ex, saveBtn);
        }

    }

    @Override
    public boolean checkInputs()
    {
        if (price.getText().trim().equals("") || quantity.getText().trim().equals("") )  {
            customDialog(bundle.getString("missing_fields"), bundle.getString("missing_fields_msg"), INFO_SMALL, true, saveBtn);
            return false;
        }
        
        try {
            Integer.parseInt(price.getText());
            if(Integer.parseInt(price.getText()) > 0){
                try{
                    Integer.parseInt(quantity.getText());                    
                    if(Integer.parseInt(quantity.getText()) > 0){
                    return true;                    
                    }
                    else{
                        customDialog(bundle.getString("invalid_qte"), bundle.getString("invalid_qte_msg"), INFO_SMALL, true, saveBtn);
                        return false;                    
                    }
                }catch (NumberFormatException e) {
                    customDialog(bundle.getString("invalid_qte"), bundle.getString("invalid_qte_msg"), INFO_SMALL, true, saveBtn);
                    return false;
                }
            }
            else{
                customDialog(bundle.getString("invalid_price"), bundle.getString("invalid_price_msg"), INFO_SMALL, true, saveBtn);
                return false;
            }
        }
        catch (NumberFormatException e) {
            customDialog(bundle.getString("invalid_price"), bundle.getString("invalid_price_msg"), INFO_SMALL, true, saveBtn);
            return false;
        }
    }

    public void updateBuy(ActionEvent event) {

        if (checkInputs()) {
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        customDialog(bundle.getString("connection_error"), bundle.getString("connection_error_msg"), INFO_SMALL, true, saveBtn);
                    }
                    
                    PreparedStatement ps;
                    Product oldProduct = getProductByName(buy.getProduct());
                    Product newProduct = productCB.getSelectionModel().getSelectedItem();
                    
                    ps = con.prepareStatement("UPDATE buy SET buy_qte = ?, buy_unit_price = ?, buy_price = ?, buy_date = concat(?,?), buy_supplier = ?, user_id = ?, prod_id = ? WHERE buy_id = ?");
                    
                    ps.setInt(7, employer.getUserID());
                    ps.setInt(1, Integer.parseInt(quantity.getText()));
                    ps.setInt(2, Integer.parseInt(price.getText()));
                    ps.setInt(3, Integer.parseInt(price.getText()) * Integer.parseInt(quantity.getText()));
                    ps.setInt(8, newProduct.getProdID());
                    ps.setString(4, date.getEditor().getText() + " ");
                    ps.setString(5, time.getEditor().getText());
                    ps.setString(6, supplierTF.getText().trim());
                    ps.setInt(9, buy.getBuyID());
                    ps.executeUpdate();
                    
                    if(oldProduct.getProdID() == newProduct.getProdID()){
                        oldProduct.onBuy(Integer.parseInt(quantity.getText()) - buy.getBuyQte(), "+", false);
                    }
                    else{
                        oldProduct.onBuy(buy.getBuyQte(), "-", true);
                        newProduct.onBuy(Integer.parseInt(quantity.getText()), "+", true);                         
                    }
                }
                customDialog(bundle.getString("buy_updated"), bundle.getString("buy_updated_msg"), INFO_SMALL, true, saveBtn);                

            }
            catch (NumberFormatException | SQLException e) {
                exceptionLayout(e, saveBtn);
            }
        }        
        
    }     
    
    @Override
    public void logOut(ActionEvent event) throws IOException {

        ((Node)event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
        AnchorPane root = (AnchorPane)loader.load();
        MainController mControl = (MainController)loader.getController();
        mControl.getEmployer(employer);
        mControl.returnMenu("buys");
        mControl.getBuyStats(date.getEditor().getText());
        mControl.getAllBuys(date.getEditor().getText());
        mControl.buyDateField.getEditor().setText(date.getEditor().getText());
        mControl.buyDateField.setValue(date.getValue());
        Common.startStage(root, (int)root.getWidth(), (int)root.getHeight());
            
    }     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        try {
            isAnimated();
        } catch (SQLException ex) {
            exceptionLayout(ex, saveBtn);
        }        
        
        getAllProducts();
               
        productCB.setItems(prodList);
        
        date.setConverter(dateFormatter());

        saveBtn.setOnAction(Action ->{
            
            updateBuy(Action);
            
        });
        
        returnBtn.setOnAction(value -> {
            try {
                logOut(value);
            } catch (IOException ex) {
                exceptionLayout(ex, saveBtn);
            }
        });

        AnimateField(price,priceStatus,"^[1-9]?[0-9]{1,7}$", isAnimated);
        AnimateField(quantity,qteStatus,"^[1-9]?[0-9]{1,7}$", isAnimated);
        
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
            if(label.getText().equals(bundle.getString("buy_updated"))){
                try {
                    saveBtn.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
                    AnchorPane root = (AnchorPane)loader.load();
                    MainController mControl = (MainController)loader.getController();
                    mControl.getEmployer(this.employer);
                    mControl.returnMenu("buys");
                    mControl.getBuyStats(date.getEditor().getText());
                    mControl.getAllBuys(date.getEditor().getText());
                    mControl.buyDateField.getEditor().setText(date.getEditor().getText());
                    mControl.buyDateField.setValue(date.getValue());
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
