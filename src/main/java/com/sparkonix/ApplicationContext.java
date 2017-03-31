package com.sparkonix;

public class ApplicationContext {

	private static ApplicationContext instance = null;
	private WebConfiguration config;

	private ApplicationContext(WebConfiguration config) {
		this.config = config;
	}

	public static ApplicationContext init(WebConfiguration config) {
		instance = new ApplicationContext(config);
		return getInstance();
	}

	public static ApplicationContext getInstance() {
		if (instance == null) {
			throw new RuntimeException("Application Context is not initialized !");
		}

		return instance;
	}

	public WebConfiguration getConfig() {
		return config;
	}

}
