/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 */
package Data;

import static Include.Common.getAllFrom;
import static Include.Common.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author user
 */
public class Client {
    
    private int ID,paid,remain;
    private String fullname,phone,regCom,nif,ai,image;

    public Client() {
        this.ID = 0;
        this.paid = 0;
        this.remain = 0;
        this.fullname = "";
        this.phone = "";
        this.regCom = "";
        this.nif = "";
        this.ai = "";
        this.image = "";
    }

    public Client(int ID, int paid, int remain, String fullname, String phone, String regCom, String nif, String ai, String image) {
        this.ID = ID;
        this.paid = paid;
        this.remain = remain;
        this.fullname = fullname;
        this.phone = phone;
        this.regCom = regCom;
        this.nif = nif;
        this.ai = ai;
        this.image = image;
    }

    public int getPaid() {
        return paid;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }
    
    

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegCom() {
        return regCom;
    }

    public void setRegCom(String regCom) {
        this.regCom = regCom;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getAi() {
        return ai;
    }

    public void setAi(String ai) {
        this.ai = ai;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return fullname; //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    public static ObservableList getClientNames(ResourceBundle rb) throws SQLException{
        
        ObservableList<String> data = FXCollections.observableArrayList();
        
        ResultSet rs;
        
        rs = getAllFrom("fullname","client","","WHERE fullname != ''","");
        while (rs.next()) {
            String emp = rs.getString("fullname");
            data.add(emp);
        }
        
        
        return data;
        
    }
    
    public static ObservableList<Client> getClients(ResourceBundle rb, boolean other) throws SQLException{
        
        ObservableList<Client> data = FXCollections.observableArrayList();
        
        ResultSet rs;
        
        rs = getAllFrom("*","client","", other? "WHERE fullname != ''": "","");
        
        while (rs.next()) {
            
            Client client = new Client();
            
            client.setID(rs.getInt("client_id"));
            client.setFullname(rs.getString("fullname"));
            if(rs.getString("image") != null){
                client.setImage(rs.getString("image"));
            }
            client.setRegCom(rs.getString("rc"));
            client.setPhone(rs.getString("tel"));
            client.setNif(rs.getString("nif"));
            client.setAi(rs.getString("ai"));
            client.setPaid(rs.getInt("paid"));
            client.setRemain(rs.getInt("remain"));
            
            data.add(client);
        }
        
        if(!other)
            data.get(0).setFullname(rb.getString("other"));
        
        return data;
        
    }
    
    public static Client getClientByName(String name) throws SQLException{
        
        Client client = new Client();
        int count;
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM client WHERE fullname = ?";
            PreparedStatement st;
            ResultSet rs;
            st = con.prepareStatement(query);
            st.setString(1, name);
            rs = st.executeQuery();
            count = 0;
            while (rs.next()) {

                client.setID(rs.getInt("client.client_id"));
                client.setFullname(rs.getString("fullname"));
                if(rs.getString("image") != null){
                    client.setImage(rs.getString("image"));
                }
                client.setRegCom(rs.getString("rc"));
                client.setPhone(rs.getString("tel"));
                client.setNif(rs.getString("nif"));
                client.setAi(rs.getString("ai"));
                client.setPaid(rs.getInt("paid"));
                client.setRemain(rs.getInt("remain"));
                ++count;
            }
        }
            if(count == 0)
                return null;
            else
                return client;              
    }
    
        public void delete() throws SQLException{
        
        try (Connection con = getConnection()) {
            String query = "DELETE FROM client WHERE client_id = ?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, this.getID());

            ps.executeUpdate();
        }        
        
    }
        
    public static boolean nameExist(String text) throws SQLException {
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM client WHERE fullname = ?";
            PreparedStatement st;
            ResultSet rs;
            st = con.prepareStatement(query);
            st.setString(1, text);
            rs = st.executeQuery();
            while(rs.next())
                return true;
        }

        return false;
    }

    public void onSell(int val, String operator, boolean include) throws SQLException {
                
        try (Connection con = getConnection()) {
        
            if(include){

                String query = "UPDATE client SET remain = remain " + operator + " ?, sells_count = sells_count " + operator + " 1 WHERE client_id = ?";

                PreparedStatement ps = con.prepareStatement(query);

                ps.setInt(1, val);
                ps.setInt(2, getID());

                ps.executeUpdate();

            }
            else{
                
                String query = "UPDATE client SET remain = remain " + operator + " ? WHERE client_id = ?";

                PreparedStatement ps = con.prepareStatement(query);

                ps.setInt(1, val);
                ps.setInt(2, getID());

                ps.executeUpdate();

            }
        
        }    
    }
       
    
}
