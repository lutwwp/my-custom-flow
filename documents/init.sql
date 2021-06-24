CREATE TABLE public.form(
form_id serial8 NOT NULL,
form_name varchar NOT NULL,
form_type varchar NOT NULL,
CONSTRAINT form_pkey PRIMARY KEY(form_id)
);

COMMENT ON TABLE public.form IS '自定义表单表';

COMMENT ON COLUMN public.form.form_id IS '表单id';
COMMENT ON COLUMN public.form.form_name IS '表单名称';
COMMENT ON COLUMN public.form.form_type IS '表单类型';


CREATE TABLE public.field(
field_id serial8 NOT NULL,
form_id int8,
field_code varchar,
order_number int4,
field_name varchar,
field_type varchar,
link_form_id int8,
link_field_id int8,
default_value_type varchar,
default_value varchar,
necessary bool,
allow_repeated bool,
show_field bool,
edit_field bool,
CONSTRAINT field_pkey PRIMARY KEY(field_id)
);

COMMENT ON TABLE public.field IS '自定义表单字段表';

COMMENT ON COLUMN public.field.field_id IS '字段id';
COMMENT ON COLUMN public.field.form_id IS '表单id';
COMMENT ON COLUMN public.field.field_code IS '字段编码';
COMMENT ON COLUMN public.field.order_number IS '字段排序';
COMMENT ON COLUMN public.field.field_name IS '字段名称';
COMMENT ON COLUMN public.field.field_type IS '字段类型';
COMMENT ON COLUMN public.field.link_form_id IS '关联表单id';
COMMENT ON COLUMN public.field.link_field_id IS '关联字段id';
COMMENT ON COLUMN public.field.default_value_type IS '默认值类型';
COMMENT ON COLUMN public.field.default_value IS '默认值';
COMMENT ON COLUMN public.field.necessary IS '是否必填';
COMMENT ON COLUMN public.field.allow_repeated IS '是否允许重复';
COMMENT ON COLUMN public.field.show_field IS '是否展示';
COMMENT ON COLUMN public.field.edit_field IS '是否可编辑';


CREATE TABLE public.link_field(
link_field_id serial8 NOT NULL,
link_type varchar NOT NULL,
master_field_id int8 NOT NULL,
slave_field_id int8 NOT NULL,
CONSTRAINT link_field_pkey PRIMARY KEY(link_field_id)
);

COMMENT ON TABLE public.link_field IS '关联字段表';

COMMENT ON COLUMN public.link_field.link_field_id IS '关联字段id';
COMMENT ON COLUMN public.link_field.link_type IS '关联类型';
COMMENT ON COLUMN public.link_field.master_field_id IS '主表字段id';
COMMENT ON COLUMN public.link_field.slave_field_id IS '从表字段id';


CREATE TABLE public.link_filter(
link_filter_id serial8 NOT NULL,
link_type varchar NOT NULL,
master_field_id int8 NOT NULL,
current_form_field_id int8 NOT NULL,
link_form_field_id int8 NOT NULL,
method varchar NOT NULL,
CONSTRAINT link_filter_pkey PRIMARY KEY(link_filter_id)
);

COMMENT ON TABLE public.link_filter IS '关联条件表';

COMMENT ON COLUMN public.link_filter.link_filter_id IS '关联条件id';
COMMENT ON COLUMN public.link_filter.link_type IS '关联类型';
COMMENT ON COLUMN public.link_filter.master_field_id IS '主表字段id';
COMMENT ON COLUMN public.link_filter.current_form_field_id IS '当前表字段id';
COMMENT ON COLUMN public.link_filter.link_form_field_id IS '关联表字段id';
COMMENT ON COLUMN public.link_filter.method IS '条件判断的方式';


CREATE TABLE public.field_value(
field_value_id serial8 NOT NULL,
form_id int8 NOT NULL,
field_id int8 NOT NULL,
field_type varchar,
field_value varchar,
primary_key_value int8,
CONSTRAINT field_value_pkey PRIMARY KEY(field_value_id)
);

COMMENT ON TABLE public.field_value IS '表单字段值的存储表';

COMMENT ON COLUMN public.field_value.field_value_id IS '主键id';
COMMENT ON COLUMN public.field_value.form_id IS '表单id';
COMMENT ON COLUMN public.field_value.field_id IS '字段id';
COMMENT ON COLUMN public.field_value.field_type IS '字段类型';
COMMENT ON COLUMN public.field_value.field_value IS '字段值';
COMMENT ON COLUMN public.field_value.primary_key_value IS '主键字段值';


CREATE TABLE public.process_info(
process_info_id serial8 NOT NULL,
form_id int8,
process_name varchar,
process_definition_key varchar,
process_definition_id varchar,
version int4,
process_param text,
process_bytearray bytea,
process_version int4,
status int4,
deleted bool,
CONSTRAINT process_info_pkey PRIMARY KEY(process_info_id)
);

COMMENT ON TABLE public.process_info IS '流程信息表';

COMMENT ON COLUMN public.process_info.process_info_id IS '主键id';
COMMENT ON COLUMN public.process_info.form_id IS '表单id';
COMMENT ON COLUMN public.process_info.process_name IS '流程名称';
COMMENT ON COLUMN public.process_info.process_definition_key IS '流程定义key值';
COMMENT ON COLUMN public.process_info.process_definition_id IS '流程定义id值';
COMMENT ON COLUMN public.process_info.version IS '流程版本';
COMMENT ON COLUMN public.process_info.process_param IS '流程参数';
COMMENT ON COLUMN public.process_info.process_bytearray IS '流程的二进制文件';
COMMENT ON COLUMN public.process_info.process_version IS '流程引擎版本';
COMMENT ON COLUMN public.process_info.status IS '状态：历史、设计、启用中';
COMMENT ON COLUMN public.process_info.deleted IS '是否删除';


CREATE TABLE public.process_node_field(
process_node_field_id serial8 NOT NULL,
process_info_id int8,
process_definition_key varchar,
process_definition_id varchar,
task_definition_key varchar,
form_id int8 NOT NULL,
field_id int8 NOT NULL,
show_field bool,
edit_field bool,
summary bool,
CONSTRAINT process_node_field_pkey PRIMARY KEY(process_node_field_id)
);

COMMENT ON TABLE public.process_node_field IS '流程节点与流程表字段关联关系';

COMMENT ON COLUMN public.process_node_field.process_node_field_id IS '主键id';
COMMENT ON COLUMN public.process_node_field.process_info_id IS '流程信息表id';
COMMENT ON COLUMN public.process_node_field.process_definition_key IS '流程定义key值';
COMMENT ON COLUMN public.process_node_field.process_definition_id IS '流程定义id值';
COMMENT ON COLUMN public.process_node_field.task_definition_key IS '任务定义key值';
COMMENT ON COLUMN public.process_node_field.form_id IS '表单id';
COMMENT ON COLUMN public.process_node_field.field_id IS '字段id';
COMMENT ON COLUMN public.process_node_field.show_field IS '是否展示';
COMMENT ON COLUMN public.process_node_field.edit_field IS '是否可编辑';
COMMENT ON COLUMN public.process_node_field.summary IS '摘要';


CREATE TABLE public.process_node_attribute(
process_node_attribute_id serial8 NOT NULL,
process_info_id int8,
process_definition_key varchar,
process_definition_id varchar,
task_definition_key varchar,
roll_back bool,
revoke bool,
CONSTRAINT process_node_attribute_pkey PRIMARY KEY(process_node_attribute_id)
);

COMMENT ON TABLE public.process_node_attribute IS '流程节点基础属性';

COMMENT ON COLUMN public.process_node_attribute.process_node_attribute_id IS '主键id';
COMMENT ON COLUMN public.process_node_attribute.process_info_id IS '流程信息表id';
COMMENT ON COLUMN public.process_node_attribute.process_definition_key IS '流程定义key值';
COMMENT ON COLUMN public.process_node_attribute.process_definition_id IS '流程定义id值';
COMMENT ON COLUMN public.process_node_attribute.task_definition_key IS '任务定义key值';
COMMENT ON COLUMN public.process_node_attribute.roll_back IS '是否开启回退';
COMMENT ON COLUMN public.process_node_attribute.revoke IS '是否开启撤销';


CREATE TABLE public.process_guarder(
process_guarder_id serial8 NOT NULL,
process_info_id int8,
process_definition_key varchar,
process_definition_id varchar,
user_id varchar,
CONSTRAINT process_guarder_pkey PRIMARY KEY(process_guarder_id)
);

COMMENT ON TABLE public.process_guarder IS '流程监控人配置';

COMMENT ON COLUMN public.process_guarder.process_guarder_id IS '主键id';
COMMENT ON COLUMN public.process_guarder.process_info_id IS '流程信息表id';
COMMENT ON COLUMN public.process_guarder.process_definition_key IS '流程定义key值';
COMMENT ON COLUMN public.process_guarder.process_definition_id IS '流程定义id值';
COMMENT ON COLUMN public.process_guarder.user_id IS '用户id';


CREATE TABLE public.process_guarder_ability(
process_guarder_ability_id serial8 NOT NULL,
process_info_id int8,
process_definition_key varchar,
process_definition_id varchar,
user_id varchar,
process_function varchar,
CONSTRAINT process_guarder_ability_pkey PRIMARY KEY(process_guarder_ability_id)
);

COMMENT ON TABLE public.process_guarder IS '流程监控人权限配置';

COMMENT ON COLUMN public.process_guarder_ability.process_guarder_ability_id IS '主键id';
COMMENT ON COLUMN public.process_guarder_ability.process_info_id IS '流程信息表id';
COMMENT ON COLUMN public.process_guarder_ability.process_definition_key IS '流程定义key值';
COMMENT ON COLUMN public.process_guarder_ability.process_definition_id IS '流程定义id值';
COMMENT ON COLUMN public.process_guarder_ability.user_id IS '用户id';
COMMENT ON COLUMN public.process_guarder_ability.process_function IS '用户权限/功能';


CREATE TABLE public.process_trigger(
process_trigger_id serial8 NOT NULL,
process_info_id int8,
master_process_definition_key varchar,
master_process_definition_id varchar,
trigger_type varchar,
process_function varchar,
flow_definition_key varchar,
slave_form_id varchar,
slave_process_definition_key varchar,
CONSTRAINT process_trigger_pkey PRIMARY KEY(process_trigger_id)
);

COMMENT ON TABLE public.process_trigger IS '流程触发额外流程配置';

COMMENT ON COLUMN public.process_trigger.process_trigger_id IS '主键id';
COMMENT ON COLUMN public.process_trigger.process_info_id IS '流程信息表id';
COMMENT ON COLUMN public.process_trigger.master_process_definition_key IS '主流程定义key值';
COMMENT ON COLUMN public.process_trigger.master_process_definition_id IS '主流程定义id值';
COMMENT ON COLUMN public.process_trigger.trigger_type IS '触发配置类型';
COMMENT ON COLUMN public.process_trigger.process_function IS '用户权限/功能';
COMMENT ON COLUMN public.process_trigger.flow_definition_key IS '流程元素key值';
COMMENT ON COLUMN public.process_trigger.slave_form_id IS '触发流程表单id';
COMMENT ON COLUMN public.process_trigger.slave_process_definition_key IS '触发流程key值';


CREATE TABLE public.field_extend_data(
field_extend_data_id serial8 NOT NULL,
form_id int8 NOT NULL,
field_id int8 NOT NULL,
type varchar,
selected bool,
name  varchar,
value varchar,
data_type varchar,
CONSTRAINT field_extend_data_pkey PRIMARY KEY(field_extend_data_id)
);

COMMENT ON TABLE public.field_extend_data IS '列扩展数据表';

COMMENT ON COLUMN public.field_extend_data.field_extend_data_id IS '主键id';
COMMENT ON COLUMN public.field_extend_data.form_id IS '表单id';
COMMENT ON COLUMN public.field_extend_data.field_id IS '字段id';
COMMENT ON COLUMN public.field_extend_data.type IS '数据类型（单选、复选）';
COMMENT ON COLUMN public.field_extend_data.name IS '字段名';
COMMENT ON COLUMN public.field_extend_data.selected IS '是否选中';
COMMENT ON COLUMN public.field_extend_data.value IS '字段值';
COMMENT ON COLUMN public.field_extend_data.data_type IS '数据类型';




CREATE TABLE public.field_extend_rule(
field_extend_rule_id serial8 NOT NULL,
form_id int8 NOT NULL,
field_id int8 NOT NULL,
type varchar,
name  varchar,
CONSTRAINT field__extend_rule_pkey PRIMARY KEY(field_extend_rule_id)
);

COMMENT ON TABLE public.field_extend_rule IS '扩展规则表';
COMMENT ON COLUMN public.field_extend_rule.field_extend_rule_id IS '主键id';
COMMENT ON COLUMN public.field_extend_rule.form_id IS '表单id';
COMMENT ON COLUMN public.field_extend_rule.field_id IS '字段id';
COMMENT ON COLUMN public.field_extend_rule.type IS '规则类型';
COMMENT ON COLUMN public.field_extend_rule.name IS '规则名';



CREATE TABLE public.field_extend_rule_detail(
field_extend_rule_detail_id serial8 NOT NULL,
field_extend_rule_id int8 NOT NULL,
rule varchar,
value  varchar,
CONSTRAINT field_extend_rule_detail_pkey PRIMARY KEY(field_extend_rule_detail_id)
);


COMMENT ON TABLE public.field_extend_rule_detail IS '扩展规则详情表';
COMMENT ON COLUMN public.field_extend_rule_detail.field_extend_rule_detail_id IS '主键id';
COMMENT ON COLUMN public.field_extend_rule_detail.field_extend_rule_id IS '扩展规则表id';
COMMENT ON COLUMN public.field_extend_rule_detail.rule IS '规则';
COMMENT ON COLUMN public.field_extend_rule_detail.value IS '规则限制字段';

ALTER TABLE "public"."field_value" ADD COLUMN "group_id" int8;
COMMENT ON COLUMN public.field_value.group_id IS '组，field_id 下同组数据';
