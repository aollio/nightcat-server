package com.nightcat.projects.web;

import com.nightcat.common.Response;
import com.nightcat.common.base.BaseController;
import com.nightcat.common.constant.Constant;
import com.nightcat.common.utility.Assert;
import com.nightcat.common.utility.Util;
import com.framework.annotation.Authorization;
import com.framework.annotation.CurrentUser;
import com.nightcat.entity.DesignType;
import com.nightcat.entity.Project;
import com.nightcat.entity.User;
import com.nightcat.projects.service.ProjectBidderService;
import com.nightcat.projects.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

import static com.nightcat.common.Response.ok;
import static com.nightcat.common.utility.Util.*;
import static com.nightcat.common.constant.HttpStatus.*;

@RestController
@RequestMapping("/projects")
public class ProjectController extends BaseController {


    @Autowired
    private ProjectService projServ;

    @Autowired
    private ProjectBidderService bidderServ;

    /**
     * 返回项目首页的timeline
     */
    @GetMapping("/timeline")
    public Response timeline(
            @RequestParam Integer type,
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(name = "since_time", required = false, defaultValue = "0") String since_time_str,
            @RequestParam(name = "max_time", required = false) String max_time_str) {

        Timestamp since_time = emptyStr(since_time_str) ? new Timestamp(0) : timeFromStr(since_time_str);
        Timestamp max_time = emptyStr(max_time_str) ? now() : timeFromStr(max_time_str);

        DesignType designType = DesignType.UNDEFINDED;

        if (type != null) {
            DesignType temp = Util.enumFromOrigin(type, DesignType.class);
            Assert.notNull(temp, BAD_REQUEST, "设计类型不对");
            designType = temp;
        }

        limit = limit == null ? Constant.DEFAULT_LIMIT : limit;

        return ok(projServ.findByType(designType, limit, since_time, max_time));
    }


    /**
     * 用户项目列表
     */
    @GetMapping("user_timeline")
    @Authorization
    public Response user_time(@CurrentUser User user,
                              @RequestParam(required = false) Integer type,
                              @RequestParam(required = false) Integer limit,
                              @RequestParam(name = "since_time", required = false, defaultValue = "0") String since_time_str,
                              @RequestParam(name = "max_time", required = false) String max_time_str) {

        DesignType designType = DesignType.UNDEFINDED;

        if (type != null) {
            DesignType temp = Util.enumFromOrigin(type, DesignType.class);
            Assert.notNull(temp, BAD_REQUEST, "设计类型不对");
            designType = temp;
        }

        limit = limit == null ? Constant.DEFAULT_LIMIT : limit;

        Timestamp since_time = timeFromStr(since_time_str);
        Timestamp max_time = emptyStr(max_time_str) ? now() : timeFromStr(max_time_str);
        return ok(projServ.findTimelineByUid(user.getUid(), designType, limit, since_time, max_time));
    }

    /**
     * 获取项目的抢单列表
     */
    @GetMapping("/grabber_list")
    @Authorization
    public Response show_grab(String id) {
        //todo
        return ok(bidderServ.findByProjectId(id));
    }


    /**
     * 显示项目详细信息
     */
    @GetMapping("/show")
    public Response show(String id) {
        Assert.strExist(id, BAD_REQUEST, "参数id不存在");

        Project project = projServ.findById(id);

        Assert.notNull(project, NOT_FOUND, "项目不存在");
        return ok(project);
    }

    /**
     * 获取每个项目的图片
     */
    @GetMapping("/imgs")
    public Response pictures(String id) {
        Assert.strExist(id, BAD_REQUEST, "参数id不存在");
        return ok(projServ.findPicturesByProjId(id));
    }


}