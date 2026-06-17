package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.model.query.FindAllProfessionals;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.access.user.ProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ProfessionalManagerImplTest {

    @Spy
    @InjectMocks
    private ProfessionalManagerImpl manager = new ProfessionalManagerImpl();
    @Mock
    private CommonConverter commonConverter;
    @Mock
    private ProfessionalDao professionalDao;
    @Mock
    private QueryConverter queryConverter;
    @Mock
    private FindAllProfessionals queryInfo;
    @Mock
    private FindAllProfessionals.Filters filters;
    @Mock
    private Pageable pageable;
    @Mock
    private Page<Professional> professionals;
    @Mock
    private QueryResult queryResult;
    @Mock
    private List<Professional> pageContent;

    private String emailParam = "email";

    @Before
    public void setUp() throws Exception {
    }


}