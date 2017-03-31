package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.SubscriptionHistoryDAO;
import com.sparkonix.entity.SubscriptionHistory;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/subscriptionhistories")
@Produces(MediaType.APPLICATION_JSON)
public class SubscriptionHistoriesResource {

	private final SubscriptionHistoryDAO subscriptionHistoryDAO;
	private final Logger log = Logger.getLogger(SubscriptionHistoriesResource.class.getName());

	public SubscriptionHistoriesResource(SubscriptionHistoryDAO subscriptionHistoryDAO) {
		this.subscriptionHistoryDAO = subscriptionHistoryDAO;
	}

	@POST
	@UnitOfWork
	public SubscriptionHistory createSubscriptionHistory(@Auth User authUser, SubscriptionHistory subscriptionHistory) throws Exception {
		return subscriptionHistoryDAO.save(subscriptionHistory);
	}

	@GET 
	@UnitOfWork
	public Response listSubscriptionHistories(@Auth User authUser) {
		try {
			log.info(" In listSubscriptionHistories");
			return Response.status(Status.OK).entity(JsonUtils.getJson(subscriptionHistoryDAO.findAll())).build();
		} catch (Exception e) {
			log.severe("Unable to find Subscription Histories " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Subscription Histories")).build();
		}
	}

}
