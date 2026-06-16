package br.com.erudio.integrationtests.testcontainers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;
import java.util.stream.Stream;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
@SpringBootTest(
        properties = {
                "spring.mvc.pathmatch.matching-strategy=ant_path_matcher",
        }
)
public class AbstractIntegrationTest {
     @org.springframework.test.context.bean.override.mockito.MockitoBean
     private br.com.erudio.mail.EmailSender emailSender;
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        // 1. INJETA AS CONFIGURAÇÕES DO DOCKER LINUX ANTES DE QUALQUER COISA
        static {
            System.setProperty("testcontainers.docker.client.checks.version", "false");
            System.setProperty("DOCKER_HOST", "unix:///var/run/docker.sock");
        }

        static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:9.1.0");

        private static void startContainers() {
            Startables.deepStart(Stream.of(mysql)).join();
        }

        private static Map<String, String> createConnectionConfiguration() {
            return Map.of(
                    "spring.datasource.url", mysql.getJdbcUrl(),
                    "spring.datasource.username", mysql.getUsername(),
                    "spring.datasource.password", mysql.getPassword()
            );
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            startContainers();

            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            MapPropertySource testcontainers = new MapPropertySource("testcontainers",
                    (Map) createConnectionConfiguration()
            );
            environment.getPropertySources().addFirst(testcontainers);
        }
    }
}
