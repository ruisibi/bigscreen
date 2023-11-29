/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.bigscreen;

import com.ruisitech.bi.entity.bigscreen.Resource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName ResourceMapper
 * @Description ResourceMapper
 * @Author huangqin
 * @Date 2021/6/7 11:53 上午
 */
public interface ResourceMapper {

    int deleteByPrimaryKey(@Param("id") String id);

    int insertSelective(Resource record);

    Resource selectByPrimaryKey(@Param("id") String id);

    List<Resource> list(  @Param("userId") Integer userId);

    List<Resource> listByIds(@Param("ids") List<String> ids);

    List<Resource> listByPaths(@Param("paths") List<String> paths);
}
