<#assign ctx=request.contextPath/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>模板导入</title>
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
                <div class="layui-card-header">模板导入</div>
                <div class="layui-card-body">
                    <ul class="layui-timeline">
                        <li class="layui-timeline-item layui-form">
                            <i class="layui-icon layui-timeline-axis"></i>
                            <div class="layui-timeline-content layui-text">
                                <h3 class="layui-timeline-title">导入座位表</h3>
                                <p>上传excel要求说明：<a
                                        href="${ctx}/toolbox/assistantAdministrator/downloadExample/5">查看范例</a></p>
                                <ul>
                                    <li>每张sheet的名字必须以教室门牌号（<b style="color:red">纯数字、或纯数字+一个字母</b>）命名，如'301'、'301A'...
                                    </li>
                                    <li>每张sheet中的单元格对应当前教室的座位，单元格的值必须是数字，且第一个座位从1开始，以等差为1增加（1、2、3、4、...)。</li>
                                    <li>每张sheet中<b style="color:red">除座位单元格外不要出现纯数字的单元格</b>，如讲台区域可以命名成"310讲台"，但不能命名成"310"!
                                    </li>
                                    <li>该表的sheet应该涵盖当前校区所有可用的教室，系统将读取这些教室以及对应教室的座位容量到数据库！</li>
                                </ul>
                            </div>
                            <div class="layui-form layui-timeline-content" style="margin-bottom: 20px;">
                                <input type="checkbox" name="read_step1" id="read_step1" lay-skin="primary"
                                       lay-filter="read_step1" title=""><b style="color: red;">我已认真阅读以上内容</b>
                            </div>
                            <div class="layui-form layui-form-item layui-timeline-content" style="margin-bottom: 20px;"
                                 id="div-campus" hidden="hidden">
                                <label class="layui-form-label">校区</label>
                                <div class="layui-input-inline">
                                    <select name="campus" id="campus" lay-verify="required" lay-search>
                                        <option value="">请选择校区</option>
                                    </select>
                                </div>
                                <div class="layui-form-mid " style="color:red">*必填项</div>
                                <button class="layui-btn layuiadmin-btn-comm" data-type="batchdel"
                                        id="download-current-seat-table-template" lay-submit
                                        lay-filter="download-current-seat-table-template">查看该校区当前使用的模板
                                </button>
                            </div>
                            <div class="layui-timeline-content layui-text" id="div-import-seat-table"
                                 hidden="hidden">
                                <button class="layui-btn layuiadmin-btn-comm" data-type="batchdel"
                                        style="background-color: #FFB800"
                                        id="import-seat-table" lay-filter="import-seat-table"><i
                                        class="layui-icon">&#xe67c;</i>导入座位表模板
                            </div>
                        </li>
                        <li class="layui-timeline-item lay-form">
                            <i class="layui-icon layui-timeline-axis"></i>
                            <div class="layui-timeline-content layui-text">
                                <h3 class="layui-timeline-title">导入教师和助教工作表模板</h3>
                                <p>上传excel要求说明：<a
                                        href="${ctx}/toolbox/assistantAdministrator/downloadExample/8">查看范例</a></p>
                                <p>该模板用于开班电话表制作。系统根据模板制作开班电话表的规则是：寻找指定名称的sheet-->寻找对应sheet下指定名称的列-->填充相应数据。
                                </p>
                                <ul>
                                    <li>系统可以利用模板表中的如下名称的三张sheet。<br>
                                        1、签到表。该sheet中前两行都是合并单元格，分别用于填充班号、助教等信息。从第三行开始，列名中可以解析的是：学员编号、学员姓名、家长联系方式。<br>
                                        2、开班电话表。该sheet中从第一行开始，列名中可以解析的是：校区、班号、教师姓名、助教、学员编号、学员姓名、家长联系方式、类别※、任课教师的要求、学校、所有在读学科※。<br>
                                        3、信息回访表。该sheet中从第一行开始，列名中可以解析的是：校区、班号、教师姓名、助教、学员编号、学员姓名、类别※。
                                    </li>
                                    <li>这些要求的sheet及其列是系统能解析的，这意味着你上传的模板表中<b style="color:red">不是必须全要包含这些sheet或列</b>。比如：<br>
                                        1、在你要上传的模板中，只有“签到表”和“开班电话表”这两张sheet，没有“信息回访表”。这是合法的，这样做开班电话表时系统将跳过对“信息回访表”的填充，不会报错没有影响。<br>
                                        2、在“开班电话表”sheet中，你要上传的模板中没有“所有在读学科※”列。这是合法的，因为做开班电话表时只填充sheet中“存在的且能解析的”列，不存在的不会报错没有影响。
                                    </li>
                                    <li><b style="color:red">最后一步也是至关重要的一步</b>：你需要为这三张sheet复制下拉一些空白行（带上相同的格式），只要行数能超过班级人数就行（我一般给100行左右），类似范例中那样。
                                        这样做的目的是将来输出的开班表格能有统一的完美格式。
                                    </li>
                                </ul>

                            </div>
                            <div class="layui-form layui-timeline-content" style="margin-bottom: 20px;">
                                <input type="checkbox" name="read_step2" id="read_step2" lay-skin="primary"
                                       lay-filter="read_step2" title=""><b style="color: red;">我已认真阅读以上内容</b>
                            </div>
                            <div class="layui-form layui-form-item layui-timeline-content" style="margin-bottom: 20px;"
                                 id="div-campus2" hidden="hidden">
                                <label class="layui-form-label">校区</label>
                                <div class="layui-input-inline">
                                    <select name="campus2" id="campus2" lay-verify="required" lay-search>
                                        <option value="">请选择校区</option>
                                    </select>
                                </div>
                                <div class="layui-form-mid " style="color:red">*必填项</div>
                                <button class="layui-btn layuiadmin-btn-comm" data-type="batchdel"
                                        id="download-current-assistant-tutorial-template" lay-submit
                                        lay-filter="download-current-assistant-tutorial-template">查看该校区当前使用的模板
                                </button>
                            </div>
                            <div class="layui-timeline-content layui-text" id="div-import-assistant-tutorial"
                                 hidden="hidden">
                                <button class="layui-btn layuiadmin-btn-comm" data-type="batchdel"
                                        style="background-color: #FFB800"
                                        id="import-assistant-tutorial" lay-filter="import-assistant-tutorial"><i
                                        class="layui-icon">&#xe67c;</i>导入教师和助教工作表模板
                            </div>
                        </li>
                        <li class="layui-timeline-item">
                            <i class="layui-icon layui-timeline-axis"></i>
                            <div class="layui-timeline-content layui-text">
                                <div class="layui-timeline-title">大功告成！</div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${ctx}/custom/js/external/jquery-3.3.1.min.js"></script>

<script src="${ctx}/plugins/layuiadmin/layui/layui.js"></script>
<script src="${ctx}/custom/js/myLayVerify.js"></script>
<script>
    layui.config({
        base: '${ctx}/plugins/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'set', 'element', 'laydate'], function () {
        var $ = layui.$
                , setter = layui.setter
                , admin = layui.admin
                , form = layui.form
                , element = layui.element
                , router = layui.router()
                , upload = layui.upload
                , laydate = layui.laydate;

        /*====================导入座位表=======================*/

        var campusNames = eval('(' + '${campusNames}' + ')');
        for (var i = 0; i < campusNames.length; i++) {
            var json = campusNames[i];
            var str = "";
            str += '<option value="' + json + '">' + json + '</option>';
            $("#campus").append(str);
        }
        form.render('select');

        //监听自动解析开关
        form.on('checkbox(read_step1)', function (data) {
            //开关是否开启，true或者false
            var checked = data.elem.checked;
            if (checked) {
                $("#div-campus").show();
                $("#div-import-seat-table").show();
            } else {
                $("#div-campus").hide();
                $("#div-import-seat-table").hide();
            }
        });

        form.on('submit(download-current-seat-table-template)', function (obj) {
            var field = obj.field;
            location.href = '${ctx}/toolbox/assistantAdministrator/downloadTemplate/2?campus=' + field.campus;
        });

        upload.render({
            elem: '#import-seat-table'
            , url: '${ctx}/toolbox/assistantAdministrator/seatTableTemplateImport'
            , data: {
                classCampus: function () {
                    return $("#campus").val();
                }
            }
            , accept: 'file' //普通文件
            , exts: 'xls|xlsx' //允许上传的文件后缀
            , before: function (obj) { //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
                layer.load(1, {shade: [0.1, '#fff']}); //上传loading
            }
            , done: function (res) {//返回值接收
                layer.closeAll('loading'); //关闭loading
                if (res.msg === "success") {
                    return layer.msg('导入成功', {
                        icon: 1
                        , time: 1000
                    });
                } else if (res.msg === "campusInvalid") {
                    return layer.msg('请选择正确的校区!', {
                        offset: '15px'
                        , icon: 2
                        , time: 2000
                    });
                } else if (res.msg === "sheetNameError") {
                    return layer.alert('sheet名应该是教室!即，纯数字或者纯数字加一个字母。如312；312A；5201；5201A...', {
                        skin: 'layui-layer-lan'
                        , closeBtn: 0
                    });
                } else if (res.msg === "tooManyRows") {
                    return layer.alert('输入表格的行数过多。最大行数限制：' + res.rowCountThreshold + '，实际行数：' + res.actualRowCount + '。尝试删除最后多余的空白行？“ctrl+shift+↓”，“右键”，“删除”，“整行”', {
                        skin: 'layui-layer-lan'
                        , closeBtn: 0
                    });
                } else {
                    return layer.msg('导入失败！检查数据准确性？', {
                        offset: '15px'
                        , icon: 2
                        , time: 2000
                    });
                }
            }
            , error: function () {
                layer.closeAll('loading'); //关闭loading
                return layer.msg('导入失败', {
                    offset: '15px'
                    , icon: 2
                    , time: 2000
                });
            }
        });

        /*====================导入教师和助教工作手册=======================*/

        for (var i = 0; i < campusNames.length; i++) {
            var json = campusNames[i];
            var str = "";
            str += '<option value="' + json + '">' + json + '</option>';
            $("#campus2").append(str);
        }
        form.render('select');

        //监听自动解析开关
        form.on('checkbox(read_step2)', function (data) {
            //开关是否开启，true或者false
            var checked = data.elem.checked;
            if (checked) {
                $("#div-campus2").show();
                $("#div-import-assistant-tutorial").show();
            } else {
                $("#div-campus2").hide();
                $("#div-import-assistant-tutorial").hide();
            }
        });

        form.on('submit(download-current-assistant-tutorial-template)', function (obj) {
            var field = obj.field;
            location.href = '${ctx}/toolbox/assistantAdministrator/downloadTemplate/1?campus=' + field.campus2;
        });

        upload.render({
            elem: '#import-assistant-tutorial'
            , url: '${ctx}/toolbox/assistantAdministrator/assistantTutorialTemplateImport'
            , data: {
                classCampus: function () {
                    return $("#campus2").val();
                }
            }
            , accept: 'file' //普通文件
            , exts: 'xls|xlsx' //允许上传的文件后缀
            , before: function (obj) { //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
                layer.load(1, {shade: [0.1, '#fff']}); //上传loading
            }
            , done: function (res) {//返回值接收
                layer.closeAll('loading'); //关闭loading
                if (res.msg === "success") {
                    return layer.msg('导入成功', {
                        icon: 1
                        , time: 1000
                    });
                } else if (res.msg === "campusInvalid") {
                    return layer.msg('请选择正确的校区!', {
                        offset: '15px'
                        , icon: 2
                        , time: 2000
                    });
                } else {
                    return layer.msg('导入失败！检查数据准确性？', {
                        offset: '15px'
                        , icon: 2
                        , time: 2000
                    });
                }
            }
            , error: function () {
                layer.closeAll('loading'); //关闭loading
                return layer.msg('导入失败', {
                    offset: '15px'
                    , icon: 2
                    , time: 2000
                });
            }
        });
    });
</script>
</body>
</html>

