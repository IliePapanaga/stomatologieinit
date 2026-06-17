package com.cl.mdd.server.core.service.common;

import com.cl.mdd.server.core.data.model.common.AcademicDegreeModel;
import com.cl.mdd.server.core.data.persistent.access.common.AcademicDegreeDao;
import com.cl.mdd.server.core.service.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class AcademicDegreeServiceImpl extends ServiceSupport implements AcademicDegreeService {

    @Autowired
    private AcademicDegreeDao academicDegreeDao;

    @Override
    public AcademicDegreeModel get(String id) {
        return commonConverter.toAcademicDegreeModel(academicDegreeDao.findOne(id));
    }

    @Override
    public List<AcademicDegreeModel> getAll() {
        return academicDegreeDao.findAll().stream()
                .map(commonConverter::toAcademicDegreeModel)
                .collect(Collectors.toList());
    }


}
