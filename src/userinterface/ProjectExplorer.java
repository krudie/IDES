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
			
	}
	
}
