package com.sparkonix.resources;

import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonObject;
import com.sparkonix.dao.MachineAmcServiceHistoryDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.MachineAmcServiceHistory;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.SendSMS;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/machineamcservicehistory/{amcServiceHistoryId}")
@Produces(MediaType.APPLICATION_JSON)
public class MachineAmcServiceHistoryResource {

	private final MachineAmcServiceHistoryDAO machineAmcServiceHistoryDAO;
	private final MachineDAO machineDAO;
	private final UserDAO userDAO;
	private final Logger log = Logger.getLogger(MachineAmcServiceHistoryResource.class.getName());

	public MachineAmcServiceHistoryResource(MachineAmcServiceHistoryDAO machineAmcServiceHistoryDAO,
			MachineDAO machineDAO, UserDAO userDAO) {
		this.machineAmcServiceHistoryDAO = machineAmcServiceHistoryDAO;
		this.machineDAO = machineDAO;
		this.userDAO = userDAO;
	}

	/**
	 * return the MachineAmcServiceHistory whose id is passed in param
	 * {@linkplain amcServiceHistoryId}
	 * 
	 * @param authUser
	 * @param amcServiceHistoryId
	 * 
	 * @return {@link MachineAmcServiceHistory}
	 * 
	 * @author abhijeet
	 */

	@GET
	@UnitOfWork
	public Response getMachineAmcServiceHistoryById(@Auth User authUser,
			@PathParam("amcServiceHistoryId") long amcServiceHistoryId) {
		try {
			log.info(" In getMachineAmcServiceHistoryById");
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(machineAmcServiceHistoryDAO.getById(amcServiceHistoryId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Machine AMC Service History " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find  Machine AMC Service History")).build();
		}
	}

	/**
	 * Update the MachineAmcServiceHistory whose id is passed in param
	 * {@linkplain amcServiceHistoryId} and return success message
	 * 
	 * @param authUser
	 * @param amcServiceHistoryId
	 * @param machineAmcServiceHistory
	 * 
	 * @return success message
	 * 
	 * @author abhijeet
	 */

	@PUT
	@UnitOfWork
	public Response updateMachineAmcServiceHistoryById(@Auth User authUser,
			@PathParam("amcServiceHistoryId") long amcServiceHistoryId,
			MachineAmcServiceHistory machineAmcServiceHistory) {
		try {
			log.info(" In updateUserById");
			MachineAmcServiceHistory amcServiceHistoryObj = machineAmcServiceHistoryDAO
					.getById(machineAmcServiceHistory.getId());

			if (amcServiceHistoryObj == null) {
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("AMC Service does not exist"))
						.build();
			}
			if (machineAmcServiceHistory.getMachine() == null) {
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Machine should not be blank"))
						.build();
			}
			Machine machine = machineDAO.getById(machineAmcServiceHistory.getMachine().getId());

			if (machine == null) {
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Machine does not exist"))
						.build();
			}

			if (authUser.getRole().equals(User.ROLE_TYPE.TECHNICIAN.toString())) {
				if (machineAmcServiceHistory.getStatus().equals("CLOSED")) {
					log.severe("Technician has changed service request status");
					amcServiceHistoryObj.setServicingDoneDate(new Date());
					amcServiceHistoryObj.setStatus("CLOSED");
				}

			} else {
				if (machineAmcServiceHistory.getAssignedTo() != null
						&& machineAmcServiceHistory.getAssignedTo().getId() > 0) {
					User technician = userDAO.getById(machineAmcServiceHistory.getAssignedTo().getId());
					if (technician == null) {
						return Response.status(Status.BAD_REQUEST)
								.entity(JsonUtils.getErrorJson("Technician does not exist")).build();
					}
					amcServiceHistoryObj.setAssignedTo(technician);
					amcServiceHistoryObj.setStatus("ASSIGNED");
					// amcServiceHistoryObj.setMachine(machine);
					// amcServiceHistoryObj.setCompanyId(machineAmcServiceHistory.getCompanyId());
					amcServiceHistoryObj.setDetails(machineAmcServiceHistory.getDetails());
					amcServiceHistoryObj.setServicingAssignedDate(new Date());
					// amcServiceHistoryObj.setMetadata("{}");

					// send sms to technician
					JsonObject jsonObjSms = new JsonObject();
					jsonObjSms.addProperty("toMobileNumber", technician.getMobile().replaceAll("\\+",""));
					jsonObjSms.addProperty("smsMessage",
							"A machine service reuest assigned to you by " + authUser.getName());

					// send SMS
					new SendSMS(jsonObjSms).run();
					log.info("Machine service request sms sent to technician.");

				} else {
					amcServiceHistoryObj.setStatus("OPEN");
					log.severe("Technician not assigned");

				}

			}
			machineAmcServiceHistoryDAO.save(amcServiceHistoryObj);

			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Machine details updated successfully"))
					.build();
		} catch (Exception e) {
			log.severe("Unable to Update  " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to update machine details")).build();
		}
	}

}
