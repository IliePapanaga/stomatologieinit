package com.cl.mdd.server.mvc.config;


import com.cl.mdd.server.mvc.rest.graphql.MDDTypeInfoGenerator;
import com.cl.mdd.server.mvc.rest.graphql.exception.GraphQLExceptionResolver;
import com.cl.mdd.server.mvc.rest.graphql.provider.GraphQLProvider;
import com.cl.mdd.server.mvc.rest.graphql.provider.unsecured.UnsecuredGraphQLProvider;
import com.cl.mdd.server.mvc.rest.graphql.strategy.CustomInterfaceMappingStrategy;
import com.cl.mdd.server.mvc.rest.graphql.strategy.MDDTypeMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import graphql.GraphQL;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.execution.batched.BatchedExecutionStrategy;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.generator.mapping.common.ObjectTypeMapper;
import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder;
import io.leangen.graphql.metadata.strategy.type.DefaultTypeInfoGenerator;
import io.leangen.graphql.metadata.strategy.type.TypeInfoGenerator;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

@Configuration
@Import(ExceptionResolversConfig.class)
public class GraphQLConfig {
    private static final String MODELS_BASE_PACKAGE = "com.cl.mdd.server";

    @Autowired
    private List<GraphQLProvider> graphQLProviders;
    @Autowired
    private List<UnsecuredGraphQLProvider> unsecuredGraphQLProviders;
    @Autowired
    private GraphQLExceptionResolver graphQLExceptionResolver;

    @Bean
    @Qualifier("securedGraphQL")
    public GraphQL securedGraphQL() {
        return GraphQL.newGraphQL(graphQLSchema(graphQLProviders))
                .queryExecutionStrategy(new BatchedExecutionStrategy(graphQLExceptionResolver))
                .mutationExecutionStrategy(new AsyncSerialExecutionStrategy(graphQLExceptionResolver))
                .build();
    }

    @Bean
    @Qualifier(value = "unsecuredGraphQL")
    public GraphQL unsecuredGraphQL() {
        return GraphQL.newGraphQL(graphQLSchema(unsecuredGraphQLProviders))
                .queryExecutionStrategy(new BatchedExecutionStrategy(graphQLExceptionResolver))
                .mutationExecutionStrategy(new AsyncSerialExecutionStrategy(graphQLExceptionResolver))
                .build();
    }

    public GraphQLSchema graphQLSchema(List graphQLProviders) {
        CustomInterfaceMappingStrategy interfaceStrategy = new CustomInterfaceMappingStrategy(true, MODELS_BASE_PACKAGE);
        GraphQLSchemaGenerator graphQLSchemaGenerator = new GraphQLSchemaGenerator()
                .withResolverBuilders(
                        //Resolve by annotations
                        new AnnotatedResolverBuilder())
                .withInterfaceMappingStrategy(interfaceStrategy)
                .withDefaultMappers()
                .withTypeMappers(new MDDTypeMapper(interfaceStrategy, new ObjectTypeMapper(), MODELS_BASE_PACKAGE));

        graphQLProviders.forEach(s-> graphQLSchemaGenerator.withOperationsFromSingleton(s, AopUtils.getTargetClass(s)));

        return graphQLSchemaGenerator
                .withValueMapperFactory(new JacksonValueMapperFactory(MODELS_BASE_PACKAGE, new DefaultTypeInfoGenerator(), new MDDClassAdapterConfigurer()))
                .withMetaDataGenerator(new MDDTypeInfoGenerator())
                .generate();
    }

    private static class MDDClassAdapterConfigurer extends JacksonValueMapperFactory.AbstractClassAdapterConfigurer {

        @Override
        public ObjectMapper configure(ObjectMapper objectMapper, Set<Type> abstractTypes, String basePackage, TypeInfoGenerator metaDataGen) {
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

            return super.configure(objectMapper, abstractTypes, basePackage, metaDataGen);
        }
    }

}
