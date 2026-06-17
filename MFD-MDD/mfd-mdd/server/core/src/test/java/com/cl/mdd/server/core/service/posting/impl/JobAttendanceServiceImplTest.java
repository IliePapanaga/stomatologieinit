package com.cl.mdd.server.core.service.posting.impl;

import com.cl.mdd.server.core.data.model.AddNoShowModel;
import com.cl.mdd.server.core.data.model.NoShowModel;
import com.cl.mdd.server.core.data.model.UpdateNoShowModel;
import com.cl.mdd.server.core.data.model.query.FindProfessionalNoShows;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.access.posting.TemporaryJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.access.prodessional.ProfessionalProfileDao;
import com.cl.mdd.server.core.data.persistent.access.user.NoShowDao;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.user.professional.NoShow;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.posting.attendance.*;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.manager.converter.QueryConverter;
import com.cl.mdd.server.core.manager.user.ProfessionalManager;
import com.cl.mdd.server.core.manager.user.professional.JobPreferenceManager;
import com.cl.mdd.server.core.security.SecurityAccess;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.contact.ContactService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobAttendanceServiceImplTest {

    @Spy
    @InjectMocks
    private JobAttendanceServiceImpl testClass;

    @Mock
    private ProfessionalManager professionalManager;

    @Mock
    private SecurityAccess securityAccess;

    @Mock
    private ProfessionalProfileDao professionalProfileDao;

    @Mock
    private JobPreferenceManager jobPreferenceManager;

    @Mock
    private CommonConverter commonConverter;

    @Mock
    private JobDayDao jobDayDao;

    @Mock
    private UserDao userDao;

    @Mock
    private Professional professional;

    @Mock
    private TemporaryJobPostingApplication temporaryJobPostingApplication;

    @Mock
    private QueryConverter queryConverter;

    @Mock
    private ContactService contactService;

    @Captor
    private ArgumentCaptor<NoShow> noShowArgumentCaptor;

    @Mock
    private TemporaryJobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Mock
    private NoShowDao noShowDao;

    @Spy
    protected TransactionHelper transactionHelper;

    @Mock
    private EventBus<AttendanceAlertedEvent> attendanceAlertedEventEventBus;

    @Mock
    private EventBus<AttendanceAlertRepliedEvent> attendanceAlertRepliedEventEventBus;

    @Mock
    private EventBus<AttendanceSosRequestedEvent> attendanceSosRequestedEventEventBus;

    @Mock
    private EventBus<AttendanceCheckedInEvent> attendanceCheckedInEventEventBus;

    @Mock
    private EventBus<AttendanceNoShowEvent> attendanceNoShowEventEventBus;


    public static final String ID = "id";

    public static final String PROFESSIONAL_ID = "proId";

    @Test
    public void updateNoShow() throws Exception {
        NoShow noShow = new NoShow();
        String id = "id";
        String comments = "comments";
        UpdateNoShowModel noShowModel = new UpdateNoShowModel(id, comments);

        doReturn(noShow).when(noShowDao).getOne(id);
        doReturn(noShow).when(noShowDao).save(noShow);

        testClass.updateNoShow(noShowModel);

        verify(noShowDao).getOne(id);
        verify(noShowDao).save(noShow);
        assertEquals(comments, noShowModel.getComments());
    }

    @Test
    public void dismissNoShow() throws Exception {
        Professional pro = new Professional();
        NoShow noShow = new NoShow();
        noShow.setProfessional(pro);
        String id = "id";
        String comments = "comments";
        UpdateNoShowModel noShowModel = new UpdateNoShowModel(id, comments);

        doReturn(noShow).when(noShowDao).getOne(id);
        doReturn(noShow).when(noShowDao).saveAndFlush(noShow);
        doNothing().when(testClass).recalculateProNoShows(pro);

        testClass.dismissNoShow(noShowModel);

        verify(noShowDao).getOne(id);
        verify(noShowDao).saveAndFlush(noShow);
        verify(testClass).recalculateProNoShows(pro);
        assertEquals(comments, noShowModel.getComments());
        assertEquals(NoShow.CLEARED, noShow.getStatus());
    }

    @Test
    public void recalculateProNoShows() throws Exception {
        Professional pro = new Professional();
        NoShow first = new NoShow();
        first.setStatus(NoShow.REGISTERED);
        NoShow second = new NoShow();
        second.setStatus(NoShow.REGISTERED);
        NoShow third = new NoShow();
        third.setStatus(NoShow.CLEARED);

        pro.setNoWorks(newHashSet(first, second, third));
        doReturn(pro).when(professionalManager).save(pro);

        testClass.recalculateProNoShows(pro);

        verify(professionalManager).save(pro);
        assertEquals(2, pro.getNoShow());

    }

    @Test
    public void noShow() throws Exception {
        NoShowModel noShowModel = new NoShowModel();
        NoShow dbNoShow = new NoShow();
        doReturn(dbNoShow).when(noShowDao).getOne(ID);
        doReturn(noShowModel).when(commonConverter).toNoShowModel(dbNoShow);

        NoShowModel actual = testClass.noShow(ID);

        verify(commonConverter).toNoShowModel(dbNoShow);
        assertNotNull(actual);
        assertSame(noShowModel, actual);
    }

    @Test
    public void getNoShows() throws Exception {
        Pageable pageable = new PageRequest(1, 1);
        FindProfessionalNoShows queryInfo = new FindProfessionalNoShows();
        queryInfo.getFilters().setProfessionalId(PROFESSIONAL_ID);
        Page<NoShowModel> professionalNoShow = new PageImpl<NoShowModel>(asList());
        QueryResult<NoShowModel> expected = new QueryResult<>();
        doReturn(pageable).when(queryConverter).toPageable(queryInfo.getPagination());
        doReturn(professionalNoShow).when(professionalManager).getProfessionalNoShow(PROFESSIONAL_ID, pageable);
        doReturn(expected).when(queryConverter).toQueryResult(eq(professionalNoShow), any());

        QueryResult<NoShowModel> actual = testClass.getNoShows(queryInfo);

        verify(professionalManager).getProfessionalNoShow(PROFESSIONAL_ID, pageable);
        verify(queryConverter).toQueryResult(eq(professionalNoShow), any());
        assertNotNull(actual);
        assertSame(expected, actual);
    }

    @Test
    public void addNoShow() throws Exception {
        NoShowModel expected = new NoShowModel();
        NoShow noShow = new NoShow();
        noShow.setProfessional(professional);
        String jobDayId = "jdId";
        JobDay jobDay = new JobDay();
        AddNoShowModel noShowModel = new AddNoShowModel();
        noShowModel.setJobDayId(jobDayId);
        doReturn(temporaryJobPostingApplication).when(temporaryJobPostingApplicationDao).findOneByAttendanceId(jobDayId);
        doReturn(professional).when(temporaryJobPostingApplication).getProfessional();
        doReturn(jobDay).when(jobDayDao).getOne(jobDayId);
        doReturn(noShow).when(noShowDao).saveAndFlush(noShowArgumentCaptor.capture());
        doNothing().when(testClass).recalculateProNoShows(professional);
        doReturn(expected).when(commonConverter).toNoShowModel(noShow);

        NoShowModel actual = testClass.addNoShow(noShowModel);

        verify(jobDayDao).getOne(jobDayId);
        verify(testClass).recalculateProNoShows(professional);
        NoShow savedNoShow = noShowArgumentCaptor.getValue();
        assertNotNull(savedNoShow);
        assertNotNull(savedNoShow.getModified());
        assertSame(professional, savedNoShow.getProfessional());
        assertSame(jobDay, savedNoShow.getJobDay());
        assertEquals(NoShow.REGISTERED, savedNoShow.getStatus());

        assertNotNull(actual);
        assertSame(expected, actual);
    }

}