/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Buy;
import Data.Employer;
import static Include.Common.animateBtn;
import static Include.Common.getConnection;
import static Include.Common.getProductByName;
import static Include.Common.initLayout;
import Include.Init;
import static Include.Init.BUY_DELETED;
import static Include.Init.BUY_DELETED_MSG;
import static Include.Init.CONNECTION_ERROR;
import static Include.Init.CONNECTION_ERROR_MESSAGE;
import static Include.Init.ERROR_SMALL;
import static Include.Init.IMAGES_PATH;
import static Include.Init.INVALID_QTE;
import static Include.Init.INVALID_QTE_MSG;
import static Include.Init.OKAY;
import static Include.Init.SELL_ADDED;
import static Include.Init.SELL_ADDED_MESSAGE;
import static Include.Init.UNKNOWN_ERROR;
import JR.JasperReporter;
import animatefx.animation.Pulse;
import animatefx.animation.SlideInDown;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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
    @FXML Button cancel;
    @FXML JFXButton addQteBtn,printBtn;
    @FXML ChoiceBox<String> nameBox;
    @FXML Label sum, total, qte;
    @FXML JFXTextField qteField,priceField;
    @FXML private StackPane stackPane;
    @FXML private JFXDialog dialog;
    @FXML private HBox topBar;
    @FXML private NumberValidator validator;
    private Employer employer = new Employer();
    
    private final ObservableList<Buy> buysList = FXCollections.observableArrayList();  
    
    ObservableList<String> nameList = FXCollections.observableArrayList();
    
    public void loadDialog(JFXDialogLayout layout, boolean btnIncluded){
        
        stackPane.setVisible(true);
        JFXButton btn = new JFXButton(OKAY);
        btn.setDefaultButton(true);
        addQteBtn.setDefaultButton(false);
        btn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            btn.setDefaultButton(false);
            addQteBtn.setDefaultButton(true);
        });
        if(btnIncluded){
            layout.setActions(btn);
        }    
        dialog = new JFXDialog(stackPane, layout , JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.show();
        
    }
    
    public void exceptionLayout(Exception e){
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);
            
            loadDialog(layout, true);
    }

    public void customDialog(String title, String body, String icon, boolean btnIncluded){
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, title, body, icon);
            
            loadDialog(layout, btnIncluded);
    }
        
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
            exceptionLayout(e);
        } 
        
    }
    
    private void insertBuy()
    {
                int insertedBuyID = 0 ;        
        
        if (checkInputs()) {
            try {

                try (Connection con = getConnection()) {
                    if(con == null) {
                        customDialog(CONNECTION_ERROR, CONNECTION_ERROR_MESSAGE, ERROR_SMALL, true);
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
                    ps.setInt(6, getProductByName(nameBox.getSelectionModel().getSelectedItem()).getProdID());
                    ps.executeUpdate();
                    
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            
                            insertedBuyID = generatedKeys.getInt(1) ;
                        }
                        else {
                            customDialog(UNKNOWN_ERROR, "Creating key failed, no ID obtained.", ERROR_SMALL, true);
                        }
                    }
                    ps = con.prepareStatement("UPDATE product SET prod_quantity = prod_quantity + ?, nbrBuys = nbrBuys + 1 WHERE prod_id = ?");
                    ps.setInt(1, Integer.parseInt(qteField.getText()));
                    ps.setInt(2, getProductByName(nameBox.getSelectionModel().getSelectedItem()).getProdID());
                    ps.executeUpdate();
                }

                Buy AddedBuy = new Buy();
                
                AddedBuy.setProduct(nameBox.getSelectionModel().getSelectedItem());
                AddedBuy.setBuyID(insertedBuyID);
                AddedBuy.setBuyPrice(Integer.parseInt(priceField.getText()) * Integer.parseInt(qteField.getText()));
                AddedBuy.setBuyQte(Integer.parseInt(qteField.getText()));
                
                buysList.add(AddedBuy);
                
                buysTable.refresh();
                
                updateReport();
                
                resetWindow();

                new Pulse(buysTable).play();
                
                customDialog(SELL_ADDED, SELL_ADDED_MESSAGE, INFO_SMALL, true);

            }
            catch (NumberFormatException | SQLException e) {
                exceptionLayout(e);
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
                
                sum.setText(sumTemp + " بيع");
                total.setText(totalTemp + " دج");
                qte.setText(qteTemp + " قطعة");        
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
        
        idCol.setCellValueFactory(new PropertyValueFactory<>("buyID"));
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
                        new Pulse(buysTable).play();
                    });
                    delete.setGraphic(new ImageView(new Image(IMAGES_PATH + "small/trash_small_white.png", 24, 24, false, false)));
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
                    
                    updateReport();
                    
                    customDialog(BUY_DELETED, BUY_DELETED_MSG, INFO_SMALL, true);
                }
                catch (SQLException e) {
                    exceptionLayout(e);
                }            
                    }
        };
        return cell;
    }; 
        
        actionCol.setCellFactory(cellFactory);
        
        buysTable.setItems(buysList);        
        
        getAllProducts();
               
        nameBox.setItems(nameList);
        
        nameBox.getSelectionModel().select(0);        
        
        addQteBtn.setOnAction(Action ->{
            
            insertBuy();
            
        });
        
        printBtn.disableProperty().bind(Bindings.size(buysTable.getItems()).isEqualTo(0));
        
        printBtn.setOnAction(Action ->{
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
            
                String selectedBuys = "";

                selectedBuys = buysTable.getItems().stream().map((buy) ->  buy.getBuyID() + ",").reduce(selectedBuys, String::concat);
                selectedBuys = selectedBuys.substring(0, selectedBuys.length() - 1);
                jr.params.put("selectedBuys", selectedBuys);                        
                jr.ShowReport("buy","");     
                dialog.close();
                stackPane.setVisible(false);

            });
            
            th.start();

        });
        
        new SlideInDown(topBar).play();
        
        animateBtn(addQteBtn);
    }    

    private boolean checkInputs() {

        try {
            Integer.parseInt(qteField.getText());
            if(Integer.parseInt(qteField.getText()) > 0 ){
                try {
                    Integer.parseInt(priceField.getText());
                    if(Integer.parseInt(priceField.getText()) > 0 )
                    return true;
                    else{
                        customDialog(INVALID_PRICE, INVALID_PRICE_MSG, ERROR_SMALL, true);
                        return false;
                    }
                }
                catch (NumberFormatException e) {
                    customDialog(INVALID_PRICE, INVALID_PRICE_MSG, ERROR_SMALL, true);
                    return false;
                } 
            }
            else{
                customDialog(INVALID_QTE, INVALID_QTE_MSG, ERROR_SMALL, true);
                return false;
            }
        }
        catch (NumberFormatException e) {
            customDialog(INVALID_QTE, INVALID_QTE_MSG, ERROR_SMALL, true);
            return false;
        }
        
        
        
    }
    
    JasperReporter jr = new JasperReporter();
    
    private void onJasperReportLoading(){
        
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, PLEASE_WAIT, REPORT_WAIT_MESSAGE, WAIT_SMALL);
            
            loadDialog(layout, false);
            
            jr = new JasperReporter();
    }

    private void resetWindow() {
       
        nameBox.getSelectionModel().select(0);
        priceField.setText("");
        qteField.setText("");
        
    }
    
}
