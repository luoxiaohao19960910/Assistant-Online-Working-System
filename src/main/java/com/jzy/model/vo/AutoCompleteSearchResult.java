package com.jzy.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author JinZhiyun
 * @version 1.0
 * @ClassName AutoCompleteSearchResult
 * @description AutoComplete下拉input搜索结果的封装
 * @date 2019/12/16 15:38
 **/
@Data
public class AutoCompleteSearchResult implements Serializable {
    private static final long serialVersionUID = -9183496654773379697L;

    /**
     * 表单框具体选择的值
     */
    protected String value;

    /**
     * 值旁边显示的子内容
     */
    protected String subValue;

    public AutoCompleteSearchResult(String value, String subValue) {
        this.value = value;
        this.subValue = subValue;
    }

    public AutoCompleteSearchResult() {
    }
}
