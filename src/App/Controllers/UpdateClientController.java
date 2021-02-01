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
import static Include.Common.getConnection;
import static Include.Init.ERROR_SMALL;
import static Include.Init.FXMLS_PATH;
import static Include.Init.IMAGES_PATH;
import static Include.Init.INFO_SMALL;
import Include.SMController;
import animatefx.animation.BounceIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
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
import javafx.stage.FileChooser;

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
        
        clientIV.setOnMouseClicked(Action -> {
            updateImage();
        });
        
        saveBtn.setOnAction(action -> {
            updateClient(action);
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

    private void updateImage() {

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Select a .JPG .PNG .GIF image", "*.jpg", "*.png", "*.gif")
        );
       
        selectedFile = fileChooser.showOpenDialog(saveBtn.getScene().getWindow());
       
        if (selectedFile != null) {
            
            try {
               
                String createImagePath = Common.saveSelectedImage(selectedFile);
                
                currentImage = selectedClient.getImage();

                selectedClient.setImage(createImagePath);

                clientIV.setFill(new ImagePattern(new Image(
                        selectedFile.toURI().toString(),
                        clientIV.getCenterX(), clientIV.getCenterY(), false, false)));
                animateNode(new BounceIn(clientIV)); 

            }
            catch (IOException ex) {
                exceptionLayout(ex, saveBtn);
            }
        }    
    }

    private void updateClient(ActionEvent action) {
        
        if (checkInputs()) {
            try {
                if(!Client.nameExist(fullnameTF.getText()) || selectedClient.getFullname().equals(fullnameTF.getText())){
                    try (Connection con = getConnection()) {
                        if(con == null) {
                            customDialog(bundle.getString("connection_error"), bundle.getString("connection_error_msg"), ERROR_SMALL, true, saveBtn);
                        }

                        PreparedStatement ps;

                        ps = con.prepareStatement("UPDATE client SET fullname = ?, tel = ?, rc = ?, nif = ?, ai = ?, image = ? WHERE client_id = ?");

                        ps.setString(1, fullnameTF.getText().trim());
                        ps.setString(2,phoneTF.getText().trim());
                        ps.setString(3, regcomTF.getText().trim());
                        ps.setString(4, nifTF.getText().trim());
                        ps.setString(5, aiTF.getText().trim());
                        ps.setString(6, selectedClient.getImage());
                        ps.setInt(7, selectedClient.getID());
                        ps.executeUpdate();

                        con.close();
                    }

                    if(!currentImage.equals(selectedClient.getImage())){
                        Common.deleteImage(currentImage);
                    }
                    
                    selectedClient.setFullname(fullnameTF.getText().trim());
                    selectedClient.setPhone(phoneTF.getText().trim());
                    selectedClient.setRegCom(regcomTF.getText().trim());
                    selectedClient.setNif(nifTF.getText().trim());
                    selectedClient.setAi(aiTF.getText().trim());

                    customDialog(bundle.getString("client_updated"), bundle.getString("client_updated_msg"), INFO_SMALL, true, saveBtn);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
                    AnchorPane root = (AnchorPane)loader.load();
                    MainController mControl = (MainController)loader.getController();
                    mControl.getEmployer(employer);
                    mControl.showClient(selectedClient);
                    mControl.returnMenu("clients");
                    ((Node)action.getSource()).getScene().getWindow().hide();
                    Common.startStage(root, (int)root.getWidth(), (int)root.getHeight());
                
            }
            else{
                customDialog(bundle.getString("fullname_exist"), bundle.getString("fullname_exist_msg"), INFO_SMALL, true, saveBtn);
            }
            }
            catch (IOException | NumberFormatException | SQLException e) {
                exceptionLayout(e, saveBtn);
            }

        }  
    }
    
    @Override
    public boolean checkInputs()
    {
        if (fullnameTF.getText().trim().equals("")){
            customDialog(bundle.getString("missing_fields"), bundle.getString("missing_fields_msg"), ERROR_SMALL, true, saveBtn);
            return false;
        }
        else if(!fullnameTF.getText().matches("^[\\p{L} .'-]+$")){
            customDialog(bundle.getString("invalid_name"), bundle.getString("invalid_name_msg"), ERROR_SMALL, true, saveBtn);
            return false;              
        }
        else if(!phoneTF.getText().trim().matches("^[5-7]?[0-9]{10}$") && !phoneTF.getText().equals("")){
            customDialog(bundle.getString("invalid_phone"), bundle.getString("invalid_phone"), ERROR_SMALL, true, saveBtn);
            return false;              
        }        
       
        return true;
    }
}
