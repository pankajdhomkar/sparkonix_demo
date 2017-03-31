package com.sparkonix.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
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

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.sparkonix.ApplicationContext;
import com.sparkonix.dao.CompanyDetailDAO;
import com.sparkonix.dao.MachineDocumentDAO;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.MachineDocument;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.MachineDocumentDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/machinedocs")
@Produces(MediaType.APPLICATION_JSON)
public class MachineDocumentsResource {

	private final MachineDocumentDAO machineDocumentDAO;
	private final CompanyDetailDAO companyDetailDAO;
	private final Logger log = Logger.getLogger(MachineDocumentsResource.class.getName());
	private final String mahineDocsLocation;

	public MachineDocumentsResource(MachineDocumentDAO machineDocumentDAO, String mahineDocsLocation,
			CompanyDetailDAO companyDetailDAO) {
		this.machineDocumentDAO = machineDocumentDAO;
		this.mahineDocsLocation = mahineDocsLocation;
		this.companyDetailDAO = companyDetailDAO;
	}

	@POST
	@UnitOfWork
	public Response uploadMachineDocument(@Auth User authUser, FormDataMultiPart multiPart) throws Exception {
		FormDataBodyPart manId = multiPart.getField("manufacturerid");
		String manufacturerId = manId.getEntityAs(String.class);
		FormDataBodyPart model = multiPart.getField("modelnum");
		String modelNumber = model.getEntityAs(String.class);
		FormDataBodyPart desc = multiPart.getField("description");
		String description = desc.getEntityAs(String.class);
		try {
			// fetch file stream, save in physical location
			// making directory for storing newly uploaded file
			String path = this.mahineDocsLocation + File.separator + manufacturerId;
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}

			List<FormDataBodyPart> bodyParts = multiPart.getFields("machinedocs");
			String fileName;
			for (FormDataBodyPart part : bodyParts) {
				InputStream is = part.getValueAs(InputStream.class);
				fileName = part.getFormDataContentDisposition().getFileName();
				String fileNameWithPath = path + File.separator + fileName;
				// write the inputStream to file in a physical location
				try {
					OutputStream outputStream = new FileOutputStream(new File(fileNameWithPath));
					int read;
					byte[] bytes = new byte[1024];

					while ((read = is.read(bytes)) != -1) {
						outputStream.write(bytes, 0, read);
					}
					outputStream.flush();
					outputStream.close();
				} catch (FileNotFoundException e) {
					log.severe("File Not Found :" + e);
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson(e.getMessage())).build();
				} catch (IOException e) {
					log.severe("IOException :" + e);
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson(e.getMessage())).build();
				}
				// make entries in database
				if (fileName != null) {
					MachineDocument newDocument = new MachineDocument();
					newDocument.setManufacturerId(Long.parseLong(manufacturerId));
					newDocument.setModelNumber(modelNumber);
					newDocument.setDocumentPath(fileName);
					newDocument.setDescription(description);

					machineDocumentDAO.save(newDocument);
					log.info("File Uploaded Successfully");
				}
			}
		} catch (Exception ex) {
			log.severe("Unable To Upload Files :" + ex);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson(ex.getMessage())).build();
		}
		return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Files Uploaded Successfully")).build();
	}

	/*
	 * @GET
	 * 
	 * @UnitOfWork public Response listMachineDocuments(@Auth User authUser) {
	 * try { log.info(" In listMachineDocuments"); return
	 * Response.status(Status.OK).entity(JsonUtils.getJson(machineDocumentDAO.
	 * findAll())).build(); } catch (Exception e) { log.severe(
	 * "Unable to find Machine Documents " + e); return
	 * Response.status(Status.BAD_REQUEST) .entity(JsonUtils.getErrorJson(
	 * "Unable to find Machine Documents")).build(); } }
	 */

	@GET
	@UnitOfWork
	public Response listMachineDocuments(@Auth User authUser) {
		try {
			log.info(" In listMachineDocuments");
			/*
			 * return Response.status(Status.OK).entity(JsonUtils.getJson(
			 * machineDocumentDAO.findAll())).build();
			 */
			List<MachineDocument> machineDocumentList = machineDocumentDAO.findAll();

			List<MachineDocumentDTO> newMachineDocumentList = new ArrayList<>();

			for (int i = 0; i < machineDocumentList.size(); i++) {

				MachineDocumentDTO machineDocumentDTO = new MachineDocumentDTO();
				machineDocumentDTO.setId(machineDocumentList.get(i).getId());
				machineDocumentDTO.setManufacturerId(machineDocumentList.get(i).getManufacturerId());
				machineDocumentDTO.setModelNumber(machineDocumentList.get(i).getModelNumber());
				machineDocumentDTO.setDescription(machineDocumentList.get(i).getDescription());
				machineDocumentDTO.setDocumentPath(machineDocumentList.get(i).getDocumentPath());

				// get manufacturer name
				CompanyDetail dbCompanyDetail = companyDetailDAO
						.getById(machineDocumentList.get(i).getManufacturerId());
				if (dbCompanyDetail != null) {
					String manufacturerName = dbCompanyDetail.getCompanyName();

					machineDocumentDTO.setManufacturerName(manufacturerName);
				} else {
					machineDocumentDTO.setManufacturerName("");
				}

				newMachineDocumentList.add(machineDocumentDTO);
			}
			return Response.status(Status.OK).entity(newMachineDocumentList).build();

		} catch (Exception e) {
			log.severe("Unable to find Machine Documents " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Machine Documents")).build();
		}
	}

	@GET
	@Path("/manufacturer/{companyID}")
	@UnitOfWork
	public Response listMachineDocumentsForManufacturer(@Auth User authUser, @PathParam("companyID") long companyID) {
		try {
			log.info(" In listMachineDocumentsForManufacturer");
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(machineDocumentDAO.findByManufacturer(companyID))).build();
		} catch (Exception e) {
			log.severe("Unable to find Machine Documents " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Machine Documents")).build();
		}
	}

	/**
	 * This method is used to fetch list of all documents by manufacturer id &
	 * model number
	 * 
	 * @param authUser
	 * @param manufacturerId
	 * @param modelNumber
	 * @return list of {@link MachineDocument}
	 */
	@GET
	@Path("/{manufacturerId}/{modelNumber}")
	@UnitOfWork
	public Response listMachineDocumentsByManufacturerIdAndModelNumber(@Auth User authUser,
			@PathParam("manufacturerId") long manufacturerId, @PathParam("modelNumber") String modelNumber) {
		try {
			log.info(" In listMachineDocumentsByManufacturerIdAndModelNumber");
			//modelNumber input should be encoded into apk build before call to this api 
			String decodedModelNumber=URLDecoder.decode(modelNumber);

			/*List<MachineDocument> machineDocumentList = machineDocumentDAO.findAllByManIdAndModelNumber(manufacturerId,
					modelNumber);*/			
			List<MachineDocument> machineDocumentList = machineDocumentDAO.findAllByManIdAndModelNumber(manufacturerId, decodedModelNumber);
			

			List<MachineDocument> newMachineDocumentList = new ArrayList<>();

			for (int i = 0; i < machineDocumentList.size(); i++) {

				MachineDocument machineDocument = new MachineDocument();
				machineDocument.setId(machineDocumentList.get(i).getId());
				machineDocument.setManufacturerId(machineDocumentList.get(i).getManufacturerId());
				machineDocument.setModelNumber(machineDocumentList.get(i).getModelNumber());
				machineDocument.setDescription(machineDocumentList.get(i).getDescription());

				// create url for mobile app using document_name
				String domainUrl = ApplicationContext.getInstance().getConfig().getDomainurl();
				String documentPathUrl = domainUrl + "/api/machinedoc/fetch/"
						+ machineDocumentList.get(i).getManufacturerId() + "/"
						+ machineDocumentList.get(i).getDocumentPath();

				machineDocument.setDocumentPath(documentPathUrl);

				newMachineDocumentList.add(machineDocument);
			}
			return Response.status(Status.OK).entity(newMachineDocumentList).build();

		} catch (Exception e) {
			log.severe("Unable to find Machine Documents " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Machine Documents")).build();
		}
	}
   // just testing
}