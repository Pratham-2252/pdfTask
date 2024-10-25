package com.example.pdftask.service;

import org.springframework.core.io.Resource;

import com.example.pdftask.dto.PdfInfo;

public interface IPdf {

	public Resource generatePdf(PdfInfo pdfInfo);
}
