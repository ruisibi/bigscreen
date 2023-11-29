<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
    <el-dialog title="指标预警" :visible.sync="show" :append-to-body="true" :close-on-click-modal="false" custom-class="nopadding">
      <template slot="title">
          <span class="el-dialog__title">指标预警</span>
          <button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
          <i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
          </button>
        </template>
	  <el-form ref="tableForm" :model="val" size="mini">
         <el-form-item label="图片样式" label-width="160px">
					<el-select v-model="val.wctype" placeholder="请选择" @change="chgpic">
						<el-option
							v-for="item in opt.wctypes"
							:key="item.value"
							:label="item.text"
							:value="item.value"
						>
						</el-option>
					</el-select>
					<el-switch
						v-model="val.fztp"
						inactive-text="反转图片"
						@change="fztpfunc"
						>
					</el-switch>
				</el-form-item>
				<el-form-item label="" label-width="160px">
					<span slot="label">
						<span id="ww1" class="warning6"></span>
						当前值
					</span>
					<el-select v-model="val.logic1" style="width:100px;" placeholder="请选择" >
						<el-option
							v-for="item in opt.logics"
							:key="item.value"
							:label="item.value"
							:value="item.value"
						>
						</el-option>
					</el-select>
					<el-input-number v-model="val.value1" controls-position="right" @change="handleChange(1)" size="small"></el-input-number>
				</el-form-item>
				<el-form-item label-width="160px">
					<span slot="label">
						<span id="ww2" class="warning5">
						</span> 当前值 &lt; <span id="and1"></span> 且
					</span>
					<el-select v-model="val.logic2" style="width:100px;" placeholder="请选择" >
						<el-option
							v-for="item in opt.logics"
							:key="item.value"
							:label="item.value"
							:value="item.value"
						>
						</el-option>
					</el-select>
					<el-input-number v-model="val.value2" controls-position="right" @change="handleChange(2)" size="small"></el-input-number>
				</el-form-item>
				<el-form-item label-width="160px">
					<span slot="label">
						<span id="ww3" class="warning4"></span>
						当前值 &lt; <span id="and2"></span>

					</span>
				</el-form-item>
        <el-form-item label-width="160px">
				  <button type="button" class="btn btn-danger btn-rounded btn-outline btn-xs" @click="clearwarning">清除预警</button>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="save()">确 定</el-button>
        <el-button @click="show = false">取 消</el-button>
      </div>
    </el-dialog>
</template>

<script>
import { mapState } from "vuex";
import {baseUrl} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'

export default {
  components:{

  },
  props:{

  },
  data(){
    return {
      show:false,
      val:{
          value1:null,
					value2:null,
					wctype:"1",
					logic1:">=",
					logic2:">=",
					fztp:false,
      },
      kpi:null,
      comp:null,
      opt:{
        oper:[
          {value:">", text:"大于"},
          {value:"<", text:"小于"},
          {value:"=", text:"等于"},
          {value:"between", text:"区间"}
        ],
        wctypes:[{value:"1",text:"交通灯"},{value:"2",text:"箭头"}],
        logics:[{value:">="},{value:">"}]
      }
    }
  },
  mounted(){

  },
  computed: {
      ...mapState(["isMini"])
  },
  methods: {
    openDailog(node, comp){
      this.show = true;
      this.comp = comp;
      this.kpi = comp.kpiJson.filter(m=>m.kpi_id === node.match)[0];
      let kpi = this.kpi;
      if(kpi.warning){
					this.val.wctype = kpi.warning.pictype;
					this.val.fztp === kpi.warning.reverse === "y";
					this.val.logic1 = kpi.warning.logic1;
					this.val.value1 = kpi.warning.val1;
					this.val.logic2 = kpi.warning.logic2;
					this.val.value2 = kpi.warning.val2;
				}
    },
    save(){
      this.show = false;
      var pictype = this.val.wctype;
      var reverse = this.val.fztp?"y":"n";
      var logic1 = this.val.logic1;
      var val1 = this.val.value1;
      var logic2 = this.val.logic2;
      var val2 = this.val.value2;
      var pic1 = $("#ww1").attr("class");
      var pic2 = $("#ww2").attr("class");
      var pic3 = $("#ww3").attr("class");
      this.kpi.warning = {pictype:pictype,reverse:reverse,logic1:logic1,val1:val1,logic2:logic2,val2:val2,pic1:pic1,pic2:pic2,pic3:pic3}
      let p = this.$parent;
      p.setUpdate();
      p.tableView();
    },
    clearwarning(){
        delete this.kpi.warning;
				this.show = false;
				let o = this.$parent;
				o.setUpdate();
				o.tableView();
    },
    chgpic(){
				if(this.val.wctype == 1){
					$("#ww1").attr("class", "warning6");
					$("#ww2").attr("class", "warning5");
					$("#ww3").attr("class", "warning4");
				}else if(this.val.wctype == 2){
					$("#ww1").attr("class", "warning3");
					$("#ww2").attr("class", "warning2");
					$("#ww3").attr("class", "warning1");
				}
			},
			fztpfunc(){
				if(this.val.fztp === true){
					if(this.val.wctype == 1){
						$("#ww1").attr("class", "warning4");
						//$("#ww2").attr("class", "warning5");
						$("#ww3").attr("class", "warning6");
					}else if(this.val.wctype == 2){
						$("#ww1").attr("class", "warning1");
						//$("#ww2").attr("class", "warning2");
						$("#ww3").attr("class", "warning3");
					}
				}else{
					if(this.val.wctype == 1){
						$("#ww1").attr("class", "warning6");
						//$("#ww2").attr("class", "warning5");
						$("#ww3").attr("class", "warning4");
					}else if(this.val.wctype == 2){
						$("#ww1").attr("class", "warning3");
						//$("#ww2").attr("class", "warning2");
						$("#ww3").attr("class", "warning1");
					}
				}
      },
      handleChange(idx){
				$("#and" + idx).text(idx === 1?this.val.value1:this.val.value2);
			},
  }
}
</script>
