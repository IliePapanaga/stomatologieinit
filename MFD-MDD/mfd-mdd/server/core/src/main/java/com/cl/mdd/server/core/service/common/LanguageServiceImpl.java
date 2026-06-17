package com.cl.mdd.server.core.service.common;

import com.cl.mdd.server.core.data.model.common.LanguageModel;
import com.cl.mdd.server.core.data.persistent.access.common.LanguageDao;
import com.cl.mdd.server.core.service.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class LanguageServiceImpl extends ServiceSupport implements LanguageService {

    @Autowired
    private LanguageDao languageDao;

    @Override
    public LanguageModel get(String id) {
        return commonConverter.toLanguageModel(languageDao.findOne(id));
    }

    @Override
    public List<LanguageModel> getAll() {
        return languageDao.findAll().stream()
                .map(commonConverter::toLanguageModel)
                .collect(Collectors.toList());
    }


}
