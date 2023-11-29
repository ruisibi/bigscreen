/*
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
/**
 * threejs 流光效果
 */
import * as THREE from 'three'
var uniforms = {
		u_time: { value: 0.0 }
	};
var clock = new THREE.Clock();
export const timerFlyCurve = setInterval(()=>{
	const elapsed = clock.getElapsedTime();
	uniforms.u_time.value = elapsed;
},20);
// 着色器设置
const vertexShader = `
				 varying vec2 vUv;
				   attribute float percent;
				   uniform float u_time;
				   uniform float number;
				   uniform float speed;
				   uniform float length;
				   varying float opacity;
				   uniform float size;
				   void main()
				   {
				       vUv = uv;
				       vec4 mvPosition = modelViewMatrix * vec4( position, 1.0 );
				       float l = clamp(1.0-length,0.0,1.0);
				       gl_PointSize = clamp(fract(percent*number + l - u_time*number*speed)-l ,0.0,1.) * size * (1./length);
				       opacity = gl_PointSize/size;
				       gl_Position = projectionMatrix * mvPosition;
				   }
			 `
const fragmentShader = `
							 #ifdef GL_ES
							   precision mediump float;
							   #endif
							   varying float opacity;
							   uniform vec3 color;
							   void main(){
							       if(opacity <=0.2){
							           discard;
							       }
							       gl_FragColor = vec4(color,1.0);
							   }
			`
export function createFlyCurve(points, cfg, closed) {
	var curve = new THREE.CatmullRomCurve3(points, closed);
	// 流光的颜色，三个数字分别代表rgb的值，不过注意，需要除以255
	// 比如浅绿色的rgb是(0,255,127)，那么这里的Vector3就等于(0,1,127/255)也就是(0,1,0.49803921)
	//var color = new THREE.Vector3( 0.5999758518718452, 0.7798940272761521, 0.6181903838257632 );
	var flyLine = initFlyLine( curve, cfg, 5000 );
	return flyLine;
}
function initFlyLine( curve, matSetting, pointsNumber ) {
		var points = curve.getPoints( pointsNumber );
		var geometry = new THREE.BufferGeometry().setFromPoints( points );
		const length = points.length;
		var percents = new Float32Array( length );
		for (let i = 0; i < points.length; i += 1) {
			percents[i] = ( i / length );
		}
		geometry.setAttribute( 'percent', new THREE.BufferAttribute( percents, 1 ) );
		const lineMaterial = initLineMaterial( matSetting );
		var flyLine = new THREE.Points( geometry, lineMaterial );
		return flyLine;
	}

function initLineMaterial( setting ) {
    const number = setting ? ( Number( setting.number ) || 1.0 ) : 1.0;
    const speed = setting ? ( Number( setting.speed ) || 1.0 ) : 1.0;
    const length = setting ? ( Number( setting.length ) || 0.5 ) : 0.5;
    const size = setting ? ( Number( setting.size ) || 3.0 ) : 3.0;
    const color = setting ? setting.color || new THREE.Vector3( 0, 1, 1 ) : new THREE.Vector3( 0, 1, 1 );
    const singleUniforms = {
        u_time: uniforms.u_time,
        number: { type: 'f', value: number },
        speed: { type: 'f', value: speed },
        length: { type: 'f', value: length },
        size: { type: 'f', value: size },
        color: { type: 'v3', value: color }
    };
    const lineMaterial = new THREE.ShaderMaterial( {
        uniforms: singleUniforms,
        vertexShader: vertexShader,
        fragmentShader: fragmentShader,
        transparent: true
    } );
    return lineMaterial;
}
export default
 {
     createFlyCurve,
     timerFlyCurve
 }
