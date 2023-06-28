function post() {
    var questionId = $("#question_id").val();//之前使用 hidden存储在question_id中的
    var content = $("#comment_content").val();//text文本框中的内容
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({//使用stringify将js的对象转化成字符串
            "parentId": questionId,
            "content": content,
            "type": 1
        }),
        success: function (response) {
            if (response.code == 200) {//回复成功后关闭这一个"评论块"
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
    // console.log(content);//在控制台打印
    // console.log(questionId);//在控制台打印
}