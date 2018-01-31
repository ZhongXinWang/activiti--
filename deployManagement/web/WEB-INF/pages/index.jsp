<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="./base/common.jsp"%>
<html>
<head>
    <title>首页</title>
    <script src="${ctx}/static/js/jquery-3.2.1.min.js"></script>
    <link rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css">


</head>
<body>
<div style="width:900px;margin:50px auto;">

    <a class="btn btn-primary"  href="${ctx}/accounts">用户管理</a>
    <a class="btn btn-primary"  href="${ctx}/deployments">部署管理</a>

</div>
</body>
</html>
