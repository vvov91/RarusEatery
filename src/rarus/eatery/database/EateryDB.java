package rarus.eatery.database;

import java.util.List;

import rarus.eatery.model.Menu;

import android.content.Context;

/**
 * Предоставляет оболочку интерфейса для работы с БД
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class EateryDB {	
	// экземпляр интерфейса для работы с БД
	private final DBManager db; 
	
	/**
	 * Конструктор класса
	 * 
	 * @param context
	 *     текущий {@link Context}
	 */
	public EateryDB(Context context) {
		this.db = new DBManager(context);
	}
	
	
	// Меню
	
	/**
	 * Возвращает меню на определенную дату
	 * 
	 * @param date
	 *     дата в Unix time формате
	 * @return
	 *     {@link List} из объектов {@link Menu}
	 */
	public List<Integer> getMenuDates() {
		db.open();
		List<Integer> result = db.getMenuDates();
		db.close();
		
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
	public List<Menu> getMenu(int date) {
		db.open();
		List<Menu> result = db.getMenuAtDate(date);
		db.close();
		
		return result;
	}	
	
	/**
	 * Сохраняет меню на определенную дату
	 * 
	 * @param menu
	 *     {@link List} из объектов {@link Menu}
	 */
	public void saveMenu(List<Menu> menu) {
		if(menu.size() == 0) return;
				
		db.open();
		for (int i = 0; i < menu.size(); i++) {
			if (menu.get(i).isModified()) {
				db.deleteMenuAtDate(menu.get(0).getDate());
				db.addMenu(menu);
				
				break;
			}
		}
		db.close();
	}
}