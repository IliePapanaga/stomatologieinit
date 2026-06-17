package com.cl.mdd.server.mvc.security.impersonation;

import com.cl.mdd.server.core.data.security.UserPrincipal;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.authentication.DisabledException;

import java.util.Collections;

import static com.cl.mdd.server.core.data.persistent.model.user.User.ROLE_PRACTICE_OWNER;
import static com.cl.mdd.server.core.data.persistent.model.user.User.ROLE_PROFESSIONAL;
import static com.cl.mdd.server.core.data.persistent.model.user.User.ROLE_SYSTEM_USER;

public class ImpersonateUserDetailsCheckerTest {

    private ImpersonateUserDetailsChecker checker = new ImpersonateUserDetailsChecker();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void check_whenUserAccountIsDisabled_throwException() {
        UserPrincipal principal = new UserPrincipal("ID", "NAME", "PASS", false, Collections.singleton(ROLE_PRACTICE_OWNER));

        expectedException.expect(DisabledException.class);

        checker.check(principal);
    }

    @Test
    public void check_whenUserHasSystemUserRole_throwException() {
        UserPrincipal principal = new UserPrincipal("ID", "NAME", "PASS", true, Collections.singleton(ROLE_SYSTEM_USER));

        expectedException.expect(InvalidImpersonatedUserTypeException.class);

        checker.check(principal);
    }

    @Test
    public void check_whenUserHasPracticeOwnerRole_passSuccessfully() {
        UserPrincipal principal = new UserPrincipal("ID", "NAME", "PASS", true, Collections.singleton(ROLE_PRACTICE_OWNER));

        checker.check(principal);
    }

    @Test
    public void check_whenUserHasProfessionalRole_passSuccessfully() {
        UserPrincipal principal = new UserPrincipal("ID", "NAME", "PASS", true, Collections.singleton(ROLE_PROFESSIONAL));

        checker.check(principal);
    }
}