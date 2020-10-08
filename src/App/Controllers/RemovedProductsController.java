/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Product;
import Data.User;
import static Include.Common.initLayout;
import static Include.Common.startStage;
import Include.GDPController;
import Include.Init;
import static Include.Init.IMAGES_PATH;
import static Include.Init.INFO_SMALL;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author user
 */
public class RemovedProductsController extends GDPController implements Initializable,Init {

    @FXML private TableView<Product> removedTable;
    @FXML private TableColumn<Product,String> nameCol;
    @FXML private TableColumn action, action1;
    @FXML private Button returnBtn;
    @FXML private JFXTextField searchField;
    
    ObservableList<Product> removedList = FXCollections.observableArrayList();
        
    
    public void getInfo(User employer){
        
        this.employer = employer;
        
    }
    
    public void confirmDialog(Object object, String type, String title, String body, String icon){
            
        JFXDialogLayout layout = new JFXDialogLayout();
        initLayout(layout, title, body, icon);
            
        stackPane.setVisible(true);
        JFXButton yesBtn = new JFXButton(bundle.getString("yes"));
        yesBtn.setDefaultButton(true);
        yesBtn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            yesBtn.setDefaultButton(false);
            Product product = (Product) object;
            try {
                product.delete();
            } catch (SQLException ex) {
                exceptionLayout(ex, returnBtn);
            }
            removedList.remove(product);
            removedTable.refresh();
        });
        JFXButton noBtn = new JFXButton(bundle.getString("no"));
        noBtn.setCancelButton(true);
        noBtn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            noBtn.setCancelButton(false);
        });        
        
        layout.setActions(yesBtn, noBtn);
        
        dialog = new JFXDialog(stackPane, layout , JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.show();
    
    }
    
    
    @Override
    public void logOut(ActionEvent event) throws IOException {

        ((Node)event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
        AnchorPane root = (AnchorPane)loader.load();
        MainController mControl = (MainController)loader.getController();
        mControl.getEmployer(employer);
        mControl.returnMenu("products");
        startStage(root, (int)root.getWidth(), (int)root.getHeight());
            
    }  
    
    
    private void initTable() {
        
        try {
            removedList = Product.getRemovedProducts();
        }
        catch (SQLException e) {
            exceptionLayout(e, returnBtn);
        }   
        
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));        
        action.setCellValueFactory(new PropertyValueFactory<>("action"));        
                   
        Callback<TableColumn<Product, String>, TableCell<Product, String>> cellFactory
                =                 //
        (final TableColumn<Product, String> param) -> {
            final TableCell<Product, String> cell = new TableCell<Product, String>() {

                final Button reassign = new Button();

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        reassign.setGraphic(new ImageView(new Image(IMAGES_PATH + "small/active_small_grey.png", 24, 24, false, false)));
                        reassign.setOnAction(event -> {
                            try {
                                Product selected = getTableView().getItems().get(getIndex());
                                selected.restore();
                                removedList.remove(selected);
                                removedTable.refresh();
                                customDialog(bundle.getString("product_activated"), bundle.getString("product_activated_msg"), INFO_SMALL, true, returnBtn);
                            } catch (SQLException ex) {
                                exceptionLayout(ex, returnBtn);
                            }
                        });
                        reassign.setStyle("-fx-background-color : #3d4956; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                        setGraphic(reassign);
                        setText(null);               

                    }
                }  

            };
            return cell;
        }; 

        action.setCellFactory(cellFactory);
        
        action1.setCellValueFactory(new PropertyValueFactory<>("action1"));        
                   
        Callback<TableColumn<Product, String>, TableCell<Product, String>> cellFactory1
                =                 //
        (final TableColumn<Product, String> param) -> {
            final TableCell<Product, String> cell = new TableCell<Product, String>() {

                final Button delete = new Button();

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        delete.setGraphic(new ImageView(new Image(IMAGES_PATH + "small/trash_small_white.png", 24, 24, false, false)));
                        delete.setOnAction(event -> {
                            Product selected = getTableView().getItems().get(getIndex());
                            confirmDialog(selected, "", bundle.getString("delete"), bundle.getString("are_u_sure"), QUESTION_SMALL);
                        });
                        delete.setStyle("-fx-background-color : red; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                        setGraphic(delete);
                        setText(null);               

                    }
                }  

            };
            return cell;
        }; 

        action1.setCellFactory(cellFactory1);         

        removedTable.setItems(removedList);      
    }
    
    private void search()
    {
        
        String keyword = searchField.getText();

        if (keyword.trim().equals("")) {
            removedTable.setItems(removedList);
        }
        else {
            ObservableList<Product> filteredData = FXCollections.observableArrayList();
            removedList.stream().filter((product) -> (product.getName().toLowerCase().contains(keyword.toLowerCase()))).forEachOrdered((product) -> {
                filteredData.add(product);
             });
            removedTable.setItems(filteredData);
        }           
        
    }  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        initTable();
        
        returnBtn.setOnAction(value -> {
            try {
                logOut(value);
            } catch (IOException ex) {
                exceptionLayout(ex, returnBtn);
            }
        });
        
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            search();
        });
    }    
    
}
