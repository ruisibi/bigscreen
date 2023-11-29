<!--
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 -->
<template>
      <div>
      <div align="center" style="font-size:32px; line-height:56px;">交叉表回调函数</div>
        在交叉表中配置回调函数，实现对数据的输出控制。 <br/>
        <ul>
        回调函数参数： <br/>
        <li>
        value：当前值  <br/>
        </li>
        <li>
        col：当前列节点  <br/>
        </li>
        <li>
        row：当前行节点  <br/>
        </li>
        <li>
        data：当前数据  <br/>
        </li>
        <li>
        outType： 输出类型，包括 html/json/excel/csv/pdf/word 等类型  <br/>
        </li>
        <li>
        rowIndex：行号，从0开始  <br/>
        </li>
         <li>
        colIndex： 列号，从0开始  <br/>
        </li>
        </ul>

        <ul>
        <li>
        extContext对象 ：当前环境变量对象  <br/>
        <ul>
        方法：
        <li>put(‘xxx’,’xxx’)方法: 设置值到extContext中，设置后的值可以通过${xxx}获取值。</li>
        <li>get(‘xxx’)方法: 获取extContext中的数据。</li>
        </ul>
        </li>
        </ul>
        <ul>
          返回值：
        <li>
       通过 return 关键字返回内容。

        </li>
        </ul>

        <p style="height:30px;"></p>
        <div align="center" style="font-size:32px; line-height:56px;">表格回调函数</div>

        在表格中配置回调函数，实现对数据的输出控制。 <br/>
        <ul>
        回调函数参数： <br/>
        <li>
        value：当前值  <br/>
        </li>
        <li>
        rowData：当前行数据  <br/>
        </li>
        <li>
        column：当前字段名  <br/>
        </li>
            <li>
                index：当前行,从0开始  <br/>
            </li>
            <li>
              outType： 输出类型，包括 html/json/excel/csv/pdf/word 等类型  <br/>
            </li>
        </ul>
        <ul>
        <li>
        extContext对象 ：当前环境变量对象  <br/>
        <ul>
        方法：
        <li>put(‘xxx’,’xxx’)方法: 设置值到extContext中，设置后的值可以通过${xxx}获取值。</li>
        <li>get(‘xxx’)方法: 获取extContext中的数据。</li>
        </ul>
        </li>
        </ul>

         <ul>
           返回值：
        <li>
        通过 return 关键字返回内容。

        </li>
        </ul>



      <div align="center">
        <el-button @click="closeme()">关 闭</el-button>
      </div>
      </div>
</template>

<script>
import {baseUrl} from '@/common/biConfig'
import * as layout from './Utils'
import $ from 'jquery'

export default {
  components:{

  },
  data(){
    return {

    }
  },
  mounted(){

  },
  computed: {

  },
  methods: {
     closeme(){
       window.close();
     }
  }
}
</script>
