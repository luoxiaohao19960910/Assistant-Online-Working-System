package com.jzy.model.vo;

import com.jzy.model.entity.Assistant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName AssistantSearchResult
 * @Author JinZhiyun
 * @Description 助教查询AutoComplete下拉input搜索结果的封装
 * @Date 2020/2/26 11:51
 * @Version 1.0
 **/
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class AssistantSearchResult extends AutoCompleteSearchResult {
    private static final long serialVersionUID = 6484515985293954110L;

    /**
     * 将'姓名'变成'XX校区-XXX'格式，存储于该字段
     */
    private String parsedValue;

    public void setParsedValue(Assistant assistant) {
        if (assistant != null) {
            String prefix = StringUtils.isEmpty(assistant.getAssistantCampus()) ? "" : assistant.getAssistantCampus() + "助教-";
            parsedValue = prefix + assistant.getAssistantName();
        }
    }
}
