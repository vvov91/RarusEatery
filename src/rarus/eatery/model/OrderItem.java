package rarus.eatery.model;

/**
 * Объект заказ
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class OrderItem {
	private int mId;
	private int mOrderId;
	private int mDishId;
	private float mAmmount;
	private float mSum;
	
	public OrderItem(int mId, int mOrderId, int mDishId, float mAmmount, float mSum) {
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