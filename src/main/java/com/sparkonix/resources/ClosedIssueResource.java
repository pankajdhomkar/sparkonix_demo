package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.ClosedIssueDAO;
import com.sparkonix.entity.ClosedIssue;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * This class provide info of closed issues/complaints by id
 * 
 * @author Satyawan
 */
@Path("/closedissue/{closedIssueId}")
@Produces(MediaType.APPLICATION_JSON)
public class ClosedIssueResource {

	private final ClosedIssueDAO closedIssueDAO;
	private final Logger log = Logger.getLogger(ClosedIssueResource.class.getName());

	public ClosedIssueResource(ClosedIssueDAO closedIssueDAO) {
		this.closedIssueDAO = closedIssueDAO;
	}

	/**
	 * @param authUser 
	 * @param closedIssueId
	 * @return {@link Response} as {@link ClosedIssue}
	 */
	@GET
	@UnitOfWork
	public Response getClosedIssueById(@Auth User authUser, @PathParam("closedIssueId") long closedIssueId) {
		try {
			log.info(" In getClosedIssueById");
			return Response.status(Status.OK).entity(JsonUtils.getJson(closedIssueDAO.getById(closedIssueId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Closed Issue " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Closed Issue"))
					.build();
		}
	}

}
