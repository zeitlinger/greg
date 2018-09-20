package de.zalando.greg;

import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchemaConfig {

    @Bean
    public GraphQLSchema schema() {
        GraphQLSchema.Builder schema = GraphQLSchema.newSchema();
        GraphQLObjectType.Builder object = GraphQLObjectType.newObject();
        object.name("foo");

        GraphQLFieldDefinition bar = GraphQLFieldDefinition.newFieldDefinition()
                .type(Scalars.GraphQLString)
                .dataFetcher(dataFetchingEnvironment -> "hello world")
                .name("bar").build();

        object.field(GraphQLFieldDefinition.newFieldDefinition()
                             .type(GraphQLObjectType.newObject()
                                           .name("bar")
                                           .field(bar)
                                           .build())
                             .dataFetcher(dataFetchingEnvironment -> new Foo())
                             .name("foo").build());

        schema.query(object.build());

        return schema.build();
    }
}
