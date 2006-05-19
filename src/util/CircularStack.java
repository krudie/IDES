package util;

public class CircularStack {
	
	    CircularList contents;
	    
	    CircularStack() {
	        contents = new CircularList();
	    }
	    public void push(Object x) {
	        contents.insert(x);
	    }
	    public Object pop() {
	        if (isEmpty())
	           return null;
	        else {
	           Object topItem = contents.head();
	           contents.delete();
	           return topItem;
	        }
	    }
	    public Object peek() {
	        if (isEmpty())
	           return null;
	        else
	           return contents.head();
	    }
	    public boolean isEmpty() {
	        return (contents.isEmpty());
	    }
	    public String toString() {
	        if (isEmpty())
	           return "empty stack";
	        else
	           return "stack " + contents;
	    }
	}

