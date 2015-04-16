package com.totvslabs.mdm.restclient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

/**
 *
 * @author shivang
 */
@Provider
public class GZipReaderInterceptor implements ReaderInterceptor {

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context)
            throws IOException, WebApplicationException {
        String encoding = context.getHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING);
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            final InputStream originalInputStream = context.getInputStream();
            context.setInputStream(new GZIPInputStream(originalInputStream));
        }
        return context.proceed();
    }
}
