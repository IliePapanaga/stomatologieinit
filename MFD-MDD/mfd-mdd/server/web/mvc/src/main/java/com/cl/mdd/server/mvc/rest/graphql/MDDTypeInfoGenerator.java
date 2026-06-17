package com.cl.mdd.server.mvc.rest.graphql;

import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import io.leangen.graphql.annotations.types.GraphQLInterface;
import io.leangen.graphql.annotations.types.GraphQLType;
import io.leangen.graphql.annotations.types.GraphQLUnion;
import io.leangen.graphql.metadata.strategy.type.DefaultTypeInfoGenerator;
import io.leangen.graphql.util.ClassUtils;
import io.leangen.graphql.util.Utils;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.Arrays;
import java.util.Optional;

public class MDDTypeInfoGenerator extends DefaultTypeInfoGenerator {

    /**
     *  Change the naming approach only for {@link Connection} wrapper
     *  <p/>
     * @param type
     * @return generated type name
     */
    @Override
    public String generateTypeName(AnnotatedType type) {

        if (type instanceof AnnotatedParameterizedType && Connection.class.equals(ClassUtils.getRawType(type.getType()))) {
            String baseName = simpleName(type);
            StringBuilder genericName = new StringBuilder();
            Arrays.stream(((AnnotatedParameterizedType) type).getAnnotatedActualTypeArguments())
                    .map(this::simpleName)
                    .forEach(argName -> genericName.append(argName));
            genericName.append(baseName);
            return genericName.toString();
        }

        return super.generateTypeName(type);
    }

    private String simpleName(AnnotatedType type) {
        Optional<String>[] names = new Optional[]{
                Optional.ofNullable(type.getAnnotation(GraphQLUnion.class))
                        .map(GraphQLUnion::name),
                Optional.ofNullable(type.getAnnotation(GraphQLInterface.class))
                        .map(GraphQLInterface::name),
                Optional.ofNullable(type.getAnnotation(GraphQLType.class))
                        .map(GraphQLType::name)
        };
        return firstNonEmptyOrDefault(names, ClassUtils.getRawType(type.getType()).getSimpleName());
    }

    private String firstNonEmptyOrDefault(Optional<String>[] optionals, String defaultValue) {
        return Arrays.stream(optionals)
                .map(opt -> opt.filter(Utils::notEmpty))
                .reduce(Utils::or)
                .map(opt -> opt.orElse(defaultValue))
                .get();
    }

}
