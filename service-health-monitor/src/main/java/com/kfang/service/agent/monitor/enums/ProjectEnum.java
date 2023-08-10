package com.kfang.service.agent.monitor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ProjectEnum
 *
 * @author hyuga
 * @since 2023/5/18-05-18 11:00
 */
@Getter
@AllArgsConstructor
public enum ProjectEnum {

    /**
     *
     */
    AGENT("房客宝", "AGENT-房客宝"),
    SAAS_AGENT("SAAS", "SAAS_AGENT"),
    TUOKEBEN("拓客本", "AGENT-拓客本"),
    INFRA("基础", "INFRA-基础服务"),
    ;

    private final String desc;

    private final String projName;

}
