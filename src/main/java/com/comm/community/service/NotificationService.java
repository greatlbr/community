package com.comm.community.service;

import com.comm.community.dto.NotificationDTO;
import com.comm.community.dto.PaginationDTO;
import com.comm.community.enums.NotificationEnum;
import com.comm.community.enums.NotificationStatusEnum;
import com.comm.community.enums.NotificationTypeEnum;
import com.comm.community.exception.CustomizeErrorCode;
import com.comm.community.exception.CustomizeException;
import com.comm.community.mapper.NotificationMapper;
import com.comm.community.mapper.UserMapper;
import com.comm.community.model.Notification;
import com.comm.community.model.NotificationExample;
import com.comm.community.model.User;
import com.comm.community.model.UserExample;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {//P49

    @Autowired(required = false)
    private NotificationMapper notificationMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {

        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        Integer totalPage;

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);

        if (totalCount % size ==0){
            totalPage = totalCount /size;
        }else totalPage = totalCount /size +1;

        //paginationDTO.setPagination(totalCount, page, size);

        if (page<1){
            page = 1;
        }


        if (page > totalPage){
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage, page);

        //size*(page - 1)
        Integer offset = size * (page - 1);

        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications =  notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        if(notifications.size() == 0){
            return paginationDTO;
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }

        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());//未读
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);//拿到这个notification
        if(notification == null){
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(!Objects.equals(notification.getReceiver(),user.getId())){//校验，防止别人通过工具访问
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        //更新已读
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
