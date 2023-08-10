package com.hyuga.service.agent.monitor.queue;

/**
 * 队列任务处理
 *
 * @author hyuga
 * @since 2021-12-09 18:23:27
 */
@FunctionalInterface
public interface QueueTaskFunc {

    /**
     * accept
     *
     * @throws Exception Exception
     */
    void accept() throws Exception;

}
