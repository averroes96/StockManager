/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Buy;
import Data.Product;
import Data.User;
import Include.Common;
import static Include.Common.controlDigitField;
import static Include.Common.getConnection;
import Include.Init;
import Include.SMController;
import JR.JasperReporter;
import animatefx.animation.Pulse;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JRException;

/**
 * FXML Controller class
 *
 * @author med
 */
public class NewBuyController extends SMController implements Initializable,Init {
    
    
    @FXML private TableView<Buy> buysTable ;
    @FXML private TableColumn<Buy,Integer> priceCol,qteCol;
    @FXML private TableColumn<Buy,String> prodCol;
    @FXML private TableColumn actionCol ;   
    @FXML private Button returnBtn;
    @FXML private JFXButton addQteBtn,printBtn;
    @FXML private ChoiceBox<Product> productCB;
    @FXML private Label sum, total, qte;
    @FXML private JFXTextField qteField,priceField;
    @FXML private HBox topBar;
    
    private final ObservableList<Buy> buysList = FXCollections.observableArrayList();  
    
    ObservableList<Product> prodList = FXCollections.observableArrayList();
        
    public void getEmployer(User employer){
        
        this.employer = employer; 
    }
    
    public void getAllProducts(){
        
        try {
            prodList = Product.getActiveProducts();
        }
        catch (SQLException e) {
            exceptionLayout(e, addQteBtn);
        } 
        
    }
    
    private void insertBuy()
    {
        int insertedBuyID = 0 ;        
        
        if (checkInputs()) {
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        customDialog(bundle.getString("connection_error"), bundle.getString("connection_error_msg"), ERROR_SMALL, true, addQteBtn);
                    }
                    PreparedStatement ps;
                    ps = con.prepareStatement("INSERT INTO buy(buy_qte, buy_unit_price, buy_price, buy_date, user_id, prod_id) values(?,?,?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(5, employer.getUserID());
                    ps.setInt(3, Integer.parseInt(priceField.getText()) * Integer.parseInt(qteField.getText()));
                    java.util.Date date = new java.util.Date();

                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    String sqlDate = sdf.format(date);                    
                    ps.setString(4, sqlDate);           
                    ps.setInt(1, Integer.parseInt(qteField.getText()));
                    ps.setInt(2, Integer.parseInt(priceField.getText()));
                    ps.setInt(6, productCB.getSelectionModel().getSelectedItem().getProdID());
                    ps.executeUpdate();
                    
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            
                            insertedBuyID = generatedKeys.getInt(1) ;
                        }
                        else {
                            customDialog(bundle.getString("unknown_error"), "Creating key failed, no ID obtained.", ERROR_SMALL, true, addQteBtn);
                        }
                    }
                    ps = con.prepareStatement("UPDATE product SET prod_quantity = prod_quantity + ?, nbrBuys = nbrBuys + 1 WHERE prod_id = ?");
                    ps.setInt(1, Integer.parseInt(qteField.getText()));
                    ps.setInt(2, productCB.getSelectionModel().getSelectedItem().getProdID());
                    ps.executeUpdate();
                }

                Buy AddedBuy = new Buy();
                
                AddedBuy.setProduct(productCB.getSelectionModel().getSelectedItem().getName());
                AddedBuy.setBuyID(insertedBuyID);
                AddedBuy.setBuyPrice(Integer.parseInt(priceField.getText()) * Integer.parseInt(qteField.getText()));
                AddedBuy.setBuyQte(Integer.parseInt(qteField.getText()));
                
                buysList.add(AddedBuy);
                
                buysTable.refresh();
                
                updateReport();
                
                resetWindow();

                animateNode(new Pulse(buysTable));
                
                customDialog(bundle.getString("buy_added"), bundle.getString("buy_added_msg"), INFO_SMALL, true, addQteBtn);

            }
            catch (NumberFormatException | SQLException e) {
                exceptionLayout(e, addQteBtn);
            }
        }

    }
    
    public void updateReport(){
        
        int sumTemp = 0,totalTemp = 0,qteTemp = 0;

        for(Buy buy : buysTable.getItems()){
            sumTemp++;
            totalTemp += buy.getBuyPrice();
            qteTemp += buy.getBuyQte();
        }

        sum.setText(sumTemp + bundle.getString("buy"));
        total.setText(totalTemp + bundle.getString("currency"));
        qte.setText(qteTemp + bundle.getString("pieces"));        
    }
    
    
    @Override
    public void logOut(ActionEvent event) throws IOException {

        ((Node)event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
        AnchorPane root = (AnchorPane)loader.load();
        MainController mControl = (MainController)loader.getController();
        mControl.getEmployer(employer);
        mControl.returnMenu("buys");
        Common.startStage(root,(int)root.getWidth(), (int)root.getHeight());
            
    }     

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        try {
            isAnimated();
        } catch (SQLException ex) {
            exceptionLayout(ex, addQteBtn);
        }
            
        if(bundle.getLocale().getLanguage().equals("ar"))
            anchorPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);         
        
        initTable();
        
        getAllProducts();
               
        productCB.setItems(prodList);
        
        productCB.getSelectionModel().select(0);        
        
        addQteBtn.setOnAction(Action ->{
            insertBuy();
        });
        
        returnBtn.setOnAction(value -> {
            try {
                logOut(value);
            } catch (IOException ex) {
                exceptionLayout(ex, addQteBtn);
            }
        });
        
        printBtn.disableProperty().bind(Bindings.size(buysTable.getItems()).isEqualTo(0));
        
        printBtn.setOnAction(Action ->{
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
            
                try {
                    String selectedBuys = "";
                    
                    selectedBuys = buysTable.getItems().stream().map((buy) ->  buy.getBuyID() + ",").reduce(selectedBuys, String::concat);
                    selectedBuys = selectedBuys.substring(0, selectedBuys.length() - 1);
                    jr.params.put("selectedBuys", selectedBuys);
                    jr.ShowReport("buy","");
                    dialog.close();
                    stackPane.setVisible(false);
                } catch (SQLException | JRException ex) {
                    exceptionLayout(ex, addQteBtn);
                }

            });
            
            th.start();

        });
        
        controlDigitField(priceField);
        controlDigitField(qteField);
                
    }    

    @Override
    public boolean checkInputs() {

        try {
            Integer.parseInt(qteField.getText());
            if(Integer.parseInt(qteField.getText()) > 0 ){
                try {
                    Integer.parseInt(priceField.getText());
                    if(Integer.parseInt(priceField.getText()) > 0 )
                    return true;
                    else{
                        customDialog(bundle.getString("invalid_price"), bundle.getString("invalid_price_msg"), ERROR_SMALL, true, addQteBtn);
                        return false;
                    }
                }
                catch (NumberFormatException e) {
                    customDialog(bundle.getString("invalid_price"), bundle.getString("invalid_price_msg"), ERROR_SMALL, true, addQteBtn);
                    return false;
                } 
            }
            else{
                customDialog(bundle.getString("invalid_qte"), bundle.getString("invalid_qte_msg"), ERROR_SMALL, true, addQteBtn);
                return false;
            }
        }
        catch (NumberFormatException e) {
            customDialog(bundle.getString("invalid_qte"), bundle.getString("invalid_qte_msg"), ERROR_SMALL, true, addQteBtn);
            return false;
        }
                
    }
    
    JasperReporter jr = new JasperReporter();
    
    private void onJasperReportLoading(){
        
        customDialog(bundle.getString("please_wait"), bundle.getString("report_wait_msg"), WAIT_SMALL, false, addQteBtn);
        jr = new JasperReporter();
    }

    private void resetWindow() {
       
        productCB.getSelectionModel().select(0);
        priceField.setText("");
        qteField.setText("");
        
    }

    private void initTable() {
        
        priceCol.setCellValueFactory(new PropertyValueFactory<>("buyPrice"));
        prodCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        qteCol.setCellValueFactory(new PropertyValueFactory<>("buyQte"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("buyAction1"));       
                   
        Callback<TableColumn<Buy, String>, TableCell<Buy, String>> cellFactory
                =                 //
        (final TableColumn<Buy, String> param) -> {
            final TableCell<Buy, String> cell = new TableCell<Buy, String>() {

                final Button delete = new Button();

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
                            animateNode(new Pulse(buysTable));
                        });
                        delete.setGraphic(new ImageView(new Image(IMAGES_PATH + "small/trash_small_white.png", 24, 24, false, false)));
                        delete.setStyle("-fx-background-color : red; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                        setGraphic(delete);
                        setText(null);               

                    }
                }

                private void deleteBuy(Buy buy) {
                    try {
                        buy.delete();

                        buysList.remove(buy);

                        buysTable.refresh();

                        updateReport();

                        customDialog(bundle.getString("buy_deleted"), bundle.getString("buy_deleted_msg"), INFO_SMALL, true, addQteBtn);
                    }
                    catch (SQLException e) {
                        exceptionLayout(e, addQteBtn);
                    }            
                        }
            };
            return cell;
        }; 
        
        actionCol.setCellFactory(cellFactory);
        
        buysTable.setItems(buysList);    
    }
    
}
