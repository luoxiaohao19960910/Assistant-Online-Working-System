package com.jzy.web.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.jzy.manager.constant.Constants;
import com.jzy.manager.constant.ModelConstants;
import com.jzy.manager.constant.RedisConstants;
import com.jzy.model.CampusEnum;
import com.jzy.model.dto.ClassSeasonDto;
import com.jzy.model.dto.MyPage;
import com.jzy.model.dto.UserWithPayStatus;
import com.jzy.model.entity.Class;
import com.jzy.model.entity.User;
import com.jzy.model.vo.Announcement;
import com.jzy.model.vo.PayAnnouncement;
import com.jzy.model.vo.PayStatus;
import com.jzy.model.vo.ResultMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @ClassName SystemController
 * @Author JinZhiyun
 * @Description 系统管理的控制器
 * @Date 2019/11/28 23:04
 * @Version 1.0
 **/
@Controller
@RequestMapping("/system")
public class SystemController extends AbstractController {
    private final static Logger logger = LogManager.getLogger(SystemController.class);

    /**
     * 跳转公告推送，从缓存中取上次的公告添加到model
     *
     * @return
     */
    @RequestMapping("/announcement")
    public String announcement(Model model) {
        Announcement announcement = (Announcement) hashOps.get(RedisConstants.ANNOUNCEMENT_KEY, Constants.BASE_ANNOUNCEMENT.toString());
        model.addAttribute(ModelConstants.ANNOUNCEMENT_EDIT_MODEL_KEY, announcement == null ? new Announcement() : announcement);
        return "system/announcement";
    }

    /**
     * 发布推送。
     * 注意游客的公告处理，其id为-1
     * id为-2的公告作为下次编辑是的缓存。
     * 其他id为正常用户的公告
     *
     * @param announcement 推送的信息
     * @return
     */
    @RequestMapping("/pushAnnouncement")
    @ResponseBody
    public Map<String, Object> pushAnnouncement(@RequestParam(value = "clearIfRead", required = false) String clearIfRead, Announcement announcement) {
        Map<String, Object> map = new HashMap<>(1);

        List<Long> userIds = userService.listAllUserIds();

        if (!Constants.ON.equals(clearIfRead)) {
            //是否永久有效
            announcement.setPermanent(true);
        }
        announcement.setRead(false);

        //推id为-2的公告，即缓存公告
        hashOps.put(RedisConstants.ANNOUNCEMENT_KEY, Constants.BASE_ANNOUNCEMENT.toString(), announcement);

        //如果是永久有效的公告，读取的时候只要读id=-2的即可，所以不用对每个用户id都保存缓存。
        if (!announcement.isPermanent()) {
            //推游客的公告
            hashOps.put(RedisConstants.ANNOUNCEMENT_KEY, Constants.GUEST_ID.toString(), announcement);
            for (Long id : userIds) {
                hashOps.put(RedisConstants.ANNOUNCEMENT_KEY, id.toString(), announcement);
            }
        }

        map.put("data", SUCCESS);
        return map;
    }

    /**
     * 清除推送。把游客（id=-1）和其他用户的公告都删除，只保留基本的公告缓存id=-2
     *
     * @return
     */
    @RequestMapping("/deleteAnnouncement")
    @ResponseBody
    public Map<String, Object> deleteAnnouncement() {
        Map<String, Object> map = new HashMap<>(1);

        List<Long> userIds = userService.listAllUserIds();

        hashOps.delete(RedisConstants.ANNOUNCEMENT_KEY, Constants.GUEST_ID.toString());
        hashOps.delete(RedisConstants.ANNOUNCEMENT_KEY, Constants.BASE_ANNOUNCEMENT.toString());
        for (Long userId : userIds) {
            hashOps.delete(RedisConstants.ANNOUNCEMENT_KEY, userId.toString());
        }

        map.put("data", SUCCESS);
        return map;
    }

    /**
     * 跳转智能校历。从缓存中查校历
     *
     * @return
     */
    @RequestMapping("/intelliClassSeason/page")
    public String intelliClassSeason(Model model) {
        model.addAttribute(ModelConstants.CAMPUS_NAMES_MODEL_KEY, JSON.toJSONString(CampusEnum.getCampusNamesList()));
        model.addAttribute(ModelConstants.SEASONS_MODEL_KEY, JSON.toJSONString(Class.SEASONS));
        model.addAttribute(ModelConstants.SUB_SEASONS_MODEL_KEY, JSON.toJSONString(Class.SUB_SEASONS));

        model.addAttribute(ModelConstants.CURRENT_ClASS_SEASON_MODEL_KEY, classService.getCurrentClassSeason());
        return "system/intelliClassSeason";
    }

    /**
     * 修改智能校历
     *
     * @param classSeason 修改后的校历信息
     * @return
     */
    @RequestMapping("/intelliClassSeason/update")
    @ResponseBody
    public Map<String, Object> updateIntelliClassSeason(ClassSeasonDto classSeason) {
        Map<String, Object> map = new HashMap<>(1);

        classService.updateCurrentClassSeason(classSeason);

        map.put("data", SUCCESS);
        return map;
    }

    /**
     * 清除智能校历
     *
     * @return
     */
    @RequestMapping("/intelliClassSeason/delete")
    @ResponseBody
    public Map<String, Object> deleteIntelliClassSeason() {
        Map<String, Object> map = new HashMap<>(1);

        classService.deleteCurrentClassSeason();

        map.put("data", SUCCESS);
        return map;
    }

    /**
     * 跳转支付公告推送，从缓存中取上次的公告添加到model
     *
     * @return
     */
    @RequestMapping("/payAnnouncement")
    public String payAnnouncement(Model model) {
        PayAnnouncement announcement = (PayAnnouncement) valueOps.get(RedisConstants.PAY_ANNOUNCEMENT_KEY);
        model.addAttribute(ModelConstants.PAY_ANNOUNCEMENT_EDIT_MODEL_KEY, announcement == null ? new PayAnnouncement() : announcement);
        return "system/payAnnouncement";
    }

    /**
     * 发布支付推送。同时置所有用户的支付状态为未支付，即清除所有用户状态缓存。
     *
     * @param announcement 推送的信息
     * @return
     */
    @RequestMapping("/pushPayAnnouncement")
    @ResponseBody
    public Map<String, Object> pushPayAnnouncement(PayAnnouncement announcement) {
        Map<String, Object> map = new HashMap<>(1);

        announcement.parseExpireTimeValueInSecondUnit();

        valueOps.set(RedisConstants.PAY_ANNOUNCEMENT_KEY, announcement);
        //清除所有用户支付状态
        List<Long> userIds = userService.listAllUserIds();
        for (Long userId : userIds) {
            redisOperation.expireKey(RedisConstants.getPayAnnouncementUserStatusKey(userId));
        }

        map.put("data", SUCCESS);
        return map;
    }

    /**
     * 清除推送。同时置所有用户的支付状态为未支付，即清除所有用户状态缓存。
     *
     * @return
     */
    @RequestMapping("/deletePayAnnouncement")
    @ResponseBody
    public Map<String, Object> deletePayAnnouncement() {
        Map<String, Object> map = new HashMap<>(1);

        redisOperation.expireKey(RedisConstants.PAY_ANNOUNCEMENT_KEY);
        //清除所有用户支付状态
        List<Long> userIds = userService.listAllUserIds();
        for (Long userId : userIds) {
            redisOperation.expireKey(RedisConstants.getPayAnnouncementUserStatusKey(userId));
        }


        map.put("data", SUCCESS);
        return map;
    }

    /**
     * 查询所有需要付费的用户的ajax交互。
     *
     * @param myPage    分页{页号，每页数量}
     * @param needToPay 筛选条件：已付/未付
     * @return
     */
    @RequestMapping("/getNeedToPayUsers")
    @ResponseBody
    public ResultMap<List<UserWithPayStatus>> getNeedToPayUsers(MyPage myPage, @RequestParam(value = "needToPay", required = false) String needToPay) {
        //存放所有需要付费的用户id
        List<Long> queryIds = new ArrayList<>();
        //存放所有需要付费的用户id中未付费的用户
        HashSet<Long> notPaidUserIds = new HashSet<>();
        //存放所有需要付费的用户id中已付费的用户
        HashSet<Long> paidUserIds = new HashSet<>();
        if (redisTemplate.hasKey(RedisConstants.PAY_ANNOUNCEMENT_KEY)) {
            //有收费公告，才查询已阅但未付费的用户
            List<Long> userIds = userService.listAllUserIds();
            for (Long userId : userIds) {
                String userPayStatusKey = RedisConstants.getPayAnnouncementUserStatusKey(userId);
                if (redisTemplate.hasKey(userPayStatusKey)) {
                    //当前用户id有缓存
                    queryIds.add(userId);
                    PayStatus payStatus = (PayStatus) valueOps.get(userPayStatusKey);
                    if (payStatus.isNeedToPay()) {
                        //未付费
                        notPaidUserIds.add(userId);
                    } else {
                        paidUserIds.add(userId);
                    }
                }
            }
        }

        if ("0".equals(needToPay)) {
            //未付
            queryIds = new ArrayList<>(notPaidUserIds);
        } else if ("1".equals(needToPay)) {
            //已付
            queryIds = new ArrayList<>(paidUserIds);
        }

        PageInfo<User> pageInfo = userService.listUsers(myPage, queryIds);
        List<User> users = pageInfo.getList();
        List<UserWithPayStatus> userWithPayStatuses = new ArrayList<>();
        for (User user : users) {
            //将User对象进一步封装成UserWithPayStatus对象
            UserWithPayStatus userWithPayStatus = new UserWithPayStatus();
            userWithPayStatus.setId(user.getId());
            userWithPayStatus.setUserWorkId(user.getUserWorkId());
            userWithPayStatus.setUserIdCard(user.getUserIdCard());
            userWithPayStatus.setUserName(user.getUserName());
            userWithPayStatus.setUserRealName(user.getUserRealName());
            userWithPayStatus.setUserRole(user.getUserRole());
            userWithPayStatus.setUserEmail(user.getUserEmail());
            userWithPayStatus.setUserPhone(user.getUserPhone());
            if (notPaidUserIds.contains(user.getId())) {
                //未支付
                userWithPayStatus.setNeedToPay(true);
            }
            userWithPayStatuses.add(userWithPayStatus);
        }
        return new ResultMap<>(0, "", (int) pageInfo.getTotal(), userWithPayStatuses);
    }
}
