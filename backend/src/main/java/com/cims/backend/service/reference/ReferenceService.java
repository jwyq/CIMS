package com.cims.backend.service.reference;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cims.backend.dto.reference.DictEntryResponse;
import com.cims.backend.dto.reference.OrgOptionResponse;
import com.cims.backend.entity.system.SysDictEntryEntity;
import com.cims.backend.entity.system.SysOrgEntity;
import com.cims.backend.entity.system.SysUserEntity;
import com.cims.backend.mapper.system.SysDictEntryMapper;
import com.cims.backend.mapper.system.SysOrgMapper;
import com.cims.backend.mapper.system.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReferenceService {

    public static final String DICT_ID_TYPE = "ID_TYPE";
    public static final String DICT_CUSTOMER_STATUS = "CUSTOMER_STATUS";
    /** 国别/地区、证件国别/地区（code 一致，分类型便于扩展） */
    public static final String DICT_COUNTRY_REGION = "COUNTRY_REGION";
    public static final String DICT_ID_COUNTRY_REGION = "ID_COUNTRY_REGION";

    private final SysDictEntryMapper dictEntryMapper;
    private final SysOrgMapper sysOrgMapper;
    private final SysUserMapper sysUserMapper;

    public ReferenceService(
        SysDictEntryMapper dictEntryMapper,
        SysOrgMapper sysOrgMapper,
        SysUserMapper sysUserMapper
    ) {
        this.dictEntryMapper = dictEntryMapper;
        this.sysOrgMapper = sysOrgMapper;
        this.sysUserMapper = sysUserMapper;
    }

    public List<DictEntryResponse> listDictEntries(String dictType) {
        List<SysDictEntryEntity> rows = dictEntryMapper.selectList(
            new LambdaQueryWrapper<SysDictEntryEntity>()
                .eq(SysDictEntryEntity::getDictType, dictType)
                .eq(SysDictEntryEntity::getStatus, 1)
                .orderByAsc(SysDictEntryEntity::getSortNo)
                .orderByAsc(SysDictEntryEntity::getId)
        );
        return rows.stream()
            .map(r -> new DictEntryResponse(r.getCode(), r.getLabelZh(), r.getSortNo()))
            .collect(Collectors.toList());
    }

    /** 按字典类型与 code 取中文名；无映射时返回 null */
    public String findDictLabel(String dictType, String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        SysDictEntryEntity one = dictEntryMapper.selectOne(
            new LambdaQueryWrapper<SysDictEntryEntity>()
                .eq(SysDictEntryEntity::getDictType, dictType)
                .eq(SysDictEntryEntity::getCode, code)
                .eq(SysDictEntryEntity::getStatus, 1)
                .last("LIMIT 1")
        );
        return one != null ? one.getLabelZh() : null;
    }

    public Map<String, String> labelsByCodes(String dictType, Collection<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<String> set = codes.stream().filter(Objects::nonNull).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
        if (set.isEmpty()) {
            return Collections.emptyMap();
        }
        List<SysDictEntryEntity> rows = dictEntryMapper.selectList(
            new LambdaQueryWrapper<SysDictEntryEntity>()
                .eq(SysDictEntryEntity::getDictType, dictType)
                .in(SysDictEntryEntity::getCode, set)
                .eq(SysDictEntryEntity::getStatus, 1)
        );
        Map<String, String> map = new LinkedHashMap<>();
        for (SysDictEntryEntity r : rows) {
            map.put(r.getCode(), r.getLabelZh());
        }
        return map;
    }

    public List<OrgOptionResponse> listOrgs() {
        List<SysOrgEntity> rows = sysOrgMapper.selectList(
            new LambdaQueryWrapper<SysOrgEntity>()
                .eq(SysOrgEntity::getStatus, 1)
                .orderByAsc(SysOrgEntity::getId)
        );
        return rows.stream()
            .map(o -> new OrgOptionResponse(o.getId(), o.getOrgCode(), o.getOrgName()))
            .collect(Collectors.toList());
    }

    public String findOrgName(Long orgId) {
        if (orgId == null) {
            return null;
        }
        SysOrgEntity o = sysOrgMapper.selectById(orgId);
        return o != null ? o.getOrgName() : null;
    }

    public Map<Long, String> orgNamesByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> set = ids.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (set.isEmpty()) {
            return Collections.emptyMap();
        }
        List<SysOrgEntity> rows = sysOrgMapper.selectBatchIds(set);
        Map<Long, String> map = new LinkedHashMap<>();
        for (SysOrgEntity o : rows) {
            map.put(o.getId(), o.getOrgName());
        }
        return map;
    }

    public String findUserDisplayName(Long userId) {
        if (userId == null) {
            return null;
        }
        SysUserEntity u = sysUserMapper.selectById(userId);
        return u != null ? u.getDisplayName() : null;
    }

    public Map<Long, String> userDisplayNamesByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> set = ids.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (set.isEmpty()) {
            return Collections.emptyMap();
        }
        List<SysUserEntity> rows = sysUserMapper.selectBatchIds(set);
        Map<Long, String> map = new LinkedHashMap<>();
        for (SysUserEntity u : rows) {
            map.put(u.getId(), u.getDisplayName());
        }
        return map;
    }
}
