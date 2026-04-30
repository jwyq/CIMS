# CIMS 分页查询约定（前后端全局规范）

## 1. 适用范围

凡列表类接口需要**服务端分页**时，请求体、响应体中的分页结构均按本文约定实现，避免各接口字段名、页码起始不一致。

与现有 **`ApiResponse<T>`** 外层契约一致：成功时 `code === 0`，业务数据放在 **`data`** 中；分页场景下 **`data` 为 `PageResult` 结构**（见下文）。

未改造的老接口仍可一次返回全量列表（`data` 为数组），新接口或改造后的列表接口应采用本文分页结构。

---

## 2. 请求（PageRequest）

统一放在 **POST** 请求的 **JSON body** 中（与当前系统多数接口风格一致），与业务筛选字段并列。

| 字段        | 类型    | 必填 | 说明 |
|-------------|---------|------|------|
| `page`      | integer | 否   | 当前页码，**从 1 开始**。缺省按 **1** 处理。 |
| `pageSize`  | integer | 否   | 每页条数。缺省按 **10** 处理。建议上限 **200**（后端校验）。 |

**约定：**

- 页码与 [Ant Design Table Pagination](https://ant.design/components/table-cn) 的 `current` 一致（均为 1-based），便于前端直接映射。
- 服务端计算偏移量：  
  `offset = (page - 1) * pageSize`（`page ≥ 1`）。

**示例：**

```json
{
  "page": 2,
  "pageSize": 20,
  "roleName": "模糊条件示例"
}
```

---

## 3. 响应（PageResult，即 `ApiResponse.data`）

| 字段        | 类型           | 说明 |
|-------------|----------------|------|
| `list`      | array          | 当前页数据列表。 |
| `total`     | number (long)  | **符合条件的总记录数**（非当前页条数）。 |
| `page`      | number         | 当前页码（与请求对齐，1-based）。 |
| `pageSize`  | number         | 每页条数（与请求对齐）。 |
| `pages`     | number         | 总页数；`total === 0` 时为 **0**。计算公式：`ceil(total / pageSize)`。 |

**示例：**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [],
    "total": 123,
    "page": 2,
    "pageSize": 20,
    "pages": 7
  }
}
```

**约定：**

- 排序字段名、排序方向由具体业务接口另行约定（如 `sortField` + `sortOrder`），本规范只约束分页四要素与 `list/total`。
- `total` 为过滤后的总数；若接口带权限或数据范围过滤，**total 与 list 必须同一套过滤规则**。

---

## 4. 边界行为

| 场景 | 建议行为 |
|------|----------|
| `page` 超出总页数且 `total > 0` | 返回最后一页数据，或返回空 `list` 并在文档中固定一种；**项目内应统一**，推荐返回**最后一页**。 |
| `total === 0` | `list` 为空数组，`pages` 为 0。 |
| 非法 `page` / `pageSize` | 后端校验失败时返回业务错误码与说明（如 `400` 对应错误码 + message），前端可提示用户或回退到第 1 页。 |

---

## 5. 前端使用说明

1. **Ant Design Table**  
   - `pagination.current` ↔ `page`  
   - `pagination.pageSize` ↔ `pageSize`  
   - 服务端模式：`pagination.total` 取响应中的 `data.total`；`data.list` 作为 `dataSource`。

2. **工具方法**  
   项目提供 `frontend/src/api/pagination.js`（见源码），用于把 Table 的分页状态转成请求体字段、统一默认与上限，避免各页面手写不一致。

3. **类型**  
   前端与后端字段名保持 **camelCase**（`page`, `pageSize`, `total`, `pages`, `list`），与现有 `ApiResponse` JSON 风格一致。

---

## 6. 后端使用说明（MyBatis-Plus 分页插件）

项目使用 **MyBatis-Plus**，分页统一用其**内置分页插件**，不再单独引入 PageHelper。

1. **插件注册**  
   - 见 `com.cims.backend.config.MybatisPlusConfig`：`MybatisPlusInterceptor` + `PaginationInnerInterceptor(DbType.MYSQL)`，`maxLimit` 与 `pageSize` 上限（200）对齐。

2. **DTO**  
   - `PageRequest` / `PageResult`：`com.cims.backend.dto.common` 包。  
   - `PageResults.from(IPage<T>)`：把 `Mapper#selectPage` 返回的 `IPage` 转成对外 `PageResult`（`list` / `total` / `page` / `pageSize` / `pages`）。

3. **查询写法**  
   - 构造 `Page<T> page = new Page<>(request.resolvePage(), request.resolvePageSize());`  
   - `mapper.selectPage(page, queryWrapper);`  
   - 返回：`ApiResponse.success(PageResults.from(page))`；若需转 VO，可对 `page.getRecords()` 映射后再 `PageResult.of(...)`。

4. **约定**  
   - COUNT 与列表由 MP 分页插件自动生成，**WHERE 条件一致**。  
   - 新接口示例：`ApiResponse<PageResult<RoleResponse>> queryRolesPage(@Valid @RequestBody RoleQueryRequest request)`，`RoleQueryRequest` **继承** `PageRequest`。

---

## 7. 版本与变更

- 初版：与当前 `ApiResponse` 及 POST JSON 风格对齐。  
- 后端持久层统一为 **MyBatis-Plus `IPage` + 分页插件**；对外 JSON 仍以本文 `PageResult` 为准。
