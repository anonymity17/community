package com.majiang.demo.service;

import com.majiang.demo.dto.NotificationDTO;
import com.majiang.demo.dto.PaginationDTO;
import com.majiang.demo.enums.NotificationtStatusEnum;
import com.majiang.demo.enums.NotificationtTypeEnum;
import com.majiang.demo.exception.CustomizeErrorCode;
import com.majiang.demo.exception.CustomizeException;
import com.majiang.demo.mapper.NotificationMapper;
import com.majiang.demo.mapper.UserMapper;
import com.majiang.demo.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTOS = new PaginationDTO<>();
        //获取个人页面中所有的通知条数
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);
        //使用方法将所有的参数传入
        paginationDTOS.setPagination(totalCount, page, size);
        //对于page越界的处理
        if (page < 1) {
            page = 1;
        }
        if (page > paginationDTOS.getTotalPage()) {
            page = paginationDTOS.getTotalPage();
        }

        Integer offset = size * (page - 1);
        //获取所有的通知目录,(查询我的所有通知，也就是将userId传进入)
        NotificationExample notificationExample1 = new NotificationExample();
        notificationExample1.createCriteria()
                .andReceiverEqualTo(userId);
        notificationExample1.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample1, new RowBounds(offset, size));
        /*判断有没有通知*/
        if (notifications.size() == 0) {
            return paginationDTOS;
        }

        List<NotificationDTO> notificationDTOList = new ArrayList<>();
        //遍历所有的通知
        for (Notification notification : notifications) {
            //要将Notification转换成NotificationDTO
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);//快速将Notification对象中的属性拷贝到NotificationDTO对象上
            notificationDTO.setTypeName(NotificationtTypeEnum.nameOfType(notification.getType()));/*根据通知的类型获取对应的字符串*/
            notificationDTOList.add(notificationDTO);
        }
        /*问题和通知都需要set，因此将paginationDTO中的类型改成泛型*/
        paginationDTOS.setData(notificationDTOList);
        return paginationDTOS;
    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId)
                .andStatusEqualTo(NotificationtStatusEnum.UNREAD.getStatus());/*根据status=0来统计未读通知*/
        long count = notificationMapper.countByExample(notificationExample);
        return count;
    }

    /**
     * 读取 user的通知，该通知id为notificationId
     *
     * @param notificationId
     * @param user
     * @return
     */
    public NotificationDTO read(Long notificationId, User user) {
        /*获取将要读取的通知*/
        Notification notification = notificationMapper.selectByPrimaryKey(notificationId);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (notification.getReceiver() != user.getId()) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        /*将 这个notification更新为已读*/
        notification.setStatus(NotificationtStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);//快速将Notification对象中的属性拷贝到NotificationDTO对象上
        /*notification中已经从表中获取到了通知的各种属性*/
        notificationDTO.setTypeName(NotificationtTypeEnum.nameOfType(notification.getType()));/*根据通知的类型获取对应的字符串*/

        return notificationDTO;
    }
}
