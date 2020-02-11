package com.jzy.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName PayAnnouncement
 * @Author JinZhiyun
 * @Description 支付保护费公告
 * @Date 2020/2/11 14:02
 * @Version 1.0
 **/
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class PayAnnouncement extends TitleAndContent {
    private static final long serialVersionUID = -289281579784221314L;

    /**
     * 是否需要支付
     */
    private boolean needToPay;
}
