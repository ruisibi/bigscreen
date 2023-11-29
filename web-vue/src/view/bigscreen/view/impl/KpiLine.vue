<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 指标引导线 （kpiLine） 自定义 组件 -->
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
      inter: null,  //循环对象
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
    return h('canvas', {domProps:{id:"can_"+this.comp.id, width:"400", height:"120"}});
  },
  mounted(){
    this.printLines();
  },
  computed: {
  },
  methods: {

    //数据更新后，自动刷新组件通用方法 (公共方法，实现类都需要实现)
    refreshData(data){
      this.data = data;
    },

    //调整组件大小后调用的通用方法 (公共方法，实现类都需要实现)
    resize(){

    },

     //属性改变调用的方法  (公共方法，实现类都需要实现)
    propChage(){

    },

    drawBase(ctx, canvas, startw, useMask){
      let comp = this.comp;
      let linecolor = "#e1e2ea";
      if(comp.style && comp.style.kpiLinelinecolor){
        linecolor = comp.style.kpiLinelinecolor;
      }
      let reverse = false;  //反转控制
      if(comp.style && comp.style.kpiLineReverse){
        reverse = comp.style.kpiLineReverse;
      }
        ctx.strokeStyle = linecolor;
				ctx.lineWidth = 2;
        ctx.beginPath();
        if(reverse == false){
				  ctx.moveTo(20, 110);
          ctx.lineTo(80, 30);
          ctx.lineTo(230, 30);
        }else{
          ctx.moveTo(380, 110);
          ctx.lineTo(320, 30);
          ctx.lineTo(170, 30);
        }
				ctx.stroke();
				ctx.closePath();

				ctx.fillStyle = linecolor;
        ctx.beginPath();
        if(reverse == false){
          ctx.arc(230, 30, 5, 0, 2 * Math.PI);
        }else{
          ctx.arc(170, 30, 5, 0, 2 * Math.PI);
        }
				ctx.fill();
				ctx.closePath();

				ctx.font = "16px sans-serif";
        ctx.beginPath();
        if(reverse == false){
          ctx.fillText(comp.style && comp.style.kpiLinekpiname?comp.style.kpiLinekpiname:"未定义名称", 245, 35);
        }else{
          ctx.fillText(comp.style && comp.style.kpiLinekpiname?comp.style.kpiLinekpiname:"未定义名称", 165, 55);
        }
				ctx.fill();
				ctx.font = "32px sans-serif";
        ctx.fillStyle = comp.style && comp.style.kpiLinekpicolor? comp.style.kpiLinekpicolor: "#6ecde5";
        let kpi = "0";
        if(this.data){
         kpi = this.data[0][comp.comp.kpi.alias] + "";
         let fmt = comp.style && comp.style.kpiLinefmt?comp.style.kpiLinefmt:"0";
         kpi = formatNumber(kpi, fmt);
        }
        if(reverse == false){
          ctx.fillText(kpi, 245, 70);
        }else{
          ctx.fillText(kpi, 165, 90);
        }
				ctx.fill();
				ctx.closePath();

        if(useMask){
          ctx.globalCompositeOperation = "destination-in";
          ctx.beginPath();
          ctx.fillStyle = "red";
          if(reverse == false){
             ctx.fillRect(0, 0, startw, canvas.height);
          }else{
             ctx.fillRect(startw, 0, canvas.width, canvas.height);
          }

          ctx.closePath();
        }
    },

    printLines(){
       let comp = this.comp;
        let canvas = document.getElementById("can_"+comp.id);
        this.canvas = canvas;
        let ctx = canvas.getContext("2d");

        let reverse = false;  //反转控制
        if(comp.style && comp.style.kpiLineReverse){
          reverse = comp.style.kpiLineReverse;
        }
        let startw = 10;
        if(reverse){
          startw = canvas.width - 10;
        }

				this.drawBase(ctx, canvas, startw, true);
				const draw = ()=>{



              let canvas = document.getElementById("can_"+comp.id);
              if(canvas == null){
                return;
              }
							let ctx = canvas.getContext("2d");

							ctx.globalCompositeOperation = "source-over";

							ctx.clearRect(0,0,canvas.width,canvas.height);

              if(reverse == false){
                startw += 3;  //控制速度
                if(startw >= canvas.width + 350){
                  startw = 1;
                }
                if(startw < canvas.width){
                  this.drawBase(ctx, canvas, startw, true);
                }else{
                  this.drawBase(ctx, canvas, startw, false);
                }
              }else{
                startw -= 3;  //控制速度
                if(startw <= -350){
                  startw = canvas.width - 10;
                }
                if(startw < 0){
                  this.drawBase(ctx, canvas, startw, false);
                }else{
                  this.drawBase(ctx, canvas, startw, true);
                }
              }



					this.inter = requestAnimationFrame(draw);
				}
				draw();
			},

  },
  watch: {

  },
  beforeDestroy(){
    if(this.canvas){
      let ctx = this.canvas.getContext("2d");
      ctx.clearRect(0,0,this.canvas.width,this.canvas.height);
    }
    if(this.inter){
      window.cancelAnimationFrame(this.inter);
    }
  },
}
</script>
