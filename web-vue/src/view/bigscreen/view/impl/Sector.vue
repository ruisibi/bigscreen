<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 扇形图 （sector） 自定义 组件， 采用 canvas 实现 -->
<script>
import {baseUrl, ajax, formatDate} from '@/common/biConfig'
import {formatNumber} from '@/common/echartsUtils'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import { Loading } from 'element-ui'
import * as dutils from '@/view/dashboard/Utils'

export default {
  components:{
  },
  data(){
    return {
      data:null,
      canvas: null,
    }
  },
  props:{
     pageInfo:{
        type:Object,
        required:false,
         default:()=>{}
      },
      comp:{
        type:Object,
        required:true,
        default:{}
      },

      editor:{
        type:Boolean,
        required:true,
        default:true
      },
      portalParams:{
        type:Array,
        required:false
      },
      //在哪里使用report/dashboard/bigscreen
      useIn:{
        type:String,
        required:true,
      },
      token:{
          type:String,
          required: false
      }
  },
  render(h){
    let ts = this;
    let comp = this.comp;
    let s = {width:"100%", height:"100%"};
    return h('canvas', {domProps:{id:"can_"+this.comp.id, width:"260", height:"120"}});
  },
  mounted(){
    this.printChart();
  },
  computed: {
  },
  methods: {
    //数据更新后，自动刷新组件通用方法
    refreshData(data){
      this.data = data;
      this.printChart();
    },
    //属性改变调用的方法
    propChage(){
      this.printChart();
    },
    //调整组件大小后调用的通用方法 (公共方法，实现类都需要实现)
    resize(){

    },

    printChart(){

       let comp = this.comp;
        let canvas = document.getElementById("can_"+comp.id);
        this.canvas = canvas;
        let ctx = canvas.getContext("2d");

         //ctx.rotate(0, 0);

        ctx.clearRect(0,0,canvas.width,canvas.height);


        let val = "50%";
        let trueValue = 50;
        if(this.data){
         trueValue = this.data[0][comp.comp.kpi.alias];  //此处只支持百分比
         let fmt = "0%"
         val = formatNumber(trueValue, fmt);
         trueValue *= 100;
        }

        ctx.fillStyle = "#2c38b9";
        if(comp.style && comp.style.sectorBgcolor){
          ctx.fillStyle = comp.style.sectorBgcolor;
        }

        //绘制圆弧
        ctx.beginPath();
        ctx.arc(canvas.width/2, canvas.height + 20, 120,   Math.PI * 1.2, Math.PI * 1.8, false);
        ctx.arc(canvas.width/2, canvas.height + 20, 75,   Math.PI * 1.8, Math.PI * 1.2, true);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();


       //值换算成 0 到 60 区间
       let rote = 60 * trueValue / 100 / 100;
       if(rote >= 0.6){
         rote = 0.6;
       }
        //绘制第二个人圆弧
        let colors = ["#264bf3", "#2e69c3", "#6dcde6"];
        if(comp.style && comp.style.sectorKpicolor){
          colors = comp.style.sectorKpicolor.split("@");
        }
        var grad = ctx.createLinearGradient(-10, 0, 120, 0);
        grad.addColorStop(0, colors[0]);
        grad.addColorStop(0.5, colors[1]);
        grad.addColorStop(1, colors[2]);
        ctx.fillStyle = grad;
        ctx.beginPath();
        ctx.arc(canvas.width/2, canvas.height + 20, 120,   Math.PI * 1.2, Math.PI * (1.2 + rote), false);  //(1.2 + rote)
        ctx.arc(canvas.width/2, canvas.height + 20, 75,   Math.PI * (1.2 + rote), Math.PI * 1.2, true);
        ctx.closePath();
        ctx.fill();

        //绘制指标值
        ctx.rotate(-0.2);
        ctx.fillStyle = "white";
        ctx.font = "28px sans-serif";
        ctx.beginPath();
        ctx.fillText(val, canvas.width / 2 - 60, 80);
        ctx.closePath();

        ctx.rotate(0.2); //把旋转设置回来
			},

  },
  watch: {

  },
  beforeDestroy(){
    if(this.canvas){
      let ctx = this.canvas.getContext("2d");
      ctx.clearRect(0,0,this.canvas.width,this.canvas.height);
    }
  },
}
</script>
