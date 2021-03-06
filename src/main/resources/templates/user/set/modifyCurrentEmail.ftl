<#assign ctx=request.contextPath/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>基本资料</title>
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
                <div class="layui-card-header">修改安全邮箱</div>
                <div class="layui-card-body" pad15>
                    <script type="text/html" template>
                        {{# if(layui.router().search.type === 'resetEmail'){ }}
                        <div class="layui-form" lay-filter="">
                            <input type="text" name="oldEmail" id="oldEmail" value="${userInfo.userEmail!""}"
                                   readonly style="display: none"
                                   class="layui-input" autocomplete="off" lay-verify="email" lay-verType="tips">
                            <div class="layui-form-item">
                                <label class="layui-form-label">新邮箱</label>
                                <div class="layui-input-inline">
                                    <input type="text" name="newEmail" id="newEmail"
                                           lay-verify="email" lay-verType="tips" autocomplete="off" placeholder="请输入新邮箱"
                                           class="layui-input">
                                </div>
                                <div class="layui-form-mid layui-word-aux">点击获取验证码发送至此新邮箱，输入正确的验证码即可完成修改！</div>
                            </div>
                            <div class="layui-form-item">
                                <label class="layui-form-label">邮箱验证码</label>
                                <div class="layui-input-inline">
                                    <div class="layui-row">
                                        <input type="text" name="newEmailcode" lay-verify="required" lay-verType="tips"
                                               placeholder="请输入邮箱验证码"
                                               id="LAY-user-login-smscode" class="layui-input">
                                    </div>
                                </div>
                                <div class="layui-input-inline">
                                    <div style="margin-left: 10px">
                                        <button type="button" class="layui-btn layui-btn-primary layui-btn-fluid"
                                                id="send-email-code-new" name="send-email-code-new" style="width: auto">
                                            获取验证码
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <div class="layui-input-block">
                                    <button class="layui-btn" lay-submit lay-filter="setmyEmail" id="my_button">确认修改
                                    </button>
                                </div>
                            </div>
                        </div>
                        {{# } else { }}
                        <div class="layui-form" lay-filter="">
                            <div class="layui-form-item">
                                <label class="layui-form-label">当前邮箱</label>
                                <div class="layui-input-inline">
                                    <input type="text" name="oldEmail" id="oldEmail" value="${userInfo.userEmail!""}"
                                           readonly
                                           class="layui-input" autocomplete="off" lay-verify="email" lay-verType="tips">
                                </div>
                                <div class="layui-form-mid layui-word-aux">此项为已绑定邮箱，完成该邮箱的验证后才能进行下一步——更改新的绑定邮箱</div>
                            </div>
                            <div class="layui-form-item">
                                <label class="layui-form-label">邮箱验证码</label>
                                <div class="layui-input-inline">
                                    <div class="layui-row">
                                        <input type="text" name="emailcode" lay-verify="required" lay-verType="tips"
                                               placeholder="请输入邮箱验证码"
                                               id="LAY-user-login-smscode" class="layui-input">
                                    </div>
                                </div>
                                <div class="layui-input-inline">
                                    <div style="margin-left: 10px">
                                        <button type="button" class="layui-btn layui-btn-primary layui-btn-fluid"
                                                id="send-email-code" name="send-email-code" style="width: auto">获取验证码
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <div class="layui-input-block">
                                    <button class="layui-btn" lay-submit lay-filter="next" id="next">下一步</button>
                                </div>
                            </div>
                        </div>

                        {{# } }}


                    </script>
                </div>

            </div>
        </div>
    </div>
</div>

<script src="${ctx}/custom/js/external/jquery-3.3.1.min.js"></script>
<script src="${ctx}/custom/js/myButton.js"></script>

<script src="${ctx}/plugins/layuiadmin/layui/layui.js"></script>
<script src="${ctx}/custom/js/myLayVerify.js"></script>
<script>
    layui.config({
        base: '${ctx}/plugins/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'set', 'element'], function () {
        var $ = layui.$
                , setter = layui.setter
                , admin = layui.admin
                , form = layui.form
                , element = layui.element
                , router = layui.router();

        var oldEmail = $("#oldEmail").val();
        /*************************************************************/
        $('#send-email-code').click(function () {
            //校验邮箱

            // 设置button效果，开始计时
            disabledSubmitButtonWithTime('send-email-code', '获取验证码', 60);

            layer.msg('我们向你当前绑定邮箱' + oldEmail + '发送了验证码，请注意查收', {
                icon: 1
                , shade: 0
            });

            //请求发送验证码
            $.ajax({
                type: "post",
                url: "${ctx}/sendVerifyCodeToEmail",
                data: {"userEmail": oldEmail},
                success: function (obj) {
                },
                dataType: "json"
            });
        });

        $('#send-email-code-new').click(function () {
            //校验邮箱
            var newEmail = $("#newEmail").val();
            console.log(newEmail);
            console.log(oldEmail);
            console.log(newEmail===oldEmail);

            if (!newEmail.match(/^[a-zA-Z0-9_-]+@([a-zA-Z0-9]+\.)+(com|cn|net|org)$/)) {
                return layer.msg('请输入正确的邮箱格式');
            }

            if (newEmail === oldEmail) {
                return layer.msg('您未对绑定邮箱做任何修改！');
            }

            // 设置button效果，开始计时
            disabledSubmitButtonWithTime('send-email-code-new', '获取验证码', 60);

            layer.msg('验证码已发送至你的新邮箱，请注意查收', {
                icon: 1
                , shade: 0
            });

            //请求发送验证码
            $.ajax({
                type: "post",
                url: "${ctx}/sendVerifyCodeToEmail",
                data: {"userEmail": newEmail},
                success: function (obj) {
                },
                dataType: "json"
            });
        });
        /*************************************************************/


        form.render();

        //下一步
        form.on('submit(next)', function (obj) {
            var field = obj.field;

            //禁用5秒
            disabledSubmitButtonWithTime('my_button', '下一步', 5);

            //请求接口
            $.ajax({
                type: 'post'
                , url: '${ctx}/emailVerifyCodeTest' //实际使用请改成服务端真实接口
                , data: {"needExistTest": true, "emailVerifyCode": field.emailcode, "userEmail": field.oldEmail}
                , success: function (res) {
                    if (res.data === "verifyCodeCorrect") {
                        location.hash = '/type=resetEmail';
                        location.reload();
                    } else if (res.data === "emailUnregistered") {
                        layer.msg('该邮箱尚未注册', {
                            icon: 5,
                            anim: 6
                        });
                    } else if (res.data === "verifyCodeWrong") {
                        layer.msg('验证码错误', {
                            icon: 5,
                            anim: 6
                        });
                    } else {
                        layer.msg('未知错误', {
                            icon: 5,
                            anim: 6
                        });
                    }
                }
            });

            return false;

        });

        //提交
        form.on('submit(setmyEmail)', function (obj) {
            var field = obj.field;

            if (field.oldEmail === field.newEmail) {
                return layer.msg('您未对绑定邮箱做任何修改！');
            }

            //禁用5秒
            disabledSubmitButtonWithTime('my_button', '确认修改', 5);

            //请求接口
            $.ajax({
                type: 'post'
                , url: '${ctx}/emailVerifyCodeTest' //实际使用请改成服务端真实接口
                , data: {"emailVerifyCode": field.newEmailcode, "userEmail": field.newEmail}
                , success: function (res) {
                    if (res.data === "verifyCodeCorrect") {
                        /*****************************************************/
                        //验证码正确后修改绑定邮箱ajax
                        $.ajax({
                            url: '${ctx}/user/modifyCurrentEmail?csrfToken=' + '${csrfToken!""}' //实际使用请改成服务端真实接口
                            , type: 'post'
                            ,
                            data: {
                                "oldEmail": field.oldEmail,
                                "newEmail": field.newEmail
                            }
                            ,
                            success: function (res) {
                                if (res.data === "newEmailExist") {
                                    return layer.msg('此新邮箱已被注册', {
                                        icon: 5,
                                        anim: 6
                                    });
                                } else if (res.data === "success") {
                                    layer.msg('修改已完成，请F5刷新页面', {
                                        icon: 1
                                        , time: 1000
                                    }, function () {
                                        location.href = '${ctx}/user/setInfo';
                                    });
                                } else {
                                    return layer.msg('未知错误', {
                                        icon: 5,
                                        anim: 6
                                    });
                                }
                            }
                        });
                        /*****************************************************/
                    } else if (res.data === "emailUnregistered") {
                        layer.msg('该邮箱尚未注册', {
                            icon: 5,
                            anim: 6
                        });
                    } else if (res.data === "verifyCodeWrong") {
                        layer.msg('验证码错误', {
                            icon: 5,
                            anim: 6
                        });
                    } else {
                        layer.msg('未知错误', {
                            icon: 5,
                            anim: 6
                        });
                    }
                }
            });


            return false;
        });


    });
</script>
</body>
</html>

