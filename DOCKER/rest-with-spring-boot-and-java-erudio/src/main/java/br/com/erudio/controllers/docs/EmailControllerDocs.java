package br.com.erudio.controllers.docs;

import br.com.erudio.data.dto.request.EmailRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface EmailControllerDocs {

    @Operation(summary = "Send an email",
            description = "Sends an email by providing details, subject and body",
            tags = "Email",
            responses = {
                    @ApiResponse(description = "Success"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Map<String, String>> sendEmail(EmailRequestDTO emailRequestDTO);

    @Operation(summary = "Send an email with attachment",
            description = "Sends an email with attachment by providing details, subject and body",
            tags = "Email",
            responses = {
                    @ApiResponse(description = "Success"),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Map<String, String>> sendEmailWithAttachment(String emailRequestJson, MultipartFile attachment);
}

