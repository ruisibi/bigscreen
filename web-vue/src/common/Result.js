/*
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */

export class Result{
  status = 0;
  data = {};
  msg = "";
  constructor(options = {}){
    this.status = options.status || 0;
    this.data = options.data || {};
    this.msg = options.msg || "";
  }
  static success = options => new Result(Object.assign(options,{status:1}));
  static fail = options => new Result(Object.assign(options,{status:0}));
  static build = res => new Result(res);
  get isSuccess(){
    return this.status === 1;
  }
}

