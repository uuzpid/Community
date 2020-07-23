/**
 * 提交回复的事件
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId, 1, content)
}

function comment2target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容")
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        //传递JSON格式
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                //请求成功，隐藏输入框标签
                $("#comment_section").hide();
                location.reload();//重新加载页面，但是这里如果重新加载，就不是异步刷新了
            } else {
                if (response.code == 2003) {
                    //如果没有登录
                    //confirm弹出确认窗口，如果点取消，返回false
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        //window.open方法可以用来打开一个新的地址（窗口）
                        window.open("https://github.com/login/oauth/authorize?client_id=c3a4af36d919a94858b4&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        //在本地创建一个值，用于实现页面跳转后如果登录成功，直接关闭页面功能
                        window.localStorage.setItem("closable", true);
                    }
                } else {
                    alert(response.message)
                }

            }
        },
        dataType: "json"
    })

}

function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2target(commentId, 2, content)
}

/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);
    /**
     //toggleClass()
     //该方法检查每个元素中指定的类。如果不存在则添加类，如果已设置则删除之。这就是所谓的切换效果。
     comments.toggleClass("in");
     //    在元素中添加一个或多个类名
     e.classList.add("active")
     */
        //获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {//如果存在则为真。
        //折叠二级评论 这个添加在二级评论的获取id的div上的 直接操作class属性加减
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active")
    } else {
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length != 1) {
            //展开二级评论
            comments.addClass("in");
            //编辑二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active")
        } else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {

                    var mediaLeftElement = $("<div/>", {
                        "class":"media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class":"media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name//text标签写成html
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class":"menu"
                    }).append($("<span/>", {//子标签的子标签拼接
                        "class":"pull-right",
                        "html":moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class":"media"
                    }).append(mediaLeftElement)
                        .append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                        html: comment.content
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                //编辑二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}

function selectTag(e) {
    var value = e.getAttribute("data-tag");
    var previous = $("#tag").val();
    if(previous.indexOf(value)==-1){//如果标签不存在，再进行添加
        if(previous){
            $("#tag").val(previous+','+value);
        }else {
            $("#tag").val(value);
        }
    }

}

/**
 * 展示标签页面
 */
function showSelectTag(){
    $("#select-tag").show();
}
