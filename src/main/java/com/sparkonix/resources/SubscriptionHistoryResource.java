package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.SubscriptionHistoryDAO;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/subscriptionhistory/{subHistoryId}")
@Produces(MediaType.APPLICATION_JSON)
public class SubscriptionHistoryResource {

	private final SubscriptionHistoryDAO subscriptionHistoryDAO;
	private final Logger log = Logger.getLogger(SubscriptionHistoryResource.class.getName());

	public SubscriptionHistoryResource(SubscriptionHistoryDAO subscriptionHistoryDAO) {
		this.subscriptionHistoryDAO = subscriptionHistoryDAO;
	}

	@GET
	@UnitOfWork
	public Response getSubscriptionHistoryById(@Auth User authUser, @PathParam("subHistoryId") long subHistoryId) {
		try {
			log.info(" In getSubscriptionHistoryById");
			return Response.status(Status.OK).entity(JsonUtils.getJson(subscriptionHistoryDAO.getById(subHistoryId)))
					.build();
		} catch (Exception e) {
			log.severe("Unable to find Subscription History " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find  Subscription History")).build();
		}
	}

}
