
package ui;

/**
 * An extension of JList that filters its contents based on
 * a search string prefix typed into a JTextField.
 * 
 * @see http://java.sun.com/developer/JDCTechTips/2005/tt1214.html
 */
import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;

/**
 * @author Squirrel
 *
 */



	   import javax.swing.text.*;
	   import javax.swing.event.*;
import java.util.*;
	   
	   public class FilteringJList extends JList {
	     /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTextField input;
	   
	   public FilteringJList() {
	     FilteringModel model = new FilteringModel();
	     setModel(new FilteringModel());
	   }
	   
	   /**
	    * Associates filtering document listener to text
	    * component.
	    */
	   
	    public void installJTextField(JTextField input) {
	      if (input != null) {
	        this.input = input;
	        FilteringModel model = (FilteringModel)getModel();
	        input.getDocument().addDocumentListener(model);
	      }
	    }
	   
	   /**
	    * Disassociates filtering document listener from text
	    * component.
	    */
	   
	    public void uninstallJTextField(JTextField input) {
	      if (input != null) {
	        FilteringModel model = (FilteringModel)getModel();
	        input.getDocument().removeDocumentListener(model);
	        this.input = null;
	      }
	    }
	   
	   /**
	    * Doesn't let model change to non-filtering variety
	    */
	   
	   public void setModel(ListModel model) {
	     if (!(model instanceof FilteringModel)) {
	       throw new IllegalArgumentException();
	     } else {
	       super.setModel(model);
	     }
	   }
	   
	   /**
	    * Adds item to model of list
	    */
	   public void addElement(Object element) {
	     ((FilteringModel)getModel()).addElement(element);
	   }
	   
	   public void removeElement(Object element) {
		   ((FilteringModel)getModel()).removeElement(element);
	   }
	   
	   /**
	    * Manages filtering of list model
	    */
	   
	   private class FilteringModel extends AbstractListModel
	       implements DocumentListener {
	     /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		List<Object> list;
	     List<Object> filteredList;
	     String lastFilter = "";
	   
	     public FilteringModel() {
	       list = new ArrayList<Object>();
	       filteredList = new ArrayList<Object>();
	     }
	   
	     public void addElement(Object element) {
	       list.add(element);
	       filter(lastFilter);
	     }
	   
	     public void removeElement(Object element){
	    	 list.remove(element);
	    	 filter(lastFilter);
	     }
	     
	     public int getSize() {
	       return filteredList.size();
	     }
	   
	     public Object getElementAt(int index) {
	       Object returnValue;
	       if (index < filteredList.size()) {
	         returnValue = filteredList.get(index);
	       } else {
	         returnValue = null;
	       }
	       return returnValue;
	     }
	   
	   
	     /**
	      * Repopulate filtered list with elements that
	      * have prefix <code>search</code>, case-insensitive. 
	      * 
	      * @param search the prefix to be matched
	      */
	     void filter(String search) {
	       filteredList.clear();
	       for (Object element: list) {
	         if (element.toString().toLowerCase().startsWith(search.toLowerCase())) {
	           filteredList.add(element);
	         }
	       }
	       fireContentsChanged(this, 0, getSize());
	     }
	   
	     // DocumentListener Methods
	   
	     public void insertUpdate(DocumentEvent event) {
	       Document doc = event.getDocument();
	       try {
	         lastFilter = doc.getText(0, doc.getLength());
	         filter(lastFilter);
	       } catch (BadLocationException ble) {
	         System.err.println("Bad location: " + ble);
	       }
	     }
	   
	     public void removeUpdate(DocumentEvent event) {
	       Document doc = event.getDocument();
	       try {
	         lastFilter = doc.getText(0, doc.getLength());
	         filter(lastFilter);
	       } catch (BadLocationException ble) {
	         System.err.println("Bad location: " + ble);
	       }
	     }
	   
	     public void changedUpdate(DocumentEvent event) {
	     }
	    }
	   
	
}
