package br.com.erudio.services;

import br.com.erudio.config.EmailConfig;
import br.com.erudio.data.dto.request.EmailRequestDTO;
import br.com.erudio.mail.EmailSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.AddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
public class EmailService {
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private EmailConfig emailConfig;

    public void sendSimpleEmail(EmailRequestDTO emailRequest){
        try {
            emailSender
                    .to(emailRequest.getTo())
                    .withSubject(emailRequest.getSubject())
                    .withMessage(emailRequest.getBody())
                    .send(emailConfig);
        } catch (AddressException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmailWithAttachment(String emailRequestJson, MultipartFile attachment) {
        File tempFile = null;
        try {
            EmailRequestDTO emailRequest = new ObjectMapper().readValue(emailRequestJson, EmailRequestDTO.class);
            tempFile = File.createTempFile(Objects.requireNonNull(attachment.getOriginalFilename()),"");
            attachment.transferTo(tempFile);

            emailSender
                    .to(emailRequest.getTo())
                    .withSubject(emailRequest.getSubject())
                    .withMessage(emailRequest.getBody())
                    .attach(tempFile.getAbsolutePath())
                    .send(emailConfig);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing email request", e);
        } catch (IOException e) {
            throw new RuntimeException("Error processing the attachment", e);
        } catch (AddressException e) {
            throw new RuntimeException(e);
        } finally {
            if(tempFile != null && tempFile.exists()) tempFile.delete();
        }

    }
}
