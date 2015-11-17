package com.totvslabs.mdm.restclient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import sun.misc.BASE64Encoder;

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

    private static void decompressGzipFile(String gzipFile, String newFile) {
        try {
            FileInputStream fis = new FileInputStream(gzipFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int len;
            while((len = gis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
            //close resources
            fos.close();
            gis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
         
    }
 
    private static void compressGzipFile(String file, String gzipFile) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(gzipFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while((len=fis.read(buffer)) != -1){
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            gzipOS.close();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
         


    public static void main(String[] args) {
    	  String file = "/Users/poffo/dados.txt";
          String gzipFile = "/Users/poffo/Downloads/GCOLIGADA.gz";
          String newFile = "/Users/poffo/udados.txt";
           
//          compressGzipFile(file, gzipFile);
           
          try {
			String string = new String(Files.readAllBytes(Paths.get(gzipFile)));
			String encode = new BASE64Encoder().encode(string.getBytes());
			System.out.println(string);
			System.out.println();
			System.out.println(encode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          
          decompressGzipFile(gzipFile, newFile);
	}
}
