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

import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.User;
import com.sparkonix.entity.jsonb.CompanyLocationAddress;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/companylocations")
@Produces(MediaType.APPLICATION_JSON)
public class CompanyLocationsResource {

	private final CompanyLocationDAO companyLocationDAO;
	private final Logger log = Logger.getLogger(CompanyLocationsResource.class.getName());

	public CompanyLocationsResource(CompanyLocationDAO companyLocationDAO) {
		this.companyLocationDAO = companyLocationDAO;
	}

	@POST
	@UnitOfWork
	public CompanyLocation createCompanyLocation(@Auth User authUser, CompanyLocation companyLocation)
			throws Exception {
		// create jsonb object string for address field in db
		companyLocation.setAddress(CompanyLocationAddress.toJson(companyLocation.getCompanyLocationAddress()));
		return companyLocationDAO.save(companyLocation);
	}

	@GET
	@UnitOfWork
	public Response listCompanyLocations(@Auth User authUser) {
		try {
			log.info(" In listCompanyLocations");

			List<CompanyLocation> companyLocationList = companyLocationDAO.findAll();

			List<CompanyLocation> locationList = new ArrayList<>();

			for (int i = 0; i < companyLocationList.size(); i++) {
				CompanyLocation companyLocation = new CompanyLocation();

				companyLocation.setId(companyLocationList.get(i).getId());
				companyLocation.setCompanyDetailsId(companyLocationList.get(i).getCompanyDetailsId());
				companyLocation.setContactPerson(companyLocationList.get(i).getContactPerson());
				companyLocation.setContactMobile(companyLocationList.get(i).getContactMobile());
				companyLocation.setOnBoardedBy(companyLocationList.get(i).getOnBoardedBy());

				String address = companyLocationList.get(i).getAddress();
				companyLocation.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));

				locationList.add(companyLocation);
			}
			return Response.status(Status.OK).entity(locationList).build();

		} catch (Exception e) {
			log.severe("Unable to find Company Location " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Company Location"))
					.build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/{companyId}/{onBoardedById}")
	public Response listCompanyLocationsByCompanyAndOnBoardedId(@Auth User authUser,
			@PathParam("companyId") long companyId, @PathParam("onBoardedById") long onBoardedById) {
		try {
			log.info(" In listCompanyLocationsByCompanyAndOnBoardedId");
			return Response.status(Status.OK)
					.entity(JsonUtils
							.getJson(companyLocationDAO.findAllByCompanyIdAndOnBoardedId(companyId, onBoardedById)))
					.build();
		} catch (Exception e) {
			log.severe("Unable to find Company Location " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Company Location"))
					.build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/{companyId}")
	public Response listCompanyLocationsByCompanyId(@Auth User authUser, @PathParam("companyId") long companyId) {
		try {
			log.info(" In listCompanyLocationsByCompanyId");
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(companyLocationDAO.findAllByCompanyId(companyId))).build();
		} catch (Exception e) {
			log.severe("Unable to find Company Location " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Company Location"))
					.build();
		}
	}

}
