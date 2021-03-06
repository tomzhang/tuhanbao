ALTER TABLE T_PAYMENT_COLLECTION MODIFY COLUMN PAYMENT_COLLECTION_DATE DATETIME DEFAULT NULL COMMENT "回款日";
ALTER TABLE T_PAYMENT_COLLECTION DROP COLUMN INVEST_ID;
ALTER TABLE T_PAYMENT_COLLECTION DROP COLUMN INVEST_REC_ID;
ALTER TABLE T_PAYMENT_COLLECTION DROP COLUMN CUSTOM_NAME;
ALTER TABLE T_PAYMENT_COLLECTION DROP COLUMN BACK_TIME_PLAN;
ALTER TABLE T_PAYMENT_COLLECTION DROP COLUMN INVEST_TIME;
ALTER TABLE T_PAYMENT_COLLECTION DROP COLUMN PAYMENT_TYPE;
ALTER TABLE T_PAYMENT_COLLECTION DROP COLUMN PROJECT_LIMIT;
ALTER TABLE T_PAYMENT_COLLECTION DROP COLUMN PAYED_CAPITAL;
ALTER TABLE T_PAYMENT_COLLECTION DROP COLUMN PAYED_PROFIT;
ALTER TABLE T_PAYMENT_COLLECTION DROP COLUMN PAYING_CAPITAL;
ALTER TABLE T_PAYMENT_COLLECTION DROP COLUMN PAYING_PROFIT;
ALTER TABLE T_PERMISSION DROP COLUMN HTML;
ALTER TABLE T_VIP_INVEST_FLOW MODIFY COLUMN DISCOUNT_PERSON_INVITATION_GROUP VARCHAR(11) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT "优惠码所属人邀请人组别";
ALTER TABLE T_VIP_INVEST_FLOW DROP COLUMN INVEST_ID;
ALTER TABLE T_VIP_INVEST_FLOW DROP COLUMN REG_INVI_CODE;
ALTER TABLE T_VIP_INVEST_FLOW DROP COLUMN DATE;
ALTER TABLE T_VIP_INVEST_FLOW DROP COLUMN TEAM_ID;
ALTER TABLE T_VIP_INVEST_FLOW DROP COLUMN SITE_ID;
ALTER TABLE T_VIP_INVEST_FLOW DROP COLUMN SITE_NAME;
ALTER TABLE T_VIP_INVEST_FLOW DROP COLUMN INV_PRO_TITLE;
ALTER TABLE T_VIP_INVEST_FLOW DROP COLUMN REG_TEAM_ID;
ALTER TABLE T_VIP_INVEST_FLOW DROP COLUMN INV_PER_TEAM;
ALTER TABLE T_VIP_INVEST_FLOW DROP COLUMN IS_FIRST_INV;
ALTER TABLE T_VIP_INVEST_INFO ADD COLUMN OFF_DAY INT COMMENT "连续未投资天数" after DAILY_DECREASEMENT;
ALTER TABLE T_VIP_INVEST_INFO ADD COLUMN VIP_TYPE INT COMMENT "客户类型：1为长线，2为短线，3为中线，4为中长线" after RANK;
ALTER TABLE T_VIP_INVEST_INFO DROP COLUMN DEAD_LINE;
ALTER TABLE T_VIP_INVEST_INFO DROP COLUMN DAY_ADD_INVEST_TIMES;
ALTER TABLE T_VIP_MSG_SENDING_HISTORY DROP COLUMN SEND_STATUS;
ALTER TABLE T_VIP_PRIMARY_INFO MODIFY COLUMN VIP_START DATETIME DEFAULT NULL COMMENT "首次导入客户数据，用户成为理财VIP时间";
ALTER TABLE T_VIP_PRIMARY_INFO DROP COLUMN REMARK;
ALTER TABLE T_VIP_PRIMARY_INFO DROP COLUMN OFF_DAY;
drop table T_HTTP_LOG;
drop table T_VIP_TYPE_INFO;
