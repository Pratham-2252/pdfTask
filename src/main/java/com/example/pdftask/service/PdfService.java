package com.example.pdftask.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.pdftask.dto.PdfInfo;
import com.example.pdftask.dto.PdfItem;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service("pdfService")
public class PdfService implements IPdf {

	@Value("${pdf.savePath}")
	private String pdfPath;

	@Override
	public Resource generatePdf(PdfInfo pdfInfo) {

		String fileHash = generateFileHash(pdfInfo);

		String filePath = pdfPath + fileHash + ".pdf";

		File file = new File(filePath);

		if (file.exists()) {

			return readPdfFromFile(file);
		} else {

			Resource resource = generatePdfFile(pdfInfo);

			savePdfToFile(file, resource);

			return resource;
		}
	}

	private Resource readPdfFromFile(File file) {

		try {
			return new ByteArrayResource(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	private void savePdfToFile(File file, Resource pdfResource) {

		try {

			file.getParentFile().mkdirs();

			try (FileOutputStream fos = new FileOutputStream(file)) {

				fos.write(pdfResource.getInputStream().readAllBytes());

				fos.flush();
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private Resource generatePdfFile(PdfInfo pdfInfo) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {

			PdfWriter writer = new PdfWriter(outputStream);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc);

			Table infoTable = new Table(2);

			infoTable.setWidth(UnitValue.createPercentValue(100));

			String sellerInfo = "Seller:\n" + pdfInfo.getSeller() + "\n" + pdfInfo.getSellerAddress() + "\nGSTIN: "
					+ pdfInfo.getSellerGstin();

			infoTable.addCell(new Paragraph(sellerInfo).setPaddingLeft(50).setPaddingRight(50));

			String buyerInfo = "Buyer:\n" + pdfInfo.getBuyer() + "\n" + pdfInfo.getBuyerAddress() + "\nGSTIN: "
					+ pdfInfo.getBuyerGstin();

			infoTable.addCell(new Paragraph(buyerInfo).setPaddingLeft(50).setPaddingRight(50));

			document.add(infoTable);

			Table itemTable = new Table(new float[] { 4, 2, 2, 2 });

			itemTable.setWidth(UnitValue.createPercentValue(100));

			itemTable.addHeaderCell("Item");
			itemTable.addHeaderCell("Quantity");
			itemTable.addHeaderCell("Rate");
			itemTable.addHeaderCell("Amount");

			for (PdfItem pdfItem : pdfInfo.getItems()) {

				itemTable.addCell(pdfItem.getName());
				itemTable.addCell(pdfItem.getQuantity());
				itemTable.addCell(String.valueOf(pdfItem.getRate()));
				itemTable.addCell(String.valueOf(pdfItem.getAmount()));

			}

			document.add(itemTable.setTextAlignment(TextAlignment.CENTER));

			Table emptyRowTable = new Table(1);

			emptyRowTable.setWidth(UnitValue.createPercentValue(100));

			emptyRowTable.setHeight(30);
			emptyRowTable.addCell(new Cell().add(new Paragraph("")));

			document.add(emptyRowTable);

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ByteArrayResource(outputStream.toByteArray());
	}

	public String generateFileHash(PdfInfo pdfInfo) {

		try {

			StringBuilder input = new StringBuilder();

			input.append(pdfInfo.getSeller()).append(pdfInfo.getSellerGstin()).append(pdfInfo.getSellerAddress())
					.append(pdfInfo.getBuyer()).append(pdfInfo.getBuyerGstin()).append(pdfInfo.getBuyerAddress());

			List<PdfItem> items = pdfInfo.getItems();

			for (PdfItem item : items) {

				input.append(item.getName()).append(item.getQuantity()).append(item.getRate()).append(item.getAmount());
			}

			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hashBytes = digest.digest(input.toString().getBytes(StandardCharsets.UTF_8));

			StringBuilder hexString = new StringBuilder();

			for (byte b : hashBytes) {

				String hex = Integer.toHexString(0xff & b);

				if (hex.length() == 1)

					hexString.append('0');

				hexString.append(hex);
			}

			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();

			return null;
		}
	}
}
