/*
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */

import Vue from 'vue'
import Vuex from 'vuex'
import $ from 'jquery'
import {plugin} from "./plugin.js"
Vue.use(Vuex)


let initState = {
  count: 0,
  isMini: false,
}
let state = JSON.parse(localStorage.getItem("state")) || initState;
export const store = new Vuex.Store({
  plugins: [plugin],
  state: state,
  mutations: {
    increment (state) {
      state.count++
    },
    //最小化窗口
    setMini (state) {
      if(state.isMini == false){
        state.isMini = true;
        $(".el-dialog__body").hide();
		    $(".el-dialog__footer").hide();
      }else{
        $(".el-dialog__body").show();
		    $(".el-dialog__footer").show();
        state.isMini = false;
      }
    }
  }
})
