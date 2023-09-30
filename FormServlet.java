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
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
 * Servlet implementation class FormServlet
 */
public class FormServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static String APPLICATION_NAME = "Automation Project3";
    final static String SPREADSHEET_ID = "1Khzlt8EOBTv6lHgsdozHc3DchqNWZVV9zzLm2fJNYA0";
    static int rowNum=1;
    static String sheetName = "Sheet1!A:E";
    List<String> columns = Arrays.asList("A", "B", "C");
    final static String RANGE = "Sheet1";
    final static GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	  final static String TOKENS_DIRECTORY_PATH = "/tokens";
	  final static List<String> SCOPES =
		      Collections.singletonList(SheetsScopes.SPREADSHEETS);
		  final String CREDENTIALS_FILE_PATH = "java/credentials1.json";
		  private static AtomicInteger clickCounter = new AtomicInteger(0);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FormServlet() {
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
		String email=request.getParameter("email");
		String contact=request.getParameter("mobile");
		String reason=request.getParameter("reason");
		String uemail=request.getParameter("username");
		String emailContent=generateEmailContent(name);
		String subject = "Welcome to Our Website!";
		RequestDispatcher dispatcher=null;
		response.setContentType("text/html");
		PrintWriter writer=response.getWriter();
		String action = request.getParameter("action");
		
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
		if ("Join as CA".equals(action)) {
			if(name==null || name.equals("")) {
				request.setAttribute("status", "InvalidName");
				dispatcher=request.getRequestDispatcher("formgeneration.jsp");
				dispatcher.forward(request, response);
			}
			if(email==null || email.equals("")) {
				request.setAttribute("status", "InvalidEmail");
				dispatcher=request.getRequestDispatcher("formgeneration.jsp");
				dispatcher.forward(request, response);
			}
			if(contact==null || contact.equals("")) {
				request.setAttribute("status", "InvalidMobile");
				dispatcher=request.getRequestDispatcher("formgeneration.jsp");
				dispatcher.forward(request, response);
			}
			else if(contact.length()>10){
				request.setAttribute("status", "InvalidMobileLength");
				dispatcher=request.getRequestDispatcher("formgeneration.jsp");
				dispatcher.forward(request, response);
			}
			if(reason==null || reason.equals("")) {
				request.setAttribute("status", "InvalidReason");
				dispatcher=request.getRequestDispatcher("formgeneration.jsp");
				dispatcher.forward(request, response);
			}
		  Credential credential=getCredentials(HTTP_TRANSPORT );
		  Sheets service =
			        new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
			            .setApplicationName(APPLICATION_NAME)
			            .build();
		  AppendValuesResponse result1 = null;
		  int currentCount = clickCounter.incrementAndGet();
		  List<Object> data1=new ArrayList<Object>(Arrays.asList(name,email,contact,reason,currentCount));
		  List<List<Object>> values=Arrays.asList(data1);
		  try {
			    // Updates the values in the specified range.
			    ValueRange body = new ValueRange()
			        .setValues(values);
			    result1 = service.spreadsheets().values()
		                .append(SPREADSHEET_ID, RANGE , body)
		                .setValueInputOption("RAW")
		                .execute();
			    
			  } catch (GoogleJsonResponseException e) {
			    // TODO(developer) - handle error appropriately
			    GoogleJsonError error = e.getDetails();
			    if (error.getCode() == 404) {
			    writer.println("Spreadsheet not found id");
			    } else {
			      throw e;
			    }
			  }
		  if (sendWelcomeEmail(email,subject,emailContent)) {
			  request.setAttribute("alertMessage", "Mail has been sent successfully");

			    // Forward to formgeneration.jsp
			  request.setAttribute("alertMessage", "Mail has been sent successfully");
			  request.getRequestDispatcher("formgeneration.jsp").forward(request, response);

	            
	        } else {
	            response.getWriter().write("Failed to send welcome email.");
	        }
		}
		else if ("DashBoard".equals(action)) {
			NetHttpTransport HTTP_TRANSPORT1 = null;
			try {
				HTTP_TRANSPORT1 = GoogleNetHttpTransport.newTrustedTransport();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Credential credential1=getCredentials(HTTP_TRANSPORT1 );
			  Sheets service =
				        new Sheets.Builder(HTTP_TRANSPORT1, JSON_FACTORY, credential1)
				            .setApplicationName(APPLICATION_NAME)
				            .build();
			  ValueRange result = null;
			 
			    try {
			      // Gets the values of the cells in the specified range.
			      result = service.spreadsheets().values().get(SPREADSHEET_ID , RANGE).execute();
			      List<List<Object>> data=result.getValues();
			      for(List<Object> row:data) {
			    	  String storedEmail = (String) row.get(1);
		               
			    		  if(uemail.equals(storedEmail) ) {
			    			  String number=(String)row.get(4);
			    			  request.setAttribute("number", number);
			    			  request.getRequestDispatcher("/dashboard.jsp").forward(request, response); 
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
	public static String generateEmailContent(String recipientName) {
        // Create a custom email content with a personalized message
		String baseURL="https://www.industryacademiacommunity.com";
		String utmSource="email";
		String utmMedium="newsLetter";
		String utmCampaign="summer_sale";
		String fullURL=baseURL+"?utm_source="+utmSource+"&utm_medium="+utmMedium+"&utm_campaign="+utmCampaign;
        return "Dear " + recipientName + ",\n\n"
            + "Welcome to IAC! We're excited to see you enrolled as CA in our community.\n\n"+"You can access our website through the following link:\n\n"+fullURL+"\n\n"
            + "If you have any quetsions,simply reply to this mail\n\n"+"Keeg Going!\n"+"Industry Academia Community\n"+"Cloud counselage Pvt.Ltd";
    }
	public static boolean sendWelcomeEmail(String recipientEmail,String subject,String emailContent) {
        final String senderEmail = "sreeswetha.revuru@gmail.com"; // Your email address
        final String senderPassword = "ysjrvgwzlcxartms"; // Your email password
        
        
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Change for other email providers
        properties.put("mail.smtp.port", "587"); // Change if needed
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.debug", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.auth.mechanisms", "PLAIN");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(emailContent);
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

}
