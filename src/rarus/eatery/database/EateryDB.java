package rarus.eatery.database;

import java.util.List;

import rarus.eatery.model.Menu;
import rarus.eatery.model.Order;

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
	 * Возвращает список дат на которые доступно меню
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
	 * Сохраняет меню
	 * 
	 * @param menu
	 *     {@link List} из объектов {@link Menu}
	 */
	public void saveMenu(List<Menu> menu) {
		if (menu.size() == 0) return;
				
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
	
	
	// Заказы
	
	/**
	 * Возвращает список дат на которые имеются заказы
	 * 
	 * @param date
	 *     дата в Unix time формате
	 * @return
	 *     {@link List} из объектов {@link Menu}
	 */
	public List<Integer> getOrdersDates() {
		db.open();
		List<Integer> result = db.getOrdersDates();
		db.close();
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
	public List<Order> getOrders(int date) {
		db.open();
		List<Order> result = db.getOrdersAtDate(date);
		db.close();
		
		return result;
	}
		
	/**
	 * Сохраняет заказы
	 * 
	 * @param orders
	 *     {@link List} из объектов {@link Order}
	 */
	public void saveOrders(List<Order> orders) {
		if (orders.size() == 0) return;
		
		db.open();
		db.deleteOrdersAll();
		db.addOrder(orders);
		db.close();
	}
}