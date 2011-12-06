/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
package dk.sfs.riskengine.metoc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

/*
 * Encapsulation of HTTP connection to shore. 
 */
public class ShoreHttp {

	private static final Logger LOG = Logger.getLogger(ShoreHttp.class);

	private static final String USER_AGENT = "RiskEngine";
	private static final String ENCODING = "UTF-8";

	private String uri;
	private String url;

	private HttpClient httpClient;
	private PostMethod method;
	
	private String serverName = "enav.frv.dk";
	private int httpPort = 80;
	private int connectTimeout = 30000;
	private int readTimeout = 60000;
	
	private byte[] responseBody;

	public ShoreHttp() {

	}

	public ShoreHttp(String uri) {
		this();
		setUri(uri);
	}

	public void makeRequest() throws Exception {
		// Make the request
		int resCode = -1;
		try {
			resCode = httpClient.executeMethod(method);
		} catch (Exception e) {
			LOG.error("problem in Metoc request: " + e.getMessage());
			throw e;
		}

		if (resCode != 200) {
			method.releaseConnection();
			throw new Exception("Metoc request returned code "+resCode );
		}

		try {
			responseBody = method.getResponseBody();

			// Check for GZip content encoding
			Header contentEncoding = method.getResponseHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().toUpperCase().indexOf("GZIP") >= 0) {
				responseBody = Compressor.decompress(responseBody);
			}		
			LOG.debug("Received XML: " + new String(responseBody));
		} catch (IOException e) {
			LOG.error("Failed to read response body: " + e.getMessage());
			throw new Exception("Invalid responce ",e);
		}

		method.releaseConnection();
	}

	public void init() {
		httpClient = new HttpClient();
		method = new PostMethod(url);
		HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
		params.setSoTimeout(readTimeout);
		params.setConnectionTimeout(connectTimeout);
		method.setRequestHeader("User-Agent", USER_AGENT);
		method.setRequestHeader("Connection", "close");
		method.addRequestHeader("Accept", "text/*");	
		
		// TODO if compress response
		method.addRequestHeader("Accept-Encoding", "gzip");
	}

	public Object getXmlUnmarshalledContent(String contextPath) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(contextPath);
		Unmarshaller u = jc.createUnmarshaller();
		return u.unmarshal(new ByteArrayInputStream(responseBody));
	}

	public void setXmlMarshalContent(String contextPath, Object obj) throws JAXBException, UnsupportedEncodingException {
		JAXBContext jc = JAXBContext.newInstance(contextPath);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
		StringWriter sw = new StringWriter();		
		m.marshal(obj, sw);
		LOG.debug("XML request: " + sw.toString());
		setRequestBody(sw.toString().getBytes(ENCODING), ENCODING);
	}

	public void setRequestBody(byte[] body, String contentType) {
		// TODO if Gzip Compress request
//		try {			
//			byte[] compressed = Compressor.compress(body);						
//			body = compressed;
//			method.addRequestHeader("Content-Encoding", "gzip");
//		} catch (IOException e) {
//			LOG.error("Failed to GZip request: " + e.getMessage());
//		}
		
		ByteArrayRequestEntity requestEntity = new ByteArrayRequestEntity(body, contentType);
		method.setRequestEntity(requestEntity);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
		this.url = "http://" + serverName;
		if (httpPort != 80) {
			this.url += ":" + serverName;
		}
		this.url += this.uri;
	}

	public String getHost() {
		return serverName;
	}

	public void setHost(String host) {
		this.serverName = host;
	}

	public int getPort() {
		return httpPort;
	}

	public void setPort(int port) {
		this.httpPort = port;
	}

}
