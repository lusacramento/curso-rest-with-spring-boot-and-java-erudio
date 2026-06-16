package br.com.erudio.file.importer.factory;

import br.com.erudio.exception.handler.BadRequestException;
import br.com.erudio.file.importer.contract.FileImporter;
import br.com.erudio.file.importer.impl.CsvImporter;
import br.com.erudio.file.importer.impl.XlsxImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileImporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileImporter.class);

    @Autowired
    private ApplicationContext context;
    public FileImporter getImporter(String fileName) throws Exception {

        logger.info("Importing file...");

        if(fileName.endsWith(".xlsx")) return context.getBean(XlsxImporter.class);
        if(fileName.endsWith(".csv")) return context.getBean(CsvImporter.class);;

        throw new BadRequestException("Invalid file format");


    }

}
