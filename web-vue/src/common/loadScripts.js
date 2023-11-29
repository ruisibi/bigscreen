/*
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
export function loadScripts(srcs=[]){
  for(let i=0;i<srcs.length;i++){
    let src = srcs[i];
    let isExist = document.querySelector(`script[src='${src}']`);
    if(isExist) continue;
    let script = document.createElement("script");
    script.setAttribute("type","text/javascript");
    script.setAttribute("src",src);
    document.body.appendChild(script);
    script.onload = function(){
      console.log(`加载完成 ${src}`);
    }
    script.onerror = function(e){
      console.log(`加载错误 ${src}`,e);
    }
  }
}
export function removeScripts(srcs = []){
  for(let i=0;i<srcs.length;i++){
    let src = srcs[i];
    let srcipt = document.querySelector(`script[src='${src}']`);
    if(srcipt){
      srcipt.remove();
      console.log(`删除完成 ${src}`);
    }
  }
}
export function loadCss(srcs=[]){
  for(let i=0;i<srcs.length;i++){
    let src = srcs[i];
    let isExist = document.querySelector(`link[href='${src}']`);
    if(isExist) continue;
    let link = document.createElement("link");
    link.setAttribute("rel","stylesheet");
    link.setAttribute("type","text/css");
    link.setAttribute("href",src);
    document.body.appendChild(link);
    link.onload = function(){
      console.log(`加载完成 ${src}`);
    }
    link.onerror = function(e){
      console.log(`加载错误 ${src}`,e);
    }
  }
}
export function removeCss(srcs = []){
  for(let i=0;i<srcs.length;i++){
    let src = srcs[i];
    let link = document.querySelector(`link[href='${src}']`);
    if(link){
      link.remove();
      console.log(`删除完成 ${src}`);
    }
  }
}
