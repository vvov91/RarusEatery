package rarus.eatery.service;
/**
 * Интерфейс обработки результата запросов сервиса
 *  
 * @author Dmitriy Bazunov <binnarywolf@gmail.com>
 */
public interface ServiceRequestResult {
	public void onSuccessfullRequest();
	public void onUnSuccessfullRequest();
}
