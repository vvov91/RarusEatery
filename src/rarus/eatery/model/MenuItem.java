package rarus.eatery.model;

/**
 * Объект меню
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class MenuItem {
	private int mId;
	private int mDate;
	private int mDishId;
	private float mAvailable;
	private int mTimestamp;
	
	public MenuItem(int mId, int mDate, int mDishId, float mAvailable, int mTimestamp) {
		this.mId = mId;
		this.mDate = mDate;
		this.mDishId = mDishId;
		this.mAvailable = mAvailable;
		this.mTimestamp = mTimestamp;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public int getDate() {
		return mDate;
	}

	public void setDate(int mDate) {
		this.mDate = mDate;
	}

	public int getDishId() {
		return mDishId;
	}

	public void setDishId(int mDishId) {
		this.mDishId = mDishId;
	}

	public float getAvailable() {
		return mAvailable;
	}

	public void setAvailable(float mAvailable) {
		this.mAvailable = mAvailable;
	}

	public int getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(int mTimestamp) {
		this.mTimestamp = mTimestamp;
	}	
}