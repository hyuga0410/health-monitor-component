package com.kfang.service.agent.monitor.dao.automapper;

import io.github.hyuga0410.health.monitor.model.ServiceLogModel;

import java.util.List;

/**
 * MonitorClientDao
 *
 * @author pengqinglong
 * @since 2021/12/1
 */
public interface MonitorDao {

    /**
     * 插入服务日志
     *
     * @param model model
     * @return 修改条数
     */
    int insertServiceLog(ServiceLogModel model);

    /**
     * 修改最后在线时间
     *
     * @param model model
     * @return 修改条数
     */
    int updateLastOnlineTime(ServiceLogModel model);

    /**
     * 修改状态
     *
     * @param model model
     * @return 修改条数
     */
    int updateStatus(ServiceLogModel model);

    /**
     * 查询服务log
     *
     * @param model model
     * @return model
     */
    ServiceLogModel selectServiceLog(ServiceLogModel model);

    /**
     * 查询服务log集合
     *
     * @param model model
     * @return list
     */
    List<ServiceLogModel> selectServiceLogList(ServiceLogModel model);

    /**
     * 查询所有在线服务集合
     *
     * @return list
     */
    List<ServiceLogModel> selectServiceLogOnlineAll();

    /**
     * 根据ID查询服务日志
     *
     * @param id id
     * @return model
     */
    ServiceLogModel selectServiceLogById(int id);

    /**
     * 根据日期清除历史服务log
     *
     * @param date 日期
     * @return 修改条数
     */
    int cleanStaleData(String date);

}
