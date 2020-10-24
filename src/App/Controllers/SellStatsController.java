/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Product;
import Data.Sell;
import static Include.Common.dateFormatter;
import static Include.Common.getAllFrom;
import static Include.Common.getConnection;
import static Include.Common.initLayout;
import Include.SMController;
import Include.Init;
import animatefx.animation.ZoomIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author med
 */
public class SellStatsController extends SMController implements Initializable,Init {

    @FXML private Button search;
    @FXML private TableView<Sell> sellsTable;
    @FXML private TableColumn<Sell, String> prodCol,userCol,dateCol,priceCol,qteCol;
    @FXML private ChoiceBox<String> prodField ;
    @FXML private JFXDatePicker startDate,endDate;
    @FXML private LineChart nbrSellsChart;
    @FXML private BarChart sumSellsChart;
    @FXML private Label idCountLabel,qteCountLabel,priceSumLabel,averageSellLabel,averageQteLabel,averagePriceLabel,interval;
    @FXML private JFXTextField filterSearch;
    @FXML private StackPane filterPane;
    @FXML private ImageView filterBtn;
        
    ObservableList<Sell> sellsList = FXCollections.observableArrayList();
    ObservableList<String> nameList = null;
    
    public void loadDialog(JFXDialogLayout layout, boolean btnIncluded){
        
        stackPane.setVisible(true);
        JFXButton btn = new JFXButton(bundle.getString("okay"));
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
            initLayout(layout, bundle.getString("unknown_error"), e.getMessage(), ERROR_SMALL);
            
            loadDialog(layout, true);
    }

    public void customDialog(String title, String body, String icon, boolean btnIncluded){
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, title, body, icon);
            
            loadDialog(layout, btnIncluded);
    }

    private void getData(String name, String start, String end, String sortingType){
        
        try {
        
            try (Connection con = getConnection()) {
                String whereClause = "" ;
                String query ;
                if(!name.equals(bundle.getString("all"))){
                    whereClause = "WHERE name = '" + name + "' " ;
                }       if(!start.equals("")){
                    if(whereClause.equals("")){
                        whereClause = "WHERE date(sell_date) >= '" + start + "' " ;
                    }
                    else
                        whereClause += "AND date(sell_date) >= '" + start + "' " ;
                }       if(!end.equals("")){
                    if(whereClause.equals("")){
                        whereClause = "WHERE date(sell_date) <= '" + end + "' " ;
                    }
                    else
                        whereClause += "AND date(sell_date) <= '" + end + "' " ;
                }       query = "SELECT * FROM sell INNER JOIN product ON product.prod_id = sell.prod_id INNER JOIN user ON user.user_id = sell.user_id " + whereClause ;
                PreparedStatement st;
                ResultSet rs;
                st = con.prepareStatement(query);
                rs = st.executeQuery(query);
                while (rs.next()) {                   
                    
                    Sell sell = new Sell();
                    sell.setSellID(rs.getInt("sell_id"));
                    sell.setSellPrice(rs.getInt("sell.sell_price_unit"));
                    sell.setTotalPrice(rs.getInt("sell.sell_price"));
                    sell.setSellDate(rs.getTimestamp("sell_date").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd h.mm a")));
                    sell.setSellQuantity(rs.getInt("sell_quantity"));
                    sell.setSellName(rs.getString("name"));
                    sell.setSeller(rs.getString("username"));
                    
                    sellsList.add(sell);
                    
                }   String query1="",query2="" ;
                switch(sortingType){
                    
                    case "day" :
                        query1 = "SELECT date(sell_date) as first_field, count(*) FROM sell INNER JOIN product ON product.prod_id = sell.prod_id INNER JOIN user ON user.user_id = sell.user_id " + whereClause + "Group by first_field " ;
                        query2 = "SELECT date(sell_date) as first_field, SUM(sell.sell_price) FROM sell INNER JOIN product ON product.prod_id = sell.prod_id INNER JOIN user ON user.user_id = sell.user_id " + whereClause + "Group by first_field " ;
                        break;
                    case "week" :
                        query1 = "SELECT extract( week from sell_date ) as first_field, count(*) FROM sell INNER JOIN product ON product.prod_id = sell.prod_id INNER JOIN user ON user.user_id = sell.user_id " + whereClause + "Group by first_field " ;
                        query2 = "SELECT extract( week from sell_date ) as first_field, SUM(sell.sell_price) FROM sell INNER JOIN product ON product.prod_id = sell.prod_id INNER JOIN user ON user.user_id = sell.user_id " + whereClause + "Group by first_field " ;
                        break;
                    case "month" :
                        query1 = "SELECT extract( month from sell_date ) as first_field, count(*) FROM sell INNER JOIN product ON product.prod_id = sell.prod_id INNER JOIN user ON user.user_id = sell.user_id " + whereClause + "Group by first_field " ;
                        query2 = "SELECT extract( month from sell_date ) as first_field, SUM(sell.sell_price) FROM sell INNER JOIN product ON product.prod_id = sell.prod_id INNER JOIN user ON user.user_id = sell.user_id " + whereClause + "Group by first_field " ;
                        break;
                        
                }   sumSellsChart.getData().clear();
                XYChart.Series<String,Integer> series = new XYChart.Series<>();
                st = con.prepareStatement(query1);
                rs = st.executeQuery();
                while (rs.next()) {

                    series.getData().add(new XYChart.Data<>(rs.getString("first_field"),rs.getInt("count(*)")));
                    
                }
                sumSellsChart.getData().addAll(series);
                series.getData().forEach((data) -> {
                    data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event1) -> {
                        Tooltip.install(data.getNode(), new Tooltip(String.valueOf(data.getYValue())));
                    });
                });
                series.setName(bundle.getString("buys_per_day"));
                nbrSellsChart.getData().clear();
                XYChart.Series<String,Integer> lineSeries = new XYChart.Series<>();
                st = con.prepareStatement(query2);
                rs = st.executeQuery();
                while (rs.next()) {

                    lineSeries.getData().add(new XYChart.Data<>(rs.getString("first_field"),rs.getInt("SUM(sell.sell_price)")));

                }
                nbrSellsChart.getData().addAll(lineSeries);
                lineSeries.getData().forEach((data) -> {
                    data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event1) -> {
                        Tooltip.install(data.getNode(), new Tooltip(data.getYValue().toString()));
                    });
                });
                lineSeries.setName(bundle.getString("sum_per_day"));
                ResultSet stats1 = getAllFrom("COUNT(sell_id), SUM(sell_quantity), SUM(sell.sell_price)", "sell", "INNER JOIN product ON sell.prod_id = product.prod_id", whereClause,"");
                while(stats1.next()){
                    
                    idCountLabel.setText(stats1.getString("COUNT(sell_id)") != null?  stats1.getString("COUNT(sell_id)") + " " + bundle.getString("sell") : bundle.getString("zero_sell"));
                    qteCountLabel.setText(stats1.getString("SUM(sell_quantity)") != null?  stats1.getString("SUM(sell_quantity)") + " " + bundle.getString("pieces") : bundle.getString("zero_pieces"));
                    priceSumLabel.setText(stats1.getString("SUM(sell.sell_price)") != null?  stats1.getString("SUM(sell.sell_price)") + " " + bundle.getString("currency") : bundle.getString("zero_currency"));
                    
                }   ResultSet stats2 = getAllFrom("COUNT(sell_id)/datediff('" + end + "','" + start + "') as abd, SUM(sell_quantity)/datediff('" + end + "','" + start + "') as aqd, SUM(sell.sell_price)/datediff('" + end + "','" + start + "') as asd", "sell", "INNER JOIN product ON sell.prod_id = product.prod_id", whereClause,"");
                while(stats2.next()){
                    
                    averageSellLabel.setText(stats2.getString("abd") != null?  stats2.getString("abd") + " " + bundle.getString("sell") : bundle.getString("zero_sell"));
                    averageQteLabel.setText(stats2.getString("aqd") != null?  stats2.getString("aqd") + " " + bundle.getString("pieces") : bundle.getString("zero_pieces"));
                    averagePriceLabel.setText(stats2.getString("asd") != null?  stats2.getString("asd") + " " + bundle.getString("currency") : bundle.getString("zero_currency"));
                    
                }
            }
        }
        catch (SQLException e) {
            exceptionLayout(e);
        }         
        
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        initTable();
        
        startDate.setConverter(dateFormatter());
        endDate.setConverter(dateFormatter());
        startDate.getEditor().setText(String.valueOf(LocalDate.now().minusWeeks(1)));
        endDate.getEditor().setText(String.valueOf(LocalDate.now()));
        startDate.setValue(LocalDate.now().minusWeeks(1));
        endDate.setValue(LocalDate.now());

        prodField.setItems(nameList);
        prodField.getSelectionModel().selectFirst();
        
        
        getData(bundle.getString("all") ,startDate.getEditor().getText(),endDate.getEditor().getText(), "day");
        
        search.setOnAction(Action -> {
                        
            if(endDate.getValue().compareTo(startDate.getValue()) >= 0){

                int interv = endDate.getValue().getDayOfYear() - startDate.getValue().getDayOfYear();
                    
                if(startDate.getValue().compareTo(LocalDate.now()) <= 0 || endDate.getValue().compareTo(LocalDate.now()) <= 0){

                    if(endDate.getValue().equals(LocalDate.now())){
                        switch(interv){
                            case 0:
                                interval.setText(bundle.getString("last_day_sells"));
                                break;
                            case 7:
                                interval.setText(bundle.getString("last_week_sells"));
                                break;
                            case 30:
                                interval.setText(bundle.getString("last_month_sells"));
                                break;
                            case 365:
                                interval.setText(bundle.getString("last_year_sells"));
                                break;
                            default:
                                interval.setText(bundle.getString("sells_of_last") + interv + " " + bundle.getString("day"));
                                break;
                        }
                    }
                    else{
                        interval.setText(bundle.getString("sells") + " " + startDate.getValue().toString() + "  -----  " + endDate.getValue().toString());
                    }

                    if(endDate.getValue().compareTo(startDate.getValue()) <= 1  || endDate.getValue().compareTo(startDate.getValue()) <= 30 ){

                        sellsTable.getItems().clear();
                        getData(prodField.getValue(),startDate.getEditor().getText(),endDate.getEditor().getText(),"day");            

                    }
                    else if(endDate.getValue().compareTo(startDate.getValue()) <= 3  ){

                        sellsTable.getItems().clear();
                        getData(prodField.getValue(),startDate.getEditor().getText(),endDate.getEditor().getText(),"week");                 

                    }
                    else if(endDate.getValue().getDayOfYear() - startDate.getValue().getDayOfYear() <= 12 ){

                        sellsTable.getItems().clear();
                        getData(prodField.getValue(),startDate.getEditor().getText(),endDate.getEditor().getText(),"month");                 

                    }            
                else
                {              
                    customDialog(bundle.getString("illegal_interval"), bundle.getString("illegal_interval_msg") , INFO_SMALL, true);                
                }
                
                }
                else{
                    customDialog(bundle.getString("invalid_interval"), bundle.getString("invalid_interval_msg"), INFO_SMALL, true, search);
                }


            }
            else {
                customDialog(bundle.getString("illegal_interval"), bundle.getString("illegal_interval_msg") , INFO_SMALL, true); 
            }             
           
        });
        
        filterSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            search(filterSearch, sellsList, sellsTable);
        });
        
        filterBtn.setOnMouseClicked(value -> {
            filterPane.setVisible(true);
            new ZoomIn(filterPane).play();
        });        
    }
    
    public void search(JFXTextField field, ObservableList<Sell> list, TableView table)
    {
        
        String keyword = field.getText();
        
        if (keyword.trim().equals("")) {
            table.setItems(list);
        }
        
        else {
            ObservableList<Sell> filteredBuys = FXCollections.observableArrayList();
            list.stream().filter((sell) -> (sell.getProduct().getName().toLowerCase().contains(keyword.toLowerCase()) || sell.getSeller().toLowerCase().contains(keyword.toLowerCase()))).forEachOrdered((sell) -> {
                filteredBuys.add(sell);
            });
            table.setItems(filteredBuys);
        }
      
    }   

    private void initTable() {
        
        try {
            nameList = Product.getActiveProductNames(); 
            nameList.add(0, bundle.getString("all"));
       } catch (SQLException ex) {
            exceptionLayout(ex);
        }
        
        prodCol.setCellValueFactory(new PropertyValueFactory<>("sellName"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("seller"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("sellDate"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        qteCol.setCellValueFactory(new PropertyValueFactory<>("sellQuantity"));
        
        
        sellsTable.setItems(sellsList);
        sellsTable.getSelectionModel().selectFirst();
    }
    
}
