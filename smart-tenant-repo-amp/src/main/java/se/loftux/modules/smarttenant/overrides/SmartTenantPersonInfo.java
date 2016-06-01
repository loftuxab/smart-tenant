package se.loftux.modules.smarttenant.overrides;

import lombok.Getter;
import lombok.Setter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;

/**
 * Created by bhagyasilva on 30/05/16.
 */
public class SmartTenantPersonInfo extends PersonService.PersonInfo {

    @Setter
    @Getter
    private String emailAddress;

    public SmartTenantPersonInfo(NodeRef nodeRef, String userName, String firstName, String lastName) {
        super(nodeRef, userName, firstName, lastName);
    }

    public SmartTenantPersonInfo(NodeRef personNodeRef, String userName, String firstName, String lastName, String emailAddress) {
        this(personNodeRef, userName, firstName, lastName);
        this.emailAddress = emailAddress;
    }
}
