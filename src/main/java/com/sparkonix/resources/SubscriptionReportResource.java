package com.sparkonix.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.Status;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sparkonix.dao.CompanyDetailDAO;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.SubscriptionReportDTO;
import com.sparkonix.entity.jsonb.CompanyLocationAddress;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/subscriptionreport")
@Produces(MediaType.APPLICATION_JSON)
public class SubscriptionReportResource {

	private final MachineDAO machineDAO;
	private final CompanyDetailDAO companyDetailDAO;
	private final CompanyLocationDAO companyLocationDAO;

	private final Logger log = Logger.getLogger(SubscriptionReportResource.class.getName());

	public SubscriptionReportResource(MachineDAO machineDAO, CompanyDetailDAO companyDetailDAO,
			CompanyLocationDAO companyLocationDAO) {
		this.machineDAO = machineDAO;
		this.companyDetailDAO = companyDetailDAO;
		this.companyLocationDAO = companyLocationDAO;
	}

	@GET
	@UnitOfWork
	@Path("/q")
	public Response fetchSubscriptionReportData(@Auth User authUser,
			@QueryParam("attendmeSubEndMonth") int attendmeSubEndMonth,
			@QueryParam("attendmeSubEndYear") int attendmeSubEndYear,
			@QueryParam("warrantyExpEndMonth") int warrantyExpEndMonth,
			@QueryParam("warrantyExpEndYear") int warrantyExpEndYear, 
			@QueryParam("amcSubEndMonth") int amcSubEndMonth,
			@QueryParam("amcSubEndYear") int amcSubEndYear, 
			@QueryParam("onBoardedBy") long onBoardedBy) {

		try {

			log.info("In fetchSubscriptionReportData");
			List<Machine> subscriptionList = machineDAO.getSubscriptionData(attendmeSubEndMonth, attendmeSubEndYear,
					warrantyExpEndMonth, warrantyExpEndYear, amcSubEndMonth, amcSubEndYear, onBoardedBy);

			List<SubscriptionReportDTO> subscriptionReportDTOList = new ArrayList<>();

			for (int i = 0; i < subscriptionList.size(); i++) {
				SubscriptionReportDTO subscriptionReportDTO = new SubscriptionReportDTO();

				subscriptionReportDTO.setMachineModel(subscriptionList.get(i).getModelNumber());
				subscriptionReportDTO.setMachineSerial(subscriptionList.get(i).getSerialNumber());

				CompanyDetail customerObj = companyDetailDAO.getById(subscriptionList.get(i).getCustomerId());
				subscriptionReportDTO.setCustomerCompanyName(customerObj.getCompanyName());
				subscriptionReportDTO.setCustomerCompanyPan(customerObj.getPan());

				CompanyLocation locationObj = companyLocationDAO.getById(subscriptionList.get(i).getLocationId());
				String address = locationObj.getAddress();
				locationObj.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
				subscriptionReportDTO.setCustomerLocation(locationObj.getCompanyLocationAddress().getLocationName()
						+ " " + locationObj.getCompanyLocationAddress().getLocationAddress());

				subscriptionReportDTO.setAttendMeSubStartDate(subscriptionList.get(i).getCurSubscriptionStartDate());
				subscriptionReportDTO.setAttendMeSubEndDate(subscriptionList.get(i).getCurSubscriptionEndDate());
				subscriptionReportDTO.setMachineInstallationDate(subscriptionList.get(i).getInstallationDate());
				subscriptionReportDTO.setMachineWarrantyExpiryDate(subscriptionList.get(i).getWarrantyExpiryDate());

				subscriptionReportDTO.setMachineAmcStartDate(subscriptionList.get(i).getCurAmcStartDate());
				subscriptionReportDTO.setMachineAmcEndDate(subscriptionList.get(i).getCurAmcEndDate());

				subscriptionReportDTOList.add(subscriptionReportDTO);
			}
			return Response.status(Status.OK).entity(subscriptionReportDTOList).build();

		} catch (Exception e) {
			log.severe("Failed to fetch report data. " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Failed to fetch report data"))
					.build();
		}
	}
	
	@GET
	@UnitOfWork
	@Path("/download/excel")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadSubscriptionReportAsExcel(@Auth User authUser,
			@QueryParam("attendmeSubEndMonth") int attendmeSubEndMonth,
			@QueryParam("attendmeSubEndYear") int attendmeSubEndYear,
			@QueryParam("warrantyExpEndMonth") int warrantyExpEndMonth,
			@QueryParam("warrantyExpEndYear") int warrantyExpEndYear, 
			@QueryParam("amcSubEndMonth") int amcSubEndMonth,
			@QueryParam("amcSubEndYear") int amcSubEndYear, 
			@QueryParam("onBoardedBy") long onBoardedBy) {

		try {

			log.info("In fetchSubscriptionReportData");
			List<Machine> subscriptionList = machineDAO.getSubscriptionData(attendmeSubEndMonth, attendmeSubEndYear,
					warrantyExpEndMonth, warrantyExpEndYear, amcSubEndMonth, amcSubEndYear, onBoardedBy);

			List<SubscriptionReportDTO> subscriptionReportDTOList = new ArrayList<>();
			// Blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();

			// Create a blank sheet
			XSSFSheet sheet = workbook.createSheet("Complaint List");

			// This data needs to be written (Object[])
			Map<String, Object[]> data = new TreeMap<String, Object[]>();
			data.put("1",
					new Object[] { "CompanyName","CompanyPAN", "MachineModel", "MachineSerial", "CompanyLocation", "AttendMeSubscriptionStartDate",
							"AttendMeSubscriptionEndDate", "InstallationDate", "WarrantyExpiryDate", "AmcSubscriptionStartDate",
							"AmcSubscriptionStartDate"});

			for (int i = 0; i < subscriptionList.size(); i++) {

				String attendMeSubStartDate = null;
				String attendMeSubEndDate= null;
				String installationDate = null;
				String warrantyExpiryDate = null;
				String amcSubStartDate = null;
				String amcSubEndDate = null;

				if (subscriptionList.get(i).getCurSubscriptionStartDate() != null) {
					attendMeSubStartDate = subscriptionList.get(i).getCurSubscriptionStartDate().toString();
				}
				if (subscriptionList.get(i).getCurSubscriptionEndDate() != null) {
					attendMeSubEndDate = subscriptionList.get(i).getCurSubscriptionEndDate().toString();
				}
				if (subscriptionList.get(i).getInstallationDate() != null) {
					installationDate = subscriptionList.get(i).getInstallationDate().toString();
				}
				if (subscriptionList.get(i).getWarrantyExpiryDate() != null) {
					warrantyExpiryDate = subscriptionList.get(i).getWarrantyExpiryDate().toString();
				}
				if (subscriptionList.get(i).getCurAmcStartDate() != null) {
					amcSubStartDate = subscriptionList.get(i).getCurAmcStartDate().toString();
				}
				if (subscriptionList.get(i).getCurAmcEndDate() != null) {
					amcSubEndDate = subscriptionList.get(i).getCurAmcEndDate().toString();
				}
				
				//customer
				CompanyDetail customerObj = companyDetailDAO.getById(subscriptionList.get(i).getCustomerId());
				
				//location
				CompanyLocation locationObj = companyLocationDAO.getById(subscriptionList.get(i).getLocationId());
				String address = locationObj.getAddress();
				locationObj.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));  

				data.put(Integer.toString(i + 2),
						new Object[] { 
								customerObj.getCompanyName(),
								customerObj.getPan(),
								subscriptionList.get(i).getModelNumber(),
								subscriptionList.get(i).getSerialNumber(),
								locationObj.getCompanyLocationAddress().getLocationName()+ " " + locationObj.getCompanyLocationAddress().getLocationAddress(),
								attendMeSubStartDate,
								attendMeSubEndDate,
								installationDate,
								warrantyExpiryDate,
								amcSubStartDate,
								amcSubEndDate  
							});
			  
			}//end of for loop
			
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
					.header("Content-Disposition", "attachment; filename=\"SubscriptionReport.xlsx" + "\"").build();
			 

		} catch (Exception e) {
			log.severe("Failed to download SubscriptionReport as excel. " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Failed to download Subscription Report"))
					.build();
		}
	}

}
