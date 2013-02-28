package rarus.eatery.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * ������������� ��������� ��� ������ � ��
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class DBManager extends SQLiteOpenHelper {
	private final String LOG_TAG = "rarus.eatery.db.dbmanager";
	
	
	// ���� ��
    private static final String DB_NAME = "Eatery.db";
    // ������ ��
    private static final int DB_VERSION = 1;
    
    
    // ������� 
    private static final String TABLE_DISHES = "Dishes";
    private static final String TABLE_MENU = "Menu";
    private static final String TABLE_ORDERS = "Orders";
    private static final String TABLE_ORDERS_HEADERS = "OrdersHeaders";
    
    // ��� id ������� � ������ �������
    public static final String KEY_ID = "_id";
    
    // ������� DISHES
    private static final String DISHES_NAME = "name";
    private static final String DISHES_DESCRIPTION = "description";
    private static final String DISHES_PORTIONED = "portioned";
    private static final String DISHES_PRICE = "price";
    private static final String DISHES_RATING = "rating";
    private static final String DISHES_PREORDER = "preorder";
    
    // ������� MENU
    private static final String MENU_DATE = "date";
    private static final String MENU_DISH_ID = "dishID";
    private static final String MENU_AVAILABLE = "available";
    private static final String MENU_TIMESTAMP = "timestamp";
    
    // ������� ORDERS
    private static final String ORDERS_ORDER_ID = "orderID";
    private static final String ORDERS_DISH_ID = "dishID";
    private static final String ORDERS_AMMOUNT = "ammount";
    private static final String ORDERS_SUM = "sum";
    
    // ������� ORDERS HEADERS
    private static final String ORDERS_H_MENU_ID = "menuID";
    private static final String ORDERS_H_EXECUTE = "execute";
    private static final String ORDERS_H_EXECUTION_DATE = "executionDate";
    private static final String ORDERS_H_MODIFIED = "modified";
    private static final String ORDERS_H_TIMESTAMP = "timestamp";
    private static final String ORDERS_H_ORDER_SRV_NUMBER = "orderSrvNumber";
    
    
    // ��������� ��
    private SQLiteDatabase mDb;

    
	/**
	 * ����������� ������
	 * 
	 * @param context
	 *     ������� {@link Context}
	 */
	public DBManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	

	/**
	 * ��������� ���������� � ��.
	 * ���� ������� ���� � ������ ������ ����������, ���� ����������� � ������ ������.
	 * 
	 * @throws SQLException
	 *     ���� �� �� �������� ��� ������ � ��������� � � ������ ������.
	 */
	public void open() throws SQLException {
		try {
			mDb = this.getWritableDatabase();
		} catch (SQLiteException ex) {
			mDb = this.getReadableDatabase();
			
			Log.e(LOG_TAG, "Failed to open database in read/write mode. DB is opened in read mode");
		}
	}
	
	/**
	 * ��������� ���������� � ��
	 */
	public void close() {
		mDb.close();
	}	
	
	// � ������ ���� �� ��� �� �������
	@Override
	public void onCreate(SQLiteDatabase _db) {
		StringBuilder query = new StringBuilder();
		
		// ������ �������
		// ������� DISHES
		query.append("CREATE TABLE ");
		query.append(TABLE_DISHES).append(" (").append(KEY_ID).append(" INTEGER PRIMARY KEY, ");
		query.append(DISHES_NAME).append(" TEXT NOT NULL, ");
		query.append(DISHES_DESCRIPTION).append(" TEXT NOT NULL, ");
		query.append(DISHES_PORTIONED).append(" INTEGER DEFAULT 0, ");
		query.append(DISHES_PRICE).append(" FLOAT NOT NULL, ");
		query.append(DISHES_RATING).append(" TEXT DEFAULT 0, ");
		query.append(DISHES_PREORDER).append(" INTEGER DEFAULT 0);");		
		_db.execSQL(query.toString());
		
		query = new StringBuilder();
		
		// ������� MENU
		query.append("CREATE TABLE ");
		query.append(TABLE_MENU).append(" (").append(KEY_ID);
		query.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		query.append(MENU_DATE).append(" INTEGER NOT NULL, ");
		query.append(MENU_DISH_ID).append(" INTEGER NOT NULL, ");
		query.append(MENU_AVAILABLE).append(" FLOAT NOT NULL DEFAULT -1, ");
		query.append(MENU_TIMESTAMP).append(" INTEGER);");
		_db.execSQL(query.toString());
		
		query = new StringBuilder();
		
		// ������� ORDERS
		query.append("CREATE TABLE ");
		query.append(TABLE_ORDERS).append(" (").append(KEY_ID);
		query.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		query.append(ORDERS_ORDER_ID).append(" INTEGER NOT NULL, ");
		query.append(ORDERS_DISH_ID).append(" INTEGER NOT NULL, ");
		query.append(ORDERS_AMMOUNT).append(" FLOAT DEFAULT 0, ");
		query.append(ORDERS_SUM).append(" FLOAT DEFAULT 0);");
		_db.execSQL(query.toString());
		
		query = new StringBuilder();
		
		// ������� ORDERS HEADERS
		query.append("CREATE TABLE ");
		query.append(TABLE_ORDERS_HEADERS).append(" (").append(KEY_ID);
		query.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		query.append(ORDERS_H_MENU_ID).append(" INTEGER NOT NULL, ");
		query.append(ORDERS_H_EXECUTE).append(" INTEGER DEFAULT 0, ");
		query.append(ORDERS_H_EXECUTION_DATE).append(" INTEGER, ");
		query.append(ORDERS_H_MODIFIED).append(" INTEGER DEFAULT 0, ");
		query.append(ORDERS_H_TIMESTAMP).append(" INTEGER, ");
		query.append(ORDERS_H_ORDER_SRV_NUMBER).append(" INTEGER DEFAULT 0);");
		_db.execSQL(query.toString());
		
		Log.i(LOG_TAG, "Created database");
	}

	// � ������ ���� ������������ �� �� ������������� ����������� ������
	// � ��������� � ����������
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}