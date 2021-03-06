package App.Controllers;

import Data.Buy;
import Data.Client;
import Data.Payment;
import Data.Product;
import static Data.Product.getActiveProducts;
import Data.Sell;
import Data.User;
import static Data.User.getUsers;
import Include.Common;
import static Include.Common.controlDigitField;
import static Include.Common.dateFormatter;
import static Include.Common.getConnection;
import static Include.Common.initLayout;
import static Include.Common.startStage;
import Include.Init;
import Include.SMController;
import JR.JasperReporter;
import animatefx.animation.AnimationFX;
import animatefx.animation.FadeIn;
import animatefx.animation.FlipInY;
import animatefx.animation.Shake;
import animatefx.animation.Tada;
import animatefx.animation.ZoomIn;
import animatefx.animation.ZoomOut;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author med
 */
public class MainController extends SMController implements Initializable,Init {
    
    
    @FXML public Label btn_close,settingsBtn;
    @FXML public AnchorPane products, sells, employers,buys,clients;
    @FXML public TableView<Sell> sellsTable ;
    @FXML public TableView<Buy> buysTable;
    @FXML public TableView<Payment> paymentsTable;
    @FXML public TableColumn<Payment, String> payDateCol;
    @FXML public TableColumn<Payment, Integer> paySumCol;
    @FXML public TableColumn<Buy, Integer> buyQteCol,buyPriceCol,buyTotalCol ;
    @FXML public TableColumn<Buy, String> buyProdCol,buySupplierCol,buyDateCol ;
    @FXML public TableColumn<Sell, Integer> sellQuantity,sellTotalCol,sellPrice ;
    @FXML public TableColumn<Sell, String> sellRef,clientCol,sellDateCol ;
    @FXML public TableColumn sellActions,sellActions2,buyAction1,buyAction2,editPaymentCol,deletePaymentCol ;   
    @FXML public ChoiceBox<String> usersCB;
    @FXML public ChoiceBox<Client> clientsCB;
    @FXML public TextField searchBuy ;
    @FXML public Label fullnameLabel,phoneLabel,emptyQte,idField,revSum,revTotal,revQte,buyDayTotal,buyDayQte,buyDaySum,userStatus,
                        lastLogged,debtStatus,clientPhone,regCommerce,clientNIF,clientAI,paidLabel,remainLabel; 
    @FXML public Button seeRecords,day,week,month,total,btn_products, btn_sells, btn_employers,btn_buys,btn_clients;
    @FXML public ImageView prodManager,userManager,sellManager,buyManager;
    @FXML public Pane billPane,billPane1;
    @FXML public JFXTextField searchField,productNameTF,productPriceTF,productQteTF,sellSearch ;
    @FXML public JFXDatePicker productDP,sellDateField,buyDateField;
    @FXML public JFXButton viewHistory,addProd,printProducts,removedProduct,deleteProduct,updateProduct,
                            updateEmployer,deleteEmployer,changePass,printSells,sellStats,newBillBtn,newSellButton,printEmployers,
                            exBtn,addEmployerButton,printBuy,printBuys,newBuyBtn,buyStatBtn,addClientBtn,printClients,updateClientBtn,
                            carryPayBtn,deleteClientBtn;
    
    @FXML public VBox infoContainer,productVB,clientBox,noUsersBox,noProductsBox,paymentsBox;
    @FXML public HBox productHB,clientTabBox;
    @FXML public Circle productIV,userIV,clientIV;
    
    ObservableList<Product> data = FXCollections.observableArrayList();
    ObservableList<Sell> sellsList = FXCollections.observableArrayList(); 
    ObservableList<String> employersList = FXCollections.observableArrayList();
    ObservableList<Client> clientsList = FXCollections.observableArrayList();
    ObservableList<Payment> paymentsList = FXCollections.observableArrayList();
    ObservableList<Buy> buysList = FXCollections.observableArrayList();
    
    
    File selectedFile = null;
    JasperReporter jr = new JasperReporter();
    int MIN_QUANTITY;
    
    public void loadProducts(ObservableList<Product> list){
        
        productVB.getChildren().clear();
        
        getAllProducts();
       
        if(!list.isEmpty()){
            list.forEach((product) -> {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "Item.fxml"), bundle);
                    HBox root = (HBox)loader.load();
                    Node node = (Node)loader.getRoot();
                    ItemController iController = (ItemController)loader.getController();                
                    iController.setValues(product);
                    iController.setMainController(this);
                    productVB.getChildren().add(node);
                }catch(IOException e){
                    e.printStackTrace();
                }
            });
        }
        
    }
    
    public void customDialog(String title, String body, String icon, boolean btnIncluded){
        
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, title, body, icon);
            
            loadDialog(layout, btnIncluded);
    }
        
    public void confirmDialog(Object object, String type, String title, String body, String icon){
            
        JFXDialogLayout layout = new JFXDialogLayout();
        initLayout(layout, title, body, icon);
            
        stackPane.setVisible(true);
        JFXButton yesBtn = new JFXButton(bundle.getString("yes"));
        yesBtn.setDefaultButton(true);
        yesBtn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            yesBtn.setDefaultButton(false);
            if(null != type)
                switch (type) {
                case "employer":
                    deleteEmployer((User) object);
                    break;
                case "buy":
                    deleteBuy((Buy) object);
                    break;
                case "sell":
                    deleteSell((Sell) object);
                    break;
                case "product":
                    deleteProduct();
                    break;
                case "client":
                    deleteClient((Client) object);
                default:
                    break;
            }
        });
        JFXButton noBtn = new JFXButton(bundle.getString("no"));
        noBtn.setCancelButton(true);
        noBtn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            noBtn.setCancelButton(false);
        });        
        
        layout.setActions(yesBtn, noBtn);
        
        dialog = new JFXDialog(stackPane, layout , JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.show();
    
    }
    
    public void loadDialog(JFXDialogLayout layout, boolean btnIncluded){
        
        stackPane.setVisible(true);
        JFXButton btn = new JFXButton(bundle.getString("okay"));
        btn.setDefaultButton(true);
        btn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            btn.setDefaultButton(false);
        });
        if(btnIncluded){
            layout.setActions(btn);
        }    
        dialog = new JFXDialog(stackPane, layout , JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.show();
        
    }
    
    public void exceptionLayout(Exception e){
        customDialog( bundle.getString("unknown_error"), e.getLocalizedMessage(), ERROR_SMALL, true);
    }

    private void onJasperReportLoading(){
        
        customDialog(bundle.getString("please_wait"), bundle.getString("report_wait_msg"), WAIT_SMALL, false);
            
        jr = new JasperReporter();
    }

    public void getEmployer(User employer) {
                
        this.employer = employer ;
        
        if(employer.getAdmin() == 0)
        {
            if(employer.getProdPrivs() == 0){
                btn_products.setDisable(true);
                products.setVisible(false);
            }
            if(employer.getSellPrivs() == 0){
                btn_sells.setDisable(true);
                sells.setVisible(false);
            }
            if(employer.getBuyPrivs() == 0){
                btn_buys.setDisable(true);
                buys.setVisible(false);
            }
            if(employer.getUserPrivs() == 0){
                btn_employers.setDisable(true);
                employers.setVisible(false);
            }

            if(employer.getProdPrivs() == 1){
                products.setVisible(true);
                sells.setVisible(false);
                buys.setVisible(false);
                employers.setVisible(false);
            }
            else if(employer.getUserPrivs() == 1){
                products.setVisible(false);
                sells.setVisible(false);
                buys.setVisible(false);
                employers.setVisible(true);                
            }
            else if(employer.getBuyPrivs() == 1){
                products.setVisible(false);
                sells.setVisible(false);
                buys.setVisible(true);
                employers.setVisible(false);                
            }
            else if(employer.getSellPrivs() == 1){
                products.setVisible(false);
                sells.setVisible(true);
                buys.setVisible(false);
                employers.setVisible(false);                
            }            
        }

        
        if (!employersList.isEmpty()) {
            showEmployer(employer.getUsername());
        }

    }        
    
    public void getAllProducts()
    {
        try {
            data = getActiveProducts();
        }
        catch (SQLException e) {            
            exceptionLayout(e);
        }
    }
    
    private void search(String tableName)
    {
        
        if(tableName.trim().equalsIgnoreCase("sells")){
        String keyword = sellSearch.getText();
        
        if (keyword.trim().equals("")) {
            sellsTable.setItems(sellsList);
        }
        
        else {
            ObservableList<Sell> filteredSells = FXCollections.observableArrayList();
            sellsList.stream().filter((sell) -> (sell.getProduct().getName().toLowerCase().contains(keyword.toLowerCase()) || sell.getSeller().toLowerCase().contains(keyword.toLowerCase()))).forEachOrdered((sell) -> {
                filteredSells.add(sell);
            });
            sellsTable.setItems(filteredSells);
        }
        }
        else if(tableName.trim().equalsIgnoreCase("products")){
            
            String keyword = searchField.getText();

           if (keyword.trim().equals("")) {
               loadProducts(data);
           }

           else {
               ObservableList<Product> filteredData = FXCollections.observableArrayList();
               data.stream().filter((product) -> (product.getName().toLowerCase().contains(keyword.toLowerCase()))).forEachOrdered((product) -> {
                   filteredData.add(product);
                });
               loadProducts(filteredData);
           }           
        }
        
        else if(tableName.trim().equalsIgnoreCase("buys")){
            
            String keyword = searchBuy.getText();

           if (keyword.trim().equals("")) {
               buysTable.setItems(buysList);
           }

           else {
               ObservableList<Buy> filteredBuys = FXCollections.observableArrayList();
               buysList.stream().filter((buy) -> (buy.getProduct().toLowerCase().contains(keyword.toLowerCase()) || buy.getUser().toLowerCase().contains(keyword.toLowerCase()))).forEachOrdered((buy) -> {
                   filteredBuys.add(buy);
                });
               buysTable.setItems(filteredBuys);
           }           
        }        
    }    

    private void updateImage()
    {

        if(productVB.getChildren().isEmpty())
        {
            customDialog(bundle.getString("info_msg"),  bundle.getString("select_product_msg"), INFO_SMALL, true);           
            return;
        }

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Select a .JPG .PNG .GIF image", "*.jpg", "*.png", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(addProd.getScene().getWindow());
       
        if (selectedFile != null) {
            
            try {
               
                String createImagePath = Common.saveSelectedImage(selectedFile);

                try (Connection con = Common.getConnection()) {
                    String query = "UPDATE product SET image_url = ? WHERE prod_id = ?";
                    
                    PreparedStatement ps = con.prepareStatement(query);
                    
                    ps.setString(1, createImagePath);
                    ps.setInt(2, Integer.parseInt(idField.getText()));
                    
                    ps.executeUpdate();
                }

                Product selectedProduct = Product.getProductByID(Integer.parseInt(idField.getText()));

                Common.deleteImage(selectedProduct.getImageURL());

                selectedProduct.setImageURL(createImagePath);
                
                productIV.setFill(new ImagePattern(new Image(
                    selectedFile.toURI().toString(), 150, 150, true, true)));
                
                loadProducts(data);
                
            }
            catch (NumberFormatException | SQLException | IOException ex) {
                exceptionLayout(ex);
            }
        }

    }    
    
    @Override
    public boolean checkInputs()
    {
        if (productNameTF.getText().trim().equals("") || productPriceTF.getText().trim().equals("") || productQteTF.getText().trim().equals("")) {
            customDialog(bundle.getString("missing_fields"), bundle.getString("missing_fields_msg"), ERROR_SMALL, true);
            return false;
        }     
        
        try {
            Integer.parseInt(productPriceTF.getText());
            if(productPriceTF.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(productPriceTF.getText()) > 0){
                if(productQteTF.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(productPriceTF.getText()) >= 0){
                    return true;
                }
                else{
                    customDialog(bundle.getString("invalid_qte"), bundle.getString("invalid_qte_msg"), ERROR_SMALL, true);                  
                    return false;
                }
            }
            else{
                customDialog(bundle.getString("invalid_price"), bundle.getString("invalid_price_msg"), ERROR_SMALL, true);                     
                return false;
            }
        }
        catch (NumberFormatException e) {
            exceptionLayout(e);
            return false;
        }
    }    
    
    private void updateProduct()
    {

        if(productVB.getChildren().isEmpty())
        {
            customDialog(bundle.getString("info_msg"), bundle.getString("select_product_msg_2"), ERROR_SMALL, true);              
            return;
        }
        
        if (checkInputs() == false) {
            return;
        }

        try {
            
            Product selectedProduct = Product.getProductByID(Integer.parseInt(idField.getText()));
            String sqlDate = "";
            String javaDate = "";
            try (Connection con = getConnection()) {
                String query;
                
                query = "UPDATE product SET name = ?, sell_price = ?, add_date = ?,prod_quantity = ?, last_change = ? WHERE prod_id = ?";
                
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, productNameTF.getText());
                ps.setInt(2, Integer.valueOf(productPriceTF.getText()));
                ps.setInt(4, Integer.valueOf(productQteTF.getText()));
                ps.setDate(3, Date.valueOf(productDP.getEditor().getText()));
                
                java.util.Date date = new java.util.Date();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.text.SimpleDateFormat ndf = new java.text.SimpleDateFormat("yyyy/MM/dd h.mm a");
                sqlDate = sdf.format(date);
                javaDate = ndf.format(date);
                ps.setString(5, sqlDate);            
                ps.setInt(6, Integer.parseInt(idField.getText()));
                ps.executeUpdate();
                
                if(!selectedProduct.getName().equals(productNameTF.getText()) || !selectedProduct.getAddDate().equals(productDP.getEditor().getText()) || selectedProduct.getProdQuantity() != Integer.valueOf(productQteTF.getText()) || selectedProduct.getSellPrice() != Integer.valueOf(productPriceTF.getText())){
                query = "INSERT INTO product_history(prod_id, user_id, change_date, new_name, new_date, new_price, new_qte, old_name, old_date, old_price, old_qte) VALUES(?,?,?,?,?,?,?,?,?,?,?)" ;
                
                ps = con.prepareStatement(query);
                ps.setInt(1, Integer.parseInt(idField.getText()));
                ps.setInt(2, this.employer.getUserID());
                ps.setString(3, sqlDate);                
                ps.setString(4, productNameTF.getText());
                ps.setDate(5, Date.valueOf(productDP.getEditor().getText()));
                ps.setInt(6, Integer.valueOf(productPriceTF.getText()));
                ps.setInt(7, Integer.valueOf(productQteTF.getText()));
                ps.setString(8, selectedProduct.getName());
                ps.setString(9, selectedProduct.getAddDate());
                ps.setInt(10,selectedProduct.getSellPrice());
                ps.setInt(11,selectedProduct.getProdQuantity());
                
                ps.executeUpdate();
                
                }
                
                con.close();
            }

            selectedProduct.setName(productNameTF.getText());
            selectedProduct.setSellPrice(Integer.valueOf(productPriceTF.getText()));
            selectedProduct.setProdQuantity(Integer.valueOf(productQteTF.getText()));
            selectedProduct.setAddDate(productDP.getEditor().getText());
            selectedProduct.setLastChange(javaDate);
                                                
            customDialog(bundle.getString("product_updated"), bundle.getString("product_updated_msg"), INFO_SMALL, true);
            
            animateNode(new FadeIn(productVB));
            
            loadProducts(data);

        }
        catch (NumberFormatException | SQLException e) {
            exceptionLayout(e);
        }
    }   
    
    private void showProduct(int index)
    {
                
        animateNode(new FadeIn(productHB));
        productNameTF.setText(data.get(index).getName());
        productQteTF.setText(String.valueOf(data.get(index).getProdQuantity()));
        productDP.getEditor().setText(data.get(index).getAddDate());
        productPriceTF.setText(String.valueOf(data.get(index).getSellPrice()));
        idField.setText(String.valueOf(data.get(index).getProdID()));        

        if (data.get(index).getImageURL() == null) {
            productIV.setFill(new ImagePattern(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/large_product_primary.png"),
                    64, 64, false, false)));
        }
        else {
            productIV.setFill(new ImagePattern(new Image(
                    new File(data.get(index).getImageURL()).toURI().toString(),
                    productIV.getCenterX(), productIV.getCenterY(), false, false)));
        }        
        
        if(data.get(index).getProdQuantity() < MIN_QUANTITY){
            emptyQte.setVisible(true);
        }
        else
            emptyQte.setVisible(false);    
        
    }
    
    public void showEmployer(String username)
    {
        
        try {
            animateNode(new ZoomOut(infoContainer));
            animateNode(new ZoomIn(infoContainer));
            User choosen = User.getUserByName(username);
            if(choosen != null){
                fullnameLabel.setText(choosen.getFullname());
                if(!choosen.getPhone().trim().equals(""))
                    phoneLabel.setText(choosen.getPhone()) ;
                else
                    phoneLabel.setText(bundle.getString("no_phone"));
                
                if (choosen.getImage().trim().equals("")) {
                    userIV.setFill(new ImagePattern(new Image(
                            ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/user.png"),
                            userIV.getCenterX(), userIV.getCenterY(), false, false)));
                }
                else {
                    userIV.setFill(new ImagePattern(new Image(
                            new File(choosen.getImage()).toURI().toString(),
                            userIV.getCenterX(), userIV.getCenterX(), false, false)));
                }
                
                if(choosen.getProdPrivs() == 1) prodManager.setImage(new Image(
                        ClassLoader.class.getResourceAsStream(IMAGES_PATH + "small/granted.png"),
                        25, 25, true, true));
                else
                    prodManager.setImage(new Image(
                            ClassLoader.class.getResourceAsStream(IMAGES_PATH + "small/notgranted.png"),
                            25, 25, true, true));
                
                if(choosen.getBuyPrivs() == 1) buyManager.setImage(new Image(
                        ClassLoader.class.getResourceAsStream(IMAGES_PATH + "small/granted.png"),
                        25, 25, true, true));
                else
                    buyManager.setImage(new Image(
                            ClassLoader.class.getResourceAsStream(IMAGES_PATH + "small/notgranted.png"),
                            25, 25, true, true));
                
                
                if(choosen.getSellPrivs() == 1) sellManager.setImage(new Image(
                        ClassLoader.class.getResourceAsStream(IMAGES_PATH + "small/granted.png"),
                        25, 25, true, true));
                else
                    sellManager.setImage(new Image(
                            ClassLoader.class.getResourceAsStream(IMAGES_PATH + "small/notgranted.png"),
                            25, 25, true, true));
                
                
                if(choosen.getUserPrivs() == 1) userManager.setImage(new Image(
                        ClassLoader.class.getResourceAsStream(IMAGES_PATH + "small/granted.png"),
                        25, 25, true, true));
                else
                    userManager.setImage(new Image(
                            ClassLoader.class.getResourceAsStream(IMAGES_PATH + "small/notgranted.png"),
                            25, 25, true, true));
                
            }
            
            if((choosen.getBuyPrivs() + choosen.getProdPrivs() + choosen.getSellPrivs() + choosen.getUserPrivs()) == 0){
                userStatus.setVisible(true);
            }
            else{
                userStatus.setVisible(false);
            }
            
            
            if(!choosen.getLastLogged().equals(""))
                lastLogged.setText(choosen.getLastLogged());
            else
                lastLogged.setText(bundle.getString("null_login"));
            
            usersCB.getSelectionModel().select(username);
        } catch (SQLException ex) {
            exceptionLayout(ex);
        }
  
    }

    public void showClient(Client choosen) {
            
        animateNode(new ZoomOut(clientBox));
        animateNode(new ZoomIn(clientBox));
        animateNode(new ZoomOut(paymentsBox));
        animateNode(new ZoomIn(paymentsBox));

        if(choosen != null){

        if(!choosen.getPhone().trim().equals(""))
            clientPhone.setText(choosen.getPhone()) ;
        else
            clientPhone.setText(bundle.getString("not_specified"));

        if(!choosen.getRegCom().trim().equals(""))
            regCommerce.setText(choosen.getRegCom()) ;
        else
            regCommerce.setText(bundle.getString("not_specified"));

        if(!choosen.getAi().trim().equals(""))
            clientAI.setText(choosen.getAi()) ;
        else
            clientAI.setText(bundle.getString("not_specified"));

        if(!choosen.getNif().trim().equals(""))
            clientNIF.setText(choosen.getNif()) ;
        else
            clientNIF.setText(bundle.getString("not_specified"));

        if (choosen.getImage().trim().equals("")) {
            clientIV.setFill(new ImagePattern(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/user.png"),
                    clientIV.getCenterX(), clientIV.getCenterY(), false, false)));
        }
        else {
            clientIV.setFill(new ImagePattern(new Image(
                    new File(choosen.getImage()).toURI().toString(),
                    clientIV.getCenterX(), clientIV.getCenterX(), false, false)));
        }

        paidLabel.setText(String.valueOf(choosen.getPaid()) + " " + bundle.getString("currency"));
        remainLabel.setText(String.valueOf(choosen.getRemain()) + " " + bundle.getString("currency"));


        clientsCB.setValue(choosen);
        clientsCB.getSelectionModel().select(choosen);

        initPaymentsTable();

        }
    }    
       

    /*private void showNextProduct()
    {
        if (productsTable.getSelectionModel().getSelectedIndex() < data.size() - 1) {
            int currentSelectedRow = productsTable.getSelectionModel().getSelectedIndex() + 1;
            productsTable.getSelectionModel().select(currentSelectedRow);
            showProduct(currentSelectedRow);
        }
    }*/    

    private void deleteProduct()
    {

        if(productVB.getChildren().isEmpty())
        {
            customDialog(bundle.getString("info_msg"), bundle.getString("select_product_msg_3"), INFO_SMALL, true);       
            return;
        }
        
        try {
            
            Product selectedProduct = Product.getProductByID(Integer.parseInt(idField.getText()));
            
            selectedProduct.toTrash();
            
            data.remove(selectedProduct);
            
            loadProducts(data);
            
            customDialog(bundle.getString("product_deleted"), bundle.getString("product_deleted_msg"), INFO_SMALL, true);
            
            System.out.println(data.size());
            
            if(data.size() > 0) {
                //showNextProduct();
            }
            else
            {
                idField.setText("");
                productNameTF.setText("");
                productPriceTF.setText("");
                productQteTF.setText("");
                productDP.getEditor().setText("");
                productIV.setFill(new ImagePattern(new Image(
                        ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/large_product_primary.png"),
                        60, 60, true, true)));
            }
            
        }
        catch (SQLException e) {
            exceptionLayout(e);
        }
        
        
    }    
    
    public void getAllSells(String selectedDate)
    {
        try {
            sellsList = Sell.getSellsByDate(selectedDate, bundle);
            initSellsTable();
        } catch (SQLException ex) {
            exceptionLayout(ex);
        }
    }
    
    public void getAllBuys(String selectedDate)
    {
        try {
            buysList = Buy.getBuysByDate(selectedDate);
            initBuysTable();
        } catch (SQLException ex) {
            exceptionLayout(ex);
        }
        
    }    
    
    public void getSellStats(String selectedDate){
        
        try {
            animateNode(new FadeIn(revQte));
            animateNode(new FadeIn(revSum));
            animateNode(new FadeIn(revTotal));
            
            animateNode(new FadeIn(sellsTable));
            
            ResultSet rs = Sell.getTodayStats(selectedDate);

            while (rs.next()) {
                
                revSum.setText(String.valueOf(rs.getInt("SUM(sell_price)")) + " " + bundle.getString("currency"));
                revTotal.setText(String.valueOf(rs.getInt("count(*)")) + " " + bundle.getString("buy"));
                revQte.setText(String.valueOf(rs.getInt("SUM(sell_quantity)")) + " " + bundle.getString("pieces"));
                
            }
            

        } catch (SQLException ex) {
            exceptionLayout(ex);
        }
        
    }
    
    public void getBuyStats(String selectedDate){
                
        try {
            animateNode(new FadeIn(buyDayQte));
            animateNode(new FadeIn(buyDaySum));
            animateNode(new FadeIn(buyDayTotal));
            
            animateNode(new FadeIn(buysTable));
            
            ResultSet rs = Buy.getTodayStats(selectedDate);
            
            while (rs.next()) {
                
                buyDaySum.setText(String.valueOf(rs.getInt("SUM(buy_price)")) + " " + bundle.getString("currency"));
                buyDayTotal.setText(String.valueOf(rs.getInt("count(*)")) + " " + bundle.getString("buy"));
                buyDayQte.setText(String.valueOf(rs.getInt("SUM(buy_qte)")) + " " + bundle.getString("pieces"));
                
            }
            
        } catch (SQLException ex) {
            exceptionLayout(ex);
        }
        
    }     
  
    private void deleteSell(Sell selectedSell)
    {
        
        try {

            selectedSell.delete();
            
            getAllProducts();
            loadProducts(data);
            sellsList.remove(selectedSell);
            sellsTable.refresh();
            
            getSellStats(sellDateField.getEditor().getText());
            
            customDialog(bundle.getString("sell_deleted"), bundle.getString("sell_deleted_msg"), INFO_SMALL, true);
            
        }
        catch (SQLException e) {
             exceptionLayout(e);
        }
    }
    
    private void deleteBuy(Buy buy) {
        try {

            buy.delete();

            loadProducts(data);

            buysList.remove(buy);

            buysTable.refresh();

            getBuyStats(buyDateField.getEditor().getText());
            
            customDialog(bundle.getString("buy_deleted"), bundle.getString("buy_deleted_msg"), INFO_SMALL, true); 

        }
        catch (SQLException e) {
            exceptionLayout(e);
        }            
    }    
    
    private void getAllEmployers()
    {

        try {
            employersList = getUsers(ACTIVE);
        }
        catch (SQLException e) {          
            exceptionLayout(e);
        }
    }
    
    private void getClientsNames()
    {

        try {
            clientsList = Client.getClients(bundle, true);
        }
        catch (SQLException e) {          
            exceptionLayout(e);
        }
    }  

    private void deleteEmployer(User selectedEmployer)
    {

        try {

            if( !User.isLastAdmin() || selectedEmployer.getAdmin() != 1){            
                
                selectedEmployer.toTrash();
            
                if(this.employer.getUsername().equals(selectedEmployer.getUsername())){

                            this.addProd.getScene().getWindow().hide();
                            AnchorPane root = FXMLLoader.load(getClass().getResource(FXML_PATH + "Login.fxml"), bundle);
                            startStage(root, 450, 350);

                }
                else{

                    usersCB.getItems().clear();
                    getAllEmployers();
                    usersCB.setItems(employersList);

                    if (!employersList.isEmpty()) {
                        usersCB.getSelectionModel().select(this.employer.getUsername());
                        showEmployer(this.employer.getUsername());
                    }

                    customDialog(bundle.getString("user_deleted"), bundle.getString("user_deleted_msg"), INFO_SMALL, true);

                }
            }
            else{
                customDialog(bundle.getString("last_admin"), bundle.getString("last_admin_msg"), INFO_SMALL, true); 
            }
            
        }
        catch (IOException | SQLException e) {
            exceptionLayout(e);
        }
    }
    
    private void deleteClient(Client selected)
    {

        try {           
                
                selected.delete();
                clientsCB.getItems().clear();
                getClientsNames();
                clientsCB.setItems(clientsList);

                if (!clientsList.isEmpty()) {
                    clientsCB.getSelectionModel().select(0);
                    showClient(clientsCB.getItems().get(0));
                }
                else{
                    clientTabBox.setVisible(false);
                    noUsersBox.setVisible(true);
                }

                customDialog(bundle.getString("client_deleted"), bundle.getString("client_deleted_msg"), INFO_SMALL, true);
            
        }
        catch (SQLException e) {
            exceptionLayout(e);
        }
    }
    
    @FXML
    public void close(MouseEvent event){
        
        System.exit(0);
    }
    
    /*private void initProductsTable(){ 
        
        prodName.setCellValueFactory(new PropertyValueFactory<>("name"));
        prodQuantity.setCellValueFactory(new PropertyValueFactory<>("prodQuantity"));
        sellProd.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        addDate.setCellValueFactory(new PropertyValueFactory<>("AddDate"));
        lastChange.setCellValueFactory(new PropertyValueFactory<>("lastChange"));

        productsTable.setItems(data);        
        
    }*/
    
    private void delete(String item, boolean empty){
        
        
        
    }
    
    private void initSellsTable(){
        
        sellQuantity.setCellValueFactory(new PropertyValueFactory<>("sellQuantity"));
        sellRef.setCellValueFactory(new PropertyValueFactory<>("sellName"));
        sellPrice.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        clientCol.setCellValueFactory(new PropertyValueFactory<>("client"));
        sellDateCol.setCellValueFactory(new PropertyValueFactory<>("sellDate"));
        sellTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        sellActions.setCellValueFactory(new PropertyValueFactory<>("sellActions"));
        Callback<TableColumn<Sell, String>, TableCell<Sell, String>> cellFactory
                =                 //
        (final TableColumn<Sell, String> param) -> {
            final TableCell<Sell, String> cell = new TableCell<Sell, String>() {

                final Button update = new Button();

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } 
                    else {
                        update.setGraphic(new ImageView(new Image(IMAGES_PATH + "small/edit_small_white.png", 24, 24, false, false)));
                        update.setOnAction(event -> {

                            Sell sell = getTableView().getItems().get(getIndex());
                            try {

                                ((Node)event.getSource()).getScene().getWindow().hide();
                                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "UpdateSell.fxml"), bundle);
                                AnchorPane root = (AnchorPane)loader.load();
                                UpdateSellController usControl = (UpdateSellController)loader.getController();
                                usControl.fillFields(sell);
                                usControl.getData(employer,sell);
                                startStage(root,(int)root.getWidth(), (int)root.getHeight());

                            } catch (IOException ex) {
                                exceptionLayout(ex);
                            }
                        });
                        update.setStyle("-fx-background-color : #3d4956; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                        setGraphic(update);
                        setText(null);               

                    }
                }
            };
            return cell;
        };
            
        sellActions.setCellFactory(cellFactory);
        
        sellActions2.setCellValueFactory(new PropertyValueFactory<>("sellActions2"));        
                   
        Callback<TableColumn<Sell, String>, TableCell<Sell, String>> cellFactory2
                =                 //
        (final TableColumn<Sell, String> param) -> {
            final TableCell<Sell, String> cell = new TableCell<Sell, String>() {

                final Button delete = new Button();

                @Override
                public void updateItem(String item, boolean empty) {
                    delete(item, empty);
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        delete.setGraphic(new ImageView(new Image(IMAGES_PATH + "small/trash_small_white.png", 24, 24, false, false)));
                        delete.setOnAction(event -> {
                            Sell sell = getTableView().getItems().get(getIndex());
                            confirmDialog(sell, "sell", bundle.getString("delete"), bundle.getString("are_u_sure"), INFO_SMALL);
                        });
                        delete.setStyle("-fx-background-color : red; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                        setGraphic(delete);
                        setText(null);               

                    }
                }
            };
            return cell;
        };
        
        sellActions2.setCellFactory(cellFactory2);        
        
        sellsTable.setItems(sellsList);
        
        sellsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        printSells.disableProperty().bind(Bindings.size(sellsTable.getItems()).isEqualTo(0));
                
        
    }
    
    public void initBuysTable(){
        
        buyTotalCol.setCellValueFactory(new PropertyValueFactory<>("buyTotalPrice"));
        buyQteCol.setCellValueFactory(new PropertyValueFactory<>("buyQte"));
        buySupplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        buyPriceCol.setCellValueFactory(new PropertyValueFactory<>("buyPrice"));
        buyProdCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        buyDateCol.setCellValueFactory(new PropertyValueFactory<>("buyDate"));
        buyAction1.setCellValueFactory(new PropertyValueFactory<>("buyAction1"));
        Callback<TableColumn<Buy, String>, TableCell<Buy, String>> cellFactoryBuy1
                =                 //
            (final TableColumn<Buy, String> param) -> {
                final TableCell<Buy, String> cell = new TableCell<Buy, String>() {

                    final Button update = new Button();

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            update.setGraphic(new ImageView(new Image(IMAGES_PATH + "small/edit_small_white.png", 24, 24, false, false)));
                            update.setOnAction(event -> {
                                Buy buy = getTableView().getItems().get(getIndex());
                            try {

                                ((Node)event.getSource()).getScene().getWindow().hide();
                                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "UpdateBuy.fxml"), bundle);
                                AnchorPane root = (AnchorPane)loader.load();
                                UpdateBuyController ubControl = (UpdateBuyController)loader.getController();
                                ubControl.setRequirements(employer, buy);
                                startStage(root,( int)root.getWidth(), (int)root.getHeight());

                            }   catch (IOException ex) {
                                    exceptionLayout(ex);
                                }                   
                            });
                            update.setStyle("-fx-background-color : #3d4956; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                            setGraphic(update);
                            setText(null);               

                        }
                    }
                };
                return cell;
            };

                buyAction1.setCellFactory(cellFactoryBuy1);

                buyAction2.setCellValueFactory(new PropertyValueFactory<>("buyAction2"));        

                Callback<TableColumn<Buy, String>, TableCell<Buy, String>> cellFactoryBuy2
                        =                 //
            (final TableColumn<Buy, String> param) -> {
                final TableCell<Buy, String> cell = new TableCell<Buy, String>() {

                    final Button delete = new Button();

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            delete.setGraphic(new ImageView(new Image(IMAGES_PATH + "small/trash_small_white.png", 24, 24, false, false)));
                            delete.setOnAction(event -> {
                                Buy buy = getTableView().getItems().get(getIndex());
                                confirmDialog(buy, "buy", bundle.getString("delete"), bundle.getString("are_u_sure"), INFO_SMALL);
                            });
                            delete.setStyle("-fx-background-color : red; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
                            setGraphic(delete);
                            setText(null);               

                        }
                    }
                };
                return cell;
            };
        
        buyAction2.setCellFactory(cellFactoryBuy2);        
        
        buysTable.setItems(buysList);
        buysTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        printBuys.disableProperty().bind(Bindings.size(buysTable.getItems()).isEqualTo(0));
        
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        bundle = rb;
        
        try {
            isAnimated();
        } catch (SQLException ex) {
            exceptionLayout(ex);
        }

        if(bundle.getLocale().getLanguage().equals("ar"))
            anchorPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        else
            anchorPane.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        
        productDP.setConverter(dateFormatter());
        sellDateField.setConverter(dateFormatter());
        buyDateField.setConverter(dateFormatter()); 
        sellDateField.getEditor().setText(String.valueOf(LocalDate.now()));
        buyDateField.getEditor().setText(String.valueOf(LocalDate.now())); 
        
        getAllProducts();
        //initProductsTable();
        
        loadProducts(data);
        
        controlDigitField(productPriceTF);
        controlDigitField(productQteTF);
        
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            search("products");
        });
        
        if (!data.isEmpty()) {
            showProduct(0);
        }
        
        /*
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showProduct(productsTable.getSelectionModel().getSelectedIndex());
            }
        });*/
        
        productIV.setOnMouseClicked(value -> {
            updateImage();
        });
        
        updateProduct.setOnAction(Action -> {
            updateProduct();
        });        
       
        deleteProduct.setOnAction(Action -> {
            confirmDialog(null, "product", bundle.getString("delete"), bundle.getString("are_u_sure"), INFO_SMALL);
        });
        
        addProd.setOnAction(Action -> {
            try {
                
                ((Node)Action.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "NewProduct.fxml"), bundle);
                Pane root = (Pane)loader.load();
                NewProductController npControl = (NewProductController)loader.getController();
                npControl.getEmployer(this.employer);
                startStage(root, (int)root.getWidth(), (int)root.getHeight());
                
            } catch (IOException ex) {
                exceptionLayout(ex);
            }

        });
        
        removedProduct.setOnAction(Action -> {
            
            try {
                ((Node)Action.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "RemovedProducts.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                RemovedProductsController rpControl = (RemovedProductsController)loader.getController();
                rpControl.getInfo(this.employer);
                startStage(root, (int)root.getWidth(), (int)root.getHeight());
            } catch (IOException ex) {
                exceptionLayout(ex);
            }
                                                   
        
        });

        viewHistory.setOnAction(Action ->{
            
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "ProductHistory.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                ProductHistoryController phControl = (ProductHistoryController)loader.getController();
                Scene scene = new Scene(root);                           
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.getIcons().add(new Image(Common.class.getResourceAsStream(APP_ICON)));
                stage.showAndWait();
            } catch (IOException ex) {
                exceptionLayout(ex);
            }            
            
        });
        
        
        // SELLS TAB
        
        getAllSells(sellDateField.getEditor().getText());
        getSellStats(sellDateField.getEditor().getText());
                
        newSellButton.setOnAction(Action -> {
            
            if(!data.isEmpty()){
            
            try {                
                ((Node)Action.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "NewSell.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                NewSellController nsControl = (NewSellController)loader.getController();
                nsControl.getEmployer(this.employer);
                startStage(root, (int)root.getWidth(), (int)root.getHeight());
                        
            }   catch (IOException ex) {
                    exceptionLayout(ex);
                }
            
            }
            else{
                customDialog(bundle.getString("no_products_found"), bundle.getString("no_products_found_msg"), ERROR_SMALL, true);
            }

        });
        

        sellSearch.textProperty().addListener((obs, oldText, newText) -> {
            search("sells");
        });

        sellDateField.setOnAction(Action -> {
            sellsTable.getItems().clear();
            getAllSells(sellDateField.getEditor().getText());
            getSellStats(sellDateField.getEditor().getText());
            sellsTable.setItems(sellsList);
        });

        sellStats.setOnAction(Action -> {
        
            try {
                Stage stage = new Stage();
                AnchorPane root = FXMLLoader.load(getClass().getResource(FXML_PATH + "SellStats.fxml"), bundle);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.getIcons().add(new Image(Common.class.getResourceAsStream(APP_ICON)));
                stage.showAndWait();
                
            } catch (IOException ex) {
                exceptionLayout(ex);
            }
            
        });        

       // Users Tab
       
        getAllEmployers();
        
        usersCB.setItems(employersList);
        
        usersCB.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showEmployer(usersCB.getSelectionModel().getSelectedItem());
            }
        });         
        
        addEmployerButton.setOnAction(Action -> {
            
            try {
                ((Node)Action.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "NewUser.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                NewUserController nsControl = (NewUserController)loader.getController();
                nsControl.getEmployer(this.employer);
                startStage(root, (int)root.getWidth(), (int)root.getHeight());
            } catch (IOException ex) {
                exceptionLayout(ex);
            }

        });

        updateEmployer.setOnAction(Action -> {
            
            try {
                User emp = User.getUserByName(usersCB.getSelectionModel().getSelectedItem());
                ((Node)Action.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "UpdateUser.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                UpdateUserController ueControl = (UpdateUserController)loader.getController();
                ueControl.getInfo(employer, emp);
                ueControl.fillFields(emp);
                startStage(root, (int)root.getWidth(), (int)root.getHeight());

            } catch (IOException | SQLException ex) {
                exceptionLayout(ex);
            }
        });

        deleteEmployer.setOnAction(Action -> {

            try {
                User emp = User.getUserByName(usersCB.getSelectionModel().getSelectedItem());
                confirmDialog(emp, "employer", bundle.getString("delete") + " " + emp.getFullname(), bundle.getString("are_u_sure"), INFO_SMALL);
            } catch (SQLException ex) {
                exceptionLayout(ex);
            }

        });
        
        exBtn.setOnAction(Action -> {
            

            try {
                ((Node)Action.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "RemovedUsers.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                RemovedUsersController ueControl = (RemovedUsersController)loader.getController();
                ueControl.getInfo(this.employer);                          
                startStage(root, (int)root.getWidth(), (int)root.getHeight());
            } catch (IOException ex) {
                exceptionLayout(ex);
            }
        
        });

        changePass.setOnAction(Action -> {

            try {
                User emp = User.getUserByName(usersCB.getSelectionModel().getSelectedItem());
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "ChangePassword.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                ChangePasswordController erControl = (ChangePasswordController)loader.getController();
                erControl.getEmployer(emp);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.getIcons().add(new Image(Common.class.getResourceAsStream(APP_ICON)));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

            } catch (IOException | SQLException ex) {
                exceptionLayout(ex);
            }
        });
        
        // Clients TAB
        
        getClientsNames();
        clientsCB.setItems(clientsList);
        if(clientsList.size() > 0){
            clientsCB.getSelectionModel().select(0);
            showClient(clientsList.get(0));
        }
        else{
            clientTabBox.setVisible(false);
            noUsersBox.setVisible(true);
        }
        clientsCB.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showClient(clientsCB.getSelectionModel().getSelectedItem());
            }
        });
        
        deleteClientBtn.setOnAction(Action -> {

            Client client = clientsCB.getSelectionModel().getSelectedItem();
            confirmDialog(client, "client", bundle.getString("delete") + " " + client.getFullname(), bundle.getString("are_u_sure"), INFO_SMALL);

        });
        
        addClientBtn.setOnAction(Action -> {
            
            try {
                ((Node)Action.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "NewClient.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                NewClientController ncControl = (NewClientController)loader.getController();
                ncControl.getEmployer(employer);
                startStage(root, (int)root.getWidth(), (int)root.getHeight());
            } catch (IOException ex) {
                exceptionLayout(ex);
            }

        });
        
        updateClientBtn.setOnAction(Action -> {
            
            try {
                Client client = clientsCB.getValue();
                ((Node)Action.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "UpdateClient.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                UpdateClientController ucControl = (UpdateClientController)loader.getController();
                ucControl.setInfo(employer, client);
                ucControl.fillFields(client);
                startStage(root, (int)root.getWidth(), (int)root.getHeight());

            } catch (IOException ex){
                exceptionLayout(ex);
            }
        });


        // Buys TAB
        
        
        getAllBuys(buyDateField.getEditor().getText());
        getBuyStats(buyDateField.getEditor().getText());
                        
        newBuyBtn.setOnAction(Action -> {
            
            if(!data.isEmpty()){
                try {
                    ((Node)Action.getSource()).getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "NewBuy.fxml"), bundle);
                    Pane root = (Pane)loader.load();
                    NewBuyController npControl = (NewBuyController)loader.getController();
                    npControl.getEmployer(this.employer);
                    startStage(root, (int)root.getWidth(), (int)root.getHeight());

                } catch (IOException ex) {
                    exceptionLayout(ex);
                }
            }
            else{
                customDialog(bundle.getString("no_products_found"), bundle.getString("no_products_found_msg"), ERROR_SMALL, true);
            }

        });
        
        buyStatBtn.setOnAction(Action -> {

            try { 
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "BuyStats.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                Common.startStage(root, (int)root.getWidth(), (int)root.getHeight());

            } catch (IOException ex) {
                exceptionLayout(ex);
            }

        });        

        searchBuy.textProperty().addListener((obs, oldText, newText) -> {
            search("buys");
        });

        buyDateField.setOnAction(Action -> {
            buysTable.getItems().clear();
            getAllBuys(buyDateField.getEditor().getText());
            getBuyStats(buyDateField.getEditor().getText());
            buysTable.setItems(buysList);
        });        
        
  
        printBuy.disableProperty().bind(Bindings.size(buysTable.getSelectionModel().getSelectedIndices()).isEqualTo(0));
        
        printProducts.disableProperty().bind(Bindings.size(productVB.getChildren()).isEqualTo(0));
        
        printEmployers.disableProperty().bind(Bindings.size(usersCB.getItems()).isEqualTo(0));

        printSells.disableProperty().bind(Bindings.size(sellsTable.getItems()).isEqualTo(0));
        
        newBillBtn.disableProperty().bind(Bindings.size(sellsTable.getSelectionModel().getSelectedItems()).isEqualTo(0));
        
        printBuys.disableProperty().bind(Bindings.size(buysTable.getItems()).isEqualTo(0));
        
        updateProduct.disableProperty().bind(Bindings.size(productVB.getChildren()).isEqualTo(0));
        
        noProductsBox.visibleProperty().bind(Bindings.size(productVB.getChildren()).isEqualTo(0));
        
        
                Tooltip.install(
                billPane, 
                new Tooltip(bundle.getString("disabled_sell_bill_msg")));
        
        Tooltip.install(
                billPane1, 
                new Tooltip(bundle.getString("disabled_buy_bill_msg")));        
        

        printSells.setOnAction(Action -> {
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
      
                try {
                    jr.params.put("sell_date", sellDateField.getEditor().getText());
                    loadReport("sellsReport");
                } catch (SQLException | JRException ex) {
                    exceptionLayout(ex);
                }
            
            });
            
            th.start();

        
        });
        
        printBuys.setOnAction(Action -> {
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
            
                try {
                    jr.params.put("buyDate", buyDateField.getEditor().getText());
                    loadReport("buys");
                } catch (SQLException | JRException ex) {
                    exceptionLayout(ex);
                }
            
            });
            
            th.start();            
        
        
        });        
        
        printProducts.setOnAction(Action -> {
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
            
                try {
                    loadReport("productsReport");
                } catch (SQLException | JRException ex) {
                    exceptionLayout(ex);
                }

            });
            
            th.start();
        
        
        });
        
        newBillBtn.setOnAction(Action -> {
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
            
                try {
                    String selectedSells = "";
                    
                    selectedSells = sellsTable.getSelectionModel().getSelectedItems().stream().map((sell) -> sell.getSellID() + ",").reduce(selectedSells, String::concat);
                    selectedSells = selectedSells.substring(0, selectedSells.length() - 1);
                    jr.params.put("selectedSells", selectedSells);
                    loadReport("sellBill");
                } catch (SQLException | JRException ex) {
                    exceptionLayout(ex);
                }
            
            });
            
            th.start();
                    
        
        });
        
        printEmployers.setOnAction(Action -> {
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
            
                try {
                    loadReport("employersList");
                } catch (SQLException | JRException ex) {
                    exceptionLayout(ex);
                }
            
            });
            
            th.start();            
        
        });
        
        printBuy.setOnAction(Action -> {
            
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
                
                try {
                    String selectedBuys = "";
                    
                    selectedBuys = buysTable.getSelectionModel().getSelectedItems().stream().map((buy) -> buy.getBuyID() + ",").reduce(selectedBuys, String::concat);
                    selectedBuys = selectedBuys.substring(0, selectedBuys.length() - 1);
                    jr.params.put("selectedBuys", selectedBuys);
                    loadReport("buy");
                } catch (SQLException | JRException ex) {
                    exceptionLayout(ex);
                }
            
            });
            
            th.start();
                      
        });
        
        settingsBtn.setOnMouseClicked((event) -> {
            try {
                ((Node)event.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "Settings.fxml"), bundle);
                AnchorPane root = (AnchorPane)loader.load();
                SettingsController sControl = (SettingsController)loader.getController();
                sControl.employer = employer;
                Common.startStage(root, (int)root.getWidth(), (int)root.getHeight());
            } catch (IOException ex) {
                exceptionLayout(ex);
            }
        });
                
        updateMenuButtons();
       
    }
    
    public void loadReport(String reportName) throws SQLException, JRException{
        jr.ShowReport(reportName,"");
        dialog.close();
        stackPane.setVisible(false);
    }

    public void selectMenu(Event event) throws IOException{
        
        
        if(event.getTarget() == btn_products){
            if(!products.isVisible()){
                animateNode(new Tada(btn_products));
                products.setVisible(true);
                animateNode(new FlipInY(products));
                btn_products.setEffect(new Glow());
                sells.setVisible(false);
                btn_sells.setEffect(null);
                employers.setVisible(false);
                btn_employers.setEffect(null);
                buys.setVisible(false);
                btn_buys.setEffect(null);
                clients.setVisible(false);
                btn_clients.setEffect(null);
                btn_clients.setStyle("-fx-background-color: transparent");
                btn_products.setStyle("-fx-background-color: tomato");
                btn_employers.setStyle("-fx-background-color: transparent");
                btn_buys.setStyle("-fx-background-color: transparent");
                btn_sells.setStyle("-fx-background-color: transparent");
            }            
            
        }
        else if(event.getTarget() == btn_sells){
            if(!sells.isVisible()){
                animateNode(new Tada(btn_sells));
                products.setVisible(false);
                btn_products.setEffect(null);
                sells.setVisible(true);
                animateNode(new FlipInY(sells));
                btn_sells.setEffect(new Glow());
                employers.setVisible(false);
                btn_employers.setEffect(null);
                buys.setVisible(false);
                btn_buys.setEffect(null);
                clients.setVisible(false);
                btn_clients.setEffect(null);
                btn_clients.setStyle("-fx-background-color: transparent");
                btn_products.setStyle("-fx-background-color: transparent");
                btn_employers.setStyle("-fx-background-color: transparent");
                btn_buys.setStyle("-fx-background-color: transparent");
                btn_sells.setStyle("-fx-background-color: tomato");
            }
        }
        else if(event.getTarget() == btn_employers){
            if(!employers.isVisible()){
                animateNode(new Tada(btn_employers));
                products.setVisible(false);
                btn_products.setEffect(null);
                sells.setVisible(false);
                btn_sells.setEffect(null);
                employers.setVisible(true);
                animateNode(new FlipInY(employers));
                btn_employers.setEffect(new Glow());
                buys.setVisible(false);
                btn_buys.setEffect(null);
                clients.setVisible(false);
                btn_clients.setEffect(null);
                btn_clients.setStyle("-fx-background-color: transparent");
                btn_products.setStyle("-fx-background-color: transparent");
                btn_employers.setStyle("-fx-background-color: tomato");
                btn_buys.setStyle("-fx-background-color: transparent");
                btn_sells.setStyle("-fx-background-color: transparent"); 
            }
        }
        else if(event.getTarget() == btn_buys){
            if(!buys.isVisible()){
                animateNode(new Tada(btn_buys));
                products.setVisible(false);
                btn_products.setEffect(null);
                sells.setVisible(false);
                btn_sells.setEffect(null);
                buys.setVisible(true);
                animateNode(new FlipInY(buys));
                btn_buys.setEffect(new Glow());
                employers.setVisible(false);
                btn_employers.setEffect(null);
                clients.setVisible(false);
                btn_clients.setEffect(null);
                btn_clients.setStyle("-fx-background-color: transparent");
                btn_products.setStyle("-fx-background-color: transparent");
                btn_employers.setStyle("-fx-background-color: transparent");
                btn_buys.setStyle("-fx-background-color: tomato");
                btn_sells.setStyle("-fx-background-color: transparent");
            }
        }
        else if(event.getTarget() == btn_clients){
            if(!clients.isVisible()){
                animateNode(new Tada(btn_clients));
                products.setVisible(false);
                btn_products.setEffect(null);
                sells.setVisible(false);
                btn_sells.setEffect(null);
                buys.setVisible(false);
                btn_buys.setEffect(null);
                employers.setVisible(false);
                btn_employers.setEffect(null);
                clients.setVisible(true);
                btn_clients.setEffect(new Glow());
                animateNode(new FlipInY(clients));
                btn_products.setStyle("-fx-background-color: transparent");
                btn_employers.setStyle("-fx-background-color: transparent");
                btn_buys.setStyle("-fx-background-color: transparent");
                btn_sells.setStyle("-fx-background-color: transparent");
                btn_clients.setStyle("-fx-background-color: tomato");
            }
        }        
        else if(event.getTarget() == btn_close){
            
            ((Node)event.getSource()).getScene().getWindow().hide();
            AnchorPane root = FXMLLoader.load(getClass().getResource(FXML_PATH + "Login.fxml"), bundle);
            startStage(root, 450, 350);
                        
        }
        
        updateMenuButtons();
        
    }
    
    public void updateMenuButtons(){
        
        if(products.isVisible()){
             btn_products.setGraphic(new ImageView(new Image(
                ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/product_large_filled_grey.png"),
                32, 32, true, true))); 
        }
        else{
             btn_products.setGraphic(new ImageView(new Image(
                ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/product_large_outlined_grey.png"),
                32, 32, true, true)));   
        }
        
        if(employers.isVisible()){
             btn_employers.setGraphic(new ImageView(new Image(
                ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/user_large_filled_grey.png"),
                32, 32, true, true))); 
        }
        else{
             btn_employers.setGraphic(new ImageView(new Image(
                ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/user_large_outlined_grey.png"),
                32, 32, true, true))); 
        }
        
        if(buys.isVisible()){
             btn_buys.setGraphic(new ImageView(new Image(
                ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/buy_large_filled_grey.png"),
                32, 32, true, true))); 
        }
        else{
             btn_buys.setGraphic(new ImageView(new Image(
                ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/buy_large_outlined_grey.png"),
                32, 32, true, true)));  
        }
        
        if(sells.isVisible()){
             btn_sells.setGraphic(new ImageView(new Image(
                ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/sell_large_filled_grey.png"),
                32, 32, true, true))); 
        }
        else{
             btn_sells.setGraphic(new ImageView(new Image(
                ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/sell_large_outlined_grey.png"),
                32, 32, true, true))); 
        }
        
    }
    
    
    public void returnMenu(String source){
        
        switch (source) {
            case "products":
                products.setVisible(true);
                sells.setVisible(false);
                employers.setVisible(false);
                buys.setVisible(false);
                clients.setVisible(false);
                break;
            case "sells":
                products.setVisible(false);
                sells.setVisible(true);
                employers.setVisible(false);
                buys.setVisible(false);
                clients.setVisible(false);
                break;
            case "employers":
                products.setVisible(false);
                sells.setVisible(false);
                employers.setVisible(true);
                buys.setVisible(false);
                clients.setVisible(false);
                break;
            case "buys":
                products.setVisible(false);
                sells.setVisible(false);
                employers.setVisible(false);
                buys.setVisible(true);
                clients.setVisible(false);
                break;
            case "clients":
                products.setVisible(false);
                sells.setVisible(false);
                employers.setVisible(false);
                buys.setVisible(false);
                clients.setVisible(true);
                break;
            default:
                break;
        }
        
        
    }
    
    public void handleMenuButtons(){
        
        AnimationFX animProduct, animUsers, animBuys, animeSells;
        
        animProduct = new Shake(btn_products);
        animUsers = new Shake(btn_employers);
        animBuys = new Shake(btn_buys);
        animeSells = new Shake(btn_sells);
        
        btn_products.setOnMouseEntered(value -> {
            if(!products.isVisible())
                animProduct.play();
        });
        btn_employers.setOnMouseEntered(value -> {
            if(!employers.isVisible())
                animUsers.play();
        });
        btn_buys.setOnMouseEntered(value -> {
            if(!buys.isVisible())
                animBuys.play();
        });
        btn_sells.setOnMouseEntered(value -> {
            if(!sells.isVisible())
                animeSells.play();
        });

        btn_products.setOnMouseExited(value -> {
            animProduct.stop();
        });
        btn_employers.setOnMouseExited(value -> {
            animUsers.stop();
        });
        btn_buys.setOnMouseExited(value -> {
            animBuys.stop();
        });
        btn_sells.setOnMouseExited(value -> {
            animeSells.stop();
        });        
        
    }

    private void initPaymentsTable() {
        
        try {
            paymentsList = Payment.getClientPayments(clientsCB.getValue());
            payDateCol.setCellValueFactory(new PropertyValueFactory<>("payDate"));
            paySumCol.setCellValueFactory(new PropertyValueFactory<>("paySum"));
            paymentsTable.setItems(paymentsList);
        } catch (SQLException ex) {
            exceptionLayout(ex);
        }
        
    }

}