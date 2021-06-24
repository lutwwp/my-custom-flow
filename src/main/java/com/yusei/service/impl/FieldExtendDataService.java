package com.yusei.service.impl;

import com.yusei.dao.FieldExtendDataDao;
import com.yusei.model.entity.FieldExtendData;
import com.yusei.service.IFieldExtendDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author liuqiang
 * @TOTD
 * @date 2020/8/31 16:32
 */
@Service
public class FieldExtendDataService implements IFieldExtendDataService {

    @Autowired
    private FieldExtendDataDao fieldExtendDataDao;

    /**
     * 根据条件查询FieldExtendData list
     */
    @Override
    public List<FieldExtendData> selectByRecord(FieldExtendData record) {
        return fieldExtendDataDao.selectByRecord(record);
    }

    @Override
    public int deleteByFieldIdList(List<Long> fieldIdList) {
        if ( CollectionUtils.isEmpty(fieldIdList)){
            return 0;
        }

        return fieldExtendDataDao.deleteByFieldIdList(fieldIdList);
    }

    @Override
    public void insertBatch(List<FieldExtendData> fieldExtendData) {
        if (!CollectionUtils.isEmpty(fieldExtendData)){
            fieldExtendDataDao.insertBatch(fieldExtendData);
        }
    }
}
