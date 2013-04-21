package rarus.eatery.database;

import java.util.List;

import rarus.eatery.model.RarusMenu;

import android.content.Context;

/**
 * Provides a wrapper interface to interact with the database
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class EateryDB {	
	// instance of an interface for working with database
	private final DBManager db; 
	
	/**
	 * Class constructor
	 * 
	 * @param context
	 *     current {@link Context}
	 */
	public EateryDB(Context context) {
		this.db = new DBManager(context);
	}
	
	
	// Menus
	
	/**
	 * Returns a list of the dates on which menu is available
	 * 
	 * @param date
	 *     Unix time date 
	 * @return
	 *     {@link List} of {@link RarusMenu} objects
	 */
	public List<Integer> getMenuDates() {
		db.open();
		List<Integer> result = db.getMenuDates();
		db.close();
		
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
	public List<RarusMenu> getMenu(int date) {
		db.open();
		List<RarusMenu> result = db.getMenuAtDate(date);
		db.close();
		
		return result;
	}	
	
	/**
	 * Saves the menu
	 * 
	 * @param menu
	 *     {@link List} of {@link RarusMenu} objects
	 */
	public void saveMenu(List<RarusMenu> menu) {
		if (menu.size() == 0) { 
			return;
		}
				
		db.open();
		db.deleteMenuAtDate(menu.get(0).getDate());
		db.addMenu(menu);
		db.deleteDishesUnused();
		db.close();
	}
	
	/**
	 * Returns not sent orders
	 * 
	 * @return
	 *     {@link List} of {@link RarusMenu} objects
	 */
	public List<RarusMenu> getOrdersNotSent() {
		db.open();
		List<RarusMenu> result = db.getOrdersNotSent();
		db.close();

		return result;
	}
	
	/**
	 * Removes all menus
	 */
	public void deleteMenu() {
		db.open();
		db.deleteMenuAll();
		db.close();
	}
}