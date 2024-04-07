<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/index.html
 -->
 
<!-- 对应 大屏的 日历组件 -->
<script>
import {baseUrl, ajax, formatDate} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import * as echartsUtils from '@/common/echartsUtils'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { createFlyCurve,timerFlyCurve } from '@/common/flyCurve.js';

export default {
  components:{    
  },
  data(){
    return {
      carouselId:null, //轮播ID
      camera:null,
      renderer: null,
      scene: null,
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
    return h('div', { class:"decorate", attrs:{id:"cav_" + this.comp.id}});
  },
  mounted(){
    this.initThreeJS();
  },
  computed: {
  },
  methods: {
    refreshData(data){
    },
    propChage(){
      //重建mesh
      let mesh = this.scene.children.filter(m=>m.name == 'mesh')[0];
      this.scene.remove(mesh);

      let container = "cav_" + this.comp.id;
      let w = $("#" + container).width();
      let h = $("#" + container).height();
      let group = this.createMesh(w, h);
      this.scene.add(group);
    },

   initThreeJS(){
     let cs = this.comp.style;
    let container = "cav_" + this.comp.id;
    let w = $("#" + container).width();
    let h = $("#" + container).height();
    //创建场景
    let scene = new THREE.Scene();
    this.scene = scene;
    //创建相机
    //let camera = new THREE.PerspectiveCamera(75, w / h, 0.1,1000);
    const camera = new THREE.OrthographicCamera( w / - 2, w / 2, h / 2, h / - 2, 1, 1000 );
    camera.position.set(0, 0, 600);
    camera.lookAt(new THREE.Vector3(0, 0, 0));
    this.camera = camera;

      //创建渲染器
    let renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setPixelRatio( window.devicePixelRatio );
    renderer.setSize(w, h);
    renderer.setClearAlpha(0);
    document.getElementById(container).appendChild(renderer.domElement);
    this.renderer = renderer;

    //var axes = new THREE.AxesHelper(1000);
    //scene.add(axes);

    //const light = new THREE.AmbientLight( 0xffffff, 10 );
    //scene.add( light );

    let group = this.createMesh(w, h);
    this.scene.add(group);

    const render = () => {
        this.carouselId = requestAnimationFrame( render );
        renderer.render( scene, camera );
     };
     render();
   },

   createMesh(w, h){
     let cs = this.comp.style;
      //绘制二维曲线
      let box = [
        new THREE.Vector3(- w/2 + 5, h/2 -5, 0 ),
        new THREE.Vector3(w/2 - 5, h/2 - 5, 0 ),
        new THREE.Vector3(w/2 - 5, -h/2 + 5, 0 ),
        new THREE.Vector3(-w/2 + 5, -h/2 + 5, 0 ),
        new THREE.Vector3(- w/2 + 5, h/2 -5, 0 ),
      ]
      let color = 'white';
      if(cs && cs.borderColor){
        color = eval("0x" + cs.borderColor.substring(1, 7));
      }

      let group = new THREE.Group();
      
       let geometry = new THREE.BufferGeometry().setFromPoints(box);
       let material = null;
       if(cs && cs.dynamicBoxType == 'dotted'){
        material = new THREE.LineDashedMaterial( {
            color:color,
            linewidth: 5,
            scale: 1,
            dashSize: 3,
            gapSize: 4
          } );
       }else{
         material = new THREE.LineBasicMaterial({ 
           color: color,
          linewidth: 1,
          linecap: 'round', //ignored by WebGLRenderer
          linejoin:  'round' //ignored by WebGLRenderer
         });
       }
        let line = new THREE.Line( geometry, material );
        line.name = "mesh";
        line.computeLineDistances();
        group.add(line);
       

        const path = new THREE.CurvePath();
        for(let i =0 ;i<box.length - 1; i++){
          path.add(new THREE.LineCurve3(box[i], box[i + 1]));
        }

        //创建 流光效果 
        let frequency = 0.1;
        if(cs && cs.frequency){
          frequency = cs.frequency;
        }
        let reverse = false;
        if(cs && cs.switchMove == true){
          reverse = true;
        }
        let flycolor = 0x4fb5f9;
        if(cs && cs.flyColor){
          flycolor = eval("0x" + cs.flyColor.substring(1, 7));
        }
        let flycount = 1;
        if(cs && cs.flycount){
          flycount = cs.flycount;
        }
        var curve = path;
				var points = curve.getSpacedPoints ( 100 );
        var lgcolor = new THREE.Color( flycolor );
        var flyLine = createFlyCurve(points, {
          speed: frequency,
          color: lgcolor,
          number: flycount, //同时跑动的流光数量
          length: 0.4, //流光线条长度
          size: 6 //粗细
        },false, reverse);
        group.add(flyLine);
        group.name = "mesh";
        return group;
    
   },
   //调整组件大小后调用的通用方法 (公共方法，实现类都需要实现)
   resize(){
     if(!this.renderer){
       return;
     }
      let container = "cav_" + this.comp.id;
      let w = $("#" + container).width();
      let h = $("#" + container).height();
       this.renderer.setSize(w,  h);
      this.renderer.setPixelRatio(window.devicePixelRatio);
      // 相机宽高比
      this.camera.aspect = w/h;
      // 更新摄像机投影矩阵
      this.camera.updateProjectionMatrix();
   }
  },
  watch: {
    
  },
  beforeDestroy(){
			if(this.carouselId){
				window.cancelAnimationFrame(this.carouselId);
			}
	},
}
</script>
<style lang="less" scoped>
.decorate {
  width: 100%;
  height: 100%;
}
</style>
