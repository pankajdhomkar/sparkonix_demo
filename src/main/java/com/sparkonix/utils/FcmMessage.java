package com.sparkonix.utils;

import com.google.gson.JsonObject;

public class FcmMessage {

	private String to;
	private Notification notification;
	private JsonObject data;
	private String[] registration_ids;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public JsonObject getData() {
		return data;
	}

	public void setData(JsonObject data) {
		this.data = data;
	}

	public String[] getRegistration_ids() {
		return registration_ids;
	}

	public void setRegistration_ids(String[] registration_ids) {
		this.registration_ids = registration_ids;
	}

	public class Notification {
		private String title;
		private String body;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

	}

}
