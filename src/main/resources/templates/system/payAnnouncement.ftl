<#assign ctx=request.contextPath/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>公告管理</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="icon" href="${ctx}/custom/img/favicon/favicon.ico"/>
    <link rel="stylesheet" href="${ctx}/plugins/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${ctx}/plugins/layuiadmin/style/admin.css" media="all">
</head>
<body>

<div class="layui-fluid">
    <div class="layui-row layui-col-space15">
        <div class="layui-col-md12">
            <div class="layui-card">
                <div class="layui-card-header">收费公告管理</div>
                <div class="layui-card-body" pad15>
                    <div class="layui-form" lay-filter="">
                        <div class="layui-form-item">
                            <label class="layui-form-label">标题</label>
                            <div class="layui-input-block">
                                <input type="text" name="title" id="title" value="${payAnnouncementEdit.title!""}"
                                       placeholder="请输入" class="layui-input" lay-verType="tips" lay-verify="required">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">内容</label>
                            <div class="layui-input-block">
                                <textarea name="content" id="content" style="height: 400px;"
                                          class="layui-textarea"
                                          lay-verType="tips" lay-verify="required"
                                          placeholder="请输入">${payAnnouncementEdit.content!""}</textarea>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">倒计时值</label>
                            <div class="layui-input-inline">
                                <input type="text" name="expireTimeValue" id="expireTimeValue"
                                       value="${payAnnouncementEdit.expireTimeValue!""}" placeholder=""
                                       autocomplete="off" class="layui-input" lay-verType="tips" lay-verify="number">
                            </div>
                            <label class="layui-form-label">倒计时单位</label>
                            <div class="layui-input-inline">
                                <select name="expireTimeUnit" id="expireTimeUnit" lay-verType="tips"
                                        lay-verify="required">
                                    <option value="">请选择单位</option>
                                    <option value="0">秒</option>
                                    <option value="1">分钟</option>
                                    <option value="2">小时</option>
                                    <option value="3">天</option>
                                </select>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">预览</label>
                            <div class="layui-input-block">
                                <button class="layui-btn" lay-filter="preview"
                                        id="preview">预览
                                </button>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">操作</label>
                            <div class="layui-input-block">
                                <button class="layui-btn" lay-submit lay-filter="set"
                                        style="background-color: #1E9FFF"
                                        id="set">推送新的支付
                                </button>
                                <button class="layui-btn" lay-submit lay-filter="clear"
                                        style="background-color: #01AAED" id="clear">清除已有推送
                                </button>
                                <button class="layui-btn"
                                        style="background-color: #FFB800" id="queryNotPaid">查询所有需要付费用户
                                </button>
                            </div>
                        </div>
                        <div class="layui-form-item" id="div-needToPay" hidden="hidden">
                            <label class="layui-form-label">筛选</label>
                            <div class="layui-input-inline">
                                <select name="needToPay" id="needToPay" lay-filter="needToPay">
                                    <option value="">请选择付款状态</option>
                                    <option value="0">未付</option>
                                    <option value="1">已付</option>
                                </select>
                            </div>
                        </div>
                        <table id="notPaidTable"></table>
                        <script type="text/html" id="buttonTpl">
                            {{#  if(!d.needToPay){ }}
                            <button class="layui-btn layui-btn-xs">已付</button>
                            {{#  } else { }}
                            <button class="layui-btn layui-btn-primary layui-btn-xs">未付</button>
                            {{#  } }}
                        </script>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${ctx}/custom/js/external/jquery-3.3.1.min.js"></script>
<script src="${ctx}/custom/js/myButton.js"></script>
<script src="${ctx}/custom/js/user.js"></script>

<script src="${ctx}/plugins/layuiadmin/layui/layui.js"></script>
<script src="${ctx}/custom/js/myLayVerify.js"></script>
<script src="${ctx}/custom/js/ppa.js"></script>
<script>
    layui.config({
        base: '${ctx}/plugins/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'set', 'element', 'code', 'laydate'], function () {
        var $ = layui.$
                , setter = layui.setter
                , admin = layui.admin
                , form = layui.form
                , element = layui.element
                , router = layui.router()
                , upload = layui.upload
                , table = layui.table
                , laydate = layui.laydate;

        $("#expireTimeUnit").val('${payAnnouncementEdit.expireTimeUnit!""}');
        form.render('select');


        $("#preview").click(function () {
            var index = layer.confirm($("#content").val(), {
                btn: ['我已完成支付，之后不再弹出', '稍后再说'] //按钮
                , title: $("#title").val()
            }, function () {
                layer.closeAll('dialog');
            }, function () {
            });
            layer.full(index);
        });


        form.on('submit(clear)', function (obj) {
            var field = obj.field;

            //执行 Ajax 后重载
            $.ajax({
                type: 'post',
                data: {},
                url: "${ctx}/system/deletePayAnnouncement",
                beforeSend: function (data) {
                    layer.load(1, {shade: [0.1, '#fff']}); //上传loading
                }
                , success: function (data) {
                    layer.closeAll('loading'); //关闭loading
                    if (data.data === "success") {
                        //登入成功的提示与跳转
                        return layer.msg('清除成功', {
                            offset: '15px'
                            , icon: 1
                            , time: 1000
                        });
                    } else {
                        layer.msg('未知错误');
                    }
                }

            });
        });


        //提交
        form.on('submit(set)', function (obj) {
            var field = obj.field;

            //执行 Ajax 后重载
            $.ajax({
                type: 'post',
                data: {
                    title: field.title
                    , content: field.content
                    , expireTimeValue: field.expireTimeValue
                    , expireTimeUnit: field.expireTimeUnit
                },
                url: "${ctx}/system/pushPayAnnouncement",
                beforeSend: function (data) {
                    layer.load(1, {shade: [0.1, '#fff']}); //上传loading
                }
                , success: function (data) {
                    layer.closeAll('loading'); //关闭loading
                    if (data.data === "success") {
                        //登入成功的提示与跳转
                        return layer.msg('推送成功', {
                            offset: '15px'
                            , icon: 1
                            , time: 1000
                        });
                    } else {
                        layer.msg('未知错误');
                    }
                }

            });
        });


        $("#queryNotPaid").click(function () {
            $("#div-needToPay").show();

            //方法级渲染
            table.render({
                elem: '#notPaidTable'
                , url: '${ctx}/system/getNeedToPayUsers' //向后端默认传page和limit
                , cols: [[
                    {field: 'id', title: 'id', sort: true}
                    , {field: 'userWorkId', title: '工号', width: 300, sort: true}
                    , {field: 'userIdCard', title: '身份证', width: 180, sort: true}
                    , {field: 'userName', title: '用户名', sort: true}
                    , {field: 'userRealName', title: '真实姓名', width: 120, sort: true}
                    , {field: 'userRole', title: '角色', width: 80, sort: true}
                    , {field: 'userEmail', title: '邮箱'}
                    , {field: 'userPhone', title: '联系方式', width: 120}
                    , {field: 'status', title: '付款状态', templet: '#buttonTpl', width: 110, align: 'center'}
                ]]
                , page: true
                , limit: 5
                , limits: [5, 10, 15, 20, 50]
                , request: {
                    pageName: 'pageNum',
                    limitName: 'pageSize'  //如不配置，默认为page=1&limit=10
                }
                , done: function (res, curr, count) {
                    //如果是异步请求数据方式，res即为你接口返回的信息。
                    //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                    console.log(res);

                    //得到当前页码
                    //console.log(curr);

                    //得到数据总量
                    //console.log(count);
                }

            });
        });


        //联动监听select
        form.on('select(needToPay)', function (data) {
            var needToPay = $(this).attr("lay-value");

            //执行重载
            table.reload('notPaidTable', {
                url: '${ctx}/system/getNeedToPayUsers' //向后端默认传page和limit
                , where: { //设定异步数据接口的额外参数，任意设
                    needToPay: needToPay
                }
                , request: {
                    pageName: 'pageNum',
                    limitName: 'pageSize'  //如不配置，默认为page=1&limit=10
                }
                , page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
        });

    });
</script>
</body>
</html>

