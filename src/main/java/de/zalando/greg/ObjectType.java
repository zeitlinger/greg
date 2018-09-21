package de.zalando.greg;

import java.util.ArrayList;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;

public class ObjectType extends GraphQLObjectType {

    public ObjectType() {
        super("root", null, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition(String name) {
        if ("__embed".equals(name)) {
            //todo: fetch the value of the current node
        }
        if ("__all".equals(name)) {
            //todo: return the current tree as json
        }

        if (name.startsWith("_")) {
            //selects an object rather than a field
            //this is merely to satisfy the schema parser

            return GraphQLFieldDefinition.newFieldDefinition()
                    .type(this)
                    .dataFetcher(dataFetchingEnvironment -> new Object())
                    .name("objectField").build();
        }

        //any field
        return GraphQLFieldDefinition.newFieldDefinition()
                .type(Scalars.GraphQLString)
                .dataFetcher(dataFetchingEnvironment -> "hello world")
                .name("stringField").build();
    }
}
