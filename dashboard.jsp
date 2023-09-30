<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


    
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Creative Text Page</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .text-container {
            background-color: #fff;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
            text-align: center;
        }

        h1 {
            color: #333;
            font-size: 36px;
            margin-bottom: 20px;
        }

        p {
            color: #555;
            font-size: 18px;
        }

        .highlight {
            color: #ff6600;
            font-weight: bold;
        }
    </style>
   
</head>
<body>
    <div class="text-container">
    
    <h1 >The link has been clicked <span class="highlight" id="numberParagraph">0 </span> times.</h1>
    </div>
</body>
 <script>
    var number = '<%= request.getAttribute("number") %>';
    document.getElementById("numberParagraph").textContent = number;
</script>
</html>
