package com.cl.mdd.server.core.service.practice;

import com.cl.mdd.server.core.data.model.AddPracticeLocation;
import com.cl.mdd.server.core.data.model.PracticeLocationModel;
import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.model.UpdatePracticeLocation;
import com.cl.mdd.server.core.data.model.query.FindAllPracticeLocationsQuery;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.service.Service;
import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;

import javax.validation.Valid;
import java.util.List;

public interface PracticeLocationService extends Service {

    PracticeLocationModel execute(@Valid AddPracticeLocation addPracticeLocation);

    PracticeLocationModel execute(@Valid UpdatePracticeLocation updatePracticeLocation);

    void delete(@Valid @ExpressionConstraint(expression = "@jobPostingDao.countByStatusesFromPracticeLocation({'ACTIVE'}, #this) == 0", message = "{practice.location.delete.with.active.postings}") String id);

    PracticeLocationModel get(String id);

    QueryResult<PracticeLocationModel> findAllPracticeLocations(FindAllPracticeLocationsQuery queryInfo);

    List<PracticeLocationModel> getPracticeLocations(PracticeModel practiceModel);

}
