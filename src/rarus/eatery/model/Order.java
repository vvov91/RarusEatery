package rarus.eatery.model;

/**
 * Order item object
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class Order {
	private int mId;					// service id
	private int mOrderId;				// order id
	private String mDishId;				// dish id, which is in this order
	private String mName;				// dish name
	private String mDescription;		// dish description
	private boolean mPortioned;			// portion flag
	private float mPrice;				// dish price
	private String mRating;				// dish rating
	private boolean mPreorder;			// preorder flag
	private float mAmmount;				// ordered amount 
	private float mSum;					// ordered sum
	private int mExecutionDate;			// order execution date
	private String mOrderSrvNumber;		// order number stored on server
	
	public Order(int mId, int mOrderId, String mDishId, String mName, String mDescription,
			boolean mPortioned, float mPrice, String mRating,
			boolean mPreorder, float mAmmount, float mSum,
			int mExecutionDate, String mOrderSrvNumber) {

		this.mId = mId;
		this.mOrderId = mOrderId;
		this.mDishId = mDishId;
		this.mName = mName;
		this.mDescription = mDescription;
		this.mPortioned = mPortioned;
		this.mPrice = mPrice;
		this.mRating = mRating;
		this.mPreorder = mPreorder;
		this.mAmmount = mAmmount;
		this.mSum = mSum;
		this.mExecutionDate = mExecutionDate;
		this.mOrderSrvNumber = mOrderSrvNumber;
	}
	
	public int getId() {
		return mId;
	}
	public void setId(int mId) {
		this.mId = mId;
	}
	public int getOrderId() {
		return mOrderId;
	}
	public void setOrderId(int mOrderId) {
		this.mOrderId = mOrderId;
	}
	public String getDishId() {
		return mDishId;
	}
	public void setDishId(String mDishId) {
		this.mDishId = mDishId;
	}
	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	public String getDescription() {
		return mDescription;
	}
	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}
	public boolean isPortioned() {
		return mPortioned;
	}
	public void setPortioned(boolean mPortioned) {
		this.mPortioned = mPortioned;
	}
	public float getPrice() {
		return mPrice;
	}
	public void setPrice(float mPrice) {
		this.mPrice = mPrice;
	}
	public String getRating() {
		return mRating;
	}
	public void setRating(String mRating) {
		this.mRating = mRating;
	}
	public boolean isPreorder() {
		return mPreorder;
	}
	public void setPreorder(boolean mPreorder) {
		this.mPreorder = mPreorder;
	}
	public float getAmmount() {
		return mAmmount;
	}
	public void setAmmount(float mAmmount) {
		this.mAmmount = mAmmount;
	}
	public float getSum() {
		return mSum;
	}
	public void setSum(float mSum) {
		this.mSum = mSum;
	}
	public int getExecutionDate() {
		return mExecutionDate;
	}
	public void setExecutionDate(int mExecutionDate) {
		this.mExecutionDate = mExecutionDate;
	}
	public String getOrderSrvNumber() {
		return mOrderSrvNumber;
	}
	public void setOrderSrvNumber(String mOrderSrvNumber) {
		this.mOrderSrvNumber = mOrderSrvNumber;
	}
}