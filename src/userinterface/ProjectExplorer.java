/**
 * 
 */
package userinterface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author edlund
 *
 */
public class ProjectExplorer {

	Tree treeWindow;
	
	
	public ProjectExplorer(Composite parent){
		treeWindow = new Tree(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
			
	
		//testcode for trees
		for (int i=0; i<4; i++) {
			TreeItem iItem = new TreeItem (treeWindow, 0);
			iItem.setImage(ResourceManager.getHotImage(ResourceManager.FILE_NEW_AUTOMATON));
			iItem.setText ("TreeItem (0) -" + i);
			for (int j=0; j<4; j++) {
				TreeItem jItem = new TreeItem (iItem, 0);
				jItem.setText ("TreeItem (1) -" + j);
				jItem.setImage(ResourceManager.getHotImage(ResourceManager.FILE_OPEN));
				for (int k=0; k<4; k++) {
					TreeItem kItem = new TreeItem (jItem, 0);
					kItem.setText ("TreeItem (2) -" + k);
					for (int l=0; l<4; l++) {
						TreeItem lItem = new TreeItem (kItem, 0);
						lItem.setText ("TreeItem (3) -" + l);
					}
				}
			}
		}
	}
	
}
