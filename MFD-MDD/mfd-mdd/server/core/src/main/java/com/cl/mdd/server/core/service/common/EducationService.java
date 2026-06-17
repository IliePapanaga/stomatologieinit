package com.cl.mdd.server.core.service.common;

import com.cl.mdd.server.core.data.model.common.EducationModel;

import java.util.List;

public interface EducationService {

    EducationModel get(String id);

    List<EducationModel> getAll();

}
