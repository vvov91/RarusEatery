package rarus.eatery.database;

import java.util.List;

import rarus.eatery.model.Menu;

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
	 * ���������� ���� �� ������������ ����
	 * 
	 * @param date
	 *     ���� � Unix time �������
	 * @return
	 *     {@link List} �� �������� {@link Menu}
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
	 *     {@link List} �� �������� {@link Menu}
	 */
	public List<Menu> getMenu(int date) {
		db.open();
		List<Menu> result = db.getMenuAtDate(date);
		db.close();
		
		return result;
	}	
	
	/**
	 * ��������� ���� �� ������������ ����
	 * 
	 * @param menu
	 *     {@link List} �� �������� {@link Menu}
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