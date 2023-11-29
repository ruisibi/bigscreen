/*
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */

/**
 * 系统使用的router
 */
import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/view/Login'
import Main from '@/view/Main'
import Welcome from '@/view/Welcome'
import Menu from '@/view/frame/Menu'
import Role from '@/view/frame/Role'
import RoleMenu from '@/view/frame/RoleMenu'
import User from '@/view/frame/User'
import Department from '@/view/frame/Department'
import UserMenu from '@/view/frame/UserMenu'
import OnlineUser from '@/view/frame/OnlineUser'
import Datasource from '@/view/etl/ds/Datasource'
import RegTable from '@/view/etl/reg/RegTable'
import SubjectType from '@/view/model/SubjectType'
import SubjectManager from '@/view/model/SubjectManager'
import NewCubeStep from '@/view/model/NewCubeStep'
import Dataset from '@/view/model/Dataset'
import Resource from '@/view/bigscreen/Resource'
import TableCallbackHelper from '@/view/portal/TableCallbackHelper'
import BigscreenList from '@/view/bigscreen/BigscreenList'
import Bigscreen from '@/view/bigscreen/Bigscreen'
import BigscreenPushView from '@/view/bigscreen/BigscreenPushView'
import BigscreenView from '@/view/bigscreen/BigscreenView'
import BigscreenShareView from '@/view/bigscreen/BigscreenShareView'
import NotFind from '@/view/NoFind'

Vue.use(Router)

let router = new Router({
  routes: [
    {
      path: '/',
      name: 'login',
      component: Login
    },
    {
      path:'*',
      component:NotFind
    },
    {
      path: '/main',
      name: 'main',
      component: Main,
      children: [
        {
          path: '/Welcome',
          name: 'welcome',
          component: Welcome
        },
        {
          path:'/frame/Menu',
          name:'menu',
          component:Menu
        },
        {
          path:'/frame/User',
          name:'user',
          component:User
        },
        {
          path:'/frame/Role',
          name:'role',
          component:Role
        },
        {
          path:'/frame/RoleMenu',
          name:'roleMenu',
          component:RoleMenu
        },
        {
          path:'/frame/UserMenu',
          name:'userMenu',
          component:UserMenu
        },
        {
          path:'/frame/OnlineUser',
          name:'onlineUser',
          component:OnlineUser
        },
        {
          path:"/etl/ds/Datasource",
          name:"datasource",
          component:Datasource
        },
        {
          path:"/etl/reg/RegTable",
          name:"regTable",
          component:RegTable
        },
        {
          path:'/frame/Department',
          name:'department',
          component:Department
        },
        {
          path:'/model/SubjectType',
          name:'subjectType',
          component:SubjectType
        },
        {
          path:'/model/SubjectManager',
          name:'subjectManager',
          component:SubjectManager
        },
        {
          path:'/model/NewCubeStep',
          name:'newCubeStep',
          component:NewCubeStep
        },
        {
          path:'/model/Dataset',
          name:'dataset',
          component:Dataset
        },
        {
          path:"/bigscreen/Resource",
          name:'resource',
          component:Resource
        },
        {
          path:"/bigscreen/BigscreenList",
          name:"bigscreenList",
          component:BigscreenList
        },
        {
          path:"/bigscreen/Bigscreen",
          name:"bigscreen",
          component:Bigscreen
        },
        {
          path:"/bigscreen/PushView",
          name:"bigscreenPushView",
          component:BigscreenPushView
        },
      ]
    },
    {
      path:'/bigscreen/MobileView',
      name:'bigscreenMobileView',
      component:BigscreenPushView
    },
    {
      path:"/portal/TableCallbackHelper",
      name:"tableCallbackHelper",
      component:TableCallbackHelper
    },
    {
      path:"/bigscreen/View",
      name:"bigscreenView",
      component:BigscreenView
    },
    {
      path:"/bigscreen/ShareView",
      name:"bigscreenShareView",
      component:BigscreenShareView
    },
  ]
})

router.beforeEach((to, from, next) => {
  if(to.path != "/"){
    // if(!checkIsLogin()){
    //   next("/")
    //   return;
    // }
  }
  next();
})

export default router;
