<!--
报表/仪表盘/大屏 资源选择类
需要 column/prop/cb 三个参数
 -->
<template>
    <div>
        <el-input placeholder="录入图片地址" v-model="prop[column]" @change="changevalue(column)" class="input-with-select">
            <el-button slot="append" @click="selectpic()">选择</el-button>
        </el-input>
        <el-dialog title="资源选择器" width="80%" :visible.sync="show" :close-on-click-modal="false" :append-to-body="true" custom-class="nopadding">
            
            <div class="row">
				<div class="col-sm-12">
					<template v-for="item in datas">
					<div class="file-box"  :key="item.id">
						<div class="file">
							<div class="image">
								<a href="javascript:;" @click="selectme(item)"><img class="img-responsive" :src="getUrl(item)"></a>
							</div>
							<div class="file-name">
								<div class="name">{{item.name}}</div>
								
							</div>
						</div>
					</div>
					</template>
				</div>
			</div>
            <el-pagination
                background
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :page-sizes="[10, 20, 50, 100]"
                :current-page="page"
                :page-size="rows"
                layout=" total, sizes, prev, pager, next, jumper"
                :total="total">
            </el-pagination>
            <div style="position: absolute; right: 10px; bottom:55px;">
                <button @click="removeResource()" type="button" class="btn btn-danger btn-rounded btn-outline btn-xs">删除已选资源</button>
            </div>
            
            <div slot="footer" class="dialog-footer">
                <el-button @click="show = false">关 闭</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import {baseUrl, ajax} from '@/common/biConfig'
import $ from 'jquery'
  
export default {
  props:{
      //字段名
     column:{
        type:String,
        required:true,
        default:null
    },
    //属性对象
    prop:{
        type:Object,
        required:true,
        default:null
    },
    //回调函数
    cb:{
        type:Function,
        required:true,
        default:null
    }
  },
  data () {
    return {
        show:false,
        total:0,
		page:1,
        rows:10,
        datas:[]
    }
  },
  computed:{
      
  },
  methods:{
    getUrl(item){
        if(item){
            return baseUrl + 'bigscreen/resource/img/' + item.path+'.action'
        }
        else{
            return "#";
        }
    },
   changevalue(c){
       this.cb(c);
   },
   selectpic(){
       this.loadData();
       this.show = true;
   },
   //选择图片后确定按钮
   save(){
       this.show = false;
   },
   loadData(){
        ajax({
            url:"bigscreen/resource/list.action",
            data:{page:this.page,rows:this.rows},
            type:"get",
            success:(resp)=>{
                this.datas = resp.rows;
                this.total = resp.total;
            }
        });
    },
     handleSizeChange(v){
        this.rows = v;
		this.loadData();
    },
    handleCurrentChange(v){
        this.page = v;
		this.loadData();
    },
    selectme(p){
        this.show = false;
        var v = "resource/img/" +  p.path+'.action';
        this.prop[this.column] = v;
        this.cb(this.column, v);
    },
    removeResource(){
        this.show = false;
        this.prop[this.column] = null;
        this.cb(this.column, null);
    }
  }
}
</script>

<style scoped lang="less">
.file-box {
    float: left;
    width: 160px;
}
.file {
    border: 1px solid #e7eaec;
    padding: 0;
    background-color: #ffffff;
    position: relative;
    margin-bottom: 10px;
    margin-right: 10px;
    .image {
        height: 100px;
        overflow: hidden;
    }
    .file-name {
        padding: 10px;
        background-color: #f8f8f8;
        border-top: 1px solid #e7eaec;
        small {
            color: #676a6c;
        }
        .name {
            overflow: hidden;
            white-space: nowrap;
            width: 100%;
            text-overflow: ellipsis;
        }
    }
}
</style>
