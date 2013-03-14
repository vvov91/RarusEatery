package rarus.eatery.service;
/**
 * Класс для взаимодействия синхронной и асинхронной частей сервиса 
 *  
 * @author Dmitriy Bazunov <binnarywolf@gmail.com> *
 */
public class APIMessage {
	private int operarionCode;
	private Object messageContent;
	public APIMessage(int operationCode,Object messageContent) {
		this.operarionCode=operationCode;
		this.messageContent=messageContent;		
	}
	public int getCode(){ return operarionCode;}
	public Object getContent(){ return messageContent;}
	public void setContent(Object o){ messageContent=o;}
}
