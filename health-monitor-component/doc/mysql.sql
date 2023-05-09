create table t_service_log
(
    FID                bigint(11) auto_increment comment '主键ID'
        primary key,
    FIP                varchar(20)                        not null comment 'IP地址',
    FPORT              int(5)                             not null comment '端口',
    FPROJECT           varchar(20)                        not null comment '项目',
    FNAME              varchar(100)                       not null comment '服务名',
    FENVIRONMENT       varchar(10)                        not null comment '服务环境',
    FSTATUS            varchar(10)                        not null comment '服务状态',
    FSERVER_STATR_TIME datetime                           not null comment '服务启动时间',
    FLAST_ONLINE_TIME  datetime                           null comment '最后在线时间',
    FONLINE_DURATION   varchar(50)                        null comment '持续在线时长',
    FOFFLINE_DURATION  varchar(50)                        null comment '持续离线时长',
    FCREATE_TIME       datetime default CURRENT_TIMESTAMP null comment '创建时间'
)
    comment '服务日志' charset = utf8mb4;

create
    index IX_ID_PROT
    on t_service_log (FIP, FPORT);

create
    index IX_IP_PORT_NAME
    on t_service_log (FIP, FPORT, FNAME);

create
    index IX_STATUS
    on t_service_log (FSTATUS)
    comment '用于监控服务端扫描存活服务';

alter table t_service_log
    add FPROJECT varchar(20) null comment '项目' after FID;

