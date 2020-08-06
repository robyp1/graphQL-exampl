package com.cadit.main;

import com.cadit.main.api.FilmCharacter;
import com.cadit.main.api.Human;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.*;
import graphql.schema.idl.*;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
import static java.util.Arrays.asList;

public class Main {

    private static final String TYPE_QUERY = "Query";


    public static void main(String[] args) throws IOException {
        esempioFacile();
        esempioDifficile();
    }

    public static void esempioFacile() {
        String schema = "type" + " " + TYPE_QUERY + "{hello: String, name: String}";

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        RuntimeWiring runtimeWiring = newRuntimeWiring().type(TYPE_QUERY,
                builder -> builder.dataFetcher("hello", new StaticDataFetcher("world"))).build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
        ExecutionResult executionResult = build.execute("{hello}");
        // Prints: {hello=world}

        System.out.println(executionResult.getData().toString());
        //result to json
        Map<String, Object> resultMap = executionResult.toSpecification();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(resultMap);
            //Prints json: {"data":{"hello":"world"}}
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    public static String heroQuery = "{" +
            "   hero {" +
            "     name" +
            "     friends {" +
            "       name" +
            "       friends {" +
            "         id" +
            "         name" +
            "       }" +
            "       " +
            "     }" +
            "   }" +
            " }";



    public static void esempioDifficile() throws IOException {
        URL url = Resources.getResource("schema.graphqls");//schema json
        String sdl = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
        ExecutionResult executionResult = build.execute(heroQuery);
        // Prints: {hello=world}

        System.out.println(executionResult.getData().toString());
        //result to json
        Map<String, Object> resultMap = executionResult.toSpecification();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(resultMap);
            //Prints json:
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private static GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private static RuntimeWiring buildWiring() {
        //multiple dataFetcher
        return newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("hero", new StaticDataFetcher(getCharactersData("1000")))
                        .dataFetcher("human", new StaticDataFetcher(getCharactersData("1001")))
                )
                .type(newTypeWiring("Human")
                        .dataFetcher("friends", dataFetchingEnvironment -> {
                                    return asList(getCharactersData("1002"), getCharactersData("1003"));
                                })
                )  .type(newTypeWiring("Hero")
                        .dataFetcher("friends", dataFetchingEnvironment -> {
                                    return asList(getCharactersData("1001"), getCharactersData("1002"));
                                })
                )
                .type(newTypeWiring("Character") //interfaccia Character nello schema è FilmCharacter che è implementata da uno Human ad esempio
                        .typeResolver(characterTypeResolver)
                )
                .build();
    }

    static Human luke = new Human(
            "1000",
            "Luke Skywalker",
            asList("1002", "1003", "2000", "2001"),
            asList(4, 5, 6),
            "Tatooine"
    );

    static Human vader = new Human(
            "1001",
            "Darth Vader",
            asList("1004"),
            asList(4, 5, 6),
            "Tatooine"
    );

    static Human han = new Human(
            "1002",
            "Han Solo",
            asList("1000", "1003", "2001"),
            asList(4, 5, 6),
            null
    );

    static Human leia = new Human(
            "1003",
            "Leia Organa",
            asList("1000", "1002", "2000", "2001"),
            asList(4, 5, 6),
            "Alderaan"
    );

    static Human tarkin = new Human(
            "1004",
            "Wilhuff Tarkin",
            asList("1001"),
            asList(4),
            null
    );

    static Map<String, Human> humanData = new LinkedHashMap<>();

    static {
        humanData.put("1000", luke);
        humanData.put("1001", vader);
        humanData.put("1002", han);
        humanData.put("1003", leia);
        humanData.put("1004", tarkin);
    }

    public static Optional<Object> getCharactersData(String id ){
        Optional<Object> character = Optional.empty();
        if (humanData.get(id)!= null){
           character = Optional.of(humanData.get(id));
        }
        return character;
    }

    /**
     * Character in the graphql type system is an Interface and something needs
     * to decide that concrete graphql object type to return
     */
    static TypeResolver characterTypeResolver = environment -> {
        FilmCharacter character = environment.getObject();
        if (character instanceof Human) {
            return (GraphQLObjectType) environment.getSchema().getType("Human");
        } else {
            return (GraphQLObjectType) environment.getSchema().getType("Droid");
        }
    };
}
