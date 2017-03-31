package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.PhoneDeviceDAO;
import com.sparkonix.entity.PhoneDevice;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.FcmTokenDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/phonedevice")
@Produces(MediaType.APPLICATION_JSON)
public class PhoneDeviceResource {

	private final PhoneDeviceDAO phoneDeviceDAO;
	private final Logger log = Logger.getLogger(PhoneDeviceResource.class.getName());

	public PhoneDeviceResource(PhoneDeviceDAO phoneDeviceDAO) {
		this.phoneDeviceDAO = phoneDeviceDAO;
	}

	@GET
	@UnitOfWork
	@Path("/{phoneDeviceId}")
	public Response getPhoneDeviceById(@Auth User authUser, @PathParam("phoneDeviceId") long phoneDeviceId) {
		try {
			log.info(" In getClosedIssueById");
			return Response.status(Status.OK).entity(JsonUtils.getJson(phoneDeviceDAO.getById(phoneDeviceId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Phone Device " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Phone Device"))
					.build();
		}
	}

	@PUT
	@UnitOfWork
	@Path("/{phoneDeviceId}")
	public Response updatePhoneDevice(@Auth User authUser, PhoneDevice phoneDevice,
			@PathParam("phoneDeviceId") long phoneDeviceId) {
		try {
			log.info(" In updatePhoneDevice");
			if (authUser == null) {
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Authorization Failed"))
						.build();
			}
			PhoneDevice phoneDevice2 = phoneDeviceDAO.getById(phoneDeviceId);
			if (phoneDevice2 != null) {
				phoneDevice2.setOperatorName(phoneDevice.getOperatorName());
				phoneDevice2.setPhoneNumber(phoneDevice.getPhoneNumber());
				phoneDevice2.setLocationId(phoneDevice.getLocationId());
				phoneDeviceDAO.save(phoneDevice2);
			}
			log.info("Company Detail updated");
			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Operator updated successfully")).build();
		} catch (Exception e) {
			log.severe("Unable to Update  " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to update operator "))
					.build();
		}
	}

	@PUT
	@UnitOfWork
	@Path("/fcmtoken")
	public Response updateFcmTokenForPhoneDevice(@Auth User authUser, FcmTokenDTO fcmTokenDTO) {

		PhoneDevice phoneDevice = phoneDeviceDAO.getOperatorByPhoneNumber(authUser.getEmail());

		if (phoneDevice == null) {
			log.severe("This is not authorized operator mobile number.");
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("This is not authorized operator mobile number")).build();
		} else {
			// update fcm token
			phoneDevice.setFcmToken(fcmTokenDTO.getFcmToken());

			try {
				phoneDeviceDAO.save(phoneDevice);
				log.severe("Fcm token updated.");
				return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Fcm token updated")).build();
			} catch (Exception e) {
				log.severe("Fcm token not updated." + e.getMessage());
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to update fcm token"))
						.build();
			}

		}
	}

}
