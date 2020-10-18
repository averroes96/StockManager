/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Product;
import static Data.Product.getProductByName;
import Data.Sell;
import Data.User;
import static Include.Common.animateBtn;
import static Include.Common.controlDigitField;
import static Include.Common.getAllProducts;
import static Include.Common.getConnection;
import static Include.Common.getPrice;
import static Include.Common.getQuantity;
import static Include.Common.initLayout;
import Include.CommonMethods;
import Include.GDPController;
import Include.Init;
import JR.JasperReporter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JRException;

/**
 * FXML Controller class
 *
 * @author med
 */
public class NewSellController extends GDPController implements Initializable,Init,CommonMethods {
    
    
    @FXML private TableView<Sell> sellsTable ;
    @FXML private TableColumn<Sell,Integer> idCol,priceCol,qteCol;
    @FXML private TableColumn<Sell,String> prodCol;
    @FXML private TableColumn actionCol ;    
    @FXML private JFXTextField priceField,qteField;
    @FXML private JFXButton addSell,deleteAll,printBtn;
    @FXML private ChoiceBox nameBox;
    @FXML private Button cancel;
    
    private final ObservableList<Sell> sellsList = FXCollections.observableArrayList();
    
    private ObservableList<String> nameList = null;        

    public void getEmployer(User employer){
        
        this.employer = employer; 
    }            
        
    
    @Override
    public boolean checkInputs()
    {
                
        if (priceField.getText().trim().equals("") || qteField.getText().trim().equals("") ){
            customDialog(MISSING_FIELDS, MISSING_FIELDS, INFO_SMALL, true, addSell);
            return false;
        }
            if(qteField.getText().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(qteField.getText()) > 0){
            try {
                if(getProductByName(nameBox.getSelectionModel().getSelectedItem().toString()).getProdQuantity() - Integer.parseInt(qteField.getText()) >= 0){
                    if(priceField.getText().trim().matches("^[1-9]?[0-9]{1,7}$"))
                        return true;
                    else{
                        customDialog(INVALID_PRICE, INVALID_PRICE_MSG, INFO_SMALL, true, addSell);
                        return false;
                    }                     
                }
                else{
                    customDialog(NOT_ENOUGH_QUANTITY, NOT_ENOUGH_QUANTITY_MSG, INFO_SMALL, true, addSell);
                    return false;                    
                }
            } catch (SQLException ex) {
                exceptionLayout(ex, addSell);
                return false;
            }
              
            }
            else{
                customDialog(INVALID_QTE, INVALID_QTE_MSG, INFO_SMALL, true, addSell);
                return false;                
            }        
    }
    
    private void resetWindow()
    {
        
        try {
            priceField.setText(String.valueOf(getPrice(nameBox.getSelectionModel().getSelectedItem().toString()))); 
            qteField.setText(String.valueOf(getQuantity(nameBox.getSelectionModel().getSelectedItem().toString())));
        } catch (SQLException ex) {
            exceptionLayout(ex, addSell);
        }
        
    }
    
    private void insertSell()
    {
                int insertedSellID = 0 ;        
        
        if (checkInputs()) {
            try {
                Product product = getProductByName(nameBox.getSelectionModel().getSelectedItem().toString());
                try (Connection con = getConnection()) {
                    if(con == null) {
                        customDialog(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, INFO_SMALL, true, addSell);
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
                            customDialog(UNKNOWN_ERROR, "Creating key failed, no ID obtained.", INFO_SMALL, true, addSell);
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
                
                customDialog(SELL_ADDED, SELL_ADDED_MESSAGE, INFO_SMALL, true, addSell);

            }
            catch (NumberFormatException | SQLException e) {
                exceptionLayout(e, addSell);
            }
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
                        //stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.setMinHeight(700);
                        stage.setMinWidth(1000);
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
            
            customDialog(SELL_DELETED, SELL_DELETED_MESSAGE, INFO_SMALL, true, addSell);
            
        }
        catch (SQLException e) {
            exceptionLayout(e, addSell);
        }
    }

    JasperReporter jr = new JasperReporter();
    
    private void onJasperReportLoading(){
        
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, PLEASE_WAIT, REPORT_WAIT_MESSAGE, WAIT_SMALL);
            
            loadDialog(layout, false , addSell);
            
            jr = new JasperReporter();
    }    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            nameList = getAllProducts(0);
        } catch (SQLException ex) {
            exceptionLayout(ex, addSell);
        }
        
        idCol.setCellValueFactory(new PropertyValueFactory<>("sellID"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        prodCol.setCellValueFactory(new PropertyValueFactory<>("sellName"));
        qteCol.setCellValueFactory(new PropertyValueFactory<>("sellQuantity"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("sellActions"));       
                   
        Callback<TableColumn<Sell, String>, TableCell<Sell, String>> cellFactory
                =                 //
    (final TableColumn<Sell, String> param) -> {
        final TableCell<Sell, String> cell = new TableCell<Sell, String>() {
            
            final Button delete = new Button();
            
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
                    delete.setGraphic(new ImageView(new Image(IMAGES_PATH + "small/trash_small_white.png", 24, 24, false, false)));
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
        
        sellsTable.getSelectionModel().selectFirst();
        
        nameBox.setItems(nameList);
        
        nameBox.getSelectionModel().select(0);
        
        try {
            priceField.setText(String.valueOf(getPrice(nameBox.getSelectionModel().getSelectedItem().toString())));
        } catch (SQLException ex) {
            exceptionLayout(ex, addSell);
        }
        try { 
            qteField.setText(String.valueOf(getQuantity(nameBox.getSelectionModel().getSelectedItem().toString())));
        } catch (SQLException ex) {
            exceptionLayout(ex, addSell);
        }

        nameBox.setOnAction(event -> {
            
            try {

                if(getQuantity(nameBox.getSelectionModel().getSelectedItem().toString()) == 0){
                    customDialog(ZERO_QTE, ZERO_QTE_MSG, INFO_SMALL, true, addSell);
                }

                priceField.setText(String.valueOf(getPrice(nameBox.getSelectionModel().getSelectedItem().toString())));
                qteField.setText(String.valueOf(getQuantity(nameBox.getSelectionModel().getSelectedItem().toString())));
                
            } catch (SQLException ex) {
                exceptionLayout(ex, addSell);
            }
            
        });
                
        deleteAll.setOnAction(Action ->{
            
            deleteAll();            
            customDialog(SELL_DELETED, SELL_DELETED_MESSAGE, INFO_SMALL, true, addSell);
            sellsList.clear();
            sellsTable.getItems().clear();    
            
        });
        
        cancel.setOnAction(value -> {
            try {
                logOut(value);
            } catch (IOException ex) {
                exceptionLayout(ex, addSell);
            }
        });
        
        deleteAll.disableProperty().bind(Bindings.size(sellsTable.getItems()).isEqualTo(0));
        printBtn.disableProperty().bind(Bindings.size(sellsTable.getItems()).isEqualTo(0));
        
        printBtn.setOnAction(Action ->{
            
            
          onJasperReportLoading();
            
            Thread th = new Thread(() -> {
            
              try {
                  String selectedSells = "";
                  
                  selectedSells = sellsTable.getItems().stream().map((sell) -> sell.getSellID() + ",").reduce(selectedSells, String::concat);
                  selectedSells = selectedSells.substring(0, selectedSells.length() - 1);
                  jr.params.put("selectedSells", selectedSells);
                  jr.ShowReport("sellBill","");
                  dialog.close();
                  stackPane.setVisible(false);
              } catch (SQLException | JRException ex) {
                  exceptionLayout(ex, addSell);
              }
            });
            
            th.start();          
        
        });
        
        controlDigitField(priceField);
        
        animateBtn(addSell);
        
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
        catch (SQLException e){
            exceptionLayout(e, addSell);
        }
        
       resetWindow();
    }


    
}
