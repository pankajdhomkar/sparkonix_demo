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

@Path("/termscondition")
public class TermsConditionResource {

	JsonObject json = new JsonObject();
	private static final Logger log = Logger.getLogger(TermsConditionResource.class.getName());

	@GET
	@Produces(MediaType.TEXT_HTML)
	public InputStream getTermsAndCondition() throws IOException {

		File file = new File("TermsOfService.html");
		log.log(Level.INFO, "In getTermsAndCondition.");

		return new FileInputStream(file);
	}
}
