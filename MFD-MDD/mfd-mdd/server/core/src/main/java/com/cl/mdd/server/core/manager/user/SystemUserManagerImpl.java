package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.model.SystemUserModel;
import com.cl.mdd.server.core.data.model.query.QueryInfo;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.manager.annotation.Manager;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;

@Manager
public class SystemUserManagerImpl implements SystemUserManager {

    private final static Map<String, String> DICTIONARY = new HashMap<>();

    @Autowired
    private SystemUserDao userDao;

    @Autowired
    private QueryConverter queryConverter;

    @Autowired
    private CommonConverter commonConverter;

    @Override
    public SystemUser get(String id) {
        return userDao.findOne(id);
    }

    @Override
    public QueryResult<SystemUserModel> findAll(QueryInfo queryInfo) {
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination(), DICTIONARY);
        Page<SystemUser> systemUsers = userDao.findAll(pageable);
        return queryConverter.toQueryResult(systemUsers, commonConverter::toSystemUserModel);
    }

    @Override
    public SystemUser save(SystemUser systemUser) {
        return userDao.save(systemUser);
    }
}
