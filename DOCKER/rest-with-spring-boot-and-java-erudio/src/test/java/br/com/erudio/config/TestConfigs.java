package br.com.erudio.config;

public interface TestConfigs {
    int SERVER_PORT = 8888;
    String HEADER_PARAM_AUTHORIZATION = "Authorization";
    String HEADER_PARAM_ORIGIN = "Origin";

    String ORIGIN_LOCAL_BACKEND = "http://localhost:8080";
    String ORIGIN_LOCAL_FRONTEND = "http://localhost:3000";
    String ORIGIN_NOT_ALLOWED = "http://semeru.com.br";
}
