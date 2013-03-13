package rarus.eatery.model;

/**
 * Объект элемент заказа
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class Order {
	private int mId;					// служебный id
	private int mOrderId;				// id заказа
	private int mDishId;				// id блюда, находящегося в заказе
	private String mName;				// название блюда
	private String mDescription;		// описание блюда
	private boolean mPortioned;			// флаг порционности блюда
	private float mPrice;				// цена блюда
	private String mRating;				// рейтинг блюда
	private boolean mPreorder;			// флаг возможности предзаказа блюда
	private float mAmmount;				// объём заказанного 
	private float mSum;					// сумма по заказу
	private int mExecutionDate;			// дата выполнения заказа
	private int mOrderSrvNumber;		// номер заказа на сервере
	
	public Order(int mId, int mOrderId, int mDishId, String mName, String mDescription,
			boolean mPortioned, float mPrice, String mRating,
			boolean mPreorder, float mAmmount, float mSum,
			int mExecutionDate, int mOrderSrvNumber) {
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
	public int getDishId() {
		return mDishId;
	}
	public void setDishId(int mDishId) {
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
	public int getOrderSrvNumber() {
		return mOrderSrvNumber;
	}
	public void setOrderSrvNumber(int mOrderSrvNumber) {
		this.mOrderSrvNumber = mOrderSrvNumber;
	}
}