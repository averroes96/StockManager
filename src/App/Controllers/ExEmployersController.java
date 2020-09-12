/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Employer;
import static Include.Common.getConnection;
import static Include.Common.initLayout;
import Include.Init;
import static Include.Init.ERROR_SMALL;
import static Include.Init.OKAY;
import static Include.Init.UNKNOWN_ERROR;
import animatefx.animation.FlipInX;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author med
 */
public class ExEmployersController implements Initializable,Init {

    @FXML TableView<Employer> exTable;
    @FXML TableColumn<Employer,Integer> id;
    @FXML TableColumn<Employer,String> fullname,username;
    @FXML TableColumn action1;
    @FXML Button returnBtn;
    @FXML private StackPane stackPane;
    @FXML private JFXDialog dialog;
    
    ObservableList<Employer> exList = FXCollections.observableArrayList();
        
    Employer admin = new Employer();
    
    public void getInfo(Employer employer){
        
        this.admin = employer;
        
    }
    
    public void loadDialog(JFXDialogLayout layout, boolean btnIncluded){
        
        stackPane.setVisible(true);
        JFXButton btn = new JFXButton(OKAY);
        btn.setDefaultButton(true);
        btn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            btn.setDefaultButton(false);
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
    
    
    public void restore(Employer selected) {
    
        try {
                       

            try (Connection con = getConnection()) {
                String query = "UPDATE user SET active = 1 WHERE user_id = ?";
                
                PreparedStatement ps = con.prepareStatement(query);
                
                ps.setInt(1, selected.getUserID());
                
                ps.executeUpdate();
            }
            
            exList.remove(selected);
            
            exTable.refresh();
            
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, EMPLOYER_ASSIGNED, EMPLOYER_ASSIGNED_MSG, INFO_SMALL);
            
            loadDialog(layout, true);
        }
        catch (SQLException e) {
            exceptionLayout(e);
        }        
    
    }    
    
    public void FillTheTable(){
        
        Connection con = getConnection();
        
        String query = "SELECT * FROM user WHERE active = 0";

        Statement st;
        ResultSet rs;
        

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                Employer employer = new Employer();
                employer.setUserID(rs.getInt("user_id"));
                employer.setFullname(rs.getString("fullname"));
                employer.setUsername(rs.getString("username"));
                employer.setAdmin(rs.getInt("admin"));

                exList.add(employer);
            }

            con.close();
        }
        catch (SQLException e) {
            exceptionLayout(e);
        }        
        
    }
    
    public void cancel(ActionEvent event) throws IOException {

                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource( FXML_PATH + "Main.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        MainController mControl = (MainController)loader.getController();
                        mControl.getEmployer(admin);
                        mControl.returnMenu("employers");
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        //stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.setMinHeight(600);
                        stage.setMinWidth(1000);
                        stage.show();            
            
    }    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        FillTheTable();
       
        id.setCellValueFactory(new PropertyValueFactory<>("userID"));
        fullname.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        action1.setCellValueFactory(new PropertyValueFactory<>("action1"));        
                   
        Callback<TableColumn<Employer, String>, TableCell<Employer, String>> cellFactory
                =                 //
    (final TableColumn<Employer, String> param) -> {
        final TableCell<Employer, String> cell = new TableCell<Employer, String>() {
            
            final Button reassign = new Button("تشغيل");
            
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    reassign.setOnAction(event -> {
                        Employer selected = getTableView().getItems().get(getIndex());
                        restore(selected);
                        new FlipInX(exTable).play();
                    });
                    reassign.setStyle("-fx-background-color : green; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                    setGraphic(reassign);
                    setText(null);               
                    
                }
            }


        };
        return cell;
    }; 
        
    action1.setCellFactory(cellFactory);    
        
        exTable.setItems(exList);
        
        
        // TODO
    }    
    
}
