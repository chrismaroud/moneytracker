package com.bitsfromspace.moneytracker.web.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author chris
 * @since 04-11-15.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    private Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    public GlobalExceptionMapper() {
        System.out.println("GlobalExceptionMapper wired up");
    }

    @Override
    public Response toResponse(Exception e) {
        LOG.log(Level.SEVERE, e.getMessage(), e);
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .entity(e.getMessage())
                .build();
    }
}
