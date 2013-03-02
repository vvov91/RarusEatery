package rarus.eatery.ui;

import java.util.ArrayList;
import java.util.List;

import rarus.eatery.model.DayMenu;
import rarus.eatery.model.Dish;

public class MenuOnDate {
	private Integer date;
	private List<DayMenu> dishes;
	public MenuOnDate(Integer date){
		this.date=date;
		dishes=new ArrayList<DayMenu>();
		
	}
	public Integer getDate(){
		return date;
	}
	public List<DayMenu> getDishes(){
		return dishes;
	}
	public void addDish(DayMenu d){
		d.setDate(date);
		dishes.add(d);
	}
	public void addDishList(List<DayMenu> list){
		dishes.addAll(list);
	}
	
	public String toString(){
		StringBuilder sb=new StringBuilder("Date: ");
		sb.append(date);
		sb.append("\n");
		for(Dish d:dishes){
			sb.append(d.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
