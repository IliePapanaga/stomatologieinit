package com.cl.mdd.server.core.service.common;

import com.cl.mdd.server.core.data.model.common.AcademicDegreeModel;

import java.util.List;

public interface AcademicDegreeService {

    AcademicDegreeModel get(String id);

    List<AcademicDegreeModel> getAll();

}
