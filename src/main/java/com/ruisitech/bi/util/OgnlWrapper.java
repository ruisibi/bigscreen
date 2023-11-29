/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.util;

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;

public class OgnlWrapper {

    private static Logger logger = LogManager.getLogger(OgnlWrapper.class);

    private static ObjectMapper om = new ObjectMapper();
    private Map<String, Object> payload;

    public OgnlWrapper(Map<String, Object> playload) {
        Validate.notEmpty(playload, "can not construct with none playload!");
        this.payload = playload;
    }


    public OgnlWrapper(Object playload) {
        this.payload = om.convertValue(playload, Map.class);
    }

    public <T> T get(String expression) {
        try {
            return (T) Ognl.getValue(expression, this.payload);
        } catch (OgnlException e) {
            logger.trace(String.format("get value with expression:[%s] due to error, return null instead of",
                expression), e);
            return null;
        }
    }

    public Long getLong(String expression) {
        try {
            Object obj = Ognl.getValue(expression, this.payload);
            if (null == obj)
                return null;
            try {
                return Long.parseLong(obj.toString());
            } catch (NumberFormatException nfe) {
                logger.warn(String.format("get value with expression:[%s] due to error, return null. value[%s] cannot be cast to java.lang.Long",
                    expression,
                    obj.toString()));
                return null;
            }
        } catch (OgnlException e) {
            logger.trace(String.format("get value with expression:[%s] due to error, return null instead of",
                expression), e);
            return null;
        }
    }

    public Integer getInt(String expression) {
        try {
            Object obj = Ognl.getValue(expression, this.payload);
            if (null == obj)
                return null;
            try {
                return Integer.parseInt(obj.toString());
            } catch (NumberFormatException nfe) {
                logger.warn(String.format("get value with expression:[%s] due to error, return null. value[%s] cannot be cast to java.lang.Integer",
                    expression,
                    obj.toString()));
                return null;
            }
        } catch (OgnlException e) {
            logger.trace(String.format("get value with expression:[%s] due to error, return null instead of",
                expression), e);
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("OgnlWrapper[%s]", this.payload.toString());
    }

//    public static void main(String[] args) {
//        String json = "{\"user\":{\"name\":\"123\",\"depart\":[1,2]}}";
//        Map<?, ?> map = JSONObject.parseObject(json, Map.class);
//        OgnlWrapper ognlWrapper = new OgnlWrapper(map);
//        logger.info(ognlWrapper.get("user.name"));
//        logger.info(ognlWrapper.get("user.depart.size").toString());
//        logger.info(ognlWrapper.get("user.depart[0]").toString());
//    }
}
