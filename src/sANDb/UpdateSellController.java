
package sANDb;

import Include.CommonMethods;
import Data.Employer;
import Data.Product;
import Data.Sell;
import static Include.Common.dateFormatter;
import static Include.Common.getConnection;
import static Include.Common.getProductByName;
import static Include.Common.getQuantity;
import static Include.Common.minimize;
import Include.Init;
import static Include.Init.INVALID_PRICE;
import static Include.Init.INVALID_PRICE_MSG;
import static Include.Init.INVALID_QTE;
import static Include.Init.INVALID_QTE_MSG;
import static Include.Init.NOT_ENOUGH_QUANTITY;
import static Include.Init.NOT_ENOUGH_QUANTITY_MSG;
import static Include.Init.UNKNOWN_ERROR;
import static Include.Init.ZERO_QTE;
import static Include.Init.ZERO_QTE_MSG;
import Include.SpecialAlert;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author med
 */
public class UpdateSellController implements Initializable,Init,CommonMethods {

    
    @FXML private Button saveButton,cancelButton;
    @FXML private TextField price,quantity;
    @FXML private DatePicker date;
    @FXML private Label minimize,priceStatus,qteStatus;
    @FXML private ChoiceBox nameBox;
    
    ObservableList<String> nameList = FXCollections.observableArrayList();
    
    Employer employer = new Employer();
    Sell sell = new Sell();
    SpecialAlert alert = new SpecialAlert();               
    
    private final double xOffset = 0;
    private final double yOffset = 0;
    
    
    @Override
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
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);

        } 
        
    }    
    
    public void fillFields(Sell selectedSell){
        
        nameBox.getSelectionModel().select(selectedSell.getProduct().getName()); 
        quantity.setText(String.valueOf(selectedSell.getSellQuantity()));
        price.setText(String.valueOf(selectedSell.getTotalPrice() / selectedSell.getSellQuantity()));
        date.getEditor().setText(selectedSell.getSellDate());

    }
    
    @Override
    public void logOut(ActionEvent event) throws IOException {

                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(employer);
                        mControl.returnMenu("sells");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource("Layout/custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource("Layout/buttons.css").toExternalForm());                          
                        stage.setScene(scene);
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
        
        Product oldProduct = getProductByName(this.sell.getSellName());
        Product newProduct = getProductByName(nameBox.getSelectionModel().getSelectedItem().toString());        
                
        if (price.getText().trim().equals("") || quantity.getText().trim().equals("") )  {
            alert.show("حقول إدخال فارغة", "من فضلك قم بتحديد كمية وسعر المنتوج قبل إضافة البيع", Alert.AlertType.WARNING,false);
            return false;
        }
            if(quantity.getText().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(quantity.getText()) > 0){
                if(oldProduct.getName().equals(newProduct.getName())){
                if(oldProduct.getProdQuantity() + this.sell.getSellQuantity() - Integer.parseInt(quantity.getText())  >= 0){
                    if(price.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(price.getText()) > 0)
                    return true;
                    else{
                    alert.show(INVALID_PRICE, INVALID_PRICE_MSG, Alert.AlertType.ERROR,false);
                    return false;
                    }                     
                }
                else{
                    alert.show(NOT_ENOUGH_QUANTITY, NOT_ENOUGH_QUANTITY_MSG, Alert.AlertType.WARNING,false);
                    return false;                    
                }
                }
                else{
                if(newProduct.getProdQuantity() - Integer.parseInt(quantity.getText())  >= 0){
                    if(price.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(price.getText()) > 0)
                    return true;
                    else{
                    alert.show(INVALID_PRICE, INVALID_PRICE_MSG, Alert.AlertType.ERROR,false);
                    return false;
                    }                     
                }
                else{
                    alert.show(NOT_ENOUGH_QUANTITY, NOT_ENOUGH_QUANTITY_MSG, Alert.AlertType.WARNING,false);
                    return false;                    
                }                    
                }
              
            }
            else{
                alert.show(INVALID_QTE, INVALID_QTE_MSG, Alert.AlertType.ERROR,false);
                return false;                
            }        
    }
           
        
    public void updateSell(ActionEvent event) {

        if (checkInputs()) {
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        alert.show(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, Alert.AlertType.ERROR,true);
                    }
                    Product oldProduct = getProductByName(this.sell.getSellName());
                    Product newProduct = getProductByName(nameBox.getSelectionModel().getSelectedItem().toString());
                    PreparedStatement ps;
                    
                    ps = con.prepareStatement("UPDATE sell SET sell_quantity = ?, sell_price_unit = ?, sell_price = ?, sell_date = ?, user_id = ?, prod_id = ? WHERE sell_id = ?");
                    
                    ps.setInt(5, employer.getUserID());
                    ps.setInt(1, Integer.parseInt(quantity.getText()));
                    ps.setInt(2, Integer.parseInt(price.getText()));
                    ps.setInt(3, Integer.parseInt(price.getText()) * Integer.parseInt(quantity.getText()));
                    ps.setInt(6, newProduct.getProdID());
                    ps.setDate(4, Date.valueOf(date.getEditor().getText()));
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

                alert.show(SELL_UPDATED, SELL_UPDATED_MSG, Alert.AlertType.INFORMATION,false);
                

                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(this.employer);
                        mControl.returnMenu("sells");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        stage.initStyle(StageStyle.TRANSPARENT);                    
                        stage.setScene(scene);
                        stage.show();                
                        ((Node)event.getSource()).getScene().getWindow().hide();                
                
            }
            catch (IOException | NumberFormatException | SQLException e) {
                alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            }
        }        
        
    }        

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        getAllProducts();
               
        nameBox.setItems(nameList);
        
        nameBox.setOnAction(event -> {
            
            if(getQuantity(nameBox.getSelectionModel().getSelectedItem().toString()) == 0){
                alert.show(ZERO_QTE, ZERO_QTE_MSG, Alert.AlertType.WARNING, false);                
            }
            
        });        
        
        date.setConverter(dateFormatter());
        
        minimize.setOnMouseClicked(Action -> {
        
            minimize(Action);
            
        });       

        price.setOnKeyReleased(event -> {
            
        if (!price.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            priceStatus.setVisible(true);
            price.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            priceStatus.setVisible(false);
            price.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }
        });
        
        price.setOnKeyPressed(event -> {

        if (!price.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            priceStatus.setVisible(true);
            price.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            priceStatus.setVisible(false);
            price.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }         
            
        });
        
        price.setOnKeyTyped(event -> {

        if (!price.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            priceStatus.setVisible(true);
            price.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            priceStatus.setVisible(false);
            price.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }         
            
        });
        


        quantity.setOnKeyReleased(event -> {
            
        if (!quantity.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            qteStatus.setVisible(true);
            quantity.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            qteStatus.setVisible(false);
            quantity.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }
        });
        
        quantity.setOnKeyPressed(event -> {

        if (!quantity.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            qteStatus.setVisible(true);
            quantity.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            qteStatus.setVisible(false);
            quantity.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }         
            
        });
        
        quantity.setOnKeyTyped(event -> {

        if (!quantity.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            qteStatus.setVisible(true);
            quantity.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            qteStatus.setVisible(false);
            quantity.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }         
            
        });         
        
        
        
    }    


    
}
