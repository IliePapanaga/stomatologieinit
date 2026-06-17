package com.cl.mdd.server.core.service.common;

import com.cl.mdd.server.core.data.model.common.SpecialityModel;

import java.util.List;

public interface SpecialityService {

    SpecialityModel get(String id);

    List<SpecialityModel> getAll();

}
