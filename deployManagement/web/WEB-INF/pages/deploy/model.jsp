<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../base/common.jsp"%>
<html>
<head>
    <title>流程部署</title>
    <script src="${ctx}/static/js/jquery-3.2.1.min.js"></script>
    <link rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css">
    <script src="${ctx}/plugins/layer/layer.js"></script>


</head>
<body>
 <div style="width:1200px;margin:50px auto;">
     <button class="btn btn-primary" data-toggle="modal" data-target="#myModal">新建模型</button>
  <h2>流程信息列表：</h2>
     <div class="table-responsive">
  <table class="table">
  <tr>
      <th>模型ID</th>
      <th>模型版本</th>
      <th>模型名称</th>
      <th>模型KEY</th>
      <th>创建时间</th>
      <th>xml资源文件</th>
      <th>图片资源文件</th>
      <th>操作</th>
  </tr>
      <c:forEach items="${dataList}" var="item">

          <tr>
              <td>${item.id}</td>
              <td>${item.version}</td>
              <td>${item.name}</td>
              <td>${item.key}</td>
              <td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd"></fmt:formatDate></td>
              <td><a href="${ctx}/deployment/model/resources?id=${item.id}&vid=${item.editorSourceValueId}">xml资源文件</a></td>
              <td><a href="${ctx}/deployment/model/resources?id=${item.id}&evid=${item.editorSourceExtraValueId}">png图片</a></td>
              <td>
                  <a href="javascript:void(0)"  onclick="deletes('${item.id}')">删除</a>

                  <a href="javascript:void(0)"  onclick="deploy('${item.id}')">部署</a>

                  <a href="${ctx}/plugins/procerss-editor/modeler.html?modelId=${item.id}">编辑</a>
              </td>
          </tr>
      </c:forEach>

  </table>
   </div>
     </div>
 <!-- Modal -->
 <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
     <div class="modal-dialog" role="document">
         <form action="${ctx}/deployment/model/management" method="post" name="form">
         <div class="modal-content">
             <div class="modal-header">
                 <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                 <h4 class="modal-title" id="myModalLabel">新建模型</h4>
             </div>
             <div class="modal-body">

                     <div class="form-group">
                         <label for="name">模型名称</label>
                         <input type="text" class="form-control"  name="name" id="name" placeholder="模型名称" value="helloTest">
                     </div>
                     <div class="form-group">
                         <label for="key">key</label>
                         <input type="text" class="form-control" id="key" name="key" placeholder="key" value="111111111">
                     </div>
                     <div class="form-group">
                         <label for="desc">描述</label>
                         <input type="text" class="form-control"  name="description" id="desc" placeholder="描述" value="模型的描述">
                     </div>

             </div>
             <div class="modal-footer">
                 <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                 <button type="submit" class="btn btn-primary">提交</button>
             </div>
         </div>
         </form>
     </div>
 </div>

 <script src="${ctx}/static/js/bootstrap.min.js"></script>
<script>

    function deploy(id){


        var index = confirm('是否部署该model');

        if(index){

            $.ajax({

                url:'${ctx}/deployment/model/deployment/'+id,
                type:'PUT',
                success: function(data){


                    data = JSON.parse(data);
                    if(data.success){

                        document.location.reload();

                    }else{

                        alert('部署失败');

                    }

                }
            });


        }

    }

    function deletes(id){


        var index = confirm('是否删除');

        if(index){


            $.ajax({

                url:'${ctx}/deployment/model/'+id,
                type:'DELETE',
                success: function(data){


                    data = JSON.parse(data);
                    if(data.success){


                        document.location.reload();


                    }else{

                       layer.msg("部署失败");

                    }

                }
            });


        }
    }

</script>
</body>
</html>
