<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
  	<div class="wrapper-content-nomargin bg">
		  <div id="container" class="framework"></div>
		  <div class="text">
				  	<p style="font-size:14px; padding-bottom:20px;">
					<b style="color:rgb(255, 110, 0);">“{{sysInfo.rsbiName}}”</b>V{{ sysInfo.rsbiVersionNumber }}是由{{productInfo.company}}自主研发的，具有自主知识产权的BI数据大屏系统，帮助客户快捷、灵活、零代码创建数据大屏应用，也可做系统开发的低代码平台。
					</p>
					<dl class="dl-horizontal info">
						<dt>系统名称：</dt><dd>{{sysInfo.rsbiName}}</dd>
						<dt>系统版本：</dt><dd>{{sysInfo.rsbiVersion}}</dd>
						<dt>版本号：</dt><dd>{{ sysInfo.rsbiVersionNumber }}</dd>
						<dt>最后更新时间：</dt><dd>{{ sysInfo.rsbiLastupdate }}</dd>
						<dt>官网地址：</dt><dd><a :href="sysInfo.rsbiNet">{{sysInfo.rsbiNet}}</a></dd>
						<dt>使用协议：</dt><dd style="color:yellow;">开放源码，供个人学习使用，如商业用途需购买使用许可 <a target="_blank" href="https://www.ruisitech.com/licenses/opensource.html">➡️了解详细</a></dd>
						<dt>jdk版本：</dt><dd>{{sysInfo.jdk}}</dd>
						<dt>支撑库：</dt><dd>{{sysInfo.dbName}}</dd>
						<dt>数据仓：</dt><dd>{{sysInfo.dwType}}</dd>

					</dl>


          <p style="color:rgb(255, 110, 0); padding-top:20px; pointer-events: all; width:300px;">如果您需要更多功能，可以<a href="#" onclick="window.open('https://www.ruisitech.com/ultimate.html/')">购买旗舰版</a> 👈👈 <br/>
		  系统介绍录像: <a href='https://www.ruisitech.com/files/bigscreen.mov'>下载</a>
		  </p>

		  </div>
		  <div class="nodeTooltip"></div>
  	</div>
</template>

<script>
	import { mapState } from "vuex";
	import {ajax, baseUrl} from '@/common/biConfig'
	import $ from 'jquery'
	import * as THREE from 'three';
	import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
	import { GLTFLoader } from "three/examples/jsm/loaders/GLTFLoader"
	import { SVGLoader } from "three/examples/jsm/loaders/SVGLoader"
	import { CSS2DRenderer, CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer.js';
	import * as BufferGeometryUtils from 'three/examples/jsm/utils/BufferGeometryUtils.js';


	export default {
	    data(){
			return {
				sysInfo:{},
				productInfo:{},
				camera: null,
				scene: null,
				renderer : null,
				labelRenderer: null,
				inter: null,
				uniformsArrs: [],
				uniformsArrs2 : [],
				raycaster: null,
				pointer: new THREE.Vector2(),
			}
		},
		mounted(){
			this.getSysInfo();
			this.loadProductInfo();
			this.frameworkChart();
		},
		computed: {
			...mapState(["isMini"])
		},
		methods: {
			getSysInfo(){
				ajax({
					url:"frame/Welcome.action",
					data:{},
					success:(resp)=>{
						this.sysInfo = resp.rows;
					}
				}, this);
			},
			loadProductInfo(){
				ajax({
					url:"login/productInfo.action",
					data:{},
					type:"GET",
					success:(r)=>{
						this.productInfo = r.rows;
					}
				}, this);
			},
			//架构图,threejs 实现
			frameworkChart(){
				let scene = new THREE.Scene();
				this.scene = scene;

				let width = $("#container").width();
				let height = $("#container").height();

				let camera = new THREE.PerspectiveCamera(45, width / height, 1, 5000);
				camera.position.set(550,537,78);
				camera.lookAt(scene.position)
				this.camera = camera;

				let container = document.getElementById( 'container' );
				let renderer = new THREE.WebGLRenderer( { antialias: true, alpha: true,powerPreference: 'high-performance' } );
				renderer.setPixelRatio( window.devicePixelRatio );
				renderer.setSize( width, height );
				container.appendChild( renderer.domElement );
				this.renderer = renderer;

				let cameraControls = new OrbitControls(camera, renderer.domElement);
				cameraControls.minPolarAngle = 0;
				cameraControls.maxPolarAngle = Math.PI / 2.2;

				//lights
				var ambient = new THREE.AmbientLight('#ffffff', 0.8);
				scene.add(ambient);

				var dirLight = new THREE.DirectionalLight( 0xffffff, 0.5 );
				dirLight.position.set( 200, 5, 200 );
				scene.add( dirLight );

				var dirLight2 = new THREE.DirectionalLight( 0xffffff, 0.7 );
				dirLight2.position.set( -200, 5, -200 );
				scene.add( dirLight2 );

				//var axes = new THREE.AxesHelper(2000)
				//scene.add(axes)

				window.addEventListener('resize', this.resizeNodeMap);
				document.addEventListener( 'mousemove', this.onPointerMove );

				this.raycaster = new THREE.Raycaster();

				 //初始化CSS2DRenderer
				let labelRenderer = new CSS2DRenderer();
				labelRenderer.setSize( width, height );
				labelRenderer.domElement.style.position = 'absolute';
				labelRenderer.domElement.style.top = '0px';
				labelRenderer.domElement.style.pointerEvents = 'none';
				container.appendChild( labelRenderer.domElement );
				this.labelRenderer = labelRenderer;

				this.showNodes();

				const render = () => {
					this.inter = requestAnimationFrame( render );
					cameraControls.update();

					let uniformsArrs2 = this.uniformsArrs2;
					if (uniformsArrs2.length > 0) {
						uniformsArrs2.forEach(function (uniforms){
							uniforms.time.value += 0.02; //粒子速度
							uniforms.time2.value += 0.005;//冲击波速度
						})
					}
					let uniformsArrs = this.uniformsArrs;
					if(uniformsArrs.length>0){
						uniformsArrs.forEach(function (uniforms){
							uniforms.time.value += 0.005;
						})
					}

					renderer.render( scene, this.camera );
					labelRenderer.render( scene, this.camera );
				};
				render();
			},
			//展示节点
			showNodes(){
				var dataArrs = [
					{
						glbUrl: baseUrl + 'object/node.glb',
						glbPosition: new THREE.Vector3(-190, 0, 250),
						nodeName:"数据源",
						color: '#0565f3', //035094
						roadPositon:new THREE.Vector3(-190, 0, 170),
						rotationType:"z",
						rotation:-90,
						shape:'left'
					},
					{
						glbUrl: baseUrl + 'object/node.glb',
						glbPosition: new THREE.Vector3(190, 0, 250),
						nodeName:"Rest接口",
						color: '#0565f3', //035094
						roadPositon:new THREE.Vector3(190, 0, 170),
						rotationType:"z",
						rotation:-90,
						shape:'right'
					},
					{
						glbUrl: baseUrl + 'object/node.glb',
						glbPosition: new THREE.Vector3(0, 0, 100),
						nodeName:"数据集",
						color: '#11CD6E',
						roadPositon:new THREE.Vector3(0, 0, 10),
						rotationType:"z",
						rotation:-90
					},
					{
						glbUrl: baseUrl + 'object/node.glb',
						glbPosition: new THREE.Vector3(0, 0, -80),
						nodeName:"立方体",
						color: '#FB821F',
						roadPositon: new THREE.Vector3(0, 0, -170),
						rotationType:"z",
						rotation:-90
					},
					{
						glbUrl: baseUrl + 'object/node.glb',
						glbPosition: new THREE.Vector3(0, 0, -260),
						nodeName:"数据大屏",
						color: '#e51ffb',

					}];
				dataArrs.forEach( (data) => {
					this.initMeshGlb(data)
				})
			},
			initMeshGlb(data){
				let scene = this.scene;
				let ts = this;
				var uniforms = {
					"time": {
						value: 0.0
					},
					"repeat":{
						value:new THREE.Vector2( 3, 1 )
					},
					"colorTexture": {value: new THREE.TextureLoader().load(require("@/assets/image/nodexqdh.png"))},
					"colorTexture1": {value: new THREE.TextureLoader().load(require("@/assets/image/nodejb.png"))},
					"color": {value: new THREE.Color(data.color)}
				};

				uniforms["colorTexture"].value.wrapS = uniforms["colorTexture"].value.wrapT = THREE.RepeatWrapping;
				uniforms["colorTexture1"].value.wrapS = uniforms["colorTexture1"].value.wrapT = THREE.RepeatWrapping;
				this.uniformsArrs.push(uniforms);
				const vertexShader = `
					varying vec2 vUv;
					varying vec3 fNormal;
					varying vec3 vPosition;
					void main()
					{
						vUv = uv;
						fNormal=normal;
						vPosition=position;

						vec4 mvPosition = modelViewMatrix * vec4( position, 1.0 );
						gl_Position = projectionMatrix * mvPosition;
					}
				`;
				const fragmentShader = `
					uniform float time;
					varying vec2 vUv;
					uniform sampler2D colorTexture;
					uniform vec2 repeat;
					uniform sampler2D colorTexture1;
					varying vec3 fNormal;
					varying vec3 vPosition;
					uniform vec3 color;
					void main( void ) {
						vec2 position = vUv;
						vec3 tempNomal= normalize(fNormal);
						float power=step(0.95,abs(tempNomal.y));
						//vec4 colorb=texture2D(colorTexture1,position.xy);
						vec4 colorb= vec4(color,1.0);
						vec4 colora = texture2D(colorTexture,vec2((vUv*repeat).x,fract((vUv*repeat).y-time)));
						if(power>0.95){
							gl_FragColor =vec4(0);
						}else{
						gl_FragColor =colorb*colora;
							//gl_FragColor =vec4( (colorb).r*colora.r, (colorb).g*colora.g, (colorb).b*colora.b, (colora+colorb).a );
						}
					}
				`;

				var customMaterial = new THREE.ShaderMaterial({
					uniforms: uniforms,
					vertexShader: vertexShader,
					fragmentShader: fragmentShader,
					blending: THREE.AdditiveBlending,
					transparent: true,
					depthTest: false,
					side: THREE.DoubleSide,
				})
				// model
				var loader = new GLTFLoader();
				var texture = new THREE.TextureLoader().load(require('@/assets/image/Base_Technology_LightEffect_Alpha.png'));
				texture.center.set(0.5, 0.5);
				texture.rotation = -Math.PI
				loader.load( data.glbUrl, function ( gltf ) {
					gltf.scene.scale.multiplyScalar(40)
					gltf.scene.position.copy(data.glbPosition)
					gltf.scene.renderOrder=1
					gltf.scene.name = data.nodeName;
					scene.add(gltf.scene)
					gltf.scene.traverse( function ( child ) {
						if ( child.isMesh ) {
							//上面一圈
							if(child.name=='opacity'){
								child.material=customMaterial
								child.rotateX(Math.PI)
								child.position.y+=1.1;
							}

								//底部4个发射光
							if(child.name=='opacity-s'){
								var material = new THREE.MeshStandardMaterial({
									map:texture,
									color: new THREE.Color(data.color).multiplyScalar(0.5),
									emissive:new THREE.Color(data.color).multiplyScalar(0.5),
									roughness: 0.438,
									metalness: 0,
									depthWrite:false,
									transparent:true,
									opacity:0.2,
									side:THREE.DoubleSide
								});
								child.material=material
							}
							if(child.name == 'deepColor') {
								var material = new THREE.MeshStandardMaterial({
									color: new THREE.Color(data.color).multiplyScalar(0.8),
									emissive:new THREE.Color(data.color).multiplyScalar(0.8),
									roughness: 0.438,
									metalness: 0,
								})
								child.material=material

							}
							if(child.name == 'lowColor') {
								var material = new THREE.MeshStandardMaterial({
									color: new THREE.Color(data.color).multiplyScalar(0.5),
									emissive:new THREE.Color(data.color).multiplyScalar(0.5),
									roughness: 0.438,
									metalness: 0,
								})
								child.material=material

							}

							if(child.name == 'black') {
								var material = new THREE.MeshStandardMaterial({
									color: new THREE.Color(data.color).multiplyScalar(0.2),
									// emissive:new THREE.Color(data.color).multiplyScalar(0.2),
									roughness: 0.438,
									metalness: 0,
									// envMap:envMap,
								})
								child.material=material
							}
						}
					} );
					ts.createNodeText(data, gltf.scene);
					if(data.roadPositon){
						ts.initRoad(data.roadPositon,data.rotationType,data.rotation, data.shape);
					}
				}, undefined, function ( e ) {
					console.error( e );
				} )
			},

			createNodeText(data, scene){
				let name = data.nodeName;
				let tag = document.createElement('div')
				tag.innerHTML = name
				tag.className = "nodelable"
				tag.style.pointerEvents = 'none'
				tag.style.position = 'absolute'
				let label = new CSS2DObject(tag)
				label.element.innerHTML = name;
				label.element.style.visibility = 'visible'
				let p = scene.position;
				label.position.set(p.x, 30, p.z);
				label.name = 'text';
				this.scene.add(label);
			},

			/**
			 * 底部道路流光
			 */
			initRoad(roadPositon,rotationType,rotation, shape){
				var width =154;
				var height =14;
				var scale =width/height
				var uniforms = {
					"time": {
						value: 0.0
					},
					"time2": {
						value: 0.0
					},
					"repeat":{
						value:new THREE.Vector2( scale/5,1)
					},
					"colorTexture": {value: new THREE.TextureLoader().load(require('@/assets/image/spgs.png'))},

					"colorTexture1": {value: new THREE.TextureLoader().load(require('@/assets/image/spcjb.png'))}
				};
				this.uniformsArrs2.push(uniforms);
				var customMaterial = new THREE.ShaderMaterial({
					uniforms: uniforms,
					vertexShader: `
						varying vec2 vUv;
						varying vec3 fNormal;
						varying vec3 vPosition;
						void main()
						{
						vUv = uv;
						fNormal=normal;
						vPosition=position;
						vec4 mvPosition = modelViewMatrix * vec4( position, 1.0 );
						gl_Position = projectionMatrix * mvPosition;
						}
					`,
					fragmentShader: `
						uniform float time;
						uniform float time2;
						varying vec2 vUv;
						uniform vec2 repeat;
						uniform sampler2D colorTexture;
						uniform sampler2D colorTexture1;
						varying vec3 fNormal;
						varying vec3 vPosition;
						void main( void ) {
						vec2 position = vUv;
						vec4 colorb = texture2D(colorTexture1,vec2(fract((vUv).x+time2),(vUv).y));
						vec4 colora = texture2D(colorTexture,vec2(fract((vUv*repeat).x+time),(vUv*repeat).y));
						gl_FragColor = vec4( (colorb).r*1., (colorb).g*1., (colorb).b*1., (colora+colorb).a );
						}
					`,
					blending: THREE.AdditiveBlending,
					transparent: true,
					side: THREE.DoubleSide
				})
				var geometry = null;
				if(!shape){
					geometry = new THREE.PlaneGeometry(width, height, 1, 1);
				}else{
					var geometry1 = new THREE.PlaneGeometry(width, height, 1, 1);
					var geometry2 = new THREE.PlaneGeometry(width, height, 1, 1);
					if("left" == shape){
						geometry2.rotateZ(-Math.PI / 2);
						geometry2.translate(-70, 83, 0);
					}else{
						geometry2.rotateZ(Math.PI / 2);
						geometry2.translate(-70, -83, 0);
					}
					geometry = BufferGeometryUtils.mergeGeometries([geometry1, geometry2]);
				}
				let mesh = new THREE.Mesh(geometry, customMaterial);
				mesh.name = "road";
				mesh.renderOrder=1
				mesh.rotation.x -= Math.PI/2
				mesh.position.copy(roadPositon)
				if(rotationType=='y'){
					mesh.rotation.y+=Math.PI/180*rotation
				}
				if(rotationType=='z'){
					mesh.rotation.z+=Math.PI/180*rotation
				}
				this.scene.add(mesh);
			},
			resizeNodeMap(){
				let camera = this.camera;
				let renderer = this.renderer;
				let width = $("#container").width();
				let height = $("#container").height();
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
			onPointerMove(event){
				 let p = $("#container").offset();
				let left = p.left;
				let top = p.top;
				let width = $("#container").width();
				let height = $("#container").height();
				this.pointer.x = ( (event.clientX - left) / width  ) * 2 - 1;
				this.pointer.y = - ( (event.clientY - top) / height  ) * 2 + 1;

				this.raycaster.setFromCamera( this.pointer, this.camera );
				const intersects = this.raycaster.intersectObjects( this.scene.children, true );
				let cur = false;
				intersects.forEach((a, b)=>{
					if(a.object.name == 'opacity' || a.object.name == 'opacity-s' || a.object.name == 'black'){
						$("#container").css({"cursor":"pointer"});
						cur = true;
						this.showLabel(a.object.parent.name, event);
					}
				});
				if(cur == false){
					$("#container").css({"cursor":""});
					this.hideLable();
				}
			},
			showLabel(nodeName, event){
				if($(".nodeTooltip").attr("show") == 'y'){
					return;
				}
				let str = "";
				if(nodeName == '数据源'){
					str = "定义BI系统链接外部数据源，支持常见的数据库，不支持的数据源用户可扩展。";
				}else if(nodeName=='Rest接口'){
					str = "注册外部Rest接口到BI系统进行调用，有JSON格式要求，不满足需要可扩展。";
				}else if(nodeName=='数据集'){
					str = "支持表、SQL查询语句、Rest接口做数据集，用来映射大屏的表格、详情页等组件。";
				}else if(nodeName=='立方体'){
					str = "基于数据集构建立方体（维度和度量），用来映射大屏的数据块、图表、交叉表等组件。";
				}else if(nodeName=='数据大屏'){
					str = "包含时间、天气、表格、图片、图表、选项卡、嵌套框、参数等多种组件，采用绝对布局构建页面。";
				}
				let p = $("#container").offset();
				let left = event.clientX - p.left + 400;
				let top = event.clientY - p.top + 20;
				$(".nodeTooltip").css({
					left:left + "px",
					top: top + "px",
				}).attr("show","y").html(str).show();
			},
			hideLable(){
				$(".nodeTooltip").attr("show", "").hide();
			}
		},
		watch: {
		},
		beforeRouteLeave(to, from, next) {
			this.$destroy();
			next();
		},
		beforeRouteEnter(to, from, next){
			next();
		},
		beforeDestroy(){
			document.removeEventListener("mousemove", this.onPointerMove);
			window.removeEventListener("resize", this.resizeNodeMap);
			if(this.inter){
				window.cancelAnimationFrame(this.inter);
			}
		},
	}
</script>
<style lang="less" scoped>
.framework {
	height: 100%;
	margin-left:400px;
}
.text {
	position:absolute;
	top:0;
	left: 0;
	width: 100%;
	color: white;
	padding:20px;
	pointer-events: none;
}
.info {
	background-image: url(../assets/bigscreen/panel-2.png);
	width: 400px;
    background-size: 100% 100%;
	background-repeat: no-repeat;
	padding: 10px 0 10px 0;
	pointer-events: all;
}
.bg {
	background-image: url(../assets/bigscreen/bg05.png);
	background-color:#010201;
	color:white;
	position: relative;
}
.nodeTooltip {
	position: absolute;
	width: 140px;
	height: 110px;
	background-color: white;
	color: black;
	border: 1px solid #010201;
	border-radius: 5px;
	top: 0;
	left: 0;
	display: none;
	z-index: 9999;
	opacity: 0.85;
	padding: 5px;
}
</style>
<style lang="less" >
.nodelable {
  font-size: 14px;
  color: yellow;
}
</style>
