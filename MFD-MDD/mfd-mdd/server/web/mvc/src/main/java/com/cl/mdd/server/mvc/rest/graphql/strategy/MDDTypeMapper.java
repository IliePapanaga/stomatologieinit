package com.cl.mdd.server.mvc.rest.graphql.strategy;

import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.graphql.generator.BuildContext;
import io.leangen.graphql.generator.OperationMapper;
import io.leangen.graphql.generator.mapping.common.InterfaceMapper;
import io.leangen.graphql.generator.mapping.common.ObjectTypeMapper;
import io.leangen.graphql.generator.mapping.strategy.InterfaceMappingStrategy;
import io.leangen.graphql.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 *  Custom type mapper.
 *  <p/>
 *  Registers all types from the given packages.
 *  Used a workaround for discovering graph ql output types using {@link CustomInterfaceMappingStrategy}
 */
public class MDDTypeMapper extends InterfaceMapper {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InterfaceMappingStrategy interfaceStrategy;
    private String basePackage;

    public MDDTypeMapper(InterfaceMappingStrategy interfaceStrategy, ObjectTypeMapper objectTypeMapper, String basePackage) {
        super(interfaceStrategy, objectTypeMapper);
        this.interfaceStrategy= interfaceStrategy;
        this.basePackage = basePackage;
    }

    @Override
    public GraphQLInterfaceType toGraphQLType(String typeName, AnnotatedType javaType, Set<Type> abstractTypes, OperationMapper operationMapper, BuildContext buildContext) {

        GraphQLInterfaceType type = super.toGraphQLType(typeName, javaType, abstractTypes, operationMapper, buildContext);

        if (javaType.getType().getTypeName().startsWith(basePackage)) {
            implementations(javaType).forEach(impl ->
                    getImplementingType(impl, abstractTypes, operationMapper, buildContext)
                            .ifPresent(implType -> buildContext.typeRepository.registerDiscoveredCovariantType(type.getName(), impl, implType)));
        }
        return type;
    }

    private List<AnnotatedType> implementations(AnnotatedType javaType) {
        // first use SPQR approach
        Set<AnnotatedType> annotatedTypes = new LinkedHashSet<>(ClassUtils.findImplementations(javaType, basePackage));
        // then use Spring as it discovers more
        if(javaType.getType() instanceof Class) {
            ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AssignableTypeFilter((Class) javaType.getType()));
            provider.findCandidateComponents(basePackage.replace('.', '/'))
                    .forEach(beanDefinition -> {
                        try {
                            annotatedTypes.add(GenericTypeReflector.annotate(
                                    Class.forName(beanDefinition.getBeanClassName())));
                        } catch (ClassNotFoundException e) {
                            logger.warn("skipping {} for GraphQL schema", beanDefinition.getBeanClassName());
                        }
                    });
        }
        return new ArrayList<>(annotatedTypes);
    }

    private Optional<GraphQLObjectType> getImplementingType(AnnotatedType implType, Set<Type> abstractTypes, OperationMapper operationMapper, BuildContext buildContext) {
        return Optional.of(implType)
                .filter(impl -> !GenericTypeReflector.isMissingTypeParameters(impl.getType()))
                .filter(impl -> !interfaceStrategy.supports(impl))
                .map(impl -> operationMapper.toGraphQLType(impl, abstractTypes, buildContext))
                .filter(impl -> impl instanceof GraphQLObjectType)
                .map(impl -> (GraphQLObjectType) impl);
    }
}
