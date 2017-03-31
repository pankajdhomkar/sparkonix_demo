package com.sparkonix.resources;

import java.io.IOException;
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

import com.google.zxing.WriterException;
import com.sparkonix.dao.CompanyDetailDAO;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.QRCodeDAO;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.QRCode;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.MachineDetailsDTO;
import com.sparkonix.entity.jsonb.CompanyLocationAddress;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/machines")
@Produces(MediaType.APPLICATION_JSON)
public class MachinesResource {

	private final MachineDAO machineDAO;
	private final QRCodeDAO qrCodeDAO;

	private final CompanyDetailDAO companyDetailDAO;
	private final CompanyLocationDAO companyLocationDAO;

	private final Logger log = Logger.getLogger(MachinesResource.class.getName());

	public MachinesResource(MachineDAO machineDAO, CompanyDetailDAO companyDetailDAO,
			CompanyLocationDAO companyLocationDAO, QRCodeDAO qrCodeDAO) {
		this.machineDAO = machineDAO;
		this.companyDetailDAO = companyDetailDAO;
		this.companyLocationDAO = companyLocationDAO;
		this.qrCodeDAO = qrCodeDAO;
	}

	@POST
	@UnitOfWork
	public Response createMachine(@Auth User authUser, Machine machine) {

		try {
			if (machine.getQrCode() != null) {
				QRCode qrCode = qrCodeDAO.getByQRCode(machine.getQrCode());
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
						Machine newMachine = machineDAO.save(machine);
						if (newMachine != null) {
							log.info("QR code has been assigned to machine");
							// update qr code as assigned
							qrCode.setStatus(QRCode.QRCODE_STATUS.ASSIGNED.toString());
							qrCodeDAO.save(qrCode);
							log.info("New machine successfully added.");
							return Response.status(Status.OK).entity(JsonUtils.getJson(newMachine)).build();

						} else {
							log.info("New machine not added.");
							return Response.status(Status.BAD_REQUEST)
									.entity(JsonUtils.getErrorJson("New machine not added")).build();
						}
					}
				}
			} else {
				log.severe("QR code is null");
				// add machine without QR code
				Machine newMachine = machineDAO.save(machine);
				if (newMachine != null) {
					log.info("New machine successfully added.");
					return Response.status(Status.OK).entity(JsonUtils.getJson(newMachine)).build();
				} else {
					log.info("New machine not added.");
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("New machine not added"))
							.build();
				}

			}
		} catch (Exception e) {
			log.severe("Unable to add machine. Error: " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to add machine")).build();
		}

	}

	@GET
	@UnitOfWork
	@Path("/all/{customerId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listMachines(@Auth User authUser, @PathParam("customerId") long customerId) {
		/*
		 * try { log.info("In listMachines"); return
		 * Response.status(Status.OK).entity(JsonUtils.getJson(machineDAO.
		 * findAll())).build(); } catch (Exception e) { log.severe(
		 * "Unable to find Machines " + e); return
		 * Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson(
		 * "Unable to find Machines")) .build(); }
		 */

		try {
			log.info(" In listMachines");
			List<Machine> machineList = machineDAO.findAllByCustomerId(customerId);

			List<MachineDetailsDTO> machineDTOList = new ArrayList<>();

			for (int i = 0; i < machineList.size(); i++) {
				MachineDetailsDTO machineDetailsDTO = new MachineDetailsDTO();

				machineDetailsDTO.setMachineId(machineList.get(i).getId());
				machineDetailsDTO.setSerialNumber(machineList.get(i).getSerialNumber());
				machineDetailsDTO.setModelNumber(machineList.get(i).getModelNumber());

				CompanyDetail manufacturerObj = companyDetailDAO.getById(machineList.get(i).getManufacturerId());
				machineDetailsDTO.setManufacturer(manufacturerObj);

				CompanyDetail resellerObj = companyDetailDAO.getById(machineList.get(i).getResellerId());
				machineDetailsDTO.setReseller(resellerObj);

				CompanyLocation locationObj = companyLocationDAO.getById(machineList.get(i).getLocationId());
				String address = locationObj.getAddress();
				locationObj.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
				machineDetailsDTO.setLocation(locationObj);

				machineDTOList.add(machineDetailsDTO);
			}
			return Response.status(Status.OK).entity(machineDTOList).build();
		} catch (Exception e) {
			log.severe("Unable to find machine list " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find machine list"))
					.build();
		}
	}

	@GET
	@Path("/{companyId}")
	@UnitOfWork
	public Response getAllMachinesForCompany(@Auth User authUser, @PathParam("companyId") long companyId)
			throws IOException, WriterException {
		try {
			log.info("In getAllMachinesForCompany");
			return Response.status(Status.OK).entity(JsonUtils.getJson(machineDAO.getAllMachinesForCompany(companyId)))
					.build();
		} catch (Exception e) {
			log.severe("Unable to find Machines " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Machines"))
					.build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/{customerId}/{onBoardedById}")
	public Response listMachinesByCustIdAndOnBoardedId(@Auth User authUser, @PathParam("customerId") long customerId,
			@PathParam("onBoardedById") long onBoardedById) {
		try {
			log.info(" In listMachinesByCustIdAndOnBoardedId");

			List<Machine> machineList = machineDAO.findAllByCustomerIdAndOnBoardedId(customerId, onBoardedById);

			List<MachineDetailsDTO> machineDTOList = new ArrayList<>();

			for (int i = 0; i < machineList.size(); i++) {
				MachineDetailsDTO machineDetailsDTO = new MachineDetailsDTO();

				machineDetailsDTO.setMachineId(machineList.get(i).getId());
				machineDetailsDTO.setSerialNumber(machineList.get(i).getSerialNumber());
				machineDetailsDTO.setModelNumber(machineList.get(i).getModelNumber());

				CompanyDetail manufacturerObj = companyDetailDAO.getById(machineList.get(i).getManufacturerId());
				machineDetailsDTO.setManufacturer(manufacturerObj);

				CompanyDetail resellerObj = companyDetailDAO.getById(machineList.get(i).getResellerId());
				machineDetailsDTO.setReseller(resellerObj);

				CompanyLocation locationObj = companyLocationDAO.getById(machineList.get(i).getLocationId());
				String address = locationObj.getAddress();
				locationObj.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
				machineDetailsDTO.setLocation(locationObj);

				machineDTOList.add(machineDetailsDTO);
			}
			return Response.status(Status.OK).entity(machineDTOList).build();
		} catch (Exception e) {
			log.severe("Unable to find machine list " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find machine list"))
					.build();
		}
	}

	@POST
	@UnitOfWork
	@Path("/bulkupload")
	public Response addBulkMachinesByExcelSheet(@Auth User authUser, List<Machine> machineList) {
		log.info(" In addBulkMachinesByExcelSheet");
		try {
			if (machineList != null && machineList.size() > 0) {
				for (Machine machine : machineList) {
					if (machine != null) {
						Machine newMachine = new Machine();
						newMachine.setName(machine.getName());
						newMachine.setQrCode(machine.getQrCode());
						newMachine.setSerialNumber(machine.getSerialNumber());
						newMachine.setModelNumber(machine.getModelNumber());
						newMachine.setDescription(machine.getDescription());
						newMachine.setMachineYear(machine.getMachineYear());
						newMachine.setManufacturerId(machine.getManufacturerId());
						newMachine.setResellerId(machine.getResellerId());
						newMachine.setInstallationDate(machine.getInstallationDate());
						newMachine.setWarrantyExpiryDate(machine.getWarrantyExpiryDate());
						newMachine.setLocationId(machine.getLocationId());
						newMachine.setSupportAssistance(machine.getSupportAssistance());
						newMachine.setCurAmcType(machine.getCurAmcType());
						newMachine.setCurAmcStatus(machine.getCurAmcStatus());
						newMachine.setCurAmcStartDate(machine.getCurAmcStartDate());
						newMachine.setCurAmcEndDate(machine.getCurAmcEndDate());
						newMachine.setCurSubscriptionType(machine.getCurSubscriptionType());
						newMachine.setCurSubscriptionStatus(machine.getCurSubscriptionStatus());
						newMachine.setCurSubscriptionStartDate(machine.getCurSubscriptionStartDate());
						newMachine.setCurSubscriptionEndDate(machine.getCurAmcEndDate());

						newMachine.setCustomerId(machine.getCustomerId());
						newMachine.setOnBoardedBy(machine.getOnBoardedBy());

						if (newMachine.getQrCode() != null) {
							QRCode qrCode = qrCodeDAO.getByQRCode(newMachine.getQrCode());
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
									Machine dbMachine = machineDAO.save(newMachine);
									if (dbMachine != null) {
										log.info("QR code has been assigned to machine");
										// update qr code as assigned
										qrCode.setStatus(QRCode.QRCODE_STATUS.ASSIGNED.toString());
										qrCodeDAO.save(qrCode);
										log.info("New machine successfully added.");

									} else {
										log.info("New machine not added.");
										return Response.status(Status.BAD_REQUEST)
												.entity(JsonUtils.getErrorJson("New machine list not added")).build();

									}
								}
							}
						} else {
							log.severe("QR code is null");
							// add machine without QR code
							Machine dbMachine = machineDAO.save(machine);
							if (dbMachine != null) {
								log.info("New machine successfully added.");

							} else {
								log.info("New machine not added.");
								return Response.status(Status.BAD_REQUEST)
										.entity(JsonUtils.getErrorJson("New machine list not added")).build();
							}
						}
					} else {
						return Response.ok(JsonUtils.getSuccessJson("Please provide bulk machines data")).build();
					}
				}

				return Response.ok(JsonUtils.getSuccessJson("Bulk machine list uploaded successfully")).build();
			} else {

				return Response.ok(JsonUtils.getSuccessJson("No record found for bulk machines upload")).build();
			}
		} catch (Exception e) {
			log.severe("Unable to process machines upload. Error: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to process bulk machines upload")).build();

		}

	}

	@GET
	@Path("/modelnumbers")
	@UnitOfWork
	public Response getAllMachineModelNumbers(@Auth User authUser) {
		try {
			log.info("In getAllMachineModelNumbers");		 
			
			return Response.status(Status.OK).entity(JsonUtils.getJson(machineDAO.getAllMachineModelNumbers())).build();
		} catch (Exception e) {
			log.severe("Unable to find Machines model numbers " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Machines model numbers")).build();
		}
	}
	
	@GET
	@Path("/modelnumbers/{manufacturer_id}")
	@UnitOfWork
	public Response getMachineModelNumberListByManId(@Auth User authUser, @PathParam("manufacturer_id") long manufacturerId) {
		try {
			log.info("In getMachineModelNumberListByManId");		 
			
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(machineDAO.getAllMachineModelNumbersByManufacturerId(manufacturerId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Machines model numbers " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Machines model numbers")).build();
		}
	}

}
