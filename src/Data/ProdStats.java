
package Data;

/**
 *
 * @author user
 */
public class ProdStats {
    
    private int buyAverage, sellAverage, qteBuy, qteSell, profit;

    public ProdStats(int buyAverage, int sellAverage, int qteBuy, int qteSell, int profit) {
        
        this.buyAverage = buyAverage;
        this.sellAverage = sellAverage;
        this.qteBuy = qteBuy;
        this.qteSell = qteSell;
        this.profit = profit;
        
    }

    public ProdStats() {
        
        this.buyAverage = 0;
        this.sellAverage = 0;
        this.qteBuy = 0;
        this.qteSell = 0;
        
    }

    public int getBuyAverage() {
        return buyAverage;
    }

    public void setBuyAverage(int buyAverage) {
        this.buyAverage = buyAverage;
    }

    public int getSellAverage() {
        return sellAverage;
    }

    public void setSellAverage(int sellAverage) {
        this.sellAverage = sellAverage;
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
