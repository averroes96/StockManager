/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.Sell;
import static Include.Common.dateFormatter;
import static Include.Common.getAllFrom;
import static Include.Common.getAllProducts;
import static Include.Common.getConnection;
import Include.Init;
import static Include.Init.UNKNOWN_ERROR;
import Include.SpecialAlert;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author med
 */
public class SellStatsController implements Initializable,Init {

    @FXML private Button search;
    @FXML private TableView<Sell> sellsTable;
    @FXML private TableColumn<Sell, Integer> idCol;
    @FXML private TableColumn<Sell, String> prodCol,userCol,dateCol,priceCol,qteCol;
    @FXML private ChoiceBox<String> prodField ;
    @FXML private JFXDatePicker startDate,endDate;
    @FXML private LineChart nbrSellsChart;
    @FXML private BarChart sumSellsChart;
    @FXML private Label idCountLabel,qteCountLabel,priceSumLabel,averageBuyLabel,averageQteLabel,averagePriceLabel;
    
    SpecialAlert alert = new SpecialAlert();
    
    ObservableList<Sell> sellsList = FXCollections.observableArrayList();
    ObservableList<String> nameList = getAllProducts(1);

    private void getData(String name, String start, String end, String sortingType){
        
        Connection con = getConnection();
        String whereClause = "" ;
        String query ;
        
        if(!name.equals("الكل")){
            whereClause = "WHERE name = '" + name + "' " ;
        }
        
        if(!start.equals("")){
            if(whereClause.equals("")){
                whereClause = "WHERE date(sell_date) >= '" + start + "' " ;
            }
            else
                whereClause += "AND date(sell_date) >= '" + start + "' " ;
        }
        
        if(!end.equals("")){
            if(whereClause.equals("")){
                whereClause = "WHERE date(sell_date) <= '" + end + "' " ;
            }
            else
                whereClause += "AND date(sell_date) <= '" + end + "' " ;
        }        

        
        query = "SELECT * FROM sell INNER JOIN product ON product.prod_id = sell.prod_id INNER JOIN user ON user.user_id = sell.user_id " + whereClause ;

        PreparedStatement st;
        ResultSet rs;
        

        try {
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
                
            }
            
            String query1="",query2="" ;
            
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
                    
            }
            

            sumSellsChart.getData().clear();
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
            
            series.setName("عدد المبيعات حسب اليوم");
            

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
            
            lineSeries.setName("المبلغ الإجمالي حسب اليوم");
            
            ResultSet stats1 = getAllFrom("COUNT(sell_id), SUM(sell_quantity), SUM(sell.sell_price)", "sell", "INNER JOIN product ON sell.prod_id = product.prod_id", whereClause,"");
            
            while(stats1.next()){
                
            idCountLabel.setText(stats1.getString("COUNT(sell_id)") != null?  stats1.getString("COUNT(sell_id)") + " بيع" : "0 بيع");
            qteCountLabel.setText(stats1.getString("SUM(sell_quantity)") != null?  stats1.getString("SUM(sell_quantity)") + " قطعة" : "0 قطع");
            priceSumLabel.setText(stats1.getString("SUM(sell.sell_price)") != null?  stats1.getString("SUM(sell.sell_price)") + " دج" : "0 دج");
            
            }
            
            ResultSet stats2 = getAllFrom("COUNT(sell_id)/datediff('" + end + "','" + start + "') as abd, SUM(sell_quantity)/datediff('" + end + "','" + start + "') as aqd, SUM(sell.sell_price)/datediff('" + end + "','" + start + "') as asd", "sell", "INNER JOIN product ON sell.prod_id = product.prod_id", whereClause,"");
            
            while(stats2.next()){
                
            averageBuyLabel.setText(stats2.getString("abd") != null?  stats2.getString("abd") + " بيع" : "0 بيع");
            averageQteLabel.setText(stats2.getString("aqd") != null?  stats2.getString("aqd") + " قطعة" : "0 قطع");
            averagePriceLabel.setText(stats2.getString("asd") != null?  stats2.getString("asd") + " دج" : "0 دج");
            
            }            
            
            

            con.close();
        }
        catch (SQLException e) {
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);

        }         
        
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        idCol.setCellValueFactory(new PropertyValueFactory<>("sellID"));
        prodCol.setCellValueFactory(new PropertyValueFactory<>("sellName"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("seller"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("sellDate"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        qteCol.setCellValueFactory(new PropertyValueFactory<>("sellQuantity"));
        
        
        sellsTable.setItems(sellsList);
        sellsTable.getSelectionModel().selectFirst();
        
        startDate.setConverter(dateFormatter());
        endDate.setConverter(dateFormatter());
        startDate.getEditor().setText(String.valueOf(LocalDate.now().minusWeeks(1)));
        endDate.getEditor().setText(String.valueOf(LocalDate.now()));
        startDate.setValue(LocalDate.now().minusWeeks(1));
        endDate.setValue(LocalDate.now());

        prodField.setItems(nameList);
        prodField.getSelectionModel().selectFirst();
        
        getData("الكل",startDate.getEditor().getText(),endDate.getEditor().getText(), "day");
        
        search.setOnAction(Action -> {
            
            System.out.println(endDate.getValue().compareTo(startDate.getValue()));
            
        if(endDate.getValue().compareTo(startDate.getValue()) >= 0){
            
            if(endDate.getValue().compareTo(startDate.getValue()) <= 1  || endDate.getValue().compareTo(startDate.getValue()) <= 30 ){
                
                sellsTable.getItems().clear();
                getData(prodField.getSelectionModel().getSelectedItem(),startDate.getEditor().getText(),endDate.getEditor().getText(),"day");            

            }
            else if(endDate.getValue().compareTo(startDate.getValue()) <= 3  ){
              
                sellsTable.getItems().clear();
                getData(prodField.getSelectionModel().getSelectedItem(),startDate.getEditor().getText(),endDate.getEditor().getText(),"week");                 
                
            }
            else if(endDate.getValue().getDayOfYear() - startDate.getValue().getDayOfYear() <= 12 ){
              
                sellsTable.getItems().clear();
                getData(prodField.getSelectionModel().getSelectedItem(),startDate.getEditor().getText(),endDate.getEditor().getText(),"month");                 
                
            }            
            else
            {              
               
            alert.show(LARGE_INTERVAL, "المجال المسموح به يجب أن يكون أصغر من 365 يوما", Alert.AlertType.WARNING,false);                
                
            }
                
    
        }
        else {
            
            alert.show(ILLEGAL_INTERVAL, ILLEGAL_INTERVAL_MSG, Alert.AlertType.WARNING,false);
            
        }             
          
            
        });
        
    }
    
}
