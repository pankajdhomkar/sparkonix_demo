package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

	private final UserDAO userDAO;
	private final Logger log = Logger.getLogger(UsersResource.class.getName());

	public UsersResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@POST
	@UnitOfWork
	public Response createUser(@Auth User authUser, User user) throws Exception {
		User dbUser = userDAO.findByEmail(user.getEmail());
		try {
			if (dbUser == null) {
				return Response.status(Status.OK).entity(userDAO.save(user)).build();
			} else {
				log.severe("User not created, Username/Email already exist.");
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Username/Email already exist.")).build();
			}
		} catch (Exception e) {
			log.severe("Failed to create user. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Failed to create User.")).build();
		}

	}

	@GET
	@UnitOfWork
	public Response listUsers(@Auth User authUser) {
		try {
			log.info(" In listUsers");
			return Response.status(Status.OK).entity(JsonUtils.getJson(userDAO.findAll())).build();
		} catch (Exception e) {
			log.severe("Unable to find Users " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Users")).build();
		}
	}

	@GET
	@Path("/{role}")
	@UnitOfWork
	public Response listUsersByRole(@Auth User authUser, @PathParam("role") String role) {
		try {
			log.info(" In listUsersByRole");
			return Response.status(Status.OK).entity(JsonUtils.getJson(userDAO.findByRole(role))).build();
		} catch (Exception e) {
			log.severe("Unable to find users by given role" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find users by given role")).build();
		}
	}

	@GET
	@Path("/{role}/{companyDetailsId}")
	@UnitOfWork
	public Response listUsersByRoleAndCompanyDetailsId(@Auth User authUser, @PathParam("role") String role,
			@PathParam("companyDetailsId") long companyDetailsId) {
		try {
			log.info(" In listUsersByRoleAndCompanyDetailsId");
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(userDAO.findAllByRoleCompanyDetailsId(role, companyDetailsId))).build();
		} catch (Exception e) {
			log.severe("Unable to find uses by given role and comany details id" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find users by given role & comany details id")).build();
		}
	}
}
