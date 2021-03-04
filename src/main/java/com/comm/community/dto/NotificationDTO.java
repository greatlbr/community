package com.comm.community.dto;

import com.comm.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    private String outerTitle;//外部名字（标题）
    private Long outerid;
    private String typeName;
    private Integer type;
}
