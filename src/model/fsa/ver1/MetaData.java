package model.fsa.ver1;

import ui.Publisher;
import io.fsa.ver1.SubElementContainer;
import model.fsa.FSAMetaData;

public class MetaData implements FSAMetaData {

	private SubElementContainer data;
	
	public MetaData(SubElementContainer data){
		this.data = data;
	}
	
	/* (non-Javadoc)
	 * @see model.fsa.FSAMetaData#getData(java.lang.String)
	 */
	public Object getData(String moduleID) throws ClassNotFoundException {
		
		
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see model.fsa.FSAMetaData#setData(java.lang.Object)
	 */
	public void setData(Object data) {
		// TODO Auto-generated method stub

	}

}
