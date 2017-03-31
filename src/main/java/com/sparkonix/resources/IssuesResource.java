package com.sparkonix.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.JsonObject;
import com.sparkonix.dao.CompanyDetailDAO;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.IssueDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.PhoneDeviceDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.Issue;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.PhoneDevice;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.IssueSearchFilterPayloadDTO;
import com.sparkonix.entity.jsonb.CompanyLocationAddress;
import com.sparkonix.utils.FcmMessage;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.MailUtils;
import com.sparkonix.utils.SendFcmPushNotification;
import com.sparkonix.utils.SendMail;
import com.sparkonix.utils.SendSMS;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/issues")
@Produces(MediaType.APPLICATION_JSON)
public class IssuesResource {

	private final IssueDAO issueDAO;
	private final PhoneDeviceDAO phoneDeviceDAO;
	private final MachineDAO machineDAO;
	private final CompanyDetailDAO companyDetailDAO;
	private final UserDAO userDAO;
	private final CompanyLocationDAO companyLocationDAO;

	private final Logger log = Logger.getLogger(IssuesResource.class.getName());

	public IssuesResource(IssueDAO issueDAO, PhoneDeviceDAO phoneDeviceDAO, MachineDAO machineDAO,
			CompanyDetailDAO companyDetailDAO, UserDAO userDAO, CompanyLocationDAO companyLocationDAO) {
		this.issueDAO = issueDAO;
		this.phoneDeviceDAO = phoneDeviceDAO;
		this.machineDAO = machineDAO;
		this.companyDetailDAO = companyDetailDAO;
		this.userDAO = userDAO;
		this.companyLocationDAO = companyLocationDAO;
	}

	/**
	 * used to add new issue/complaint. also it send email for web admin & fcm
	 * notification to operator
	 * 
	 * @param authUser
	 * @param issue
	 * @return
	 * @throws Exception
	 */
	@POST
	@UnitOfWork
	public Response createIssue(@Auth User authUser, Issue issue) throws Exception {

		if (authUser == null) {
			log.severe("User authorization failed");
			return Response.status(Status.UNAUTHORIZED).build();
		}
		if (issue.getMachineId() == 0) {
			log.severe("Machine ID is not valid");
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Machine ID is not valid"))
					.build();
		}

		PhoneDevice phoneDevice = phoneDeviceDAO.getOperatorByPhoneNumber(authUser.getEmail());
		if (phoneDevice == null) {
			log.severe("This is not authorized mobile number.");
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("This is not authorized mobile number")).build();
		}

		Machine machineObj = machineDAO.getById(issue.getMachineId());
		// get location details by location_id in machine

		CompanyLocation machineLocation = companyLocationDAO.getById(machineObj.getLocationId());
		// convert string to json object
		String address = machineLocation.getAddress();
		machineLocation.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
		// machineLocationNameAddress
		String machineLocationNameAddress = machineLocation.getCompanyLocationAddress().getLocationName() + ", "
				+ machineLocation.getCompanyLocationAddress().getLocationAddress();

		// get customerCompanyName
		CompanyDetail customerCompanyDetail = companyDetailDAO.getById(machineObj.getCustomerId());

		Issue newIssue = new Issue();
		newIssue.setIssueNumber("IN-" + System.currentTimeMillis() / 1000L);
		newIssue.setReportingDevice(phoneDevice.getId());
		newIssue.setStatus(Issue.ISSUE_STATUS.OPEN.toString());
		newIssue.setDateReported(new Date());
		newIssue.setMachineId(issue.getMachineId());
		newIssue.setDetails(issue.getDetails());

		// set redundant field for faster lookup
		newIssue.setMachineModelNumber(machineObj.getModelNumber());
		newIssue.setMachineSerialNumber(machineObj.getSerialNumber());
		newIssue.setMachineInstallationDate(machineObj.getInstallationDate());

		newIssue.setPhonedeviceOperatorName(phoneDevice.getOperatorName());
		newIssue.setMachineLocationId(machineObj.getLocationId());
		newIssue.setMachineLocationNameAddress(machineLocationNameAddress);
		newIssue.setCustomerId(machineObj.getCustomerId());
		newIssue.setCustomerCompanyName(customerCompanyDetail.getCompanyName());
		newIssue.setManufacturerId(machineObj.getManufacturerId());
		newIssue.setResellerId(machineObj.getResellerId());
		newIssue.setMachineSupportAssistance(machineObj.getSupportAssistance());

		Issue dbIssue = issueDAO.save(newIssue);
		if (dbIssue != null) {

			// send push notification to operator
			FcmMessage fcmMessage = new FcmMessage();
			String details = "Complaint no.: " + dbIssue.getIssueNumber() + "\n" + "Details: " + dbIssue.getDetails()
					+ "\n" + "Status: " + dbIssue.getStatus() + "\n" + "Reported By: " + phoneDevice.getOperatorName()
					+ "\n" + "Reported Date: " + dbIssue.getDateReported();

			JsonObject data = new JsonObject();
			data.addProperty("title", "Complaint registered");
			data.addProperty("message", "Your complaint has been registered with AttendMe");
			data.addProperty("description", details);

			fcmMessage.setData(data);
			fcmMessage.setTo(phoneDevice.getFcmToken());

			Thread t1 = new Thread(new SendFcmPushNotification(fcmMessage));
			t1.start();

			// send email to web admin if subscribed
			Machine machine = machineDAO.getById(dbIssue.getMachineId());
			if (machine != null) {
				String supportAssistance = machine.getSupportAssistance();
				if (supportAssistance.equalsIgnoreCase("MANUFACTURER")) {
					CompanyDetail manufacturer = companyDetailDAO.getById(machine.getManufacturerId());
					if (manufacturer != null) {					

						//send email/sms to manufacturer support contact if incident raised
						 
						// EMAIL
						String manContactEmail = manufacturer.getCustSupportEmail();
						if (manContactEmail != null) {
							JsonObject jsonObj = MailUtils.getComplaintReportedMail(manContactEmail, dbIssue,
									phoneDevice);
							Thread t5 = new Thread(new SendMail(jsonObj));
							t5.start();
							log.info("Complaint registration email sent to support contact details.");
						}
						// SMS
						String manContactPhone = manufacturer.getCustSupportPhone();
						if (manContactPhone != null) {
							JsonObject jsonObjSms = new JsonObject();
							jsonObjSms.addProperty("toMobileNumber", manContactPhone.replaceAll("\\+", ""));
							/*jsonObjSms.addProperty("smsMessage",
									"A new complaint has been registered with AttendMe. "
											+ "Complaint Number- " + dbIssue.getIssueNumber());*/
							jsonObjSms.addProperty("smsMessage",
									"A new complaint has been registered with AttendMe. "
									+ "Complaint No: " + dbIssue.getIssueNumber()+", "
									+ "Customer Name: "+ dbIssue.getCustomerCompanyName()+", "
									+ "Operator Mobile: "+ phoneDevice.getPhoneNumber()); 
							 
							Thread t6 = new Thread(new SendSMS(jsonObjSms));
							t6.start();
							log.info("Complaint registration sms sent to support contact details.");
						}					
												
						// not subscribed
						String subscriptionStatus = manufacturer.getCurSubscriptionStatus();
						if (subscriptionStatus.equals(CompanyDetail.CUR_SUBSCRIPTION_STATUS.INACTIVE)) {
							//manufacturer not onboard
							log.severe("A manufacturer has not subscribed to AttendMe.");
						} else {
							// send email/sms to web admin
							User manufacturerWebAdmin = userDAO.getByCompanyDetailsIdAndRole(manufacturer.getId(),
									User.ROLE_TYPE.MANUFACTURERADMIN.toString());
							//EMAIL
							String manufacturerWebAdminEmail = manufacturerWebAdmin.getEmail();							
							if (manufacturerWebAdminEmail != null) {
								JsonObject jsonObj = MailUtils.getComplaintReportedMail(manufacturerWebAdminEmail,
										dbIssue, phoneDevice);

								Thread t2 = new Thread(new SendMail(jsonObj));
								t2.start();

								log.info("Email sent to manufacturer web admin");
							} else {
								log.info("There is no manufacturer web admin email");								

							} 
							//SMS
							String manufacturerWebAdminMobile = manufacturerWebAdmin.getMobile();
							if (manufacturerWebAdminMobile != null) {
								JsonObject jsonObjSms = new JsonObject();
								jsonObjSms.addProperty("toMobileNumber", manufacturerWebAdminMobile.replaceAll("\\+", ""));
								/*jsonObjSms.addProperty("smsMessage",
										"A new complaint has been registered with AttendMe. "
												+ "Complaint Number- " + dbIssue.getIssueNumber());*/
								jsonObjSms.addProperty("smsMessage",
										"A new complaint has been registered with AttendMe. "
										+ "Complaint No: " + dbIssue.getIssueNumber()+", "
										+ "Customer Name: "+ dbIssue.getCustomerCompanyName()+", "
										+ "Operator Mobile: "+ phoneDevice.getPhoneNumber()); 
								 
								Thread t6 = new Thread(new SendSMS(jsonObjSms));
								t6.start();
								log.info("Complaint registration sms sent to wen admin mobile.");
							}
							 
						}

					} else {
						log.severe("A manufacturer does not exist.");
					}

				} else if (supportAssistance.equalsIgnoreCase("RESELLER")) {

					CompanyDetail reseller = companyDetailDAO.getById(machine.getResellerId());
					if (reseller != null) {

						//send email/sms to reseller support contact if incident raised
						// EMAIL
						String manContactEmail = reseller.getCustSupportEmail();
						if (manContactEmail != null) {
							JsonObject jsonObj = MailUtils.getComplaintReportedMail(manContactEmail, dbIssue,
									phoneDevice);
							Thread t5 = new Thread(new SendMail(jsonObj));
							t5.start();
							log.info("Complaint registration email sent to support contact details.");
						}
						// SMS
						String manContactPhone = reseller.getCustSupportPhone();
						if (manContactPhone != null) {
							JsonObject jsonObjSms = new JsonObject();
							jsonObjSms.addProperty("toMobileNumber", manContactPhone.replaceAll("\\+", ""));
							/*jsonObjSms.addProperty("smsMessage",
									"A new complaint has been registered with AttendMe. "
											+ "Complaint Number- " + dbIssue.getIssueNumber());*/
							jsonObjSms.addProperty("smsMessage",
									"A new complaint has been registered with AttendMe. "
									+ "Complaint No: " + dbIssue.getIssueNumber()+", "
									+ "Customer Name: "+ dbIssue.getCustomerCompanyName()+", "
									+ "Operator Mobile: "+ phoneDevice.getPhoneNumber()); 
							 
							Thread t6 = new Thread(new SendSMS(jsonObjSms));
							t6.start();
							log.info("Complaint registration sms sent to support contact details.");
						}
						String subscriptionStatus = reseller.getCurSubscriptionStatus();

						// not subscribed
						if (subscriptionStatus.equals(CompanyDetail.CUR_SUBSCRIPTION_STATUS.INACTIVE)) {
							log.severe("A reseller has not subscribed to AttendMe.");
						} else {
							// send email to web admin
							User resellerWebAdmin = userDAO.getByCompanyDetailsIdAndRole(reseller.getId(),
									User.ROLE_TYPE.RESELLERADMIN.toString());
							String resellerWebAdminEmail = resellerWebAdmin.getEmail();
							//EMAIL
							if (resellerWebAdminEmail != null) {
								JsonObject jsonObj = MailUtils.getComplaintReportedMail(resellerWebAdminEmail, dbIssue,
										phoneDevice);
								// new SendMail(jsonObj).run();
								Thread t3 = new Thread(new SendMail(jsonObj));
								t3.start();
								log.info("Email sent to reseller web admin");
							} else {
								log.info("There is no reseller web admin email");					

							}
							//SMS
							String resellerWebAdminMobile = resellerWebAdmin.getMobile();
							if (resellerWebAdminMobile != null) {
								JsonObject jsonObjSms = new JsonObject();
								jsonObjSms.addProperty("toMobileNumber", resellerWebAdminMobile.replaceAll("\\+", ""));
								/*jsonObjSms.addProperty("smsMessage",
										"A new complaint has been registered with AttendMe. "
												+ "Complaint Number- " + dbIssue.getIssueNumber());*/
								jsonObjSms.addProperty("smsMessage",
										"A new complaint has been registered with AttendMe. "
										+ "Complaint No: " + dbIssue.getIssueNumber()+", "
										+ "Customer Name: "+ dbIssue.getCustomerCompanyName()+", "
										+ "Operator Mobile: "+ phoneDevice.getPhoneNumber()); 
								// send SMS
								Thread t8 = new Thread(new SendSMS(jsonObjSms));
								t8.start();
								log.info("Complaint registration sms sent to web admin mobile.");
							}
 
						}
					} else {
						log.severe("A reseller does not exist.");
					}
				}

			} else {
				log.severe("A machine does not exist.");
			}

			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("A complaint created successfully"))
					.build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("A complaint not created"))
					.build();
		}

	}

	@GET
	@UnitOfWork
	public Response listIssues(@Auth User authUser) {
		try {
			log.info(" In listIssues");
			return Response.status(Status.OK).entity(JsonUtils.getJson(issueDAO.findAll())).build();
		} catch (Exception e) {
			log.severe("Unable to find Issues " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Issues")).build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/machine/{machineId}")
	public Response listIssuesByMachineId(@Auth User authUser, @PathParam("machineId") long machineId) {
		try {
			log.info(" In listIssuesByMachineId");
			return Response.status(Status.OK).entity(JsonUtils.getJson(issueDAO.findAllByMachineId(machineId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Issues " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Issues")).build();
		}
	}

	/*
	 * @GET
	 * 
	 * @Path("/{category}/{parameter}")
	 * 
	 * @UnitOfWork public Response listIssuesByCategory(@Auth User
	 * authUser, @PathParam("category") String category,
	 * 
	 * @PathParam("parameter") long parameter) { try { log.info(
	 * " In listIssuesByCategory"); return Response.status(Status.OK)
	 * .entity(JsonUtils.getJson(issueDAO.getIssuesByCategory(category,
	 * parameter))).build(); } catch (Exception e) { log.severe(
	 * "Unable to find Issues " + e); return
	 * Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson(
	 * "Unable to find issues")).build(); } }
	 */

	@GET
	@Path("/{role}/{companyId}")
	@UnitOfWork
	public Response listIssuesByRoleAndCompanyId(@Auth User authUser, @PathParam("role") String role,
			@PathParam("companyId") long manResCompanyId) {
		try {
			log.info(" In listIssuesByRoleAndCompanyId");
			String supportAssistance = null;

			if (role.equals(User.ROLE_TYPE.MANUFACTURERADMIN.toString())) {
				// if manufacturer web admin
				supportAssistance = Machine.SUPPORT_ASSISTANCE.MANUFACTURER.toString();

			} else if (role.equals(User.ROLE_TYPE.RESELLERADMIN.toString())) {
				// if reseller web admin
				supportAssistance = Machine.SUPPORT_ASSISTANCE.RESELLER.toString();

			}
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(
							issueDAO.findAllBySupportAssitanaceAndCompanyId(supportAssistance, manResCompanyId)))
					.build();
		} catch (Exception e) {
			log.severe("Unable to find Issues " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find issues")).build();
		}
	}

	@GET
	@Path("/assignedto/{assignedToId}")
	@UnitOfWork
	public Response listIssuesByAssignedToId(@Auth User authUser, @PathParam("assignedToId") long assignedToId) {
		try {

			log.info(" In listIssuesByAssignedToId");
			return Response.status(Status.OK).entity(JsonUtils.getJson(issueDAO.findAllByAssignedToId(assignedToId)))
					.build();

		} catch (Exception e) {
			log.severe("Unable to find Issues " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find issues")).build();
		}
	}

	@POST
	@Path("/listbyfilter")
	@UnitOfWork
	public Response listIssuesBySearchFilter(@Auth User authUser,
			IssueSearchFilterPayloadDTO issueSearchFilterPayloadDTO) {
		try {
			log.info(" In listIssuesBySearchFilter");
			String supportAssistance = null;

			if (issueSearchFilterPayloadDTO.getManResRole().equals(User.ROLE_TYPE.MANUFACTURERADMIN.toString())) {
				// if manufacturer web admin
				supportAssistance = Machine.SUPPORT_ASSISTANCE.MANUFACTURER.toString();

			} else if (issueSearchFilterPayloadDTO.getManResRole().equals(User.ROLE_TYPE.RESELLERADMIN.toString())) {
				// if reseller web admin
				supportAssistance = Machine.SUPPORT_ASSISTANCE.RESELLER.toString();

			}else if (issueSearchFilterPayloadDTO.getManResRole().equals(User.ROLE_TYPE.SUPERADMIN.toString())) {
				// if superadmin
				supportAssistance="";
			}	
			return Response.status(Status.OK)
					.entity(JsonUtils
							.getJson(issueDAO.findAllBySearchFilter(supportAssistance, issueSearchFilterPayloadDTO)))
					.build();

		} catch (Exception e) {
			log.severe("Unable to find Issues " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find issues")).build();
		}

	}

	@POST
	@Path("/listbyfilter/excel")
	@UnitOfWork
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getIssuesListBySearchFilterInExcel(@Auth User authUser,
			IssueSearchFilterPayloadDTO issueSearchFilterPayloadDTO) {
		try {
			log.info(" In getIssuesListBySearchFilterInExcel");

			String supportAssistance = null;

			if (issueSearchFilterPayloadDTO.getManResRole().equals(User.ROLE_TYPE.MANUFACTURERADMIN.toString())) {
				// if manufacturer web admin
				supportAssistance = Machine.SUPPORT_ASSISTANCE.MANUFACTURER.toString();

			} else if (issueSearchFilterPayloadDTO.getManResRole().equals(User.ROLE_TYPE.RESELLERADMIN.toString())) {
				// if reseller web admin
				supportAssistance = Machine.SUPPORT_ASSISTANCE.RESELLER.toString();

			}else if (issueSearchFilterPayloadDTO.getManResRole().equals(User.ROLE_TYPE.SUPERADMIN.toString())) {
				// if superadmin
				supportAssistance="";
			}

			List<Issue> issueList = issueDAO.findAllBySearchFilter(supportAssistance, issueSearchFilterPayloadDTO);

			// Blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();

			// Create a blank sheet
			XSSFSheet sheet = workbook.createSheet("Complaint List");

			// This data needs to be written (Object[])
			Map<String, Object[]> data = new TreeMap<String, Object[]>();
			data.put("1",
					new Object[] { "IssueNumber", "MachineModel", "MachineSerial", "MachineInstallationDate",
							"CustomerCompanyName", "MachineLocation", "ComplaintDetails", "OperatorName",
							"ReportedDate", "AssignedDate", "TechnicianName", "ClosedDate", "FailureReason",
							"ActionTaken", "Status" });

			for (int i = 0; i < issueList.size(); i++) {
				String installationDate = null;
				String reportedDate = null;
				String assignedDate = null;
				String closedDate = null;

				if (issueList.get(i).getMachineInstallationDate() != null) {
					installationDate = issueList.get(i).getMachineInstallationDate().toString();
				}
				if (issueList.get(i).getDateReported() != null) {
					reportedDate = issueList.get(i).getDateReported().toString();
				}
				if (issueList.get(i).getDateAssigned() != null) {
					assignedDate = issueList.get(i).getDateAssigned().toString();
				}
				if (issueList.get(i).getDateClosed() != null) {
					closedDate = issueList.get(i).getDateClosed().toString();
				}

				data.put(Integer.toString(i + 2),
						new Object[] { issueList.get(i).getIssueNumber(), issueList.get(i).getMachineModelNumber(),
								issueList.get(i).getMachineSerialNumber(), installationDate,
								issueList.get(i).getCustomerCompanyName(),
								issueList.get(i).getMachineLocationNameAddress(), issueList.get(i).getDetails(),
								issueList.get(i).getPhonedeviceOperatorName(), reportedDate, assignedDate,
								issueList.get(i).getTechnicianName(), closedDate, issueList.get(i).getFailureReason(),
								issueList.get(i).getActionTaken(), issueList.get(i).getStatus() });
			}

			// Iterate over data and write to sheet
			Set<String> keyset = data.keySet();
			int rownum = 0;
			for (String key : keyset) {
				Row row = sheet.createRow(rownum++);
				Object[] objArr = data.get(key);
				int cellnum = 0;
				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String)
						cell.setCellValue((String) obj);
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
				}
			}
			// Write the workbook in ByteArrayOutputStream
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			workbook.write(byteArrayOutputStream);
			byteArrayOutputStream.close();
			log.info("Workbook written in byteArrayOutputStream");

			// implement StreamingOutput to return as response
			StreamingOutput stream = new StreamingOutput() {
				public void write(OutputStream out) throws IOException, WebApplicationException {
					try {
						out.write(byteArrayOutputStream.toByteArray());
						log.info("byteArrayOutputStream written in StreamingOutput");
					} catch (Exception e) {
						log.severe("Failed to write byteArrayOutputStream into StreamingOutput");
						throw new WebApplicationException(e);
					} finally {
						out.flush();
						out.close();
					}
				}
			};
			// finally return response as required stream
			return Response.ok(stream)
					.header("Content-Disposition", "attachment; filename=\"complaint_list.xlsx" + "\"").build();

		} catch (Exception e) {
			log.severe("Failed to download excel. " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Failed to download as excel"))
					.build();
		}

	}

}