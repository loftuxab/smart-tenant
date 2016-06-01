package se.loftux.modules.smarttenant.overrides;

import lombok.extern.log4j.Log4j;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.security.person.PersonServiceImpl;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;

import java.util.List;
import java.util.Set;

/**
 * Created by bhagyasilva on 27/05/16.
 */
@Log4j
public class SmartTenantPersonServiceImpl extends PersonServiceImpl implements SmartTenantPersonService {

//    /**
//     * {@inheritDoc}
//     */
//    public NodeRef getPersonOrNull(String userNameOrEmail)
//    {
//        // Get the person with different methods depending on whether we are
//        // supplied a username or an email address. Only a confirmed person or
//        // null will be returned
//        if (userNameOrEmail.contains("@")){
//
//        }else{
//            return super.getPersonOrNull(userNameOrEmail);
//        }
//    }


//    // canned query
//    public List<PersonInfo> getPeopleImplDB(String filter, ScriptPagingDetails pagingRequest, String sortBy, Boolean sortAsc)
//    {
//        List<QName> filterProps = null;
//
//        if ((filter != null) && (filter.length() > 0))
//        {
//            filter = filter.trim();
//            if (! filter.equals("*"))
//            {
//                filter = filter.replace("\\", "").replace("\"", "");
//
//                // simple non-FTS filter: firstname or lastname or username starting with term (ignoring case)
//
//                filterProps = new ArrayList<QName>(3);
//                filterProps.add(ContentModel.PROP_FIRSTNAME);
//                filterProps.add(ContentModel.PROP_LASTNAME);
//                filterProps.add(ContentModel.PROP_USERNAME);
//            }
//        }
//
//        // Build the sorting. The user controls the primary sort, we supply
//        // additional ones automatically
//        List<Pair<QName,Boolean>> sort = new ArrayList<Pair<QName,Boolean>>();
//        if ("lastName".equals(sortBy))
//        {
//            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_LASTNAME, sortAsc));
//            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_FIRSTNAME, sortAsc));
//            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_USERNAME, sortAsc));
//        }
//        else if ("firstName".equals(sortBy))
//        {
//            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_FIRSTNAME, sortAsc));
//            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_LASTNAME, sortAsc));
//            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_USERNAME, sortAsc));
//        }
//        else
//        {
//            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_USERNAME, sortAsc));
//            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_FIRSTNAME, sortAsc));
//            sort.add(new Pair<QName, Boolean>(ContentModel.PROP_LASTNAME, sortAsc));
//        }
//
//        return getPeople(filter, filterProps, sort, pagingRequest).getPage();
//    }

    /**
     * {@inheritDoc}
     */
    public PagingResults<PersonInfo> getPeople(String pattern, List<QName> filterStringProps, Set<QName> inclusiveAspects, Set<QName> exclusiveAspects, boolean includeAdministraotrs, List<Pair<QName, Boolean>> sortProps, PagingRequest pagingRequest)
    {
        //get current users memberships

        //evaluate each users memberships against current user membership sites

        //check for the current user and see if a member of the admin.

        return super.getPeople(pattern, filterStringProps, inclusiveAspects, exclusiveAspects, includeAdministraotrs, sortProps,pagingRequest);
    }

}
