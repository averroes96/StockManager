/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sANDb.Controllers;

import Include.CommonMethods;
import Data.Employer;
import Data.Product;
import Data.Sell;
import Include.Common;
import static Include.Common.getAllProducts;
import static Include.Common.getConnection;
import static Include.Common.getPrice;
import static Include.Common.getProductByName;
import static Include.Common.getQuantity;
import static Include.Common.minimize;
import Include.Init;
import static Include.Init.UNKNOWN_ERROR;
import Include.SpecialAlert;
import JR.JasperReporter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author med
 */
public class NewSellController implements Initializable,Init,CommonMethods {
    
    
    @FXML private TableView<Sell> sellsTable ;
    @FXML private TableColumn<Sell,Integer> idCol,priceCol,quantityCol,totalCol;
    @FXML private TableColumn<Sell,Integer> prodCol;
    @FXML private TableColumn actionCol ;    
    @FXML private TextField priceField,qteField;
    @FXML private Label minimize,priceStatus;
    @FXML private Button addSell,deleteAll,printBtn;
    @FXML private ChoiceBox nameBox;
    
    private final ObservableList<Sell> sellsList = FXCollections.observableArrayList();
    
    private final ObservableList<String> nameList = getAllProducts(0);
    
    private final SpecialAlert alert = new SpecialAlert();
        
    private Employer employer = new Employer();       
      
        

    public void getEmployer(Employer employer){
        
        this.employer = employer; 
    }            
        
    
    @Override
    public boolean checkInputs()
    {
                
        if (priceField.getText().trim().equals("") || qteField.getText().trim().equals("") )  {
            alert.show("حقول إدخال فارغة", "من فضلك قم بتحديد كمية وسعر المنتوج قبل إضافة البيع", Alert.AlertType.WARNING,false);
            return false;
        }
            if(qteField.getText().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(qteField.getText()) > 0){
                if(getProductByName(nameBox.getSelectionModel().getSelectedItem().toString()).getProdQuantity() - Integer.parseInt(qteField.getText()) >= 0){
                    if(priceField.getText().trim().matches("^[1-9]?[0-9]{1,7}$"))
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
                alert.show(INVALID_QTE, INVALID_QTE_MSG, Alert.AlertType.ERROR,false);
                return false;                
            }        
    }
    
    private void resetWindow()
    {
        
                priceField.setText(String.valueOf(getPrice(nameBox.getSelectionModel().getSelectedItem().toString())));
                qteField.setText(String.valueOf(getQuantity(nameBox.getSelectionModel().getSelectedItem().toString()))); 
        
    }
    
    private void insertSell()
    {
                int insertedSellID = 0 ;        
        
        if (checkInputs()) {
            try {
                Product product = getProductByName(nameBox.getSelectionModel().getSelectedItem().toString());
                try (Connection con = getConnection()) {
                    if(con == null) {
                        alert.show(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, Alert.AlertType.ERROR,true);
                    }
                    PreparedStatement ps;
                    ps = con.prepareStatement("INSERT INTO sell(sell_price_unit, sell_price, sell_quantity, sell_date, prod_id, user_id) values(?,?,?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(6, employer.getUserID());
                    ps.setInt(1, Integer.parseInt(priceField.getText()));
                    ps.setInt(2, Integer.parseInt(priceField.getText()) * Integer.parseInt(qteField.getText()));
                    ps.setInt(3, Integer.parseInt(qteField.getText()) );
                    ps.setInt(5, product.getProdID());
                    java.util.Date date = new java.util.Date();

                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    String sqlDate = sdf.format(date);                    
                    ps.setString(4, sqlDate); 
                    ps.executeUpdate();
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            
                            insertedSellID = generatedKeys.getInt(1) ;
                        }
                        else {
                            alert.show(UNKNOWN_ERROR,"Creating key failed, no ID obtained.",Alert.AlertType.ERROR,true);
                        }
                    }
                    ps = con.prepareStatement("UPDATE product SET prod_quantity = prod_quantity - ?, nbrSells = nbrSells + 1 WHERE prod_id = ?");
                    ps.setInt(1, Integer.parseInt(qteField.getText()));
                    ps.setInt(2, product.getProdID());
                    ps.executeUpdate();
                }

                Sell AddedSell = new Sell();
                
                AddedSell.setProduct(product);
                AddedSell.setSellName(product.getName());
                AddedSell.setSellID(insertedSellID);
                AddedSell.setTotalPrice(Integer.parseInt(priceField.getText()) * Integer.parseInt(qteField.getText()));
                AddedSell.setSellPrice(Integer.parseInt(priceField.getText()));
                AddedSell.setSellQuantity(Integer.parseInt(qteField.getText()));
                
                sellsList.add(AddedSell);
                
                sellsTable.refresh();
                
                resetWindow();                
                
                alert.show(SELL_ADDED, SELL_ADDED_MESSAGE, Alert.AlertType.INFORMATION,false);               


            }
            catch (NumberFormatException | SQLException e) {
                alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            }
        }

    }     
        
    @Override
    public void logOut(ActionEvent event) throws IOException {

                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sANDb/FXMLs/Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(employer);
                        mControl.returnMenu("sells");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource("/sANDb/Layout/custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource("/sANDb/Layout/buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.show();               
            
    }   
    
    private void deleteSell(Sell selectedSell)
    {        
        try {

            try (Connection con = getConnection()) {
                String query = "DELETE FROM sell WHERE sell_id = ?";
                
                PreparedStatement ps = con.prepareStatement(query);
                
                ps.setInt(1, selectedSell.getSellID());
                
                ps.executeUpdate();
                
                query = "UPDATE product SET prod_quantity = prod_quantity + ?, nbrSells = nbrSells - 1 WHERE prod_id = ?";
                
                ps = con.prepareStatement(query);
                
                ps.setInt(2, selectedSell.getProduct().getProdID());
                ps.setInt(1, selectedSell.getSellQuantity());
                
                ps.executeUpdate();
            }

            sellsList.remove(selectedSell);
            
            sellsTable.refresh();

            resetWindow();
            
            alert.show(SELL_DELETED, SELL_DELETED_MESSAGE, Alert.AlertType.INFORMATION,false);
            
        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        }
    }    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        idCol.setCellValueFactory(new PropertyValueFactory<>("sellID"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        prodCol.setCellValueFactory(new PropertyValueFactory<>("sellName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("sellQuantity"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("sellActions"));       
                   
        Callback<TableColumn<Sell, String>, TableCell<Sell, String>> cellFactory
                =                 //
    (final TableColumn<Sell, String> param) -> {
        final TableCell<Sell, String> cell = new TableCell<Sell, String>() {
            
            final Button delete = new Button("حذف");
            
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    delete.setOnAction(event -> {
                        Sell sell = getTableView().getItems().get(getIndex());
                        deleteSell(sell);
                    });
                    delete.setStyle("-fx-background-color : red; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                    setGraphic(delete);
                    setText(null);               
                    
                }
            }
        };
        return cell;
    };
        
   
        
        actionCol.setCellFactory(cellFactory);
        
        sellsTable.setItems(sellsList);       
        
        addSell.setOnAction(Action -> {
            insertSell();
        });
        
        minimize.setOnMouseClicked(Action -> {
        
            minimize(Action);
            
        });
        
        sellsTable.getSelectionModel().selectFirst();
        
        nameBox.setItems(nameList);
        
        nameBox.getSelectionModel().select(0);
        
        priceField.setText(String.valueOf(getPrice(nameBox.getSelectionModel().getSelectedItem().toString())));
        qteField.setText(String.valueOf(getQuantity(nameBox.getSelectionModel().getSelectedItem().toString()))); 

        nameBox.setOnAction(event -> {
            
            if(getQuantity(nameBox.getSelectionModel().getSelectedItem().toString()) == 0){
                alert.show(ZERO_QTE, ZERO_QTE_MSG, Alert.AlertType.WARNING, false);                
            }
            priceField.setText(String.valueOf(getPrice(nameBox.getSelectionModel().getSelectedItem().toString())));
            qteField.setText(String.valueOf(getQuantity(nameBox.getSelectionModel().getSelectedItem().toString()))); 
            
        });
        
        priceField.setOnKeyReleased(event -> {
            
        if (!priceField.getText().matches("^[1-9]?[0-9]*$")) {
            priceStatus.setVisible(true);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            priceStatus.setVisible(false);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }             
            
        });
        priceField.setOnKeyPressed(event -> {
            
        if (!priceField.getText().matches("^[1-9]?[0-9]*$")) {
            priceStatus.setVisible(true);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            priceStatus.setVisible(false);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }            
            
        });
        priceField.setOnKeyTyped(event -> {
            
        if (!priceField.getText().matches("^[1-9]?[0-9]*$")) {
            priceStatus.setVisible(true);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            priceStatus.setVisible(false);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }            
            
        });        
        
        deleteAll.setOnAction(Action ->{
            
            deleteAll();            
            
            alert.show(SELL_DELETED, SELL_DELETED_MESSAGE, Alert.AlertType.INFORMATION,false);

            sellsList.clear();
            sellsTable.getItems().clear();            
        });
        
        deleteAll.disableProperty().bind(Bindings.size(sellsTable.getItems()).isEqualTo(0));
        printBtn.disableProperty().bind(Bindings.size(sellsTable.getItems()).isEqualTo(0));
        
        printBtn.setOnAction(Action ->{
            String selectedSells = "";
            
            selectedSells = sellsTable.getItems().stream().map((sell) -> sell.getSellID() + ",").reduce(selectedSells, String::concat);
            selectedSells = selectedSells.substring(0, selectedSells.length() - 1);
            JasperReporter jr = new JasperReporter();
            jr.params.put("selectedSells", selectedSells);                        
            jr.ShowReport("sellBill","");          
        
        });
        
    }    

    private void deleteAll() {
        
        Iterator<Sell> myIterator = sellsList.iterator();
       
        
        try {

        while(myIterator.hasNext()){
            
            Sell selectedSell = myIterator.next();
            
            try (Connection con = getConnection()) {
                String query = "DELETE FROM sell WHERE sell_id = ?";
                
                PreparedStatement ps = con.prepareStatement(query);
                
                ps.setInt(1, selectedSell.getSellID());
                
                ps.executeUpdate();
                
                query = "UPDATE product SET prod_quantity = prod_quantity + ?, nbrSells = nbrSells - 1 WHERE prod_id = ?";
                
                ps = con.prepareStatement(query);
                
                ps.setInt(2, selectedSell.getProduct().getProdID());
                ps.setInt(1, selectedSell.getSellQuantity());
                
                ps.executeUpdate();
            }
            
        }
        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        }

        
       
    }


    
}
