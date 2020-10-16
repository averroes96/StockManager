/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import static Include.Common.getAllFrom;
import static Include.Common.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author med
 */
public class User {

    private SimpleIntegerProperty userID ;
    private SimpleStringProperty fullname;
    private SimpleStringProperty phone;
    private SimpleIntegerProperty admin;
    private SimpleStringProperty image;
    private SimpleStringProperty lastLogged;
    private String username;
    private String password;
    private int prodPrivs,userPrivs,buyPrivs,sellPrivs;
    

    public User() {

        this.userID = new SimpleIntegerProperty(0);
        this.fullname = new SimpleStringProperty("");
        this.lastLogged = new SimpleStringProperty("");
        this.phone = new SimpleStringProperty("");
        this.admin = new SimpleIntegerProperty(0);
        this.password = "";
        this.username = "";
        this.image = new SimpleStringProperty("");
        prodPrivs = 0;
        userPrivs = 0;
        buyPrivs = 0;
        sellPrivs = 0;
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
    
    public boolean checkPassword(String password) throws SQLException{
        
        try (Connection con = getConnection()) {
            String query = "SELECT password FROM user WHERE user_id = ?";

            PreparedStatement st;
            ResultSet rs;

            st = con.prepareStatement(query);
            st.setInt(1, this.getUserID());
            rs = st.executeQuery();

            if (rs.next()) {

                return rs.getString("password").equals(password);

            }
        }

    return false;
    
    }

    public void changePassword(String newpass) throws SQLException{
        
        Connection con = getConnection();
        
        PreparedStatement ps;

        ps = con.prepareStatement("UPDATE user SET password = ? WHERE user_id = ?");

        ps.setString(1, newpass);
        ps.setInt(2, this.getUserID());

        ps.executeUpdate();        
        
    }
    
    public void activate() throws SQLException{
        
        Connection con = getConnection();
        
        PreparedStatement ps;
        
        ps = con.prepareStatement("UPDATE user SET active = 1 WHERE user_id = ?");
        ps.setInt(1, this.getUserID());

        ps.executeUpdate();
        
    }
    
    public void unauthorize() throws SQLException{
        
        Connection con = getConnection();
        
        PreparedStatement ps;
        
        ps = con.prepareStatement("UPDATE user SET active = 2 WHERE user_id = ?");
        ps.setInt(1, this.getUserID());

        ps.executeUpdate();
        
    }  
    
    public void toTrash() throws SQLException{
        
                try (Connection con = getConnection()) {
                    String query = "UPDATE user SET active = 0 WHERE user_id = ?";
                    
                    PreparedStatement ps = con.prepareStatement(query);
                    
                    ps.setInt(1, this.getUserID());
                    
                    ps.executeUpdate();
                }
    }
    
    public void restore() throws SQLException{
        
        try (Connection con = getConnection()) {
            String query = "UPDATE user SET active = 1 WHERE user_id = ?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, this.getUserID());

            ps.executeUpdate();
        }        
        
    }
    
    public void delete() throws SQLException{
        
        try (Connection con = getConnection()) {
            String query = "DELETE FROM user WHERE user_id = ?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, this.getUserID());

            ps.executeUpdate();
        }        
        
    }
    
    // Static methods
    
    public static boolean isLastAdmin() throws SQLException{
        

            int count;
            try (Connection con = getConnection()) {
                count = 0;
                String query = "SELECT count(*) FROM user WHERE admin = 1";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getInt("count(*)") == 1;
                }
                con.close();
            }

            return false;      
        
    }
    
    public static ObservableList getUsers(int status) throws SQLException{
        
        ObservableList<String> data = FXCollections.observableArrayList();
        
        ResultSet rs;
        
            rs = getAllFrom("username","user","","WHERE active = " + status,"");
        
        while (rs.next()) {
            String emp = rs.getString("username");
            data.add(emp);
        }
        
        return data;
        
    }
    
    public static boolean usernameExist(String username) throws SQLException{
               
            try (Connection con = getConnection()) {
                String query = "SELECT * FROM user WHERE username = ?";
                PreparedStatement st;
                ResultSet rs;
                st = con.prepareStatement(query);
                st.setString(1, username);
                rs = st.executeQuery();
                while(rs.next())
                    return true;
            }
            
            return false;             
   }     
    
}
