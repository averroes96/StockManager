
package Data;

import static Include.Common.getConnection;
import static Include.Init.UNKNOWN_ERROR;
import Include.SpecialAlert;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;

/**
 *
 * @author user
 */
public class ProdStats {
    
    private int totalBuy, totalSell, qteBuy, qteSell, profit;
    
    private static SpecialAlert alert = new SpecialAlert();

    public ProdStats(int totalBuy, int totalSell, int qteBuy, int qteSell, int profit) {
        
        this.totalBuy = totalBuy;
        this.totalSell = totalSell;
        this.qteBuy = qteBuy;
        this.qteSell = qteSell;
        this.profit = profit;
        
    }

    public ProdStats() {
        
        this.totalBuy = 0;
        this.totalSell = 0;
        this.qteBuy = 0;
        this.qteSell = 0;
        
    }

    public int getTotalBuy() {
        return totalBuy;
    }

    public void setTotalBuy(int totalBuy) {
        this.totalBuy = totalBuy;
    }

    public int getTotalSell() {
        return totalSell;
    }

    public void setTotalSell(int totalSell) {
        this.totalSell = totalSell;
    }

    public int getQteBuy() {
        return qteBuy;
    }

    public void setQteBuy(int qteBuy) {
        this.qteBuy = qteBuy;
    }

    public int getQteSell() {
        return qteSell;
    }

    public void setQteSell(int qteSell) {
        this.qteSell = qteSell;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }
    
    public static ProdStats get(int prodID){
        
        Connection con = getConnection();
        String query = "SELECT * FROM product_stats WHERE prod_id = ?";

        PreparedStatement st;
        ResultSet rs;
                

        try {
            st = con.prepareStatement(query);
            st.setInt(1,prodID);
            rs = st.executeQuery();

            while (rs.next()) {
                
                ProdStats stats = new ProdStats(rs.getInt("total_bought"),rs.getInt("total_solde"),rs.getInt("qte_bought"),rs.getInt("qte_sold"),rs.getInt("capital"));
                return stats;
            }

            con.close();
            
            
        }
        catch (SQLException e){ 
            
            alert.show(UNKNOWN_ERROR, e.getMessage(), Alert.AlertType.ERROR,true);
            return null;
        }
        
        return new ProdStats();
        
    }    
    
    
}
