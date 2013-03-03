package rarus.eatery.service;

import java.util.List;

import rarus.eatery.model.Dish;
import rarus.eatery.model.Menu;

public class ServiceList {
	private List<Menu> menu;
	private List<Dish>	dishes;
	public ServiceList(List<Menu> menu,List<Dish>	dishes){
		this.menu=menu;
		this.dishes=dishes;
	}
	public List<Menu> getMenu(){ return menu;}
	public List<Dish> getDishes(){ return dishes;}
}
