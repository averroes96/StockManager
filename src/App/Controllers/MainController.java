package App.Controllers;

import Data.Buy;
import Data.Employer;
import Data.Product;
import Data.Sell;
import Include.Common;
import static Include.Common.dateFormatter;
import static Include.Common.getAllFrom;
import static Include.Common.getConnection;
import static Include.Common.getUser;
import static Include.Common.initLayout;
import static Include.Common.setDraggable;
import Include.Init;
import static Include.Init.IMAGES_PATH;
import static Include.Init.OKAY;
import static Include.Init.UNKNOWN_ERROR;
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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author med
 */
public class MainController implements Initializable,Init {
    
    
    @FXML private Label btn_close;
    @FXML private AnchorPane products, sells, employers,buys;
    @FXML private TableView<Product> productsTable ;
    @FXML private TableView<Sell> sellsTable ;
    @FXML private TableView<Buy> buysTable ;
    @FXML private TableColumn<Buy, Integer> buyIDCol,buyQteCol,buyPriceCol,buyTotalCol ;
    @FXML private TableColumn<Buy, String> buyProdCol,buyUserCol,buyDateCol ;
    @FXML private TableColumn<Product, String> prodName,addDate,lastChange ;
    @FXML private TableColumn<Product, Integer> sellProd,prodQuantity,id,nbrSellsCol,nbrBuysCol ; 
    @FXML private TableColumn<Sell, Integer> sellID,sellQuantity,sellTotalCol,sellPrice ;
    @FXML private TableColumn<Sell, String> sellRef,seller,sellDateCol ;
    @FXML private TableColumn sellActions,sellActions2,buyAction1,buyAction2 ;   
    @FXML public ChoiceBox<String> usersCB ;
    @FXML private TextField searchBuy ;
    @FXML public DatePicker buyDateField;
    @FXML private Label productImg,fullnameLabel,phoneLabel,emptyQte,idField,revSum,revTotal,revQte,buyDayTotal,buyDayQte,buyDaySum,userStatus,lastLogged,userImage; 
    @FXML public Button seeRecords,day,week,month,total,btn_products, btn_sells, btn_employers,btn_buys;
    @FXML private ImageView prodManager,userManager,sellManager,buyManager;
    @FXML public Pane billPane,billPane1;
    @FXML private JFXTextField searchField,refField,priceField2,quantityField,sellSearch ;
    @FXML private JFXDatePicker dateField,sellDateField;
    @FXML private JFXButton updateImage,viewHistory,addProd,printProducts,removedProduct,productStats,deleteProduct,updateProduct,
                            updateEmployer,deleteEmployer,changePass,printSells,sellStats,newBillBtn,newSellButton,printEmployers,
                            exBtn,addEmployerButton,printBuy,printBuys,newBuyBtn,buyStatBtn;
    
    @FXML private StackPane stackPane;
    @FXML private JFXDialog dialog;
    @FXML private VBox infoContainer;
    @FXML private HBox productHB;
    
    ObservableList<Product> data = FXCollections.observableArrayList();
    ObservableList<Sell> sellsList = FXCollections.observableArrayList(); 
    ObservableList<String> employersList = FXCollections.observableArrayList();
    ObservableList<Buy> buysList = FXCollections.observableArrayList(); 
    
    
    final String dateFormat = "yyyy-MM-dd";

    File selectedFile = null;
    
    private Employer thisEmployer = new Employer();
    
    JasperReporter jr = new JasperReporter();
    
    private double xOffset = 0;
    private double yOffset = 0;
    
    public void confirmDialog(Object object, String type, String title, String body, String icon){
            
        JFXDialogLayout layout = new JFXDialogLayout();
        initLayout(layout, title, body, icon);
            
        stackPane.setVisible(true);
        JFXButton yesBtn = new JFXButton("نعم");
        yesBtn.setDefaultButton(true);
        yesBtn.setOnAction(Action -> {
            dialog.close();
            stackPane.setVisible(false);
            yesBtn.setDefaultButton(false);
            if(null != type)
                switch (type) {
                case "employer":
                    deleteEmployer((Employer) object);
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
                default:
                    break;
            }
        });
        JFXButton noBtn = new JFXButton("لا");
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
        JFXButton btn = new JFXButton(OKAY);
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
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);
            
            loadDialog(layout, true);
    }

    private void onJasperReportLoading(){
        
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, PLEASE_WAIT, REPORT_WAIT_MESSAGE, WAIT_SMALL);
            
            loadDialog(layout, false);
            
            jr = new JasperReporter();
    }

    public void getEmployer(Employer employer) {
        
        thisEmployer = employer ;
        
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
            usersCB.getSelectionModel().select(thisEmployer.getUsername());
            showEmployer(thisEmployer.getUsername());
        }

    }        
    

    public void fillTheTable()
    {

        ResultSet rs;
        

        try {
            rs = getAllFrom("*","product","","WHERE on_hold = 0","ORDER BY add_date DESC");

            while (rs.next()) {
                Product product = new Product();
                product.setProdID(rs.getInt("prod_id"));
                product.setName(rs.getString("name"));
                product.setSellPrice(rs.getInt("sell_price"));
                product.setAddDate(rs.getDate("add_date").toString());
                product.setProdQuantity(rs.getInt("prod_quantity"));
                product.setImageURL(rs.getString("image_url"));
                product.setNbrBuys(rs.getInt("nbrBuys"));
                product.setNbrSells(rs.getInt("nbrSells"));
                
                if(rs.getTimestamp("last_change") != null){
                    product.setLastChange(rs.getTimestamp("last_change").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy h.mm a")));
                }
                else
                    product.setLastChange("/");
                data.add(product);
            }

        }
        catch (SQLException e) {
            
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);
            
            loadDialog(layout, true);            
            
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
               productsTable.setItems(data);
           }

           else {
               ObservableList<Product> filteredData = FXCollections.observableArrayList();
               data.stream().filter((product) -> (product.getName().toLowerCase().contains(keyword.toLowerCase()))).forEachOrdered((product) -> {
                   filteredData.add(product);
                });
               productsTable.setItems(filteredData);
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

        if(productsTable.getSelectionModel().getSelectedItem() == null)
        {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, INFO_MESSAGE, INFO_MSG, INFO_SMALL);                
                
            loadDialog(layout, true);               
            return;
        }

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Select a .JPG .PNG .GIF image", "*.jpg", "*.png", "*.gif")
        );

       
        selectedFile = fileChooser.showOpenDialog(null);

       
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

                Product selectedProduct = (Product) productsTable.getSelectionModel().getSelectedItem();

                Common.deleteImage(selectedProduct.getImageURL());

                selectedProduct.setImageURL(createImagePath);

                productImg.setText("");
                productImg.setGraphic(new ImageView(new Image(
                    selectedFile.toURI().toString(), 150, 150, true, true)));
                
                productsTable.refresh();
            }
            catch (NumberFormatException | SQLException ex) {
                JFXDialogLayout layout = new JFXDialogLayout();
                initLayout(layout, UNKNOWN_ERROR, LOAD_IMAGE_ERROR, ERROR_SMALL);                

                loadDialog(layout, true);                    
            }
        }

    }    
    
    private boolean checkInputs()
    {
        if (refField.getText().trim().equals("") && priceField2.getText().trim().equals("") && quantityField.getText().trim().equals("")) {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, MISSING_FIELDS, "إسم وأسعار المنتج غير مملوءة", ERROR_SMALL);                

            loadDialog(layout, true);     
        }
        else if (refField.getText().trim().equals("")) {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, MISSING_FIELDS, "قم بإدخال إسم المنتج من فضلك", ERROR_SMALL);                

            loadDialog(layout, true);               
            return false;
        }
        else if (priceField2.getText().trim().equals("")) {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, MISSING_FIELDS, "من فضلك قم بإدخال أسعار المنتج", ERROR_SMALL);                

            loadDialog(layout, true);                
            return false;
        }
        else if (quantityField.getText().trim().equals("")) {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, MISSING_FIELDS, "من فضلك قم بإدخال أسعار المنتج", ERROR_SMALL);                

            loadDialog(layout, true);   
            return false;
        }        
        

        try {
            Integer.parseInt(priceField2.getText());
            if(priceField2.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(priceField2.getText()) > 0){
                if(quantityField.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(priceField2.getText()) >= 0){
                    return true;
                }
                else{
                    JFXDialogLayout layout = new JFXDialogLayout();
                    initLayout(layout, INVALID_QTE, INVALID_QTE_MSG, ERROR_SMALL);                

                    loadDialog(layout, true);                     
                    return false;
                }
            }
            else{
                    JFXDialogLayout layout = new JFXDialogLayout();
                    initLayout(layout, INVALID_PRICE, "من فضلك قم بإدخال أسعار صالحة", ERROR_SMALL);                

                    loadDialog(layout, true);                     
                    return false;
            }
        }
        catch (NumberFormatException e) {
                    
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, INVALID_PRICE, "من فضلك قم بإدخال أسعار صالحة", ERROR_SMALL);                

            loadDialog(layout, true);             
            return false;
        }
    }    
    
    private void updateProduct()
    {

        if(productsTable.getSelectionModel().getSelectedItem() == null)
        {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, INFO_MESSAGE, INFO_MSG1, INFO_SMALL);                

            loadDialog(layout, true);               
            return;
        }
        
        if (checkInputs() == false) {
            return;
        }

        Product selectedProduct = (Product) productsTable.getSelectionModel().getSelectedItem();

        try {
            String sqlDate = "";
            String javaDate = "";
            try (Connection con = getConnection()) {
                String query;
                
                query = "UPDATE product SET name = ?, sell_price = ?, add_date = ?,prod_quantity = ?, last_change = ? WHERE prod_id = ?";
                
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, refField.getText());
                ps.setInt(2, Integer.valueOf(priceField2.getText()));
                ps.setInt(4, Integer.valueOf(quantityField.getText()));
                ps.setDate(3, Date.valueOf(dateField.getEditor().getText()));
                
                java.util.Date date = new java.util.Date();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.text.SimpleDateFormat ndf = new java.text.SimpleDateFormat("yyyy/MM/dd h.mm a");
                sqlDate = sdf.format(date);
                javaDate = ndf.format(date);
                ps.setString(5, sqlDate);            
                ps.setInt(6, Integer.parseInt(idField.getText()));
                ps.executeUpdate();
                
                if(!selectedProduct.getName().equals(refField.getText()) || !selectedProduct.getAddDate().equals(dateField.getEditor().getText()) || selectedProduct.getProdQuantity() != Integer.valueOf(quantityField.getText()) || selectedProduct.getSellPrice() != Integer.valueOf(priceField2.getText())){
                query = "INSERT INTO product_history(prod_id, user_id, change_date, new_name, new_date, new_price, new_qte, old_name, old_date, old_price, old_qte) VALUES(?,?,?,?,?,?,?,?,?,?,?)" ;
                
                ps = con.prepareStatement(query);
                ps.setInt(1, Integer.parseInt(idField.getText()));
                ps.setInt(2, this.thisEmployer.getUserID());
                ps.setString(3, sqlDate);                
                ps.setString(4, refField.getText());
                ps.setDate(5, Date.valueOf(dateField.getEditor().getText()));
                ps.setInt(6, Integer.valueOf(priceField2.getText()));
                ps.setInt(7, Integer.valueOf(quantityField.getText()));
                ps.setString(8, selectedProduct.getName());
                ps.setString(9, selectedProduct.getAddDate());
                ps.setInt(10,selectedProduct.getSellPrice());
                ps.setInt(11,selectedProduct.getProdQuantity());
                
                ps.executeUpdate();
                
                }
                
                con.close();
            }

            selectedProduct.setName(refField.getText());
            selectedProduct.setSellPrice(Integer.valueOf(priceField2.getText()));
            selectedProduct.setProdQuantity(Integer.valueOf(quantityField.getText()));
            selectedProduct.setAddDate(dateField.getEditor().getText());
            selectedProduct.setLastChange(javaDate);
            
            productsTable.refresh();

            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, PRODUCT_UPDATED, PRODUCT_UPDATED_MSG, INFO_SMALL);                

            loadDialog(layout, true);
            
        
        }
        catch (NumberFormatException | SQLException e) {
            
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);                

            loadDialog(layout, true);
            
        }
    }   
    
    private void showProduct(int index)
    {
        
        new FadeIn(productHB).play();
        refField.setText(data.get(index).getName());
        quantityField.setText(String.valueOf(data.get(index).getProdQuantity()));
        dateField.getEditor().setText(data.get(index).getAddDate());
        priceField2.setText(String.valueOf(data.get(index).getSellPrice()));
        idField.setText(String.valueOf(data.get(index).getProdID()));        

        if (data.get(index).getImageURL() == null) {
            productImg.setText("");
            productImg.setGraphic(new ImageView(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "product_default.png"),
                    60, 60, true, true)));
        }
        else {
            productImg.setText("");
            productImg.setGraphic(new ImageView(new Image(
                    new File(data.get(index).getImageURL()).toURI().toString(),
                    220, 170, true, true)));
        }        
        
        if(data.get(index).getProdQuantity() < 10){
            emptyQte.setVisible(true);
        }
        else
            emptyQte.setVisible(false);    
        
    }
    
    private void showEmployer(String username)
    {
        
        new ZoomOut(infoContainer).play();
        new ZoomIn(infoContainer).play();
        Employer choosen = getUser(username);
        if(choosen != null){
        fullnameLabel.setText(choosen.getFullname());
        if(!choosen.getPhone().trim().equals("")) 
            phoneLabel.setText(choosen.getPhone()) ;
        else 
            phoneLabel.setText("لا يوجد رقم هاتف");
        
        if (choosen.getImage().trim().equals("") ) {
            userImage.setText("");
            userImage.setGraphic(new ImageView(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "large/user.png"),
                    64, 64, true, true)));
        }
        else {
            userImage.setText("");
            userImage.setGraphic(new ImageView(new Image(
                    new File(choosen.getImage()).toURI().toString(),
                    220, 200, true, true)));
        }
        
        if(choosen.getProdPrivs() == 1) prodManager.setImage(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "granted.png"),
                    25, 25, true, true));
        else
            prodManager.setImage(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "notgranted.png"),
                    25, 25, true, true));    
        
        if(choosen.getBuyPrivs() == 1) buyManager.setImage(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "granted.png"),
                    25, 25, true, true)); 
        else
            buyManager.setImage(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "notgranted.png"),
                    25, 25, true, true));           

    
        if(choosen.getSellPrivs() == 1) sellManager.setImage(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "granted.png"),
                    25, 25, true, true)); 
        else
            sellManager.setImage(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "notgranted.png"),
                    25, 25, true, true));           


        if(choosen.getUserPrivs() == 1) userManager.setImage(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "granted.png"),
                    25, 25, true, true)); 
        else
            userManager.setImage(new Image(
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "notgranted.png"),
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
            lastLogged.setText("لم يتم تسجيل الدخول بعد");
  
    }    
       

    private void showNextProduct()
    {
        if (productsTable.getSelectionModel().getSelectedIndex() < data.size() - 1) {
            int currentSelectedRow = productsTable.getSelectionModel().getSelectedIndex() + 1;
            productsTable.getSelectionModel().select(currentSelectedRow);
            showProduct(currentSelectedRow);
        }
    }    

    private void deleteProduct()
    {

        if(productsTable.getSelectionModel().getSelectedItem() == null)
        {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, INFO_MESSAGE, INFO_MSG, INFO_SMALL);                

            loadDialog(layout, true);             
            return;
        }

        Product selectedProduct = (Product) productsTable.getSelectionModel().getSelectedItem();
        
        try {

            try (Connection con = getConnection()) {
                String query = "UPDATE product SET on_hold = 1 WHERE prod_id = ?";
                
                PreparedStatement ps = con.prepareStatement(query);
                
                ps.setInt(1, selectedProduct.getProdID());
                
                ps.executeUpdate();
            }
            
            data.remove(selectedProduct);
            
            productsTable.refresh();
            
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, PRODUCT_DELETED, PRODUCT_DELETED_MSG, ERROR_SMALL);                

            loadDialog(layout, true); 
            
            if(data.size() > 0) {
                showNextProduct();
            }
            else
            {
                idField.setText("");
                refField.setText("");
                priceField2.setText("");
                quantityField.setText("");
                dateField.getEditor().setText("");
                productImg.setText(NO_IMAGE_FOUND);
                productImg.setGraphic(null);
            }
            
        }
        catch (SQLException e) {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);                

            loadDialog(layout, true); 
        }
        
        
    }    
    
    private void getAllSells(String selectedDate)
    {
        Connection con = getConnection();
        String query = "SELECT * FROM sell INNER JOIN product ON sell.prod_id = product.prod_id INNER JOIN user ON sell.user_id = user.user_id WHERE date(sell_date) = ? ORDER BY time(sell_date) ASC";

        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(query);
            st.setString(1,selectedDate);
            rs = st.executeQuery();

            while (rs.next()) {
                Sell sell = new Sell();
                sell.setSellID(rs.getInt("sell_id"));
                sell.setSellPrice(rs.getInt("sell.sell_price_unit"));
                sell.setTotalPrice(rs.getInt("sell.sell_price"));
                sell.setSellDate(rs.getTime("sell_date").toString());
                sell.setSellQuantity(rs.getInt("sell_quantity"));
                sell.setSeller(rs.getString("username"));
                
                Product product = new Product();
                product.setProdID(rs.getInt("prod_id"));
                product.setName(rs.getString("name"));               
                product.setSellPrice(rs.getInt("product.sell_price"));
                product.setProdQuantity(rs.getInt("prod_quantity"));
                product.setAddDate(rs.getDate("add_date").toString());
                product.setNbrBuys(rs.getInt("nbrBuys"));
                product.setNbrSells(rs.getInt("nbrSells"));
                product.setImageURL(rs.getString("image_url")); 
                
                sell.setProduct(product);
                sell.setSellName(rs.getString("name")); 

                sellsList.add(sell);
            }

            con.close();
        }
        catch (SQLException e) {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);
            
            loadDialog(layout, true);
        }
    }
    
    private void getAllBuys(String selectedDate)
    {
        Connection con = getConnection();
        String query = "SELECT * FROM buy INNER JOIN product ON buy.prod_id = product.prod_id INNER JOIN user ON buy.user_id = user.user_id WHERE date(buy_date) = ? ORDER BY time(buy_date) ASC";

        PreparedStatement st;
        ResultSet rs;

        try {
            st = con.prepareStatement(query);
            st.setString(1,selectedDate);
            rs = st.executeQuery();

            while (rs.next()) {
                Buy buy = new Buy();
                buy.setBuyID(rs.getInt("buy_id"));
                buy.setBuyPrice(rs.getInt("buy.buy_unit_price"));
                buy.setBuyTotalPrice(rs.getInt("buy.buy_price"));
                buy.setBuyDate(rs.getTime("buy_date").toString());
                buy.setBuyQte(rs.getInt("buy_qte"));
                buy.setProduct(rs.getString("name"));
                buy.setUser(rs.getString("username"));  

                buysList.add(buy);
            }

            con.close();
        }
        catch (SQLException e) {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);
            
            loadDialog(layout, true);
        }
    }    
    
    public void getSellStats(String selectedDate, String type){
        
        new FadeIn(revQte).play();
        new FadeIn(revSum).play();
        new FadeIn(revTotal).play();
        
        new FadeIn(sellsTable).play();
        
        Connection con = getConnection();
        String query = "";
        PreparedStatement st;
        ResultSet rs;        
        if(selectedDate.equals("")){
            
                query = "SELECT count(*), SUM(sell_price), SUM(sell_quantity) FROM sell";
        }
        else{
            query = "SELECT count(*), SUM(sell_price), SUM(sell_quantity) FROM sell WHERE date(sell_date) = ? ";
        }

        try {
            st = con.prepareStatement(query);
            if(!selectedDate.equals("")){
            st.setString(1,selectedDate);
            }
            rs = st.executeQuery();
            
            int priceSum = 0;
            int totals = 0;
            int qtes = 0;

            while (rs.next()) {
                
                priceSum = rs.getInt("SUM(sell_price)");
                totals = rs.getInt("count(*)");
                qtes = rs.getInt("SUM(sell_quantity)");
                
            }
            
            revSum.setText(String.valueOf(priceSum) + " دج");
            revTotal.setText(String.valueOf(totals) + " بيع");
            revQte.setText(String.valueOf(qtes) + " قطعة");
            con.close();
        }
        catch (SQLException e) {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);
            
            loadDialog(layout, true);
        }        
        
    }
    
    public void getBuyStats(String selectedDate, String type){
        
        new FadeIn(buyDayQte).play();
        new FadeIn(buyDaySum).play();
        new FadeIn(buyDayTotal).play();        
        
        new FadeIn(buysTable).play();
        
        Connection con = getConnection();
        String query = "";
        PreparedStatement st;
        ResultSet rs;        
        if(selectedDate.equals("")){
            
            query = "SELECT count(*), SUM(buy_price), SUM(buy_qte) FROM buy";
        }
        else{
            query = "SELECT count(*), SUM(buy_price), SUM(buy_qte) FROM buy WHERE date(buy_date) = ? ";
        }

        try {
            st = con.prepareStatement(query);
            if(!selectedDate.equals("")){
            st.setString(1,selectedDate);
            }
            rs = st.executeQuery();
            
            int priceSum = 0;
            int totals = 0;
            int qtes = 0;

            while (rs.next()) {
                
                priceSum = rs.getInt("SUM(buy_price)");
                totals = rs.getInt("count(*)");
                qtes = rs.getInt("SUM(buy_qte)");
            }
            
            buyDaySum.setText(String.valueOf(priceSum) + " دج");
            buyDayTotal.setText(String.valueOf(totals) + " بيع");
            buyDayQte.setText(String.valueOf(qtes) + " قطعة");
            
            con.close();
        }
        catch (SQLException e) {
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, UNKNOWN_ERROR, e.getMessage(), ERROR_SMALL);
            
            loadDialog(layout, true);
        }        
        
    }     
  
    private void deleteSell(Sell selectedSell)
    {
        
        try {

            selectedSell.delete();
            
            data.clear();
            fillTheTable();
            productsTable.setItems(data);

            sellsList.remove(selectedSell);
            
            sellsTable.refresh();
            
            getSellStats(sellDateField.getEditor().getText(),"");
            
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, SELL_DELETED, SELL_DELETED_MESSAGE, INFO_SMALL);
            
            loadDialog(layout, true);
            
        }
        catch (SQLException e) {
             exceptionLayout(e);
        }
    }
    
    private void deleteBuy(Buy buy) {
                try {
                    
                    buy.delete();
                    
                    data.clear();
                    fillTheTable();
                    productsTable.setItems(data);

                    buysList.remove(buy);

                    buysTable.refresh();

                    getSellStats(buyDateField.getEditor().getText(),"");
                    
                    JFXDialogLayout layout = new JFXDialogLayout();
                    initLayout(layout, BUY_DELETED, BUY_DELETED_MSG, INFO_SMALL);
            
                    loadDialog(layout, true);

                }
                catch (SQLException e) {

                    exceptionLayout(e);
                }            
            }    
    
    private void getAllEmployers()
    {
        ResultSet rs;

        try {
            rs = getAllFrom("username","user","","WHERE active != 0","");

            while (rs.next()) {
                
                String employer = rs.getString("username");
                employersList.add(employer);
            }

        }
        catch (SQLException e) {          
            exceptionLayout(e);
        }
    }  

    private void deleteEmployer(Employer selectedEmployer)
    {

        try {
            
            
            if( Employer.getAdminCount() > 1 || selectedEmployer.getAdmin() != 1){            
                
               selectedEmployer.delete();
            
            if(this.thisEmployer.getUsername().equals(selectedEmployer.getUsername())){
                
                        this.addProd.getScene().getWindow().hide();
                        
                        Stage stage = new Stage();
                        AnchorPane root = FXMLLoader.load(getClass().getResource(FXML_PATH + "Login.fxml"));
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        //stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.setMinHeight(350);
                        stage.setMinWidth(450);
                        stage.show();
                        root.setOnMousePressed((MouseEvent event) -> {
                            xOffset = event.getSceneX();
                            yOffset = event.getSceneY();
                        });
                        root.setOnMouseDragged((MouseEvent event) -> {
                            stage.setX(event.getScreenX() - xOffset);
                            stage.setY(event.getScreenY() - yOffset);
                        });                         
                
            }
            else{
            
            usersCB.getItems().clear();
            getAllEmployers();
            usersCB.setItems(employersList);
            
        if (!employersList.isEmpty()) {
            usersCB.getSelectionModel().select(thisEmployer.getUsername());
            showEmployer(thisEmployer.getUsername());
        }
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, EMPLOYER_DELETED, EMPLOYER_DELETED_MSG, INFO_SMALL);
            
            loadDialog(layout, true);
            
            }
            }
            else{
                JFXDialogLayout layout = new JFXDialogLayout();
                initLayout(layout, LAST_ADMIN, LAST_ADMIN_MSG, ERROR_SMALL);

                loadDialog(layout, true);
                
            }
            
        }
        catch (IOException | SQLException e) {

            exceptionLayout(e);

        }
    }

    @FXML
    public void minimize(MouseEvent event){
        
        ((Stage)((Label)event.getSource()).getScene().getWindow()).setIconified(true);
        
    }
    
    @FXML
    public void close(MouseEvent event){
        
        System.exit(0);
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        
        stackPane.setVisible(false);
        
        fillTheTable();

        dateField.setConverter(dateFormatter());
        sellDateField.setConverter(dateFormatter());
        buyDateField.setConverter(dateFormatter()); 
        sellDateField.getEditor().setText(String.valueOf(LocalDate.now()));
        buyDateField.getEditor().setText(String.valueOf(LocalDate.now())); 
        
        id.setCellValueFactory(new PropertyValueFactory<>("prodID"));
        prodName.setCellValueFactory(new PropertyValueFactory<>("name"));
        prodQuantity.setCellValueFactory(new PropertyValueFactory<>("prodQuantity"));
        sellProd.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        addDate.setCellValueFactory(new PropertyValueFactory<>("AddDate"));
        lastChange.setCellValueFactory(new PropertyValueFactory<>("lastChange"));

        productsTable.setItems(data);
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            search("products");
        });
        
        if (!data.isEmpty()) {
            productsTable.getSelectionModel().select(0);
            showProduct(0);
        }
        
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showProduct(productsTable.getSelectionModel().getSelectedIndex());
            }
        });

        updateImage.setOnAction(Action -> {
            updateImage();
        });
        
        productImg.setOnMouseClicked(value -> {
            updateImage();
        });
        
        updateProduct.setOnAction(Action -> {
            updateProduct();
            //new Shake(updateProduct).play();
        });        
       
        deleteProduct.setOnAction(Action -> {
            confirmDialog(null, "product", DELETE, ARE_U_SURE, INFO_SMALL);
        });
        
        addProd.setOnAction(Action -> {
            try {
                
                ((Node)Action.getSource()).getScene().getWindow().hide();
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "NewProduct.fxml"));
                Pane root = (Pane)loader.load();
                NewProductController npControl = (NewProductController)loader.getController();
                npControl.getEmployer(thisEmployer);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                //stage.initStyle(StageStyle.TRANSPARENT);                
                scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                 
                stage.show();
                setDraggable(root,stage);
                
                
            } catch (IOException ex) {
                exceptionLayout(ex);
            }

        });
        
        removedProduct.setOnAction(Action -> {
            
                        try {            

                            ((Node)Action.getSource()).getScene().getWindow().hide();
                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "RemovedProducts.fxml"));
                            AnchorPane root = (AnchorPane)loader.load();
                            RemovedProductsController rpControl = (RemovedProductsController)loader.getController();
                            rpControl.getInfo(this.thisEmployer);
                            Scene scene = new Scene(root);
                            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                            //stage.initStyle(StageStyle.TRANSPARENT);
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());
                            stage.setScene(scene);                         
                            stage.show();
                            setDraggable(root,stage);                            
                            
                        } catch (IOException ex) {
                            exceptionLayout(ex);
                        }                            
        
        });

        productStats.setOnAction(Action -> {
            
                        try {
                            
                            Product product = productsTable.getSelectionModel().getSelectedItem();
                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "ProductStats.fxml"));
                            AnchorPane root = (AnchorPane)loader.load();
                            ProdStatController psControl = (ProdStatController)loader.getController();
                            psControl.setProduct(product);
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());
                            stage.setScene(scene);
                            stage.setTitle("إحصائيات المنتوج " + product.getName());
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setResizable(false);
                            stage.showAndWait();
                            setDraggable(root,stage);                          
                        } catch (IOException ex) {
                            exceptionLayout(ex);
                        }
        });

        viewHistory.setOnAction(Action ->{
            
                        try {
                            
                            Product product = productsTable.getSelectionModel().getSelectedItem();
                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "ProductHistory.fxml"));
                            AnchorPane root = (AnchorPane)loader.load();
                            ProductHistoryController phControl = (ProductHistoryController)loader.getController();
                            Scene scene = new Scene(root);                           
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());
                            stage.setScene(scene);
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.showAndWait();
                            setDraggable(root,stage);                            
                        } catch (IOException ex) {
                            exceptionLayout(ex);
                        }            
            
        });
        
        
        // SELLS TAB
        
        getAllSells(sellDateField.getEditor().getText());
        getSellStats(sellDateField.getEditor().getText(),"");
        
        sellID.setCellValueFactory(new PropertyValueFactory<>("sellID"));
        sellQuantity.setCellValueFactory(new PropertyValueFactory<>("sellQuantity"));
        sellRef.setCellValueFactory(new PropertyValueFactory<>("sellName"));
        sellPrice.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        seller.setCellValueFactory(new PropertyValueFactory<>("seller"));
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
                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "UpdateSell.fxml"));
                            AnchorPane root = (AnchorPane)loader.load();
                            UpdateSellController usControl = (UpdateSellController)loader.getController();
                            usControl.fillFields(sell);
                            usControl.getData(thisEmployer,sell);
                            Scene scene = new Scene(root);
                            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                            //stage.initStyle(StageStyle.TRANSPARENT);
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());
                            stage.setScene(scene);
                            stage.show();
                            root.setOnMousePressed((MouseEvent event1) -> {
                                xOffset = event1.getSceneX();
                                yOffset = event1.getSceneY();
                            });
                            root.setOnMouseDragged((MouseEvent event1) -> {
                                stage.setX(event1.getScreenX() - xOffset);
                                stage.setY(event1.getScreenY() - yOffset);
                            });
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
            
            final Button delete = new Button("حذف");
            
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    delete.setGraphic(new ImageView(new Image(IMAGES_PATH + "small/trash_small_white.png", 24, 24, false, false)));
                    delete.setOnAction(event -> {
                        Sell sell = getTableView().getItems().get(getIndex());
                        confirmDialog(sell, "sell", DELETE, ARE_U_SURE, INFO_SMALL);
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
        
        newSellButton.setOnAction(Action -> {
            
            if(productsTable.getItems().size() > 0){
            
            try {                
                        ((Node)Action.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "NewSell.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        NewSellController nsControl = (NewSellController)loader.getController();
                        nsControl.getEmployer(thisEmployer);
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        //stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.show();
                        setDraggable(root,stage); 
                        
            } catch (IOException ex) {
                exceptionLayout(ex);
            }
            
            }
            else{
                JFXDialogLayout layout = new JFXDialogLayout();
                initLayout(layout, NO_PRODUCTS_FOUND, NO_PRODUCTS_FOUND_MESSAGE, ERROR_SMALL);                

                loadDialog(layout, true);
            }

        });
        

        sellSearch.textProperty().addListener((obs, oldText, newText) -> {
            search("sells");
        });

        sellDateField.setOnAction(Action -> {
            sellsTable.getItems().clear();
            getAllSells(sellDateField.getEditor().getText());
            getSellStats(sellDateField.getEditor().getText(),"");
            sellsTable.setItems(sellsList);
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
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "NewEmployer.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        NewEmployerController nsControl = (NewEmployerController)loader.getController();
                        nsControl.getEmployer(thisEmployer);
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        //stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.show();
                        setDraggable(root,stage);
                        
            } catch (IOException ex) {
                exceptionLayout(ex);
            }

        });

        updateEmployer.setOnAction(Action -> {
            
                        try {
                            Employer employer = getUser(usersCB.getSelectionModel().getSelectedItem());
                            ((Node)Action.getSource()).getScene().getWindow().hide();
                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "UpdateEmployer.fxml"));
                            AnchorPane root = (AnchorPane)loader.load();
                            UpdateEmployerController ueControl = (UpdateEmployerController)loader.getController();
                            ueControl.getInfo(thisEmployer,employer);
                            ueControl.fillFields(employer);                            
                            Scene scene = new Scene(root);
                            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                            //stage.initStyle(StageStyle.TRANSPARENT);
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());
                            stage.setScene(scene);
                            stage.show();
                            setDraggable(root,stage);                           
                            
                        } catch (IOException ex) {
                            exceptionLayout(ex);
                        }
        });

        deleteEmployer.setOnAction(Action -> {

                Employer employer = getUser(usersCB.getSelectionModel().getSelectedItem());
                confirmDialog(employer, "employer", DELETE + " " + employer.getFullname(), ARE_U_SURE, INFO_SMALL);

        });
        
        exBtn.setOnAction(Action -> {
            
                        try {            

                            ((Node)Action.getSource()).getScene().getWindow().hide();
                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "ExEmployers.fxml"));
                            AnchorPane root = (AnchorPane)loader.load();
                            ExEmployersController ueControl = (ExEmployersController)loader.getController();
                            ueControl.getInfo(this.thisEmployer);
                            Scene scene = new Scene(root);
                            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                            //stage.initStyle(StageStyle.TRANSPARENT);
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());
                            stage.setScene(scene);                         
                            stage.show();
                            setDraggable(root,stage);                          
                            
                        } catch (IOException ex) {
                            exceptionLayout(ex);
                        }                            
        
        });

        changePass.setOnAction(Action -> {
            
                        try {
                            Employer employer = getUser(usersCB.getSelectionModel().getSelectedItem());
                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "ChangePass.fxml"));
                            AnchorPane root = (AnchorPane)loader.load();
                            ChangePassController erControl = (ChangePassController)loader.getController();
                            erControl.getEmployer(employer);
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());
                            stage.setScene(scene);
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setResizable(false);
                            stage.showAndWait();
                             
                        } catch (IOException ex) {
                            exceptionLayout(ex);
                        }
        });


        // Buys TAB
        
        getAllBuys(buyDateField.getEditor().getText());
        getBuyStats(buyDateField.getEditor().getText(),"");
        
        buyIDCol.setCellValueFactory(new PropertyValueFactory<>("buyID"));
        buyTotalCol.setCellValueFactory(new PropertyValueFactory<>("buyTotalPrice"));
        buyQteCol.setCellValueFactory(new PropertyValueFactory<>("buyQte"));
        buyUserCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        buyPriceCol.setCellValueFactory(new PropertyValueFactory<>("buyPrice"));
        buyProdCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        buyDateCol.setCellValueFactory(new PropertyValueFactory<>("buyDate"));
        buyAction1.setCellValueFactory(new PropertyValueFactory<>("buyAction1"));
        nbrBuysCol.setCellValueFactory(new PropertyValueFactory<>("nbrBuys"));
        nbrSellsCol.setCellValueFactory(new PropertyValueFactory<>("nbrSells"));
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
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "UpdateBuy.fxml"));
                        AnchorPane root = (AnchorPane)loader.load();
                        UpdateBuyController ubControl = (UpdateBuyController)loader.getController();
                        ubControl.setRequirements(thisEmployer,buy);
                        ubControl.fillFields(buy);
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        //stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.show();
                        setDraggable(root,stage);
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
                        confirmDialog(buys, "buy", DELETE, ARE_U_SURE, INFO_SMALL);
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
        
        newBuyBtn.setOnAction(Action -> {

                try {
                
                ((Node)Action.getSource()).getScene().getWindow().hide();
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "NewQuantity.fxml"));
                Pane root = (Pane)loader.load();
                NewQuantityController npControl = (NewQuantityController)loader.getController();
                npControl.getEmployer(thisEmployer);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                //stage.initStyle(StageStyle.TRANSPARENT);                               
                stage.show();
                setDraggable(root,stage);
                
                
                } catch (IOException ex) {
                    exceptionLayout(ex);
                }

        });
        
        buyStatBtn.setOnAction(Action -> {

                        try { 
                            Stage stage = new Stage();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + "BuyStats.fxml"));
                            AnchorPane root = (AnchorPane)loader.load();
                            BuyStatsController erControl = (BuyStatsController)loader.getController();
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                            scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());
                            stage.setScene(scene);
                            stage.showAndWait();
                             
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
            getBuyStats(buyDateField.getEditor().getText(),"");
            buysTable.setItems(buysList);
        });        
        
                
        buysTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        sellsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        printBuy.disableProperty().bind(Bindings.size(buysTable.getSelectionModel().getSelectedIndices()).isEqualTo(0));
        
        printProducts.disableProperty().bind(Bindings.size(productsTable.getItems()).isEqualTo(0));
        
        printEmployers.disableProperty().bind(Bindings.size(usersCB.getItems()).isEqualTo(0));

        printSells.disableProperty().bind(Bindings.size(sellsTable.getItems()).isEqualTo(0));
        
        newBillBtn.disableProperty().bind(Bindings.size(sellsTable.getSelectionModel().getSelectedItems()).isEqualTo(0));
        
        printBuys.disableProperty().bind(Bindings.size(buysTable.getItems()).isEqualTo(0));
        
        Tooltip.install(
                billPane, 
                new Tooltip("لإضافة فاتورة جديدة عليك أولا تحديد المبيعات من الجدول أدناه"));
        
        Tooltip.install(
                billPane1, 
                new Tooltip("لإضافة فاتورة جديدة عليك أولا تحديد المشتريات من الجدول أدناه"));        

        sellStats.setOnAction(Action -> {
        
            try {
                Stage stage = new Stage();
                AnchorPane root = FXMLLoader.load(getClass().getResource(FXML_PATH + "SellStats.fxml"));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.showAndWait();
                setDraggable(root,stage);
                
            } catch (IOException ex) {
                exceptionLayout(ex);
                
            }
            
        });

        printSells.setOnAction(Action -> {
            
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
      
            jr.params.put("sell_date", sellDateField.getEditor().getText());
            jr.ShowReport("sellsReport","");
            dialog.close();
            stackPane.setVisible(false);
            
            });
            
            th.start();

        
        });
        
        printBuys.setOnAction(Action -> {
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
            
            jr.params.put("buyDate", buyDateField.getEditor().getText());
            jr.ShowReport("buys","");
            dialog.close();
            stackPane.setVisible(false);
            
            });
            
            th.start();            
        
        
        });        
        
        printProducts.setOnAction(Action -> {
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
            
                jr.ShowReport("productsReport","");
                dialog.close();
                stackPane.setVisible(false);

            });
            
            th.start();
        
        
        });
        newBillBtn.setOnAction(Action -> {
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
            
            String selectedSells = "";
                        
            selectedSells = sellsTable.getSelectionModel().getSelectedItems().stream().map((sell) -> sell.getSellID() + ",").reduce(selectedSells, String::concat);
            selectedSells = selectedSells.substring(0, selectedSells.length() - 1);
            jr.params.put("selectedSells", selectedSells);                        
            jr.ShowReport("sellBill","");   
            dialog.close();
            stackPane.setVisible(false);
            
            });
            
            th.start();
                    
        
        });
        
        printEmployers.setOnAction(Action -> {
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
            
                jr.ShowReport("employersList","");
                dialog.close();
                stackPane.setVisible(false);
            
            });
            
            th.start();            
        
        });
        
        printBuy.setOnAction(Action -> {
            
            
            onJasperReportLoading();
            
            Thread th = new Thread(() -> {
                
            String selectedBuys = "";
            
            selectedBuys = buysTable.getSelectionModel().getSelectedItems().stream().map((buy) -> buy.getBuyID() + ",").reduce(selectedBuys, String::concat);
            selectedBuys = selectedBuys.substring(0, selectedBuys.length() - 1);
            jr.params.put("selectedBuys", selectedBuys);                        
            jr.ShowReport("buy","");                  
            dialog.close();
            stackPane.setVisible(false);
            
            });
            
            th.start();
                      
        });
                
        updateMenuButtons();
       
    }

    public void selectMenu(Event event) throws IOException{
        
        
        if(event.getTarget() == btn_products){
            if(!products.isVisible()){
                new Tada(btn_products).play();
                products.setVisible(true);
                new FlipInY(products).play();
                btn_products.setEffect(new Glow());
                sells.setVisible(false);
                btn_sells.setEffect(null);
                employers.setVisible(false);
                btn_employers.setEffect(null);
                buys.setVisible(false);
                btn_buys.setEffect(null);
            }            
            
        }
        else if(event.getTarget() == btn_sells){
            if(!sells.isVisible()){
                new Tada(btn_sells).play();
                products.setVisible(false);
                btn_products.setEffect(null);
                sells.setVisible(true);
                new FlipInY(sells).play();
                btn_sells.setEffect(new Glow());
                employers.setVisible(false);
                btn_employers.setEffect(null);
                buys.setVisible(false);
                btn_buys.setEffect(null);
            }
        }
        else if(event.getTarget() == btn_employers){
            if(!employers.isVisible()){
                new Tada(btn_employers).play();
                products.setVisible(false);
                btn_products.setEffect(null);
                sells.setVisible(false);
                btn_sells.setEffect(null);
                employers.setVisible(true);
                new FlipInY(employers).play();
                btn_employers.setEffect(new Glow());
                buys.setVisible(false);
                btn_buys.setEffect(null);
                 
            }
        }
        else if(event.getTarget() == btn_buys){
            if(!buys.isVisible()){
                new Tada(btn_buys).play();
                products.setVisible(false);
                btn_products.setEffect(null);
                sells.setVisible(false);
                btn_sells.setEffect(null);
                buys.setVisible(true);
                new FlipInY(buys).play();
                btn_buys.setEffect(new Glow());
                employers.setVisible(false);
                btn_employers.setEffect(null);
            }
        }        
        else if(event.getTarget() == btn_close){
            
                        ((Node)event.getSource()).getScene().getWindow().hide();
                        Stage stage = new Stage();
                        AnchorPane root = FXMLLoader.load(getClass().getResource(FXML_PATH + "Login.fxml"));
                        Scene scene = new Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        //stage.initStyle(StageStyle.TRANSPARENT);
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "custom.css").toExternalForm());
                        scene.getStylesheets().add(getClass().getResource(LAYOUT_PATH + "buttons.css").toExternalForm());                          
                        stage.setScene(scene);
                        stage.setMinHeight(350);
                        stage.setMinWidth(450);                        
                        stage.show();
                        setDraggable(root,stage);
                        
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
                break;
            case "sells":
                products.setVisible(false);
                sells.setVisible(true);
                employers.setVisible(false);
                buys.setVisible(false);
                break;
            case "employers":
                products.setVisible(false);
                sells.setVisible(false);
                employers.setVisible(true);
                buys.setVisible(false);
                break;
            case "buys":
                products.setVisible(false);
                sells.setVisible(false);
                employers.setVisible(false);
                buys.setVisible(true);
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
  
    
}