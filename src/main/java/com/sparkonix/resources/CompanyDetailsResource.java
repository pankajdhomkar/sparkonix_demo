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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sparkonix.dao.CompanyDetailDAO;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.PhoneDeviceDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.CustomerDetailsDTO;
import com.sparkonix.entity.dto.ManResDTO;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.MailUtils;
import com.sparkonix.utils.SendMail;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/companydetails")
@Produces(MediaType.APPLICATION_JSON)
public class CompanyDetailsResource {

	private final CompanyDetailDAO companyDetailDAO;
	private final CompanyLocationDAO companyLocationDAO;
	private final MachineDAO machineDAO;
	private final PhoneDeviceDAO phoneDeviceDAO;
	private final UserDAO userDAO;

	private final Logger log = Logger.getLogger(CompanyDetailsResource.class.getName());

	public CompanyDetailsResource(CompanyDetailDAO companyDetailDAO, CompanyLocationDAO companyLocationDAO,
			MachineDAO machineDAO, PhoneDeviceDAO phoneDeviceDAO, UserDAO userDAO) {
		this.companyDetailDAO = companyDetailDAO;
		this.companyLocationDAO = companyLocationDAO;
		this.machineDAO = machineDAO;
		this.phoneDeviceDAO = phoneDeviceDAO;
		this.userDAO = userDAO;
	}

	@POST
	@UnitOfWork
	public Response createCompanyDetail(@Auth User authUser, CompanyDetail companyDetail) {
		try {
			//synchronized(CompanyDetail.class){
			if (companyDetail.getCompanyType().equals("CUSTOMER")) {

				if (companyDetail.getPan().length() != 10) {
					return Response.status(Status.BAD_REQUEST)
							.entity(JsonUtils.getErrorJson("Enter 10 digit in company PAN.")).build();
				}

				CompanyDetail dbCompanyDetail = companyDetailDAO
						.findCompanyDetailByPanAndCompanyType(companyDetail.getPan(), companyDetail.getCompanyType());

				if (dbCompanyDetail != null) {
					// exist
					Gson gson = new Gson();
					JsonObject json = new JsonObject();
					json.addProperty("message", "A customer already exist with this PAN, Please add factory location.");
					json.addProperty("entity", gson.toJson(dbCompanyDetail));

					return Response.status(Status.OK).entity(json.toString()).build();
				} else {
					// not exist
					Gson gson = new Gson();
					JsonObject json = new JsonObject();
					json.addProperty("message", "New customer created successfully.");
					json.addProperty("entity", gson.toJson(companyDetailDAO.save(companyDetail)));

					// send email to super admin
					JsonObject jsonObj = MailUtils.getAddCompanyMail(companyDetail, authUser);
					//new SendMail(jsonObj).run();
					Thread t1 = new Thread(new SendMail(jsonObj));
					t1.start();
					
					log.info("Email sent to super admin");

					return Response.status(Status.OK).entity(json.toString()).build();
					// return
					// Response.status(Status.OK).entity(companyDetailDAO.save(companyDetail)).build();
				}
			}
			//end of synch
			//}
			
		} catch (Exception e) {
			log.severe("Failed to create customer. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Failed to create customer"))
					.build();
		}
		return null;

		/*try {
			if (companyDetail.getCompanyType().equals("MANUFACTURER")
					|| companyDetail.getCompanyType().equals("RESELLER")) {

				CompanyDetail dbCompanyDetail = companyDetailDAO
						.findCompanyDetailByPanAndCompanyType(companyDetail.getPan(), companyDetail.getCompanyType());

				if (dbCompanyDetail != null) {
					// exist
					return Response.status(Status.BAD_REQUEST)
							.entity(JsonUtils.getErrorJson("Company Detail with given PAN is already exist.")).build();
				} else {
					// not exist
					return Response.status(Status.OK).entity(companyDetailDAO.save(companyDetail)).build();
				}

			}
		} catch (Exception e) {
			log.severe("Failed to create Company detail. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Failed to create Company detail.")).build();
		}
		return null;*/

	}

	@GET
	@UnitOfWork
	public Response listCompanyDetails(@Auth User authUser) {
		try {
			log.info("In listCompanyDetails");
			return Response.status(Status.OK).entity(JsonUtils.getJson(companyDetailDAO.findAll())).build();
		} catch (Exception e) {
			log.severe("Unable to find Compnay Details " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Company Details"))
					.build();
		}
	}

	@GET
	@Path("/{companyType}")
	@UnitOfWork
	public Response listCompanyDetailsByCompanyType(@Auth User authUser, @PathParam("companyType") String companyType) {
		try {
			log.info("In listCompanyDetailsByCompanyType");
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(companyDetailDAO.findAllByCompanyType(companyType))).build();
		} catch (Exception e) {
			log.severe("Unable to find Company Details by Company Type" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Company Details by Company Type")).build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/{onBoardedById}/{userRole}/{companyType}")
	public Response listCompanyDetailsOnBoardedById(@Auth User authUser, @PathParam("onBoardedById") long onBoardedById,
			@PathParam("userRole") String userRole, @PathParam("companyType") String companyType) {
		try {
			log.info("In listCompanyDetailsOnBoardedById");

			// used for manage customer view page
			if (companyType.equalsIgnoreCase("CUSTOMER")) {
				// get count of location+machines+operators
				List<CompanyDetail> listCompanyDetails = new ArrayList<>();

				if (userRole.equals("SUPERADMIN")) {
					listCompanyDetails = companyDetailDAO.findAllByCompanyType(companyType);

				} else if (userRole.equals("SALESTEAM") || userRole.equals(User.ROLE_TYPE.MANUFACTURERADMIN.toString())
						|| userRole.equals(User.ROLE_TYPE.RESELLERADMIN.toString())) {
					listCompanyDetails = companyDetailDAO.findAllByOnBoardedId(onBoardedById, userRole, companyType);
				}

				List<CustomerDetailsDTO> listCustomerDetailsDTO = new ArrayList<>();

				for (int i = 0; i < listCompanyDetails.size(); i++) {
					CustomerDetailsDTO customerDetailsDTO = new CustomerDetailsDTO();
					long customerId = listCompanyDetails.get(i).getId();
					long companyLocationCount = companyLocationDAO.getCountByCustomerIdAndOnBoardedBy(customerId,
							onBoardedById, userRole);
					long machinesCount = machineDAO.getCountByCustomerIdAndOnBoardedBy(customerId, onBoardedById,
							userRole);
					long operatorsCount = phoneDeviceDAO.getCountByCustomerIdAndOnBoardedBy(customerId, onBoardedById,
							userRole);

					customerDetailsDTO.setCompanyDetail(listCompanyDetails.get(i));
					customerDetailsDTO.setFactoryLocationsCount(companyLocationCount);
					customerDetailsDTO.setMachinesCount(machinesCount);
					customerDetailsDTO.setOperatorsCount(operatorsCount);

					listCustomerDetailsDTO.add(customerDetailsDTO);
				}
				return Response.status(Status.OK).entity(JsonUtils.getJson(listCustomerDetailsDTO)).build();

			}

			// used for Manufacturers and Resellers view page
			if (companyType.equalsIgnoreCase("MANUFACTURER") || companyType.equalsIgnoreCase("RESELLER")) {

				// show all to super admin
				if (userRole.equals("SUPERADMIN")) {
					return Response.status(Status.OK)
							.entity(JsonUtils.getJson(companyDetailDAO.findAllByCompanyType(companyType))).build();

				} else if (userRole.equals("SALESTEAM")
						|| userRole.equals(User.ROLE_TYPE.MANUFACTURERADMIN.toString())) {
					// show only onBoarded by him
					return Response.status(Status.OK)
							.entity(JsonUtils
									.getJson(companyDetailDAO.findAllByOnBoardedAndType(onBoardedById, companyType)))
							.build();
				}
			}
		} catch (Exception e) {
			log.severe("Unable to find Company Details by onBoardedID " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Company Details for logged in user")).build();
		}
		return null;
	}

	/**
	 * used for adding new Manufacturer/Reseller by salesteam/superadmin
	 * 
	 * @param authUser
	 *            user is authenticated by his username and password.
	 * @param manResDTO
	 *            its payload which contain object of {@link CompanyDetail} and
	 *            {@link User}
	 * @return success message if manufacturer/reseller created
	 */
	@POST
	@UnitOfWork
	@Path("addmanres")
	public Response createCompanyDetailForManRes(@Auth User authUser, ManResDTO manResDTO) {

		CompanyDetail manResDetail = manResDTO.getManResDetail();
		User manResWebAdmin = manResDTO.getWebAdminUser();

		if (manResDetail.getPan().length() != 10) {
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Enter 10 digit in company PAN."))
					.build();
		}

		CompanyDetail dbCompanyDetail = companyDetailDAO.findCompanyDetailByPanAndCompanyType(manResDetail.getPan(),
				manResDetail.getCompanyType());
		User dbUser = null;
		if (manResWebAdmin != null) {
			dbUser = userDAO.findByEmail(manResWebAdmin.getEmail());
		}
		if (dbCompanyDetail != null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson(
							"A " + manResDetail.getCompanyType().toLowerCase() + " already exists with this PAN."))
					.build();
		} else if (dbUser != null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("A web admin already exists with this email.")).build();
		}
		try {
			// save CompanyDetail
			CompanyDetail newCompanyDetail = companyDetailDAO.save(manResDetail);

			if (manResWebAdmin != null) {
				String role = null;
				if (newCompanyDetail.getCompanyType().equals(CompanyDetail.COMPANY_TYPE.MANUFACTURER.toString())) {
					role = User.ROLE_TYPE.MANUFACTURERADMIN.toString();

				} else if (newCompanyDetail.getCompanyType().equals(CompanyDetail.COMPANY_TYPE.RESELLER.toString())) {
					role = User.ROLE_TYPE.RESELLERADMIN.toString();
				}

				manResWebAdmin.setRole(role);
				manResWebAdmin.setCompanyDetailsId(newCompanyDetail.getId());

				// save User
				userDAO.save(manResWebAdmin);
			}

			// send email to super admin
			JsonObject jsonObj = MailUtils.getAddCompanyMail(newCompanyDetail, authUser);
			//new SendMail(jsonObj).run();
			Thread t2 = new Thread(new SendMail(jsonObj));
			t2.start();
			log.info("Email sent to super admin");

			return Response.status(Status.OK)
					.entity(JsonUtils.getSuccessJson(
							"A " + newCompanyDetail.getCompanyType().toLowerCase() + " has been created successfully."))
					.build();
		} catch (Exception e) {
			log.severe("Failed to create company detail. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Failed to create company detail.")).build();
		}

	}
}
