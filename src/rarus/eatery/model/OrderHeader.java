package rarus.eatery.model;

/**
 * Объект заголовка заказа
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class OrderHeader {
	private int mId;
	private int mMenuId;
	private boolean mExecute;
	private int mExecutionDate;
	private boolean mModified;
	private int mTimestamp;
	private int mOrderSrvNumber;
	
	public OrderHeader(int mId, int mMenuId, boolean mExecute, int mExecutionDate, boolean mModified,
			int mTimestamp, int mOrderSrvNumber) {
		this.mId = mId;
		this.mMenuId = mMenuId;
		this.mExecute = mExecute;
		this.mExecutionDate = mExecutionDate;
		this.mModified = mModified;
		this.mTimestamp = mTimestamp;
		this.mOrderSrvNumber = mOrderSrvNumber;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public int getMenuId() {
		return mMenuId;
	}

	public void setMenuId(int mMenuId) {
		this.mMenuId = mMenuId;
	}

	public boolean isExecute() {
		return mExecute;
	}

	public void setExecute(boolean mExecute) {
		this.mExecute = mExecute;
	}

	public int getExecutionDate() {
		return mExecutionDate;
	}

	public void setExecutionDate(int mExecutionDate) {
		this.mExecutionDate = mExecutionDate;
	}

	public boolean isModified() {
		return mModified;
	}

	public void setModified(boolean mModified) {
		this.mModified = mModified;
	}

	public int getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(int mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	public int getOrderSrvNumber() {
		return mOrderSrvNumber;
	}

	public void setOrderSrvNumber(int mOrderSrvNumber) {
		this.mOrderSrvNumber = mOrderSrvNumber;
	}
}