package br.com.erudio.services;

import br.com.erudio.config.FileStorageConfig;
import br.com.erudio.exception.handler.FileNotFoundException;
import br.com.erudio.exception.handler.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {

        Path path = Paths.get(
                        fileStorageConfig
                                .getUploadDir()
                )
                .toAbsolutePath()
                .normalize();
        this.fileStorageLocation = path;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            String errorMessage = "Could not create the directory where files will be stored!";
            logger.error(errorMessage);
            throw new FileStorageException(errorMessage, e);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            logger.info("Creating Directories...");
            if (fileName.contains("..")) {
                String errorMessage = "Sorry! File name contains a invalid path sequence ";
                logger.error(errorMessage);
                throw new FileStorageException(errorMessage + fileName);
            }

            logger.info("Saving File on Disk...");

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            String errorMessage = "Could not store file " + fileName + ". Please try Again!";
            logger.error(errorMessage);
            throw new FileStorageException(errorMessage, e);
        }
    }

    public Resource loadFileAsResource(String fileName) throws MalformedURLException {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                String errorMessage = "The '" + fileName + "' file not found.";
                throw new FileNotFoundException(errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "The '" + fileName + "' file not found.";
            logger.error(errorMessage);
            throw new FileNotFoundException(errorMessage, e);
        }
    }
}
