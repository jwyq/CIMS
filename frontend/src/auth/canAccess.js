function hasAny(values = [], expected = []) {
  if (!expected || expected.length === 0) {
    return true;
  }
  return expected.some((item) => values.includes(item));
}

function hasAll(values = [], expected = []) {
  if (!expected || expected.length === 0) {
    return true;
  }
  return expected.every((item) => values.includes(item));
}

/**
 * 仅按资源码判定可见性/可访问性（不校验角色是否满足 rule 中的 anyRoles/allRoles）。
 * 用户具备的资源码来自登录：角色 → 角色资源 → resourceCodes。
 */
export function canAccess(user, rule = {}) {
  if (!user) {
    return false;
  }
  const userResourceCodes = user.resourceCodes || [];

  return (
    hasAny(userResourceCodes, rule.anyResourceCodes) &&
    hasAll(userResourceCodes, rule.allResourceCodes)
  );
}
