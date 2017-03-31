package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.auth.JwtToken;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.hibernate.UnitOfWork;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

	private final UserDAO userDAO;
	private final Logger log = Logger.getLogger(LoginResource.class.getName());

	public LoginResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@POST
	@UnitOfWork
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserAuthentication(User user) {

		User userObj = null;
		try {
			userObj = userDAO.findUserByUsernameAndPassword(user.getEmail(), user.getPassword());
			if (userObj != null) {
				log.info("User found");
				User tempUser = new User();
				tempUser.setId(userObj.getId());
				tempUser.setName(userObj.getName());
				tempUser.setEmail(userObj.getEmail());
				tempUser.setAltEmail(userObj.getAltEmail());
				tempUser.setMobile(userObj.getMobile());
				tempUser.setCompanyDetailsId(userObj.getCompanyDetailsId());
				tempUser.setRole(userObj.getRole());
				tempUser.setNotificationType(userObj.getNotificationType());
				tempUser.setMetadata(userObj.getMetadata());

				String token = JwtToken.generateToken(userObj.getEmail(), tempUser);
				userObj.setToken(token);
				
				return Response.status(Status.OK).entity(JsonUtils.getJson(userObj)).build();

			} else {
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Username/Password is not valid")).build();
			}
		} catch (Exception e) {
			log.severe("Unable to find User. " + e);
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson(e.getMessage())).build();
		}
	}

}
