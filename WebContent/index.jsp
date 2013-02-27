<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <!--
    Copyright 2010-2011 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.Amazon/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
  -->
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Welcome</title>
  <style>
  body {
    color: #ffffff;
    background-color: #c7c7c7;
    font-family: Arial, sans-serif;
    font-size:14px;
    -moz-transition-property: text-shadow;
    -moz-transition-duration: 4s;
    -webkit-transition-property: text-shadow;
    -webkit-transition-duration: 4s;
    text-shadow: none;
  }
  body.blurry {
    -moz-transition-property: text-shadow;
    -moz-transition-duration: 4s;
    -webkit-transition-property: text-shadow;
    -webkit-transition-duration: 4s;
    text-shadow: #fff 0px 0px 25px;
  }
  a {
    color: #0188cc;
  }
  .textColumn, .linksColumn {
    padding: 2em;
  }
  .textColumn {
    position: absolute;
    top: 0px;
    right: 50%;
    bottom: 0px;
    left: 0px;

    text-align: right;
    padding-top: 11em;
    background-color: #0188cc;
    background-image: -moz-radial-gradient(left top, circle, #6ac9f9 0%, #0188cc 60%);
    background-image: -webkit-gradient(radial, 0 0, 1, 0 0, 500, from(#6ac9f9), to(#0188cc));
  }
  .textColumn p {
    width: 75%;
    float:right;
  }
  .linksColumn {
    position: absolute;
    top:0px;
    right: 0px;
    bottom: 0px;
    left: 50%;

    background-color: #c7c7c7;
  }

  h1 {
    font-size: 500%;
    font-weight: normal;
    margin-bottom: 0em;
  }
  h2 {
    font-size: 200%;
    font-weight: normal;
    margin-bottom: 0em;
  }
  ul {
    padding-left: 1em;
    margin: 0px;
  }
  li {
    margin: 1em 0em;
  }

  /* minigame */
.toast {
  color: #000;
  border: 1px solid #444;
  background-color: #eee;
  -webkit-border-radius: 8px;
  -moz-border-radius: 8px;
  border-radius: 8px;
  position: fixed;
  top: 0px;
  margin: 5px 0px;
  padding: 5px 10px 5px 5px;

  opacity: 0;
  -webkit-transition: opacity 1s;
  -moz-transition: opacity 1s;  
  transition: opacity 1s;
}
.toast.shown {
  opacity: 1;
  -webkit-transition: opacity 1s;  
  -moz-transition: opacity 1s;  
  transition: opacity 1s;
}
.toast div {
  font-size: 25px;
  float: left;
  margin-top: 5px;
  margin-bottom: 5px;
}
.toast h2 {
  font-size: 18px;
  margin: 0px;
  margin-left: 35px;
}
.toast p {
  font-size: 13px;
  margin: 3px 0px 0px 35px;
}


.pointsDisplay {
  position: fixed;
  left: 5px;
  bottom: 5px;
  background-color: #eee;
  border: 1px solid #444;
  padding: 5px;
  color: #000;
  font-size:175%;
}
  </style>
</head>
<body id="sample">
  <div class="textColumn">
    <h1>Congratulations</h1>
    <p>Your first AWS Elastic Beanstalk Application is now running on your own dedicated environment in the AWS Cloud</p>
  </div>
  
</body>
</html>
