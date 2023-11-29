/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.etl;

import com.ruisitech.bi.entity.etl.DataSource;
import com.ruisitech.bi.service.etl.DataSourceService;
import com.ruisitech.bi.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/etl")
public class DataSourceController extends BaseController {

	@Autowired
	private DataSourceService dsService;

	@RequestMapping(value="/listDataSource.action")
	public @ResponseBody
    Object list(){
		return super.buildSucces(dsService.listDataSource());
	}

	@RequestMapping(value="/deleteDataSource.action")
	public @ResponseBody
    Object delete(Integer id){
		DataSource ds = new DataSource();
		ds.setId(id);
		dsService.deleteDataSource(ds);
		return this.buildSucces();
	}
	@RequestMapping(value="/testDataSource.action", method = RequestMethod.POST)
	public @ResponseBody
    Object test(DataSource ds){
		try{
			return dsService.testDataSource(ds);
		}catch(Exception ex){
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/saveDataSource.action", method = RequestMethod.POST)
	public @ResponseBody
    Object save(DataSource ds){
		try{
			dsService.insertDataSource(ds);
			return this.buildSucces();
		}catch(Exception ex){
			ex.printStackTrace();
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/updateDataSource.action", method = RequestMethod.POST)
	public @ResponseBody
    Object update(DataSource ds){
		try{
			dsService.updateDataSource(ds);
			return this.buildSucces();
		}catch(Exception ex){
			ex.printStackTrace();
			return super.buildError(ex.getMessage());
		}
	}

	@RequestMapping(value="/getDataSource.action")
	public @ResponseBody
    Object get(Integer id){
		DataSource ds = dsService.selectDataSourceByPrimaryKey(id);
		return super.buildSucces(ds);
	}

}
