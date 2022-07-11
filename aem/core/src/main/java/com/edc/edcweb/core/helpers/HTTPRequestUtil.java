package com.edc.edcweb.core.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.serviceImpl.EloquaServiceImpl;

public class HTTPRequestUtil {

	private static final Logger log = LoggerFactory.getLogger(EloquaServiceImpl.class);
	private static String HTTP_REQUEST_POST = "POST";
	private static String HTTP_REQUEST_GET = "GET";

	/**
	 * Send RESTful GET/POST request and return HTTPResponseMessage object
	 * 
	 * @param HTTPRequestParams
	 *            parameters
	 * @return HTTPResponseMessage
	 */
	public static HTTPResponseMessage doRESTfulRequest(HTTPRequestParams parameters) {
		log.info(HTTPRequestUtil.class.getName() + " Method Name doRESTfulRequest() [IN]");

		HTTPResponseMessage respMsg = new HTTPResponseMessage();
		String txtLine = "";
		String strResponse = "";
		try {

			String accessURL = parameters.getUrl();
			String requstBody = parameters.getRequestBody();
			String requestType = parameters.getRequestType();
			if (requestType == null || requestType.isEmpty()) {
				requestType = HTTP_REQUEST_GET;
			}

			URL url = new URL(accessURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(Constants.CONNECT_TIMEOUT);
			conn.setReadTimeout(Constants.CONNECT_TIMEOUT);
			conn.setDoOutput(true);
			conn.setRequestMethod(requestType);

			// Request Header Properties
			HashMap<String, String> reqProperties = parameters.getRequestProperties();
			for (Map.Entry<String, String> entry : reqProperties.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}

			// Body
			if (requestType.equalsIgnoreCase(HTTP_REQUEST_POST) && requstBody != null && !requstBody.isEmpty()) {
				byte[] postData = requstBody.getBytes();
				OutputStream os = conn.getOutputStream();
				os.write(postData);
				os.flush();
				os.close();
			}

			int intRespCode = conn.getResponseCode();
			String strRespMsg = conn.getResponseMessage();

			respMsg.setResponseCode(intRespCode);
			respMsg.setResponseMessage(strRespMsg);

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "UTF-8")); //NOSONAR

			while ((txtLine = br.readLine()) != null) {
				strResponse += txtLine;
			}

			respMsg.setResponseBody(strResponse);
			conn.disconnect();

		} catch (IOException | IllegalArgumentException | IllegalStateException | SecurityException | NullPointerException e) {
			log.error(e.getMessage());

		}

		log.info(HTTPRequestUtil.class.getName() + " Method Name doRESTfulRequest() [OUT]");

		return respMsg;

	}
	
	/**
	 * A generic HTTP post function
	 * 
	 * @param String strURL target URL
	 * @param Map<String, String> reqProperties  request properties
	 * @return HTTPResponseMessage 
	 */
	public static HTTPResponseMessage doHTTPPost(String strURL, Map<String, String> reqProperties,
			Map<String, String> reqArguments) {

		log.info(HTTPRequestUtil.class.getName() + " Method Name doHTTPPost() [IN]");

		String txtLine = "";
		String strResponds = "";
		URL url;
		URLConnection urlConnection;
		HttpURLConnection httpURLConnection;
		HTTPResponseMessage respMsg = new HTTPResponseMessage();

		try {

			// URL Connection
			url = new URL(strURL);
			urlConnection = url.openConnection();
			httpURLConnection = (HttpURLConnection) urlConnection;
			httpURLConnection.setConnectTimeout(Constants.CONNECT_TIMEOUT);
			httpURLConnection.setReadTimeout(Constants.CONNECT_TIMEOUT);
			httpURLConnection.setRequestMethod(HTTP_REQUEST_POST);
			httpURLConnection.setDoOutput(true);

			// Arguments
			StringJoiner stringJoiner = new StringJoiner("&");
			for (Map.Entry<String, String> entry : reqArguments.entrySet()) {
				stringJoiner.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
						+ URLEncoder.encode(entry.getValue(), "UTF-8"));
			}

			byte[] out = stringJoiner.toString().getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			httpURLConnection.setFixedLengthStreamingMode(length);

			// Header Properties
			for (Map.Entry<String, String> entry : reqProperties.entrySet()) {
				if (entry.getValue() != null && entry.getValue().trim().length() > 0) {
					httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			httpURLConnection.connect();

			OutputStream os = httpURLConnection.getOutputStream();
			os.write(out);

			// Get response code
			int respCode = httpURLConnection.getResponseCode();
			
			respMsg.setResponseCode(respCode);
			respMsg.setResponseMessage(httpURLConnection.getResponseMessage());
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader((httpURLConnection.getInputStream()), "UTF-8"));

			while ((txtLine = br.readLine()) != null) {
				strResponds += txtLine;
			}
			
			respMsg.setResponseBody(strResponds);
			

		} catch (IOException | IllegalArgumentException | IllegalStateException | SecurityException | NullPointerException e) {
			log.error(e.getMessage());
		}

		log.info(HTTPRequestUtil.class.getName() + " Method Name doHTTPPost() [OUT]");
		return respMsg;
	}
}
