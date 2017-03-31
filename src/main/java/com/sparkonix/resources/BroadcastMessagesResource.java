package com.sparkonix.resources;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonObject;
import com.sparkonix.dao.PhoneDeviceDAO;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.BroadcastMessageDTO;
import com.sparkonix.utils.FcmMessage;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.SendFcmPushNotification;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/broadcastmessages")
@Produces(MediaType.APPLICATION_JSON)
public class BroadcastMessagesResource {

	private final PhoneDeviceDAO phoneDeviceDAO;
	private final Logger log = Logger.getLogger(BroadcastMessagesResource.class.getName());

	public BroadcastMessagesResource(PhoneDeviceDAO phoneDeviceDAO) {
		this.phoneDeviceDAO = phoneDeviceDAO;
	}

	@POST
	@UnitOfWork
	public Response broadcastNewMessage(@Auth User authUser, BroadcastMessageDTO broadcastMessageDTO) {
		try {
			List<String> fcmTokenList = phoneDeviceDAO.getAllFcmTokens();

			// send push notification to operator
			FcmMessage fcmMessage = new FcmMessage();

			JsonObject data = new JsonObject();
			data.addProperty("title", broadcastMessageDTO.getTitle());
			data.addProperty("message", broadcastMessageDTO.getMessage());
			data.addProperty("description", broadcastMessageDTO.getDescription());

			fcmMessage.setData(data);

			String[] fcmTokenArr = new String[fcmTokenList.size()];
			fcmTokenArr = fcmTokenList.toArray(fcmTokenArr);
			fcmMessage.setRegistration_ids(fcmTokenArr);

			Thread t1 = new Thread(new SendFcmPushNotification(fcmMessage));
			t1.start();
			log.info("A broadcast message has been sent to operators.");
			return Response.status(Status.OK)
					.entity(JsonUtils.getSuccessJson("A broadcast message has been sent to operators.")).build();

		} catch (Exception e) {
			log.severe("Failed to broadcast message. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Failed to broadcast message"))
					.build();
		}

	}

}
