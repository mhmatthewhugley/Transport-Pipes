package de.robotricker.transportpipes.duct.pipe.filter;

public class FilterResponse {
	
	private int amount;
	private boolean hasItem;
	
	public FilterResponse(int amount, boolean hasItem) {
		this.amount = amount;
		this.hasItem = hasItem;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public void setHasItem(boolean hasItem) {
		this.hasItem = hasItem;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public boolean hasItem() {
		return hasItem;
	}

}
