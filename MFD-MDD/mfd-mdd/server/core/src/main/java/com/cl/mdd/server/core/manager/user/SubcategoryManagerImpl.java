package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.CertificateType;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.manager.annotation.Manager;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Manager
public class SubcategoryManagerImpl implements SubcategoryManager {

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Override
    public List<SubCategory> findAll() {
        return subCategoryDao.findAllByOrderByName();
    }

    @Override
    public SubCategory findOne(String id) {
        return subCategoryDao.findOne(id);
    }

    @Override
    public Set<String> mandatoryCertificatesForSubcategory(SubCategory subCategory) {
        return CollectionUtils.emptyIfNull(subCategory.getCertificateTypes()).stream()
                .filter(certificateType -> !certificateType.isOptional())
                .map(CertificateType::getId).collect(Collectors.toSet());
    }

}
