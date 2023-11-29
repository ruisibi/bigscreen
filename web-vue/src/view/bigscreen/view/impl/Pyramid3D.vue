<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 3D 金字塔 自定义 组件 -->
<script>
import {baseUrl, ajax, formatDate} from '@/common/biConfig'
import {formatNumber} from '@/common/echartsUtils'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import { Loading } from 'element-ui'
import * as dutils from '@/view/dashboard/Utils'
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { OBJLoader } from 'three/examples/jsm/loaders/OBJLoader.js';
import { CSS2DRenderer, CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer.js';

export default {
  components:{
  },
  data(){
    return {
      data:null,
      camera: null,
      renderer: null,
      scene: null,
      labelRenderer: null,
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
    let width = comp.width || 360;
    let height = comp.height || 240;
    return h('div', {domProps:{id:"threejs_"+this.comp.id, width:width, height:height}});
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
      this.drawChart();
    },
    //属性改变调用的方法
    propChage(){
      //this.printChart();
    },

    //调整组件大小后调用的通用方法 (公共方法，实现类都需要实现)
    resize(){
      this.resizeChart();
    },

    resizeChart(){
      let camera = this.camera;
      let renderer = this.renderer;
      let comp = this.comp;
      let width = comp.width || 360;
      let height = comp.height || 240;
      // 设置摄像机视锥体的长宽比（覆盖创建相机时的长宽比）
      camera.aspect = width / height;
      // 更新摄像机投影矩阵。在任何参数被改变以后必须被调用。
      camera.updateProjectionMatrix();
      // 设置渲染区尺寸
      renderer.setSize(width,  height);
      // 设置设备像素比
      renderer.setPixelRatio(window.devicePixelRatio);
      this.labelRenderer.setSize( width,  height );
    },

    printChart(){
        let comp = this.comp;
        let width = comp.width || 360;
        let height = comp.height || 240;
        let scene = new THREE.Scene();
        this.scene = scene;

				this.camera = new THREE.PerspectiveCamera( 55, width / height, 0.1, 1000 );
				this.camera.position.set(0, 0, 50);
				this.camera.lookAt(new THREE.Vector3(0, 0, 0)); // 相机看向原点


				this.renderer = new THREE.WebGLRenderer( { antialias: false, alpha: true } );
				this.renderer.setPixelRatio( window.devicePixelRatio );
        this.renderer.setSize( width, height );
        $('#threejs_'+comp.id).html(this.renderer.domElement);

        //const controls = new OrbitControls( this.camera, this.renderer.domElement );
        //controls.enableDamping = true;

         //初始化CSS2DRenderer
        let labelRenderer = new CSS2DRenderer();
        labelRenderer.setSize( width, height );
        labelRenderer.domElement.style.position = 'absolute';
        labelRenderer.domElement.style.top = '0px';
        labelRenderer.domElement.style.pointerEvents = 'none';
        $('#threejs_'+comp.id).append( labelRenderer.domElement );
        this.labelRenderer = labelRenderer;

        //坐标轴
        //var axes = new THREE.AxesHelper(1000);
        //scene.add(axes);

         //灯光
        let c = 0xffffff;
        let directionalLight1 = new THREE.DirectionalLight(c,1); //037af1
        directionalLight1.position.set(-100, 100, -100);
        let directionalLight2 = new THREE.DirectionalLight(c, 2);
        directionalLight2.position.set(100, 100, 100);
        scene.add(directionalLight1);
        scene.add(directionalLight2);

        let directionalLight3 = new THREE.DirectionalLight(c, 3);
        directionalLight3.position.set(100, 100, 100);
         scene.add(directionalLight3);

        let directionalLight4 = new THREE.DirectionalLight(c, 4);
        directionalLight4.position.set(100, -100, 100);
        scene.add(directionalLight4);

        if(!this.data || this.data.length == 0){
          this.drawEmptyChart();
        }

        this.renderChart();
    },

    renderChart(){
      this.renderer.render( this.scene, this.camera );
      this.labelRenderer.render( this.scene, this.camera );
    },

    drawEmptyChart(){
      let groups = new THREE.Group();
      let colors = [0xa7a7a7, 0xa39d90, 0x779e2f, 0x276292, 0x541688, 0xc21493, 0xa4aa3c, 0x0a806b];
      //维度个数，最多8个
      const size = 5;
      const meshY = [40,25,17,12,9,7,5.7,4.7];  //定义mesh的间距
      const meshH = [60,23,15,10,7.5,6,4.8,4]; //定义mesh 的高度

      for(let i=size; i>=0; i--){
        let material3 = new THREE.MeshLambertMaterial({
          color: colors[i],
          transparent: true,
          opacity: 0.9,
          depthWrite: true,
        });
        let box = new THREE.CylinderGeometry(i * 3, (i  + 1) * 3, meshH[size], 4);
        let mesh3 = new THREE.Mesh(box, material3);
        mesh3.position.x = -15;
        mesh3.position.y= 22 -  i * meshY[size];
        groups.add(mesh3);

        //文本
        let tag = document.createElement('div')
        tag.className = 'jztlable';
        tag.style.position = 'absolute';
        tag.style.pointerEvents = 'none';
        let label = new CSS2DObject(tag)
        label.element.innerHTML =  "维度"+(i+ 1);
        label.element.style.visibility = 'visible'
        label.element.style.border = "1px solid #" + (colors[i].toString(16));
        label.position.set(28, mesh3.position.y, 0);
        label.name = "text";
        groups.add(label);

        //划线
        const points = [];
        points.push(new THREE.Vector3(mesh3.position.x, mesh3.position.y, mesh3.position.z));
        points.push(new THREE.Vector3(label.position.x, label.position.y, label.position.z));
        const geometry = new THREE.BufferGeometry().setFromPoints( points );
        const material = new THREE.LineBasicMaterial( { color: colors[i] } );
        const line = new THREE.Line( geometry, material );
        groups.add(line);
      }
      groups.name = 'comp';
      //先移除以前的
      $(this.scene.children).each((a, b)=>{
        if(b.name == 'comp'){
          $(b.children).each((c, d)=>{
            b.remove(d);
          });
          this.scene.remove(b);
        }
      });
      this.scene.add(groups);
    },

    drawChart(){
      let dt = this.data;
      if(dt && dt.length > 8){
        this.$notify.error("3D金字塔图形只支持最多8个维度值。");
        return;
      }
      if(!dt){
        return;
      }
      if(!this.comp.comp.dim){
        return;
      }
      if(!this.comp.comp.kpi){
        return;
      }
      let groups = new THREE.Group();
      let kpiAlias = this.comp.comp.kpi.alias;
      let dimAlias = this.comp.comp.dim.alias;

      //数据排序
      dt.sort((a, b)=>{
        return a[kpiAlias] - b[kpiAlias];
      });

      let colors = [0xa7a7a7, 0xa39d90, 0x779e2f, 0x276292, 0x541688, 0xc21493, 0xa4aa3c, 0x0a806b];
      //维度个数，最多8个
      const size = dt.length - 1;
      const meshY = [40,25,17,12,9,7,5.7,4.7];  //定义mesh的间距
      const meshH = [60,23,15,10,7.5,6,4.8,4]; //定义mesh 的高度

      for(let i=size; i>=0; i--){
        let material3 = new THREE.MeshLambertMaterial({
          color: colors[i],
          transparent: true,
          opacity: 0.9,
          depthWrite: true,
        });
        let box = new THREE.CylinderGeometry(i * 3, (i  + 1) * 3, meshH[size], 4);
        let mesh3 = new THREE.Mesh(box, material3);
        mesh3.position.x = -15;
        mesh3.position.y= 22 -  i * meshY[size];
        groups.add(mesh3);

        //文本
        let tag = document.createElement('div')
        tag.className = 'jztlable';
        tag.style.position = 'absolute';
        tag.style.pointerEvents = 'none';
        let label = new CSS2DObject(tag)
        //label.element.innerHTML =  "维度"+(i+ 1);
        let v = dt[i][kpiAlias];
        let fmt = this.comp.comp.kpi.fmt;
        if(fmt){
          v = formatNumber(v, fmt);
        }
        label.element.innerHTML =  dt[i][dimAlias]+"：" +  v;
        label.element.style.visibility = 'visible'
        label.element.style.border = "1px solid #" + (colors[i].toString(16));
        label.position.set(28, mesh3.position.y, 0);
        label.name = "text";
        groups.add(label);

        //划线
        const points = [];
        points.push(new THREE.Vector3(mesh3.position.x, mesh3.position.y, mesh3.position.z));
        points.push(new THREE.Vector3(label.position.x, label.position.y, label.position.z));
        const geometry = new THREE.BufferGeometry().setFromPoints( points );
        const material = new THREE.LineBasicMaterial( { color: colors[i] } );
        const line = new THREE.Line( geometry, material );
        groups.add(line);
      }
      groups.name = 'comp';
      //先移除以前的
      $(this.scene.children).each((a, b)=>{
        if(b.name == 'comp'){
          $(b.children).each((c, d)=>{
            b.remove(d);
          });
          this.scene.remove(b);
        }
      });
      this.scene.add(groups);
      this.renderChart();
    }
  },
  watch: {

  },
  beforeDestroy(){

  },
}
</script>
<style lang="less" >
.jztlable {
  font-size: 16px;
  padding: 0px 5px 0px 5px;
  color: white;
  border-radius: 5px;
  width: 120px;
  background-color: black;
}
</style>
