package com.cadit.main.control;

import com.cadit.main.base.JsonCollectors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.stream.Collectors;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

@Path("message")
public class MessageResponse {

    //esample url: http://localhost:8080/graphQL-example/app/message/Roby
    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getMessageFor(@PathParam("name") final String name){

        String schema = "type Query" + "{hello: String, name: String}";

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        RuntimeWiring runtimeWiring = newRuntimeWiring().type("Query",
                builder -> builder.dataFetcher("hello", new StaticDataFetcher(name))).build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
        ExecutionResult executionResult = build.execute("{hello}");
        // Prints: {hello=xxx}

        System.out.println(executionResult.getData().toString());
        //result to json
        Map<String, Object> resultMap = executionResult.toSpecification();

        return resultMap.entrySet().stream().collect(JsonCollectors.toJsonBuilder()).build();


    }
}
