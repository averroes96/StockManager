package Data;

import static Include.Common.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class Buy {
    
    private SimpleIntegerProperty buyID ;
    private SimpleIntegerProperty buyQte ;
    private SimpleIntegerProperty buyPrice ;
    private SimpleIntegerProperty buyTotalPrice;
    private SimpleStringProperty buyDate ;
    private SimpleStringProperty product ;
    private SimpleStringProperty supplier;
    private SimpleStringProperty user;

    public Buy() {
        this.buyID = new SimpleIntegerProperty(0);
        this.buyQte = new SimpleIntegerProperty(0);
        this.buyPrice = new SimpleIntegerProperty(0);
        this.buyTotalPrice = new SimpleIntegerProperty(0);
        this.buyDate = new SimpleStringProperty("");
        this.product = new SimpleStringProperty("");
        this.user = new SimpleStringProperty("");
    }


    public int  getBuyID() {
        return buyID.getValue();
    }

    public void setBuyID(int buyID) {
        this.buyID = new SimpleIntegerProperty(buyID);
    }

    public int getBuyQte() {
        return buyQte.getValue();
    }

    public void setBuyQte(int buyQte) {
        this.buyQte = new SimpleIntegerProperty(buyQte);
    }

    public int getBuyPrice() {
        return buyPrice.getValue();
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = new SimpleIntegerProperty(buyPrice);
    }

    public String getProduct() {
        return product.getValue();
    }

    public void setProduct(String product) {
        this.product = new SimpleStringProperty(product);
    }

    public String getUser() {
        return user.getValue();
    }

    public void setUser(String user) {
        this.user = new SimpleStringProperty(user);
    }

    public String getBuyDate() {
        return buyDate.getValue();
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = new SimpleStringProperty(buyDate);
    }

    public int getBuyTotalPrice() {
        return buyTotalPrice.getValue();
    }

    public void setBuyTotalPrice(int buyTotalPrice) {
        this.buyTotalPrice = new SimpleIntegerProperty(buyTotalPrice);
    }

    public String getSupplier() {
        return supplier.getValue();
    }

    public void setSupplier(String supplier) {
        this.supplier = new SimpleStringProperty(supplier);
    }
    
    
    
    public void delete() throws SQLException{
        
        try (Connection con = getConnection()) {
            String query = "DELETE FROM buy WHERE buy_id = ?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, this.getBuyID());
            ps.executeUpdate();

            query = "UPDATE product SET prod_quantity = prod_quantity - ? WHERE prod_id = ?";

            ps = con.prepareStatement(query);
            ps.setInt(2, Product.getProductByName(this.getProduct()).getProdID());
            ps.setInt(1, this.getBuyQte());
            ps.executeUpdate();
            
            query = "UPDATE product SET nbrBuys = nbrBuys - 1 WHERE prod_id = ?";

            ps = con.prepareStatement(query);
            ps.setInt(1, Product.getProductByName(this.getProduct()).getProdID());
            ps.executeUpdate();
        }
        
        
    }
    
    public String getTime() throws SQLException{

        try (Connection con = getConnection()) {
            String query = "SELECT time(buy_date) FROM buy WHERE buy_id = ?";

            PreparedStatement st;
            ResultSet rs;

            st = con.prepareStatement(query);
            st.setInt(1, getBuyID());
            rs = st.executeQuery();

            while (rs.next()) {

                return rs.getString("time(buy_date)");

            }
        }

    return null;
    
    }
    
    public String getDate() throws SQLException{

        try (Connection con = getConnection()) {
            String query = "SELECT date(buy_date) FROM buy WHERE buy_id = ?";

            PreparedStatement st;
            ResultSet rs;

            st = con.prepareStatement(query);
            st.setInt(1, getBuyID());
            rs = st.executeQuery();

            while (rs.next()) {
                return rs.getString("date(buy_date)");
            }
        }

    return null;
    
    }    
    
    public static ObservableList getBuysByDate(String selectedDate) throws SQLException{
        
        ObservableList<Buy> data = FXCollections.observableArrayList();
        
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM buy INNER JOIN product ON buy.prod_id = product.prod_id INNER JOIN user ON buy.user_id = user.user_id WHERE date(buy_date) = ? ORDER BY time(buy_date) ASC";
            
            PreparedStatement st;
            ResultSet rs;
            
            st = con.prepareStatement(query);
            st.setString(1,selectedDate);
            rs = st.executeQuery();

            while (rs.next()) {
                Buy buy = new Buy();
                buy.setBuyID(rs.getInt("buy_id"));
                buy.setBuyPrice(rs.getInt("buy.buy_unit_price"));
                buy.setBuyTotalPrice(rs.getInt("buy.buy_price"));
                buy.setBuyDate(rs.getTime("buy_date").toString());
                buy.setBuyQte(rs.getInt("buy_qte"));
                buy.setProduct(rs.getString("name"));
                buy.setUser(rs.getString("username"));
                buy.setSupplier(rs.getString("buy_supplier"));
                
                data.add(buy);
            }
            
            return data;
        } 
        
        
    }
    
    
    public static ResultSet getTodayStats(String selectedDate) throws SQLException{
        
            Connection con = getConnection();
            String query = "";
            PreparedStatement st;
            ResultSet rs;
            if(selectedDate.equals("")){
                
                query = "SELECT count(*), SUM(buy_price), SUM(buy_qte) FROM buy";
            }
            else{
                query = "SELECT count(*), SUM(buy_price), SUM(buy_qte) FROM buy WHERE date(buy_date) = ? ";
            }
            
            st = con.prepareStatement(query);
            if(!selectedDate.equals("")){
                st.setString(1,selectedDate);
            }
            rs = st.executeQuery();
            
            return rs;
        
    }

    
}
