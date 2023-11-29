<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 3D 地球 自定义 组件 -->
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

export default {
  components:{
  },
  data(){
    return {
      data:null,
      inter: null,
      earthRadius: 70,  //地球半径
      linePoints:{},
      markers:[],  //地球上的标记点
      camera: null,
      renderer: null,
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
      //this.printChart();
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
    },

    printChart(){
        let comp = this.comp;
        let width = comp.width || 360;
        let height = comp.height || 240;
        const scene = new THREE.Scene();
				scene.background = new THREE.Color( 0x00091e );

				this.camera = new THREE.PerspectiveCamera( 55, width / height, 0.1, 1000 );
				this.camera.position.set(0, 0, 225);
				this.camera.lookAt(new THREE.Vector3(0, 0, 0)); // 相机看向原点


				this.renderer = new THREE.WebGLRenderer( { antialias: true } );
				this.renderer.setPixelRatio( window.devicePixelRatio );
				this.renderer.setSize( width, height );


				$('#threejs_'+comp.id).html(this.renderer.domElement);

				const controls = new OrbitControls( this.camera, this.renderer.domElement );
        controls.enableDamping = true;
        //controls.enableZoom = false;

        //灯光
				const lights = [];
				lights[ 0 ] = new THREE.DirectionalLight( 0xffffff, 3 );
				lights[ 1 ] = new THREE.DirectionalLight( 0xffffff, 3 );
				lights[ 2 ] = new THREE.DirectionalLight( 0xffffff, 3 );

				lights[ 0 ].position.set( 0, 200, 0 );
				lights[ 1 ].position.set( 100, 200, 100 );
				lights[ 2 ].position.set( - 100, - 200, - 100 );

				scene.add( lights[ 0 ] );
				scene.add( lights[ 1 ] );
        scene.add( lights[ 2 ] );

        //背景星星
        const vertices = [];
				const colors = [];
				for ( let i = 0; i < 200; i ++ ) {

					const x = THREE.MathUtils.randFloatSpread( 1600 );
					const y = THREE.MathUtils.randFloatSpread( 1600 );
					const z = THREE.MathUtils.randInt( 1000, -1000 );;

					vertices.push( x, y, z );

					var color = new THREE.Color();
					color.setHSL( Math.random() * 0.2 + 0.5, 0.55, Math.random() * 0.25 + 0.55 );
					colors.push( color.r, color.g, color.b );
        }
        var xxtexture = new THREE.TextureLoader().load( require('@/assets/image/gradient.png') );
				const bggeometry = new THREE.BufferGeometry();
				bggeometry.setAttribute( 'position', new THREE.Float32BufferAttribute( vertices, 3 ) );
				bggeometry.setAttribute( 'color', new THREE.Float32BufferAttribute( colors, 3 ) );
				const bgmaterial = new THREE.PointsMaterial( { size:5, map: xxtexture, transparent: true, vertexColors: true} );
				const points = new THREE.Points( bggeometry, bgmaterial );
				scene.add( points );

        // 地球
				const group = new THREE.Group();
				const texture = new THREE.TextureLoader().load(require("@/assets/image/earth2.jpg"));
				var geometry = new THREE.SphereGeometry( this.earthRadius, 100, 100 );
				const material = new THREE.MeshStandardMaterial( { map: texture} );
				const sphere = new THREE.Mesh( geometry, material );
        group.add( sphere );

        let sc = [104.06574, 30.65946];
        if(comp.style && comp.style.lonlatStart){
          sc = JSON.parse(comp.style.lonlatStart);
        }
				//创建地球上的标注
        let pos = this.lglt2xyz(sc[0], sc[1], this.earthRadius);
        var markerMesh = this.createMarker(pos, this.earthRadius);
				var waveMesh = this.createWaveMesh(pos, this.earthRadius);
        group.add( markerMesh );
        group.add(waveMesh);

        //目标点经纬度
				let zbx = [{lon:-120.140096,lat:38.491617}, {lon:139.042717,lat:36.379264}, {lon:-77.605546, lat:38.375846 },
				{lon:116.082891,lat:39.5536}, {lon:57.800255,lat:40.177406},{lon:100.481983,lat:14.276454}, {lon:11.14471,lat:43.852713},
        {lon:121.675669,lat:31.253345}, {lon:72.94123,lat:21.183033}, {lon:148.664504,lat:-36.439761}, {lon:7.226086,lat:8.029958},
        {lon:-50.76389,lat:-0.982283}, {lon:87.603875,lat:43.852713}];
				if(comp.style && comp.style.lonlatEnd){
          zbx = eval("("+comp.style.lonlatEnd+")");
        }
				for(let c=0; c<zbx.length;c++){
					let pos2 = this.lglt2xyz(zbx[c].lon,zbx[c].lat, this.earthRadius);
          var markerMesh2 = this.createMarker(pos2, this.earthRadius);
          var waveMesh2 = this.createWaveMesh(pos2, this.earthRadius);
          group.add(markerMesh2);
          group.add(waveMesh2);

					//画飞线
					let v1 = new THREE.Vector3(pos.x, pos.y, pos.z);
					let v2 = new THREE.Vector3(pos2.x, pos2.y, pos2.z);

					group.add(this.addLines(v1, v2, c));
        }

        //飞线上物体移动特效
				for(let l in this.linePoints){
					const aGeo = new THREE.SphereGeometry( 1, 10, 10 );
					const aMater = new THREE.MeshPhongMaterial( { color: '#F8D764' } );
					const aMesh = new THREE.Mesh( aGeo, aMater );
					aMesh.name = "line";
					group.add( aMesh );
        }

        //绘制中国国界
				$.ajax({
					url: baseUrl + "chartjson/china-outline.json",
					type:"GET",
					success:(resp)=>{
						let flyLine = this.initMapBorder(resp);
						group.add(flyLine);
					}
				});

        //初始化位置
        group.rotation.x += 0.4;
				group.rotation.y += 2.5;

        scene.add(group);

        let lineIndex = 0;
        const render = () => {

					this.inter = requestAnimationFrame( render );

          controls.update();

          //地球移动
          group.rotation.y -= 0.001;
          //背景星星移动
          points.rotation.y += 0.0002;
          //标记运动
          this.markerMove(this.earthRadius * 0.005);

          //飞线上点的移动
					let idx = 0;
					group.children.forEach((ele, index)=>{
						if(ele.name == 'line'){
							let points = this.linePoints[idx][lineIndex];
							ele.position.set(points.x, points.y, points.z);
							idx++;
						}
					});

					lineIndex++;
					if(lineIndex >= 200){
						lineIndex = 0;
					}

					this.renderer.render( scene, this.camera );
				}
				render();
    },
    //标记移动
    markerMove(size){
      // 所有波动光圈都有自己的透明度和大小状态
      // 一个波动光圈透明度变化过程是：0~1~0反复循环
      let WaveMeshArr = this.markers;
      if (WaveMeshArr.length) {
        WaveMeshArr.forEach( function ( mesh ) {
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
        } );
      }
    },
    //创建在地球上的标注
    createMarker(pos, radius){
      const texture = new THREE.TextureLoader().load(require("@/assets/image/earthmarker.png"));
      var material = new THREE.MeshBasicMaterial( {
        map: texture,
        transparent: true, //使用背景透明的png贴图，注意开启透明计算
        // side: THREE.DoubleSide, //双面可见
        depthWrite: false, //禁止写入深度缓冲区数据
      } );
      const planGeometry = new THREE.PlaneGeometry( 10, 10 );
      var mesh = new THREE.Mesh( planGeometry, material );
      var size = radius * 0.004;//矩形平面Mesh的尺寸
      mesh.scale.set( size, size, size );//设置mesh大小
      //设置mesh位置
      mesh.position.set( pos.x, pos.y, pos.z );
      // mesh在球面上的法线方向(球心和球面坐标构成的方向向量)
      var coordVec3 = new THREE.Vector3( pos.x, pos.y, pos.z ).normalize();
      // mesh默认在XOY平面上，法线方向沿着z轴new THREE.Vector3(0, 0, 1)
      var meshNormal = new THREE.Vector3( 0, 0, 1 );
      // 四元数属性.quaternion表示mesh的角度状态
      //.setFromUnitVectors();计算两个向量之间构成的四元数值
      mesh.quaternion.setFromUnitVectors( meshNormal, coordVec3 );
      return mesh;
    },
    //创建在地球上的光圈
    createWaveMesh(pos, radius){
      const texture = new THREE.TextureLoader().load(require("@/assets/image/lightround.png"));
      var material = new THREE.MeshBasicMaterial( {
        color: 0x22ffcc,
        map: texture,
        transparent: true, //使用背景透明的png贴图，注意开启透明计算
        // side: THREE.DoubleSide, //双面可见
        depthWrite: false, //禁止写入深度缓冲区数据
      } );
      const planGeometry = new THREE.PlaneGeometry( 10, 10 );
      var mesh = new THREE.Mesh( planGeometry, material );
      var size = radius * 0.005;//矩形平面Mesh的尺寸
      mesh.scale.set( size, size, size );//设置mesh大小
      //设置mesh位置
      mesh.position.set( pos.x, pos.y, pos.z );
      // mesh在球面上的法线方向(球心和球面坐标构成的方向向量)
      var coordVec3 = new THREE.Vector3( pos.x, pos.y, pos.z ).normalize();
      // mesh默认在XOY平面上，法线方向沿着z轴new THREE.Vector3(0, 0, 1)
      var meshNormal = new THREE.Vector3( 0, 0, 1 );
      // 四元数属性.quaternion表示mesh的角度状态
      //.setFromUnitVectors();计算两个向量之间构成的四元数值
      mesh.quaternion.setFromUnitVectors( meshNormal, coordVec3 );
      this.markers.push(mesh);
      return mesh;
    },
    /**
			*lng:经度
			*lat:维度
			*radius:地球半径
			*/
			lglt2xyz(lng, lat, radius) {
				const phi = (180 + lng) * (Math.PI / 180)
				const theta = (90 - lat) * (Math.PI / 180)
				return {
					x: -radius * Math.sin(theta) * Math.cos(phi),
					y: radius * Math.cos(theta),
					z: radius * Math.sin(theta) * Math.sin(phi),
				}
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

      addLines( v0, v3, cindex) {
				let angle = (v0.angleTo(v3) * 120) /(Math.PI / 0.4); // 0 ~ Math.PI  // 计算向量夹角
				let aLen = angle * 2.5,
				hLen = angle * angle * 50;
				let p0 = new THREE.Vector3(0, 0, 0);  // 法线向量
				var temp = new THREE.Vector3();
				let rayLine = new THREE.Ray(p0, this.getVCenter(v0.clone(), v3.clone()));  // 顶点坐标
				let vtop = rayLine.at(hLen / rayLine.at(1, temp).distanceTo(p0), temp); // 位置
				// 控制点坐标
				let v1 = this.getLenVcetor(v0.clone(), vtop, aLen);
				let v2 = this.getLenVcetor(v3.clone(), vtop, aLen);
				// 绘制三维三次贝赛尔曲线
				var curve = new THREE.CubicBezierCurve3( v0, v1, v2, v3 );
				var points = curve.getPoints( 200 );
				this.linePoints[cindex] = points;
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
				return new Line2( geometry, matLine );
      },

      initMapBorder( chinaJson ) {
				const linGeometry = new THREE.BufferGeometry();
				//const positions = [];
				const points = [];

				// 新建一个省份容器：用来存放省份对应的模型和轮廓线
				chinaJson.features.forEach((feature, idx)=>{
					const coordinates = feature.geometry.coordinates;
					coordinates.forEach( multiPolygon => {
						multiPolygon.forEach( polygon => {
              if (polygon.length > 20) {
                for (let i = 0; i < polygon.length; i ++) {
                  let pos = this.lglt2xyz( polygon[i][0], polygon[i][1], this.earthRadius );
                  //positions.push( pos.x, pos.y, pos.z );
                  points.push(new THREE.Vector3(pos.x, pos.y, pos.z));
                }
              }
						} );
					} );
        });
        var color = new THREE.Vector3( 0.5999758518718452, 0.7798940272761521, 0.6181903838257632 );
        var flyLine = createFlyCurve(points, {
          speed: 0.1,
          color: color,
          number: 3, //同时跑动的流光数量
          length: 0.2, //流光线条长度
          size: 3 //粗细
        },false);
				return flyLine;

			}
  },
  watch: {

  },
  beforeDestroy(){
    if(this.inter){
      window.cancelAnimationFrame(this.inter);
    }
    //timerFlyCurve();
  },
}
</script>
