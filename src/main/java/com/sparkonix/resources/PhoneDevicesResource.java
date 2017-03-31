package com.sparkonix.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonObject;
import com.sparkonix.auth.JwtToken;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.PhoneDeviceDAO;
import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.PhoneDevice;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.PhoneDeviceOtpDTO;
import com.sparkonix.entity.jsonb.CompanyLocationAddress;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.SendSMS;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/phonedevices")
@Produces(MediaType.APPLICATION_JSON)
public class PhoneDevicesResource {

	private final PhoneDeviceDAO phoneDeviceDAO;
	private final CompanyLocationDAO companyLocationDAO;

	private final Logger log = Logger.getLogger(PhoneDevicesResource.class.getName());

	public PhoneDevicesResource(PhoneDeviceDAO phoneDeviceDAO, CompanyLocationDAO companyLocationDAO) {
		this.phoneDeviceDAO = phoneDeviceDAO;
		this.companyLocationDAO = companyLocationDAO;
	}

	/**
	 * This method is used to register the authorized mobile number for
	 * Operator.
	 * 
	 * @param authUser
	 * @param phoneDevice
	 *            Its authorized phone device (aka Operator)
	 * @return The object {@link PhoneDevice}
	 */
	@POST
	@UnitOfWork
	public Response createPhoneDevice(@Auth User authUser, PhoneDevice phoneDevice) throws Exception {
		try {

			PhoneDevice dbPhoneDevice = phoneDeviceDAO.getOperatorByMobileNumCustomerIdLocationId(
					phoneDevice.getPhoneNumber(), phoneDevice.getCustomerId(), phoneDevice.getLocationId());
			if (dbPhoneDevice != null) {
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("The Operator mobile number is already registered.")).build();
			}

			PhoneDevice phoneDevice2 = phoneDeviceDAO.save(phoneDevice);
			if (phoneDevice2 != null) {
				// send sms
				JsonObject jsonObj = new JsonObject();
				jsonObj.addProperty("toMobileNumber", phoneDevice2.getPhoneNumber().replaceAll("\\+", ""));
				jsonObj.addProperty("smsMessage", "You have been registered to use the Attendme application. "
						+ "Click here https://goo.gl/0glP8q to download.");

				// send SMS(using textlocal)
				new SendSMS(jsonObj).run();
				log.info("Registraion success sms has sent to operator");
			}
			return Response.status(Status.OK).entity(phoneDevice2).build();
		} catch (Exception e) {
			log.severe("Failed to create Operator. Error:" + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Failed to create Operator."))
					.build();
		}
	}

	@GET
	@UnitOfWork
	public Response listPhoneDevices(@Auth User authUser) {
		try {
			log.info(" In listPhoneDevices");
			return Response.status(Status.OK).entity(JsonUtils.getJson(phoneDeviceDAO.findAll())).build();
		} catch (Exception e) {
			log.severe("Unable to find Phone devices" + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Phone devices"))
					.build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/otp/{phoneNumber}")
	public Response getOtp(@PathParam("phoneNumber") String phoneNumber2) {
		// remove this line after UI fixes done in apk(to accept 10digit number
		// with country code)
		String phoneNumber = "+" + phoneNumber2;

		PhoneDevice operatorObj = null;
		try {
			operatorObj = phoneDeviceDAO.getOperatorByPhoneNumber(phoneNumber);
		} catch (Exception e1) {
			log.severe("Mobile number is not registered");
		}
		if (operatorObj == null) {
			log.severe("Mobile number is not registered");

			PhoneDevice newOperator = new PhoneDevice();
			newOperator.setPhoneNumber(phoneNumber);
			newOperator.setOperatorName("Demo Operator");
			newOperator.setCustomerId(2);
			newOperator.setLocationId(1);
			newOperator.setOnBoardedBy(1);
			newOperator.setOtp("9999");

			try {
				PhoneDevice dbNewOperator = phoneDeviceDAO.save(newOperator);
				dbNewOperator.setOperatorName(dbNewOperator.getOperatorName()+""+dbNewOperator.getId());
				phoneDeviceDAO.save(newOperator);
				log.severe("New operator created");
			} catch (Exception e) {
				return Response.status(Status.OK).entity(JsonUtils.getErrorJson("Failed to create new operator"))
						.build();
			}
			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("OTP has been sent")).build();
		} else {
			operatorObj.setOtp("9999");
			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("OTP has been sent")).build();
		}
	}

	@POST
	@UnitOfWork
	@Path("/otp/validate")
	public Response validateOtp(PhoneDeviceOtpDTO phoneDeviceOtpDTO) {
		phoneDeviceOtpDTO.setPhoneNumber("+" + phoneDeviceOtpDTO.getPhoneNumber());
		try {
			if (phoneDeviceOtpDTO.getPhoneNumber().equals("") || phoneDeviceOtpDTO.getOtp().equals("")) {
				log.severe("Mobile number or OTP should not be empty.");
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Mobile number or OTP should not be empty")).build();

			}
			PhoneDevice operatorObj = phoneDeviceDAO.getOperatorByPhoneNumber(phoneDeviceOtpDTO.getPhoneNumber());
			if (operatorObj.getOtp().equals(phoneDeviceOtpDTO.getOtp())) {
				log.info("OTP is valid.");
				// generate token for @Auth
				User userObj = new User();
				userObj.setRole("OPERATOR");
				userObj.setEmail(phoneDeviceOtpDTO.getPhoneNumber());

				// generate token
				String token = JwtToken.generateToken(phoneDeviceOtpDTO.getPhoneNumber(), userObj);
				log.info("JWT token generated.");
				userObj.setToken(token);

				JsonObject jsonObj = new JsonObject();
				jsonObj.addProperty("success", "OTP is valid");
				jsonObj.addProperty("token", token);

				// set OTP to null
				operatorObj.setOtp(null);
				log.info("OTP value updated as null.");

				// updating fcm token for push notification
				if (phoneDeviceOtpDTO.getFcmToken() == null || phoneDeviceOtpDTO.getFcmToken().trim().isEmpty()) {
					log.severe("Fcm token is empty.");
				} else {
					operatorObj.setFcmToken(phoneDeviceOtpDTO.getFcmToken());
					log.info("Updated fcm token value.");
				}

				return Response.status(Status.OK).entity(jsonObj.toString()).build();

			} else {
				log.severe("OTP is not valid.");
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("OTP is not valid")).build();
			}
		} catch (Exception e) {
			log.severe("Unable to validate otp. Error: " + e.getMessage());

			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to validate otp")).build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/{customerId}/{onBoardedById}")
	public Response listPhoneDevicesByCustIdAndOnBoardedId(@Auth User authUser,
			@PathParam("customerId") long customerId, @PathParam("onBoardedById") long onBoardedById) {
		try {
			log.info(" In listPhoneDevicesByCustIdAndOnBoardedId");

			List<PhoneDevice> phoneDevicesList = phoneDeviceDAO.findAllByCustomerIdAndOnBoardedId(customerId,
					onBoardedById);

			List<PhoneDevice> devicesList = new ArrayList<>();

			for (int i = 0; i < phoneDevicesList.size(); i++) {
				PhoneDevice phoneDevice = new PhoneDevice();

				phoneDevice.setId(phoneDevicesList.get(i).getId());
				phoneDevice.setOperatorName(phoneDevicesList.get(i).getOperatorName());
				phoneDevice.setPhoneNumber(phoneDevicesList.get(i).getPhoneNumber());
				phoneDevice.setCustomerId(phoneDevicesList.get(i).getCustomerId());

				CompanyLocation locationObj = companyLocationDAO.getById(phoneDevicesList.get(i).getLocationId());
				String address = locationObj.getAddress();
				locationObj.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
				phoneDevice.setCompanyLocation(locationObj);

				phoneDevice.setOnBoardedBy(phoneDevicesList.get(i).getOnBoardedBy());

				devicesList.add(phoneDevice);
			}
			return Response.status(Status.OK).entity(devicesList).build();
		} catch (Exception e) {
			log.severe("Unable to find machine list " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find machine list"))
					.build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/{customerId}")
	public Response listPhoneDevicesByCustomerId(@Auth User authUser, @PathParam("customerId") long customerId) {
		try {
			log.info(" In listPhoneDevicesByCustomerId");

			List<PhoneDevice> phoneDevicesList = phoneDeviceDAO.findAllByCustomerId(customerId);

			List<PhoneDevice> devicesList = new ArrayList<>();

			for (int i = 0; i < phoneDevicesList.size(); i++) {
				PhoneDevice phoneDevice = new PhoneDevice();

				phoneDevice.setId(phoneDevicesList.get(i).getId());
				phoneDevice.setOperatorName(phoneDevicesList.get(i).getOperatorName());
				phoneDevice.setPhoneNumber(phoneDevicesList.get(i).getPhoneNumber());
				phoneDevice.setCustomerId(phoneDevicesList.get(i).getCustomerId());

				CompanyLocation locationObj = companyLocationDAO.getById(phoneDevicesList.get(i).getLocationId());
				String address = locationObj.getAddress();
				locationObj.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
				phoneDevice.setCompanyLocation(locationObj);

				phoneDevice.setOnBoardedBy(phoneDevicesList.get(i).getOnBoardedBy());

				devicesList.add(phoneDevice);
			}
			return Response.status(Status.OK).entity(devicesList).build();
		} catch (Exception e) {
			log.severe("Unable to find machine list " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find machine list"))
					.build();
		}
	}

}
