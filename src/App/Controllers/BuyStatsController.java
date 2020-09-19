
package App.Controllers;

import Data.Buy;
import static Include.Common.dateFormatter;
import static Include.Common.getAllFrom;
import static Include.Common.getAllProducts;
import static Include.Common.getConnection;
import static Include.Common.initLayout;
import Include.Init;
import static Include.Init.ERROR_SMALL;
import static Include.Init.OKAY;
import static Include.Init.UNKNOWN_ERROR;
import animatefx.animation.ZoomIn;
import animatefx.animation.ZoomOut;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author user
 */
public class BuyStatsController implements Initializable,Init {

    @FXML private JFXButton search;
    @FXML private TableView<Buy> buysTable;
    @FXML private TableColumn<Buy, Integer> idCol;
    @FXML private TableColumn<Buy, String> prodCol,userCol,dateCol,priceCol,qteCol;
    @FXML private ChoiceBox<String> prodField ;
    @FXML private JFXDatePicker startDate,endDate;
    @FXML private LineChart nbrBuysChart;
    @FXML private BarChart sumBuysChart;
    @FXML private Label idCountLabel,qteCountLabel,priceSumLabel,averageBuyLabel,averageQteLabel,averagePriceLabel, interval;
    @FXML private JFXTextField filterSearch;
    @FXML private StackPane stackPane, filterPane;
    @FXML private JFXDialog dialog;
    @FXML private ImageView filterBtn;

    ObservableList<Buy> buysList = FXCollections.observableArrayList();
    ObservableList<String> nameList = getAllProducts(1);
    @FXML ObservableList<BarChart.Data> barList = FXCollections.observableArrayList();

    private void getData(String name, String start, String end){

        Connection con = getConnection();
        String whereClause = "" ;
        String query ;

        if(!name.equals("الكل")){
            whereClause = "WHERE name = '" + name + "' " ;
        }

        if(!start.equals("")){
            if(whereClause.equals("")){
                whereClause = "WHERE date(buy_date) >= '" + start + "' " ;
            }
            else
                whereClause += "AND date(buy_date) >= '" + start + "' " ;
        }

        if(!end.equals("")){
            if(whereClause.equals("")){
                whereClause = "WHERE date(buy_date) <= '" + end + "' " ;
            }
            else
                whereClause += "AND date(buy_date) <= '" + end + "' " ;
        }


        query = "SELECT * FROM buy INNER JOIN product ON product.prod_id = buy.prod_id INNER JOIN user ON user.user_id = buy.user_id " + whereClause ;

        PreparedStatement st;
        ResultSet rs;


        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery(query);

            while (rs.next()) {

                Buy buy = new Buy();
                buy.setBuyID(rs.getInt("buy_id"));
                buy.setBuyPrice(rs.getInt("buy.buy_unit_price"));
                buy.setBuyTotalPrice(rs.getInt("buy.buy_price"));
                buy.setBuyDate(rs.getTimestamp("buy_date").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd h.mm a")));
                buy.setBuyQte(rs.getInt("buy_qte"));
                buy.setProduct(rs.getString("name"));
                buy.setUser(rs.getString("username"));

                buysList.add(buy);

            }

            query = "SELECT date(buy_date), count(*) FROM buy INNER JOIN product ON product.prod_id = buy.prod_id INNER JOIN user ON user.user_id = buy.user_id " + whereClause + "Group by date(buy_date) " ;

            sumBuysChart.getData().clear();
            XYChart.Series<String,Integer> series = new XYChart.Series<>();

                st = con.prepareStatement(query);
                rs = st.executeQuery();


                while (rs.next()) {

                    series.getData().add(new XYChart.Data<>(rs.getString("date(buy_date)"),rs.getInt("count(*)")));

                }

            sumBuysChart.getData().addAll(series);

            series.getData().forEach((data) -> {
                data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event1) -> {
                    Tooltip.install(data.getNode(), new Tooltip(String.valueOf(data.getYValue().floatValue())));
                });
            });

            series.setName("عدد المشتريات حسب اليوم");

            query = "SELECT date(buy_date), SUM(buy_price) FROM buy INNER JOIN product ON product.prod_id = buy.prod_id INNER JOIN user ON user.user_id = buy.user_id " + whereClause + "Group by date(buy_date) " ;

            nbrBuysChart.getData().clear();
            XYChart.Series<String,Integer> lineSeries = new XYChart.Series<>();

                st = con.prepareStatement(query);
                rs = st.executeQuery();


                while (rs.next()) {

                    lineSeries.getData().add(new XYChart.Data<>(rs.getString("date(buy_date)"),rs.getInt("SUM(buy_price)")));

                }

            nbrBuysChart.getData().addAll(lineSeries);

            lineSeries.getData().forEach((data) -> {
                data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event1) -> {
                    Tooltip.install(data.getNode(), new Tooltip(data.getYValue().toString()));
                });
            });

            lineSeries.setName("المبلغ الإجمالي حسب اليوم");
            
            String select = "COUNT(buy_id), SUM(buy_qte), SUM(buy_price)";
            String tableName = "buy";
            String innerJoin = "INNER JOIN product ON buy.prod_id = product.prod_id";

            ResultSet stats1 = getAllFrom(select, tableName, innerJoin, whereClause,"");

            while(stats1.next()){

            idCountLabel.setText(stats1.getString("COUNT(buy_id)") != null?  stats1.getString("COUNT(buy_id)") + " شراء" : "0 شراء");
            qteCountLabel.setText(stats1.getString("SUM(buy_qte)") != null?  stats1.getString("SUM(buy_qte)") + " قطعة" : "0 قطع");
            priceSumLabel.setText(stats1.getString("SUM(buy_price)") != null?  stats1.getString("SUM(buy_price)") + " دج" : "0 دج");

            }
            
            select = "COUNT(buy_id)/datediff('" + end + "','" + start + "') as abd, SUM(buy_qte)/datediff('" + end + "','" + start + "') as aqd, SUM(buy_price)/datediff('" + end + "','" + start + "') as asd";
            tableName = "buy";
            innerJoin = "INNER JOIN product ON buy.prod_id = product.prod_id";
            
            ResultSet stats2 = getAllFrom(select, tableName, innerJoin, whereClause,"");

            while(stats2.next()){

            averageBuyLabel.setText(stats2.getString("abd") != null?  stats2.getString("abd") + " شراء" : "0 شراء");
            averageQteLabel.setText(stats2.getString("aqd") != null?  stats2.getString("aqd") + " قطعة" : "0 قطع");
            averagePriceLabel.setText(stats2.getString("asd") != null?  stats2.getString("asd") + " دج" : "0 دج");

            }



            con.close();
        }
        catch (SQLException e) {
            exceptionLayout(e);
        }

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

    public void customDialog(String title, String body, String icon, boolean btnIncluded){
            JFXDialogLayout layout = new JFXDialogLayout();
            initLayout(layout, title, body, icon);
            
            loadDialog(layout, btnIncluded);
    }

    public void search(JFXTextField field, ObservableList<Buy> list, TableView table)
    {
        
        String keyword = field.getText();
        
        if (keyword.trim().equals("")) {
            table.setItems(list);
        }
        
        else {
            ObservableList<Buy> filteredBuys = FXCollections.observableArrayList();
            list.stream().filter((buy) -> (buy.getProduct().toLowerCase().contains(keyword.toLowerCase()) || buy.getUser().toLowerCase().contains(keyword.toLowerCase()))).forEachOrdered((buy) -> {
                filteredBuys.add(buy);
            });
            table.setItems(filteredBuys);
        }
      
    }    

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        idCol.setCellValueFactory(new PropertyValueFactory<>("buyID"));
        prodCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("buyDate"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("buyPrice"));
        qteCol.setCellValueFactory(new PropertyValueFactory<>("buyQte"));


        buysTable.setItems(buysList);
        buysTable.getSelectionModel().selectFirst();

        startDate.setConverter(dateFormatter());
        endDate.setConverter(dateFormatter());
        startDate.getEditor().setText(String.valueOf(LocalDate.now().minusWeeks(1)));
        endDate.getEditor().setText(String.valueOf(LocalDate.now()));
        startDate.setValue(LocalDate.now().minusWeeks(1));
        endDate.setValue(LocalDate.now());

        prodField.setItems(nameList);
        prodField.getSelectionModel().selectFirst();

        getData(ALL, startDate.getEditor().getText(), endDate.getEditor().getText());

        search.setOnAction(Action -> {
            
            new ZoomOut(filterPane).play();
            filterPane.setVisible(false);
            
            if(endDate.getValue().compareTo(startDate.getValue()) >= 0){
                
                int interv = endDate.getValue().getDayOfYear() - startDate.getValue().getDayOfYear();

                if(endDate.getValue().equals(LocalDate.now())){
                    switch(interv){
                        case 7:
                            interval.setText(LAST_WEEK_BUYS);
                            break;
                        case 30:
                            interval.setText(LAST_MONTH_BUYS);
                            break;
                        case 365:
                            interval.setText(LAST_YEAR_BUYS);
                            break;
                        default:
                            interval.setText("مشتريات آخر " + interv + " يوم");
                            break;
                    }
                }
                else{
                    interval.setText(BUYS + startDate.getValue().toString() + "  -----  " + endDate.getValue().toString());
                }

                buysTable.getItems().clear();
                getData(prodField.getSelectionModel().getSelectedItem(),startDate.getEditor().getText(),endDate.getEditor().getText());

            }
            else {
                customDialog(ILLEGAL_INTERVAL, ILLEGAL_INTERVAL_MSG, INFO_SMALL, true);
            }


        });
        
        filterSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            search(filterSearch, buysList, buysTable);
        });
        
        filterBtn.setOnMouseClicked(value -> {
            filterPane.setVisible(true);
            new ZoomIn(filterPane).play();
        });
    }

}
