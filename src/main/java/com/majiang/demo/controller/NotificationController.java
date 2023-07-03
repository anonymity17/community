package com.majiang.demo.controller;

import com.majiang.demo.dto.NotificationDTO;
import com.majiang.demo.dto.PaginationDTO;
import com.majiang.demo.enums.NotificationtTypeEnum;
import com.majiang.demo.model.User;
import com.majiang.demo.service.NotificationService;
import com.majiang.demo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 完成点击通知内容进行跳转，从而完成已读功能
 */
@Controller
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    /*@PathVariable注解用于获取URL路径中的{id}参数的值*/
    /*@RequestParam用于获取查询参数或表单数据中的参数值。*/
    @GetMapping("/notification/{id}")//访问首页的时候，如下
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "id") Long id) {
        System.out.println("id" + id);
        //验证是否登录
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";//返回到首页
        }
        NotificationDTO notificationDTO = notificationService.read(id, user);
        /*判断通知的类型是否正确*/
        if (NotificationtTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType() ||
                NotificationtTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()) {
            /*跳转到被回复的那个问题*/
            return "redirect:/question/" + notificationDTO.getOuterId();
        } else {
            return "redirect:/";
        }
    }
}
