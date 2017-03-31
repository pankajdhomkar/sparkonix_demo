package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.MachineAmcHistoryDAO;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/machineamchistory/{amcHistoryId}")
@Produces(MediaType.APPLICATION_JSON)
public class MachineAmcHistoryResource {

	private final MachineAmcHistoryDAO machineAmcHistoryDAO;
	private final Logger log = Logger.getLogger(MachineAmcHistoryResource.class.getName());

	public MachineAmcHistoryResource(MachineAmcHistoryDAO machineAmcHistoryDAO) {
		this.machineAmcHistoryDAO = machineAmcHistoryDAO;
	}

	@GET
	@UnitOfWork
	public Response getMachineAmcHistoryById(@Auth User authUser,
			@PathParam("amcHistoryId") long amcHistoryId) {
		try {
			log.info(" In getMachineAmcHistoryById");
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(machineAmcHistoryDAO.getById(amcHistoryId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Machine AMC History " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find  Machine AMC History")).build();
		}
	}

}
