package br.com.erudio.controllers;

import br.com.erudio.controllers.docs.EmailControllerDocs;
import br.com.erudio.data.dto.request.EmailRequestDTO;
import br.com.erudio.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController implements EmailControllerDocs {

    @Autowired
    private EmailService service;

    @PostMapping
    @Override
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody EmailRequestDTO emailRequest) {
        service.sendSimpleEmail(emailRequest);
        var successMessage = Map.of("message", "Email sent successfully");
        return ResponseEntity.ok(successMessage);
    }


    @PostMapping(
            value = "/withAttachment",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Override
    public ResponseEntity<Map<String, String>> sendEmailWithAttachment(
            @RequestParam("emailRequest") String emailRequestJson,
            @RequestParam("attachment") MultipartFile attachment
    ) {
        service.sendEmailWithAttachment(emailRequestJson, attachment);
        var successMessage = Map.of("message", "Email with attachment sent successfully");
        return ResponseEntity.ok(successMessage);
    }
}
