package model;


public interface DESModelSubscriber {
	public void saveStatusChanged(DESModelMessage message);
	public void modelNameChanged(DESModelMessage message);
}
