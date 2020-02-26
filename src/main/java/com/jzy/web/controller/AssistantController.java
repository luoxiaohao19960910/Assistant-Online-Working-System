package com.jzy.web.controller;

import com.jzy.model.entity.Assistant;
import com.jzy.model.vo.AssistantSearchResult;
import com.jzy.model.vo.AutoCompleteSearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName AssistantController
 * @Author JinZhiyun
 * @Description 助教业务控制器（所有角色通用，不涉及权限问题）
 * @Date 2020/2/26 11:11
 * @Version 1.0
 **/
@Controller
@RequestMapping("/assistant")
public class AssistantController extends AbstractController {
    /**
     * 获取模糊匹配的助教姓名，且不区分大小写。
     *
     * @param assistantName 输入字符串作为关键字
     * @return 符合条件的数据 {@link AutoCompleteSearchResult}
     */
    @RequestMapping("/getAssistantsLikeAssistantName")
    @ResponseBody
    public Map<String, Object> getAssistantsLikeAssistantName(@RequestParam(value = "keywords", required = false) String assistantName) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", 0);
        map.put("msg", "");


        List<Assistant> assistants = assistantService.listAssistantsLikeAssistantName(assistantName);
        List<AutoCompleteSearchResult> results = new ArrayList<>();

        for (Assistant assistant : assistants) {
            if (assistant.getAssistantCampus() == null) {
                assistant.setAssistantCampus("");
            }
            if (assistant.getAssistantPhone() == null) {
                assistant.setAssistantPhone("");
            }

            AssistantSearchResult result = new AssistantSearchResult();
            result.setValue(assistant.getAssistantName());
            result.setSubValue(assistant.getAssistantPhone());
            result.setParsedValue(assistant);
            results.add(result);
        }
        map.put("data", results);
        return map;
    }
}
