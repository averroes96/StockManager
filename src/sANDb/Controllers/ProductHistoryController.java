
package sANDb.Controllers;

import Data.ProdHistory;
import static Include.Common.dateFormatter;
import static Include.Common.getAllProducts;
import static Include.Common.getConnection;
import Include.Init;
import static Include.Init.UNKNOWN_ERROR;
import Include.SpecialAlert;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author user
 */
public class ProductHistoryController implements Initializable,Init {
    
    @FXML private Label oldQte,newQte,oldName,newName,oldDate,newDate,oldPrice,newPrice;
    @FXML private Button search;
    @FXML private TableView<ProdHistory> historyTable;
    @FXML private TableColumn<ProdHistory, Integer> idCol;
    @FXML private TableColumn<ProdHistory, String> prodCol,userCol,dateCol;
    @FXML private ChoiceBox<String> prodField ;
    @FXML private DatePicker startDate,endDate;
    
    SpecialAlert alert = new SpecialAlert();
    
    ObservableList<ProdHistory> historyList = FXCollections.observableArrayList();
    ObservableList<String> nameList = getAllProducts(1);
    
    private void getAllHistory(String name, String start, String end){
        
        Connection con = getConnection();
        String whereClause = "" ;
        String query ;
        
        if(!name.equals("الكل")){
            whereClause = "WHERE name = '" + name + "' " ;
        }
        
        if(!start.equals("")){
            if(whereClause.equals("")){
                whereClause = "WHERE date(change_date) >= '" + start + "' " ;
            }
            else
                whereClause += "AND date(change_date) >= '" + start + "' " ;
        }
        
        if(!end.equals("")){
            if(whereClause.equals("")){
                whereClause = "WHERE date(change_date) <= '" + end + "' " ;
            }
            else
                whereClause += "AND date(change_date) <= '" + end + "' " ;
        }        

        
        query = "SELECT * FROM product_history INNER JOIN product ON product.prod_id = product_history.prod_id INNER JOIN user ON user.user_id = product_history.user_id " + whereClause ;

        PreparedStatement st;
        ResultSet rs;
        

        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                ProdHistory history = new ProdHistory();
                
                history.setDate(rs.getTimestamp("change_date").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy h.mm a")));
                history.setProdHistID(rs.getInt("prod_hist_id"));
                history.setProduct(rs.getString("name"));
                history.setUser(rs.getString("username"));
                history.setNewDate(rs.getString("new_date"));
                history.setOldDate(rs.getString("old_date"));
                history.setNewName(rs.getString("new_name"));
                history.setOldName(rs.getString("old_name"));
                history.setNewPrice(rs.getInt("new_price"));
                history.setOldPrice(rs.getInt("old_price"));
                history.setNewQte(rs.getInt("new_qte"));
                history.setOldQte(rs.getInt("old_qte"));
                
                historyList.add(history);
            }

            con.close();
        }
        catch (SQLException e) {
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);

        }

        
        
    }
    
    private void setValues(ProdHistory selected){
        
        oldName.setText(selected.getOldName());
        oldPrice.setText(String.valueOf(selected.getOldPrice()));
        oldQte.setText(String.valueOf(selected.getOldQte()));
        oldDate.setText(selected.getOldDate());
        
        if(selected.nameIsChanged()){
            newName.setText(selected.getNewName());
            oldName.setStyle("-fx-strikthrough: true");
        }
        else{
            newName.setText("/");
            oldName.setStyle("-fx-strikthrough: false");
        }

        if(selected.dateIsChanged()){
            newDate.setText(selected.getNewDate());
            oldDate.setStyle("-fx-strikthrough: true");
        }
        else{
            newDate.setText("/");
            oldDate.setStyle("-fx-strikthrough: false");
        }

        if(selected.priceIsChanged()){
            newPrice.setText(String.valueOf(selected.getNewPrice()));
            oldPrice.setStyle("-fx-strikthrough: true");
        }
        else{
            newPrice.setText("/");
            oldPrice.setStyle("-fx-strikthrough: false");
        }

        if(selected.qteIsChanged()){
            newQte.setText(String.valueOf(selected.getNewQte()));
            oldQte.setStyle("-fx-strikthrough: true");
        }
        else{
            newQte.setText("/");
            oldQte.setStyle("-fx-strikthrough: false");
        }       
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        idCol.setCellValueFactory(new PropertyValueFactory<>("prodHistID"));
        prodCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        getAllHistory("الكل","","");
        historyTable.setItems(historyList);
        historyTable.getSelectionModel().selectFirst();
        
        startDate.setConverter(dateFormatter());
        endDate.setConverter(dateFormatter());
        startDate.getEditor().setText(String.valueOf(LocalDate.now().minusWeeks(1)));
        endDate.getEditor().setText(String.valueOf(LocalDate.now()));
        startDate.setValue(LocalDate.now().minusWeeks(1));
        endDate.setValue(LocalDate.now());

        prodField.setItems(nameList);
        prodField.getSelectionModel().selectFirst();
        
        if(!historyList.isEmpty()){
        setValues(historyTable.getSelectionModel().getSelectedItem());
        }
        
        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setValues(historyTable.getSelectionModel().getSelectedItem());
            }
        });
        
        search.setOnAction(Action ->{
            
        if(endDate.getValue().compareTo(startDate.getValue()) >= 0){
            
            historyTable.getItems().clear();
            getAllHistory(prodField.getSelectionModel().getSelectedItem(),startDate.getEditor().getText(),endDate.getEditor().getText());            
        
        }
        else {
            
            alert.show(ILLEGAL_INTERVAL, ILLEGAL_INTERVAL_MSG, Alert.AlertType.WARNING,false);
            
        }             
                      
        });
        
        
        
    }    


    
}
