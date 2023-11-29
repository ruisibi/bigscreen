/*
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */

import $ from 'jquery'
import {Result} from "./Result.js"

export const sendRequest = (ins,url,params,showLoading=true,showError=true) => {
  return new Promise((resolve,reject) => {
    let loadingIns = null;
    if(showLoading){
      loadingIns = ins.$loading();
    }
    let o = {
      type: "POST",
      data:params,
      dataType:"JSON",
      xhrFields: {withCredentials: true},
      contentType:"application/json; charset=utf-8",
      crossDomain: true,
      url:url,
      success:function(resp){
        if(resp.result === 1){
          resolve(Result.success({data:resp}))
        }else{
          if(showError) ins.$notify.error({title: '系统出错',message:resp.msg,offset: 50});
          resolve(Result.fail({data:resp}))
        }
      },
      error: function(resp){
        if(showError) ins.$notify.error({title: '系统出错',offset: 50});
        resolve(Result.fail({msg:"系统出错",data:resp}))
      },
      complete:function(resp){
        if(showLoading){
          setTimeout(() => {
            loadingIns.close();
          },1000)
        }
      }
    };
    try{
      $.ajax(o);
    }catch(e){
      if(showLoading){
        setTimeout(() => {
          loadingIns.close();
        },1000)
      }
      if(showError) ins.$notify.error({title: '系统出错',offset: 50});
      resolve(Result.fail({msg:"程序出错"}))
    }finally{

    }

  });
}
