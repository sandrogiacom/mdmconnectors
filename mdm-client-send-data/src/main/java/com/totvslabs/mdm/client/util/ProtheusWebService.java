package com.totvslabs.mdm.client.util;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class ProtheusWebService {
    public static void main(String args[]) {
    	ProtheusWebService.callWebService("http://172.16.103.116/ws/FWWSMODEL.apw", "MATA030", "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+PE1BVEEwMzAgT3BlcmF0aW9uPSIzIiB2ZXJzaW9uPSIxLjAxIj4KICA8TUFUQTAzMF9TQTEgbW9kZWx0eXBlPSJGSUVMRFMiPgogICAgPEExX0ZJTElBTCBvcmRlcj0iMSI+CiAgICAgIDx2YWx1ZT5Sb2Jzb25ubm48L3ZhbHVlPgogICAgPC9BMV9GSUxJQUw+CiAgICA8QTFfQ09EIG9yZGVyPSIyIj4KICAgICAgPHZhbHVlPlRFU1QwMTwvdmFsdWU+CiAgICA8L0ExX0NPRD4KICAgIDxBMV9MT0pBIG9yZGVyPSIzIj4KICAgICAgPHZhbHVlPjAwPC92YWx1ZT4KICAgIDwvQTFfTE9KQT4KICAgIDxBMV9OT01FIG9yZGVyPSI0Ij4KICAgICAgPHZhbHVlPlJvYnNvbm5ubjwvdmFsdWU+CiAgICA8L0ExX05PTUU+CiAgICA8QTFfUEVTU09BIG9yZGVyPSI1Ij4KICAgICAgPHZhbHVlPlJvYnNvbm5ubjwvdmFsdWU+CiAgICA8L0ExX1BFU1NPQT4KICAgIDxBMV9FTkQgb3JkZXI9IjYiPgogICAgICA8dmFsdWU+UlVBIFRFU1RFLCAxMTE8L3ZhbHVlPgogICAgPC9BMV9FTkQ+CiAgICA8QTFfTlJFRFVaIG9yZGVyPSI3Ij4KICAgICAgPHZhbHVlPlJvYnNvbm5ubjwvdmFsdWU+CiAgICA8L0ExX05SRURVWj4KICAgIDxBMV9CQUlSUk8gb3JkZXI9IjgiPgogICAgICA8dmFsdWU+VEVTVEU8L3ZhbHVlPgogICAgPC9BMV9CQUlSUk8+CiAgICA8QTFfVElQTyBvcmRlcj0iOSI+CiAgICAgIDx2YWx1ZT5SPC92YWx1ZT4KICAgIDwvQTFfVElQTz4KICAgIDxBMV9FU1Qgb3JkZXI9IjEwIj4KICAgICAgPHZhbHVlPlNQPC92YWx1ZT4KICAgIDwvQTFfRVNUPgogICAgPEExX0NFUCBvcmRlcj0iMTIiPgogICAgICA8dmFsdWU+MTIzNDU2NzwvdmFsdWU+CiAgICA8L0ExX0NFUD4KICAgIDxBMV9NVU4gb3JkZXI9IjE0Ij4KICAgICAgPHZhbHVlPlRFU1RFPC92YWx1ZT4KICAgIDwvQTFfTVVOPgogICAgPEExX0VORENPQiBvcmRlcj0iMTkiPgogICAgICA8dmFsdWUvPgogICAgPC9BMV9FTkRDT0I+CiAgICA8QTFfREREIG9yZGVyPSIyMSI+CiAgICAgIDx2YWx1ZS8+CiAgICA8L0ExX0RERD4KICAgIDxBMV9FTkRFTlQgb3JkZXI9IjIzIj4KICAgICAgPHZhbHVlLz4KICAgIDwvQTFfRU5ERU5UPgogICAgPEExX1RFTCBvcmRlcj0iMjQiPgogICAgICA8dmFsdWUvPgogICAgPC9BMV9URUw+CiAgICA8QTFfQ0dDIG9yZGVyPSIyOCI+CiAgICAgIDx2YWx1ZT5Sb2Jzb25ubm48L3ZhbHVlPgogICAgPC9BMV9DR0M+CiAgICA8QTFfSU5TQ1Igb3JkZXI9IjMxIj4KICAgICAgPHZhbHVlPlJvYnNvbm5ubjwvdmFsdWU+CiAgICA8L0ExX0lOU0NSPgogICAgPEExX0lOU0NSTSBvcmRlcj0iMzIiPgogICAgICA8dmFsdWU+Um9ic29ubm5uPC92YWx1ZT4KICAgIDwvQTFfSU5TQ1JNPgogICAgPEExX1BBSVMgb3JkZXI9IjMzIj4KICAgICAgPHZhbHVlLz4KICAgIDwvQTFfUEFJUz4KICAgIDxBMV9WRU5EIG9yZGVyPSIzNSI+CiAgICAgIDx2YWx1ZS8+CiAgICA8L0ExX1ZFTkQ+CiAgICA8QTFfVFJBTlNQIG9yZGVyPSI0MyI+CiAgICAgIDx2YWx1ZS8+CiAgICA8L0ExX1RSQU5TUD4KICAgIDxBMV9CQUlSUk9DIG9yZGVyPSIxMDAiPgogICAgICA8dmFsdWUvPgogICAgPC9BMV9CQUlSUk9DPgogICAgPEExX0NFUEMgb3JkZXI9IjEwMSI+CiAgICAgIDx2YWx1ZS8+CiAgICA8L0ExX0NFUEM+CiAgICA8QTFfTVVOQyBvcmRlcj0iMTAyIj4KICAgICAgPHZhbHVlLz4KICAgIDwvQTFfTVVOQz4KICAgIDxBMV9FU1RDIG9yZGVyPSIxMDMiPgogICAgICA8dmFsdWUvPgogICAgPC9BMV9FU1RDPgogICAgPEExX0JBSVJST0Ugb3JkZXI9IjEwNCI+CiAgICAgIDx2YWx1ZS8+CiAgICA8L0ExX0JBSVJST0U+CiAgICA8QTFfQ0VQRSBvcmRlcj0iMTA1Ij4KICAgICAgPHZhbHVlLz4KICAgIDwvQTFfQ0VQRT4KICAgIDxBMV9NVU5FIG9yZGVyPSIxMDYiPgogICAgICA8dmFsdWUvPgogICAgPC9BMV9NVU5FPgogICAgPEExX0VTVEUgb3JkZXI9IjEwNyI+CiAgICAgIDx2YWx1ZS8+CiAgICA8L0ExX0VTVEU+CiAgICA8QTFfRU1BSUwgb3JkZXI9IjEzMiI+CiAgICAgIDx2YWx1ZS8+CiAgICA8L0ExX0VNQUlMPgogICAgPEExX0NPTVBMRU0gb3JkZXI9IjE1OSI+CiAgICAgIDx2YWx1ZS8+CiAgICA8L0ExX0NPTVBMRU0+CiAgICA8QTFfRFRDQUQgb3JkZXI9IjE2MSI+CiAgICAgIDx2YWx1ZT5Sb2Jzb25ubm48L3ZhbHVlPgogICAgPC9BMV9EVENBRD4KICA8L01BVEEwMzBfU0ExPgo8L01BVEEwMzA+");
    }
    
    public static boolean callWebService(String serverURL, String applicationID, String data) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(serverURL, applicationID, data), serverURL);

            // Process the SOAP Response
            printSOAPResponse(soapResponse);

            soapConnection.close();
            
            return true;
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
        }
        
        return false;
    }

    private static SOAPMessage createSOAPRequest(String serverURL, String applicationID, String data) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = serverURL;

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("fww", serverURI);

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("PUTXMLDATA", "fww");
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("USERTOKEN","fww");
        soapBodyElem1.addTextNode("");
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("MODELID","fww");
        soapBodyElem2.addTextNode(applicationID);
        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("MODELXML","fww");
        soapBodyElem3.addTextNode(data);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI  + "/PUTXMLDATA");

        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }

    /**
     * Method used to print the SOAP Response
     */
    private static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
    }
}
