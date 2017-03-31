package com.sparkonix.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonObject;

@Path("/privacypolicy")
public class PrivacyPolicyResource {

	JsonObject json = new JsonObject();
	private static final Logger log = Logger.getLogger(PrivacyPolicyResource.class.getName());

	@GET
	@Produces(MediaType.TEXT_HTML)
	public InputStream getPrivacyPolicy() throws IOException {

		File file = new File("PrivacyPolicy.html");
		log.log(Level.INFO, "In getPrivacyPolicy.");

		return new FileInputStream(file);
	}
}
