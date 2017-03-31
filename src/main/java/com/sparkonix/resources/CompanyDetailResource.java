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

import com.sparkonix.dao.CompanyDetailDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.CompanyPanDTO;
import com.sparkonix.entity.dto.ManResDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/companydetail")
@Produces(MediaType.APPLICATION_JSON)
public class CompanyDetailResource {

	private final CompanyDetailDAO companyDetailDAO;
	private final UserDAO userDAO;
	private final Logger log = Logger.getLogger(CompanyDetailResource.class.getName());

	public CompanyDetailResource(CompanyDetailDAO companyDetailDAO, UserDAO userDAO) {
		this.companyDetailDAO = companyDetailDAO;
		this.userDAO = userDAO;
	}

	@GET
	@UnitOfWork
	@Path("/{compDetailId}")
	public Response getCompanyDetailById(@Auth User authUser, @PathParam("compDetailId") long companyDetailId) {
		try {
			log.info(" In getCompanyDetailById");
			return Response.status(Status.OK).entity(JsonUtils.getJson(companyDetailDAO.getById(companyDetailId)))
					.build();
		} catch (Exception e) {
			log.severe("Unable to find Company detail " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find company detail"))
					.build();
		}
	}

	@PUT
	@UnitOfWork
	@Path("/{compDetailId}")
	public Response updateCompanyDetail(@Auth User authUser, CompanyDetail companyDetail,
			@PathParam("compDetailId") long companyDetailId) {
		try {
			log.info(" In updateCompanyDetail");
			if (authUser == null) {
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Authorization Failed"))
						.build();
			}
			CompanyDetail dbCompanyDetail = companyDetailDAO.getById(companyDetailId);
			if (dbCompanyDetail != null) {
				dbCompanyDetail.setCompanyName(companyDetail.getCompanyName());
				dbCompanyDetail.setPan(companyDetail.getPan());
				dbCompanyDetail.setCustSupportName(companyDetail.getCustSupportName());
				dbCompanyDetail.setCustSupportPhone(companyDetail.getCustSupportPhone());
				dbCompanyDetail.setCustSupportEmail(companyDetail.getCustSupportEmail());
				dbCompanyDetail.setCurSubscriptionType(companyDetail.getCurSubscriptionType());
				dbCompanyDetail.setCurSubscriptionStatus(companyDetail.getCurSubscriptionStatus());
				dbCompanyDetail.setCurSubscriptionStartDate(companyDetail.getCurSubscriptionStartDate());
				dbCompanyDetail.setCurSubscriptionEndDate(companyDetail.getCurSubscriptionEndDate());

				companyDetailDAO.save(dbCompanyDetail);
			}
			log.info("Company Detail updated");
			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Company detail updated successfully"))
					.build();
		} catch (Exception e) {
			log.severe("Unable to Update  " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to Update ")).build();
		}
	}

	/**
	 * this method is used to get the {@link CompanyDetail} & {@link User} by
	 * using companyDetailId for edit Manufacturer/Reseller by
	 * salesTeam/superAdmin
	 * 
	 * @param authUser
	 *            user is authenticated by his username and password.
	 * @param companyDetailId
	 * @return response as object of {@link ManResDTO}
	 */
	@GET
	@UnitOfWork
	@Path("/editmanres/{companyDetailId}")
	public Response getManResDTO(@Auth User authUser, @PathParam("companyDetailId") long companyDetailId) {

		log.info("In getManResDTO");
		ManResDTO manResDTO = new ManResDTO();
		try {
			CompanyDetail dbCompanyDetail = companyDetailDAO.getById(companyDetailId);
			if (dbCompanyDetail != null) {
				manResDTO.setManResDetail(dbCompanyDetail);
			} else {
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to find company detail.")).build();
			}

			String role = null;
			if (dbCompanyDetail.getCompanyType().equals(CompanyDetail.COMPANY_TYPE.MANUFACTURER.toString())) {
				role = User.ROLE_TYPE.MANUFACTURERADMIN.toString();
			} else if (dbCompanyDetail.getCompanyType().equals(CompanyDetail.COMPANY_TYPE.RESELLER.toString())) {
				role = User.ROLE_TYPE.RESELLERADMIN.toString();
			}

			User dbUser = userDAO.getByCompanyDetailsIdAndRole(companyDetailId, role);
			if (dbUser != null) {
				manResDTO.setWebAdminUser(dbUser);
			} else {
				/*
				 * return Response.status(Status.BAD_REQUEST)
				 * .entity(JsonUtils.getErrorJson(
				 * "Unable to find company detail.")).build();
				 */
				manResDTO.setWebAdminUser(null);
			}

			return Response.status(Status.OK).entity(manResDTO).build();

		} catch (Exception e) {
			log.severe("Unable to find company detail by id. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find company detail."))
					.build();
		}

	}

	@PUT
	@UnitOfWork
	@Path("/editmanres/{companyDetailId}")
	public Response updateManResDTO(@Auth User authUser, @PathParam("companyDetailId") long companyDetailId,
			ManResDTO manResDTO) {

		log.info("In updateManResDTO");
		CompanyDetail manResDetail = manResDTO.getManResDetail();
		User manResWebAdmin = manResDTO.getWebAdminUser();

		try {

			if (manResDetail == null && manResWebAdmin == null) {
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to update company detail.")).build();
			} else {

				CompanyDetail dbCompanyDetail = companyDetailDAO.getById(companyDetailId);

				// update company details
				dbCompanyDetail.setCompanyName(manResDetail.getCompanyName());

				dbCompanyDetail.setCustSupportName(manResDetail.getCustSupportName());
				dbCompanyDetail.setCustSupportPhone(manResDetail.getCustSupportPhone());
				dbCompanyDetail.setCustSupportEmail(manResDetail.getCustSupportEmail());

				dbCompanyDetail.setCurSubscriptionType(manResDetail.getCurSubscriptionType());
				dbCompanyDetail.setCurSubscriptionStartDate(manResDetail.getCurSubscriptionStartDate());
				dbCompanyDetail.setCurSubscriptionEndDate(manResDetail.getCurSubscriptionEndDate());
				dbCompanyDetail.setCurSubscriptionStatus(manResDetail.getCurSubscriptionStatus());

				companyDetailDAO.save(dbCompanyDetail);

				// update web admin user
				if (manResWebAdmin != null) {
					User dbUser = userDAO.getById(manResWebAdmin.getId());

					dbUser.setName(manResWebAdmin.getName());
					dbUser.setMobile(manResWebAdmin.getMobile());
					dbUser.setAltEmail(manResWebAdmin.getAltEmail());

					userDAO.save(dbUser);
				}

			}
			return Response.status(Status.OK)
					.entity(JsonUtils.getSuccessJson(manResDetail.getCompanyType() + " has been updated successfully."))
					.build();

		} catch (Exception e) {
			log.severe("Unable to update company detail. Error: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to update company detail.")).build();
		}

	}

	@GET
	@UnitOfWork
	@Path("/check/{companyType}/{companyPan}")
	public Response checkByCompanyTypeAndCompanyPan(@Auth User authUser, @PathParam("companyType") String companyType,
			@PathParam("companyPan") String companyPan) {
		try {
			log.info(" In checkByCompanyTypeAndCompanyPan");
			CompanyDetail companyDetail = companyDetailDAO.findCompanyDetailByPanAndCompanyType(companyPan,
					companyType);
			CompanyPanDTO companyPanDTO = new CompanyPanDTO();

			if (companyDetail == null) {
				companyPanDTO.setCompanyPanExist(false);
			} else {
				companyPanDTO.setCompanyPanExist(true);
			}
			return Response.status(Status.OK).entity(JsonUtils.getJson(companyPanDTO)).build();

		} catch (Exception e) {
			log.severe("Unable to find Company detail by PAN" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find company detail by PAN")).build();
		}
	}

}
