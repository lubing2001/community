$(function(){
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(     // 向服务器发送请求
        CONTEXT_PATH + "/like",     // 路径
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},  // 携带参数
        function(data) {            // 服务器返回参数之后调用这个回调函数
            data = $.parseJSON(data);   // 将字符串转成js对象
            if(data.code == 0) {        // code==0表示这次请求是成功的
                $(btn).children("i").text(data.likeCount);      // 通过调用这个js的button得到下级标签去改变下级标签的值
                $(btn).children("b").text(data.likeStatus==1?'已赞':"赞");
            } else {
                alert(data.msg);        // 失败的话弹出一个提示
            }
        }
    );
}


// 置顶
function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $("#topBtn").attr("disabled", "disabled");  //已经置顶后就无需置顶，设置disabled按钮为disabled不可用
            } else {
                alert(data.msg);
            }
        }
    );
}

// 加精
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $("#wonderfulBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}

// 删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                location.href = CONTEXT_PATH + "/index";            // 帖子删除以后跳转到首页
            } else {
                alert(data.msg);
            }
        }
    );
}