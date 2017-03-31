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

import com.sparkonix.dao.CompanyDetailDAO;
import com.sparkonix.dao.MachineAmcServiceHistoryDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.PhoneDeviceDAO;
import com.sparkonix.dao.QRCodeDAO;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.PhoneDevice;
import com.sparkonix.entity.QRCode;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.MachineDetailsDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/machine")
@Produces(MediaType.APPLICATION_JSON)
public class MachineResource {

	private final MachineDAO machineDAO;
	private final QRCodeDAO qrCodeDAO;
	private final CompanyDetailDAO companyDetailDAO;
	private final PhoneDeviceDAO phoneDeviceDAO;
	private final MachineAmcServiceHistoryDAO machineAmcServiceHistoryDAO;
	private final Logger log = Logger.getLogger(MachineResource.class.getName());

	public MachineResource(MachineDAO machineDAO, CompanyDetailDAO companyDetailDAO,
			MachineAmcServiceHistoryDAO machineAmcServiceHistoryDAO, QRCodeDAO qrCodeDAO,
			PhoneDeviceDAO phoneDeviceDAO) {
		this.machineDAO = machineDAO;
		this.companyDetailDAO = companyDetailDAO;
		this.machineAmcServiceHistoryDAO = machineAmcServiceHistoryDAO;
		this.qrCodeDAO = qrCodeDAO;
		this.phoneDeviceDAO = phoneDeviceDAO;
	}

	@GET
	@UnitOfWork
	@Path("/{machineId}")
	public Response getMachineById(@Auth User authUser, @PathParam("machineId") long machineId) {
		try {
			log.info(" In getMachineById");
			return Response.status(Status.OK).entity(JsonUtils.getJson(machineDAO.getById(machineId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Machine " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Machine")).build();
		}
	}

	/**
	 * It is used to get machine details by using QR code
	 * 
	 * @param authUser
	 * @param qrCode
	 * @return {@link MachineDetailsDTO}
	 */
	@GET
	@UnitOfWork
	@Path("/getbyqrcode/{qrCode}")
	public Response getMachineDetailsByQrCode(@Auth User authUser, @PathParam("qrCode") String qrCode) {
		try {
			log.info(" In getMachineDetailsByQrCode");
			if (authUser == null) {
				log.severe("User authorization failed");
				return Response.status(Status.UNAUTHORIZED).build();
			}

			PhoneDevice operator = phoneDeviceDAO.getOperatorByPhoneNumber(authUser.getEmail());
			Machine machine = machineDAO.getMachineByQrCode(qrCode);
			if (machine == null) {
				log.severe("No machine found.");
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Not valid AttendMe QR Code"))
						.build();
			} else {
				log.info("Machine found.");
				// operator can only scan machine of their customer_id
//				if (operator.getCustomerId() != machine.getCustomerId()) {
//					log.severe("This operator can scan machines, which only belongs to his company.");
//					return Response.status(Status.BAD_REQUEST)
//							.entity(JsonUtils
//									.getErrorJson("This operator can scan machines, which only belongs to his company"))
//							.build();
//				} else {
					log.info("This Operator belongs to company, which has this machine");

					CompanyDetail manufacturerObj = companyDetailDAO.getCustSupportInfo(machine.getManufacturerId());
					Date lastServiceDate = machineAmcServiceHistoryDAO.getLastServiceDate(machine.getId());

					MachineDetailsDTO machineDetailsDTO = new MachineDetailsDTO();

					machineDetailsDTO.setMachineId(machine.getId());
					machineDetailsDTO.setSerialNumber(machine.getSerialNumber());
					machineDetailsDTO.setModelNumber(machine.getModelNumber());
					machineDetailsDTO.setInstallationDate(machine.getInstallationDate());
					machineDetailsDTO.setCurAmcType(machine.getCurAmcType());
					machineDetailsDTO.setCurAmcStartDate(machine.getCurAmcStartDate());
					machineDetailsDTO.setCurAmcEndDate(machine.getCurAmcEndDate());
					machineDetailsDTO.setCurAmcStatus(machine.getCurAmcStatus());
					machineDetailsDTO.setManufacturer(manufacturerObj);
					machineDetailsDTO.setLastServiceDate(lastServiceDate);

					return Response.status(Status.OK).entity(JsonUtils.getJson(machineDetailsDTO)).build();
				}
			//}
		} catch (Exception e) {
			log.severe("Unable to find Machine by QR Code" + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Not valid AttendMe QR Code"))
					.build();
		}
	}

	@PUT
	@UnitOfWork
	@Path("/{machineId}")
	public Response updateMachine(@Auth User authUser, Machine machine, @PathParam("machineId") long machineId) {
		try {
			log.info(" In updateMachine");
			if (authUser == null) {
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Authorization Failed"))
						.build();
			}

			QRCode qrCode = null;
			Machine existingMachine = machineDAO.getById(machineId);
			if (existingMachine != null) {

				if (machine.getQrCode() != null && !machine.getQrCode().equals(existingMachine.getQrCode())) {
					qrCode = qrCodeDAO.getByQRCode(machine.getQrCode());
					if (qrCode == null) {
						log.info("This is not valid AttendMe QR code");
						return Response.status(Status.BAD_REQUEST)
								.entity(JsonUtils.getErrorJson("This is not valid AttendMe QR code")).build();
					} else {
						log.info("This is valid AttendMe QR code");
						if (qrCode.getStatus().equals(QRCode.QRCODE_STATUS.ASSIGNED.toString())) {
							log.severe("This QR code is not available.");
							return Response.status(Status.BAD_REQUEST)
									.entity(JsonUtils.getErrorJson("This QR code is not available")).build();
						} else {
							log.info("QR code has been assigned to machine");
							existingMachine.setQrCode(machine.getQrCode());
						}
					}

				}else{
					existingMachine.setQrCode(machine.getQrCode());
				}
				existingMachine.setName(machine.getName());
				existingMachine.setSerialNumber(machine.getSerialNumber());
				existingMachine.setModelNumber(machine.getModelNumber());
				existingMachine.setDescription(machine.getDescription());
				existingMachine.setMachineYear(machine.getMachineYear());
				existingMachine.setManufacturerId(machine.getManufacturerId());
				existingMachine.setResellerId(machine.getResellerId());
				existingMachine.setInstallationDate(machine.getInstallationDate());
				existingMachine.setWarrantyExpiryDate(machine.getWarrantyExpiryDate());
				existingMachine.setLocationId(machine.getLocationId());
				existingMachine.setSupportAssistance(machine.getSupportAssistance());
				existingMachine.setCurAmcType(machine.getCurAmcType());
				existingMachine.setCurAmcStartDate(machine.getCurAmcStartDate());
				existingMachine.setCurAmcEndDate(machine.getCurAmcEndDate());
				existingMachine.setCurAmcStatus(machine.getCurAmcStatus());
				existingMachine.setCurSubscriptionType(machine.getCurSubscriptionType());
				existingMachine.setCurSubscriptionStartDate(machine.getCurSubscriptionStartDate());
				existingMachine.setCurSubscriptionEndDate(machine.getCurSubscriptionEndDate());
				existingMachine.setCurSubscriptionStatus(machine.getCurSubscriptionStatus());

				Machine newMachine = machineDAO.save(existingMachine);
				if (newMachine != null) {
					if (qrCode != null) {
						qrCode.setStatus(QRCode.QRCODE_STATUS.ASSIGNED.toString());
						qrCodeDAO.save(qrCode);
					}
					log.info("Machine successfully updated.");
					return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Machine updated successfully"))
							.build();

				} else {
					log.info("Machine not updated.");
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Machine not updated"))
							.build();
				}
			} else {
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find machine"))
						.build();
			}
		} catch (Exception e) {
			log.severe("Unable to update machine" + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to update machine"))
					.build();
		}
	}

}
