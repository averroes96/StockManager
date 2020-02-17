
package Data;

/**
 *
 * @author user
 */
public class ProdStats {
    
    private int totalBuy, totalSell, qteBuy, qteSell, profit;

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
    
    
}
