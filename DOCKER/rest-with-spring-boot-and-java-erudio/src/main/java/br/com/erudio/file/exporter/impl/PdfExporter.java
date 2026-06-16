package br.com.erudio.file.exporter.impl;

import br.com.erudio.data.dto.v1.PersonDTOV1;
import br.com.erudio.file.exporter.contract.PersonExporter;
import br.com.erudio.model.Book;
import br.com.erudio.services.QRCodeService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Component
public class PdfExporter implements PersonExporter {

    @Autowired
    private QRCodeService service;

    @Override
    public Resource exportPeople(List<PersonDTOV1> people) throws Exception {
        String templateFileName = "/templates/people.jrxml";
        InputStream inputStream = getClass().getResourceAsStream(templateFileName);
        String errorMessage = "Template file not found: \"/templates/people.jrxml\"";
        if(inputStream == null) throw new RuntimeException(errorMessage);

        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(people);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", "People Report");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    @Override
    public Resource exportPerson(PersonDTOV1 person) throws Exception {

        String mainTemplateFileName = "/templates/person.jrxml";
        InputStream mainTemplateStream = getClass().getResourceAsStream(mainTemplateFileName);
        String templateErrorMessage = "Template file not found: \"/templates/person.jrxml\"";
        if(mainTemplateStream == null) throw new RuntimeException(templateErrorMessage);

        String subReportFileName = "/templates/books.jrxml";
        InputStream subReportStream = getClass().getResourceAsStream(subReportFileName);
        String subReportErrorMessage = "Template file not found: \"/templates/books.jrxml\"";
        if(subReportStream == null) throw new RuntimeException(subReportErrorMessage);

        JasperReport subReport = JasperCompileManager.compileReport(subReportStream);
        JasperReport mainReport = JasperCompileManager.compileReport(mainTemplateStream);

        int qrCodeWidth = 200;
        int qrCodeHeight = 200;

        InputStream qrCodeStream = service.generateQRCode(person.getProfileUrl(), qrCodeWidth, qrCodeHeight);

        List<Book> books = person.getBooks();

        JRBeanCollectionDataSource subReportDataSource = new JRBeanCollectionDataSource(person.getBooks());
        JRBeanCollectionDataSource mainDataSource = new JRBeanCollectionDataSource(Collections.singletonList(person));

        String path = Objects.requireNonNull(getClass().getResource("/templates/books.jasper")).getPath();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("SUB_REPORT_DATA_SOURCE", subReportDataSource);
        parameters.put("SUB_REPORT_DIR", path);
        parameters.put("BOOK_SUB_REPORT", subReport);
        parameters.put("QR_CODE_IMAGE", qrCodeStream);

        JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, parameters, mainDataSource);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

}
