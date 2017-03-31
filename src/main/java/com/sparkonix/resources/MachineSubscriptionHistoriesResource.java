package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.MachineSubscriptionHistoryDAO;
import com.sparkonix.entity.MachineSubscriptionHistory;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/machinesubscriptionhistories")
@Produces(MediaType.APPLICATION_JSON)
public class MachineSubscriptionHistoriesResource {

	private final MachineSubscriptionHistoryDAO machineSubscriptionHistoryDAO;
	private final Logger log = Logger.getLogger(MachineSubscriptionHistoriesResource.class.getName());

	public MachineSubscriptionHistoriesResource(MachineSubscriptionHistoryDAO machineSubscriptionHistoryDAO) {
		this.machineSubscriptionHistoryDAO = machineSubscriptionHistoryDAO;
	}

	@POST
	@UnitOfWork
	public MachineSubscriptionHistory createSubscriptionHistory(@Auth User authUser,
			MachineSubscriptionHistory machineSubscriptionHistory) throws Exception {
		return machineSubscriptionHistoryDAO.save(machineSubscriptionHistory);
	}

	@GET
	@UnitOfWork
	public Response listMachineSubscriptionHistories(@Auth User authUser) {
		try {
			log.info(" In listMachineSubscriptionHistories");
			return Response.status(Status.OK).entity(JsonUtils.getJson(machineSubscriptionHistoryDAO.findAll()))
					.build();
		} catch (Exception e) {
			log.severe("Unable to find Machines Subscription Histories " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Machines Subscription Histories")).build();
		}
	}

}
