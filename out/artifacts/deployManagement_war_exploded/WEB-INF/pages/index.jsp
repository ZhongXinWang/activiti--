<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="base/common.jsp"%>
<html>
<head>
    <title>流程部署</title>
    <script src="${ctx}/static/js/jquery-3.2.1.min.js"></script>
</head>
<body>
 <div style="width:900px;margin:50px auto;">
     <h2>上传压缩包部署流程</h2>
     <form action="${ctx}/deployment/files" method="post" enctype="multipart/form-data">

         <input type="file" name="file" id="file">
         <input type="submit" value="上传">

     </form>
     <a href="${ctx}/deployment">新建模型</a>
  <h2>流程信息列表：</h2>

  <table border="1" width="90%">
  <tr>
      <th>流程定义ID</th>
      <th>流程部署Id</th>
      <th>流程定义名称</th>
      <th>流程定义KEY</th>
      <th>版本号</th>
      <th>XML资源文件</th>
      <th>图片资源文件</th>
      <th>操作</th>
      
  </tr>
      <c:forEach items="${dataList}" var="item">

          <tr>
              <td>${item.id}</td>
              <td>${item.deploymentId}</td>
              <td>${item.name}</td>
              <td>${item.key}</td>
              <td>${item.version}</td>
              <td><a href="${ctx}/deployment/resources?id=${item.deploymentId}&name=${item.resourceName}">${item.resourceName}</a></td>
              <td><a href="${ctx}/deployment/resources?id=${item.deploymentId}&name=${item.diagramResourceName}">${item.diagramResourceName}</a></td>
              <td><a href="javascript:void(0)"  onclick="deletes(${item.deploymentId})">删除</a></td>
          </tr>

      </c:forEach>

  </table>

     </div>
<script>

    function deletes(id){


        var index = confirm('是否删除');

        if(index){


            $.ajax({

                url:'${ctx}/deployment/'+id,
                type:'DELETE',
                success: function(data){


                    data = JSON.parse(data);
                    if(data.success){

                        document.location.reload();

                    }else{

                        alert('删除失败');

                    }

                }
            });


        }
    }

</script>
</body>
</html>
