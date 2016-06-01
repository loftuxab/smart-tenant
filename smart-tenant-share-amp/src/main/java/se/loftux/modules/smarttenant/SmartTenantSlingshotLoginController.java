package se.loftux.modules.smarttenant;

import org.alfresco.web.site.servlet.SlingshotLoginController;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.mvc.AbstractLoginController;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by bhagyasilva on 30/05/16.
 */
public class SmartTenantSlingshotLoginController extends SlingshotLoginController {

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userNameOrEmail = (String) request.getParameter(PARAM_USERNAME);

        if(userNameOrEmail.contains("@")){
            //get the user name from the webscript, set the username to the request and call the super method.
            // Get a connector to call server side webscripts
            String alfrescoEndpointID = AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID;
            Connector connector = FrameworkUtil.getConnector(alfrescoEndpointID);

            if (connector != null)
            {
                String url = "/smarttenant/person/find-person?email=" + URLEncoder.encode(userNameOrEmail);
                Response resp = connector.call(url, new ConnectorContext(HttpMethod.GET));
                if (resp.getStatus().getCode() == Status.STATUS_OK)
                {
                    JSONParser jp = new JSONParser();
                    JSONObject userData = (JSONObject) jp.parse(resp.getResponse().toString());

                    String userName = (String) userData.get("username");
//                    request.getParameterMap().put(PARAM_USERNAME, userName);
                    request.setAttribute(PARAM_USERNAME, userName);


                    String userEmail = userNameOrEmail;
                }
                else if (resp.getStatus().getCode() == Status.STATUS_NOT_FOUND)
                {
                    boolean noUserFound = true;
                }
                else if (resp.getStatus().getCode() == Status.STATUS_CONFLICT)
                {
                    boolean multipleUsersFound = true;
                }
                else
                {
                    if (logger.isErrorEnabled())
                    {
                        logger.error("The 'find-person' webscript erroneously responded with " +
                                "status code '" + resp.getStatus().getCode() +
                                "' and status message: " + resp.getStatus().getMessage());
                    }
                }
            }

        }

        return handleRequestInternalForAttribute(request, response);
    }


    public ModelAndView handleRequestInternalForAttribute(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        request.setCharacterEncoding("UTF-8");

        String username = (String) request.getParameter(AbstractLoginController.PARAM_USERNAME);
        if(username.contains("@")){
            username = ((String) request.getAttribute(PARAM_USERNAME));
        }

        String password = (String) request.getParameter(AbstractLoginController.PARAM_PASSWORD);

        boolean success = false;
        try
        {
            // check whether there is already a user logged in
            HttpSession session = request.getSession(false);
            // handle SSO which doesn't set a user until later
            if (session != null && request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID) != null)
            {
                // destroy old session and log out the current user
                AuthenticationUtil.logout(request, response);
            }

            // see if we can authenticate the user
            boolean authenticated = this.userFactory.authenticate(request, username, password);
            if (authenticated)
            {
                AuthenticationUtil.login(request, response, username, false, webFrameworkConfiguration.isLoginCookiesEnabled());

                // mark the fact that we succeeded
                success = true;
            }
        }
        catch (Throwable err)
        {
            throw new ServletException(err);
        }

        // If they succeeded in logging in, redirect to the success page
        // Otherwise, redirect to the failure page
        if (success)
        {
            onSuccess(request, response);
        }
        else
        {
            onFailure(request, response);
        }

        return null;
    }

    private WebFrameworkConfigElement webFrameworkConfiguration;

    private UserFactory userFactory;

    @Override
    public void setWebFrameworkConfiguration(WebFrameworkConfigElement webFrameworkConfiguration) {
        this.webFrameworkConfiguration = webFrameworkConfiguration;
    }

    @Override
    public void setUserFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }
}
