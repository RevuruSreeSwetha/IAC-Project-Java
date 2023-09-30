<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Registration Form</title>
<link rel="stylesheet"
	href="fonts/material-icon/css/material-design-iconic-font.min.css">

<!-- Main css -->
<link rel="stylesheet" href="css/style.css">
</head>
<body>
<input type="hidden" id="status" value="<%=request.getAttribute("status")%>"/>
<div class="main">

		<!-- Sing in  Form -->
		<section class="sign-in">
			<div class="container">
				<div class="signin-content">
					

					<div class="signin-form">
						<h2 class="form-title">Registration Form To Join As CA</h2>
						<form method="post" action="./FormServlet" class="register-form"
							id="login-form">
							<input type="hidden" name="username" id="uemail" value="<%=request.getAttribute("username")%>"/>
							<div class="form-group">
								<label for="username"><i
									class="zmdi zmdi-account material-icons-name"></i></label> <input
									type="text" name="name" id="username"
									placeholder="Your Name" />
							</div>
							<div class="form-group">
								<label for="email"><i class="zmdi zmdi-lock"></i></label> <input
									type="email" name="email" id="email"
									placeholder="Email" />
							</div>
							<div class="form-group">
								<label for="mobile"><i class="zmdi zmdi-lock"></i></label> <input
									type="text" name="mobile" id="mobile"
									placeholder="Mobile No" />
							</div>
							<div class="form-group">
								<label for="reason"><i class="zmdi zmdi-lock"></i></label> <input
									type="text" name="reason" id="reason"
									placeholder="Reason to Join as CA" />
							</div>
							<div class="form-group form-button">
								<input type="submit" name="action" id="signin"
									class="form-submit" value="Join as CA" />
							</div>
							<div class="form-group form-button">
								<input type="submit" name="action" id="signin"
									class="form-submit" value="DashBoard" />
							</div>
						</form>
						
					</div>
				</div>
			</div>
		</section>

	</div>
	
<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>
<script type="text/javascript">
    var status=document.getElementById("status").value;
    if(status=="InvalidName"){
    	swal("Sorry!","Enter Name","error");
    }
   if(status=="InvalidEmail"){
    	swal("Sorry!","Enter Email","error");
    }
   if(status=="InvalidMobile"){
   	swal("Sorry!","Enter MobileNumber","error");
   }
   if(status=="InvalidMobileLength"){
   	swal("Sorry!","Check MobileNumber","error");
   }
   if(status=="InvalidReason"){
   	swal("Sorry!","Enter Reason","error");
   }
	</script>
	 <script>
        var alertMessage = '<%= request.getAttribute("alertMessage") %>';
        if (alertMessage=="Mail has been sent successfully") {
            alert(alertMessage);
        }
    </script>
</body>


</html>