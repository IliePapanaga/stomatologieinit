package com.cl.mdd.server.core.service.reporting;

import com.cl.mdd.server.core.service.Service;

public interface ReportingService extends Service {

    ReportResponse execute(ReportRequest request);

}
