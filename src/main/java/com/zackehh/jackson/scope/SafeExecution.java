package com.zackehh.jackson.scope;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Interface to execute a block in a scope which catches an IOException.
 *
 * The intent is that this interface can be used to execute ObjectMapper
 * based operations without having to implement your own catching.
 *
 * @param <T> the result typing.
 */
public interface SafeExecution<T> {

    /**
     * Executes the custom logic on a provided ObjectMapper and
     * returns the defined custom type as a result.
     *
     * @param mapper an ObjectMapper instance to use in scope.
     * @return an instance of the defined custom type.
     * @throws IOException
     */
    T apply(ObjectMapper mapper) throws IOException;
}