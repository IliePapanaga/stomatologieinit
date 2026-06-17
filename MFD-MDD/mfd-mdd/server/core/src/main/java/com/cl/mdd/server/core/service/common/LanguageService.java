package com.cl.mdd.server.core.service.common;

import com.cl.mdd.server.core.data.model.common.LanguageModel;

import java.util.List;

public interface LanguageService {

    LanguageModel get(String id);

    List<LanguageModel> getAll();

}
