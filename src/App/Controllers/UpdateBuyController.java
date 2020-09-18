/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Buy;
import Data.Employer;
import Data.Product;
import Include.Common;
import static Include.Common.AnimateField;
import static Include.Common.dateFormatter;
import static Include.Common.getConnection;
import static Include.Common.getDate;
import static Include.Common.getProductByName;
import Include.GDPController;
import Include.Init;
import animatefx.animation.Swing;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
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

/**
 * FXML Controller class
 *
 * @author user
 */
public class UpdateBuyController extends GDPController implements Initializable,Init {

    @FXML private JFXButton saveButton;
    @FXML private Button returnBtn;
    @FXML private JFXTextField price,quantity;
    @FXML private JFXDatePicker date;
    @FXML private Label priceStatus,qteStatus;
    @FXML private ChoiceBox nameBox;    
    
    private Buy buy = new Buy();
    
    ObservableList<String> nameList = FXCollections.observableArrayList();

    public void getAllProducts(){
        
        Connection con = getConnection();
        
        String query = "SELECT name FROM product WHERE on_hold = 0 ORDER BY prod_id ASC";

        Statement st;
        ResultSet rs;
        

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {

                nameList.add(rs.getString("name"));
            }

            con.close();
        }
        catch (SQLException e) {
            exceptionLayout(e, saveButton);
        } 
        
    }      
        
    
    public void setRequirements(Employer employer, Buy buy){
        
        this.employer = employer;
        this.buy = buy;
        
        fillFields(buy);
        
    }
    
    public void fillFields(Buy buy){
        
        nameBox.getSelectionModel().select(buy.getProduct()); 
        quantity.setText(String.valueOf(buy.getBuyQte()));
        price.setText(String.valueOf(buy.getBuyPrice()));
        date.getEditor().setText(getDate(buy.getBuyID(), "buy"));

    }

    @Override
    public boolean checkInputs()
    {
        if (price.getText().trim().equals("") || quantity.getText().trim().equals("") )  {
            customDialog(MISSING_FIELDS, MISSING_FIELDS_MSG, INFO_SMALL, true, saveButton);
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
                        customDialog(INVALID_QTE, INVALID_QTE_MSG, INFO_SMALL, true, saveButton);
                        return false;                    
                    }
                }catch (NumberFormatException e) {
                    customDialog(INVALID_QTE, INVALID_QTE_MSG, INFO_SMALL, true, saveButton);
                    return false;
                }
            }
            else{
                customDialog(INVALID_PRICE, INVALID_PRICE_MSG, INFO_SMALL, true, saveButton);
                return false;
            }
        }
        catch (NumberFormatException e) {
            customDialog(INVALID_PRICE, INVALID_PRICE_MSG, INFO_SMALL, true, saveButton);
            return false;
        }
    }

    public void updateBuy(ActionEvent event) {

        if (checkInputs()) {
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        customDialog(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, INFO_SMALL, true, saveButton);
                    }
                    
                    PreparedStatement ps;
                    Product oldProduct = getProductByName(this.buy.getProduct());
                    Product newProduct = getProductByName(nameBox.getSelectionModel().getSelectedItem().toString());
                    
                    ps = con.prepareStatement("UPDATE buy SET buy_qte = ?, buy_unit_price = ?, buy_price = ?, buy_date = concat(?,time(buy_date)), user_id = ?, prod_id = ? WHERE buy_id = ?");
                    
                    ps.setInt(5, employer.getUserID());
                    ps.setInt(1, Integer.parseInt(quantity.getText()));
                    ps.setInt(2, Integer.parseInt(price.getText()));
                    ps.setInt(3, Integer.parseInt(price.getText()) * Integer.parseInt(quantity.getText()));
                    ps.setInt(6, newProduct.getProdID());
                    ps.setString(4, date.getEditor().getText() + " ");
                    ps.setInt(7, this.buy.getBuyID());
                    ps.executeUpdate();
                    
                    if(oldProduct.getProdID() == newProduct.getProdID()){
                    
                        ps = con.prepareStatement("UPDATE product SET prod_quantity = prod_quantity + ? WHERE prod_id = ?");
                        ps.setInt(1, Integer.parseInt(quantity.getText()) - this.buy.getBuyQte());
                        ps.setInt(2, oldProduct.getProdID());
                        ps.executeUpdate();
                    
                    }
                    else{
                        ps = con.prepareStatement("UPDATE product SET prod_quantity = prod_quantity - ?, nbrBuys = nbrBuys - 1 WHERE prod_id = ?");
                        ps.setInt(1, this.buy.getBuyQte());
                        ps.setInt(2, oldProduct.getProdID());
                        ps.executeUpdate();

                        ps = con.prepareStatement("UPDATE product SET prod_quantity = prod_quantity + ?, nbrBuys = nbrBuys + 1 WHERE prod_id = ?");
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
                        mControl.returnMenu("buys");
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
    
    public void logOut(ActionEvent event) throws IOException {

                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(employer);
                        mControl.returnMenu("buys");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.setMinHeight(700);
                        stage.setMinWidth(1000);
                        stage.show();            
            
    }     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        getAllProducts();
               
        nameBox.setItems(nameList);
        
        date.setConverter(dateFormatter());

        saveButton.setOnAction(Action ->{
            
            updateBuy(Action);
            
        });
        
        returnBtn.setOnAction(value -> {
            try {
                logOut(value);
            } catch (IOException ex) {
                exceptionLayout(ex, saveButton);
            }
        });

        AnimateField(price,priceStatus,"^[1-9]?[0-9]{1,7}$");
        AnimateField(quantity,qteStatus,"^[1-9]?[0-9]{1,7}$");

        date.setOnAction(value -> {
            new Swing(date).play();
        });
        
        Common.controlDigitField(price);
        Common.controlDigitField(quantity);
    }    
    
}
