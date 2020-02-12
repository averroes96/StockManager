/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sANDb;

import Data.Employer;
import Data.Product;
import static Include.Common.getConnection;
import static Include.Common.minimize;
import Include.Init;
import static Include.Init.EMPLOYER_ASSIGNED;
import static Include.Init.EMPLOYER_ASSIGNED_MSG;
import static Include.Init.UNKNOWN_ERROR;
import Include.SpecialAlert;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author user
 */
public class RemovedProductsController implements Initializable,Init {

    @FXML private TableView<Product> removedTable;
    @FXML private TableColumn<Product,Integer> idCol,qteCol,priceCol;
    @FXML private TableColumn<Product,String> nameCol;
    @FXML private TableColumn action;
    @FXML private Label minimize;
    @FXML private Button returnBtn;
    
    ObservableList<Product> removedList = FXCollections.observableArrayList();
    
    SpecialAlert alert = new SpecialAlert();
    
    Employer admin = new Employer();
    
    private double xOffset;
    private double yOffset;
    
    public void getInfo(Employer employer){
        
        this.admin = employer;
        
    }    
    
    
    public void cancel(ActionEvent event) throws IOException {

                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(admin);
                        mControl.returnMenu("products");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource("Layout/custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource("Layout/buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.show();
                        root.setOnMousePressed((MouseEvent mevent) -> {
                            xOffset = mevent.getSceneX();
                            yOffset = mevent.getSceneY();
                        });
                        root.setOnMouseDragged((MouseEvent mevent) -> {
                            stage.setX(mevent.getScreenX() - xOffset);
                            stage.setY(mevent.getScreenY() - yOffset);
                        });                         
                        
            
    }
    
    public void FillTheTable(){
        
        Connection con = getConnection();
        
        String query = "SELECT * FROM product WHERE on_hold = 1";

        Statement st;
        ResultSet rs;
        

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                Product product = new Product();
                product.setName(rs.getString("name"));
                product.setProdID(rs.getInt("prod_id"));
                product.setProdQuantity(rs.getInt("prod_quantity"));
                product.setSellPrice(rs.getInt("sell_price"));

                removedList.add(product);
            }

            con.close();
        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        }        
        
    }    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        FillTheTable();
       
        idCol.setCellValueFactory(new PropertyValueFactory<>("prodID"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        qteCol.setCellValueFactory(new PropertyValueFactory<>("prodQuantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        
        action.setCellValueFactory(new PropertyValueFactory<>("action"));        
                   
        Callback<TableColumn<Product, String>, TableCell<Product, String>> cellFactory
                =                 //
    (final TableColumn<Product, String> param) -> {
        final TableCell<Product, String> cell = new TableCell<Product, String>() {
            
            final Button reassign = new Button("تفعيل");
            
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    reassign.setOnAction(event -> {
                        Product selected = getTableView().getItems().get(getIndex());
                        restore(selected);
                    });
                    reassign.setStyle("-fx-background-color : green; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                    setGraphic(reassign);
                    setText(null);               
                    
                }
            }

    public void restore(Product selected) {
    
        try {
                       

            try (Connection con = getConnection()) {
                String query = "UPDATE product SET on_hold = 0 WHERE prod_id = ?";
                
                PreparedStatement ps = con.prepareStatement(query);
                
                ps.setInt(1, selected.getProdID());
                
                ps.executeUpdate();
            }
            
            removedList.remove(selected);
            
            removedTable.refresh();
            
                alert.show(EMPLOYER_ASSIGNED, EMPLOYER_ASSIGNED_MSG, Alert.AlertType.INFORMATION,false);

        }
        catch (SQLException e) {
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        }        
        
    
    }    


        };
        return cell;
    }; 
        
    action.setCellFactory(cellFactory);    
        
        removedTable.setItems(removedList);        
        
        minimize.setOnMouseClicked(Action -> {
        
            minimize(Action);
            
        });        
        
        
    }    
    
}
