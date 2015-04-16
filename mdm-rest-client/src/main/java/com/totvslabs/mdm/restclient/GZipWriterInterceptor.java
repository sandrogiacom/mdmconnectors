package com.totvslabs.mdm.restclient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
public class GZipWriterInterceptor implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context)
            throws IOException, WebApplicationException {
        String encoding = (String) context.getHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING);
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            final OutputStream outputStream = context.getOutputStream();
            context.setOutputStream(new GZIPOutputStream(outputStream));
        }
        context.proceed();
    }
}
