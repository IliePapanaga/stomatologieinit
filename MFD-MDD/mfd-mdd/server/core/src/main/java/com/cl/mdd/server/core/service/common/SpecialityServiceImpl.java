package com.cl.mdd.server.core.service.common;

import com.cl.mdd.server.core.data.model.common.SpecialityModel;
import com.cl.mdd.server.core.data.persistent.access.common.SpecialityDao;
import com.cl.mdd.server.core.service.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class SpecialityServiceImpl extends ServiceSupport implements SpecialityService {

    @Autowired
    private SpecialityDao specialityDao;

    @Override
    public SpecialityModel get(String id) {
        return commonConverter.toSpecialityModel(specialityDao.findOne(id));
    }

    @Override
    public List<SpecialityModel> getAll() {
        return specialityDao.findAll().stream()
                .map(commonConverter::toSpecialityModel)
                .collect(Collectors.toList());
    }

}
