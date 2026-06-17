package com.cl.mdd.server.core.service.common;

import com.cl.mdd.server.core.data.model.common.EducationModel;
import com.cl.mdd.server.core.data.persistent.access.common.EducationDao;
import com.cl.mdd.server.core.service.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class EducationServiceImpl extends ServiceSupport implements EducationService {

    @Autowired
    private EducationDao educationDao;

    @Override
    public EducationModel get(String id) {
        return commonConverter.toEducationModel(educationDao.findOne(id));
    }

    @Override
    public List<EducationModel> getAll() {
        return educationDao.findAll().stream()
                .map(commonConverter::toEducationModel)
                .collect(Collectors.toList());
    }


}
