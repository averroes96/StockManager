/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sANDb.Controllers;

import static Include.Common.dateFormatter;
import static Include.Common.getConnection;
import static Include.Common.getProductByName;
import Include.Init;
import static Include.Init.UNKNOWN_ERROR;
import Include.SpecialAlert;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author med
 */
public class SellStatsController implements Initializable,Init {

    
    @FXML Label weekSells,weekSum,monthSells,monthSum,yearSells,yearSum ;
    @FXML LineChart sellLineChart ;
    @FXML LineChart sumLineChart;
    @FXML DatePicker startDate,endDate;
    @FXML ChoiceBox productBox ;
    @FXML ObservableList<Data> list = FXCollections.observableArrayList();
    @FXML Button filter;
    SpecialAlert alert = new SpecialAlert();
    
    ObservableList<String> productsList = FXCollections.observableArrayList();
    
    private void getAllProducts()
    {
        Connection con = getConnection();
        String query = "SELECT name FROM product WHERE on_hold = 0";

        Statement st;
        ResultSet rs;
        
        productsList.add("Tout");
        
        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                String product = rs.getString("name");
                productsList.add(product);
            }

            con.close();
        }
        catch (SQLException e) {          
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        }
    }     
    
    
    public void getAllStats(){
        
        Connection con = getConnection();
        String weekQuery = "SELECT count(*), SUM(sell_price) FROM sell WHERE date(sell_date) <= curdate() AND date(sell_date) >= date(curdate() - INTERVAL 7 day ) ";
        String monthQuery = "SELECT count(*), SUM(sell_price) FROM sell WHERE date(sell_date) <= curdate() AND date(sell_date) >= date(curdate() - INTERVAL 30 day ) ";
        String yearQuery = "SELECT count(*), SUM(sell_price) FROM sell WHERE date(sell_date) <= curdate() AND date(sell_date) >= date(curdate() - INTERVAL 365 day ) ";
        String query = "SELECT count(*), SUM(sell_price) FROM sell";
        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(weekQuery);
            rs = st.executeQuery();
            
            int weeksum = 0;
            int weeksells = 0;
            int monthsum = 0;
            int monthsells = 0;
            int yearsells = 0;
            int yearsum = 0;
            int Sum = 0;
            int Sells = 0;            

            while (rs.next()) {
                
                weeksum = rs.getInt("SUM(sell_price)");
                weeksells = rs.getInt("count(*)");
                
            }
            
            st = con.prepareStatement(monthQuery);
            rs = st.executeQuery();            
            
            while (rs.next()) {
                
                monthsum = rs.getInt("SUM(sell_price)");
                monthsells = rs.getInt("count(*)");
                
            }
            
            st = con.prepareStatement(yearQuery);
            rs = st.executeQuery();            
            
            while (rs.next()) {
                
                yearsum = rs.getInt("SUM(sell_price)");
                yearsells = rs.getInt("count(*)");
                
            }

            st = con.prepareStatement(yearQuery);
            rs = st.executeQuery();            
            
            while (rs.next()) {
                
                Sum = rs.getInt("SUM(sell_price)");
                Sells = rs.getInt("count(*)");
                
            }

            st = con.prepareStatement(query);
            rs = st.executeQuery();            
            
            while (rs.next()) {
                
                Sum = rs.getInt("SUM(sell_price)");
                Sells = rs.getInt("count(*)");
                
            }            
            
            weekSum.setText(String.valueOf(weeksum) + " دج");
            weekSells.setText(String.valueOf(weeksells) + " بيع");
            monthSum.setText(String.valueOf(monthsum) + " دج");
            monthSells.setText(String.valueOf(monthsells) + " بيع");            
            yearSum.setText(String.valueOf(yearsum) + " دج");
            yearSells.setText(String.valueOf(yearsells) + " بيع");                       
            

            con.close();
        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        }         
        
    }
    
    public void loadLineChart(String startDate, String endDate, String product){
        
        String whereClause = "";
        
        if(!startDate.equals("")){
            
            whereClause += " WHERE date(sell_date) >= '" + startDate + "' " ;
            
        }
        if(!endDate.equals("")){
            
            if(whereClause.equals("")){
                whereClause += " WHERE date(sell_date) <= '" + endDate + "' " ;
            }
            else
            {
                whereClause += " AND date(sell_date) <= '" + endDate + "' ";
            }
            
        }
        if(!product.equals("Tout")){
            

              if(whereClause.equals("")){
                  whereClause += " WHERE prod_id = " + getProductByName(product).getProdID() + " " ;
              }
              else
              {
                  whereClause += " AND prod_id = " + getProductByName(product).getProdID() + " " ;
              }

            
        }
        
        Connection con = getConnection();
        String query = "SELECT date(sell_date), count(*) FROM sell " + whereClause + "Group by date(sell_date) " ;
        PreparedStatement st;
        ResultSet rs;
        
        sellLineChart.getData().clear();
        XYChart.Series<String,Integer> series = new XYChart.Series<>();

        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
           

            while (rs.next()) {

                series.getData().add(new XYChart.Data<>(rs.getString("date(sell_date)"),rs.getInt("count(*)")));
                
            }
            
        sellLineChart.getData().addAll(series);
        
        series.getData().forEach((data) -> {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event1) -> {
                Tooltip.install(data.getNode(), new Tooltip(data.getYValue().toString()));
            });
            });            
            
            con.close();
        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        } 

        series.setName("المبيعات");
                
        
    }
    
    public void loadSumChart(String startDate, String endDate, String product){
        
        String whereClause = "";
        
        if(!startDate.equals("")){
            
            whereClause += " WHERE date(sell_date) >= '" + startDate + "' " ;
            
        }
        if(!endDate.equals("")){
            
            if(whereClause.equals("")){
                whereClause += " WHERE date(sell_date) <= '" + endDate + "' " ;
            }
            else{
                whereClause += " AND date(sell_date) <= '" + endDate + "' ";
            }
            
        }
        if(!product.equals("Tout")){
            

              if(whereClause.equals("")){
                  whereClause += " WHERE prod_id = " + getProductByName(product).getProdID() + " " ;
              }
              else
              {
                  whereClause += " AND prod_id = " + getProductByName(product).getProdID() + " " ;
              }

            
        }        
        
        Connection con = getConnection();
        String query = "SELECT date(sell_date), SUM(sell_price) FROM sell " + whereClause + " Group by date(sell_date)";
        PreparedStatement st;
        ResultSet rs;
        
        sumLineChart.getData().clear();
        XYChart.Series<String,Integer> series = new XYChart.Series<>();

        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
           

            while (rs.next()) {

                series.getData().add(new XYChart.Data<>(rs.getString("date(sell_date)"),rs.getInt("SUM(sell_price)")));
                
            }
            
        sumLineChart.getData().addAll(series);
        
        series.getData().forEach((data) -> {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event1) -> {
                Tooltip.install(data.getNode(), new Tooltip(data.getYValue().toString()));//To change body of generated methods, choose Tools | Templates.
            });
            });            
            
            con.close();
        }
        catch (SQLException e) {
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        } 

        series.setName("المبلغ الإجمالي");
        
        
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        startDate.setConverter(dateFormatter());
        endDate.setConverter(dateFormatter());

        startDate.getEditor().setText(String.valueOf(LocalDate.now().minusWeeks(1)));
        endDate.getEditor().setText(String.valueOf(LocalDate.now()));
        
        startDate.setValue(LocalDate.now().minusWeeks(1));
        endDate.setValue(LocalDate.now());

        getAllProducts();
        
        productBox.setItems(productsList);
        
        if(!productsList.isEmpty()){
            productBox.getSelectionModel().select(0);
        }
        
        getAllStats();
        loadLineChart(startDate.getEditor().getText(), endDate.getEditor().getText(),productBox.getSelectionModel().getSelectedItem().toString());
        loadSumChart(startDate.getEditor().getText(), endDate.getEditor().getText(),productBox.getSelectionModel().getSelectedItem().toString());

        filter.setOnAction(Action -> {

        if(endDate.getValue().compareTo(startDate.getValue()) > 0){
            
        if(endDate.getValue().compareTo(startDate.getValue()) <= 30){
            
            
        
        loadLineChart(startDate.getEditor().getText(), endDate.getEditor().getText(),productBox.getSelectionModel().getSelectedItem().toString());
        loadSumChart(startDate.getEditor().getText(), endDate.getEditor().getText(),productBox.getSelectionModel().getSelectedItem().toString());
        
       }
        else {
            
            alert.show(LARGE_INTERVAL, LARGE_INTERVAL_MSG, Alert.AlertType.WARNING,false);
            
        }
        
        }
        else {
            
            alert.show(ILLEGAL_INTERVAL, ILLEGAL_INTERVAL_MSG, Alert.AlertType.WARNING,false);
            
        }        
        
        });

    }    
    
}
