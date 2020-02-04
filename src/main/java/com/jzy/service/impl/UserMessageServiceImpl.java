package com.jzy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jzy.dao.UserMessageMapper;
import com.jzy.manager.exception.InvalidFileInputException;
import com.jzy.manager.util.FileUtils;
import com.jzy.model.LogLevelEnum;
import com.jzy.model.dto.MyPage;
import com.jzy.model.dto.UserMessageDto;
import com.jzy.model.dto.search.UserMessageSearchCondition;
import com.jzy.model.entity.UserMessage;
import com.jzy.service.UserMessageService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author JinZhiyun
 * @version 1.0
 * @ClassName UserMessageServiceImpl
 * @description 用户消息业务接口实现
 * @date 2019/12/6 20:34
 **/
@Service
public class UserMessageServiceImpl extends AbstractServiceImpl implements UserMessageService {
    private final static Logger logger = LogManager.getLogger(UserMessageServiceImpl.class);

    @Autowired
    private UserMessageMapper userMessageMapper;

    @Override
    public UserMessage getUserMessageById(Long id) {
        return id == null ? null : userMessageMapper.getUserMessageById(id);
    }

    @Override
    public UserMessageDto getUserMessageDtoById(Long id) {
        return id == null ? null : userMessageMapper.getUserMessageDtoById(id);
    }

    @Override
    public UserMessage getUserMessageByUserId(Long userId) {
        return userId == null ? null : userMessageMapper.getUserMessageByUserId(userId);
    }

    @Override
    public UserMessage getUserMessageByUserFromId(Long userFromId) {
        return userFromId == null ? null : userMessageMapper.getUserMessageByUserFromId(userFromId);
    }

    @Override
    public List<UserMessageDto> listUserMessagesByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        UserMessageSearchCondition condition = new UserMessageSearchCondition();
        condition.setUserId(userId);
        return userMessageMapper.listUserMessages(condition);
    }

    @Override
    public PageInfo<UserMessageDto> listUserMessages(MyPage myPage, UserMessageSearchCondition condition) {
        PageHelper.startPage(myPage.getPageNum(), myPage.getPageSize());
        List<UserMessageDto> userMessages = userMessageMapper.listUserMessages(condition);
        return new PageInfo<>(userMessages);
    }

    @Override
    public PageInfo<UserMessageDto> listUserMessagesByUserIdAndRead(MyPage myPage, Long userId, boolean read) {
        PageHelper.startPage(myPage.getPageNum(), myPage.getPageSize());
        List<UserMessageDto> userMessages = userMessageMapper.listUserMessagesByRead(userId, read);
        return new PageInfo<>(userMessages);
    }

    @Override
    public long updateUserMessageReadById(Long id) {
        if (id == null) {
            return 0;
        }
        return userMessageMapper.updateUserMessageReadById(id);
    }


    @Override
    public long countUserMessagesByUserIdAndRead(Long userId, boolean read) {
        if (userId == null) {
            return 0;
        }
        return userMessageMapper.countUserMessagesByUserIdAndRead(userId, read);
    }

    @Override
    public long updateManyUserMessagesReadByIds(List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            return 0;
        }
        return userMessageMapper.updateManyUserMessagesReadByIds(ids);
    }

    @Override
    public long updateManyUserMessagesReadByUserId(Long userId) {
        if (userId == null) {
            return 0;
        }
        return userMessageMapper.updateManyUserMessagesReadByUserId(userId);
    }

    /**
     * 根据id删除消息的图片
     *
     * @param id
     */
    private void deletePictureFileById(Long id) {
        UserMessage message = getUserMessageById(id);
        if (message != null) {
            /*
             * 删除图片
             */
            deleteUserMessagePicture(message);
        }
    }

    @Override
    public long deleteManyUserMessagesByIds(List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            return 0;
        }

        for (Long id : ids) {
            deletePictureFileById(id);
        }
        return userMessageMapper.deleteManyUserMessagesByIds(ids);
    }

    @Override
    public String insertOneUserMessage(UserMessage userMessage) {
        if (userMessage == null) {
            return FAILURE;
        }
        /*
         * 用户上传的图片的处理
         */
        dealWithInsertingUserMessagePicture(userMessage);

        userMessageMapper.insertOneUserMessage(userMessage);
        return SUCCESS;
    }

    /**
     * 用户上传的图片的处理。对用户上传的图片：如果不是欢迎图片，就要做重命名。
     *
     * @param userMessage 用户消息对象入参
     * @return 处理过图片字段的入参用户消息对象直接返回
     */
    private UserMessage dealWithInsertingUserMessagePicture(UserMessage userMessage) {
        if (!userMessage.isWelcomePicture()) {
            //如果不是欢迎图片，就要做重命名
            if (!StringUtils.isEmpty(userMessage.getMessagePicture())) {
                //如果用户上传了新图片
                renameUserMessagePicture(userMessage);
            } else {
                //仍设置原头像
                userMessage.setMessagePicture(null);
            }
        }

        return userMessage;
    }

    /**
     * 对入参userMessage，删除它在硬盘上保存的配图文件（如果有且不是默认图片的话）。
     *
     * @param userMessage 用户消息对象
     */
    private void deleteUserMessagePicture(UserMessage userMessage) {
        if (!userMessage.isWelcomePicture()) {
            //不是欢迎图片
            if (!StringUtils.isEmpty(userMessage.getMessagePicture())) {
                FileUtils.deleteFile(filePathProperties.getUserMessagePictureDirectory() + userMessage.getMessagePicture());
            }
        }
    }

    /**
     * 重命名新消息的缓存的图片文件名。
     *
     * @param userMessage 用户消息对象
     * @return 重命名后的新图片文件名
     */
    private String renameUserMessagePicture(UserMessage userMessage) {
        //将上传的新图片文件重名为含日期时间的newUserIconName，该新文件名用来保存到数据库
        String newPicture = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new Date()) + "_" + userMessage.getMessagePicture();
        FileUtils.renameByName(filePathProperties.getUserMessagePictureDirectory(), userMessage.getMessagePicture(), newPicture);
        userMessage.setMessagePicture(newPicture);

        return newPicture;
    }

    @Override
    public String uploadPicture(MultipartFile file) throws InvalidFileInputException {
        Long userId = userService.getSessionUserInfo().getId();
        return uploadPicture(file, userId.toString());
    }

    @Override
    public String uploadPicture(MultipartFile file, String id) throws InvalidFileInputException {
        if (file == null || file.isEmpty()) {
            String msg = "上传文件为空";
            logger.error(msg);
            throw new InvalidFileInputException(msg);
        }

        String originalFilename = file.getOriginalFilename();
        String idStr;
        if (StringUtils.isEmpty(id)) {
            idStr = UUID.randomUUID().toString().replace("-", "_");
        } else {
            idStr = id;
        }
        String fileName = "picture_" + idStr + originalFilename.substring(originalFilename.lastIndexOf("."));
        String filePath = filePathProperties.getUserMessagePictureDirectory();
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            String msg = "id:" + id + "——消息图片附件上传失败";
            logger.error(msg);
            importantLogService.saveImportantLogBySessionUser(msg, LogLevelEnum.ERROR, null);
        }
        return fileName;
    }

    @Override
    public String insertManyUserMessages(List<UserMessage> userMessages) throws Exception {
        if (userMessages == null || userMessages.size() == 0) {
            return FAILURE;
        }

        /*
         * 用户上传的图片的处理
         */
        dealWithUserMessagesPicturesByCopyingAndDelete(userMessages);

        //插入
        userMessageMapper.insertManyUserMessages(userMessages);

        return SUCCESS;
    }

    /**
     * 对于批量的用户消息，对用户上传的图片的处理。
     * 对用户上传的图片：如果不是欢迎图片，就先对发送方的图片复制，最后再将原图删除。
     *
     * @param userMessages 批量用户消息对象入参
     * @return 处理过图片字段的入参批量用户消息对象直接返回
     */
    private List<UserMessage> dealWithUserMessagesPicturesByCopyingAndDelete(List<UserMessage> userMessages) throws Exception {
        String originalFile = "";
        for (UserMessage message : userMessages) {
            if (!StringUtils.isEmpty(message.getMessagePicture())) {
                //如果用户上传了新图片
                //将上传的新图片文件重名为含日期时间的newUserIconName，该新文件名用来保存到数据库
                originalFile = message.getMessagePicture();
                String newPicture = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new Date()) + "_" + UUID.randomUUID().toString().replace("-", "") + "_" + originalFile;
                FileUtils.copyFile(filePathProperties.getUserMessagePictureDirectory() + originalFile, filePathProperties.getUserMessagePictureDirectory() + newPicture);
                message.setMessagePicture(newPicture);

            } else {
                message.setMessagePicture(null);
            }
        }

        //删除原来上传的文件
        if (!StringUtils.isEmpty(originalFile)) {
            FileUtils.deleteFile(filePathProperties.getUserMessagePictureDirectory() + originalFile);
        }

        return userMessages;
    }
}
