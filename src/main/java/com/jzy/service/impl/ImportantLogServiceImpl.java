package com.jzy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jzy.dao.ImportantLogMapper;
import com.jzy.manager.util.ImportantLogUtils;
import com.jzy.model.LogLevelEnum;
import com.jzy.model.dto.ImportantLogDetailedDto;
import com.jzy.model.dto.MyPage;
import com.jzy.model.dto.search.ImportantLogSearchCondition;
import com.jzy.model.entity.ImportantLog;
import com.jzy.model.entity.User;
import com.jzy.service.ImportantLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName ImportantLogServiceImpl
 * @Author JinZhiyun
 * @Description 持久化到数据库的重要日志业务接口实现
 * @Date 2020/1/31 9:35
 * @Version 1.0
 **/
@Service
public class ImportantLogServiceImpl extends AbstractServiceImpl implements ImportantLogService {
    @Autowired
    private ImportantLogMapper importantLogMapper;

    @Override
    public String insertOneImportantLog(ImportantLog importantLog) {
        if (importantLog == null) {
            return FAILURE;
        }

        importantLogMapper.insertOneImportantLog(importantLog);
        return SUCCESS;
    }

    @Override
    public PageInfo<ImportantLogDetailedDto> listImportantLog(MyPage myPage, ImportantLogSearchCondition condition) {
        PageHelper.startPage(myPage.getPageNum(), myPage.getPageSize());
        List<ImportantLogDetailedDto> allLog = importantLogMapper.listImportantLog(condition);
        return new PageInfo<>(allLog);
    }

    @Override
    public long deleteManyImportantLogByIds(List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            return 0;
        }
        return importantLogMapper.deleteManyImportantLogByIds(ids);
    }

    @Override
    public long deleteImportantLogByCondition(ImportantLogSearchCondition condition) {
        if (condition == null) {
            return 0;
        }
        return importantLogMapper.deleteImportantLogByCondition(condition);
    }

    @Override
    public boolean saveImportantLogBySessionUser(String msg) {
        return saveImportantLogBySessionUser(msg, null);
    }

    @Override
    public boolean saveImportantLogBySessionUser(String msg, String ip) {
        return saveImportantLogBySessionUser(msg, LogLevelEnum.INFO, ip);
    }

    @Override
    public boolean saveImportantLogBySessionUser(String msg, LogLevelEnum level, String ip) {
        return saveImportantLogBySessionUser(msg, level, ip, null);
    }

    @Override
    public boolean saveImportantLogBySessionUser(String msg, LogLevelEnum level, String ip, String remark) {
        return saveImportantLog(msg, level, userService.getSessionUserInfo(), ip, remark);
    }

    @Override
    public boolean saveImportantLog(String msg, User user, String ip) {
        return saveImportantLog(msg, LogLevelEnum.INFO, user, ip);
    }

    @Override
    public boolean saveImportantLog(String msg, LogLevelEnum level, User user, String ip) {
        return saveImportantLog(msg, level, user, ip, null);
    }

    @Override
    public boolean saveImportantLog(String msg, LogLevelEnum level, User user, String ip, String remark) {
        ImportantLog log = new ImportantLog();
        log.setMessage(msg);
        log.setLevel(level.getLevel());
        if (user != null) {
            log.setOperatorId(user.getId());
        }
        log.setOperatorIp(ip);
        log.setRemark(remark);
        //将该重要日志记录持久化到数据库
        if (ImportantLogUtils.isValidImportantLogUpdateInfo(log)) {
            importantLogService.insertOneImportantLog(log);
            return true;
        }
        return false;
    }
}
