<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>

        <el-dialog title="注册已有表" :visible.sync="show" :close-on-click-modal="false" custom-class="nopadding">
			 <template slot="title">
				<span class="el-dialog__title">注册已有表</span>
				<button type="button" class="el-dialog__headerbtn minibtn" @click="$store.commit('setMini')" style="right:40px; font-size:14px; color:#909399;">
				<i class="fa" :class="isMini?'fa-expand':'fa-compress'"></i>
				</button>
			</template>
              <el-form  size="small">
                <el-form-item label="数据源：" label-width="120px">
                  <el-select v-model="dsource" placeholder="请选择" style="width:100%">
                    <el-option
                    v-for="item in opts.dss"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    </el-option>
                  </el-select>
                </el-form-item>
				<el-form-item label="目标表：" label-width="120px">
				  <div id="tablestree" style="height: 260px; overflow: auto;"></div>
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
import { baseUrl, ajax } from "@/common/biConfig";
import operationDailog from '@/components/OperationDailog'
import $ from "jquery";

export default {
  data() {
    return {
      show: false,
      opts:{
        dss:[{
			id:-1,name:"BI数据仓"
		}]
	  },
	  dsource:-1
    }
  },
  components: {
	},
  mounted() {
    //this.loadDatas();
  },
  computed: {
	  ...mapState(["isMini"])
  },
  methods: {
	save(){
	  var tableRef = $("#tablestree").jstree(true);
      var node = tableRef.get_selected(true);

      if (node.length == 0 || node[0].li_attr.tp == 'schema') {
        this.$notify.error("请选择目标表");
        return;
      }

      var dsource = this.dsource;
      node = node[0]; //获取当前模式名

      var currSchema = null;

	  const exec = ()=>{
		var p = node.parent; //模式名

		var selSchema = $.trim(p);
		var tname = (currSchema == selSchema ? "" : selSchema + ".") + node.id; //模式名 + . + 表名

		ajax({
			type: "post",
			url: "etl/regSchemaTable.action",
			dataType: "json",
			data: {
				tableName: tname,
				dsource: dsource
			},
			success: (resp)=> {
				if (resp.result == 0) {
					this.$notify.error(resp.msg);
					return;
				}else{
					this.$notify.success("表注册成功！");
					this.show = false;
					this.$parent.loadDatas();
				}

			}
		}, this);
	  }
	  ajax({
        type: "post",
        url: "etl/currSchema.action",
        dataType: "json",
        data: {
          t: Math.random(),
          dsource: dsource
        },
        success: (resp)=> {
		  currSchema = $.trim(resp.rows);
		  exec();
        }
	  });
	},
    newCustom(){
	  this.show = true;
	  this.dsource = -1;
	  //加载数据源
	  ajax({
		type:"GET",
		data:{ },
		url:"etl/listDataSource.action",
		success:(resp)=>{
			this.opts.dss.splice(1, this.opts.dss.length);
			this.opts.dss = this.opts.dss.concat(resp.rows);

			//加载默认表
			this.initTableTree(-1);
		}
	  }, this);
    },

	initTableTree(v){
		let ts = this;
		ajax({
            type: "GET",
            url: "etl/listSchema.action",
            dataType: "json",
            data: {
              dsource: v,
              t: Math.random()
            },
            success: function success(resp) {
				resp = resp.rows;
              //重新加载树
              var ref = $("#tablestree").jstree(true);

              if (ref) {
                ref.destroy();
              }

              var ndt = [];
              $(resp).each(function (a, b) {
                ndt.push({
                  id: b,
                  icon: 'fa fa-database',
                  text: b,
                  li_attr: {
                    tp: "schema"
                  },
                  children: true
                });
              });
              $("#tablestree").jstree({
                core: {
                  check_callback: true,
                  'data': function data(obj, callback) {
                    if (obj.id == '#') {
                      callback.call(this, ndt);
                    } else {
                      ajax({
                        type: "post",
                        url: "etl/listTablesBySchema.action",
                        dataType: "json",
                        data: {
                          id: obj.id,
                          dsource: v
                        },
                        success: function success(r) {
                          callback.call(this, r.rows);
                        }
                      }, ts);
                    }
                  }
                },
                "plugins": ["wholerow"]
              });
            }
          }, this);
	},
  },
  watch: {
	  dsource:function(v){
		  if(v){
			  this.initTableTree(v);
		  }
	  }
  },
};
</script>

<style lang="less" scoped>
@import "../../../style/common";
</style>
