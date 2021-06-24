
package com.yusei.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


import com.yusei.service.IFieldExtendRuleDetailService;
import com.yusei.dao.FieldExtendRuleDetailDao;
import com.yusei.model.entity.FieldExtendRuleDetail;
import org.springframework.util.CollectionUtils;

/**
 * 扩展规则详情表Service实现
 *
 * @author generated by liuqiang
 * @date 2020-09-02 13:58:18
 */
@Service
public class IFieldExtendRuleDetailServiceImpl implements IFieldExtendRuleDetailService {

    @Autowired
    private FieldExtendRuleDetailDao fieldExtendRuleDetailDao;

    @Override
    public void insertBatch(List<FieldExtendRuleDetail> fieldExtendRuleDetailList) {
      if ( !CollectionUtils.isEmpty(fieldExtendRuleDetailList) ){
        fieldExtendRuleDetailDao.insertBatch(fieldExtendRuleDetailList);
      }

    }

    /**
     * <p>description: 根据规则查询规则详情 </p>
     * @author: liuqiang
     * @date: 2020/9/2  14:56
     */
    @Override
    public Map<Long, List<FieldExtendRuleDetail>> selectMapByRuleIdList(List<Long> extendRuleIdList) {
        Map<Long, List<FieldExtendRuleDetail>> resultMap = new HashMap<>();
        if ( CollectionUtils.isEmpty(extendRuleIdList)){
            return resultMap;
        }

        List<FieldExtendRuleDetail> detailList = fieldExtendRuleDetailDao.selectListByRuleIdList(extendRuleIdList);
        if ( CollectionUtils.isEmpty(detailList)){
            return resultMap;
        }

        detailList.forEach(e -> {
            List<FieldExtendRuleDetail> list = resultMap.get(e.getFieldExtendRuleId());
            if ( CollectionUtils.isEmpty(list)){
                list = new ArrayList<>();
                resultMap.put(e.getFieldExtendRuleId(), list);
            }

           list.add(e);
        });

        return resultMap;
    }

    @Override
    public int deleteByExtendRuleIdList(List<Long> ruleIdList) {
        if ( CollectionUtils.isEmpty(ruleIdList)){
            return 0;
        }

        return fieldExtendRuleDetailDao.deleteByExtendRuleIdList(ruleIdList);
    }
}
