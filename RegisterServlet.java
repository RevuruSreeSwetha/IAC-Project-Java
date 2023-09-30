package com.tcs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

/**
 * Servlet implementation class RegisterServlet
 */
public class RegisterServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	final static String APPLICATION_NAME = "Automation Project2";
    final static String SPREADSHEET_ID = "1YfZBohiYAgm-vZzCtvZTXS6u62fGilOVNm40MXTdlfk";
    static int rowNum=1;
    static String sheetName = "Sheet1!A:C";
    List<String> columns = Arrays.asList("A", "B", "C");
    final static String RANGE = "Sheet1";
    final static GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	  final static String TOKENS_DIRECTORY_PATH = "/tokens";
	  final static List<String> SCOPES =
		      Collections.singletonList(SheetsScopes.SPREADSHEETS);
		  final String CREDENTIALS_FILE_PATH = "java/credentials.json";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String name=request.getParameter("name");
		String pswd=request.getParameter("pass");
		String email=request.getParameter("email");
		String mobile=request.getParameter("contact");
		String con_pass=request.getParameter("re_pass");
		RequestDispatcher dispatcher=null;
		response.setContentType("text/html");
		PrintWriter writer=response.getWriter();
		if(name==null || name.equals("")) {
			request.setAttribute("status", "InvalidName");
			dispatcher=request.getRequestDispatcher("registration.jsp");
			dispatcher.forward(request, response);
		}
		if(email==null || email.equals("")) {
			request.setAttribute("status", "InvalidEmail");
			dispatcher=request.getRequestDispatcher("registration.jsp");
			dispatcher.forward(request, response);
		}
		if(pswd==null || pswd.equals("")) {
			request.setAttribute("status", "InvalidPwd");
			dispatcher=request.getRequestDispatcher("registration.jsp");
			dispatcher.forward(request, response);
		}
		else if(!pswd.equals(con_pass)) {
			request.setAttribute("status", "InvalidPassword");
			dispatcher=request.getRequestDispatcher("registration.jsp");
			dispatcher.forward(request, response);
		}
		if(mobile==null || mobile.equals("")) {
			request.setAttribute("status", "InvalidContact");
			dispatcher=request.getRequestDispatcher("registration.jsp");
			dispatcher.forward(request, response);
		}
		else if(mobile.length()>10){
			request.setAttribute("status", "InvalidMobileLength");
			dispatcher=request.getRequestDispatcher("registration.jsp");
			dispatcher.forward(request, response);
		}
		NetHttpTransport HTTP_TRANSPORT = null;
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  Credential credential=getCredentials(HTTP_TRANSPORT );
		  
		  Sheets service =
			        new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
			            .setApplicationName(APPLICATION_NAME)
			            .build();
		  AppendValuesResponse result1 = null;
		  List<Object> data1=new ArrayList<Object>(Arrays.asList(name,pswd,email,mobile));
		  List<List<Object>> values=Arrays.asList(data1);
		  try {
		    // Updates the values in the specified range.
		    ValueRange body = new ValueRange()
		        .setValues(values);
		    result1 = service.spreadsheets().values()
	                .append(SPREADSHEET_ID, RANGE , body)
	                .setValueInputOption("RAW")
	                .execute();
		    dispatcher=request.getRequestDispatcher("registration.jsp");
		    dispatcher.forward(request, response);
		  } catch (GoogleJsonResponseException e) {
		    // TODO(developer) - handle error appropriately
		    GoogleJsonError error = e.getDetails();
		    if (error.getCode() == 404) {
		    writer.println("Spreadsheet not found id");
		    } else {
		      throw e;
		    }
		  }
		
	}
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
		      throws IOException {
		    // Load client secrets.
		 final String CREDENTIALS_FILE_PATH = "/credentials.json";
		    InputStream in =RegisterServlet.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		    if (in == null) {
		      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		    }
		    GoogleClientSecrets clientSecrets =
		        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		    // Build flow and trigger user authorization request.
		  GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
		        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
		        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
		        .setAccessType("offline")
		        .build();
		    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("sreeswetha.revuru@gmail.com");
		  }
}
