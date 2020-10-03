/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import static Include.Common.getAllFrom;
import static Include.Common.getConnection;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author med
 */
public class Product extends RecursiveTreeObject<Product> {

    private SimpleIntegerProperty prodID;
    private SimpleStringProperty name;   
    private SimpleIntegerProperty sellPrice;
    private SimpleStringProperty imageURL;
    private SimpleIntegerProperty prodQuantity;
    private SimpleStringProperty addDate;
    private SimpleStringProperty lastChange;
    private SimpleIntegerProperty nbrSells;
    private SimpleIntegerProperty nbrBuys;

    public Product() {
        this.prodID = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty("");
        this.sellPrice = new SimpleIntegerProperty(0);        
        this.prodQuantity = new SimpleIntegerProperty(0);        
        this.addDate = new SimpleStringProperty("");
        this.lastChange = new SimpleStringProperty("");
    }

    public Product(int id, String name, String brand, int sellPrice, int quantity, String addDate) {
        this.prodID = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.sellPrice = new SimpleIntegerProperty(sellPrice);
        this.prodQuantity = new SimpleIntegerProperty(quantity);
        this.addDate = new SimpleStringProperty(addDate);
    }

    public int getProdID() {
        return prodID.getValue();
    }

    public void setProdID(int prodID) {
        this.prodID = new SimpleIntegerProperty(prodID);
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public int getSellPrice() {
        return sellPrice.getValue();
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = new SimpleIntegerProperty(sellPrice);
    }

    public String getImageURL() {
        return imageURL.getValue();
    }

    public void setImageURL(String imageURL) {
        this.imageURL = new SimpleStringProperty(imageURL);
    }

    public int getProdQuantity() {
        return prodQuantity.getValue();
    }

    public void setProdQuantity(int prodQuantity) {
        this.prodQuantity = new SimpleIntegerProperty(prodQuantity);
    }

    public String getAddDate() {
        return addDate.getValue();
    }

    public void setAddDate(String addDate) {
        this.addDate = new SimpleStringProperty(addDate);
    }
    
    public int getNbrSells() {
        return nbrSells.getValue();
    }

    public void setNbrSells( int nbrSells) {
        this.nbrSells = new SimpleIntegerProperty(nbrSells);
    }

    public int getNbrBuys() {
        return nbrBuys.getValue();
    }

    public void setNbrBuys(int nbrBuys) {
        this.nbrBuys = new SimpleIntegerProperty(nbrBuys);
    }

    public String getLastChange() {
        return lastChange.getValue();
    }

    public void setLastChange(String lastChange) {
        this.lastChange = new SimpleStringProperty(lastChange);
    }

    
    

    @Override
    public String toString() {
        return  name.getValue() ;
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
        final Product other = (Product) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
    public void toTrash() throws SQLException{
        
            try (Connection con = getConnection()) {
                String query = "UPDATE product SET on_hold = 1 WHERE prod_id = ?";
                
                PreparedStatement ps = con.prepareStatement(query);
                
                ps.setInt(1, this.getProdID());
                
                ps.executeUpdate();
            }
        
    }
    
    public static ObservableList getActiveProducts() throws SQLException{
        
        ObservableList<Product> data = FXCollections.observableArrayList();
        
        ResultSet rs;

        rs = getAllFrom("*","product","","WHERE on_hold = 0","ORDER BY add_date DESC");

            while (rs.next()) {
                Product product = new Product();
                product.setProdID(rs.getInt("prod_id"));
                product.setName(rs.getString("name"));
                product.setSellPrice(rs.getInt("sell_price"));
                product.setAddDate(rs.getDate("add_date").toString());
                product.setProdQuantity(rs.getInt("prod_quantity"));
                product.setImageURL(rs.getString("image_url"));
                product.setNbrBuys(rs.getInt("nbrBuys"));
                product.setNbrSells(rs.getInt("nbrSells"));
                
                if(rs.getTimestamp("last_change") != null){
                    product.setLastChange(rs.getTimestamp("last_change").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy h.mm a")));
                }
                else
                    product.setLastChange("/");
                data.add(product);
            }
            
        return data;
            
            
        
    }

    
}
