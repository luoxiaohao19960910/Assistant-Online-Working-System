function pushPayAnnouncement(title, content, url) {
    layui.define(['table', 'form'], function (exports) {
        var $ = layui.$;
        var index = layer.confirm(content, {
            btn: ['我已完成支付，之后不再弹出', '稍后再说'] //按钮
            , title: title
            , closeBtn:0
        }, function () {
            //执行 Ajax 后重载
            $.ajax({
                type: 'post',
                data: {},
                url: url,
                beforeSend: function (data) {
                    layer.load(1, {shade: [0.1, '#fff']}); //上传loading
                }
                , success: function (data) {
                    layer.closeAll('loading'); //关闭loading
                    if (data.data === "success") {
                        //登入成功的提示与跳转
                        layer.msg('爱你！', {
                            icon: 1
                        });
                    } else {
                        layer.msg('未知错误');
                    }
                }

            });
            layer.closeAll('dialog');
        }, function () {
        });
        layer.full(index);
    });
}