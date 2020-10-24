/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.User;
import static Include.Common.initLayout;
import static Include.Common.startStage;
import Include.SMController;
import Include.Init;
import static Include.Init.IMAGES_PATH;
import animatefx.animation.FlipInX;
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
 * @author med
 */
public class ExEmployersController extends SMController implements Initializable,Init {

    @FXML TableView<User> exTable;
    @FXML TableColumn<User,String> fullname,username;
    @FXML TableColumn action1, action2;
    @FXML JFXTextField searchField;
    @FXML Button returnBtn;
    
    ObservableList<User> exList = FXCollections.observableArrayList();
    
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
            User user = (User) object;
            try {
                user.delete();
            } catch (SQLException ex) {
                exceptionLayout(ex, returnBtn);
            }
            exList.remove(user);
            exTable.refresh();
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
    
    public void getInfo(User employer){
        this.employer = employer;
    }    
    
    public void restore(User selected) {
    
        try {
            
            selected.restore();
            exList.remove(selected);
            exTable.refresh();
            
            customDialog(bundle.getString("user_activated"), bundle.getString("user_activated_msg"), INFO_SMALL, true, returnBtn);
        }
        catch (SQLException e) {
            exceptionLayout(e, returnBtn);
        }        
    
    }    
    
    public void fillList(){
        
        try {
            exList = User.getUsers(NOT_ACTIVE);
        }
        catch (SQLException e) {
            exceptionLayout(e, returnBtn);
        }        
        
    }
    
    @Override
    public void logOut(ActionEvent event) throws IOException {

        ((Node)event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource( FXML_PATH + "Main.fxml"), bundle);
        AnchorPane root = (AnchorPane)loader.load();
        MainController mControl = (MainController)loader.getController();
        mControl.getEmployer(employer);
        mControl.returnMenu("employers");
        startStage(root, (int)root.getWidth(), (int)root.getHeight());
            
    } 
    
    public void initTable(){
        
        fullname.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        action1.setCellValueFactory(new PropertyValueFactory<>("action1"));        
                   
        Callback<TableColumn<User, String>, TableCell<User, String>> cellFactory
                =                 //
        (final TableColumn<User, String> param) -> {
            final TableCell<User, String> cell = new TableCell<User, String>() {

                final Button reassign = new Button(bundle.getString("activate"));

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        reassign.setGraphic(new ImageView(new Image(IMAGES_PATH + "small/active_small_grey.png", 24, 24, false, false)));
                        reassign.setOnAction(event -> {
                            User selected = getTableView().getItems().get(getIndex());
                            restore(selected);
                            new FlipInX(exTable).play();
                        });
                        reassign.setStyle("-fx-background-color : #3d4956; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                        setGraphic(reassign);
                        setText(null);               

                    }
                }


            };
            return cell;
        }; 

        action1.setCellFactory(cellFactory);
        
        action2.setCellValueFactory(new PropertyValueFactory<>("action1"));        
                   
        Callback<TableColumn<User, String>, TableCell<User, String>> cellFactory1
                =                 //
        (final TableColumn<User, String> param) -> {
            final TableCell<User, String> cell = new TableCell<User, String>() {

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
                            User selected = getTableView().getItems().get(getIndex());
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
        
        exTable.setItems(exList);        
        
    }
    
    private void search()
    {
        
        String keyword = searchField.getText();

        if (keyword.trim().equals("")) {
            exTable.setItems(exList);
        }
        else {
            ObservableList<User> filteredData = FXCollections.observableArrayList();
            exList.stream().filter((user) -> (user.getUsername().toLowerCase().contains(keyword.toLowerCase()) || user.getFullname().toLowerCase().contains(keyword.toLowerCase()))).forEachOrdered((user) -> {
                filteredData.add(user);
             });
            exTable.setItems(filteredData);
        }           
        
    }  
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        fillList();
        
        initTable();
        
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            search();
        });
        
        returnBtn.setOnAction(value -> {
            try {
                logOut(value);
            } catch (IOException ex) {
                exceptionLayout(ex, returnBtn);
            }
        });
        
    }    
    
}
