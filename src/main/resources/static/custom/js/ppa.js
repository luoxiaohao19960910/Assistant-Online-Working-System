function pushPayAnnouncement(title, content, startDate, expirationDate, url) {
    layui.define(['table', 'form'], function (exports) {
        var $ = layui.$
            , util = layui.util;

        var thisTimer0, setPayCountdown = function (startDate, expirationDate) {
            clearTimeout(thisTimer0);
            util.countdown(expirationDate, startDate, function (date, startDate, timer) {
                var str = date[0] + '天' + date[1] + '时' + date[2] + '分' + date[3] + '秒';
                $('#pay-countdown').html(str);
                thisTimer0 = timer;
            });
        };

        var index = layer.confirm(content + '<br>请在“<b id="pay-countdown" style="color:red"></b>”内完成支付，否则会影响正常功能的使用。', {
            btn: ['我已完成支付，之后不再弹出', '稍后再说'] //按钮
            , title: title
            , closeBtn: 0
        }, function () {
            //执行 Ajax 后重载
            if (url != null) {
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
            }
            layer.closeAll('dialog');
        }, function () {
        });
        layer.full(index);
        setPayCountdown(startDate, expirationDate);
    });
}