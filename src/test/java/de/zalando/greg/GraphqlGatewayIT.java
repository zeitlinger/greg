package de.zalando.greg;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import wiremock.org.apache.commons.lang3.StringEscapeUtils;

import static java.net.HttpURLConnection.HTTP_OK;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@AutoConfigureWireMock(port = 0)
public class GraphqlGatewayIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("http://localhost:${wiremock.server.port}")
    private String targetHost;

    @Before
    public void setUp() {
        stubFor(
                get(urlEqualTo("/articles/1"))
                        .willReturn(aResponse()
                                            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                            .withStatus(HTTP_OK)
                                            .withBody("{ " +
                                                              "\"id\": \"1\", " +
                                                              "\"user\": { " +
                                                              "\"name\": \"Alex\", " +
                                                              "\"address\": \"" + targetHost + "/addresses/1\" " +
                                                              "} " +
                                                              "}")));

        stubFor(
                get(urlEqualTo("/addresses/1"))
                        .willReturn(aResponse()
                                            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                            .withStatus(HTTP_OK)
                                            .withBody("{ " +
                                                              "\"street\": \"Downing St.\" " +
                                                              "}")));
    }

    @Test
    public void singleField() {
        assertRequest(getQuery("result(uri: \"" + targetHost + "/articles/1\") { id } "),
                      "{\"data\":{\"result\":{\"id\":\"1\"}}}");
    }

    @Test
    public void nestedField() {
        assertRequest(getQuery("result(uri: \"" + targetHost + "/articles/1\") { _user { name } } "),
                      "{\"data\":{\"result\":{\"_user\":{\"name\":\"Alex\"}}}}");
    }

    @Test
    public void fetchRelation() {
        assertRequest(getQuery("result(uri: \"" + targetHost + "/articles/1\") { _user { _address { " +
                                       "__embed { body { street } resultCode } " +
                                       "} } } "),
                      "{\"data\":{\"result\":{\"_user\":{\"_address\":{\"__embed\":{\"body\":{\"street\":\"Downing St.\"},\"resultCode\":200}}}}}}");
    }

    @Test
    public void wholeObject() {
        assertRequest(getQuery("result(uri: \"" + targetHost + "/articles/1\") { __all } "),
                      "{\"data\":{\"result\":{\"__all\":\"{\\\"id\\\":\\\"1\\\"," +
                              "\\\"user\\\":{\\\"name\\\":\\\"Alex\\\"," +
                              "\\\"address\\\":\\\"" + targetHost + "/addresses/1\\\"}}\"}}}");
    }

    private String getQuery(String input) {
        return "{ \"query\": \"{ " + StringEscapeUtils.escapeJson(input) + "}\" }";
    }

    private void assertRequest(String request, String expected) {
        Assertions.assertThat(restTemplate.postForEntity("/greg", request, String.class).getBody()).isEqualTo(expected);
    }
}