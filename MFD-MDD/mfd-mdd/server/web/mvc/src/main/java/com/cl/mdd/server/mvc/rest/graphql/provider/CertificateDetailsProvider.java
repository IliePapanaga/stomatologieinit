package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.RejectCertificateDetailsModel;
import com.cl.mdd.server.core.data.model.certificates.CertificateDetailsModel;
import com.cl.mdd.server.core.data.model.query.FindProfessionalRequiredCertificates;
import com.cl.mdd.server.core.data.model.query.RequiredCertificate;
import com.cl.mdd.server.core.service.user.CertificateDetailsService;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * Certificate data provider.
 * <p/>
 * Exposes Certificates operations to graph ql.
 */
@Component
@Validated
public class CertificateDetailsProvider implements GraphQLProvider {

    @Autowired
    private WebSecurityAccess webSecurityAccess;

    @Autowired
    private CertificateDetailsService certificateDetailsService;

    @GraphQLQuery(name = "professionalRequiredCertificates")
    public Connection<RequiredCertificate> professionalRequiredCertificates(@GraphQLArgument(name = "page") Integer page,
                                                                            @GraphQLArgument(name = "perPage") Integer perPage,
                                                                            @GraphQLArgument(name = "orders") List<FindProfessionalRequiredCertificates.FindProfessionalRequiredCertificatesOrders> orders) {
        return professionalRequiredCertificatesByProfessionalId(webSecurityAccess.currentUserId(),
                page,
                perPage,
                orders);
    }

    @GraphQLQuery(name = "professionalRequiredCertificatesByProfessionalId")
    public Connection<RequiredCertificate> professionalRequiredCertificatesByProfessionalId(@GraphQLNonNull @GraphQLArgument(name = "professionalId") String professionalId,
                                                                                            @GraphQLArgument(name = "page") Integer page,
                                                                                            @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                            @GraphQLArgument(name = "orders") List<FindProfessionalRequiredCertificates.FindProfessionalRequiredCertificatesOrders> orders) {
        FindProfessionalRequiredCertificates queryInfo = new FindProfessionalRequiredCertificates();
        queryInfo.getFilters().setProfessionalId(professionalId);
        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(certificateDetailsService.fetch(queryInfo));
    }

    @GraphQLQuery(name = "certificateDetails")
    public CertificateDetailsModel certificateDetailsById(@GraphQLNonNull @GraphQLArgument(name = "id") String id) {
        return certificateDetailsService.get(id);
    }

    @GraphQLMutation(name = "approveCertificateDetails")
    public void approveCertificateDetails(@GraphQLNonNull @GraphQLArgument(name = "id", description = "certificate id") String id) {
        certificateDetailsService.approve(id);
    }

    @GraphQLMutation(name = "rejectCertificateDetails")
    public void rejectCertificateDetails(@GraphQLArgument(name = "reject") @Valid RejectCertificateDetailsModel model) {
        certificateDetailsService.reject(model);
    }

}
