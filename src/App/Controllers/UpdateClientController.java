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
package App.Controllers;

import Data.Client;
import Data.User;
import Include.Common;
import static Include.Init.FXMLS_PATH;
import static Include.Init.IMAGES_PATH;
import Include.SMController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * FXML Controller class
 *
 * @author user
 */
public class UpdateClientController extends SMController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Button returnBtn;
    @FXML
    private JFXTextField fullnameTF,phoneTF,regcomTF,nifTF,aiTF;
    @FXML
    private Circle clientIV;
    @FXML
    private JFXButton saveBtn;
    
    
    String currentImage = "";
    Client selectedClient = new Client();
    File selectedFile = null;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        try {
            isAnimated();
        } catch (SQLException ex) {
            exceptionLayout(ex, saveBtn);
        }        
        
        if(bundle.getLocale().getLanguage().equals("ar"))
            anchorPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT); 
        
        
        returnBtn.setOnAction(Action -> {
            
            try {
                ((Node)Action.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                MainController mControl = (MainController)loader.getController();
                mControl.getEmployer(employer);
                mControl.returnMenu("clients");
                Common.startStage(root, (int)root.getWidth(), (int)root.getHeight());
                
            } catch (IOException ex) {
                exceptionLayout(ex, saveBtn);
            }
        });
        
        Common.controlDigitField(phoneTF);
    }    
    
    void setInfo(User user, Client client) {
        
        employer = user;
        selectedClient = client;
        
    }

    void fillFields(Client client) {
        
        fullnameTF.setText(client.getFullname());
        phoneTF.setText(client.getPhone());
        regcomTF.setText(client.getRegCom());
        nifTF.setText(client.getNif());
        aiTF.setText(client.getAi());
        
        if (client.getImage().equals("")) {
            clientIV.setFill(new ImagePattern(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/user.png"),
                    96, 96, false, false)));
        }
        else {
            clientIV.setFill(new ImagePattern(new Image(
                    new File(client.getImage()).toURI().toString(),
                    96, 96, false, false)));
        }
    }
}
