package se.loftux.modules.smarttenant.overrides;

import org.alfresco.query.CannedQueryParameters;
import org.alfresco.repo.domain.node.NodeDAO;
import org.alfresco.repo.domain.qname.QNameDAO;
import org.alfresco.repo.domain.query.CannedQueryDAO;
import org.alfresco.repo.security.person.GetPeopleCannedQuery;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;

/**
 * Created by bhagyasilva on 27/05/16.
 */
public class SmartTenantGetPeopleCannedQuery extends GetPeopleCannedQuery {

    public SmartTenantGetPeopleCannedQuery(NodeDAO nodeDAO, QNameDAO qnameDAO, CannedQueryDAO cannedQueryDAO, TenantService tenantService, NodeService nodeService, AuthorityService authorityService, CannedQueryParameters params) {
        super(nodeDAO, qnameDAO, cannedQueryDAO, tenantService, nodeService, authorityService, params);
    }
}
