package rarus.eatery.service;

import java.util.List;

import rarus.eatery.model.Dish;
import rarus.eatery.model.MenuItem;

public class ServiceList {
	private List<MenuItem> menu;
	private List<Dish>	dishes;
	public ServiceList(List<MenuItem> menu,List<Dish>	dishes){
		this.menu=menu;
		this.dishes=dishes;
	}
	public List<MenuItem> getMenu(){ return menu;}
	public List<Dish> getDishes(){ return dishes;}
}
