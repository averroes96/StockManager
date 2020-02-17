/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import java.util.Objects;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author med
 */
public class Product {

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

    
    
    

    
}
