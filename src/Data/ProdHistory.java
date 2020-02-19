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
package Data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author user
 */
public class ProdHistory {
    
    private SimpleIntegerProperty prodHistID ;
    private SimpleStringProperty product,user,date ;
    private int oldPrice,newPrice,oldQte,newQte;
    private String oldName,newName,oldDate,newDate;

    public ProdHistory() {
        
        this.prodHistID = new SimpleIntegerProperty(0);
        this.product = new SimpleStringProperty("");
        this.user = new SimpleStringProperty("");
        this.date = new SimpleStringProperty("");
        
        
    }

    public int getProdHistID() {
        return prodHistID.getValue();
    }

    public void setProdHistID(int prodHistID) {
        this.prodHistID = new SimpleIntegerProperty(prodHistID);
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

    public String getDate() {
        return date.getValue();
    }

    public void setDate(String date) {
        this.date = new SimpleStringProperty(date);
    }

    public int getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(int oldPrice) {
        this.oldPrice = oldPrice;
    }

    public int getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(int newPrice) {
        this.newPrice = newPrice;
    }

    public int getOldQte() {
        return oldQte;
    }

    public void setOldQte(int oldQte) {
        this.oldQte = oldQte;
    }

    public int getNewQte() {
        return newQte;
    }

    public void setNewQte(int newQte) {
        this.newQte = newQte;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getOldDate() {
        return oldDate;
    }

    public void setOldDate(String oldDate) {
        this.oldDate = oldDate;
    }

    public String getNewDate() {
        return newDate;
    }

    public void setNewDate(String newDate) {
        this.newDate = newDate;
    }
    
    public boolean nameIsChanged(){
        return !this.oldName.equals(this.newName);
    }
    
    public boolean priceIsChanged(){
        return this.oldPrice != this.newPrice;
    }

    public boolean dateIsChanged(){
        return !this.oldDate.equals(this.newDate);
    }

    public boolean qteIsChanged(){
        return this.oldQte != this.newQte;
    }    
    
    
    
}
