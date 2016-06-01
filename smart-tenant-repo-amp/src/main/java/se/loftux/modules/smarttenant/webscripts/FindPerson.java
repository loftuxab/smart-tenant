package se.loftux.modules.smarttenant.webscripts;

import java.util.HashMap;
import java.util.Map;

import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

import se.loftux.modules.smarttenant.core.SmartTenantService;
import se.loftux.modules.smarttenant.overrides.SmartTenantPersonInfo;
import se.loftux.modules.smarttenant.overrides.exceptions.MultipleUsersWithSameEmailException;


/**
 * Find a person with matching email address
 * 
 * @author tomrun (Tommy Runesson <tommy.runesson@cad-q.se>)
 */
public class FindPerson extends DeclarativeWebScript
{
    // Logger
    private static final Log logger = LogFactory.getLog(FindPerson.class);

    // Required services and resources (provided by Spring Framework)

    @Setter
    private SmartTenantService smartTenantService;


    /* (non-Javadoc)
     * @see DeclarativeWebScript#executeImpl(WebScriptRequest, Status, Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Request URL: " + req.getURL());
        }

        // Extract email address from the URL
        String email = req.getParameter("email");
        if (email == null || email.length() == 0)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Email address not specified");
        }

        // Do some logging
        if (logger.isDebugEnabled())
        {
            logger.debug("Searching for person with matching email address '" + email + "' ...");
        }

        // Construct model for username
        Map<String, Object> model = new HashMap<String, Object>(1, 1.0f);
        String username = null;

        // Get the person and the username
        try
        {
            SmartTenantPersonInfo personInfo = smartTenantService.getPersonByEmailOrNull(email);
            if (personInfo == null || personInfo.getUserName() == null || personInfo.getUserName().length() == 0)
            {
                // Do some logging
                if (logger.isDebugEnabled())
                {
                    logger.debug("No user with matching email address '" + email + "' could be found");
                }
               
                status.setRedirect(true);
                status.setCode(Status.STATUS_NOT_FOUND);
                status.setMessage("No user with matching email address could be found");
            }
            else
            {
                username = personInfo.getUserName();
            }
        }
        catch (MultipleUsersWithSameEmailException e)
        {
            // Do some logging
            if (logger.isWarnEnabled())
            {
                logger.warn("Multiple users with matching email address were found. Message:\n" + e.getMessage(), e);
            }

            status.setRedirect(true);
            status.setCode(Status.STATUS_CONFLICT);
            status.setMessage("Multiple users with matching email address were found");
        }

        // Do some logging
        if (logger.isDebugEnabled() && username != null)
        {
            logger.debug("Found user '" + username + "' with matching email address '" + email + "'");
        }

        model.put("username", username);

        return model;
    }
}
