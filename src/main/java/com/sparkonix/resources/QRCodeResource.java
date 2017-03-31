package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.QRCodeDAO;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.MachineQRcodeDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/qrcode")
@Produces(MediaType.APPLICATION_JSON)
public class QRCodeResource {
	private final Logger log = Logger.getLogger(QRCodeResource.class.getName());

	private QRCodeDAO qrCodeDAO;
	private MachineDAO machineDAO;

	public QRCodeResource(QRCodeDAO qrCodeDAO, MachineDAO machineDAO) {
		this.qrCodeDAO = qrCodeDAO;
		this.machineDAO = machineDAO;
	}

	@GET
	@UnitOfWork
	@Path("/{qrcode}")
	public Response getByQRCode(@Auth User authUser, @PathParam("qrcode") String qrcode) {
		try {
			log.info(" In getByQRCode");
			return Response.status(Status.OK).entity(JsonUtils.getJson(qrCodeDAO.getByQRCode(qrcode))).build();
		} catch (Exception e) {
			log.severe("Unable to QR Code " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find QR Code")).build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/check/{qrcode}")
	public Response checkAssignedQrCodeToMachine(@Auth User authUser, @PathParam("qrcode") String qrcode) {
		try {
			log.info(" In checkAssignedQrCodeToMachine");
			Machine machine = machineDAO.findMachineByQrCode(qrcode);
			MachineQRcodeDTO machineQRcodeDTO = new MachineQRcodeDTO();

			if (machine == null) {
				machineQRcodeDTO.setQRcodeAssigned(false);
			} else {
				machineQRcodeDTO.setQRcodeAssigned(true);
			}
			return Response.status(Status.OK).entity(JsonUtils.getJson(machineQRcodeDTO)).build();

		} catch (Exception e) {
			log.severe("Unable to find QR code." + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find QR code")).build();
		}
	}
}
