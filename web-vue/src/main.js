/**
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */

// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuex from 'vuex'
import App from './App'
import router from './router/index'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import 'bootstrap3/dist/css/bootstrap.min.css'
import 'font-awesome/css/font-awesome.css'
import {globalMixins,globalPlugin} from "./common/globalMixins.js"
import {store} from "./store/index.js"
import 'codemirror/lib/codemirror.css'
import * as echartsUtils from '@/common/echartsUtils'

Vue.use(ElementUI);
Vue.use(globalPlugin);
Vue.mixin(globalMixins);

//echarts 相关 放入window对象
window.echartsUtils = echartsUtils;
const echarts = require('echarts');
window.echarts = echarts;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})
