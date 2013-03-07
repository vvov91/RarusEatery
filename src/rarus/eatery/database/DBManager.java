package rarus.eatery.database;

import java.util.ArrayList;
import java.util.List;

import rarus.eatery.model.Menu;
import rarus.eatery.model.Dish;
import rarus.eatery.model.MenuItem;
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
 * Предоставляет интерфейс для работы с БД
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class DBManager extends SQLiteOpenHelper {
	private final String LOG_TAG = "rarus.eatery.db.dbmanager";
	
	
	// файл БД
    private static final String DB_NAME = "Eatery.db";
    // версия БД
    private static final int DB_VERSION = 1;
    
    
    // таблицы 
    private static final String TABLE_DISHES = "Dishes";
    private static final String TABLE_MENU = "Menu";
    private static final String TABLE_ORDERS = "Orders";
    private static final String TABLE_ORDERS_HEADERS = "OrdersHeaders";
    
    // имя id столбца в каждой таблице
    public static final String KEY_ID = "_id";
    
    // таблица DISHES
    private static final String DISHES_NAME = "name";
    private static final String DISHES_DESCRIPTION = "description";
    private static final String DISHES_PORTIONED = "portioned";
    private static final String DISHES_PRICE = "price";
    private static final String DISHES_RATING = "rating";
    private static final String DISHES_PREORDER = "preorder";
    
    // таблица MENU
    private static final String MENU_DATE = "date";
    private static final String MENU_DISH_ID = "dishID";
    private static final String MENU_AVAILABLE = "available";
    private static final String MENU_TIMESTAMP = "timestamp";
    
    // таблица ORDERS
    private static final String ORDERS_ORDER_ID = "orderID";
    private static final String ORDERS_DISH_ID = "dishID";
    private static final String ORDERS_AMMOUNT = "ammount";
    private static final String ORDERS_SUM = "sum";
    
    // таблица ORDERS HEADERS
    private static final String ORDERS_H_MENU_ID = "menuID";
    private static final String ORDERS_H_EXECUTE = "execute";
    private static final String ORDERS_H_EXECUTION_DATE = "executionDate";
    private static final String ORDERS_H_MODIFIED = "modified";
    private static final String ORDERS_H_TIMESTAMP = "timestamp";
    private static final String ORDERS_H_ORDER_SRV_NUMBER = "orderSrvNumber";
    
    
    // экземпляр БД
    private SQLiteDatabase mDb;

    
	/**
	 * Конструктор класса
	 * 
	 * @param context
	 *     текущий {@link Context}
	 */
	public DBManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	

	/**
	 * Открывает соединение с БД.
	 * Если открыть базу в режиме записи невозможно, база открывается в режиме чтения.
	 * 
	 * @throws SQLException
	 *     если БД не доступна для записи и открывает её в режиме чтения.
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
	 * Закрывает соединение с БД
	 */
	public void close() {
		mDb.close();
	}	
	
	// в случае если БД ещё не создана
	@Override
	public void onCreate(SQLiteDatabase _db) {
		StringBuilder query = new StringBuilder();
		
		// создаём таблицы
		// таблица DISHES
		query.append("CREATE TABLE ");
		query.append(TABLE_DISHES).append(" (").append(KEY_ID).append(" INTEGER PRIMARY KEY, ");
		query.append(DISHES_NAME).append(" TEXT NOT NULL, ");
		query.append(DISHES_DESCRIPTION).append(" TEXT NOT NULL, ");
		query.append(DISHES_PORTIONED).append(" INTEGER DEFAULT 0, ");
		query.append(DISHES_PRICE).append(" FLOAT NOT NULL, ");
		query.append(DISHES_RATING).append(" TEXT, ");
		query.append(DISHES_PREORDER).append(" INTEGER DEFAULT 0);");		
		_db.execSQL(query.toString());
		
		query = new StringBuilder();
		
		// таблица MENU
		query.append("CREATE TABLE ");
		query.append(TABLE_MENU).append(" (").append(KEY_ID);
		query.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		query.append(MENU_DATE).append(" INTEGER NOT NULL, ");
		query.append(MENU_DISH_ID).append(" INTEGER NOT NULL, ");
		query.append(MENU_AVAILABLE).append(" FLOAT NOT NULL DEFAULT -1, ");
		query.append(MENU_TIMESTAMP).append(" INTEGER);");
		_db.execSQL(query.toString());
		
		query = new StringBuilder();
		
		// таблица ORDERS
		query.append("CREATE TABLE ");
		query.append(TABLE_ORDERS).append(" (").append(KEY_ID);
		query.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		query.append(ORDERS_ORDER_ID).append(" INTEGER NOT NULL, ");
		query.append(ORDERS_DISH_ID).append(" INTEGER NOT NULL, ");
		query.append(ORDERS_AMMOUNT).append(" FLOAT DEFAULT 0, ");
		query.append(ORDERS_SUM).append(" FLOAT DEFAULT 0);");
		_db.execSQL(query.toString());
		
		query = new StringBuilder();
		
		// таблица ORDERS HEADERS
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

	// в случае если существующая БД не соответствует необходимой версии
	// и нуждается в обновлении
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	// Блюда
	
	/**
	 * Добавляет блюда
	 * 
	 * @param dishes
	 *     {@link List} из объектов {@link Dish}
	 */
	public void addDish(List<Dish> dishes) {
		StringBuilder query = new StringBuilder();	
		SQLiteStatement insertDishStmt;
		int skipped = 0;
				
		query.append("INSERT INTO ").append(TABLE_DISHES).append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
		insertDishStmt = mDb.compileStatement(query.toString());
		
		mDb.beginTransaction();
		try {
			for (int i = 0; i < dishes.size(); i++) {
				Cursor c = mDb.query(false, TABLE_DISHES, new String[] {KEY_ID}, KEY_ID + " = ?",
						new String[] {Integer.toString(dishes.get(i).getId())}, null, null, null,
						null);				
				if (c.getCount() > 0) {
					c.close();
					skipped++;
					continue;
				}			
				
				insertDishStmt.bindString(1, Integer.toString(dishes.get(i).getId()));
				insertDishStmt.bindString(2, dishes.get(i).getName());
				insertDishStmt.bindString(3, dishes.get(i).getDescription());
				insertDishStmt.bindString(4, (dishes.get(i).isPortioned() ? "1" : "0"));
				insertDishStmt.bindString(5, Float.toString(dishes.get(i).getPrice()));
				insertDishStmt.bindString(6, dishes.get(i).getRating());
				insertDishStmt.bindString(7, (dishes.get(i).isPreorder() ? "1" : "0"));
				insertDishStmt.execute();
			}
			insertDishStmt.close();
		    mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "Added dishes (" + Integer.toString(dishes.size() - skipped) + ")");			
		}		
	}
	
	/**
	 * Изменяет рейтинг блюда
	 * 
	 * @param id
	 *     id блюда
	 * @param rating
	 *     рейтинг
	 */
	public void setDishRating(int id, String rating) {
		ContentValues data = new ContentValues();
		data.put(DISHES_RATING, rating);
		
		mDb.beginTransaction();		
		try {
			mDb.update(TABLE_DISHES, data, KEY_ID + " = ?", new String[] {Integer.toString(id)});	    
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
	}

	/**
	 * Удаляет блюдо
	 * 
	 * @param dishId
	 *     id блюда
	 */
	public void deleteDish(int dishId) {
		mDb.beginTransaction();
		try {
			mDb.delete(TABLE_DISHES, KEY_ID + " = ?", new String[] {Integer.toString(dishId)});
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "Deleted dish (id = " + Integer.toString(dishId) + ")");
		}
	}
	
	/**
	 * Удаляет все блюда
	 */
	public void deleteDishAll() {
		mDb.beginTransaction();
		try {
			mDb.delete(TABLE_DISHES, null, null);
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "Deleted all dishes");
		}
	}
	
	
	// Меню
	
	/**
	 * Добавляет меню
	 * 
	 * @param menu
	 *     {@link List} из объектов {@link MenuItem}
	 */
	public void addMenu(List<MenuItem> menu) {
		StringBuilder query = new StringBuilder();
		SQLiteStatement insertMenuStmt;
		
		query.append("INSERT INTO ").append(TABLE_MENU).append(" VALUES (?, ?, ?, ?, ?)");
		insertMenuStmt = mDb.compileStatement(query.toString());

		mDb.beginTransaction();
		try {
			for (int i = 0; i < menu.size(); i++) {
				insertMenuStmt.bindNull(1);
				insertMenuStmt.bindString(2, Integer.toString(formatDate(menu.get(i).getDate())));
				insertMenuStmt.bindString(3, Integer.toString(menu.get(i).getDishId()));
				insertMenuStmt.bindString(4, Float.toString(menu.get(i).getAvailable()));
				insertMenuStmt.bindString(5, Integer.toString(menu.get(i).getTimestamp()));
				insertMenuStmt.execute();
			}
			insertMenuStmt.close();
		    mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "Added menu (" + Integer.toString(menu.size()) + ")");
		}
	}
	
	/**
	 * Вовзращает количество дат на которые доступно меню
	 * 
	 * @return
	 *     int с кол-вом дат
	 */
	public int getMenuDatesCount() {
		StringBuilder query = new StringBuilder();
		int result = 0;
		
		query.append("SELECT COUNT(DISTINCT ").append(MENU_DATE).append(") ");
		query.append("FROM ").append(TABLE_MENU);
		
		mDb.beginTransaction();
		try {
			Cursor c = mDb.rawQuery(query.toString(), new String[] {});
			mDb.setTransactionSuccessful();			

			c.moveToFirst();
			result = c.getInt(0);
			c.close();
		} finally {
			mDb.endTransaction();
		}
		return result;
	}
	
	/**
	 * Возвращает список дат на которые доступно меню
	 * 
	 * @return
	 *     {@link List} из {@link Integer} Unix time дат.
	 *     Даты в Unix time формате. Отсортированы по возрастанию.
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
				c.close();
			}
		} finally {
			mDb.endTransaction();
		}
		return result;
	}
	
	/**
	 * Возвращает количество блюд в меню на определенную дату
	 * 
	 * @param date
	 *     дата в Unix time формате
	 * @return
	 *     int кол-во блюд
	 */
	public int getMenuAtDateCount(int date) {
		StringBuilder query = new StringBuilder();
		int result = 0;
		
		query.append("SELECT COUNT(").append(MENU_DISH_ID).append(") FROM ");
		query.append(TABLE_MENU).append(" WHERE ").append(MENU_DATE).append(" = ?");
		
		mDb.beginTransaction();
		try {
			Cursor c = mDb.rawQuery(query.toString(),
					new String[] {Integer.toString(formatDate(date))});
			mDb.setTransactionSuccessful();			

			c.moveToFirst();
			result = c.getInt(0);
			c.close();
		} finally {
			mDb.endTransaction();
		}
		return result;
	}
	
	/**
	 * Возвращает меню на определенную дату
	 * 
	 * @param date
	 *     дата в Unix time формате
	 * @return
	 *     {@link List} из объектов {@link Menu}
	 */
	public List<Menu> getMenuAtDate(int date) {
		StringBuilder query = new StringBuilder();
		List<Menu> result = new ArrayList<Menu>();
		
		query.append(TABLE_MENU).append(" AS MU INNER JOIN ").append(TABLE_DISHES);
		query.append(" AS DS ON MU.").append(MENU_DISH_ID).append(" = DS.").append(KEY_ID);
		
		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(false, query.toString(),
					new String[] {"MU." + KEY_ID, "DS." + KEY_ID, DISHES_NAME,
						DISHES_DESCRIPTION, DISHES_PORTIONED, DISHES_PRICE, DISHES_RATING,
						DISHES_PREORDER, MENU_AVAILABLE, MENU_TIMESTAMP},
						MENU_DATE + " = ?", new String[] {Integer.toString(formatDate(date))},
						null, null, null, null);
			mDb.setTransactionSuccessful();
			
			if (c.moveToFirst()) {
				do {
					result.add(new Menu(c.getInt(0), formatDate(date), c.getInt(1),
							c.getString(2), c.getString(3), (c.getInt(4) == 0 ? false : true),
							c.getFloat(5), c.getString(6), (c.getInt(7) == 0 ? false : true),
							c.getFloat(8), c.getInt(9)));
				} while(c.moveToNext());
				c.close();
			}
		} finally {
			mDb.endTransaction();
		}
		return result;
	}
	
	/**
	 * Возвращает временной штамп меню на определенную дату
	 * 
	 * @param date
	 *     дата в Unix time формате
	 * @return
	 *     временной штамп
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
	 * Удаляет меню на определенную дату
	 * 
	 * @param date
	 *     дата в Unix time формате
	 */
	public void deleteMenuAtDate(int date) {
		mDb.beginTransaction();
		try {
			mDb.delete(TABLE_MENU, MENU_DATE + " = ?",
					new String[] {Integer.toString(formatDate(date))});
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "Deleted menu (date = " + Integer.toString(formatDate(date)) + ")");
		}
	}
	
	/**
	 * Удаляет все меню
	 */
	public void deleteMenuAll() {
		mDb.beginTransaction();
		try {
			mDb.delete(TABLE_MENU, null, null);
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "Deleted all menu");
		}
	}
	
	
	// Заказы
	
	/**
	 * Добавляет заказы
	 * 
	 * @param orders
	 *     {@link List} из объектов {@link Order}
	 */
	public void addOrder(List<Order> orders) {
		StringBuilder query = new StringBuilder();
		SQLiteStatement insertOrderStmt;
		SQLiteStatement insertOrderHeadersStmt;
		
		query.append("INSERT INTO ").append(TABLE_ORDERS);
		query.append(" VALUES (?, ?, ?, ?, ?)");
		insertOrderStmt = mDb.compileStatement(query.toString());
		
		query = new StringBuilder();
		
		query.append("INSERT INTO ").append(TABLE_ORDERS_HEADERS);
		query.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
		insertOrderHeadersStmt = mDb.compileStatement(query.toString());
		
		mDb.beginTransaction();
		try {
			for (int i = 0; i < orders.size(); i++) {
				insertOrderStmt.bindNull(1);
				insertOrderStmt.bindString(2, Integer.toString(orders.get(i).getOrderId()));
				insertOrderStmt.bindString(3, Integer.toString(orders.get(i).getDishId()));
				insertOrderStmt.bindString(4, Float.toString(orders.get(i).getAmmount()));
				insertOrderStmt.bindString(5, Float.toString(orders.get(i).getSum()));
				
				insertOrderHeadersStmt.bindNull(1);
				insertOrderHeadersStmt.bindString(2,
						Integer.toString(orders.get(i).getMenuId()));
				insertOrderHeadersStmt.bindString(3,
						(orders.get(i).isExecute() ? "1" : "0"));
				insertOrderHeadersStmt.bindString(4,
						Integer.toString(orders.get(i).getExecutionDate()));
				insertOrderHeadersStmt.bindString(5,
						(orders.get(i).isModified() ? "1" : "0"));
				insertOrderHeadersStmt.bindString(6,
						Integer.toString(orders.get(i).getTimestamp()));
				insertOrderHeadersStmt.bindString(7,
								Integer.toString(orders.get(i).getOrderSrvNumber()));
				

				insertOrderStmt.execute();
				insertOrderHeadersStmt.execute();
			}
			insertOrderStmt.close();
		    mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "Added orders (" + Integer.toString(orders.size()) + ")");
		}
	}
	
	/**
	 * Возвращает все заказы
	 * 
	 * @return
	 *     {@link List} из объектов {@link Order}
	 */
	public List<Order> getOrdersAll() {
		StringBuilder query = new StringBuilder();
		List<Order> result = new ArrayList<Order>();
		
		query.append(TABLE_ORDERS).append(" AS OS INNER JOIN ").append(TABLE_ORDERS_HEADERS);
		query.append(" AS OH ON OS.").append(ORDERS_ORDER_ID).append(" = OH.").append(KEY_ID);

		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(false, query.toString(),
					new String[] {"OS." + KEY_ID, "OS." + ORDERS_ORDER_ID, "OS." + ORDERS_DISH_ID,
						ORDERS_H_MENU_ID, ORDERS_H_EXECUTE, ORDERS_H_EXECUTION_DATE,
						ORDERS_H_MODIFIED, "OH." + ORDERS_H_TIMESTAMP, ORDERS_H_ORDER_SRV_NUMBER},
						null, new String[] {null},
						null, null, null, null);
			mDb.setTransactionSuccessful();
			
			if (c.moveToFirst()) {
				do {
					result.add(new Order(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3),
							(c.getInt(4) == 0 ? false : true), c.getInt(5),
							(c.getInt(6) == 0 ? false : true), c.getInt(7), c.getInt(8),
							c.getFloat(9), c.getFloat(10)));
				} while(c.moveToNext());
				c.close();
			}
		} finally {
			mDb.endTransaction();
		}
		return result;	
	}
	
	/**
	 * Возвращает заказы по id который указан у заказов на сервере  
	 * 
	 * @param srvNumber
	 *     id с сервера
	 * @return
	 *     {@link List} из объектов {@link Order}
	 */
	public List<Order> getOrdersBySrvNumber(int srvNumber) {
		StringBuilder query = new StringBuilder();
		List<Order> result = new ArrayList<Order>();
		
		query.append(TABLE_ORDERS).append(" AS OS INNER JOIN ").append(TABLE_ORDERS_HEADERS);
		query.append(" AS OH ON OS.").append(ORDERS_ORDER_ID).append(" = OH.").append(KEY_ID);

		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(false, query.toString(),
					new String[] {"OS." + KEY_ID, "OS." + ORDERS_ORDER_ID, "OS." + ORDERS_DISH_ID,
						ORDERS_H_MENU_ID, ORDERS_H_EXECUTE, ORDERS_H_EXECUTION_DATE,
						ORDERS_H_MODIFIED, "OH." + ORDERS_H_TIMESTAMP, ORDERS_H_ORDER_SRV_NUMBER},
						ORDERS_H_ORDER_SRV_NUMBER, new String[] {Integer.toString(srvNumber)},
						null, null, null, null);
			mDb.setTransactionSuccessful();
			
			if (c.moveToFirst()) {
				do {
					result.add(new Order(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3),
							(c.getInt(4) == 0 ? false : true), c.getInt(5),
							(c.getInt(6) == 0 ? false : true), c.getInt(7), c.getInt(8),
							c.getFloat(9), c.getFloat(10)));
				} while(c.moveToNext());
				c.close();
			}
		} finally {
			mDb.endTransaction();
		}
		return result;	
	}
	
	/**
	 * Возвращает ещё не отправленные заказы
	 *  
	 * @return
	 *     {@link List} из объектов {@link Order}
	 */
	public List<Order> getOrdersNotExecuted() {
		StringBuilder query = new StringBuilder();
		List<Order> result = new ArrayList<Order>();
		
		query.append(TABLE_ORDERS).append(" AS OS INNER JOIN ").append(TABLE_ORDERS_HEADERS);
		query.append(" AS OH ON OS.").append(ORDERS_ORDER_ID).append(" = OH.").append(KEY_ID);

		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(false, query.toString(),
					new String[] {"OS." + KEY_ID, "OS." + ORDERS_ORDER_ID, "OS." + ORDERS_DISH_ID,
						ORDERS_H_MENU_ID, ORDERS_H_EXECUTE, ORDERS_H_EXECUTION_DATE,
						ORDERS_H_MODIFIED, "OH." + ORDERS_H_TIMESTAMP, ORDERS_H_ORDER_SRV_NUMBER},
						ORDERS_H_EXECUTE, new String[] {"0"},
						null, null, null, null);
			mDb.setTransactionSuccessful();
			
			if (c.moveToFirst()) {
				do {
					result.add(new Order(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3),
							(c.getInt(4) == 0 ? false : true), c.getInt(5),
							(c.getInt(6) == 0 ? false : true), c.getInt(7), c.getInt(8),
							c.getFloat(9), c.getFloat(10)));
				} while(c.moveToNext());
				c.close();
			}
		} finally {
			mDb.endTransaction();
		}
		return result;	
	}
	
	/**
	 * Возвращает измененные заказы
	 * 
	 * @return
	 *     {@link List} из объектов {@link Order}
	 */
	public List<Order> getOrdersModified() {
		StringBuilder query = new StringBuilder();
		List<Order> result = new ArrayList<Order>();
		
		query.append(TABLE_ORDERS).append(" AS OS INNER JOIN ").append(TABLE_ORDERS_HEADERS);
		query.append(" AS OH ON OS.").append(ORDERS_ORDER_ID).append(" = OH.").append(KEY_ID);

		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(false, query.toString(),
					new String[] {"OS." + KEY_ID, "OS." + ORDERS_ORDER_ID, "OS." + ORDERS_DISH_ID,
						ORDERS_H_MENU_ID, ORDERS_H_EXECUTE, ORDERS_H_EXECUTION_DATE,
						ORDERS_H_MODIFIED, "OH." + ORDERS_H_TIMESTAMP, ORDERS_H_ORDER_SRV_NUMBER},
						ORDERS_H_MODIFIED, new String[] {"1"},
						null, null, null, null);
			mDb.setTransactionSuccessful();
			
			if (c.moveToFirst()) {
				do {
					result.add(new Order(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3),
							(c.getInt(4) == 0 ? false : true), c.getInt(5),
							(c.getInt(6) == 0 ? false : true), c.getInt(7), c.getInt(8),
							c.getFloat(9), c.getFloat(10)));
				} while(c.moveToNext());
				c.close();
			}
		} finally {
			mDb.endTransaction();
		}
		return result;	
	}
	
	/**
	 * Возвращает количество заказов на определенную дату
	 * 
	 * @param date
	 *     дата в Unix time формате
	 * @return
	 *     количество заказов
	 */
	public int getOrdersAtDateCount(int date) {
		StringBuilder query = new StringBuilder();
		int result = 0;
		
		query.append("SELECT COUNT(").append(ORDERS_ORDER_ID).append(") FROM ");
		query.append(TABLE_ORDERS).append(" AS OS INNER JOIN ").append(TABLE_ORDERS_HEADERS);
		query.append(" AS OH ON OS.").append(ORDERS_ORDER_ID).append(" = OH.").append(KEY_ID);
		query.append(" INNER JOIN ").append(TABLE_MENU).append(" AS MU ON OH.");
		query.append(ORDERS_H_MENU_ID).append(" = MU.").append(KEY_ID);
		query.append(" WHERE ").append(MENU_DATE).append(" = ?");
		
		mDb.beginTransaction();
		try {
			Cursor c = mDb.rawQuery(query.toString(),
					new String[] {Integer.toString(formatDate(date))});
			mDb.setTransactionSuccessful();			

			c.moveToFirst();
			result = c.getInt(0);
			c.close();
		} finally {
			mDb.endTransaction();
		}
		return result;
	}
	
	/**
	 * Возвращает заказы на определенную дату
	 * 
	 * @param date
	 *     дата в Unix time формате
	 * @return
	 *     {@link List} из объектов {@link Order}
	 */
	public List<Order> getOrdersAtDate(int date) {
		StringBuilder query = new StringBuilder();
		List<Order> result = new ArrayList<Order>();
		
		query.append(TABLE_ORDERS).append(" AS OS INNER JOIN ").append(TABLE_ORDERS_HEADERS);
		query.append(" AS OH ON OS.").append(ORDERS_ORDER_ID).append(" = OH.").append(KEY_ID);
		query.append(" INNER JOIN ").append(TABLE_MENU).append(" AS MU ON OH.");
		query.append(ORDERS_H_MENU_ID).append(" = MU.").append(KEY_ID);

		mDb.beginTransaction();
		try {
			Cursor c = mDb.query(false, query.toString(),
					new String[] {"OS." + KEY_ID, "OS." + ORDERS_ORDER_ID, "OS." + ORDERS_DISH_ID,
						ORDERS_H_MENU_ID, ORDERS_H_EXECUTE, ORDERS_H_EXECUTION_DATE,
						ORDERS_H_MODIFIED, "OH." + ORDERS_H_TIMESTAMP, ORDERS_H_ORDER_SRV_NUMBER},
						MENU_DATE + " = ?", new String[] {Integer.toString(formatDate(date))},
						null, null, null, null);
			mDb.setTransactionSuccessful();
			
			if (c.moveToFirst()) {
				do {
					result.add(new Order(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3),
							(c.getInt(4) == 0 ? false : true), c.getInt(5),
							(c.getInt(6) == 0 ? false : true), c.getInt(7), c.getInt(8),
							c.getFloat(9), c.getFloat(10)));
				} while(c.moveToNext());
				c.close();
			}
		} finally {
			mDb.endTransaction();
		}
		return result;	
	}
	
	/**
	 * Модифицирует значения заказов
	 * 
	 * @param orders
	 *     {@link List} из объектов {@link Order}
	 */
	public void editOrder(List<Order> orders) {
		ContentValues osData = new ContentValues();
		ContentValues ohData = new ContentValues();
		
		mDb.beginTransaction();
		try {
			for (int i = 0; i < orders.size(); i++) {
				osData.put(ORDERS_AMMOUNT, orders.get(i).getAmmount());
				osData.put(ORDERS_SUM, orders.get(i).getSum());
				
				ohData.put(ORDERS_H_EXECUTE, (orders.get(i).isExecute() ? "1" : "0"));
				ohData.put(ORDERS_H_EXECUTION_DATE, formatDate(orders.get(i).getExecutionDate()));
				ohData.put(ORDERS_H_MODIFIED, (orders.get(i).isModified() ? "1" : "0"));
				ohData.put(ORDERS_H_TIMESTAMP, orders.get(i).getTimestamp());
				ohData.put(ORDERS_H_ORDER_SRV_NUMBER, orders.get(i).getOrderSrvNumber());
				
				mDb.update(TABLE_ORDERS, osData, ORDERS_ORDER_ID + " = ?",
						new String[] {Integer.toString(orders.get(i).getOrderId())});
				mDb.update(TABLE_ORDERS_HEADERS, ohData, KEY_ID,
						new String[] {Integer.toString(orders.get(i).getOrderId())});
				
				osData = new ContentValues();
				ohData = new ContentValues();
			}
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "Modified orders (" + orders.size() + ")");
		}		
	}
	
	/**
	 * Удаляет все заказы
	 */
	public void deleteOrdersAll() {
		mDb.beginTransaction();
		try {
			mDb.delete(TABLE_ORDERS, null, null);
			mDb.delete(TABLE_ORDERS_HEADERS, null, null);
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			
			Log.i(LOG_TAG, "Deleted all orders and orders headers");
		}
	}
	
	
	/**
	 * Округляет время указанное в Unix time до 00:00
	 * 
	 * @param date
	 *    дата в Unix time формате
	 * @return
	 *    округлённая дата в Unix time формате
	 */
	private int formatDate(int date) {
		return date = date - (date % 86400);
	}
}