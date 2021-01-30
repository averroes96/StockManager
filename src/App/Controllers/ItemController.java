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
package App.Controllers;

import Data.Product;
import Include.Init;
import static Include.Init.IMAGES_PATH;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;

/**
 * FXML Controller class
 *
 * @author user
 */
public class ItemController implements Initializable, Init {

    @FXML private Label productLabel,qteLabel,priceLabel,dateLabel,editLabel,buysLabel,sellsLabel;
    @FXML private HBox parent;
    
    
    private MainController mainController;
    private Product product;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
                
        parent.setOnMouseEntered((event) -> {
            parent.setStyle("-fx-background-color : #bbb");
        });
        parent.setOnMouseExited((event) -> {
            parent.setStyle("-fx-background-color : #eee");
        });
       
        parent.setOnMouseClicked((event) -> {
            showProduct();
        });
        
    }
    

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    
    public void setValues(Product product){
        
        productLabel.setText(product.getName());
        qteLabel.setText(String.valueOf(product.getProdQuantity()));
        priceLabel.setText(String.valueOf(product.getSellPrice()));
        dateLabel.setText(product.getAddDate());
        editLabel.setText(product.getLastChange());
        buysLabel.setText(String.valueOf(product.getNbrBuys()));
        sellsLabel.setText(String.valueOf(product.getNbrSells()));
        
        setProduct(product);
        
    }    

    private void showProduct() {
                
        mainController.productNameTF.setText(product.getName());
        mainController.productPriceTF.setText(String.valueOf(product.getSellPrice()));
        mainController.idField.setText(String.valueOf(product.getProdID()));
        mainController.productQteTF.setText(String.valueOf(product.getProdQuantity()));
        mainController.productDP.getEditor().setText(product.getAddDate());
        
        if (product.getImageURL() == null) {
            mainController.productIV.setFill(new ImagePattern(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/large_product_primary.png"),
                    64, 64, false, false)));
        }
        else {
            try{
            mainController.productIV.setFill(new ImagePattern(new Image(
                    new File(product.getImageURL()).toURI().toString(),
                    mainController.productIV.getCenterX(), mainController.productIV.getCenterY(), false, false)));
            }catch(IllegalArgumentException | NullPointerException e){
            mainController.productIV.setFill(new ImagePattern(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/large_product_primary.png"),
                    64, 64, false, false)));
            mainController.customDialog(mainController.bundle.getString("image_not_found"), mainController.bundle.getString("image_not_found_msg"), ERROR_SMALL, true);
            }
        }   
    }
    
}
