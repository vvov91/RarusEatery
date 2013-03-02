package rarus.eatery.ui;

import java.util.ArrayList;
import java.util.List;

public class MenuList{
	private List<Integer> dataList;
	private List<MenuOnDate> dataMenuList;

	public MenuList() {
		dataList = new ArrayList<Integer>();
		dataMenuList = new ArrayList<MenuOnDate>();
	}

	public List<Integer> getAvalibleData() {
		return dataList;
	}

	public MenuOnDate getMenuByDate(Integer Data) {
		for (MenuOnDate menu : dataMenuList) {
			if (menu.getDate().equals(Data))
				return menu;
		}
		return null;
	}

	public void addMenuOnDate(MenuOnDate mD) {
		dataList.add(mD.getDate());
		dataMenuList.add(mD);
	}

	
}
