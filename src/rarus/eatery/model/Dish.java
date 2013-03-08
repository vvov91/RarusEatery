package rarus.eatery.model;

/**
 * Объект блюдо
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class Dish {
	private int mId;
	private String mName;
	private String mDescription;
	private boolean mPortioned;
	private float mPrice;
	private String mRating;
	private boolean mPreorder;
	
	public String toString(){
		StringBuilder build=new StringBuilder("Dish:");
		build.append(mName);
		build.append(" ID: ");
		build.append(mId);
		build.append(" price =");
		build.append(mPrice);
		return build.toString();
	}
	public Dish(int mId, String mName, String mDescription, boolean mPortioned, float mPrice, 
			String mRating, boolean mPreorder) {
		this.mId = mId;
		this.mName = mName;
		this.mDescription = mDescription;
		this.mPortioned = mPortioned;
		this.mPrice = mPrice;
		this.mRating = mRating;
		this.mPreorder = mPreorder;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
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
}