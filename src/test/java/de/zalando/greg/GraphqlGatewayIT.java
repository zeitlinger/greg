package de.zalando.greg;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class GraphqlGatewayIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void gateway() {
        ResponseEntity<String> post = restTemplate.postForEntity(
                "/greg",
                "{ \"query\": \"{ " +
//                        "root { " +
                        "bar " +
                        "_baz { bla } " +
//                        "}" +
                        " }\" }"
                        , String.class);
        Assertions.assertThat(post.getBody())
                .isEqualTo("{\"data\":{\"bar\":\"hello world\",\"_baz\":{\"bla\":\"hello world\"}}}");
    }
}