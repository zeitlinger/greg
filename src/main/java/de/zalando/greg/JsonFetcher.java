package de.zalando.greg;

import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JsonFetcher {

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ObjectMapper objectMapper;
    
    public JsonNode fetchUri(String uri) {
        return getJsonNode(fetchEntity(uri));
    }

    public ResponseEntity<String> fetchEntity(String uri) {
        return restTemplate.getForEntity(uri, String.class);
    }

    public JsonNode getJsonNode(ResponseEntity<String> entity) {
        String body = entity.getBody();
        try {
            return objectMapper.readTree(body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
