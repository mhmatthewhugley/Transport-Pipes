package de.robotricker.transportpipes.duct.pipe.filter;

public class FilterResponse {
	
	private int weight;
	private boolean hasItems;
	
	public FilterResponse(int weight, boolean hasItems) {
		this.weight = weight;
		this.hasItems = hasItems;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public void setHasItems(boolean hasItems) {
		this.hasItems = hasItems;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public boolean hasItems() {
		return hasItems;
	}

}
