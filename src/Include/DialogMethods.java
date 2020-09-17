
package Include;

import com.jfoenix.controls.JFXDialogLayout;

/**
 *
 * @author user
 */
public abstract class DialogMethods {
    
    public void loadDialog(JFXDialogLayout layout, boolean btnIncluded){}
    
    public void exceptionLayout(Exception e){}

    public void customDialog(String title, String body, String icon, boolean btnIncluded){}
    
}
