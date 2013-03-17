package rarus.eatery.model;

import java.util.Date;

/**
 * ������ ������� ����
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class Menu {
	private int mId;					// id ����
	private int mDate;					// ����, �� ������� ��� ����
	private String mDishId;				// id �����, ������������ � ����
	private String mName;				// �������� �����
	private String mDescription;		// �������� �����
	private boolean mPortioned;			// ���� ������������ �����
	private float mPrice;				// ���� �����
	private String mRating;				// ������� �����
	private boolean mPreorder;			// ���� ����������� ���������� �����
	private float mAvailable;			// ����� ��������� ��� ������
	private float mAmmount;				// ����� ����������� 
	private boolean mModified;			// ���� ��������� ��������� ����
	private int mTimestamp;				// ��������� ����� ����
	
	public Menu(int mId, int mDate, String mDishId, String mName,
			String mDescription, boolean mPortioned, float mPrice,
			String mRating, boolean mPreorder, float mAvailable,
			float mAmmount, boolean mModified, int mTimestamp) {
		
		this.mId = mId;
		this.mDate = mDate;
		this.mDishId = mDishId;
		this.mName = mName;
		this.mDescription = mDescription;
		this.mPortioned = mPortioned;
		this.mPrice = mPrice;
		this.mRating = mRating;
		this.mPreorder = mPreorder;
		this.mAvailable = mAvailable;
		this.mAmmount = mAmmount;
		this.mModified = mModified;
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
	public float getAvailable() {
		return mAvailable;
	}
	public void setAvailable(float mAvailable) {
		this.mAvailable = mAvailable;
	}
	public float getAmmount() {
		return mAmmount;
	}
	public void setAmmount(float mAmmount) {
		this.mAmmount = mAmmount;
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
	public String toString(){
		java.util.Date date=new Date((long)mDate*1000);
		return ("�����: "+mName +"���� : "+date.toString());
	}
}