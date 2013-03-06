package rarus.eatery.model;

/**
 * Объект заказ с заголовками
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class Order extends OrderHeader {
	private int mId;
	private int mOrderId;
	private int mDishId;
	private float mAmmount;
	private float mSum;
	
	public Order(int mId, int mOrderId, int mDishId, int mMenuId, boolean mExecute, int mExecutionDate,
			boolean mModified, int mTimestamp, int mOrderSrvNumber, float mAmmount, float mSum) {
		super(mId, mMenuId, mExecute, mExecutionDate, mModified, mTimestamp,
				mOrderSrvNumber);

		this.mId = mId;
		this.mOrderId = mOrderId;
		this.mDishId = mDishId;
		this.mAmmount = mAmmount;
		this.mSum = mSum;
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
}