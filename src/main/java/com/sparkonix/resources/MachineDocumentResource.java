package com.sparkonix.resources;

import java.io.File;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.MachineDocumentDAO;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/machinedoc")
@Produces(MediaType.APPLICATION_JSON)
public class MachineDocumentResource {

	private final MachineDocumentDAO machineDocumentDAO;
	private final Logger log = Logger.getLogger(MachineDocumentResource.class.getName());
	private final String mahineDocsLocation;

	public MachineDocumentResource(MachineDocumentDAO machineDocumentDAO, String mahineDocsLocation) {
		this.machineDocumentDAO = machineDocumentDAO;
		this.mahineDocsLocation = mahineDocsLocation;
	}

	@GET
	@UnitOfWork
	@Path("/{machineDocId}")
	public Response getMachineDocById(@Auth User authUser, @PathParam("machineDocId") long machineDocId) {
		try {
			log.info(" In getMachineDocById");
			return Response.status(Status.OK).entity(JsonUtils.getJson(machineDocumentDAO.getById(machineDocId)))
					.build();
		} catch (Exception e) {
			log.severe("Unable to find Machine Document" + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Machine Document"))
					.build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/fetch/{manufacturerId}/{docName}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response fetchMachineDocByFileName(@PathParam("manufacturerId") String manufacturerId,
			@PathParam("docName") String docName) {

		File filePath = new File(this.mahineDocsLocation + File.separator + manufacturerId + File.separator + docName);

		if (filePath.exists()) {
			return Response.ok(filePath, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "inline; filename=\"" + docName + "\"").build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Machine Document"))
					.build();
		}
	}
}
