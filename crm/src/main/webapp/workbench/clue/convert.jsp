<%@ page import="com.fasterxml.jackson.annotation.ObjectIdGenerators" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
    /*
        jsp九大内置对象
        request
        response
        page
        pageContext
        application
        session
        out
        exception
        config

     */
    String fullname =request.getParameter("fullname");
    String appellation = request.getParameter("appellation");
%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>


<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<script type="text/javascript">
	$(function(){
		$("#isCreateTransaction").click(function(){
			if(this.checked){
				$("#create-transaction2").show(200);
			}else{
				$("#create-transaction2").hide(200);
			}
		});
        $(".time").datetimepicker({
            minView: "month",
            language:  'zh-CN',
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true,
            pickerPosition: "bottom-left"
        });
        //为搜索市场活动的文本框绑定敲键盘事件，如果敲的是回车键，根据关键字搜索并展开市场活动列表
        $("#aname").keydown(function (event) {
            if(event.keyCode==13){
                //搜索并展现市场活动
                $.ajax({
                    url:"workbench/clue/getActivityListByName.do",
                    data:{
                        name:$.trim($("#aname").val())
                    },
                    type:"get",
                    dataType:"json",
                    success:function (data) {
                        /*
                            data
                                List<Activity> aList...
                                [{市场活动1},{2},{3}]
                         */
                        var html = "";
                        $.each(data,function (i,n) {
                            html += '<tr>';
                            html += '<td><input type="radio" value="'+n.id+'" name="xz"/></td>';
                            html += '<td id="'+n.id+'">'+n.name+'</td>';
                            html += '<td>'+n.startDate+'</td>';
                            html += '<td>'+n.endDate+'</td>';
                            html += '<td>'+n.owner+'</td>';
                            html += '</tr>';

                        });
                        $("#activitySearchBody").html(html)
                    }
                });
                //及时终止该方法的默认回车行为
                return false;
            }
        });
        //为提交按钮绑定事件，为市场活动源文本框以及隐藏域文本框填写信息
        $("#submitActivityBtn").click(function () {
            //取得选中的单选框的id值
            var $xz=$("input[name=xz]:checked");
            var id = $xz.val();
            //将id值赋予给市场活动源的隐藏域当中
            $("#activityId").val(id);

            //取得选中的记录的市场活动名称
            var name = $("#"+id).html();

            //将name值赋予给市场活动源的文本框当中
            $("#activityName").val(name);

            //关闭模态窗口
            $("#searchActivityModal").modal("hide");
        });
        //为转换按钮绑定事件，执行线索的转换操作
        $("#convertBtn").click(function () {
            /*
                使用传统请求还是ajax请求？
                由于对于当前转换操作不需要再当前页面中做任何的局部刷新操作
                所有我们发出传统请求即可，当后台处理完线索转换的业务之后跳转到线索列表页
                clue/index.jsp
                传统请求
                在地址栏输入地址，敲回车
                执行a href 超链接
                执行js代码 window.location.href
                提交form表单
                ...
             */
            /*
                我们要执行的是线索转换的操作，但是在执行线索转换的同时，有可能为客户创建一笔临时的交易

                通过复选框"为客户创建交易"
                如果调√了说明需要创建交易
                如果没有调√说明不需要创建交易
             */
            if($("#isCreateTransaction").prop("checked")){
                //window.location.href = "workbench/clue/convert.do?clueId=xxx&money=xxx&expectedDate=xxx"
                //提交表单
                $("#transForm").submit();
            }else{
                window.location.href="workbench/clue/convert.do?clueId=${param.id}";
            }
        })
	});
</script>

</head>
<body>
	
	<!-- 搜索市场活动的模态窗口 -->
	<div class="modal fade" id="searchActivityModal" role="dialog" >
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">搜索市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" id="aname" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
								<td></td>
							</tr>
						</thead>
						<tbody id="activitySearchBody">
							<%--<tr>
								<td><input type="radio" name="activity"/></td>
								<td>发传单</td>
								<td>2020-10-10</td>
								<td>2020-10-20</td>
								<td>zhangsan</td>
							</tr>
							<tr>
								<td><input type="radio" name="activity"/></td>
								<td>发传单</td>
								<td>2020-10-10</td>
								<td>2020-10-20</td>
								<td>zhangsan</td>
							</tr>--%>
						</tbody>
					</table>
				</div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" id="submitActivityBtn">提交</button>
                </div>
			</div>
		</div>
	</div>

	<div id="title" class="page-header" style="position: relative; left: 20px;">
		<h4>转换线索 <small>${param.fullname}${param.appellation}-${param.company}</small></h4>
	</div>
	<div id="create-customer" style="position: relative; left: 40px; height: 35px;">
		新建客户：${param.company}
	</div>
	<div id="create-contact" style="position: relative; left: 40px; height: 35px;">
		新建联系人：${param.fullname}${param.appellation}
	</div>
	<div id="create-transaction1" style="position: relative; left: 40px; height: 35px; top: 25px;">
		<input type="checkbox" id="isCreateTransaction"/>
		为客户创建交易
	</div>
	<div id="create-transaction2" style="position: relative; left: 40px; top: 20px; width: 80%; background-color: #F7F7F7; display: none;" >
	
		<form id="transForm" action="workbench/clue/convert.do" method="post">
			<input type="hidden" name="flag" value="a"/>
			<input type="hidden" name="clueId" value="${param.id}"/>
		  <div class="form-group" style="width: 400px; position: relative; left: 20px;">
		    <label for="amountOfMoney">金额</label>

		    <input type="text" class="form-control" id="amountOfMoney" name="money">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="tradeName">交易名称</label>
		    <input type="text" class="form-control" id="tradeName" name="name">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="expectedClosingDate">预计成交日期</label>
		    <input type="text" class="form-control time" id="expectedClosingDate" name="expectedDate">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="stage">阶段</label>
		    <select id="stage"  class="form-control" name="stage">
		    	<option></option>
		    	<c:forEach items="${stageList}" var="s">
                    <option value="${s.value}">${s.text}</option>
                </c:forEach>
		    </select>
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="activity">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" data-toggle="modal" data-target="#searchActivityModal" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
              <input type="text" class="form-control" id="activityName"  placeholder="点击上面搜索" readonly>
              <input type="hidden" id="activityId" name="activityId"/>
		  </div>
		</form>
		
	</div>
	
	<div id="owner" style="position: relative; left: 40px; height: 35px; top: 50px;">
		记录的所有者：<br>
		<b>${param.owner}</b>
	</div>
	<div id="operation" style="position: relative; left: 40px; height: 35px; top: 100px;">

		<input class="btn btn-primary" type="button" value="转换" id="convertBtn">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input class="btn btn-default" type="button" value="取消">
	</div>
</body>
</html>