/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
package sANDb.Controllers;

import Data.ProdHistory;
import Include.Common;
import static Include.Common.dateFormatter;
import static Include.Common.getConnection;
import Include.CommonMethods;
import Include.Init;
import static Include.Init.UNKNOWN_ERROR;
import Include.SpecialAlert;
import java.io.IOException;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author user
 */
public class ProductHistoryController implements Initializable,Init,CommonMethods {
    
    @FXML private Label oldQte,newQte,oldName,newName,oldDate,newDate,oldPrice,newPrice;
    @FXML private Button search;
    @FXML private TableView<ProdHistory> historyTable;
    @FXML private TableColumn<ProdHistory, Integer> idCol;
    @FXML private TableColumn<ProdHistory, String> prodCol,userCol,dateCol;
    @FXML private ChoiceBox<String> prodField ;
    @FXML private DatePicker startDate,endDate;
    
    SpecialAlert alert = new SpecialAlert();
    
    ObservableList<ProdHistory> historyList = FXCollections.observableArrayList();
    static ObservableList<String> nameList = Common.nameList;
    
    private void getAllHistory(int prodID, String start, String end){
        
        Connection con = getConnection();
        String whereClause = "" ;
        String query ;
        
        if(prodID != 0){
            whereClause = "WHERE prod ID = " + prodID + " " ;
        }
        
        if(!start.equals("")){
            if(whereClause.equals("")){
                whereClause = "WHERE date(change_date) >= '" + start + "' " ;
            }
            else
                whereClause += "AND date(change_date) >= '" + start + "' " ;
        }
        
        if(!end.equals("")){
            if(whereClause.equals("")){
                whereClause = "WHERE date(change_date) <= '" + end + "' " ;
            }
            else
                whereClause += "AND date(change_date) <= '" + end + "' " ;
        }        

        
        query = "SELECT * FROM product_history INNER JOIN product ON product.prod_id = product_history.prod_id INNER JOIN user ON user.user_id = product_history.user_id " + whereClause ;

        PreparedStatement st;
        ResultSet rs;
        

        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery(query);

            while (rs.next()) {
                ProdHistory history = new ProdHistory();
                history.setDate(rs.getTimestamp("change_date").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy h.mm a")));
                history.setProdHistID(rs.getInt("prod_hist_id"));
                history.setProduct(rs.getString("name"));
                history.setUser(rs.getString("username"));
                
                historyList.add(history);
            }

            con.close();
        }
        catch (SQLException e) {
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);

        }        
        
    }
    
    @Override
    public void logOut(ActionEvent event) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean checkInputs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        idCol.setCellValueFactory(new PropertyValueFactory<>("prodHistID"));
        prodCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        getAllHistory(0,"","");
        historyTable.setItems(historyList);
        
        startDate.setConverter(dateFormatter());
        endDate.setConverter(dateFormatter());
        startDate.getEditor().setText(String.valueOf(LocalDate.now()));
        endDate.getEditor().setText(String.valueOf(LocalDate.now()));

        prodField.setItems(nameList);
        
        
    }    


    
}
