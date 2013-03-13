package rarus.eatery.database;

import java.util.List;

import rarus.eatery.model.RarusMenu;
import rarus.eatery.model.Order;

import android.content.Context;

/**
 * ������������� �������� ���������� ��� ������ � ��
 * 
 * @author Victor Vovchenko <v.vovchenko91@gmail.com>
 *
 */
public class EateryDB {	
	// ��������� ���������� ��� ������ � ��
	private final DBManager db; 
	
	/**
	 * ����������� ������
	 * 
	 * @param context
	 *     ������� {@link Context}
	 */
	public EateryDB(Context context) {
		this.db = new DBManager(context);
	}
	
	
	// ����
	
	/**
	 * ���������� ������ ��� �� ������� �������� ����
	 * 
	 * @param date
	 *     ���� � Unix time �������
	 * @return
	 *     {@link List} �� �������� {@link RarusMenu}
	 */
	public List<Integer> getMenuDates() {
		db.open();
		List<Integer> result = db.getMenuDates();
		db.close();
		
		return result;
	}
	
	/**
	 * ���������� ���� �� ������������ ����
	 * 
	 * @param date
	 *     ���� � Unix time �������
	 * @return
	 *     {@link List} �� �������� {@link RarusMenu}
	 */
	public List<RarusMenu> getMenu(int date) {
		db.open();
		List<RarusMenu> result = db.getMenuAtDate(date);
		db.close();
		
		return result;
	}	
	
	/**
	 * ��������� ����
	 * 
	 * @param menu
	 *     {@link List} �� �������� {@link RarusMenu}
	 */
	public void saveMenu(List<RarusMenu> menu) {
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
	
	/**
	 * ������� ��� ��������� ����
	 */
	public void deleteMenu() {
		db.open();
		db.deleteMenuAll();
		db.close();
	}
	
	
	// ������
	
	/**
	 * ���������� ������ ��� �� ������� ������� ������
	 * 
	 * @param date
	 *     ���� � Unix time �������
	 * @return
	 *     {@link List} �� �������� {@link RarusMenu}
	 */
	public List<Integer> getOrdersDates() {
		db.open();
		List<Integer> result = db.getOrdersDates();
		db.close();
		return result;
	}
	
	/**
	 * ���������� ������ �� ������������ ����
	 * 
	 * @param date
	 *     ���� � Unix time �������
	 * @return
	 *     {@link List} �� �������� {@link Order}
	 */
	public List<Order> getOrders(int date) {
		db.open();
		List<Order> result = db.getOrdersAtDate(date);
		db.close();
		
		return result;
	}
	
	/**
	 * ���������� ��� �� ������������ ������
	 * 
	 * @return
	 *     {@link List} �� �������� {@link RarusMenu}
	 */
	public List<RarusMenu> getOrdersNotExecuted() {
		db.open();
		List<RarusMenu> result = db.getOrdersNotExecuted();
		db.close();
		
		return result;
	}
		
	/**
	 * ��������� ������
	 * 
	 * @param orders
	 *     {@link List} �� �������� {@link Order}
	 */
	public void saveOrders(List<Order> orders) {
		if (orders.size() == 0) return;
		
		db.open();
		db.deleteOrdersAll();
		db.addOrder(orders);
		db.close();
	}
	
	/**
	 * ������� ��� ��������� ������
	 */
	public void deleteOrders() {
		db.open();
		db.deleteOrdersAll();
		db.close();
	}
}