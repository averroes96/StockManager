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

import Include.Common;
import Include.GDPController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author user
 */
public class SettingsController extends GDPController implements Initializable {
    
    @FXML private JFXTextField appNameTF;
    @FXML private JFXSlider minQteSlider;
    @FXML private JFXButton saveBtn;
    @FXML private ChoiceBox languagesCB;
    @FXML private JFXToggleButton animationsTB;
    @FXML private Label minQteValue;
    @FXML private AnchorPane anchorPane;
    
    ObservableList<String> langsList = FXCollections.observableArrayList();
    MainController parentController;

    public MainController getParentController() {
        return parentController;
    }

    public void setParentController(MainController parentController) {
        this.parentController = parentController;
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
               
        try {
            
            bundle = rb;
            
            initFields();
            initSlider(minQteSlider, minQteValue);
            
        } catch (SQLException ex) {
            exceptionLayout(ex, saveBtn);
        }
    }


    public void initSlider(JFXSlider slider, Label sliderLabel){

        slider.valueProperty().addListener((obs,oldVal,newVal)->{
            slider.setValue(newVal.intValue());
        });

        sliderLabel.textProperty().bindBidirectional(slider.valueProperty(), NumberFormat.getIntegerInstance());
        
    }    

    private void initLanguagesList() {
        
        langsList.addAll(new String[]{ 
            bundle.getString("arabic"), 
            bundle.getString("english"), 
            bundle.getString("french") 
        });
        
        languagesCB.setItems(langsList);
    }

    private void getCurrentLanguage() throws SQLException {
        
        String currentLang = Common.getSettingValue("app_language");
        
        switch(currentLang){
            case "ar_DZ":
                languagesCB.getSelectionModel().select(0);
                break;
            case "en_DZ":
                languagesCB.getSelectionModel().select(1);
                break;
            case "fr_DZ":
                languagesCB.getSelectionModel().select(2);
                break;
        }
        
        if(currentLang.equals("ar_DZ"))
            anchorPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        
    }

    private void initFields() throws SQLException {
        
        appNameTF.setText(Common.getSettingValue("app_name"));
        minQteValue.setText(Common.getSettingValue("min_qte"));
        minQteSlider.setValue(Double.valueOf(Common.getSettingValue("min_qte")));

        if(Common.getSettingValue("animations").equals("true"))
            animationsTB.setSelected(true);
        else
            animationsTB.setSelected(false);

        initLanguagesList();

        getCurrentLanguage();
        
    }
    
}
