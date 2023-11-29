/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.ext.service;

import com.rsbi.ext.engine.cross.OlapWriterInterface;
import com.rsbi.ext.engine.view.context.cross.CrossReportContext;
import com.rsbi.ext.engine.wrapper.ExtRequest;
import com.rsbi.ext.engine.wrapper.ExtWriter;
import com.ruisitech.bi.entity.bireport.DimDto;
import com.ruisitech.bi.entity.bireport.TableQueryDto;

import java.util.List;

public class UserDefinedOlapWriter implements OlapWriterInterface {

	private TableQueryDto table;

	@Override
	public void wirteRowDims(ExtRequest request, ExtWriter out, CrossReportContext report) {
		table = (TableQueryDto)request.getAttribute("table");

		out.print("<div class='rowDimsList'>");
		out.print("<table class=\"grid5\" cellpadding=\"0\" cellspacing=\"0\">");
		out.print("<tr>");
		List<DimDto> rows = table.getRows();
		for(int i=0; i<rows.size(); i++){
			DimDto row  = rows.get(i);
			Integer id = row.getId();
			String name = row.getDimdesc();
			out.print("<th>");
			out.print("<span>"+name+" <a href=javascript:; cid=\""+id+"\" cpos=\"row\" cname=\""+name+"\" onclick='setDimInfo()' class='tableDimOpt dimoptbtn set'> &nbsp; </a></span>");
			out.print("</th>");
		}
		out.print("</tr>");
		out.print("</table>");
		out.println("</div>");
	}

	@Override
	public void writeColDims(ExtRequest request, ExtWriter out, CrossReportContext report) {
		table = (TableQueryDto)request.getAttribute("table");

		out.print("<div class='colDimsList'>");
		List<DimDto> cols = table.getCols();
		if(cols.size() <= 0){
			out.print(" <div style=\"margin:3px;color:#999999;font-size:13px;\">列标签区域</div> ");
		}else{
			for(int i=0; i<cols.size(); i++){
				DimDto col  = cols.get(i);
				Integer id = col.getId();
				String name = col.getDimdesc();
				out.print("<span>"+name+" <a href=javascript:; cid=\""+id+"\" cpos='col' cname=\'"+name+"\' onclick='setDimInfo()' class='tableDimOpt dimoptbtn set'> &nbsp; </a></span>");
			}
		}

		out.println("</div>");
	}

}
