
package App.Controllers;

import Data.ProdStats;
import Data.Product;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author user
 */
public class ProdStatController implements Initializable {

    
    @FXML private Label totalBuy,totalSell,qteBuy,qteSell,profitLabel;
    
    
    ProdStats stats = new ProdStats();
    
    public void setProduct(Product selected){
        
        //this.stats = ProdStats.get(selected.getProdID());

        totalBuy.setText(String.valueOf(stats.getTotalBuy()) + ".00 دج");
        totalSell.setText(String.valueOf(stats.getTotalSell()) + ".00 دج");
        qteBuy.setText(String.valueOf(stats.getQteBuy()));
        qteSell.setText(String.valueOf(stats.getQteSell()));
        profitLabel.setText(String.valueOf(stats.getProfit()) + ".00 دج");
        
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
             
        
    }    
    
}
