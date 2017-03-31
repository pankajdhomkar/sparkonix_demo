package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.MachineAmcHistoryDAO;
import com.sparkonix.entity.MachineAmcHistory;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/machineamchistories")
@Produces(MediaType.APPLICATION_JSON)
public class MachineAmcHistoriesResource {

	private final MachineAmcHistoryDAO machineAmcHistoryDAO;
	private final Logger log = Logger.getLogger(MachineAmcHistoriesResource.class.getName());

	public MachineAmcHistoriesResource(MachineAmcHistoryDAO machineAmcHistoryDAO) {
		this.machineAmcHistoryDAO = machineAmcHistoryDAO;
	}

	@POST
	@UnitOfWork
	public MachineAmcHistory createAmcHistory(@Auth User authUser,
			MachineAmcHistory machineAmcHistory) throws Exception {
		return machineAmcHistoryDAO.save(machineAmcHistory);
	}

	@GET 
	@UnitOfWork
	public Response listMachineAmcHistories(@Auth User authUser) {
		try {
			log.info(" In listMachineAmcHistories");
			return Response.status(Status.OK).entity(JsonUtils.getJson(machineAmcHistoryDAO.findAll()))
					.build();
		} catch (Exception e) {
			log.severe("Unable to find Machines AMC Histories " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Machines AMC Histories")).build();
		}
	}

}
