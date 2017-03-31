package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.MachineSubscriptionHistoryDAO;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/machinesubscriptionhistory/{subHistoryId}")
@Produces(MediaType.APPLICATION_JSON)
public class MachineSubscriptionHistoryResource {

	private final MachineSubscriptionHistoryDAO machineSubscriptionHistoryDAO;
	private final Logger log = Logger.getLogger(MachineSubscriptionHistoryResource.class.getName());

	public MachineSubscriptionHistoryResource(MachineSubscriptionHistoryDAO machineSubscriptionHistoryDAO) {
		this.machineSubscriptionHistoryDAO = machineSubscriptionHistoryDAO;
	}

	@GET
	@UnitOfWork
	public Response getMachineSubscriptionHistoryById(@Auth User authUser,
			@PathParam("subHistoryId") long subHistoryId) {
		try {
			log.info(" In getMachineSubscriptionHistoryById");
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(machineSubscriptionHistoryDAO.getById(subHistoryId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Machine Subscription History " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find  Machine Subscription History")).build();
		}
	}

}
