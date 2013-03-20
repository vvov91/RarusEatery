package rarus.eatery.database;

import java.util.ArrayList;
import java.util.List;

import rarus.eatery.model.RarusMenu;
import rarus.eatery.model.Order;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Provides an interface to interact with the database
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class DBManager extends SQLiteOpenHelper {
	private final String LOG_TAG = "rarus.eatery.db.dbmanager";
	
	
	// database file
    private static final String DB_NAME = "Eatery.db";
    // database version
    private static final int DB_VERSION = 1;
    
    
    // tables names
    private static final String TABLE_DISHES = "Dishes";
    private static final String TABLE_MENU = "Menu";
    private static final String TABLE_ORDERS = "Orders";
    private static final String TABLE_ORDERS_HEADERS = "OrdersHeaders";
    
    // _id field in every table
    public static final String KEY_ID = "_id";
    
    // Dishes table
    private static final String DISHES_NAME = "name";
    private static final String DISHES_DESCRIPTION = "description";
    private static final String DISHES_PORTIONED = "portioned";
    private static final String DISHES_PRICE = "price";
    private static final String DISHES_RATING = "rating";
    private static final String DISHES_PREORDER = "preorder";
    
    // Menu table
    private static final String MENU_DATE = "date";
    private static final String MENU_DISH_ID = "dishID";
    private static final String MENU_AVAILABLE = "available";
    private static final String MENU_AMMOUNT = "ammount";
    private static final String MENU_MODIFIED = "modified";
    private static final String MENU_TIMESTAMP = "timestamp";
    
    // Orders table
    private static final String ORDERS_ORDER_ID = "orderID";
    private static final String ORDERS_DISH_ID = "dishID";
    private static final String ORDERS_AMMOUNT = "ammount";
    private static final String ORDERS_SUM = "sum";
    
    // Orders Headers table
    private static final String ORDERS_H_EXECUTION_DATE = "executionDate";
    private static final String ORDERS_H_ORDER_SRV_NUMBER = "orderSrvNumber";
    
    
    // database instance
    private SQLiteDatabase mDb;

    
	/**
	 * Class constructor
	 * 
	 * @param context
	 *     current {@link Context}
	 */
	public DBManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	

	/**
	 * Opens database connection.
	 * If read/write mode not working, opens in read mode.
	 * 
	 * @throws SQLException
	 *     if DB is not writable
	 */
	public void open() throws SQLException {
		try {
			mDb = this.getWritableDatabase();
		} catch (SQLiteException ex) {
			mDb = this.getReadableDatabase();
			
			Log.e(LOG_TAG,
					"[DB] Can not open the database in read / write mode. Working in read mode.");
		}
	}
	
	/**
	 * Closes database connection
	 */
	public void close() {
		mDb.close();
	}	
	
	// if DB is not created
	@Override
	public void onCreate(SQLiteDatabase _db) {
		StringBuilder query = new StringBuilder();
		
		// creating tables
		// Dishes table
		query.append("CREATE TABLE ");
		query.append(TABLE_DISHES).append(" (").append(KEY_ID).append(" TEXT PRIMARY KEY, ");
		query.append(DISHES_NAME).append(" TEXT NOT NULL, ");
		query.append(DISHES_DESCRIPTION).append(" TEXT NOT NULL, ");
		query.append(DISHES_PORTIONED).append(" INTEGER DEFAULT 0, ");
		query.append(DISHES_PRICE).append(" FLOAT NOT NULL, ");
		query.append(DISHES_RATING).append(" TEXT, ");
		query.append(DISHES_PREORDER).append(" INTEGER DEFAULT 0);");		
		_db.execSQL(query.toString());
		
		query = new StringBuilder();
		
		// Menu table
		query.append("CREATE TABLE ");
		query.append(TABLE_MENU).append(" (").append(KEY_ID);
		query.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		query.append(MENU_DATE).append(" INTEGER NOT NULL, ");
		query.append(MENU_DISH_ID).append(" INTEGER NOT NULL, ");
		query.append(MENU_AVAILABLE).append(" FLOAT NOT NULL DEFAULT -1, ");
		query.append(MENU_AMMOUNT).append(" FLOAT DEFAULT 0, ");
		query.append(MENU_MODIFIED).append(" INTEGER DEFAULT 0, ");
		query.append(MENU_TIMESTAMP).append(" INTEGER DEFAULT 0);");
		_db.execSQL(query.toString());
		
		query = new StringBuilder();
		
		// Orders table
		query.append("CREATE TABLE ");
		query.append(TABLE_ORDERS).append(" (").append(KEY_ID);
		query.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		query.append(ORDERS_ORDER_ID).append(" INTEGER NOT NULL, ");
		query.append(ORDERS_DISH_ID).append(" INTEGER NOT NULL, ");
		query.append(ORDERS_AMMOUNT).append(" FLOAT DEFAULT 0, ");
		query.append(ORDERS_SUM).append(" FLOAT DEFAULT 0);");
		_db.execSQL(query.toString());
		
		query = new StringBuilder();
		
		// Orders Headers table
		query.append("CREATE TABLE ");
		query.append(TABLE_ORDERS_HEADERS).append(" (").append(KEY_ID);
		query.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		query.append(ORDERS_H_EXECUTION_DATE).append(" INTEGER DEFAULT 0, ");
		query.append(ORDERS_H_ORDER_SRV_NUMBER).append(" TEXT DEFAULT 0);");
		_db.execSQL(query.toString());
		
		Log.i(LOG_TAG, "[DB] DB created");
	}

	// if an existing database does not match the required version and needs an update
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	
	// Dishes
	
	/**
	 * Changes dish rating
	 * 
	 * @param id
	 *     dish id
	 * @param rating
	 *     dish rating
	 */
	public void setDishRating(String id, String rating) {
		ContentValues data = new ContentValues();
		data.put(DISHES_RATING, rating);
		
		mDb.beginTransaction();		
		try {
			// updating dish record using dish ID
			mDb.update(TABLE_DISHES, data, KEY_ID + " = ?", new String[] {id});	    
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
	}

	/**
	 * Removes unused dishes
	 */
	public void deleteDishesUnused() {		
		int count = 0;
		
		mDb.beginTransaction();
		try {
			// getting all dishes id's
			Cursor cDishes = mDb.query(TABLE_DISHES, new String[] {KEY_ID}, null, null, null,
					null, null);
			
			if (cDishes.moveToFirst()) {				
				do {
					// checking if dish is in at least one menu
					Cursor cMenu = mDb.query(TABLE_MENU,
							new String[] {KEY_ID}, MENU_DISH_ID + " = ?",
							new String[] {cDishes.getString(0)}, null, null, null, "1");
					
					if (cMenu.getCount() > 0) {
						// if it is, continue to next dish
						continue;
					} else {
						// checking if dish is in at least one order
						Cursor cOrders = mDb.query(TABLE_ORDERS,
								new String[] {KEY_ID}, ORDERS_DISH_ID + " = ?",
								new String[] {cDishes.getString(0)}, null, null, null, "1");
						
						if (cOrders.getCount() > 0) {
							// if it is, continue to next dish
							continue;
						} else {
							// if dish is not at least in one menu or order - deleting it
							mDb.delete(TABLE_DISHES, KEY_ID + " = ?",
									new String[] {cDishes.getString(0)});							
							Log.i(LOG_TAG, "[DB] Deleting unused dish (ID: "
									+ cDishes.getString(0) + ")");							
							count++;
						}
						cOrders.close();						
					}
					cMenu.close();					
				} while (cDishes.moveToNext());
			}
			cDishes.close();
			
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			if (count > 0)
				Log.i(LOG_TAG, "[DB] Total dishes deleted: " + Integer.toString(count));
		}
	}
	
	
	// Menu
	
	/**
	 * Adds menu
	 * 
	 * @param menu
	 *     {@link List} of {@link RarusMenu} objects
	 */
	public void addMenu(List<RarusMenu> menu) {
		StringBuilder query = new StringBuilder();
		SQLiteStatement insertDishStmt;
		SQLiteStatement insertMenuStmt;
		
		// insert statement for inserting dish
		query.append("INSERT INTO ").append(TABLE_DISHES).append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
		insertDishStmt = mDb.compileStatement(query.toString());
		
		query = new StringBuilder();
		
		// insert statement for inserting menu
		query.append("INSERT INTO ").append(TABLE_MENU).append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
		insertMenuStmt = mDb.compileStatement(query.toString());

		mDb.beginTransaction();
		try {
			for (int i = 0; i < menu.size(); i++) {
				// checking if dish is already in DB
				Cursor c = mDb.query(false, TABLE_DISHES, new String[] {KEY_ID}, KEY_ID + " = ?",
						new String[] {menu.get(i).getDishId()},
						null, null, null, null);
				
				if (c.getCount() == 0) {
					// if not - adding it
					insertDishStmt.bindString(1, menu.get(i).getDishId());
					insertDishStmt.bindString(2, menu.get(i).getName());
					insertDishStmt.bindString(3, menu.get(i).getDescription());
					insertDishStmt.bindString(4, (menu.get(i).isPortioned() ? "1" : "0"));
					insertDishStmt.bindString(5, Float.toString(menu.get(i).getPrice()));
					insertDishStmt.bindString(6, menu.get(i).getRating());
					insertDishStmt.bindString(7, (menu.get(i).isPreorder() ? "1" : "0"));
					insertDishStmt.execute();
				}
				c.close();
				
				// adding menu
				insertMenuStmt.bindNull(1);
				insertMenuStmt.bindString(2, Integer.toString(formatDate(menu.get(i).getDate())));
				insertMenuStmt.bindString(3, menu.get(i).getDishId());
				insertMenuStmt.bindString(4, Float.toString(menu.get(i).getAvailable()));
				insertMenuStmt.bindString(5, Float.toString(menu.get(i).getAmmount()));
				insertMenuStmt.bindString(6, (menu.get(i).isModified() ? "1" : "0"));
				insertMenuStmt.bindString(7, Integer.toString(menu.get(i).getTimestamp()));
				insertMenuStmt.execute();
			}
			insertDishStmt.close();
			insertMenuStmt.close();
			
		    mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "[DB] Added menus (" + Integer.toString(menu.size()) + ")");
		}
	}
	
	/**
	 * Returns a list of the dates on which menu is available
	 * 
	 * @return
	 *     {@link List} of {@link Integer} Unix time dates sorted in ascending order.
	 */
	public List<Integer> getMenuDates() {
		List<Integer> result = new ArrayList<Integer>();
		
		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(true, TABLE_MENU, new String[] {MENU_DATE}, null, null, 
					null, null, MENU_DATE + " ASC", null);
			mDb.setTransactionSuccessful();
			
			if (c.getCount() > 0) {
				c.moveToFirst();				
				do {
					result.add(c.getInt(0));
				} while(c.moveToNext());
			}
			c.close();
		} finally {
			mDb.endTransaction();
		}
		return result;
	}	
		
	/**
	 * Returns the menu at a given date
	 * 
	 * @param date
	 *     Unix time date
	 * @return
	 *     {@link List} of {@link RarusMenu} objects
	 */
	public List<RarusMenu> getMenuAtDate(int date) {
		StringBuilder query = new StringBuilder();
		List<RarusMenu> result = new ArrayList<RarusMenu>();
		
		// query joining tables Menu and Dishes by dish ID
		query.append(TABLE_MENU).append(" AS MU INNER JOIN ").append(TABLE_DISHES);
		query.append(" AS DS ON MU.").append(MENU_DISH_ID).append(" = DS.").append(KEY_ID);
		
		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(false, query.toString(),
					new String[] {"MU." + KEY_ID, MENU_DATE, "DS." + KEY_ID, DISHES_NAME,
						DISHES_DESCRIPTION, DISHES_PORTIONED, DISHES_PRICE,
						DISHES_RATING, DISHES_PREORDER, MENU_AVAILABLE,
						MENU_AMMOUNT, MENU_MODIFIED, MENU_TIMESTAMP},
						MENU_DATE + " = ?", new String[] {Integer.toString(formatDate(date))},
						null, null, null, null);
			mDb.setTransactionSuccessful();
			
			if (c.moveToFirst()) {
				do {
					result.add(new RarusMenu(c.getInt(0), c.getInt(1), c.getString(2),
							c.getString(3), c.getString(4), (c.getInt(5) == 0 ? false : true), 
							c.getFloat(6), c.getString(7), (c.getInt(8) == 0 ? false : true),
							c.getFloat(9), c.getFloat(10), (c.getInt(11) == 0 ? false : true),
							c.getInt(12)));
				} while(c.moveToNext());
			}
			c.close();
		} finally {
			mDb.endTransaction();
		}
		return result;
	}
	
	/**
	 * Returns the timestamp of the menu at a given date
	 * 
	 * @param date
	 *     Unix time date
	 * @return
	 *     timestamp
	 */
	public int getMenuTimestamp(int date) {
		int result = 0;
		
		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(TABLE_MENU, new String[] {MENU_TIMESTAMP}, MENU_DATE + " = ?",
					new String[] {Integer.toString(formatDate(date))}, null, null, null, "1");
			mDb.setTransactionSuccessful();
			
			if (c.moveToFirst()) result = c.getInt(0);
			c.close();
		} finally {
			mDb.endTransaction();
		}
		return result;
	}
	
	/**
	 * Removes the menu at a given date
	 * 
	 * @param date
	 *     Unix time date
	 */
	public void deleteMenuAtDate(int date) {
		mDb.beginTransaction();
		try {
			mDb.delete(TABLE_MENU, MENU_DATE + " = ?",
					new String[] {Integer.toString(formatDate(date))});
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "[DB] Deleted menu at date " + Integer.toString(formatDate(date)));
		}
	}
	
	/**
	 * Removes all menus
	 */
	public void deleteMenuAll() {
		mDb.beginTransaction();
		try {
			mDb.delete(TABLE_MENU, null, null);
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "[DB] Deleted all menus");
		}
	}
	
	
	// Orders
	
	/**
	 * Adds orders
	 * 
	 * @param orders
	 *     {@link List} of {@link Order} objects
	 */
	public void addOrder(List<Order> orders) {
		StringBuilder query = new StringBuilder();
		SQLiteStatement insertOrderStmt;
		SQLiteStatement insertDishStmt;
		SQLiteStatement insertOrderHeadersStmt;
		int orderId = 0;
		
		// insert statement for inserting order
		query.append("INSERT INTO ").append(TABLE_ORDERS);
		query.append(" VALUES (?, ?, ?, ?, ?)");
		insertOrderStmt = mDb.compileStatement(query.toString());
		
		query = new StringBuilder();
		
		// insert statement for inserting dish
		query.append("INSERT INTO ").append(TABLE_DISHES).append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
		insertDishStmt = mDb.compileStatement(query.toString());
		
		query = new StringBuilder();
		
		// insert statement for inserting order headers
		query.append("INSERT INTO ").append(TABLE_ORDERS_HEADERS);
		query.append(" VALUES (?, ?, ?)");
		insertOrderHeadersStmt = mDb.compileStatement(query.toString());
		
		mDb.beginTransaction();
		try {
			for (int i = 0; i < orders.size(); i++) {
				// inserting order headers
				insertOrderHeadersStmt.bindNull(1);
				insertOrderHeadersStmt.bindString(2,
						Integer.toString(orders.get(i).getExecutionDate()));
				insertOrderHeadersStmt.bindString(3, orders.get(i).getOrderSrvNumber());
				
				// getting the order ID from the last inserted row
				orderId = (int) insertOrderHeadersStmt.executeInsert();
				
				// checking if dish is already in DB
				Cursor c = mDb.query(false, TABLE_DISHES, new String[] {KEY_ID}, KEY_ID + " = ?",
						new String[] {orders.get(i).getDishId()},
						null, null, null, null);
				
				if (c.getCount() == 0) {
					// if not - adding it
					insertDishStmt.bindString(1, orders.get(i).getDishId());
					insertDishStmt.bindString(2, orders.get(i).getName());
					insertDishStmt.bindString(3, orders.get(i).getDescription());
					insertDishStmt.bindString(4, (orders.get(i).isPortioned() ? "1" : "0"));
					insertDishStmt.bindString(5, Float.toString(orders.get(i).getPrice()));
					insertDishStmt.bindString(6, orders.get(i).getRating());
					insertDishStmt.bindString(7, (orders.get(i).isPreorder() ? "1" : "0"));
					insertDishStmt.execute();
				}
				c.close();
								
				// inserting order
				insertOrderStmt.bindNull(1);
				insertOrderStmt.bindString(2, Integer.toString(orderId));
				insertOrderStmt.bindString(3, orders.get(i).getDishId());
				insertOrderStmt.bindString(4, Float.toString(orders.get(i).getAmmount()));
				insertOrderStmt.bindString(5, Float.toString(orders.get(i).getSum()));
				insertOrderStmt.execute();
			}
			insertOrderHeadersStmt.close();
			insertDishStmt.close();
			insertOrderStmt.close();
			
		    mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "[DB] Added orders (" + Integer.toString(orders.size()) + ")");
		}
	}
	
	/**
	 * Returns a list of the dates on which there are orders
	 * 
	 * @return
	 *     {@link List} of {@link Integer} Unix time dates sorted in ascending order.
	 */
	public List<Integer> getOrdersDates() {
		List<Integer> result = new ArrayList<Integer>();
		
		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(true, TABLE_ORDERS_HEADERS,
					new String[] {ORDERS_H_EXECUTION_DATE},	null, null, null, null, null, null);
			mDb.setTransactionSuccessful();
			
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					result.add(c.getInt(0));
				} while(c.moveToNext());
			}
			c.close();
		} finally {
			mDb.endTransaction();
		}
		return result;
	}
	
	/**
	 * Returns all orders
	 * 
	 * @return
	 *     {@link List} of {@link Order} objects
	 */
	public List<Order> getOrdersAll() {
		StringBuilder query = new StringBuilder();
		List<Order> result = new ArrayList<Order>();
		
		// query joining tables Orders and Orders Headers by order ID
		// and joining table Dishes by dish ID
		query.append(TABLE_ORDERS).append(" AS OS INNER JOIN ").append(TABLE_ORDERS_HEADERS);
		query.append(" AS OH ON OS.").append(ORDERS_ORDER_ID).append(" = OH.").append(KEY_ID);
		query.append(" INNER JOIN ").append(TABLE_DISHES).append(" AS DS ON OS.");
		query.append(ORDERS_DISH_ID).append(" = DS.").append(KEY_ID);

		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(false, query.toString(),
					new String[] {"OS." + KEY_ID, "OS." + ORDERS_ORDER_ID, "DS." + KEY_ID,
						"DS." + DISHES_NAME, "DS." + DISHES_DESCRIPTION, "DS." + DISHES_PORTIONED,
						"DS." + DISHES_PRICE, "DS." + DISHES_RATING, "DS." + DISHES_PREORDER,
						"OS." + ORDERS_AMMOUNT, "OS." + ORDERS_SUM, "OH." + ORDERS_H_EXECUTION_DATE,
						"OH." + ORDERS_H_ORDER_SRV_NUMBER},
						null, new String[] {null},
						null, null, null, null);
			mDb.setTransactionSuccessful();
			
			if (c.moveToFirst()) {
				do {
					result.add(new Order(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3),
							c.getString(4), (c.getInt(5) == 0 ? false : true), c.getFloat(6),
							c.getString(7), (c.getInt(8) == 0 ? false : true), c.getFloat(9),
							c.getFloat(10), c.getInt(11), c.getString(12)));
				} while(c.moveToNext());
			}
			c.close();
		} finally {
			mDb.endTransaction();
		}
		return result;	
	}
	
	/**
	 * Returns orders at a given date
	 * 
	 * @param date
	 *     Unix time date
	 * @return
	 *     {@link List} of {@link Order} objects
	 */
	public List<Order> getOrdersAtDate(int date) {
		StringBuilder query = new StringBuilder();
		List<Order> result = new ArrayList<Order>();
		
		// query joining tables Orders and Orders Headers by order ID
		// and joining table Dishes by dish ID
		query.append(TABLE_ORDERS).append(" AS OS INNER JOIN ").append(TABLE_ORDERS_HEADERS);
		query.append(" AS OH ON OS.").append(ORDERS_ORDER_ID).append(" = OH.").append(KEY_ID);
		query.append(" INNER JOIN ").append(TABLE_DISHES).append(" AS DS ON OS.");
		query.append(ORDERS_DISH_ID).append(" = DS.").append(KEY_ID);

		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(false, query.toString(),
					new String[] {"OS." + KEY_ID, "OS." + ORDERS_ORDER_ID, "DS." + KEY_ID,
					"DS." + DISHES_NAME, "DS." + DISHES_DESCRIPTION, "DS." + DISHES_PORTIONED,
					"DS." + DISHES_PRICE, "DS." + DISHES_RATING, "DS." + DISHES_PREORDER,
					"OS." + ORDERS_AMMOUNT, "OS." + ORDERS_SUM, "OH." + ORDERS_H_EXECUTION_DATE,
					"OH." + ORDERS_H_ORDER_SRV_NUMBER}, ORDERS_H_EXECUTION_DATE + " = ?",
					new String[] {Integer.toString(formatDate(date))}, null, null, null, null);
			mDb.setTransactionSuccessful();
			
			if (c.moveToFirst()) {
				do {
					result.add(new Order(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3),
							c.getString(4), (c.getInt(5) == 0 ? false : true), c.getFloat(6),
							c.getString(7), (c.getInt(8) == 0 ? false : true), c.getFloat(9),
							c.getFloat(10), c.getInt(11), c.getString(12)));
				} while(c.moveToNext());
			}
			c.close();
		} finally {
			mDb.endTransaction();
		}
		return result;	
	}
	
	/**
	 * Returns not sent orders
	 * 
	 * @return
	 *     {@link List} of {@link RarusMenu} objects
	 */
	public List<RarusMenu> getOrdersNotSent() {
		StringBuilder query = new StringBuilder();
		List<RarusMenu> result = new ArrayList<RarusMenu>();
		
		// query joining tables Menu and Dishes by dish ID
		query.append(TABLE_MENU).append(" AS MU INNER JOIN ").append(TABLE_DISHES);
		query.append(" AS DS ON MU.").append(MENU_DISH_ID).append(" = DS.").append(KEY_ID);
		
		mDb.beginTransaction();
		try {
			// getting dishes with modified flag on ("1" which means "true")
			Cursor c = mDb.query(false, query.toString(),
					new String[] {"MU." + KEY_ID, MENU_DATE, "DS." + KEY_ID, DISHES_NAME,
						DISHES_DESCRIPTION, DISHES_PORTIONED, DISHES_PRICE,
						DISHES_RATING, DISHES_PREORDER, MENU_AVAILABLE,
						MENU_AMMOUNT, MENU_MODIFIED, MENU_TIMESTAMP},
						MENU_MODIFIED + " = ?", new String[] {"1"}, null, null, null, null);
			mDb.setTransactionSuccessful();
			
			if (c.moveToFirst()) {
				do {
					result.add(new RarusMenu(c.getInt(0), c.getInt(1), c.getString(2),
							c.getString(3), c.getString(4), (c.getInt(5) == 0 ? false : true), 
							c.getFloat(6), c.getString(7), (c.getInt(8) == 0 ? false : true),
							c.getFloat(9), c.getFloat(10), (c.getInt(11) == 0 ? false : true),
							c.getInt(12)));
				} while(c.moveToNext());
			}
			c.close();
		} finally {
			mDb.endTransaction();
		}
		return result;
	}
		
	/**
	 * Removes orders at a given date
	 *  
	 * @param date
	 *     Unix time date
	 */
	public void deleteOrdersAtDate(int date) {
		int count = 0;
		
		mDb.beginTransaction();
		try {			
			Cursor c = mDb.query(TABLE_ORDERS_HEADERS, new String[] {KEY_ID},
					ORDERS_H_EXECUTION_DATE + " = ?",
					new String[] {Integer.toString(formatDate(date))}, null, null, null);
			
			if (c.moveToFirst()) {
				// deleting order 
				mDb.delete(TABLE_ORDERS, ORDERS_ORDER_ID + " = ?",
						new String[] {Integer.toString(c.getInt(0))});
				// and order headers
				mDb.delete(TABLE_ORDERS_HEADERS, KEY_ID + " = ?",
						new String[] {Integer.toString(c.getInt(0))});
				count++;
			}
			c.close();
			
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "[DB] Deleted orders (" + count + ") at date ("
					+ formatDate(date) + ")");
		}
	}
	
	/**
	 * Removes all orders
	 */
	public void deleteOrdersAll() {
		mDb.beginTransaction();
		try {
			mDb.delete(TABLE_ORDERS, null, null);
			mDb.delete(TABLE_ORDERS_HEADERS, null, null);
			
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "[DB] Deleted all orders");
		}
	}
	
	
	/**
	 * Rounds the time specified in the Unix time format to 00:00
	 * 
	 * @param date
	 *   Unix time date
	 * @return
	 *    rounded date in Unix time format
	 */
	private int formatDate(int date) {
		return date = date - (date % 86400);
	}
}