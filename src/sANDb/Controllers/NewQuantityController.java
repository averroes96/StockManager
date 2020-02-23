/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sANDb.Controllers;

import Data.Buy;
import Data.Employer;
import static Include.Common.getConnection;
import static Include.Common.getProductByName;
import static Include.Common.minimize;
import Include.Init;
import static Include.Init.CONNECTION_ERROR;
import static Include.Init.CONNECTION_ERROR_MESSAGE;
import static Include.Init.INVALID_QTE;
import static Include.Init.INVALID_QTE_MSG;
import static Include.Init.SELL_ADDED;
import static Include.Init.SELL_ADDED_MESSAGE;
import static Include.Init.UNKNOWN_ERROR;
import Include.SpecialAlert;
import JR.JasperReporter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public class NewQuantityController implements Initializable,Init {
    
    
    @FXML private TableView<Buy> buysTable ;
    @FXML private TableColumn<Buy,Integer> priceCol,qteCol,idCol;
    @FXML private TableColumn<Buy,String> prodCol;
    @FXML private TableColumn actionCol ;   
    @FXML Button cancel,addQteBtn,printBtn;
    @FXML ChoiceBox<String> productBox;
    @FXML Label minimize,qteStatus,priceStatus;
    @FXML TextField qteField,priceField;
    
    private Employer employer = new Employer();
    
    private final ObservableList<Buy> buysList = FXCollections.observableArrayList();  
    
    ObservableList<String> nameList = FXCollections.observableArrayList();
    
    SpecialAlert alert = new SpecialAlert();
    
    private final double xOffset = 0;
    private final double yOffset = 0;     

    public void getEmployer(Employer employer){
        
        this.employer = employer; 
    }
    
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
    
    private void insertBuy()
    {
                int insertedBuyID = 0 ;        
        
        if (checkInputs()) {
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        alert.show(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, Alert.AlertType.ERROR,true);
                    }
                    PreparedStatement ps;
                    ps = con.prepareStatement("INSERT INTO buy(buy_qte, buy_unit_price, buy_price, buy_date, user_id, prod_id) values(?,?,?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(5, employer.getUserID());
                    ps.setInt(3, Integer.parseInt(priceField.getText()) * Integer.parseInt(qteField.getText()));
                    //LocalDate todayLocalDate = LocalDate.now();
                    //Date sqlDate = Date.valueOf(todayLocalDate);
                    java.util.Date date = new java.util.Date();

                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    String sqlDate = sdf.format(date);                    
                    ps.setString(4, sqlDate);           
                    ps.setInt(1, Integer.parseInt(qteField.getText()));
                    ps.setInt(2, Integer.parseInt(priceField.getText()));
                    ps.setInt(6, getProductByName(productBox.getSelectionModel().getSelectedItem()).getProdID());
                    ps.executeUpdate();
                    
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            
                            insertedBuyID = generatedKeys.getInt(1) ;
                        }
                        else {
                            alert.show(UNKNOWN_ERROR,"Creating key failed, no ID obtained.",Alert.AlertType.ERROR,true);
                        }
                    }
                    ps = con.prepareStatement("UPDATE product SET prod_quantity = prod_quantity + ?, nbrBuys = nbrBuys + 1 WHERE prod_id = ?");
                    ps.setInt(1, Integer.parseInt(qteField.getText()));
                    ps.setInt(2, getProductByName(productBox.getSelectionModel().getSelectedItem()).getProdID());
                    ps.executeUpdate();
                }

                Buy AddedBuy = new Buy();
                
                AddedBuy.setProduct(productBox.getSelectionModel().getSelectedItem());
                AddedBuy.setBuyID(insertedBuyID);
                AddedBuy.setBuyPrice(Integer.parseInt(priceField.getText()) * Integer.parseInt(qteField.getText()));
                AddedBuy.setBuyQte(Integer.parseInt(qteField.getText()));
                
                buysList.add(AddedBuy);
                
                buysTable.refresh();
                
                resetWindow();                
                
                alert.show(SELL_ADDED, SELL_ADDED_MESSAGE, Alert.AlertType.INFORMATION,false);               


            }
            catch (NumberFormatException | SQLException e) {
                alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            }
        }

    }     
    
    
    public void logOut(ActionEvent event) throws IOException {

                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sANDb/FXMLs/Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(employer);
                        mControl.returnMenu("buys");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource("/sANDb/Layout/custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource("/sANDb/Layout/buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.show();            
            
    }     

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        idCol.setCellValueFactory(new PropertyValueFactory<>("buyID"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("buyPrice"));
        prodCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        qteCol.setCellValueFactory(new PropertyValueFactory<>("buyQte"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("buyAction1"));       
                   
        Callback<TableColumn<Buy, String>, TableCell<Buy, String>> cellFactory
                =                 //
    (final TableColumn<Buy, String> param) -> {
        final TableCell<Buy, String> cell = new TableCell<Buy, String>() {
            
            final Button delete = new Button("حذف");
            
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    delete.setOnAction(event -> {
                        Buy buy = getTableView().getItems().get(getIndex());
                        deleteBuy(buy);
                    });
                    delete.setStyle("-fx-background-color : red; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                    setGraphic(delete);
                    setText(null);               
                    
                }
            }

            private void deleteBuy(Buy buy) {
        try {

            try (Connection con = getConnection()) {
                String query = "DELETE FROM buy WHERE buy_id = ?";
                
                PreparedStatement ps = con.prepareStatement(query);
                
                ps.setInt(1, buy.getBuyID());
                
                ps.executeUpdate();
                
                query = "UPDATE product SET prod_quantity = prod_quantity - ?, nbrBuys = nbrBuys - 1 WHERE prod_id = ?";
                
                ps = con.prepareStatement(query);
                
                ps.setInt(2, getProductByName(buy.getProduct()).getProdID());
                ps.setInt(1, buy.getBuyQte());
                
                ps.executeUpdate();
            }

            buysList.remove(buy);
            
            buysTable.refresh();
                       
            
            alert.show(BUY_DELETED, BUY_DELETED_MSG, Alert.AlertType.INFORMATION,false);
            
        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        }            }
        };
        return cell;
    }; 
        
        actionCol.setCellFactory(cellFactory);
        
        buysTable.setItems(buysList);        
        
        getAllProducts();
               
        productBox.setItems(nameList);
        
        productBox.getSelectionModel().select(0);        
        
        addQteBtn.setOnAction(Action ->{
            
            insertBuy();
            
        });
        
        printBtn.disableProperty().bind(Bindings.size(buysTable.getItems()).isEqualTo(0));
        
        printBtn.setOnAction(Action ->{
            String selectedBuys = "";
            
            selectedBuys = buysTable.getItems().stream().map((buy) ->  buy.getBuyID() + ",").reduce(selectedBuys, String::concat);
            selectedBuys = selectedBuys.substring(0, selectedBuys.length() - 1);
            JasperReporter jr = new JasperReporter();
            jr.params.put("selectedBuys", selectedBuys);                        
            jr.ShowReport("buy","");          
        
        });
        
        priceField.setOnKeyReleased(event -> {
            
        if (!priceField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            priceStatus.setVisible(true);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            priceStatus.setVisible(false);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }
        });
        
        priceField.setOnKeyPressed(event -> {

        if (!priceField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            priceStatus.setVisible(true);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            priceStatus.setVisible(false);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }         
            
        });
        
        priceField.setOnKeyTyped(event -> {

        if (!priceField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            priceStatus.setVisible(true);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            priceStatus.setVisible(false);
            priceField.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }         
            
        });
        

        qteField.setOnKeyReleased(event -> {
            
        if (!qteField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            qteStatus.setVisible(true);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            qteStatus.setVisible(false);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }
        });
        
        qteField.setOnKeyPressed(event -> {

        if (!qteField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            qteStatus.setVisible(true);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            qteStatus.setVisible(false);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }         
            
        });
        
        qteField.setOnKeyTyped(event -> {

        if (!qteField.getText().matches("^[1-9]?[0-9]{1,7}$")) {
            qteStatus.setVisible(true);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:red;");
        }
        else{
            qteStatus.setVisible(false);
            qteField.setStyle("-fx-border-width: 2; -fx-border-color:green;");
        }         
            
        });
        
        minimize.setOnMouseClicked(Action ->{
        
            minimize(Action);
        
        });
        
        
    }    

    private boolean checkInputs() {

        try {
            Integer.parseInt(qteField.getText());
            if(Integer.parseInt(qteField.getText()) > 0 )
            return true;
            else{
            alert.show(INVALID_QTE, INVALID_QTE_MSG, Alert.AlertType.ERROR,false);
            return false;
            }
        }
        catch (NumberFormatException e) {
            alert.show(INVALID_QTE, INVALID_QTE_MSG, Alert.AlertType.ERROR,false);
            return false;
        }           
        
    }

    private void resetWindow() {
       
        productBox.getSelectionModel().select(0);
        priceField.setText("");
        qteField.setText("");
    }
    
}
