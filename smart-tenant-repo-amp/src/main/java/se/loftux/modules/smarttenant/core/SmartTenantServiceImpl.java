package se.loftux.modules.smarttenant.core;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.alfresco.model.ContentModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.LimitBy;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.commons.lang3.StringUtils;
import se.loftux.modules.smarttenant.overrides.SmartTenantPersonInfo;
import se.loftux.modules.smarttenant.overrides.exceptions.MultipleUsersWithSameEmailException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by bhagyasilva on 30/05/16.
 */
@Log4j
public class SmartTenantServiceImpl implements SmartTenantService {

    @Setter
    PersonService personService;

    @Setter
    NodeService nodeService;

    @Setter
    SearchService searchService;

    private StoreRef storeRef;

    @Override
    public SmartTenantPersonInfo getPersonByEmailOrNull(String email) throws MultipleUsersWithSameEmailException {
        if(StringUtils.isNotEmpty(email)){
            List<SmartTenantPersonInfo> peopleByEmail = getPeopleByEmailFTS(email);
            if (peopleByEmail.size() > 1) {
                throw new MultipleUsersWithSameEmailException();
            }else{
                return peopleByEmail.get(0);
            }
        }
        return null;
    }

//    /**
//     * {@inheritDoc}
//     */
//    public List<SmartTenantPersonInfo> getPeopleByEmail(String email, boolean ignoreCase)
//    {
//        List<SmartTenantPersonInfo> peopleByEmailList = Collections.<SmartTenantPersonInfo>emptyList();
//        if (StringUtils.isNotBlank(email))
//        {
//            // Filter: By email property
//            List<Pair<QName, String>> filterProps = new ArrayList<Pair<QName, String>>(1);
//            filterProps.add(new Pair<QName, String>(ContentModel.PROP_EMAIL, email));
//            // Sorting: Not needed
//            List<Pair<QName, Boolean>> sortProps = null;
//            // Paging: We are only expecting one single match. If there are more
//            //         matches we have a problem in the system, so 10 will do!
//            PagingRequest paging = new PagingRequest(10, null);
//            // Get a list of persons with matching email address
//            List<PersonService.PersonInfo> personInfoList = personService.getPeople(filterProps, ignoreCase, sortProps, paging).getPage();
//            peopleByEmailList = getPeopleByEmailFTS(personInfoList);
//        }
//        // Return the list of persons with matching email address
//        return peopleByEmailList;
//    }

    public void setStoreUrl(String storeUrl)
    {
        this.storeRef = new StoreRef(storeUrl);
    }

    public List<SmartTenantPersonInfo> getPeopleByEmailFTS(String email){
        String searchQuery = "TYPE:\"cm:person\" AND =@cm:email:\"" + email + "\"";
        SearchParameters params = new SearchParameters();
        params.setQuery(searchQuery);
        params.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
        params.addStore(this.storeRef);
        params.setLimitBy(LimitBy.FINAL_SIZE);

        ResultSet query = searchService.query(params);
        List<NodeRef> people = query.getNodeRefs();
        return getSmartTenantPersonInfoList(people);
    }

    private List<SmartTenantPersonInfo> getSmartTenantPersonInfoList(List<NodeRef> personInfoList) {
        List<SmartTenantPersonInfo> smartTenantPersonInfoList = Collections.<SmartTenantPersonInfo>emptyList();
        if (personInfoList != null && !personInfoList.isEmpty())
        {
            smartTenantPersonInfoList = new ArrayList<SmartTenantPersonInfo>(personInfoList.size());
            for (NodeRef personNodeRef : personInfoList)
            {
                smartTenantPersonInfoList.add(buildSmartTenantPersonInfo(personNodeRef));
            }
        }
        return smartTenantPersonInfoList;
    }


//    private List<SmartTenantPersonInfo> buildCadqPersonInfoList(List<PersonService.PersonInfo> personInfoList)
//    {
//        List<SmartTenantPersonInfo> smartTenantPersonInfoList = Collections.<SmartTenantPersonInfo>emptyList();
//        if (personInfoList != null && !personInfoList.isEmpty())
//        {
//            smartTenantPersonInfoList = new ArrayList<SmartTenantPersonInfo>(personInfoList.size());
//            for (PersonService.PersonInfo personInfo : personInfoList)
//            {
//                smartTenantPersonInfoList.add(buildSmartTenantPersonInfo(personInfo.getNodeRef()));
//            }
//        }
//        return smartTenantPersonInfoList;
//    }

    /**
     * {@inheritDoc}
     */
    public SmartTenantPersonInfo buildSmartTenantPersonInfo(NodeRef personNodeRef) throws InvalidNodeRefException
    {
        SmartTenantPersonInfo smartTenantPersonInfo = null;
        if (personNodeRef != null && nodeService.exists(personNodeRef))
        {
            Map<QName, Serializable> personProps = nodeService.getProperties(personNodeRef);
            smartTenantPersonInfo = new SmartTenantPersonInfo(personNodeRef,
                    (String) personProps.get(ContentModel.PROP_USERNAME),
                    (String) personProps.get(ContentModel.PROP_FIRSTNAME),
                    (String) personProps.get(ContentModel.PROP_LASTNAME),
                    (String) personProps.get(ContentModel.PROP_EMAIL));
        }
        else
        {
            String msg = "The person NodeRef '" + personNodeRef + "' is null or does not exist!";
            log.warn(msg);
            throw new InvalidNodeRefException(msg, personNodeRef);
        }
        return smartTenantPersonInfo;
    }
}
