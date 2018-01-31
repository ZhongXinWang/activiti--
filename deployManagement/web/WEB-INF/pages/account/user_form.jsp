
<%--
  Created by 王钟鑫
  User: USER
  Date: 2018/1/30
  Time: 17:20
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../base/common.jsp" %>
<html>
<head>
    <title>Title</title>
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
<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
    <h4 class="modal-title">
        <c:if test='${operator == "add"}'>新增用户</c:if>
        <c:if test='${operator != "add"}'>编辑用户</c:if>
    </h4>
</div>
<form id="form1">
    <div class="modal-body">

        <div class="form-group">
            <label for="name" class="control-label">用户名：</label>
            <input type="text" class="form-control"  name="name" value="${user.name}" id="name" <c:if test='${operator != "add"}'>readonly</c:if>>
        </div>
        <div class="form-group">

            <label for="email" class="control-label">邮箱：</label>
            <input type="text" class="form-control"  name="email"  value="${user.email}" id="email">
        </div>
        <div class="form-group">

            <label for="groupId" class="control-label">组别：</label>
            <select  class="form-control"  name="groupId"  id="groupId">
                <c:forEach var="item" items="${dataList}">

                    <option value="${item.id}" name="id" <c:if test="${user.groupId == item.id}">selected</c:if>>${item.name}</option>

                </c:forEach>
            </select>
        </div>

    </div>
    <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="submit" class="btn btn-primary" >提交</button>
    </div>
</form>
</div>
<script>
    $(function () {

        var operator1 = "add";
        var action = "${ctx}/accounts/users";
        var method = "POST";
        //update操作
        if('${operator}' != 'add'){

          operator1 = "update";
        }

        //验证，提交
        $('#form1')
                .bootstrapValidator({
                    message: '值不许为空',
                    feedbackIcons: {
                        valid: 'glyphicon glyphicon-ok',
                        invalid: 'glyphicon glyphicon-remove',
                        validating: 'glyphicon glyphicon-refresh'
                    },
                    fields: {
                        name: {
                            message: 'The username is not valid',
                            validators: {
                                notEmpty: {
                                    message: '不许为空'
                                },
                                stringLength: {
                                    min: 2,
                                    max: 30,
                                    message: 'The username must be more than 6 and less than 30 characters long'
                                }
//                                regexp: {
//                                    regexp: /^[a-zA-Z0-9_\.]+$/,
//                                    message: 'The username can only consist of alphabetical, number, dot and underscore'
//                                }
                            }
                        },
                        email: {
                            validators: {
                                notEmpty: {
                                    message: '邮箱不许为空'
                                },
                                emailAddress: {
                                    message: '邮箱地址错误'
                                }
                            }
                        }

                    }
                })
                .on('success.form.bv', function (e) {
                    // Prevent form submission
                    e.preventDefault();

                    // Get the form instance
                    var $form = $(e.target);
                    console.log($form.serialize());
                    // Get the BootstrapValidator instance
                    var bv = $form.data('bootstrapValidator');

                    if (operator1 == "update") {

                        action = "${ctx}/accounts/users/${id}";

                    }
                    var index = layer.msg('加载中', {
                        icon: 16
                        , shade: 0.01
                    });

                    $.ajax({

                        url: action,
                        type: method,
                        dataType: 'json',
                        data: $form.serialize(),
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
                });
    });

</script>

</body>
</html>
