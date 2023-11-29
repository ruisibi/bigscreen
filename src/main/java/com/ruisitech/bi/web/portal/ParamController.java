/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.hongwangweb.com/licenses/index.html
 */
package com.ruisitech.bi.web.portal;

import com.ruisitech.bi.entity.bireport.DimDto;
import com.ruisitech.bi.entity.bireport.ParamDto;
import com.ruisitech.bi.entity.model.TableDimension;
import com.ruisitech.bi.service.model.TableDimensionService;
import com.ruisitech.bi.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ParamController
 * @Description ParamController
 * @Author huangqin
 * @Date 2020/12/2 10:25 上午
 */
@Controller
@RequestMapping(value = "/portal")
public class ParamController extends BaseController {

    @Autowired
    private TableDimensionService dimService;

    @RequestMapping(value={"/queryDims.action", "/share/queryDims.action"})
    public @ResponseBody
    Object queryDims(Integer tableId){
        List dims = dimService.getTableDims(tableId);
        //不显示层级维度
        List<DimDto> ret = new ArrayList<DimDto>();
        for(int i=0; i<dims.size(); i++) {
            DimDto dim = (DimDto)dims.get(i);
            if(!"y".equals(dim.getIspcdim())){
                ret.add(dim);
            }
        }
        return super.buildSucces(ret);
    }

    @RequestMapping(value={"/paramSearch2Json.action", "/share/paramSearch2Json.action"})
    public @ResponseBody
    Object paramSearch2Json(ParamDto param, String parentJSON, String keyword) throws Exception{
        Map<String, Object> ret = new HashMap<String, Object>();
        TableDimension d = dimService.queryDimCol(param.getId(), param.getTid());
        List<Object> ls = dimService.paramFilter(d, parentJSON, keyword, param.getTid());
        ret.put("type", d.getType());
        if(ls.size() == 0) {
            return ret;
        }
        if(d.getType().equals("month") || d.getType().equals("day")){
            Map first = (Map)ls.get(0);
            Map end = (Map)ls.get(ls.size() - 1);
            ret.put("min",  first.get("id"));
            ret.put("max", end.get("id"));
            ret.put("dateformat", d.getDateformat());
        }else {
            ret.put("datas", ls);
        }
        return super.buildSucces(ret);
    }
}
