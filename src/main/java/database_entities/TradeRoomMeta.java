/**
 * Entity class that is associated with a user. This class is a container of all meta data associated with 
 * a user's TradeRoom. This would also be the ideal place to include personal security preferences that 
 * are not associated with personal details. i.e. set the scope of the entirety of the room. 
 * 
 * 
 */
package database_entities;

import java.util.List;

public class TradeRoomMeta {
	private Integer maxTradeItemCount;
	private Integer maxItemImageCount;
	private Integer numberOfSuccessfulTrades;
	private Integer numberOfItems;
	private List<String> itemIds; // List of Item Ids
	private List<String> tradeLogIds;	//List of previous trades kept on a separate collection (Not Implemented yet)

	public TradeRoomMeta(Integer maxTradeItemCount, Integer maxItemImageCount, Integer numberOfSuccessfulTrades, List<String> itemIds, List<String> tradeLogIds) {
		this.maxTradeItemCount = maxTradeItemCount;
		this.maxItemImageCount = maxItemImageCount;
		this.numberOfSuccessfulTrades = numberOfSuccessfulTrades;
		this.itemIds = itemIds;
		this.tradeLogIds = tradeLogIds;
		
		if(itemIds == null)
			this.numberOfItems = 0;
		else
			this.numberOfItems = itemIds.size();
	}

	public Integer getMaxTradeItemCount() {
		return maxTradeItemCount;
	}

	public void setMaxTradeItemCount(Integer maxTradeItemCount) {
		this.maxTradeItemCount = maxTradeItemCount;
	}

	public List<String> getItemIds() {
		return itemIds;
	}

	public void setItemIds(List<String> itemIds) {
		this.itemIds = itemIds;
	}
	
	public Integer getMaxItemImageAccount() {
		return this.maxItemImageCount;
	}
	
	public void setMaxItemImageCount(Integer maxItemImageCount) {
		this.maxItemImageCount = maxItemImageCount;
	}
	
	public Integer getNumberOfItems() {
		return this.numberOfItems;
	}
	
	public void setNumberOfItems(Integer numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public Integer getNumberOfSuccessfulTrades() {
		return numberOfSuccessfulTrades;
	}

	public void setNumberOfSuccessfulTrades(Integer numberOfSuccessfulTrades) {
		this.numberOfSuccessfulTrades = numberOfSuccessfulTrades;
	}

	public Integer getMaxItemImageCount() {
		return maxItemImageCount;
	}
	
	

	public List<String> getTradeLogIds() {
		return tradeLogIds;
	}

	public void setTradeLogIds(List<String> tradeLogIds) {
		this.tradeLogIds = tradeLogIds;
	}

	@Override
	public String toString() {
		return "TradeRoomMeta [maxTradeItemCount=" + maxTradeItemCount
				+ ", maxItemImageCount=" + maxItemImageCount
				+ ", numberOfSuccessfulTrades=" + numberOfSuccessfulTrades
				+ ", numberOfItems=" + numberOfItems + ", itemIds=" + itemIds
				+ ", tradeLogIds=" + tradeLogIds + "]";
	}
}
