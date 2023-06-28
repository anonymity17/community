function post(){
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
        success: function(response){
            if (response.code == 200){//回复成功后关闭这一个"评论块"
                $("#comment_section").hide();
            }else{
                alert(response.message);
            }
            console.log(response);
        },
        dataType: "json"
    });
    console.log(content);//在控制台打印
    console.log(questionId);//在控制台打印
}