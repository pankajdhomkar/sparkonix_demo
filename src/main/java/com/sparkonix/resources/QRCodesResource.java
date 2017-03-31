package com.sparkonix.resources;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sparkonix.ApplicationContext;
import com.sparkonix.dao.QRCodeDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.QRCode;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.UniqueCode;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/qrcodes")
@Produces(MediaType.APPLICATION_JSON)
public class QRCodesResource {

	private final QRCodeDAO qrCodeDAO;
	private final UserDAO userDAO;

	private final Logger log = Logger.getLogger(QRCodesResource.class.getName());

	public QRCodesResource(QRCodeDAO qrcodedao, UserDAO userDAO) {
		this.qrCodeDAO = qrcodedao;
		this.userDAO = userDAO;
	}

	@GET
	@UnitOfWork
	public Response listQRCodes(@Auth User authUser) {
		try {
			log.info(" In listQRCodes");
			return Response.status(Status.OK).entity(JsonUtils.getJson(qrCodeDAO.findAll())).build();
		} catch (Exception e) {
			log.severe("Unable to find QR Codes " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find QR Codes"))
					.build();
		}
	}

	@POST
	@Path("/generate/{numCodes}")
	@UnitOfWork
	public Response createQRCodes(@Auth User authUser, @PathParam("numCodes") int numCodes) {
		/// create batch name with today's date and time
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy-HHmmss");
		Date date = new Date();
		String batchName = dateFormat.format(date);
		// create directory with batchName - all images will be under the
		// batchName directory
		String qrCodeImagesDirectory = ApplicationContext.getInstance().getConfig().getQrCodeImagesDirectory();
		String filePath = qrCodeImagesDirectory + "\\" + batchName;
		File files = new File(filePath);
		files.setExecutable(true, false);
		files.setReadable(true, false);
		files.setWritable(true, false);
		if (!files.exists()) {
			if (!files.mkdirs()) {
				log.severe("Unable to create required directory for QR codes ");
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to create required directory for QR codes")).build();
			}
		}
		// for given number, generate unique code and create the image files
		for (int i = 0; i < numCodes; i++) {
			String qrCodeText;

			// generate new qrCodeText & check if its already generated/exist in
			// db & if not then write it to database
			while ((qrCodeText = saveUniqueQrCodeInDatabase(batchName, authUser.getId())) != null) {
				log.info("QR Code has written into database.");
				break;
			}

			String fileName = filePath + "\\QRtemp.png";
			int width = 410, height = 410;
			String fileType = "png";
			File qrFile = new File(fileName);
			try {
				// create the QR image file
				if (!createQRImage(qrFile, qrCodeText, width, height, fileType)) {
					log.severe("Could not find a writer for the QR code image");
					return Response.status(Status.BAD_REQUEST)
							.entity(JsonUtils.getErrorJson("Could not find a writer for the QR code image")).build();
				}

				// write qr code text on image
				BufferedImage backgroundImage = ImageIO.read(qrFile);
				Graphics g = backgroundImage.getGraphics();
				g.setColor(Color.BLACK);
				g.setFont(new Font("verdana", Font.PLAIN, 43));
				
				//vertically center text
				int textWidth = g.getFontMetrics().stringWidth(qrCodeText);
				int xValue=(width-textWidth)/2;
				
				g.drawString(qrCodeText, xValue, 389);

				// Save as new image
				ImageIO.write(backgroundImage, "PNG", new File(filePath, qrCodeText + ".png"));

				// overlay this image on the background
				// load source images
				/*
				 * ClassLoader classLoader = getClass().getClassLoader(); File
				 * bgFile = new File(classLoader.getResource(
				 * "assets/images/StickerBackgroundDesign.png").getFile());
				 * BufferedImage backgroundImage = ImageIO.read(bgFile);
				 * BufferedImage overlay = ImageIO.read(qrFile);
				 * 
				 * // create the new image canvas BufferedImage combined = new
				 * BufferedImage(backgroundImage.getWidth(),
				 * backgroundImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				 * 
				 * // paint both images, preserving the alpha channels Graphics
				 * g = combined.getGraphics(); g.drawImage(backgroundImage, 0,
				 * 0, null); g.drawImage(overlay, 120, 368, null);
				 * g.setColor(Color.BLACK); g.setFont(new Font("Courier New",
				 * Font.BOLD, 25)); g.drawString(qrCodeText, 200, 370 + height -
				 * 10);
				 * 
				 * // Save as new image ImageIO.write(combined, "PNG", new
				 * File(filePath, qrCodeText + ".png"));
				 */

			} catch (Exception e) {
				log.severe("Unable to generate QR Codes " + e);
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to generate QR Codes"))
						.build();
			}
		}

		// delete temp qr file
		File tempFile = new File(filePath + "\\QRtemp.png");
		if (tempFile.delete()) {
			log.info("A file QRtemp.png deleted");
		} else {
			log.severe("A file QRtemp.png not deleted");
		}
		// create zip for above batch
		File folder = new File(filePath);
		String zippedFileName = filePath + ".zip";
		File[] listOfFiles = folder.listFiles();
		File outputZip = createZip(listOfFiles, zippedFileName);
		boolean doesExist = new File(zippedFileName).exists();
		if (doesExist) {
			log.info("zip exist");
			// delete batch folder
			File[] folderFiles = folder.listFiles();
			if (null != folderFiles) {
				for (int i = 0; i < folderFiles.length; i++) {
					folderFiles[i].delete();
				}
			}
			folder.delete();
			log.info("Batch folder deleted");
		} else {
			log.info("zip does not exist");
		}
		return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("QR codes generated")).build();
	}

	/**
	 * This method returns a newly generated unique QR code having length as 16
	 * char.
	 * 
	 * @param batchName
	 *            its folder name for respetive QR code batch.
	 * @param userId
	 *            its user who has generated QR code
	 * @return a QR code text
	 */
	private String saveUniqueQrCodeInDatabase(String batchName, long userId) {
		// create 16 char unique code & write it in database
		String qrCodeText = UniqueCode.generateUniqueCode();
		String finalQrCodeText = null;
		try {

			User dbUser = userDAO.getById(userId);
			if (dbUser == null) {
				log.warning("User does not exist.");
				return null;
			}
			QRCode qrcode = new QRCode();
			qrcode.setBatchName(batchName);
			qrcode.setQrCode(qrCodeText);
			qrcode.setStatus(QRCode.QRCODE_STATUS.UNASSIGNED.toString());
			qrcode.setCreatedBy(dbUser);
			QRCode dbQrCode = qrCodeDAO.save(qrcode);
			if (dbQrCode != null) {
				finalQrCodeText = dbQrCode.getQrCode();
			} else {
				finalQrCodeText = null;
			}
		} catch (Exception e1) {
			log.severe("Unable to write QR Code into database" + e1);
		}
		return finalQrCodeText;
	}

	public boolean createQRImage(File qrFile, String qrCodeText, int width, int height, String fileType)
			throws IOException, WriterException {
		// Create the ByteMatrix for the QR-Code that encodes the given String
		Hashtable hintMap = new Hashtable();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, width, height, hintMap);
		// Make the BufferedImage that are to hold the QRCode
		int matrixWidth = byteMatrix.getWidth();
		int matrixHeight = byteMatrix.getHeight();
		BufferedImage image = new BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixHeight);
		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixHeight; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		return ImageIO.write(image, fileType, qrFile);
	}

	public File createZip(File[] files, String filePath) {
		File zipfile = new File(filePath);
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];
		try {
			// create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
			// compress the files
			for (int i = 0; i < files.length; i++) {
				// FileInputStream in = new
				// FileInputStream(files.get(i).getCanonicalName());
				FileInputStream in = new FileInputStream(files[i].getCanonicalFile());
				// add ZIP entry to output stream
				out.putNextEntry(new ZipEntry(files[i].getName()));
				// transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				// complete the entry
				out.closeEntry();
				in.close();
			}
			// complete the ZIP file
			out.close();
			return zipfile;
		} catch (IOException ex) {
			log.severe(ex.getMessage());
		}
		return null;
	}

	@GET
	@Path("/download/zip/{batchName}")
	@Produces("application/zip")
	// @Produces("application/octet-stream")
	public Response downloadQRBatchZip(@PathParam("batchName") String batchName) throws IOException {

		String qrCodeImagesDirectory = ApplicationContext.getInstance().getConfig().getQrCodeImagesDirectory();
		String filePath = qrCodeImagesDirectory + "\\" + batchName + ".zip";

		File file = new File(filePath);
		if (file.exists()) {

			ResponseBuilder responseBuilder = Response.ok((Object) file);
			responseBuilder.header("Content-Disposition", "attachment; filename=\"" + batchName + ".zip\"");
			return responseBuilder.build();
		} else {
			log.severe("Failed to download QR codes batch.");
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Failed to download QR codes batch")).build();
		}

	}

}
