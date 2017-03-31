package com.sparkonix.resources;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonObject;
import com.sparkonix.dao.ResetPasswordTokenDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.PasswordResetToken;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.ForgotPasswordDTO;
import com.sparkonix.entity.dto.ResetPasswordCheckDTO;
import com.sparkonix.entity.dto.ResetPasswordDTO;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.MailUtils;
import com.sparkonix.utils.SendMail;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private final UserDAO userDAO;
	private final ResetPasswordTokenDAO passwordTokenDao;
	private final Logger log = Logger.getLogger(UserResource.class.getName());

	public UserResource(UserDAO userDAO, ResetPasswordTokenDAO passwordTokenDao) {
		this.userDAO = userDAO;
		this.passwordTokenDao = passwordTokenDao;
	}

	@GET
	@UnitOfWork
	@Path("/{userId}")
	public Response getUserById(@Auth User authUser, @PathParam("userId") long userId) {
		try {
			log.info(" In getUserById");
			return Response.status(Status.OK).entity(JsonUtils.getJson(userDAO.getById(userId))).build();
		} catch (Exception e) {
			log.severe("Unable to find User " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find User")).build();
		}
	}

	@DELETE
	@UnitOfWork
	@Path("/{userId}")
	public Response deleteUser(@Auth User authUser, @PathParam("userId") long userid) {
		try {
			log.info(" In deleteUser");
			userDAO.delete(userid);
			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("User Deleted Successfully")).build();
		} catch (Exception e) {
			log.severe("Unable to Delete User " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to Delete User")).build();
		}
	}

	@PUT
	@UnitOfWork
	@Path("/{userId}")
	public Response updateUserById(@Auth User authUser, @PathParam("userId") long userId, User userobj) {
		try {
			log.info(" In updateUserById");
			userDAO.save(userobj);
			return Response.status(Status.OK).entity(userDAO.save(userobj)).build();
		} catch (Exception e) {
			log.severe("Unable to Update  " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to Update ")).build();
		}
	}

	@PUT
	@UnitOfWork
	@Path("/resetpassword")
	public Response resetPassword(@Auth User authUser, ResetPasswordDTO resetPasswordDTO) {
		try {
			log.info(" In resetPassword");

			if (authUser == null) {
				return Response.status(Status.UNAUTHORIZED).build();
			}

			User dbUser = userDAO.getById(resetPasswordDTO.getUserId());
			if (dbUser == null) {
				// user not found
				log.severe("User not found");
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("User not found")).build();

			} else {

				if (dbUser.getPassword().equals(resetPasswordDTO.getOldPassword())) {
					log.info("User found");
					dbUser.setPassword(resetPasswordDTO.getNewPassword());
					userDAO.save(dbUser);

					log.info("Password updated successfully");

					return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Password updated successfully"))
							.build();
				} else {
					// old pass is not valid
					log.severe("Incorrect old password");
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Incorrect old password"))
							.build();
				}

			}
		} catch (Exception e) {
			log.severe("Unable to reset password. Error: " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to reset password"))
					.build();
		}
	}

	@POST
	@UnitOfWork
	@Path("/forgotpassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resetPassword(ForgotPasswordDTO forgotPasswordDTO) {
		User user = userDAO.findByEmail(forgotPasswordDTO.getEmail());
		if (user == null) {
			return Response.status(Status.UNAUTHORIZED).entity(JsonUtils.getErrorJson("User does not exists")).build();
		} else {
			PasswordResetToken token = createPasswordResetTokenForUser(user, UUID.randomUUID().toString());
			sendResetPwdEmail(token, user);
			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Email sent to your email id for resetting password")).build();
		}
	}

	private void sendResetPwdEmail(PasswordResetToken token, User user) {
		JsonObject mailJson = MailUtils.getResetPasswordMail(token, user);
		new Thread(new SendMail(mailJson)).start();
	}

	public PasswordResetToken createPasswordResetTokenForUser(User user, String token) {
		PasswordResetToken myToken = new PasswordResetToken(token);
		try {
			myToken.setEmail(user.getEmail());
			myToken.setExpiryDate(new Date(System.currentTimeMillis() + PasswordResetToken.EXPIRY_PERIOD));
			passwordTokenDao.save(myToken);
			return myToken;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@POST
	@UnitOfWork
	@Path("/resetpasswordcheck/{token}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resetPasswordCheck(@PathParam("token") String token) {
		PasswordResetToken tempToken = passwordTokenDao.findToken(token);
		if (tempToken != null) {
			User user = userDAO.findByEmail(tempToken.getEmail());
			if (user == null) {
				return Response.status(Status.UNAUTHORIZED).entity(JsonUtils.getErrorJson("User does not exists"))
						.build();
			} else {
				if (tempToken.isExpired()) {
					return Response.status(Status.UNAUTHORIZED).entity(JsonUtils.getErrorJson("Link has expired"))
							.build();
				} else {
					tempToken.setSecondToken(UUID.randomUUID().toString());
					boolean success = false;
					try {
						passwordTokenDao.save(tempToken);
						success = true;
						return Response.status(Status.OK).entity(new ResetPasswordCheckDTO(tempToken.getToken(), tempToken.getSecondToken())).build();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (!success) {
							return Response.status(Status.INTERNAL_SERVER_ERROR)
									.entity(JsonUtils.getErrorJson("Server crashed while processing token")).build();
						}
					}

					if (!success) {
						return Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(JsonUtils.getErrorJson("Server crashed while processing token")).build();
					}
				}
			}
		} else {
			return Response.status(Status.UNAUTHORIZED).entity(JsonUtils.getErrorJson("You are not authorized"))
					.build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(JsonUtils.getErrorJson("Unknown server error"))
				.build();
	}
	
	@POST
	@UnitOfWork
	@Path("/resetpasswordsubmit")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resetPasswordCheck(ResetPasswordCheckDTO resetPasswordCheckDTO) {
		PasswordResetToken tempToken = passwordTokenDao.findByTokens(resetPasswordCheckDTO.getT1(), resetPasswordCheckDTO.getT2());
		if (tempToken != null) {
			if(tempToken.isExpired()) {
				return Response.status(Status.UNAUTHORIZED).entity(JsonUtils.getErrorJson("Link has expired"))
						.build();
			} else {
				User user = userDAO.findByEmail(tempToken.getEmail());
				user.setPassword(resetPasswordCheckDTO.getNewPassword());
				try {
					userDAO.save(user);
					return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Password reset successfully")).build();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(JsonUtils.getErrorJson("Unknown error on server"))
						.build();
				
			}
		} else {
			return Response.status(Status.UNAUTHORIZED).entity(JsonUtils.getErrorJson("You are not authorized"))
					.build();
		}
	}

}
