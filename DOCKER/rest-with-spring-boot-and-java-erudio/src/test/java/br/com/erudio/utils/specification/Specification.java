package br.com.erudio.utils.specification;

import br.com.erudio.config.TestConfigs;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

public class Specification {
    public static RequestSpecBuilder getSpecification(String basePath, String origin, String token){
        return new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, origin)
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, getAuthorization(token))
                .setBasePath(basePath)
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL));
    }

    public static RequestSpecBuilder getSpecificationWithYaml(String basePath, String origin, String token) {
        return new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, origin)
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, getAuthorization(token))
                .setBasePath(basePath)
                .setPort(TestConfigs.SERVER_PORT)
                .setConfig(
                        RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig()
                                                .encodeContentTypeAs(
                                                        MediaType.APPLICATION_YAML_VALUE,
                                                        ContentType.TEXT
                                                )
                                )
                )
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL));
    }

    private static String getAuthorization(String token){
        return  "Bearer " + token;
    }
}
