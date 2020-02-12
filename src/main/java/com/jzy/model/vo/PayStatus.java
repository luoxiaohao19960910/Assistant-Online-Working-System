package com.jzy.model.vo;

import com.jzy.manager.util.MyTimeUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName PayStatus
 * @Author JinZhiyun
 * @Description 用户支付状态
 * @Date 2020/2/12 10:19
 * @Version 1.0
 **/
@Data
public class PayStatus implements Serializable {
    private static final long serialVersionUID = 6875570234752500951L;

    /**
     * 是否需要支付
     */
    private boolean needToPay;

    /**
     * 支付倒计时起始的时间
     */
    private Date startDate;

    /**
     * 支付截止日期
     */
    private Date expirationDate;

    /**
     * 获得一个已支付的支付状态
     *
     * @return
     */
    public static PayStatus getPaidStatus() {
        PayStatus payStatus = new PayStatus();
        payStatus.setNeedToPay(false);
        return payStatus;
    }

    /**
     * 根据当前时间和过期倒计时，计算支付到期的时间date
     *
     * @param expirationSecond 单位为秒的过期倒计时
     */
    public PayStatus(long expirationSecond) {
        this.needToPay = true;
        this.startDate = new Date();
        this.expirationDate = MyTimeUtils.getSecondsAfter(startDate, (int) expirationSecond);
    }

    public PayStatus() {
    }
}
