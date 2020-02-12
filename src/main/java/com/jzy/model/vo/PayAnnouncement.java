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
     * 收费倒计时值
     */
    private int expireTimeValue;

    /**
     * 收费倒计时单位。
     * 0：秒
     * 1：分
     * 2：小时
     * 3：天
     */
    private int expireTimeUnit;

    /**
     * 秒级别的倒计时值，通过实例方法parseExpireTimeValueInSecondUnit转化
     */
    private long expireTimeValueInSecondUnit;

    /**
     * 计算秒级别的倒计时值
     */
    public void parseExpireTimeValueInSecondUnit() {
        if (expireTimeUnit == 0) {
            expireTimeValueInSecondUnit = expireTimeValue;
        } else if (expireTimeUnit == 1) {
            expireTimeValueInSecondUnit = expireTimeValue * 60;
        } else if (expireTimeUnit == 2) {
            expireTimeValueInSecondUnit = expireTimeValue * 60 * 60;
        } else if (expireTimeUnit == 3) {
            expireTimeValueInSecondUnit = expireTimeValue * 60 * 60 * 24;
        }
    }
}
