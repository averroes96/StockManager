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
import static Include.Common.setDraggable;
import Include.Init;
import static Include.Init.IMAGES_PATH;
import static Include.Init.OKAY;
import static Include.Init.UNKNOWN_ERROR;
import Include.SpecialAlert;
import JR.JasperReporter;
import animatefx.animation.AnimationFX;
import animatefx.animation.FadeIn;
import animatefx.animation.Pulse;
import animatefx.animation.Shake;
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
import javafx.scene.control.Alert;
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
import javafx.scene.text.Text;
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
    
    
    SpecialAlert alert = new SpecialAlert();

    final String dateFormat = "yyyy-MM-dd";

    File selectedFile = null;
    
    private Employer thisEmployer = new Employer();
    
    JasperReporter jr = new JasperReporter();
    
    private double xOffset = 0;
    private double yOffset = 0;
    
    
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

    private void onJasperReportLoading(){
        
            JFXDialogLayout layout = new JFXDialogLayout();
            Image image = new Image(IMAGES_PATH + "wait_small.png");
            ImageView icon = new ImageView(image);
            Label label = new Label(PLEASE_WAIT);
            label.graphicProperty().setValue(icon);
            layout.setHeading(label);
            layout.setBody(new Text(REPORT_WAIT_MESSAGE));
            
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
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);

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
            alert.show(INFO_MESSAGE, INFO_MSG, Alert.AlertType.INFORMATION,false);
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
                alert.show(UNKNOWN_ERROR, LOAD_IMAGE_ERROR, Alert.AlertType.ERROR,false);
            }
        }

    }    
    
    private boolean checkInputs()
    {
        if (refField.getText().trim().equals("") && priceField2.getText().trim().equals("") && quantityField.getText().trim().equals("")) {
            alert.show(MISSING_FIELDS, "إسم وأسعار المنتج غير مملوءة", Alert.AlertType.WARNING,false);
        }
        else if (refField.getText().trim().equals("")) {
            alert.show(MISSING_FIELDS, "قم بإدخال إسم المنتج من فضلك", Alert.AlertType.WARNING,false);
            return false;
        }
        else if (priceField2.getText().trim().equals("")) {
            alert.show(MISSING_FIELDS, "من فضلك قم بإدخال أسعار المنتج", Alert.AlertType.WARNING,false);
            return false;
        }
        else if (quantityField.getText().trim().equals("")) {
            alert.show(MISSING_FIELDS, "من فضلك قم بإدخال أسعار المنتج", Alert.AlertType.WARNING,false);
            return false;
        }        
        

        try {
            Integer.parseInt(priceField2.getText());
            if(priceField2.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(priceField2.getText()) > 0){
                if(quantityField.getText().trim().matches("^[1-9]?[0-9]{1,7}$") && Integer.parseInt(priceField2.getText()) >= 0){
                    return true;
                }
                else{
                    alert.show(INVALID_QTE, INVALID_QTE_MSG, Alert.AlertType.ERROR, false);
                    return false;
                }
            }
            else{
            alert.show(INVALID_PRICE, "من فضلك قم بإدخال أسعار صالحة", Alert.AlertType.ERROR,false);
            return false;
            }
        }
        catch (NumberFormatException e) {
            alert.show(INVALID_PRICE, "من فضلك قم بإدخال أسعار صالحة", Alert.AlertType.ERROR,false);
            return false;
        }
    }    
    
    private void updateProduct()
    {

        if(productsTable.getSelectionModel().getSelectedItem() == null)
        {
            alert.show(INFO_MESSAGE, INFO_MSG1, Alert.AlertType.INFORMATION,true);
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

            
            alert.show(PRODUCT_UPDATED,
                    PRODUCT_UPDATED_MSG,
                    Alert.AlertType.INFORMATION,
                    false);
        
        }
        catch (NumberFormatException | SQLException e) {
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);

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
                    ClassLoader.class.getResourceAsStream(IMAGES_PATH + "user.png"),
                    220, 200, true, true)));
        }
        else {
            userImage.setText("");
            userImage.setGraphic(new ImageView(new Image(
                    new File(choosen.getImage()).toURI().toString(),
                    250, 200, true, true)));
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
            alert.show(INFO_MESSAGE, INFO_MSG, Alert.AlertType.INFORMATION,false);
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
            
            alert.show(PRODUCT_DELETED, PRODUCT_DELETED_MSG, Alert.AlertType.INFORMATION,false);            
            
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
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
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
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
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
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        }
    }    
    
    public void getSellStats(String selectedDate, String type){
        
        new FadeIn(revQte).play();
        new FadeIn(revSum).play();
        new FadeIn(revTotal).play();
        
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

            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
        }        
        
    }
    
    public void getBuyStats(String selectedDate, String type){
        
        new FadeIn(buyDayQte).play();
        new FadeIn(buyDaySum).play();
        new FadeIn(buyDayTotal).play();        
        
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

            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
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
            
            alert.show(SELL_DELETED, SELL_DELETED, Alert.AlertType.INFORMATION,false);
            
        }
        catch (SQLException e) {
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
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

                    alert.show(BUY_DELETED, BUY_DELETED, Alert.AlertType.INFORMATION,false);

                }
                catch (SQLException e) {

                    alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
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
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
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
                alert.show(EMPLOYER_DELETED, EMPLOYER_DELETED_MSG, Alert.AlertType.INFORMATION,false);
            
            }
            }
            else{
                
                alert.show(LAST_ADMIN, LAST_ADMIN_MSG, Alert.AlertType.ERROR,false);
                
            }
            
        }
        catch (IOException | SQLException e) {

            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);

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
        
        updateProduct.setOnAction(Action -> {
            updateProduct();
            //new Shake(updateProduct).play();
        });        
       
        deleteProduct.setOnAction(Action -> {
            deleteProduct();
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
               alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
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
                            alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
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
                            alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
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
                            stage.setResizable(false);
                            stage.showAndWait();
                            setDraggable(root,stage);                            
                        } catch (IOException ex) {
                            alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
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
            
            final Button update = new Button("تعديل");
            
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } 
                else {
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
                                alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
                            }
                    });
                    update.setStyle("-fx-background-color : green; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
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
                    delete.setOnAction(event -> {
                        Sell sell = getTableView().getItems().get(getIndex());
                        deleteSell(sell);
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
                alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
            }
            
            }
            else{
                alert.show(NO_PRODUCTS_FOUND, NO_PRODUCTS_FOUND_MESSAGE, Alert.AlertType.ERROR,true);
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
                alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
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
                            alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
                        }
        });

        deleteEmployer.setOnAction(Action -> {

                Employer employer = getUser(usersCB.getSelectionModel().getSelectedItem());
                deleteEmployer(employer);
                


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
                            alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
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
                            alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
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
            
            final Button update = new Button("تعديل");
            
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
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
                alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
            }                        
                    });
                    update.setStyle("-fx-background-color : green; -fx-text-fill: white; -fx-background-radius: 30;fx-background-insets: 0; -fx-cursor: hand;");                    
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
            
            final Button delete = new Button("حذف");
            
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    delete.setOnAction(event -> {
                        Buy buy = getTableView().getItems().get(getIndex());
                        deleteBuy(buy);
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
                    alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
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
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setResizable(false);
                            stage.showAndWait();
                             
                        } catch (IOException ex) {
                            alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
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
                alert.show(UNKNOWN_ERROR, ex.getMessage(), Alert.AlertType.ERROR,true);
                
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
        
        handleMenuButtons();
       
    }

    public void selectMenu(Event event) throws IOException{
        
        
        if(event.getTarget() == btn_products){
            new Pulse(btn_products).play();
            products.setVisible(true);
            btn_products.setEffect(new Glow());
            sells.setVisible(false);
            btn_sells.setEffect(null);
            employers.setVisible(false);
            btn_employers.setEffect(null);
            buys.setVisible(false);
            btn_buys.setEffect(null);            
            
        }
        else if(event.getTarget() == btn_sells){
            new Pulse(btn_sells).play();
            products.setVisible(false);
            btn_products.setEffect(null);
            sells.setVisible(true);
            btn_sells.setEffect(new Glow());
            employers.setVisible(false);
            btn_employers.setEffect(null);
            buys.setVisible(false);
            btn_buys.setEffect(null);              
        }
        else if(event.getTarget() == btn_employers){
            new Pulse(btn_employers).play();
            products.setVisible(false);
            btn_products.setEffect(null);
            sells.setVisible(false);
            btn_sells.setEffect(null);
            employers.setVisible(true);
            btn_employers.setEffect(new Glow());
            buys.setVisible(false);
            btn_buys.setEffect(null);              
        }
        else if(event.getTarget() == btn_buys){
            new Pulse(btn_buys).play();
            products.setVisible(false);
            btn_products.setEffect(null);
            sells.setVisible(false);
            btn_sells.setEffect(null);
            buys.setVisible(true);
            btn_buys.setEffect(new Glow());
            employers.setVisible(false);
            btn_employers.setEffect(null);              
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
            animProduct.play();
        });
        btn_employers.setOnMouseEntered(value -> {
            animUsers.play();
        });
        btn_buys.setOnMouseEntered(value -> {
            animBuys.play();
        });
        btn_sells.setOnMouseEntered(value -> {
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