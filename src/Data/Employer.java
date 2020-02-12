/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author med
 */
public class Employer {

    private SimpleIntegerProperty userID ;
    private SimpleStringProperty fullname;
    private SimpleStringProperty phone;
    private SimpleIntegerProperty admin;
    private SimpleStringProperty image;
    private SimpleStringProperty lastLogged;
    private String username;
    private String password;
    private int prodPrivs,userPrivs,buyPrivs,sellPrivs;

    public Employer() {

        this.userID = new SimpleIntegerProperty(0);
        this.fullname = new SimpleStringProperty("");
        this.lastLogged = new SimpleStringProperty("");
        this.phone = new SimpleStringProperty("");
        this.admin = new SimpleIntegerProperty(0);
        this.password = "";
        this.username = "";
        this.image = new SimpleStringProperty("");
    }

    public int getUserID() {
        return userID.getValue();
    }

    public void setUserID(int userID) {
        this.userID = new SimpleIntegerProperty(userID);
    }

    public String getFullname() {
        return fullname.getValue();
    }

    public void setFullname(String fullname) {
        this.fullname = new SimpleStringProperty(fullname);
    }

    public int getAdmin() {
        return admin.getValue();
    }

    public void setAdmin(int admin) {
        this.admin = new SimpleIntegerProperty(admin);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image.getValue();
    }

    public void setImage(String image) {
        this.image = new SimpleStringProperty(image);
    }

    public String getPhone() {
        return phone.getValue();
    }

    public void setPhone(String phone) {
        this.phone = new SimpleStringProperty(phone);
    }

    public int getProdPrivs() {
        return prodPrivs;
    }

    public void setProdPrivs(int prodPrivs) {
        this.prodPrivs = prodPrivs;
    }

    public int getUserPrivs() {
        return userPrivs;
    }

    public void setUserPrivs(int userPrivs) {
        this.userPrivs = userPrivs;
    }

    public int getBuyPrivs() {
        return buyPrivs;
    }

    public void setBuyPrivs(int buyPrivs) {
        this.buyPrivs = buyPrivs;
    }

    public int getSellPrivs() {
        return sellPrivs;
    }

    public void setSellPrivs(int sellPrivs) {
        this.sellPrivs = sellPrivs;
    }

    public String getLastLogged() {
        return lastLogged.getValue();
    }

    public void setLastLogged(String lastLogged) {
        this.lastLogged = new SimpleStringProperty(lastLogged);
    }

    
}
