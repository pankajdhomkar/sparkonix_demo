package com.sparkonix.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Logger;

import com.google.gson.JsonObject;
import com.sparkonix.ApplicationContext;

public class SendSMS implements Runnable {

	private String toMobileNumber;
	private String smsMessage;
	private final Logger log = Logger.getLogger(SendSMS.class.getName());

	public SendSMS(JsonObject jsonObject) {
		this.toMobileNumber = jsonObject.get("toMobileNumber").getAsString();
		this.smsMessage = jsonObject.get("smsMessage").getAsString();
	}

	@Override
	public void run() {
		log.info("sending sms");		
		String senderId = ApplicationContext.getInstance().getConfig().getSmsSenderId();
		String smsApiKey = ApplicationContext.getInstance().getConfig().getSmsApiKey();
		String smsApiUrl = ApplicationContext.getInstance().getConfig().getSmsApiUrl();
		String flashsms = "0";
		String channel = "2"; // Promotional = 1 or Transactional = 2
		String dcs="0"; //DCS=0 For English, DCS=8 For Unicode (hindi,marathi,punjabi etc.)
		String route="1";
		
		try {
			// Construct data
			String data = smsApiUrl 
					+ "/api/mt/SendSMS?"
					+ "APIKey="+URLEncoder.encode(smsApiKey, "UTF-8")
					+ "&senderid="+URLEncoder.encode(senderId, "UTF-8")
					+ "&channel="+URLEncoder.encode(channel, "UTF-8")
					+ "&DCS="+URLEncoder.encode(dcs, "UTF-8")
					+ "&flashsms="+URLEncoder.encode(flashsms, "UTF-8")
					+ "&number="+URLEncoder.encode(toMobileNumber, "UTF-8")
					+ "&text="+URLEncoder.encode(smsMessage, "UTF-8")
					+ "&route="+URLEncoder.encode(route, "UTF-8");
			
			URL url = new URL(data);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			String sResult = "";
			while ((line = rd.readLine()) != null) {
				// Process line...
				sResult = sResult + line + " ";
			}
			rd.close();
			log.info("SMS has been sent successfully");
			// log.info(sResult);
		} catch (Exception e) {
			log.severe("SMS sending failed. Reason: " + e.getMessage());
		}
	}
}
