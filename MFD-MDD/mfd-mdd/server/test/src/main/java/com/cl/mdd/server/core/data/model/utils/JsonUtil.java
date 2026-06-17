package com.cl.mdd.server.core.data.model.utils;

import com.cl.mdd.server.core.data.model.certificates.CertificateDetailsModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.leangen.graphql.metadata.strategy.type.DefaultTypeInfoGenerator;
import io.leangen.graphql.metadata.strategy.value.ValueMapperFactory;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;
import io.leangen.graphql.util.Defaults;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class JsonUtil {

    private static final String MODELS_BASE_PACKAGE = "com.cl.mdd.server";

    /**
     * Reads the value from json path
     * <p/>
     * @param path - node path
     * @param json
     * @return string value from the given path
     */
    public static String valueFromPath(String path, String json) {
        JsonNode jsonNode = traversePath(json, path);
        return read(jsonNode, String.class, defaultObjectMapper());
    }

    /**
     * Reads the typified value from the given path
     * @param path - node path
     * @param json
     * @param type - node type
     * @param <T> - result type
     * @return value of the node
     */
    public static <T> T valueFromPath(String path, String json, Class<T> type) {
        JsonNode jsonNode = traversePath(json, path);
        return read(jsonNode, type, defaultObjectMapper());
    }

    /**
     * Reads the typified value and cast subtypes of provided abstract type from the given path
     * @param path - node path
     * @param json
     * @param type - node type
     * @param abstractTypes - set of abstract Types
     * @param <T> - result type
     * @return value of the node
     */
    public static <T> T valueFromPath(String path, String json, Class<T> type, Set<Type> abstractTypes) {
        JsonNode jsonNode = traversePath(json, path);
        return read(jsonNode, type, typeAwareObjectMapper(abstractTypes));
    }

    public static <T> Collection<T> collectionOf(String json, Class<T> type) {
        ObjectMapper objectMapper = defaultObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        try {
            return objectMapper.readValue(json, typeFactory.constructCollectionType(Collection.class, type));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Reads the typified value the given path

     * @param path - node path
     * @param valueTypeRef  expected return type (will try to map)
     * @param <T> - result type
     * @return result
     */
    public static <T> T valueFromPath(String path, String json,  TypeReference<T> valueTypeRef) {
        JsonNode jsonNode = traversePath(json, path);
        return read(jsonNode, valueTypeRef);
    }

    protected static JsonNode traversePath(String json, String path){
        JsonNode jsonNode = rootNode(json);
        Validate.notBlank(path, "Invalid path");
        List<String> nodes = Arrays.asList(path.split("\\."));
        Iterator<String> iterator = nodes.iterator();
        while(iterator.hasNext()){
            String node = iterator.next();
            jsonNode = jsonNode.get(node);
        }

        return jsonNode;
    }

    protected static ObjectMapper defaultObjectMapper(){
        ObjectMapper objectMapper =  new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.findAndRegisterModules();

        return objectMapper;
    }

    protected static ObjectMapper typeAwareObjectMapper(Set<Type> abstractTypes){
        ObjectMapper objectMapper = buildTypeAwareObjectMapper(abstractTypes);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.findAndRegisterModules();

        return objectMapper;
    }

    protected static ObjectMapper buildTypeAwareObjectMapper(Set<Type> abstractTypes) {
        JacksonValueMapperFactory.AbstractClassAdapterConfigurer configurer = new JacksonValueMapperFactory.AbstractClassAdapterConfigurer();
        return configurer.configure(new ObjectMapper(), abstractTypes, MODELS_BASE_PACKAGE, new DefaultTypeInfoGenerator());
    }

    protected static JsonNode rootNode(String json){
        ObjectMapper objectMapper = defaultObjectMapper();
        JsonNode root = null;
        try {
            root = objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Objects.isNull(root)) throw new RuntimeException("Can't read tree from json: " + json);

        return root;
    }

    protected static  <T> T read(JsonNode node, Class<T> type, ObjectMapper mapper){
        try {
            return mapper.readValue(new TreeTraversingParser(node), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static  <T> T read(JsonNode node,  TypeReference<T> valueTypeRef){
        try {
            return defaultObjectMapper().readValue(new TreeTraversingParser(node), valueTypeRef);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
