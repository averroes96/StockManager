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
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author user
 */
public class Payment {
    
    private int ID;
    private Client client;
    private SimpleStringProperty payDate,clientName;
    private SimpleIntegerProperty paySum;

    public Payment() {
        ID = 0;
        client = new Client();
        payDate = new SimpleStringProperty("");
        clientName = new SimpleStringProperty("");
        paySum = new SimpleIntegerProperty(0);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getPayDate() {
        return payDate.getValue();
    }

    public void setPayDate(String payDate) {
        this.payDate = new SimpleStringProperty(payDate);
    }

    public String getClientName() {
        return clientName.getValue();
    }

    public void setClientName(String clientName) {
        this.clientName = new SimpleStringProperty(clientName);
    }

    public int getPaySum() {
        return paySum.getValue();
    }

    public void setPaySum(int paySum) {
        this.paySum = new SimpleIntegerProperty(paySum);
    }

    @Override
    public String toString() {
        return  payDate + " " + clientName + " " + paySum;
    }
    
    public static ObservableList<Payment> getClientPayments(Client choosen) throws SQLException {
        
        ObservableList<Payment> data = FXCollections.observableArrayList();
        
        ResultSet rs;
        
        rs = getAllFrom("*","payment","INNER JOIN client ON payment.client_id = client.client_id", "WHERE payment.client_id = " + choosen.getID() ,"");
        
        while (rs.next()) {
            
            Client client = new Client();
            
            client.setID(rs.getInt("payment.client_id"));
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
            
            Payment payment = new Payment();
            payment.setClient(client);
            payment.setClientName(client.getFullname());
            payment.setID(rs.getInt("pay_id"));
            payment.setPayDate(rs.getString("pay_date"));
            payment.setPaySum(rs.getInt("pay_sum"));
            
            data.add(payment);
        }
                
        return data;
    }
    
    
    
    
    
}
