/*
 * Copyright 2021 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */

export let handleVueError = (err, vm, info) => {
  console.error(err, vm, info);
}
export let handleVueWarning = (msg, vm, trace) => {
  console.warn(msg, vm, trace);
}
export let handleGlobalError = (message, source, lineno, colno, error) => {
  console.error(message, source, lineno, colno, error);
}
export let handleGlobalRejection = (e) => {
  console.error(e);
}
