package com.sparkonix.resources;

import java.util.ArrayList;
import java.util.Date;
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
import com.sparkonix.dao.MachineAmcServiceHistoryDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.Issue;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.MachineAmcServiceHistory;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.MachineAmcServiceHistoryDTO;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.SendSMS;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/machineamcservicehistories")
@Produces(MediaType.APPLICATION_JSON)
public class MachineAmcServiceHistoriesResource {

	private final MachineAmcServiceHistoryDAO machineAmcServiceHistoryDAO;
	private final MachineDAO machineDAO;
	private final UserDAO userDAO;
	private final Logger log = Logger.getLogger(MachineAmcServiceHistoriesResource.class.getName());

	public MachineAmcServiceHistoriesResource(MachineAmcServiceHistoryDAO machineAmcServiceHistoryDAO,
			MachineDAO machineDAO, UserDAO userDAO) {
		this.machineAmcServiceHistoryDAO = machineAmcServiceHistoryDAO;
		this.machineDAO = machineDAO;
		this.userDAO = userDAO;
	}

	/**
	 * Saves the new MachineAmcServiceHistory and return same with id
	 * 
	 * @param authUser
	 * @param machineAmcServiceHistory
	 * @return {@link MachineAmcServiceHistory}
	 * 
	 * @author abhijeet
	 */
	@POST
	@UnitOfWork
	public Response createAmcServiceHistory(@Auth User authUser, MachineAmcServiceHistory machineAmcServiceHistory) {
		try {
			if (machineAmcServiceHistory.getMachine() == null) {
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Machine should not be blank"))
						.build();
			}
			Machine machine = machineDAO.getById(machineAmcServiceHistory.getMachine().getId());

			if (machine == null) {
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Machine does not exist"))
						.build();
			}

			if (machineAmcServiceHistory.getAssignedTo().getId() > 0) {
				User technician = userDAO.getById(machineAmcServiceHistory.getAssignedTo().getId());
				if (technician == null) {
					return Response.status(Status.BAD_REQUEST)
							.entity(JsonUtils.getErrorJson("Technician does not exist")).build();
				}
				machineAmcServiceHistory.setAssignedTo(technician);
				machineAmcServiceHistory.setStatus(Issue.ISSUE_STATUS.ASSIGNED.toString());
				
				// send sms to technician
				JsonObject jsonObjSms = new JsonObject();
				jsonObjSms.addProperty("toMobileNumber",technician.getMobile().replaceAll("\\+",""));
				jsonObjSms.addProperty("smsMessage",
						"A machine service reuest assigned to you by " + authUser.getName());

				// send SMS
				new SendSMS(jsonObjSms).run();
				log.info("Machine service request sms sent to technician.");

			} else {
				machineAmcServiceHistory.setStatus(Issue.ISSUE_STATUS.OPEN.toString());
			}
			machineAmcServiceHistory.setMachine(machine);
			machineAmcServiceHistory.setCompanyId(machineAmcServiceHistory.getCompanyId());
			machineAmcServiceHistory.setDetails(machineAmcServiceHistory.getDetails());
			machineAmcServiceHistory.setServicingAssignedDate(new Date());
			machineAmcServiceHistory.setMetadata("{}");
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(machineAmcServiceHistoryDAO.save(machineAmcServiceHistory))).build();
		} catch (Exception e) {
			log.severe("Failed to create new service request. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("New service request not created"))
					.build();
		}

	}

	/**
	 * return list of all {@link MachineAmcServiceHistory}
	 * 
	 * @param authUser
	 * @return List of {@link MachineAmcServiceHistory}
	 * 
	 * @author abhijeet
	 */
	@GET
	@UnitOfWork
	public Response listMachineAmcServiceHistories(@Auth User authUser) {
		try {
			log.info(" In listMachineAmcServiceHistories");
			return Response.status(Status.OK).entity(JsonUtils.getJson(machineAmcServiceHistoryDAO.findAll())).build();
		} catch (Exception e) {
			log.severe("Unable to find Machines AMC Service Histories " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Machines AMC Service Histories")).build();
		}
	}

	/**
	 * return list of all {@link MachineAmcServiceHistory} by particular
	 * {@linkplain category} (technician,company or machine) and for particular
	 * {@linkplain parameter} i.e user id (webadmin or technician)
	 * 
	 * @param authUser
	 * @param category
	 * @param parameter
	 * @return List of {@link MachineAmcServiceHistory}
	 * 
	 * @author abhijeet
	 */
	@GET
	@Path("/{category}/{parameter}")
	@UnitOfWork
	public Response listServicesByCategory(@Auth User authUser, @PathParam("category") String category,
			@PathParam("parameter") long parameter) {
		try {
			log.info(" In listServicesByCategory");
			List<MachineAmcServiceHistory> machineAmcServiceHistoryList = null;
			if (category.equalsIgnoreCase("technician")) {
				User userObj = userDAO.getById(parameter);
				if (userObj != null) {
					machineAmcServiceHistoryList = machineAmcServiceHistoryDAO.getServicesForTechnician(userObj);
				}
			} else {
				machineAmcServiceHistoryList = machineAmcServiceHistoryDAO.getServicesByCategory(category, parameter);
			}

			return Response.status(Status.OK).entity(JsonUtils.getJson(machineAmcServiceHistoryList)).build();
		} catch (Exception e) {
			log.severe("Unable to find Machines AMC Service Histories " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Machines AMC Service Histories")).build();
		}
	}

	/**
	 * This method returns list of AMC service histories for respective machine
	 * (accessed by mobile app)
	 * 
	 * @param authUser
	 * @param machineId
	 * @return object of {@link MachineAmcServiceHistoryDTO}
	 */
	@GET
	@UnitOfWork
	@Path("/bymachine/{machineId}")
	public Response listMachineAmcServiceHistoriesByMachineId(@Auth User authUser,
			@PathParam("machineId") long machineId) {
		try {
			log.info(" In listMachineAmcServiceHistoriesByMachineId");
			List<MachineAmcServiceHistory> machineAmcServiceHistoryList = machineAmcServiceHistoryDAO
					.getServicesByCategory("machine", machineId);

			List<MachineAmcServiceHistoryDTO> machineAmcServiceHistoryDTOList = new ArrayList<>();

			for (int i = 0; i < machineAmcServiceHistoryList.size(); i++) {
				MachineAmcServiceHistoryDTO machineAmcServiceHistoryDTO = new MachineAmcServiceHistoryDTO();

				machineAmcServiceHistoryDTO.setId(machineAmcServiceHistoryList.get(i).getId());
				machineAmcServiceHistoryDTO.setMachineId(machineAmcServiceHistoryList.get(i).getMachine().getId());
				machineAmcServiceHistoryDTO.setDetails(machineAmcServiceHistoryList.get(i).getDetails());
				machineAmcServiceHistoryDTO
						.setModelNumber(machineAmcServiceHistoryList.get(i).getMachine().getModelNumber());
				machineAmcServiceHistoryDTO
						.setServicingAssignedDate(machineAmcServiceHistoryList.get(i).getServicingAssignedDate());
				machineAmcServiceHistoryDTO
						.setServicingDoneDate(machineAmcServiceHistoryList.get(i).getServicingDoneDate());

				machineAmcServiceHistoryDTO
						.setAssignedToName(machineAmcServiceHistoryList.get(i).getAssignedTo().getName());
				machineAmcServiceHistoryDTO
						.setAssignedToEmail(machineAmcServiceHistoryList.get(i).getAssignedTo().getEmail());
				machineAmcServiceHistoryDTO
						.setAssignedToMobile(machineAmcServiceHistoryList.get(i).getAssignedTo().getMobile());

				machineAmcServiceHistoryDTO.setActionTaken(machineAmcServiceHistoryList.get(i).getActionTaken());

				machineAmcServiceHistoryDTOList.add(machineAmcServiceHistoryDTO);
			}
			return Response.status(Status.OK).entity(machineAmcServiceHistoryDTOList).build();
		} catch (Exception e) {
			log.severe("Unable to find machine amc service history list. Reason: " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find machine amc service history list")).build();
		}
	}
}
