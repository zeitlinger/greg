package de.zalando.greg;

import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import org.springframework.http.ResponseEntity;

public class ObjectType extends GraphQLObjectType {

    private final JsonFetcher jsonFetcher;

    public ObjectType(JsonFetcher jsonFetcher) {
        super("object", null, new ArrayList<>(), new ArrayList<>());
        this.jsonFetcher = jsonFetcher;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition(String name) {
        if ("__embed".equals(name)) {
            return GraphQLFieldDefinition.newFieldDefinition()
                    .type(getEmbed(this))
                    .dataFetcher(environment -> jsonFetcher.fetchEntity(((JsonNode) environment.getSource()).asText()))
                    .name("fetchField").build();
        }

        if ("__all".equals(name)) {
            return GraphQLFieldDefinition.newFieldDefinition()
                    .type(Scalars.GraphQLString)
                    .dataFetcher(environment -> environment.getSource().toString())
                    .name("allFields").build();
        }

        if (name.startsWith("_")) {
            //selects an object rather than a field
            //the _ in the beginning is merely to satisfy the schema parser

            return GraphQLFieldDefinition.newFieldDefinition()
                    .type(this)
                    .dataFetcher(environment -> getNode(environment, name.substring(1)))
                    .name("objectField").build();
        }

        //any field
        return GraphQLFieldDefinition.newFieldDefinition()
                .type(Scalars.GraphQLString)
                .dataFetcher(environment -> getNode(environment, name).asText())
                .name("stringField").build();
    }

    private JsonNode getNode(DataFetchingEnvironment environment, String name) {
        return ((ObjectNode) environment.getSource()).get(name);
    }

    private GraphQLObjectType getEmbed(ObjectType objectType) {
        return GraphQLObjectType.newObject()
                .name("embed")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                               .name("body")
                               .type(objectType)
                               .dataFetcher(environment -> jsonFetcher.getJsonNode(getResponse(environment)))
                               .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                               .name("resultCode")
                               .type(Scalars.GraphQLInt)
                               .dataFetcher(environment -> getResponse(environment).getStatusCodeValue())
                               .build())
                .build();
    }

    private ResponseEntity<String> getResponse(DataFetchingEnvironment environment) {
        //noinspection unchecked
        return (ResponseEntity<String>) environment.getSource();
    }

}
