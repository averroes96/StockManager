
package App.Controllers;

import Data.ProdHistory;
import Data.Product;
import static Include.Common.dateFormatter;
import static Include.Common.getConnection;
import Include.Init;
import Include.SMController;
import animatefx.animation.ZoomIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
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
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author user
 */
public class ProductHistoryController extends SMController implements Initializable,Init {
    
    @FXML private Label oldQte,newQte,oldName,newName,oldDate,newDate,oldPrice,newPrice;
    @FXML private JFXButton search;
    @FXML private TableView<ProdHistory> historyTable;
    @FXML private TableColumn<ProdHistory, String> prodCol,userCol,dateCol;
    @FXML private ChoiceBox<String> prodField ;
    @FXML private JFXDatePicker startDate,endDate;
    @FXML private VBox nameVB,dateVB,priceVB,qteVB;
        
    ObservableList<ProdHistory> historyList = FXCollections.observableArrayList();
    ObservableList<String> nameList = null;

    
    private void getAllHistory(String name, String start, String end){
        
        try {
        
            try (Connection con = getConnection()) {
                String whereClause = "" ;
                String query ;
                
                if(!name.equals(bundle.getString("all"))){
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
            }
        }
        catch (SQLException e) {
            exceptionLayout(e, search);
        }
        if(!historyTable.getItems().isEmpty())
            setValues(historyTable.getItems().get(0));
        
        
    }
    
    public void setValues(ProdHistory selected){
        
        animateNode(new ZoomIn(nameVB));
        animateNode(new ZoomIn(dateVB));
        animateNode(new ZoomIn(priceVB));
        animateNode(new ZoomIn(qteVB));
        
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
    
    private void initTable(){
        
        prodCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        historyTable.setItems(historyList);
        getAllHistory(bundle.getString("all"),"","");
        historyTable.getSelectionModel().selectFirst();        
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        if(bundle.getLocale().getLanguage().equals("ar"))
            anchorPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        
        initTable();
        
        try {
            nameList = Product.getActiveProductNames();
            nameList.add(0, bundle.getString("all"));
            isAnimated();
        } catch (SQLException ex) {
            exceptionLayout(ex, search);
        }
        
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
            
            animateNode(new ZoomIn(historyTable));

            if(endDate.getValue().compareTo(startDate.getValue()) >= 0){
                
                if(startDate.getValue().compareTo(LocalDate.now()) <= 0 || endDate.getValue().compareTo(LocalDate.now()) <= 0){
                    historyTable.getItems().clear();
                    getAllHistory(prodField.getSelectionModel().getSelectedItem(),startDate.getEditor().getText(),endDate.getEditor().getText());            
                }
                else{
                    customDialog(bundle.getString("invalid_interval"), bundle.getString("invalid_interval_msg"), INFO_SMALL, true, search);         
                }
                }
                
            else {
                customDialog(bundle.getString("illegal_interval"), bundle.getString("illegal_interval_msg"), INFO_SMALL, true, search);         
            }             
                      
        });
        
        
        
    }    


    
}
