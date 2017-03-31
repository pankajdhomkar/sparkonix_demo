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

import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.User;
import com.sparkonix.entity.jsonb.CompanyLocationAddress;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/companylocation/{companyLocationId}")
@Produces(MediaType.APPLICATION_JSON)
public class CompanyLocationResource {

	private final CompanyLocationDAO companyLocationDAO;
	private final Logger log = Logger.getLogger(CompanyLocationResource.class.getName());

	public CompanyLocationResource(CompanyLocationDAO companyLocationDAO) {
		this.companyLocationDAO = companyLocationDAO;
	}

	@GET
	@UnitOfWork
	public Response getCompanyLocationById(@Auth User authUser,
			@PathParam("companyLocationId") long companyLocationId) {
		try {
			log.info(" In getCompanyLocationById");

			CompanyLocation dbCompanyLocation = companyLocationDAO.getById(companyLocationId);

			CompanyLocation companyLocation = new CompanyLocation();

			companyLocation.setId(dbCompanyLocation.getId());
			companyLocation.setCompanyDetailsId(dbCompanyLocation.getCompanyDetailsId());
			companyLocation.setContactPerson(dbCompanyLocation.getContactPerson());
			companyLocation.setContactMobile(dbCompanyLocation.getContactMobile());
			companyLocation.setOnBoardedBy(dbCompanyLocation.getOnBoardedBy());

			// convert string to json object
			String address = dbCompanyLocation.getAddress();
			companyLocation.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));

			return Response.status(Status.OK).entity(companyLocation).build();
		} catch (Exception e) {
			log.severe("Unable to find Company Location " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Company Location"))
					.build();
		}
	}

	@PUT
	@UnitOfWork
	public Response updateCompanyLocation(@Auth User authUser, @PathParam("companyLocationId") long companyLocationId,
			CompanyLocation companyLocation) {
		try {
			log.info(" In updateCompanyLocation");

			CompanyLocation dbCompanyLocation = companyLocationDAO.getById(companyLocationId);

			if (dbCompanyLocation == null) {
				log.severe(" Unable to find company location by id");
			} else {

				dbCompanyLocation.setContactPerson(companyLocation.getContactPerson());
				dbCompanyLocation.setContactMobile(companyLocation.getContactMobile());

				// convert string address into jsonb object
				dbCompanyLocation
						.setAddress(CompanyLocationAddress.toJson(companyLocation.getCompanyLocationAddress()));

				companyLocationDAO.save(dbCompanyLocation);
				log.info("Company location details updated");
			}
			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Company location updated successfully"))
					.build();
		} catch (Exception e) {
			log.severe(" Unable to update company location. Error: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to update company location")).build();
		}
	}

}
