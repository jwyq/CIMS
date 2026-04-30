/** 客户模块共用枚举（与后端状态域一致） */
export const CUSTOMER_STATUS_OPTIONS = ["ACTIVE", "INACTIVE", "FROZEN"];

/** 证件类型（与注册/入库编码一致；查询下拉可全量筛选） */
export const CUSTOMER_ID_TYPE_OPTIONS = [
  { label: "统一社会信用代码", value: "UNIFIED_SOCIAL_CREDIT" },
  { label: "营业执照", value: "BUSINESS_LICENSE" },
  { label: "组织机构代码", value: "ORG_CODE" },
  { label: "身份证", value: "ID_CARD" },
  { label: "护照", value: "PASSPORT" },
  { label: "港澳居民来往内地通行证", value: "HK_MO_PERMIT" },
  { label: "台湾居民来往大陆通行证", value: "TW_PERMIT" }
];

export function getCustomerIdTypeLabel(code) {
  if (!code) return "—";
  const hit = CUSTOMER_ID_TYPE_OPTIONS.find((o) => o.value === code);
  return hit ? hit.label : code;
}

/** 字典未加载时的兜底（与 sys_dict_entry COUNTRY_REGION / ID_COUNTRY_REGION 一致） */
export const REGION_CODE_LABEL = {
  CN: "中国",
  HK: "中国香港",
  MO: "中国澳门",
  SG: "新加坡",
  US: "美国"
};

export const FALLBACK_REGION_OPTIONS = Object.entries(REGION_CODE_LABEL).map(([value, label]) => ({
  label,
  value
}));

/**
 * @param {string|null|undefined} code
 * @param {Record<string, string>} [labelMap] 来自 /reference/dicts?type=COUNTRY_REGION 等合并后的 code→中文
 */
export function getRegionLabel(code, labelMap) {
  if (!code) return "—";
  if (labelMap && Object.prototype.hasOwnProperty.call(labelMap, code)) {
    return labelMap[code];
  }
  return REGION_CODE_LABEL[code] || code;
}

/** 客户状态 code → 中文（与 sys_dict_entry CUSTOMER_STATUS 一致） */
export const CUSTOMER_STATUS_LABEL_ZH = {
  ACTIVE: "正常",
  INACTIVE: "停用",
  FROZEN: "冻结"
};

export function getCustomerStatusLabelZh(code) {
  if (!code) return "—";
  return CUSTOMER_STATUS_LABEL_ZH[code] || code;
}

/** 公司客户注册 remark JSON：证件国别/地区 code */
export function getIdCountryRegionCodeFromRemark(remark) {
  return getRemarkField(remark, "idCountryRegion");
}

/** 公司客户注册 remark JSON：国别/地区 code */
export function getCountryRegionCodeFromRemark(remark) {
  return getRemarkField(remark, "countryRegion");
}

function getRemarkField(remark, key) {
  if (!remark || typeof remark !== "string") return null;
  try {
    const o = JSON.parse(remark);
    if (o && typeof o === "object" && o[key] != null) {
      return String(o[key]);
    }
  } catch {
    /* ignore */
  }
  return null;
}
