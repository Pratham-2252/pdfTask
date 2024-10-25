package com.example.pdftask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdftask.dto.PdfInfo;
import com.example.pdftask.service.IPdf;

@RestController
@RequestMapping("/api/v1")
public class PdfController {

	@Autowired
	private IPdf pdfService;

	@PostMapping(value = "/generate-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<?> generatePdf(@RequestBody PdfInfo pdfInfo) {

		Resource resource = pdfService.generatePdf(pdfInfo);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "transaction.pdf");

		return ResponseEntity.ok().headers(headers).body(resource);
	}
}
