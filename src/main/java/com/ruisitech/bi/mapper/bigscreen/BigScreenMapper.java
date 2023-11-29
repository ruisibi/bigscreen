/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.mapper.bigscreen;

import com.ruisitech.bi.entity.bigscreen.BigScreen;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BigScreenMapper {
    int deleteByPrimaryKey(@Param("id") Integer id);

    int insertSelective(BigScreen record);

    BigScreen selectByPrimaryKey(@Param("id") Integer id);

    int updateByPrimaryKeySelective(BigScreen record);

    List<BigScreen> list( @Param("userId") Integer userId, @Param("search") String search);

    Integer maxBigScreenId();

    /**
     * 查询大屏模版列表
     * @return
     */
    List<BigScreen> queryTemplate( );
}
