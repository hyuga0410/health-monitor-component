package com.hyuga.service.agent.monitor.api;

import cn.hyugatool.core.string.StringUtil;
import com.hyuga.service.agent.monitor.constants.MonitorConstants;
import com.hyuga.service.agent.monitor.enums.ProjectEnum;
import com.hyuga.service.agent.monitor.service.ServiceHeartbeatViewService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 房源表微服务层Api
 *
 * @author fjk
 * @since 2021-03-08
 */
@Controller
@RequestMapping(value = "/monitor/")
public class MonitorApi {

    @Resource
    private ServiceHeartbeatViewService serviceHeartbeatViewService;

    // 省份简标 ==============================================================================================================

    @GetMapping("server/view")
    public void serverView(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
        try {
            String env = request.getParameter(MonitorConstants.ENV);
            String project = request.getParameter(MonitorConstants.PROJECT);
            String singleStr = request.getParameter(MonitorConstants.SINGLE);

            env = StringUtil.isEmpty(env) ? "all" : env;
            project = StringUtil.isEmpty(project) ? ProjectEnum.AGENT.name() : project;
            boolean single = StringUtil.equals(singleStr, "true");

            // 响应结果 html页面
            response.setCharacterEncoding(request.getCharacterEncoding());
            response.setContentType(MonitorConstants.TEXT_HTML);

            PrintWriter writer = response.getWriter();
            writer.write(serviceHeartbeatViewService.getHtml(env.toUpperCase(), project, single));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
