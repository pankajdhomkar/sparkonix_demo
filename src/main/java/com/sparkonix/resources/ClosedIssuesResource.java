package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

@Path("/closedissues")
@Produces(MediaType.APPLICATION_JSON)
public class ClosedIssuesResource {

	private final ClosedIssueDAO closedIssuesDAO;
	private final Logger log = Logger.getLogger(ClosedIssuesResource.class.getName());

	public ClosedIssuesResource(ClosedIssueDAO closedIssuesDAO) {
		this.closedIssuesDAO = closedIssuesDAO;
	}

	@POST
	@UnitOfWork
	public ClosedIssue createClosedIssue(@Auth User authUser, ClosedIssue closedIssue) throws Exception {
		return closedIssuesDAO.save(closedIssue);
	}

	@GET
	@UnitOfWork
	public Response listClosedIssues(@Auth User authUser) {
		try {
			log.info(" In listClosedIssues");
			return Response.status(Status.OK).entity(JsonUtils.getJson(closedIssuesDAO.findAll())).build();
		} catch (Exception e) {
			log.severe("Unable to find Closed Issues " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Closed Issues"))
					.build();
		}
	}

}
