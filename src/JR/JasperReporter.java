/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JR;

import static Include.Common.getConnection;
import Include.Init;
import Include.SpecialAlert;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.util.HashMap;
import javafx.scene.control.Alert;
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
    
    SpecialAlert alert = new SpecialAlert();
    public HashMap params = new HashMap() ; 
    

    public JasperReporter() throws HeadlessException {
        
    }

    public void ShowReport(String type,String query){
        
        Connection conn = getConnection();
        
        switch (type) {
            case "sellsReport":
                try {
                    JasperReport report = JasperCompileManager.compileReport(ClassLoader.class.getResourceAsStream("/JR/sellDay.jrxml"));
                    JasperPrint jp = JasperFillManager.fillReport(report, params,conn);
                    JRViewer viewer = new JRViewer(jp);
                    viewer.setOpaque(true);
                    viewer.setVisible(true);
                    
                    this.add(viewer);
                    this.setSize(900, 500);
                    this.setVisible(true);
                } catch (JRException ex) {
                    alert.show(JR_ERROR, ex.getMessage(), Alert.AlertType.ERROR, true);
                }       break;
            case "productsReport":
                System.getProperty("user.dir");
                try {
                    
                    JasperReport report = JasperCompileManager.compileReport(ClassLoader.class.getResourceAsStream("/JR/products.jrxml"));
                    JasperPrint jp = JasperFillManager.fillReport(report, params,conn);
                    JRViewer viewer = new JRViewer(jp);
                    viewer.setOpaque(true);
                    viewer.setVisible(true);
                    
                    this.add(viewer);
                    this.setSize(900, 500);
                    this.setVisible(true);
                } catch (JRException ex) {
                    alert.show(JR_ERROR, ex.getMessage(), Alert.AlertType.ERROR, true);
                }       break;
            case "sellBill":
                try {
                    JasperReport report = JasperCompileManager.compileReport(ClassLoader.class.getResourceAsStream("/JR/sellBill.jrxml"));
                    JasperPrint jp = JasperFillManager.fillReport(report, params,conn);
                    JRViewer viewer = new JRViewer(jp);
                    viewer.setOpaque(true);
                    viewer.setVisible(true);
                    
                    this.add(viewer);
                    this.setSize(900, 500);
                    this.setVisible(true);
                    
                } catch (JRException ex) {
                    alert.show(JR_ERROR, ex.getMessage(), Alert.AlertType.ERROR, true);
                }       break;
            case "employersList":
                try {
                    JasperReport report = JasperCompileManager.compileReport(ClassLoader.class.getResourceAsStream("/JR/EmployersList.jrxml"));
                    JasperPrint jp = JasperFillManager.fillReport(report, params,conn);
                    JRViewer viewer = new JRViewer(jp);
                    viewer.setOpaque(true);
                    viewer.setVisible(true);
                    
                    this.add(viewer);
                    this.setSize(900, 500);
                    this.setVisible(true);
                    
                } catch (JRException ex) {
                    alert.show(JR_ERROR, ex.getMessage(), Alert.AlertType.ERROR, true);
                }       break;
            case "buy":
                try {
                    JasperReport report = JasperCompileManager.compileReport(ClassLoader.class.getResourceAsStream("/JR/buyBill.jrxml"));
                    JasperPrint jp = JasperFillManager.fillReport(report, params,conn);
                    JRViewer viewer = new JRViewer(jp);
                    viewer.setOpaque(true);
                    viewer.setVisible(true);
                    
                    this.add(viewer);
                    this.setSize(900, 500);
                    this.setVisible(true);
                    
                } catch (JRException ex) {
                    alert.show(JR_ERROR, ex.getMessage(), Alert.AlertType.ERROR, true);
                }       break;
            case "buys":
                try {
                    JasperReport report = JasperCompileManager.compileReport(ClassLoader.class.getResourceAsStream("/JR/buyDay.jrxml"));
                    JasperPrint jp = JasperFillManager.fillReport(report, params,conn);
                    JRViewer viewer = new JRViewer(jp);
                    viewer.setOpaque(true);
                    viewer.setVisible(true);
                    
                    this.add(viewer);
                    this.setSize(900, 500);
                    this.setVisible(true);
                    
                } catch (JRException ex) {
                    alert.show(JR_ERROR, ex.getMessage(), Alert.AlertType.ERROR, true);
                }       break;                
            default:
                break;
                
                
        }
        

        
    }
    
    
    
}
