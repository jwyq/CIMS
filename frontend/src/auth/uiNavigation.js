import { navigationCatalog } from "./navigationConfig";

/**
 * 导航结构与「路径/子模块 ↔ 资源码条件」来自本地静态配置；
 * 用户是否具备权限仅依据登录返回的 resourceCodes（后端按角色汇总），不再从服务端拉取规则对象。
 */
export function getUiNavigation(/* user 仅保留形参，便于调用处统一传入；规则不依赖用户对象 */ _user) {
  return navigationCatalog;
}
