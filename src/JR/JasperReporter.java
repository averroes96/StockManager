/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JR;

import static Include.Common.getConnection;
import Include.Init;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author med
 */
public class JasperReporter extends JFrame implements Init{
    
    public HashMap params = new HashMap() ;
    public Connection conn;
    

    public JasperReporter() throws HeadlessException {
        
    }
    
    public void setReportDetails(String path) throws JRException, SQLException{
        
                    conn = getConnection();

                    JasperReport report = JasperCompileManager.compileReport(ClassLoader.class.getResourceAsStream(path));
                    JasperPrint jp = JasperFillManager.fillReport(report, params,conn);
                    JRViewer viewer = new JRViewer(jp);
                    viewer.setOpaque(true);
                    viewer.setVisible(true);
                    
                    this.add(viewer);
                    this.setSize(900, 500);
                    this.setVisible(true);
        
    }

    public void ShowReport(String type,String query) throws SQLException, JRException{
        
        
        switch (type) {
            case "sellsReport":
                
                    setReportDetails("/JR/sellDay.jrxml");
                    break;
                    
            case "productsReport":
                    
                    setReportDetails("/JR/products.jrxml");
                    break;
                    
            case "sellBill":
                
                    setReportDetails("/JR/sellBill.jrxml");
                    break;
                    
            case "employersList":
                
                    setReportDetails("/JR/EmployersList.jrxml");
                    break;
                    
            case "buy":
                
                    setReportDetails("/JR/buyBill.jrxml");
                    break;
                    
            case "buys":
                    JasperReport report = JasperCompileManager.compileReport(ClassLoader.class.getResourceAsStream("/JR/buyDay.jrxml"));
                    setReportDetails("/JR/buyDay.jrxml");
                    break;
                    
            default:
                break;
            
        }
        

        
    }
    
    
}
