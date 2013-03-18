package rarus.eatery.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Объект элемент меню
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class RarusMenu implements Parcelable{
	private int mId;					// id меню
	private int mDate;					// дата, на которую это меню
	private String mDishId;				// id блюда, находящегося в меню
	private String mName;				// название блюда
	private String mDescription;		// описание блюда
	private boolean mPortioned;			// флаг порционности блюда
	private float mPrice;				// цена блюда
	private String mRating;				// рейтинг блюда
	private boolean mPreorder;			// флаг возможности предзаказа блюда
	private float mAvailable;			// объём доступный для заказа
	private float mAmmount;				// объём заказанного 
	private boolean mModified;			// флаг состояния изменения меню
	private int mTimestamp;				// временной штамп меню
	
	public RarusMenu(int mId, int mDate, String mDishId, String mName,
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(mId);
		parcel.writeInt(mDate);
		parcel.writeString(mDishId);
		parcel.writeString(mName);
		parcel.writeString(mDescription);
		parcel.writeByte((byte) (mPortioned ? 1 : 0));
		parcel.writeFloat(mPrice);
		parcel.writeString(mRating);
		parcel.writeByte((byte) (mPreorder ? 1 : 0));
		parcel.writeFloat(mAvailable);
		parcel.writeFloat(mAmmount);
		parcel.writeByte((byte) (mModified ? 1 : 0));
		parcel.writeInt(mTimestamp);
	}

	public static final Parcelable.Creator<RarusMenu> CREATOR = new Parcelable.Creator<RarusMenu>() {
		public RarusMenu createFromParcel(Parcel in) {
			return new RarusMenu(in);
		}

		public RarusMenu[] newArray(int size) {
			return new RarusMenu[size];
		}
	};

	private RarusMenu(Parcel parcel) {
		this.mId = parcel.readInt();
		this.mDate = parcel.readInt();
		this.mDishId = parcel.readString();
		this.mName = parcel.readString();
		this.mDescription = parcel.readString();
		this.mPortioned = parcel.readByte() == 1;
		this.mPrice = parcel.readFloat();
		this.mRating = parcel.readString();
		this.mPreorder = parcel.readByte() == 1;
		this.mAvailable = parcel.readFloat();
		this.mAmmount = parcel.readFloat();
		this.mModified = parcel.readByte() == 1;
		this.mTimestamp = parcel.readInt();
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
}