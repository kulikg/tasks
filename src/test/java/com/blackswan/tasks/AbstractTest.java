package com.blackswan.tasks;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.slf4j.LoggerFactory.getLogger;
import static org.testcontainers.containers.Network.newNetwork;
import static org.testcontainers.utility.DockerImageName.parse;

import com.blackswan.tasks.api.IdResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.net.URIBuilder;
import java.net.URI;
import java.util.Map;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@TestInstance(PER_CLASS)
public class AbstractTest {

    Network network = newNetwork();
    MySQLContainer<?> mysql = new MySQLContainer<>(parse("mysql:8.0"))
            .withLogConsumer(new Slf4jLogConsumer(getLogger("mysql")))
            .withUsername("mysql_user")
            .withPassword("mysql_password")
            .withDatabaseName("TASKS")
            .withNetworkAliases("mysql_host")
            .withNetwork(network);
    GenericContainer<?> tasks = new GenericContainer<>(parse("blackswan/tasks:latest"))
            .withLogConsumer(new Slf4jLogConsumer(getLogger("tasks")))
            .dependsOn(mysql)
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/actuator/health"))
            .withNetwork(network);
    TestRestTemplate restTemplate = new TestRestTemplate();

    @BeforeAll
    public void beforeAll() {
        tasks.withEnv(appEnv()).start();
    }

    @AfterAll
    public void afterAll() {
        tasks.stop();
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        val resourceStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("clear_db.sql");
        val schema = IOUtils.toString(resourceStream, UTF_8);
        val connection = mysql.createConnection("?allowMultiQueries=true");
        connection.createStatement().execute(schema);
        connection.close();
    }

    protected Map<String, String> appEnv() {
        return Map.of(
                "spring.datasource.url", "jdbc:mysql://mysql_host:3306/TASKS",
                "spring.datasource.username", "mysql_user",
                "spring.datasource.password", "mysql_password"
        );
    }

    protected <E> ResponseEntity<IdResponse> post(String path, E entity) {
        return restTemplate.postForEntity(uri(path), entity, IdResponse.class);
    }

    protected void delete(String path) {
        restTemplate.delete(uri(path));
    }

    protected <T> ResponseEntity<T> get(String path, Class<T> type) {
        return restTemplate.getForEntity(uri(path), type);
    }

    protected <E> ResponseEntity<String> put(String path, E entity) {
        val headers = new HttpHeaders();
        val httpEntity = new HttpEntity<>(entity, headers);
        return restTemplate.exchange(uri(path), HttpMethod.PUT, httpEntity, String.class);
    }

    protected String pathForUser(Long userId) {
        return "api/user/" + userId + "/task";
    }

    private URI uri(String path) {
        try {
            return new URIBuilder()
                    .setScheme("http")
                    .setHost(tasks.getHost())
                    .setPort(tasks.getMappedPort(8080))
                    .setPath(path)
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
