/*
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */

export const plugin = store => {
  store.subscribe((mutation, state) => {
    localStorage.setItem("state",JSON.stringify(state));
  })
}
