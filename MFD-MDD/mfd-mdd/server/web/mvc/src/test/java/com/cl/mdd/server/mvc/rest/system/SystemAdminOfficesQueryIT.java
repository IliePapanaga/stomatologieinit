package com.cl.mdd.server.mvc.rest.system;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.model.SystemUserPracticeModel;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeLocationDao;
import com.cl.mdd.server.core.data.persistent.access.user.PracticeOwnerDao;
import com.cl.mdd.server.core.data.persistent.access.user.ProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeOwnerWorker;
import com.cl.mdd.server.mvc.rest.practice.worker.PracticeWorker;
import com.cl.mdd.server.mvc.rest.professional.ProfessionalWorker;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.cl.mdd.server.core.data.model.query.FindSystemUserPractices.FindSystemUserPracticesOrders.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.Assert.*;

@ActiveProfiles({"hsqldb-local"})//, "USE_REAL_GOOGLE_API_SERVICE"})
public class SystemAdminOfficesQueryIT extends BaseMvcIntegrationTest {

    public static final String FIRST_NAME_BASE = "firstName";

    public static final String LAST_NAME_BASE = "lastName";

    public static final String INACTIVE = "INACTIVE";

    public static final String OFFICE_NAME_BASE = "officeName";

    public static final String PHONE_BASE = "123456789";

    public static final String MANAGER_NAME_BASE = "managerName";

    public static final String GENERAL_SPECIALTY = "GENERAL";

    public static final String CUSTOM_SPECIALTY = "CUSTOM";

    @Autowired
    private PracticeOwnerWorker practiceOwnerWorker;

    @Autowired
    private PracticeWorker practiceWorker;

    @Autowired
    private SystemUserWorker systemUserWorker;

    @Autowired
    private PracticeLocationDao practiceLocationDao;

    @Autowired
    private PracticeOwnerDao practiceOwnerDao;

    @Autowired
    private ProfessionalWorker professionalWorker;

    @Autowired
    private ProfessionalDao professionalDao;

    @Autowired
    private TransactionHelper transactionHelper;

    private RegisterPracticeOwner practiceOwner;

    private RegisterPracticeOwner practiceOwner2;

    private AddPracticeLocation addBucharestPracticeLocation;

    private PracticeLocationModel bucharestPracticeLocation;

    private AddPracticeLocation whiteHouse;

    private PracticeLocationModel whiteHousePracticeLocation;

    private PracticeOwnerModel registered1;

    private PracticeOwnerModel registered2;

    private ZonedDateTime creationTime;

    private ZonedDateTime creationTime2;

    private PracticeLocationModel parisPracticeLocation;

    @Before
    public void setUp() throws Exception {
        // CREATE PRACTICE OWNER WITH STATUS EMAIL CONFIRMATION PENDING (SHOULD NOT BE VISIBLE IN QUERIES)
        practiceOwnerWorker.register(create(RegisterPracticeOwner.class));
        // CREATE PRACTICE OWNER
        practiceOwner = create(RegisterPracticeOwner.class);
        practiceOwner.getContact().getName().setFirst(FIRST_NAME_BASE + "A");
        practiceOwner.getContact().getName().setLast(LAST_NAME_BASE + "A");
        practiceOwner.getRegisterPractice().setName(OFFICE_NAME_BASE + "A");
        practiceOwner.getRegisterPractice().setOfficeManagerName(MANAGER_NAME_BASE + "A");
        practiceOwner.getRegisterPractice().setPhone(PHONE_BASE + "1");
        practiceOwner.getRegisterPractice().setSpecialities(singleton(GENERAL_SPECIALTY));
        registered1 = practiceOwnerWorker.registerAndActivate(practiceOwner);
        // ADD BUCHAREST PRACTICE LOCATION
        addBucharestPracticeLocation = create(AddPracticeLocation.class);
        addBucharestPracticeLocation.getContact().setAddress(BUCHAREST_ADDRESS);
        bucharestPracticeLocation = practiceWorker.addPracticeLocation(addBucharestPracticeLocation, practiceOwner);
        // ADD second practice location for practice owner one
        AddPracticeLocation paris = create(AddPracticeLocation.class);
        paris.getContact().setAddress(PARIS_ADDRESS);
        parisPracticeLocation = practiceWorker.addPracticeLocation(paris, practiceOwner);

        creationTime = ZonedDateTime.now();
        // CREATE PRACTICE OWNER2
        practiceOwner2 = create(RegisterPracticeOwner.class);
        practiceOwner2.getContact().getName().setFirst(FIRST_NAME_BASE + "B");
        practiceOwner2.getContact().getName().setLast(LAST_NAME_BASE + "B");
        practiceOwner2.getRegisterPractice().setName(OFFICE_NAME_BASE + "B");
        practiceOwner2.getRegisterPractice().setOfficeManagerName(MANAGER_NAME_BASE + "B");
        practiceOwner2.getRegisterPractice().setPhone(PHONE_BASE + "2");
        practiceOwner2.getRegisterPractice().setSpecialities(singleton(CUSTOM_SPECIALTY));
        registered2 = practiceOwnerWorker.registerAndActivate(practiceOwner2);
        creationTime2 = ZonedDateTime.now();

        // ADD WHITE HOUSE PRACTICE LOCATION
        whiteHouse = create(AddPracticeLocation.class);
        whiteHouse.getContact().setAddress(WASHINGTON_ADDRESS);
        whiteHousePracticeLocation = practiceWorker.addPracticeLocation(whiteHouse, practiceOwner2);
    }

    public void activateDeactivatePracticeOwner() throws Exception {
        String id = registered1.getId();
        transactionHelper.executeInTransaction(() -> {
            PracticeOwner db = practiceOwnerDao.getOne(id);
            assertNotNull(db);
            assertEquals(User.ACTIVE, db.getStatus());
        });

        UserActivateDeactivateResult userActivateDeactivateResult = systemUserWorker.activateDeactivatePracticeOwner(id, false, null, null, SYSTEM_CREDENTIALS);
        assertNotNull(userActivateDeactivateResult);
        assertEquals(id, userActivateDeactivateResult.getId());
        assertEquals(User.INACTIVE, userActivateDeactivateResult.getStatus());

        transactionHelper.executeInTransaction(() -> {
            PracticeOwner db = practiceOwnerDao.getOne(id);
            assertNotNull(db);
            assertEquals(User.INACTIVE, db.getStatus());
        });

        userActivateDeactivateResult = systemUserWorker.activateDeactivatePracticeOwner(id, true, null, null, SYSTEM_CREDENTIALS);
        assertNotNull(userActivateDeactivateResult);
        assertEquals(id, userActivateDeactivateResult.getId());
        assertEquals(User.ACTIVE, userActivateDeactivateResult.getStatus());

        transactionHelper.executeInTransaction(() -> {
            PracticeOwner db = practiceOwnerDao.getOne(id);
            assertNotNull(db);
            assertEquals(User.ACTIVE, db.getStatus());
        });

        // requires SYSTEM_USER_ROLE
        systemUserWorker.activateDeactivatePracticeOwner(id, false, "Access is denied", "activateDeactivatePracticeOwner", toHttpBasic(practiceOwner));
    }

    public void activateDeactivateProfessional() throws Exception {
        RegisterProfessional proAccount = create(RegisterProfessional.class);
        ProfessionalModel registered = professionalWorker.registerAndActivate(proAccount);
        String id = registered.getId();

        transactionHelper.executeInTransaction(() -> {
            Professional db = professionalDao.getOne(id);
            assertNotNull(db);
            assertEquals(User.ACTIVE, db.getStatus());
        });

        UserActivateDeactivateResult userActivateDeactivateResult = systemUserWorker.activateDeactivateProfessional(id, false, null, null, SYSTEM_CREDENTIALS);
        assertNotNull(userActivateDeactivateResult);
        assertEquals(id, userActivateDeactivateResult.getId());
        assertEquals(User.INACTIVE, userActivateDeactivateResult.getStatus());

        transactionHelper.executeInTransaction(() -> {
            Professional db = professionalDao.getOne(id);
            assertNotNull(db);
            assertEquals(User.INACTIVE, db.getStatus());
        });

        userActivateDeactivateResult = systemUserWorker.activateDeactivateProfessional(id, true, null, null, SYSTEM_CREDENTIALS);
        assertNotNull(userActivateDeactivateResult);
        assertEquals(id, userActivateDeactivateResult.getId());
        assertEquals(User.ACTIVE, userActivateDeactivateResult.getStatus());

        transactionHelper.executeInTransaction(() -> {
            Professional db = professionalDao.getOne(id);
            assertNotNull(db);
            assertEquals(User.ACTIVE, db.getStatus());
        });
        // requires SYSTEM_USER_ROLE
        systemUserWorker.activateDeactivateProfessional(id, false, "Access is denied", "activateDeactivateProfessional", toHttpBasic(practiceOwner));
        // requires SYSTEM_USER_ROLE
        systemUserWorker.activateDeactivateProfessional(id, false, "Access is denied", "activateDeactivateProfessional", toHttpBasic(proAccount));
    }


    @Test
    public void query() throws Exception {
        SystemUserPracticeModel first = toModel(practiceOwner, 2, registered1.getId());

        SystemUserPracticeModel second = toModel(practiceOwner2, 1, registered2.getId());

        // pagination first element
        List<SystemUserPracticeModel> results = systemUserWorker.systemUserPractices(0, 1, null, null, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first), results);
        // by first name ASC
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), results);
        // by first name DESC
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_DESC), SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), results);

        // by last name ASC
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(LAST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), results);
        // by last name DESC
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(LAST_NAME_DESC), SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), results);

        transactionHelper.executeInTransaction(() -> {
            PracticeOwner fromDB = practiceOwnerDao.getOne(registered2.getId());
            fromDB.setStatus(INACTIVE);
            practiceOwnerDao.save(fromDB);
        });
        second.setStatus(INACTIVE);
        // by status ASC
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(STATUS_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), results);
        // by status DES
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(STATUS_DESC), SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), results);

        // by office name ASC
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(OFFICE_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), results);
        // by office name DES
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(OFFICE_NAME_DESC), SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), results);

        // by manager name ASC
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(OFFICE_MANAGER_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), results);
        // by manager name DES
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(OFFICE_MANAGER_DESC), SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), results);

        // by phone ASC
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(OFFICE_PHONE_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), results);
        // by phone DES
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(OFFICE_PHONE_DESC), SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), results);

        // by last activity ASC
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(LAST_ACTIVITY_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), results);

        // by last activity DESC
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, null, asList(LAST_ACTIVITY_DESC), SYSTEM_CREDENTIALS);
        compareResult(asList(second, first), results);

        // filter by distance 223 miles
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, 223d, ORIGIN_LAT, ORIGIN_LNG, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        first.setLocations(1l);
        compareResult(asList(first), results);
        first.setLocations(2l); //restore
        // filter by distance 222 miles
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, 222d, ORIGIN_LAT, ORIGIN_LNG, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        assertTrue(CollectionUtils.isEmpty(results));
        // filter by creation time between yesterday and now
        results = systemUserWorker.systemUserPractices(0, 100, null, null, ZonedDateTime.now().minusDays(1), ZonedDateTime.now(), null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), results);
        // filter by creation time between yesterday and before creation of second practice
        results = systemUserWorker.systemUserPractices(0, 100, null, null, ZonedDateTime.now().minusDays(1), creationTime, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first), results);
        // filter by creation time between creation of the first practice and now
        results = systemUserWorker.systemUserPractices(0, 100, null, null, creationTime, ZonedDateTime.now(), null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(second), results);
        // filter specialties CUSTOM and GENERAL
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, asList(GENERAL_SPECIALTY, CUSTOM_SPECIALTY), null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), results);
        // filter specialties CUSTOM
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, asList(CUSTOM_SPECIALTY), null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(second), results);
        // filter specialties GENERAL
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, asList(GENERAL_SPECIALTY), null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first), results);
        // filter by lastActivity time between yesterday and now
        results = systemUserWorker.systemUserPractices(0, 100, ZonedDateTime.now().minusDays(1), ZonedDateTime.now(), null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first, second), results);
        // filter by lastActivity time between yesterday and before creation of second practice
        results = systemUserWorker.systemUserPractices(0, 100, ZonedDateTime.now().minusDays(1), creationTime, null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first), results);
        // filter by lastActivity time between creation of the first practice and now
        results = systemUserWorker.systemUserPractices(0, 100, creationTime, ZonedDateTime.now(), null, null, null, null, null, null, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(second), results);

        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, "managerNameB", asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(second), results);

        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, "managerNameA", asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first), results);

        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, "officeNameB", asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(second), results);

        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, "officeNameA", asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first), results);

        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, "nameB", asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(second), results);

        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, null, "nameA", asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first), results);

        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, "lastNameB", null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(second), results);

        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, null, null, null, null, null, "lastNameA", null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first), results);


        RegisterProfessional proAccount = create(RegisterProfessional.class);
        ProfessionalModel registered = professionalWorker.registerAndActivate(proAccount);
        professionalWorker.blackListPracticeLocation(toHttpBasic(proAccount), parisPracticeLocation.getId());

        // filter by Blacklisted FALSE
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, Boolean.FALSE, null, null, null, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(second), results);
        // filter by Blacklisted TRUE
        results = systemUserWorker.systemUserPractices(0, 100, null, null, null, null, Boolean.TRUE, null, null, null, null, null, null, asList(FIRST_NAME_ASC), SYSTEM_CREDENTIALS);
        compareResult(asList(first), results);

        activateDeactivatePracticeOwner();
        activateDeactivateProfessional();
    }

    protected void compareResult(List<SystemUserPracticeModel> expectedList, List<SystemUserPracticeModel> actualList) {
        assertEquals(expectedList.size(), actualList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            SystemUserPracticeModel expected = expectedList.get(i);
            PracticeOwner expectedDb = practiceOwnerDao.findOne(expected.getId());
            SystemUserPracticeModel actual = actualList.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getFirstName(), actual.getFirstName());
            assertEquals(expected.getLastName(), actual.getLastName());
            assertEquals(expected.getStatus(), actual.getStatus());
            assertEquals(expected.getCountry(), actual.getCountry());
            assertEquals(expected.getState(), actual.getState());
            assertEquals(expected.getCity(), actual.getCity());
            assertEquals(expected.getStreet(), actual.getStreet());
            assertEquals(expected.getZipCode(), actual.getZipCode());
            assertEquals(expected.getLocations(), actual.getLocations());
            assertEquals(expected.getOfficePhone(), actual.getOfficePhone());
            assertEquals(expected.getOfficeManagerName(), actual.getOfficeManagerName());
            assertEquals(expected.getOfficeRating(), actual.getOfficeRating());
            assertEquals(expectedDb.getLastActivity().withZoneSameInstant(ZoneId.of("UTC")), actual.getLastActivity());
        }
    }

    protected SystemUserPracticeModel toModel(RegisterPracticeOwner p, long locations, String id) {
        return new SystemUserPracticeModel(id, p.getContact().getName().getFirst(), p.getContact().getName().getLast(),
                "ACTIVE", p.getRegisterPractice().getName(),
                p.getContact().getAddress().getCountry(), p.getContact().getAddress().getState(), p.getContact().getAddress().getCity(), p.getContact().getAddress().getStreet(), p.getContact().getAddress().getZipCode(),
                locations, p.getRegisterPractice().getPhone(),
                p.getRegisterPractice().getOfficeManagerName(), null, 0.0, 0L);

    }

}

