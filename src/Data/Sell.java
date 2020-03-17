/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import static Include.Common.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author med
 */
public class Sell {
    
    private SimpleIntegerProperty sellID;
    private SimpleIntegerProperty sellPrice;
    private SimpleIntegerProperty totalPrice;
    private SimpleStringProperty sellDate;    
    private SimpleStringProperty sellName;
    private SimpleIntegerProperty sellQuantity;
    private SimpleStringProperty seller;
    private Product product ;

    public Sell() {
        
        this.sellID = new SimpleIntegerProperty(0);
        this.sellDate = new SimpleStringProperty("");
        this.sellPrice = new SimpleIntegerProperty(0);
        this.totalPrice = new SimpleIntegerProperty(0);
        this.sellQuantity = new SimpleIntegerProperty(0);
        this.product = new Product();
        this.sellName = new SimpleStringProperty("");
        this.seller = new SimpleStringProperty("");
        
    }

    public int getSellID() {
        return sellID.getValue();
    }

    public void setSellID(int sellID) {
        this.sellID = new SimpleIntegerProperty(sellID);
    }

    public int getSellPrice() {
        return sellPrice.getValue();
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = new SimpleIntegerProperty(sellPrice);
    }

    public String getSellDate() {
        return sellDate.getValue();
    }

    public void setSellDate(String sellDate) {
        this.sellDate = new SimpleStringProperty(sellDate);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getSellName() {
        return sellName.getValue();
    }

    public void setSellName(String sellName) {
        this.sellName = new SimpleStringProperty(sellName);
    }

    public int getSellQuantity() {
        return sellQuantity.getValue();
    }

    public void setSellQuantity(int sellQuantity) {
        this.sellQuantity = new SimpleIntegerProperty(sellQuantity);
    }

    public int getTotalPrice() {
        return totalPrice.getValue();
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = new SimpleIntegerProperty(totalPrice);
    }

    public String getSeller() {
        return seller.getValue();
    }

    public void setSeller(String seller) {
        this.seller = new SimpleStringProperty(seller);
    }
    

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sell other = (Sell) obj;
        return Objects.equals(this.sellID, other.sellID);
    }

    @Override
    public String toString() {
        return sellName.getValue() ;
    }
    
    public void delete() throws SQLException{
        
            try (Connection con = getConnection()) {
                String query = "DELETE FROM sell WHERE sell_id = ?";
                
                PreparedStatement ps = con.prepareStatement(query);
                
                ps.setInt(1, this.getSellID());
                
                ps.executeUpdate();
                
                query = "UPDATE product SET prod_quantity = prod_quantity + ? WHERE prod_id = ?";
                
                ps = con.prepareStatement(query);
                
                ps.setInt(2, this.getProduct().getProdID());
                ps.setInt(1, this.getSellQuantity());
                
                ps.executeUpdate();
            }        
    }
}
