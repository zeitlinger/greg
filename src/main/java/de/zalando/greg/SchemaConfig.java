package de.zalando.greg;

import graphql.Scalars;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchemaConfig {

    @Autowired
    private JsonFetcher jsonFetcher;

    @Bean
    public GraphQLSchema schema() {
        GraphQLSchema.Builder builder = GraphQLSchema.newSchema();

        ObjectType objectType = new ObjectType(jsonFetcher);

        builder.query(
                GraphQLObjectType.newObject()
                        .name("root")
                        .field(GraphQLFieldDefinition.newFieldDefinition()
                                       .name("result")
                                       .type(objectType)
                                       .dataFetcher(environment -> jsonFetcher.fetchUri(environment.getArgument("uri")))
                                       .argument(GraphQLArgument.newArgument()
                                                         .name("uri")
                                                         .type(Scalars.GraphQLString)
                                                         .build())
                                       .build())
                        .build());

        return builder.build();
    }

}
