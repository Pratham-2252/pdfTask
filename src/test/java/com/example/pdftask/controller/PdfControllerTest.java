package com.example.pdftask.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pdftask.dto.PdfInfo;
import com.example.pdftask.dto.PdfItem;
import com.example.pdftask.service.IPdf;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PdfController.class)
public class PdfControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IPdf iPdf;

	@Test
	public void testGeneratePdf() throws Exception {
		PdfInfo pdfInfo = new PdfInfo();

		pdfInfo.setSeller("XYZ Pvt. Ltd.");
		pdfInfo.setSellerGstin("29AABBCCDD121ZD");
		pdfInfo.setSellerAddress("New Delhi, India");
		pdfInfo.setBuyer("Vedant Computers");
		pdfInfo.setBuyerGstin("29AABBCCDD131ZD");
		pdfInfo.setBuyerAddress("New Delhi, India");

		PdfItem item = new PdfItem();

		item.setName("Product 1");
		item.setQuantity("12 Nos");
		item.setRate(123.00);
		item.setAmount(1476.00);

		pdfInfo.setItems(Collections.singletonList(item));

		when(iPdf.generatePdf(any(PdfInfo.class))).thenReturn(new ByteArrayResource("Dummy PDF Content".getBytes()));

		mockMvc.perform(post("/api/v1/generate-pdf").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(pdfInfo))).andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=transaction.pdf"));
	}
}
