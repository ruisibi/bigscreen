<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 3D 地图 自定义 组件 -->
<script>
import {baseUrl, ajax, formatDate} from '@/common/biConfig'
import {formatNumber} from '@/common/echartsUtils'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import { Loading } from 'element-ui'
import * as dutils from '@/view/dashboard/Utils'
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { DragControls } from 'three/examples/jsm/controls/DragControls.js';
import { Line2 } from 'three/examples/jsm/lines/Line2'
import { LineGeometry } from 'three/examples/jsm/lines/LineGeometry'
import { LineMaterial } from 'three/examples/jsm/lines/LineMaterial'
import { createFlyCurve,timerFlyCurve } from '@/common/flyCurve.js';
import * as d3geo from 'd3-geo';
import { CSS2DRenderer, CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer.js';

export default {
  components:{
  },
  data(){
    return {
      data:null,
      maxData: null,
      minData: null,
      inter: null,
      camera: null,
      renderer: null,
      labelRenderer: null,
      rotatingApertureMesh: null, //背景旋转圆环
      pointer: new THREE.Vector2(),
      raycaster: null,
      projection: null,
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
    if(!this.comp.comp.tid){ //未绑定数据，才加载地图，如果绑定了数据，在 refreshData 中加载地图
      this.printChart();
    }
  },
  computed: {
  },
  methods: {
    //数据更新后，自动刷新组件通用方法
    refreshData(data){
      this.data = data;
      if(data == null){
         this.maxData = null;
         this.minData = null;
      }else{
        let max = 0;
        let min = Number.MAX_SAFE_INTEGER;

        let k = this.comp.comp.kpi.alias;
        data.forEach((a, b)=>{
          let v = a[k];
          if(v > max){
            max = v;
          }
          if(v < min){
            min = v;
          }
        });
        this.maxData = max;
        this.minData = min;
      }
      //重新加载图形
      if(this.inter){
        window.cancelAnimationFrame(this.inter);
      }
      this.printChart();
    },
    //属性改变调用的方法
    propChage(){
      if(this.inter){
        window.cancelAnimationFrame(this.inter);
      }
      this.printChart();
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

        const scene = new THREE.Scene();
        scene.background = new THREE.Color( 0x00091e );

        this.camera = new THREE.PerspectiveCamera( 45, width / height, 0.1, 1050 );
				this.camera.position.set(3.4, 118, 92);
				this.camera.lookAt(new THREE.Vector3(0, 0, 0)); // 相机看向原点


				this.renderer = new THREE.WebGLRenderer( { antialias: true } );
				this.renderer.setPixelRatio( window.devicePixelRatio );
        this.renderer.setSize( width, height );

        $('#threejs_'+comp.id).html(this.renderer.domElement);

        document.addEventListener( 'mousemove', this.onPointerMove );
        //window.addEventListener('resize', this.resizeChart);

        this.raycaster = new THREE.Raycaster();

        //初始化CSS2DRenderer
        let labelRenderer = new CSS2DRenderer();
        labelRenderer.setSize( width, height );
        labelRenderer.domElement.style.position = 'absolute';
        labelRenderer.domElement.style.top = '0px';
        labelRenderer.domElement.style.pointerEvents = 'none';
        $('#threejs_'+comp.id).append( labelRenderer.domElement );
        this.labelRenderer = labelRenderer;

        const controls = new OrbitControls( this.camera, this.renderer.domElement );
        controls.enableDamping = true;
        //设置相机距离原点的最远距离
        controls.minDistance = 1;
        //设置相机距离原点的最远距离
        controls.maxDistance = 2000;
        // orbitcontrols.minPolarAngle = Math.PI / 180*10;
        controls.maxPolarAngle = Math.PI / 180*75;//不然看到底部，超过90就看到底部了

        //灯光
        scene.add(new THREE.AmbientLight(0x7af4ff,1.2));
        let directionalLight1 = new THREE.DirectionalLight(0x7af4ff,1); //037af1
        directionalLight1.position.set(-100, 10, -100);
        let directionalLight2 = new THREE.DirectionalLight(0x7af4ff, 1);
        directionalLight2.position.set(100, 10, 100);
        scene.add(directionalLight1);
        scene.add(directionalLight2);

        //坐标轴
        //var axes = new THREE.AxesHelper(1000);
        //scene.add(axes);

        //背景星空
        let stars = this.initbgPoint();
        scene.add(stars);

        let center = [102.44662948242187, 30.927128325051036];  //默认是四川地图的中心
        if(comp.style && comp.style.mapCenter){
          center = eval("(["+comp.style.mapCenter+"])");
        }
        let rate = 550;
        if(comp.style && comp.style.mapRate){
          rate = comp.style.mapRate;
        }
        //初始化坐标系
        this.projection = d3geo.geoMercator().center(center).scale(rate).translate([0, 0]);

        //获取地图数据 (四川地图)
        const map = new THREE.Group(); //对象集合
        let maparea = "510000.json";  //四川地图的JSON文件
        if(comp.style && comp.style.mapJsonFile){
          maparea = comp.style.mapJsonFile;
        }
        $.ajax({
          url: baseUrl + "chartjson/" + maparea,
          type : "GET",
          success:(resp)=>{
            this.initMap(map, resp);
            this.initMapMesh(map, resp);
             //初始化地图上的数据点
            this.initMapPointer(map, resp);
            //初始化地图上的飞线
            this.initMapFlyLine(map, resp);
            scene.add(map);
          }
        });


        this.initBgImg(scene);

        const render = () => {

					this.inter = requestAnimationFrame( render );

          controls.update();

          //星空移动
          stars.rotation.z += 0.0002;

          if(this.rotatingApertureMesh){
            this.rotatingApertureMesh.rotation.z += 0.0005;
          }
          $(map.children).each((a, b)=>{
            if(b.name == 'area'){
              b.material[0].color = new THREE.Color(0.19806931954941637 ,  0.5647115056965487,  0.5394794890033748);
            }
          });
          if(!(this.pointer.x == 0 && this.pointer.y == 0)){
            this.raycaster.setFromCamera( this.pointer, this.camera );
            const intersects = this.raycaster.intersectObjects( map.children, false );
            intersects.forEach((a, b)=>{
              if(a.object.name == 'area'){
                let mesh = a.object;
                mesh.material[0].color = new THREE.Color(0.19806931954941637, 0.10998939353851522, 0.5394794890033748);
              }
            });
          }

          //地图标记动画
          map.children.forEach(e=>{
            if(e.name =='markerPoint'){
              let size = 3;
              let mesh = e;
              mesh._s += 0.007;
              mesh.scale.set( size * mesh._s, size * mesh._s, size * mesh._s );
              if (mesh._s <= 1.5) {
                //mesh._s=1，透明度=0 mesh._s=1.5，透明度=1
                mesh.material.opacity = ( mesh._s - 1 ) * 2;
              } else if (mesh._s > 1.5 && mesh._s <= 2) {
                //mesh._s=1.5，透明度=1 mesh._s=2，透明度=0
                mesh.material.opacity = 1 - ( mesh._s - 1.5 ) * 2;
              } else {
                mesh._s = 1.0;
              }
            }
          });

          this.renderer.render( scene, this.camera );
          this.labelRenderer.render( scene, this.camera );
        }
        render();
    },
    //初始化背景点
    initbgPoint(){
      var texture = new THREE.TextureLoader().load( require('@/assets/image/gradient.png') );
      const positions = [];
      const colors = [];
      const geometry = new THREE.BufferGeometry();
      for (var i = 0; i < 1000; i ++) {
        var vertex = new THREE.Vector3();
        vertex.x = Math.random() * 2 - 1;
        vertex.y = Math.random() * 2 - 1;
        vertex.z = Math.random() * 2 - 1;
        positions.push( vertex.x, vertex.y, vertex.z );
        var color = new THREE.Color();
        color.setHSL( Math.random() * 0.2 + 0.5, 0.55, Math.random() * 0.25 + 0.55 );
        colors.push( color.r, color.g, color.b );
      }
      geometry.setAttribute( 'position', new THREE.Float32BufferAttribute( positions, 3 ) );
      geometry.setAttribute( 'color', new THREE.Float32BufferAttribute( colors, 3 ) );
      const bgmaterial = new THREE.PointsMaterial( { size:1, map: texture, transparent: true, vertexColors: true, blending: THREE.AdditiveBlending, sizeAttenuation: true} );
      var stars = new THREE.Points( geometry, bgmaterial );
      stars.scale.set( 300, 300, 300 );
      return stars;
    },

    //初始化地图飞线
    initMapFlyLine(group, mapJson){
      let s = this.comp.style;
      if(s && s.lonlatStart && s.lonlatEnd){
        //起始点
        let center = JSON.parse(s.lonlatStart);
        const [x1, y1]=this.projection(center);
        let marker = this.createPointMesh(4);
        marker.position.set(x1 , 4.9, y1 );
        group.add(marker);

        let point = this.createFlyLineMarker(3);
        point.position.set(x1 , 4.9, y1 );
        group.add(point);

        //目标点经纬度
        let targets = eval("("+s.lonlatEnd+")");
        for(let c=0; c<targets.length;c++){
          const [x, y]=this.projection([targets[c].lon,targets[c].lat]);
          let marker = this.createPointMesh(4);
          marker.position.set(x , 4.9, y );
          group.add(marker);

          //let point = this.createFlyLineMarker(3);
          //point.position.set(x , 4.9, y );
          //group.add(point);

          //画飞线
					let v2 = new THREE.Vector3(x1, 4.9, y1);
          let v1 = new THREE.Vector3(x, 4.9, y);

          let line = this.addLines(v2, v1, c);
          group.add(line.line);

          //流光效果
          var curve = line.curve;
          var points = curve.getPoints( 50 );
          var color = new THREE.Color( 0x4fb5f9 );
          var flyLine = createFlyCurve(points, {
            speed: 0.6,
            color: color,
            number: 1, //同时跑动的流光数量
            length: 0.2, //流光线条长度
            size: 10 //粗细
          },false);
          group.add(flyLine);
        }
      }
    },

    addLines( v0, v3, cindex) {
				let angle = (v0.angleTo(v3) * 120) /(Math.PI / 0.01); // 0 ~ Math.PI  // 计算向量夹角
				let aLen = angle * 2.5,
				hLen = angle * angle * 50;
				let p0 = new THREE.Vector3(0, 0, 0);  // 法线向量
				var temp = new THREE.Vector3();
				let rayLine = new THREE.Ray(p0, this.getVCenter(v0.clone(), v3.clone()));  // 顶点坐标
				let vtop = rayLine.at(hLen / rayLine.at(1.5, temp).distanceTo(p0), temp); // 位置
				// 控制点坐标
        let v1 = this.getLenVcetor(v0.clone(), vtop, aLen);
        v1.setY(30);
        let v2 = this.getLenVcetor(v3.clone(), vtop, aLen);
        v2.setY(30);
				// 绘制三维三次贝赛尔曲线
				var curve = new THREE.CubicBezierCurve3( v0, v1, v2, v3 );
				var points = curve.getPoints( 50 );
				//this.linePoints[cindex] = points;
				var positions = [];
				var colors = [];

				/**
				 * HSL中使用渐变
				 * h — hue value between 0.0 and 1.0
				 * s — 饱和度 between 0.0 and 1.0
				 * l — 亮度 between 0.0 and 1.0
				 */
				let color = new THREE.Color();
				for (var j = 0; j < points.length; j ++) {
					color.setHSL( .31666+1*0.005,0.7, 0.7); //绿色
					//let color = new THREE.Color(Math.random(),Math.random(),Math.random());
					colors.push( color.r, color.g, color.b );
					positions.push( points[j].x, points[j].y, points[j].z );
				}
				var geometry =  new LineGeometry();
				geometry.setPositions( positions );
				geometry.setColors( colors);
				var matLine = new LineMaterial( {
					linewidth: 0.001,
					vertexColors: true,
				} );
        let line = new Line2( geometry, matLine );
        return {line:line, curve:curve};
      },

    initMapPointer(group, mapJson){
      if(this.data && this.maxData >=0 && this.minData >= 0){  //存在数据
        mapJson.features.forEach(elem => {
          const properties = elem.properties;
          let areaName = properties.name;
          let lightCenter = properties.cp;
          const [x, y]=this.projection(lightCenter);

          //这里创建光柱
          let v = this.data.filter(m=>m[this.comp.comp.dim.alias] == areaName)[0];
          if(!v){
            return;
          }
          v = v[this.comp.comp.kpi.alias];
          v = (v - this.minData) / (this.maxData - this.minData);
          let height = v * 15;
          const geometry = new THREE.ConeGeometry(1, height, 4);
          // 柱体旋转90度，垂直于Y轴
          // geometry.rotateX(Math.PI / 2)
          // 柱体的z轴移动高度一半对齐中心点
          geometry.translate(0, height / 2,0);
          // 柱子材质
          var textureLoader = new THREE.TextureLoader();
          const material = new THREE.MeshBasicMaterial({
            //map: textureLoader.load( require("@/assets/image/lightcolumn.png") ),
            color:0xe9e9e9,
            transparent: true,
            depthWrite: true,
            depthTest: true,
            side: THREE.DoubleSide,
            opacity:0.6,
          })
          // 光柱01
          let light01 = new THREE.Mesh(geometry, material);
          light01.position.set(x, 5, y - 2);

          group.add(light01);

          //标记
          let marker = this.createPointMesh(5);
          marker.position.set(x, 4.9, y - 2);
          group.add(marker);
        });
      }
    },

    initMap(map, mapJson){
      var matLine = new LineMaterial( {
        color: 0xffffff,
        linewidth: 0.0013,
        vertexColors: true,
        dashed: false,
        alphaToCoverage: true,

      } );

      var matLine2 = new LineMaterial( {
        color: '#01bdc2',
        linewidth: 0.005,
        vertexColors: true,
        dashed: false,
        alphaToCoverage: true,
      } );

      // d3-geo转化坐标
      const projection = this.projection;
      // 遍历省份构建模型
      mapJson.features.forEach(elem => {
        const coordinates = elem.geometry.coordinates
        const properties = elem.properties;
        //这里创建文本
        if(this.comp.style && this.comp.style.showAreaName == true){
          this.createTextPoint(properties,projection, map)
        }
        coordinates.forEach(multiPolygon => {
            const positions = [];
            var colors = [];
            const color = new THREE.Color();
            var linGeometry = new LineGeometry();
             multiPolygon.forEach(polygon => {
                const [x, y] = projection(polygon)
                positions.push(x, -y, 4.01);
                color.setHSL( 1 , 1, 1 );
                colors.push( color.r, color.g, color.b );
              })
            //Line2
            linGeometry.setPositions( positions );
            linGeometry.setColors( colors );
            const line = new Line2( linGeometry, matLine );
            const line2 = new Line2( linGeometry, matLine2 );
            line.computeLineDistances();
            line.rotateX(-Math.PI/2)
            line2.rotateX(-Math.PI/2)
            line.position.set(0,0.1,-3)
            line2.position.set(0,-3.5,-3)
            line2.computeLineDistances();
            line.scale.set( 1, 1, 1 );
            map.add(line)
            map.add(line2)
        })
      })
    },
    //地图几何体
    initMapMesh(groups, chinaJson) {
      var textureLoader = new THREE.TextureLoader();
      let textureMap=textureLoader.load(require("@/assets/image/3d-map.jpeg"));
      let texturefxMap=textureLoader.load(require("@/assets/image/3d-map-fx.jpeg"));
      textureMap.wrapS = THREE.RepeatWrapping;  //纹理水平方向的平铺方式
      textureMap.wrapT = THREE.RepeatWrapping;  //纹理垂直方向的平铺方式
      textureMap.flipY = texturefxMap.flipY = false;
      textureMap.rotation = texturefxMap.rotation = THREE.MathUtils.degToRad(45);
      const scale = 0.01;
      textureMap.repeat.set(scale, scale);
      texturefxMap.repeat.set(scale, scale);
      textureMap.offset.set(0.5,0.5);
      texturefxMap.offset.set(0.5,0.5);
      // d3-geo转化坐标
      const projection = this.projection;
      // 遍历省份构建模型
      chinaJson.features.forEach(elem => {
        // 新建一个省份容器：用来存放省份对应的模型和轮廓线
        const coordinates = elem.geometry.coordinates
        const properties = elem.properties;
        coordinates.forEach((multiPolygon) => {
          const shape = new THREE.Shape();
            var v3ps = [];
            multiPolygon.forEach((polygon, i) => {
              if(polygon.length > 2){  //一个省有多个区域的情况，比如河北
                polygon.forEach((p, i2)=>{
                  const [x, y] = projection(p)
                  if (i2 === 0) {
                      shape.moveTo(x, -y)
                  }
                  shape.lineTo(x, -y);
                  v3ps.push(new THREE.Vector3(x, -y, 4.02))
                });
              }else{
                const [x, y] = projection(polygon)
                if (i === 0) {
                    shape.moveTo(x, -y)
                }
                shape.lineTo(x, -y);
                v3ps.push(new THREE.Vector3(x, -y, 4.02))
              }
            });
            const extrudeSettings = {
              depth: 4,  //该属性指定图形可以拉伸多高，默认值是100
              bevelEnabled: false, //是否给这个形状加斜面，默认加斜面。
            };
            //拉升成地图
            const geometry = new THREE.ExtrudeGeometry(shape, extrudeSettings);
            let color = new THREE.Color(0.19806931954941637 ,  0.5647115056965487,  0.5394794890033748);
            //材质
            const material = new THREE.MeshPhongMaterial({
              map: textureMap,
              normalMap:texturefxMap,
              color: color,
              combine: THREE.MultiplyOperation,
              transparent: true,
              opacity: 1,
            });
            material.needsUpdate = true
            const material1 = new THREE.MeshLambertMaterial({ color: 0x238377, transparent: true, opacity: 1 });

            const mesh = new THREE.Mesh(geometry,  [material, material1])
            mesh.rotateX(-Math.PI/2)
            mesh.position.set(0,0,-3)
            mesh.name = 'area';
            groups.add(mesh)

            //流光效果
            /**
            var curve = new THREE.CatmullRomCurve3(v3ps, true);
            var points = curve.getPoints( 50 );
            var color = new THREE.Vector3( 0.5999758518718452, 0.7798940272761521, 0.6181903838257632 );
            var flyLine = createFlyCurve(points, {
              speed: 0.1,
              color: color,
              number: 3, //同时跑动的流光数量
              length: 0.2, //流光线条长度
              size: 3 //粗细
            },false);

            flyLine.position.set(0,0.1,-3)
            flyLine.scale.multiplyScalar(1.001)
            flyLine.rotateX(-Math.PI/2)
            groups.add(flyLine);

             */
        })
      })
    },
    initBgImg(scene){
      var textureLoader = new THREE.TextureLoader();
      let circlePoint = textureLoader.load(require("@/assets/image/circle-point.png"))
      let material3 = new THREE.MeshPhongMaterial({
        color: 0x00ffff,
        map: circlePoint,
        transparent: true,
        opacity: 1,
        depthWrite: false,
        // depthTest: false,
      });
      let plane3 = new THREE.PlaneGeometry(200, 200);
      let mesh3 = new THREE.Mesh(plane3, material3);
      mesh3.rotateX(-Math.PI/2)
      mesh3.position.y=0.06
      scene.add(mesh3);

      let rotatingApertureTexture = textureLoader.load(require("@/assets/image/rotatingAperture.png"));
      let rotatingApertureerial = new THREE.MeshBasicMaterial({
        map: rotatingApertureTexture,
        transparent: true,
        opacity: 1,
        depthTest: true,
        depthWrite: false,
      });
      var rotatingApertureGeometry = new THREE.PlaneGeometry(110, 110);
      var rotatingApertureMesh = new THREE.Mesh(rotatingApertureGeometry, rotatingApertureerial);
      rotatingApertureMesh.rotateX(-Math.PI/2)
      rotatingApertureMesh.position.y=0.02
      rotatingApertureMesh.scale.set(1.2, 1.2, 1.2);
      scene.add(rotatingApertureMesh);
      this.rotatingApertureMesh = rotatingApertureMesh;
    },
    //地图文本
    createTextPoint(properties,projection,group){
      let areaName = properties.name;
      let lightCenter = properties.cp;
      const [x, y]=projection(lightCenter);

      let tag = document.createElement('div')
      tag.innerHTML = name
      tag.className = "arealable"
      tag.style.pointerEvents = 'none'
      // tag.style.visibility = 'hidden'
      tag.style.position = 'absolute'
      let label = new CSS2DObject(tag)
      label.element.innerHTML = areaName
      label.element.style.visibility = 'visible'
      label.position.set(x,5.01, y  + 3)
      label.position.z-=3
      group.add(label);
    },
    createPointMesh(size){
      // 标记点：几何体，材质，
      const geometry = new THREE.PlaneGeometry(1, 1)
      const material = new THREE.MeshBasicMaterial({
        map: new THREE.TextureLoader().load(require("@/assets/image/lightround.png")),
        color: 0x00ffff,
        side: THREE.DoubleSide,
        transparent: true,
        depthWrite: false, //禁止写入深度缓冲区数据
      })
      let mesh = new THREE.Mesh(geometry, material)
      mesh.renderOrder = 2
      mesh.rotation.x=Math.PI/2
      mesh.name = 'markerPoint'
      // 缩放
      const scale = 1 * size
      mesh.scale.set(scale, scale, scale);
      mesh._s = Math.random();
      return mesh
    },
     //创建飞线标记
    createFlyLineMarker(size){
      const texture = new THREE.TextureLoader().load(require("@/assets/image/earthmarker.png"));
      var material = new THREE.MeshBasicMaterial( {
        map: texture,
        transparent: true, //使用背景透明的png贴图，注意开启透明计算
        // side: THREE.DoubleSide, //双面可见
        depthWrite: false, //禁止写入深度缓冲区数据
      } );
      const planGeometry = new THREE.PlaneGeometry( 1, 1 );
      var mesh = new THREE.Mesh( planGeometry, material );
      mesh.scale.set( size, size, size );//设置mesh大小
      mesh.rotateX(-Math.PI/2);
      return mesh;
    },
    onPointerMove( event ) {
      let bl = this.pageInfo.style.bl;
      if(!bl){
        bl = 1;
      }
      let comp = this.comp;
      let width = comp.width || 360;
      let height = comp.height || 240;
      width = width * bl;
      height = height * bl;

      let p = $("#optarea").position();
      let left = comp.left * bl + p.left;
      let top = comp.top * bl  + p.top;
      this.pointer.x = ( (event.clientX - left) / width  ) * 2 - 1;
      this.pointer.y = - ( (event.clientY - top) / height  ) * 2 + 1;
    },
    // 计算v1,v2 的中点
    getVCenter( v1, v2 ) {
      const v = v1.add( v2 );
      return v.divideScalar( 2 );
    },

    // 计算V1，V2向量固定长度的点
    getLenVcetor( v1, v2, len ) {
      const v1v2Len = v1.distanceTo( v2 );
      return v1.lerp( v2, len / v1v2Len );
    },
  },
  watch: {

  },
  beforeDestroy(){
    document.removeEventListener("mousemove", this.onPointerMove);
    //window.removeEventListener("resize", this.resizeChart);
    if(this.inter){
      window.cancelAnimationFrame(this.inter);
    }
    //timerFlyCurve();
  },
}
</script>
<style lang="less" >
.arealable {
  font-size: 16px;
  color: yellow;
}
</style>
