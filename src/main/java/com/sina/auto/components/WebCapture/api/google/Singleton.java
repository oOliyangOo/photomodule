package com.sina.auto.components.WebCapture.api.google;

public class Singleton{
	private volatile static Singleton singleton;
	private Singleton(){}
	public static Singleton getInstance(){
		if(singleton == null){
			synchronized(Singleton.class){
				if(singleton == null){
					singleton = new Singleton();
				}
			}
		}
		return singleton;
	}
}