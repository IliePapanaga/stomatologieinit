package com.cl.mdd.server.mvc.rest;

import graphql.ExecutionInput;
import graphql.GraphQL;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/graphql")
public class GraphQLController {
    @Autowired
    private GraphQL securedGraphQL;
    @Autowired
    private GraphQL unsecuredGraphQL;

    /**
     * Secured GraphQL endpoint
     * <p/>
     * Handles graphQl requests
     * @param request
     * @return execution result
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object execute(@RequestBody Map<String, Object> request) {
        return executeRequest(securedGraphQL, request);
    }

    /**
     * Unsecured GraphQL endpoint
     * <p/>
     * Handles unsecured graphQl requests
     * @param request
     * @return execution result
     */
    @RequestMapping("public")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object unsecuredExecute(@RequestBody Map<String, Object> request) {
        return executeRequest(unsecuredGraphQL, request);
    }

    protected Object executeRequest(GraphQL graphQL, @RequestBody Map<String, Object> request) {
        Object query = request.get("query");
        Validate.notNull(query);
        Object operationName = request.get("operationName");
        // TODO: next is a typo, isn't it?
        Validate.notNull(query);

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query((String) query)
                .operationName((String) operationName)
                .build();

        return graphQL.execute(executionInput);
    }

}
