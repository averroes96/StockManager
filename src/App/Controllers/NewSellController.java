/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Product;
import Data.Sell;
import Data.User;
import Include.Common;
import static Include.Common.animateBtn;
import static Include.Common.controlDigitField;
import static Include.Common.getConnection;
import static Include.Common.initLayout;
import Include.CommonMethods;
import Include.SMController;
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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JRException;

/**
 * FXML Controller class
 *
 * @author med
 */
public class NewSellController extends SMController implements Initializable,Init,CommonMethods {
    
    
    @FXML private TableView<Sell> sellsTable ;
    @FXML private TableColumn<Sell,Integer> priceCol,qteCol;
    @FXML private TableColumn<Sell,String> prodCol;
    @FXML private TableColumn actionCol ;    
    @FXML private JFXTextField priceField,qteField;
    @FXML private JFXButton addSell,deleteAll,printBtn;
    @FXML private ChoiceBox<Product> productCB;
    @FXML private Button returnBtn;
    
    private final ObservableList<Sell> sellsList = FXCollections.observableArrayList();
    
    private ObservableList<Product> prodList = null;        

    public void getEmployer(User user){
        this.employer = user; 
    }            
        
    
    @Override
    public boolean checkInputs()
    {
                
        if (priceField.getText().trim().equals("") || qteField.getText().trim().equals("") ){
            customDialog(bundle.getString("missing_fields"), bundle.getString("missing_fields_msg"), INFO_SMALL, true, addSell);
            return false;
        }
            if(qteField.getText().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(qteField.getText()) > 0){
            try {
                if(productCB.getSelectionModel().getSelectedItem().getCurrentQuantity() - Integer.parseInt(qteField.getText()) >= 0){
                    if(priceField.getText().trim().matches("^[1-9]?[0-9]{1,7}$"))
                        return true;
                    else{
                        customDialog(bundle.getString("invalid_price"), bundle.getString("invalid_price_msg"), INFO_SMALL, true, addSell);
                        return false;
                    }                     
                }
                else{
                    customDialog(bundle.getString("not_enough_qte"), bundle.getString("not_enough_qte_msg"), INFO_SMALL, true, addSell);
                    return false;                    
                }
            } catch (SQLException ex) {
                exceptionLayout(ex, addSell);
                return false;
            }
              
            }
            else{
                customDialog(bundle.getString("invalid_qte"), bundle.getString("invalid_qte_msg"), INFO_SMALL, true, addSell);
                return false;                
            }        
    }
    
    private void resetWindow() throws SQLException
    {
            priceField.setText(String.valueOf(productCB.getSelectionModel().getSelectedItem().getSellPrice())); 
            qteField.setText(String.valueOf(productCB.getSelectionModel().getSelectedItem().getCurrentQuantity()));
    }
    
    private void insertSell()
    {
        int insertedSellID = 0 ;        
        
        if (checkInputs()) {
            try {
                Product product = productCB.getSelectionModel().getSelectedItem();
                try (Connection con = getConnection()) {
                    if(con == null) {
                        customDialog(bundle.getString("connection_error"), bundle.getString("connection_error_msg"), INFO_SMALL, true, addSell);
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
                            customDialog(bundle.getString("unknown_error"), "Creating key failed, no ID obtained.", INFO_SMALL, true, addSell);
                        }
                    }
                    product.hasSold(Integer.parseInt(qteField.getText()));
                }

                Sell addedSell = new Sell(
                        insertedSellID, 
                        Integer.parseInt(priceField.getText()), 
                        Integer.parseInt(priceField.getText()) * Integer.parseInt(qteField.getText()), 
                        product.getName(),
                        Integer.parseInt(qteField.getText()),
                        product
                );
                                
                sellsList.add(addedSell);
                sellsTable.refresh();
                resetWindow();                
                
                customDialog(bundle.getString("sell_added"), bundle.getString("sell_added_msg"), INFO_SMALL, true, addSell);
            }
            catch (NumberFormatException | SQLException e) {
                exceptionLayout(e, addSell);
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
        mControl.returnMenu("sells");
        Common.startStage(root, (int)root.getWidth(), (int)root.getHeight());
            
    }   
    
    private void deleteSell(Sell selectedSell)
    {        
        try {

            selectedSell.delete();

            sellsList.remove(selectedSell);
            sellsTable.refresh();
            resetWindow();
            
            customDialog(bundle.getString("sell_deleted"), bundle.getString("sell_deleted_msg"), INFO_SMALL, true, addSell);
            
        }
        catch (SQLException e) {
            exceptionLayout(e, addSell);
        }
    }

    JasperReporter jr = new JasperReporter();
    
    private void onJasperReportLoading(){
        
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, bundle.getString("please_wait"), bundle.getString("report_wait_msg"), WAIT_SMALL);
            loadDialog(layout, false , addSell);
            
            jr = new JasperReporter();
    }    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            bundle = rb;
            
            prodList = Product.getActiveProducts();
            
            initTable();
            
            addSell.setOnAction(Action -> {
                insertSell();
            });
            
            productCB.setItems(prodList);
            
            productCB.getSelectionModel().select(0);
            
            priceField.setText(String.valueOf(productCB.getSelectionModel().getSelectedItem().getSellPrice()));
            qteField.setText(String.valueOf(productCB.getSelectionModel().getSelectedItem().getCurrentQuantity()));
            
            productCB.setOnAction(event -> {
                
                try {
                    if(productCB.getSelectionModel().getSelectedItem().getCurrentQuantity() == 0){
                        customDialog(bundle.getString("zero_quantity"), bundle.getString("zero_quantity_msg"), INFO_SMALL, true, addSell);
                    }
                    
                    priceField.setText(String.valueOf(productCB.getSelectionModel().getSelectedItem().getSellPrice()));
                    qteField.setText(String.valueOf(productCB.getSelectionModel().getSelectedItem().getCurrentQuantity()));
                } catch (SQLException ex) {
                    exceptionLayout(ex, addSell);
                }
                
            });
            
            deleteAll.setOnAction(Action ->{
                
                deleteAll();
                customDialog(bundle.getString("sell_deleted"), bundle.getString("sell_deleted_msg"), INFO_SMALL, true, addSell);
                sellsList.clear();
                sellsTable.getItems().clear();
                
            });
            
            returnBtn.setOnAction(value -> {
                try {
                    logOut(value);
                } catch (IOException ex) {    
                    exceptionLayout(ex, addSell);
                }
            });
            
            deleteAll.disableProperty().bind(Bindings.size(sellsTable.getItems()).isEqualTo(0));
            printBtn.disableProperty().bind(Bindings.size(sellsTable.getItems()).isEqualTo(0));
            
            productCB.valueProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    if(newValue.isEmpty()){
                        addSell.setDisable(true);
                    }
                    else{
                        addSell.setDisable(false);
                    }
                } catch (SQLException ex) {
                    exceptionLayout(ex, addSell);
                }
            });
            
            
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
            controlDigitField(qteField);
            
            animateBtn(addSell);
        } catch (SQLException ex) {
            exceptionLayout(ex, addSell);
        }
        
    }    

    private void deleteAll() {
        
        try {
            Iterator<Sell> myIterator = sellsList.iterator();
            
            while(myIterator.hasNext()){
                Sell selectedSell = myIterator.next();
                selectedSell.delete();
            }
            
            resetWindow();
        } catch (SQLException ex) {
            exceptionLayout(ex, addSell);
        }
    }

    private void initTable() {
        
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
        
        sellsTable.getSelectionModel().selectFirst();
        
    }


    
}
