package rarus.eatery.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
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
		query.append(DISHES_RATING).append(" TEXT DEFAULT 0, ");
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
}