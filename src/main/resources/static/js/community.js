/**
 * 提交回复
 */
function post() {
    var questionId = $("#question_id").val();//之前使用 hidden存储在question_id中的
    var content = $("#comment_content").val();//text文本框中的内容
    comment2Target(questionId,1,content);
}

function comment2Target(targetId, type, content) {
    /*前端的判断是让用户更早的得到相应，后端的判断是最终避免数据错误的提交*/
    /*虽然后端有毒内容是否为空的判断，这里还是要继续做判断*/
    if (!content) {
        alert("不能回复空内容~~")
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({//使用stringify将js的对象转化成字符串
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {//回复成功后关闭这一个"评论块"
                /*回复的评论没有like显示在评论列表，此处刷新*/
                window.location.reload();
                $("#comment_section").hide();
            } else {
                if (response.code == 2003) {
                    //没有登录,弹出是否是否登录的确认框
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        //确定登录，打开一个新的登录地址
                        window.open("https://github.com/login/oauth/authorize?client_id=Iv1.b387f2fbd395e4b0&redirect_uri=http://localhost:8888/callback&scope=user&state=1")
                        //这里确认登录后会打开一个新页面（首页）
                        //但是我们希望回到之前的编辑评论的页面状态=》传递一个值，如果值是对的就将这个新页面给关掉
                        //但是传递值需要后端api调来调去=》使用纯前端的方式来进行=》html5 web存储中的localStorage
                        window.localStorage.setItem("closable", true);//（1）设置localStorage为true
                        //确认登录后会跳回首页=》（2）去首页判断localStorage是否为true，如果是就关掉
                    }
                } else {
                    alert(response.message);
                }
            }
            console.log(response);
        },
        dataType: "json"
    });
}

function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val()
    comment2Target(commentId,2,content);
}

/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);
    var collapse = e.getAttribute("data-collapse");
    if (collapse){
        comments.removeClass("in");
        e.removeAttribute("data-collapse")
        e.classList.remove("active")
    }else{
        var subCommentContainer = $("#comment-" + id)
        if (subCommentContainer.children().length != 1){//说明这个列表已经加载过了，不用重新请求加载列表了
            //展开二级评论
            comments.addClass("in")
            /*标记二级评论展开状态*/
            e.setAttribute("data-collapse","in")
            e.classList.add("active")
        }else{
            $.getJSON("/comment/" + id,function (data){
                console.log(data)
                $.each(data.data.reverse(),function (index, comment) {
                    var mediaLeftElement =$("<div/>",{
                        "class":"media-left",
                    }).append($("<img/>", {
                        "class": "media-object",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>",{
                        "class":"media-body",
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class":"pull-right",
                        "html": moment(comment.gmtCreate).format('yyyy-MM-DD HH:mm')
                    })));

                    var mediaElement = $("<div/>",{
                      "class":"media"
                    }).append(mediaLeftElement)
                        .append(mediaBodyElement)

                    var commentElement =$("<div/>",{
                        "class":"col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    }).append(mediaElement)
                    subCommentContainer.prepend(commentElement);
                })
                //展开二级评论
                comments.addClass("in")
                /*标记二级评论展开状态*/
                e.setAttribute("data-collapse","in")
                e.classList.add("active")
            });
        }


    }
}