package br.com.erudio.utils.auth;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationtests.dto.security.AccountCredentialsDTO;
import br.com.erudio.integrationtests.dto.security.TokenDTO;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;

public class Authenticator {
    public static TokenDTO signin() {
        String username = "leandro";
        String password = "admin123";
        AccountCredentialsDTO credentials = new AccountCredentialsDTO(username, password);
        return given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(credentials)
                .when()
                .post()
                .then()
                .	statusCode(200)
                .extract()
                .body()
                .as(TokenDTO.class);
    }
}
