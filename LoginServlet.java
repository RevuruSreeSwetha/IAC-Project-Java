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
import javax.servlet.http.HttpSession;

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
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static String APPLICATION_NAME = "Automation Project2";
    final static String SPREADSHEET_ID = "1YfZBohiYAgm-vZzCtvZTXS6u62fGilOVNm40MXTdlfk";
    static int rowNum=1;
    static String sheetName = "Sheet1!A:D";
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
    public LoginServlet() {
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
		response.setContentType("text/html");
		PrintWriter writer=response.getWriter();
		String uemail=request.getParameter("username");
		String upwd=request.getParameter("password");
		HttpSession session=request.getSession();
		RequestDispatcher dispatcher=null;
		if(uemail==null || uemail.equals("")) {
			request.setAttribute("status", "InvalidEmail");
			dispatcher=request.getRequestDispatcher("login.jsp");
			dispatcher.forward(request, response);
		}
		if(upwd==null || upwd.equals("")) {
			request.setAttribute("status", "InvalidPwd");
			dispatcher=request.getRequestDispatcher("login.jsp");
			dispatcher.forward(request, response);
		}
		else {
			dispatcher=request.getRequestDispatcher("index.jsp");
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
		  ValueRange result = null;
		    try {
		      // Gets the values of the cells in the specified range.
		      result = service.spreadsheets().values().get(SPREADSHEET_ID , RANGE).execute();
		      List<List<Object>> data=result.getValues();
		      int numRows = result.getValues() != null ? result.getValues().size() : 0;
		     
		      
		      for(List<Object> row:data) {
		    	  String storedEmail = (String) row.get(2);
	                String storedPassword = (String) row.get(1);
		    		  if(uemail.equals(storedEmail) && upwd.equals(storedPassword)) {
		    			  request.setAttribute("username", uemail);
		    			  dispatcher=request.getRequestDispatcher("formgeneration.jsp"); 
		    			  dispatcher.forward(request, response);
		    		  }
		    	  else {
		    		  dispatcher=request.getRequestDispatcher("login.jsp");  
		    	  }
		    	  }
		      
		    }
		     catch (GoogleJsonResponseException e) {
		      // TODO(developer) - handle error appropriately
		      GoogleJsonError error = e.getDetails();
		      if (error.getCode() == 404) {
		       writer.printf("Spreadsheet not found with id '%s'.\n", SPREADSHEET_ID );
		      } else {
		        throw e;
		      }
		    }
	}
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
		      throws IOException {
		    // Load client secrets.
		 final String CREDENTIALS_FILE_PATH = "/credentials.json";
		    InputStream in =LoginServlet.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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
