<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../base/common.jsp" %>
<html>
<head>
    <title>用户管理</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <script src="${ctx}/static/js/jquery-3.2.1.min.js"></script>
    <link rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/static/css/bootstrap-table.css">
    <link rel="stylesheet" href="${ctx}/static/css/bootstrapValidator.min.css">
    <script src="${ctx}/static/js/bootstrap.min.js"></script>
    <script src="${ctx}/static/js/bootstrap-table.js"></script>
    <script src="${ctx}/static/js/bootstrap-table-locale-all.js"></script>
    <script src="${ctx}/static/js/bootstrapValidator.min.js"></script>

    <script src="${ctx}/static/layer/layer.js"></script>

</head>
<body>
<div class="container">
    <div class="row">
        <h2>用户列表</h2>
        <div id="toolbar" class="btn-group">
            <button id="btn_add" type="button" class="btn btn-default">
                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增
            </button>
            <button id="btn_edit" type="button" class="btn btn-default"
                    data-whatever="修改" data-opera="update" id="update">
                <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
            </button>
            <button id="btn_delete" type="button" class="btn btn-default">
                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除
            </button>
        </div>
        <div class=".col-sm-12 .col-lg-10">
            <table  id="table">
                <thead>
                <tr>
                    <th data-checkbox="true"></th>
                    <th data-field="id">编号</th>
                    <th data-field="firstName">名称</th>
                    <th data-field="email">邮箱</th>
                </tr>
                </thead>
            </table>
        </div>

    </div>
</div>
<!--模态框-->
<div class="modal fade" id="modal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
         </div>
    </div>
</div>
<script>

    var $table = $('#table');
    $(function () {

        $("#btn_add").click(function () {

            $('#modal').modal({

                remote: '${ctx}/accounts/users/add'

            });
        });

        $('#btn_delete').click(function () {

            if ($table.bootstrapTable('getSelections').length == 0) {

                layer.msg('请选择选项', {icon: 5});
                return;
            }

            var check = $table.bootstrapTable('getSelections');
            var id = [];
            $.each(check, function (index, item) {

                id.push(item.id);

            });

            //询问框
            layer.confirm('确定删除？', {
                btn: ['删除', '取消'] //按钮
            }, function () {

                var index = layer.msg('删除中', {
                    icon: 16
                    ,shade: 0.01
                });
                $.ajax({
                    url: '${ctx}/accounts/users',
                    type: 'DELETE',
                    dataType: 'json',
                    data: JSON.stringify(id),
                    success: function (data) {

                        if (data.success) {

                            layer.close(index);
                            layer.msg(data.msg);
                            setTimeout(function () {

                                document.location.reload();

                            }, 1200);

                        } else {

                            alert(layer.msg);

                        }

                    }
                });

            }, function () {

                layer.msg('的确很重要', {icon: 1});
            });

        });

        $('#btn_edit').click(function () {


            if ($table.bootstrapTable('getSelections').length == 0) {

                layer.msg('请选择选项', {icon: 5});
                return;
            }
            if ($table.bootstrapTable('getSelections').length > 1) {

                layer.msg('不支持多项编辑', {icon: 5});
                return;
            }

            var value = $table.bootstrapTable('getSelections');

            $('#modal').modal({

                remote: '${ctx}/accounts/users/' + value[0].id

            });


        });

        $("#modal").on("hidden.bs.modal", function () {
            $(this).removeData("bs.modal");
        });
        $table.bootstrapTable({
            url: '${ctx}/accounts/users',
            method: 'GET',   //请求类型
            dataType: "json",   //返回的数据类型
            cache: false, // 不缓存
            height: 600, // 设置高度，会启用固定表头的特性
            striped: true, // 隔行加亮
            toolbar: '#toolbar',   //工具栏
            //  pagination: true,                   //是否显示分页（*）
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            // sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
            // pageNumber: 1,                       //初始化加载第一页，默认第一页
            // pageSize: 50,                       //每页的记录行数（*）
            //  pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
            responseHandler: function (data) {    //在返回数据之后调用

                return data;
            }
        });

    });
</script>
</body>
</html>
