<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->

<!-- 对应 大屏的 选项卡组件 -->
<script>
import {baseUrl, ajax, formatDate} from '@/common/biConfig'
import $ from 'jquery'
import * as utils from '@/view/portal/Utils'
import * as echartsUtils from '@/common/echartsUtils'
import * as bsUtils from '@/view/bigscreen/bsUtils'

export default {
  components:{
  },
  data(){
    return {
      tabCount:3,
      currSelect: 0,
      carouselId: null,
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
  },
  render(h){
    let ts = this;
    let comp = this.comp;
    let s = {width:"100%", height:"100%"};
    let tabCount = this.comp.tabs.length;  //选项卡总个数


    let tabStyle = {width:Math.floor(100 / tabCount) + "%" };
    let tabClassName = "def";
    if(comp.style){
      let st = comp.style;
      if(st.size){
        tabStyle['font-size'] = st.size+'px';
      }
      if(st.color){
        tabStyle['color'] = st.color;
      }
      if(st.tabStyle){
        tabClassName = st.tabStyle;
      }
    }
    if(comp.style && comp.style.tabModel == 'block'){  //选项卡渲染成方块
      let tabs = [];
      for(let i=0; i<tabCount; i++){
        let c = "tab-header-" + tabClassName + "-" + comp.style.tabModel;
        if(i == this.currSelect){
          c += " selected";
        }
        tabs.push(h('span', {class:c, on:{
          click:()=>{
            this.tabChange(i);
          }
        }}, ''));
      }
      return h('div', {style:s, class:"tab-" + tabClassName+"-block"}, tabs);
    }else if(comp.style && comp.style.tabModel == 'circle'){  //选项卡渲染成圆点
      return h("div", "未实现");
    }else{ //默认渲染
      let tabs = [];
      for(let i=0; i<tabCount; i++){
        let c = "tab-item-" + tabClassName;
        if(i == this.currSelect){
          c += " selected";
        }
        let tab = this.comp.tabs[i];
        tabs.push(h('div', {class:c, style:tabStyle, on:{
          click:()=>{
            this.tabChange(i);
          }
        }}, tab.name));
      }
      let header = h('div', {class:"tab-header-" + tabClassName}, tabs);
      return h('div', {style:s, class:"tab-"+tabClassName}, [header]);
    }
  },
  mounted(){
    if(this.editor != true){
      this.refresh();
    }
  },
  computed: {
  },
  methods: {
    //通用方法，view
    view(){

    },
    /**
     * 选项卡切换， i 表示切换第几个选项卡
     */
    tabChange(i){
      this.currSelect = i;
      //隐藏指定的组件
      let curComps = bsUtils.tabClickEvent(this.pageInfo, this.comp.id, i);
      this.$parent.$forceUpdate();
      //组件更新后，对于表格，交叉表等组件，需要重新计算组件高度
      this.$parent.$nextTick(()=>{
        $(curComps).each((a, b)=>{
          if(b.type == 'grid' || b.type == 'table'){
            this.$parent.$refs['mv_' + b.id].computeTableHeight();
          }
        });
      });
    },
    //自动切换
    refresh(){
      let c = this.comp;
      if(c.style && c.style.autoswitch == true){
        let currentIndex = 0;
        let  scollIdx = 0;
        let switchStep = c.style.switchStep || 5;
        const carousel = ()=>{
          if(scollIdx == 0 || scollIdx % (60 * switchStep) == 0) {
            this.tabChange(currentIndex);
            currentIndex++;
            if(currentIndex >= this.comp.tabs.length){
              currentIndex = 0;
            }
          }
          scollIdx++;
          if(scollIdx >= 10000){
            scollIdx = 0;
          }
          this.carouselId = requestAnimationFrame(carousel);
        }
        carousel();
      }
    }
  },
  watch: {

  },
  beforeDestroy(){
     //终止轮播事件
    if(this.carouselId){
      cancelAnimationFrame(this.carouselId);
    }
  },
}
</script>
<style lang="less" scoped>
.tab-def {

}
.tab-header-def .tab-item-def {
  float: left;
  text-align: center;
  cursor: pointer;
  font-weight: bold;
  color: #182AAC;
  padding: 4px;
 }

.tab-header-def .tab-item-def.selected {
  background-image: linear-gradient(180deg, rgb(255, 255, 255) 0%, rgb(74, 176, 247) 100%);
  -webkit-background-clip: text;
  color: transparent;
}
.tab-def-block .tab-header-def-block {
  display: inline-block;
  width: 14px;
  height: 14px;
  background-color: rgba(6, 27, 127, 1);
  margin-right: 16px;
  cursor: pointer;
}

.tab-def-block .tab-header-def-block.selected {
  border: solid 3px rgba(53, 172, 255, 1);
}

.tab-style1 {

}
.tab-header-style1 .tab-item-style1 {
  float: left;
  text-align: center;
  cursor: pointer;
  font-weight: bold;
  color: #55A7C0;
  padding: 4px;
 }

.tab-header-style1 .tab-item-style1.selected {
  color: #FFFFFF;
}
</style>
