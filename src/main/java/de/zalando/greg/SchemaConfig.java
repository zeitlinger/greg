package de.zalando.greg;

import graphql.schema.GraphQLSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchemaConfig {

    @Bean
    public GraphQLSchema schema() {
        GraphQLSchema.Builder builder = GraphQLSchema.newSchema();
        builder.query(new ObjectType());
        return builder.build();
    }
}
