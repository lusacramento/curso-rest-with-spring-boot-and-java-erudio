package br.com.erudio.file.exporter.impl;

import br.com.erudio.data.dto.v1.PersonDTOV1;
import br.com.erudio.file.exporter.contract.PersonExporter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class XlsxExporter implements PersonExporter {
    @Override
    public Resource exportPeople(List<PersonDTOV1> people) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("People");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"id", "firstName", "lastName", "address", "gender", "enabled"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }

            int rowIndex = 1;
            for (PersonDTOV1 person : people) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(person.getId());
                row.createCell(1).setCellValue(person.getFirstName());
                row.createCell(2).setCellValue(person.getLastName());
                row.createCell(3).setCellValue(person.getAddress());
                row.createCell(4).setCellValue(person.getGender());
                row.createCell(5).setCellValue(Boolean.toString(person.getEnabled() != null && person.getEnabled()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource exportPerson(PersonDTOV1 person) throws Exception {
        return null;
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

}
