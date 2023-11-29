/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.portal;

import com.rsbi.ext.engine.ExtConstants;
import com.rsbi.ext.engine.init.TemplateManager;
import com.rsbi.ext.engine.util.IdCreater;
import com.rsbi.ext.engine.view.context.Element;
import com.rsbi.ext.engine.view.context.MVContext;
import com.rsbi.ext.engine.view.context.MVContextImpl;
import com.rsbi.ext.engine.view.context.dc.grid.GridDataCenterContext;
import com.rsbi.ext.engine.view.context.dc.grid.GridDataCenterContextImpl;
import com.rsbi.ext.engine.view.context.dc.grid.GridSetConfContext;
import com.rsbi.ext.engine.view.context.detail.DetailColContext;
import com.rsbi.ext.engine.view.context.detail.DetailReportContext;
import com.rsbi.ext.engine.view.context.detail.DetalReportContextImpl;
import com.rsbi.ext.engine.view.context.form.InputField;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.portal.CompParamDto;
import com.ruisitech.bi.entity.portal.DetailColDto;
import com.ruisitech.bi.entity.portal.DetailReportDto;
import com.ruisitech.bi.service.bireport.BaseCompService;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.QueryRestService;
import com.ruisitech.bi.service.model.DatasetService;
import com.ruisitech.bi.util.RSBIUtils;
import com.ruisitech.ext.service.DataControlInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName DetailReportService
 * @Description DetailReportService
 * @Author hq
 * @Date 2020/5/17 11:44
 */
@Service
@Scope("prototype")
public class DetailReportService extends BaseCompService {

    public static String deftMvId = "mv.portal.detail";

    private Map<String, InputField> mvParams = new HashMap<String, InputField>(); //mv的参数

    @Autowired
    private DataControlInterface dataControl; //数据权限控制

    @Autowired
    private TableCacheService cacheService;

    @Autowired
    private QueryRestService restService;

    @Autowired
    private DatasetService datasetService;

    public MVContext json2MV(DetailReportDto detail) throws Exception{
        TableInfoVO tvo = null;
        if(detail.getTid() != null) {
            tvo = cacheService.getTableInfo(detail.getTid());
            detail.checkSql(tvo);
        }
        //创建MV
        MVContext mv = new MVContextImpl();
        mv.setChildren(new ArrayList<Element>());
        String formId = ExtConstants.formIdPrefix + IdCreater.create();
        mv.setFormId(formId);
        mv.setMvid(deftMvId);
        mv.setHideMV(true);

        //处理参数,把参数设为hidden
        parserHiddenParam(detail.getPortalParams(), mv, mvParams, detail.getUseIn());
        //创建明细表对象
        DetailReportContext ctx = join2DetailReport(detail, tvo);
        if(ctx == null){
            return mv;
        }
        mv.getChildren().add(ctx);
        ctx.setParent(mv);

        //创建数据sql
        String sql = this.createSql(detail);
        GridDataCenterContext dc = this.createDataCenter(detail, sql);
        ctx.setRefDataCenter(dc.getId());
        if(mv.getGridDataCenters() == null){
            mv.setGridDataCenters(new HashMap<>());
        }
        mv.getGridDataCenters().put(dc.getId(), dc);

        //处理scripts
        String script = createBaseJSFunction();
        if(this.scripts != null){
            script += this.scripts;
        }
        mv.setScripts(script);
        if(tvo != null) {
            super.createDsource(tvo.getDsource(), mv);
        }
        return mv;
    }


    /**
    *
    * @param detail 明细封装对象
    * @return: com.ruisi.ext.engine.view.context.detail.DetailReportContext
    */
    public DetailReportContext join2DetailReport(DetailReportDto detail, TableInfoVO tvo){
        if(detail.getCols() == null){
            return null;
        }
        DetailReportContext report = new DetalReportContextImpl();
        report.setId(detail.getId());
        report.setColSize(detail.getColSize());
        List<DetailColContext> cols = detail.getCols().stream().map(c ->{
            DetailColContext col = new DetailColContext();
            col.setDesc(c.getDispName() == null || c.getDispName().length() == 0 ? c.getName():c.getDispName());
            if(tvo.isRegEsTable() && tvo.isEsKeyword(c.getName())){
                col.setAlias(c.getName().replaceAll(".keyword", ""));
            }else {
                col.setAlias(c.getName());
            }
            col.setFormat(c.getFmt());
            col.setAlign(c.getAlign());
            if(c.getCode() != null && c.getCode().length() > 0){
                String code = RSBIUtils.unescape(c.getCode());
                String fun = "function "+c.getFuncname()+"(value, row){"+code+"}\n";
                this.scripts.append(fun);
                col.setJsFunc(c.getFuncname());
            }
            return col;
        }).collect(Collectors.toList());
        report.setCols(cols);
        report.setExportType(detail.getExportType());
        return report;
    }

    public String createSql(DetailReportDto detail){
        TableInfoVO tvo = this.cacheService.getTableInfo(detail.getTid());
        if(tvo.isRest()) {
            return super.createRestSql(detail.getCompParams(),  detail.getLinkAccept(), tvo);
        }
        StringBuffer sb = new StringBuffer("select ");
        List<DetailColDto> cols = detail.getCols();
        for(int i=0; i<cols.size(); i++){
            DetailColDto col = cols.get(i);
            String name = col.getName();
            String expression = col.getExpression();  //表达式字段
            if(expression != null && expression.length() > 0){
                sb.append(" "+ expression + " as " + name);
            }else if("Datetime".equalsIgnoreCase(col.getType())){  //es需要格式化
                if(tvo.isEs()) {
                    sb.append("date_format("+name+",'yyyy-MM-dd HH:mm:ss')  as "+ name);
                }else {
                    sb.append(" " + name + " as " + name);
                }
            }else if(tvo.isRegEsTable() && tvo.isEsKeyword(col.getName())){
                String n = name.replaceAll(".keyword", "");
                sb.append(" " + n + " as " + n);
            }else{
                sb.append(" " + name + " as " + name);
            }
            if(i != cols.size() - 1){
                sb.append(",");
            }
        }

        sb.append(" from ");

        sb.append(datasetService.createTableSql(tvo, false));

        sb.append(" where 1=1 ");
        //数据权限
        if(dataControl != null){
            String ret = dataControl.process(RSBIUtils.getLoginUserInfo(), tvo);
            if(ret != null){
                sb.append(ret);
            }
        }

        //添加参数筛选
        List<CompParamDto> pageParams = detail.getCompParams();
        String condition = super.dealCubeParams(pageParams, detail.getUseIn(),"dim", tvo);
        if("ds".equals(tvo.getIncome()) && sb.indexOf(DatasetService.cond)  >= 0) {
            sb = new StringBuffer(datasetService.replaceSql(sb.toString(), condition));  //替换参数到数据集中
        }else{
            sb.append(condition);
        }
        sb.append(super.dealDashboardParams(detail.getDashboardParam(), tvo));
        if("dashboard".equals(detail.getUseIn())) {  //处理仪表盘事件接收
            super.dealLinkAccept(detail.getLinkAccept(), sb);
        }
        return sb.toString();
    }

    public GridDataCenterContext createDataCenter(DetailReportDto detail, String sql) throws IOException {
        GridDataCenterContext ctx = new GridDataCenterContextImpl();
        GridSetConfContext conf = new GridSetConfContext();
        //判断是否通过 elasticsearch 查询
        TableInfoVO tinfo = cacheService.getTableInfo(detail.getTid());
        if(tinfo.isRest()) {
            conf.setMaster(String.valueOf(detail.getTid()));
            conf.setDatasetProvider(restService);
        }
        //明细数据不缓存
        //conf.setCache(dcCacheService);
        if (!(tinfo.getDsourceId() == null || tinfo.getDsourceId() == -1)) {
            conf.setRefDsource("ds-" + tinfo.getDsourceId());
        }
        conf.setUseCache(false);
        ctx.setConf(conf);
        ctx.setId("DC-" + IdCreater.create());
        String name = TemplateManager.getInstance().createTemplate(sql);
        ctx.getConf().setTemplateName(name);
        return ctx;
    }

    public Map<String, InputField> getMvParams() {
        return mvParams;
    }
}
