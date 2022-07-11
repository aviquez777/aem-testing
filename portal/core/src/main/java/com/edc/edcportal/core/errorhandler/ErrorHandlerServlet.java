package com.edc.edcportal.core.errorhandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.errorhandler.pojo.EDCErrorCode;
import com.edc.edcportal.core.errorhandler.services.ErrorHandlerConfigService;
import com.edc.edcportal.core.helpers.Constants;

@SuppressWarnings("CQRules:CQBP-75")

@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + Constants.Paths.ERROR_HANDLER_SERVLET_URL, })

public class ErrorHandlerServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -5539295786275883093L;
    private static final Logger log = LoggerFactory.getLogger(ErrorHandlerServlet.class);

    // Get the Configuration from service
    @Reference
    @Inject
    private ErrorHandlerConfigService configuration;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        log.debug("ErrorHandler Servlet doGet***********************************************");

        String entityID = request.getParameter("entityID");
        String statusCode = request.getParameter("statusCode");
        String statusMessage = reEncodeChars(request.getParameter("statusMessage"));
        String relayState = request.getParameter("RelayState");
        log.debug("ErrorHandler Servlet EntityID: {}", entityID);
        log.debug("ErrorHandler Servlet Status Code: {}", statusCode);
        log.debug("ErrorHandler Servlet Status Message Original: {}", request.getParameter("statusMessage"));
        log.debug("ErrorHandler Servlet Status Message: {}", statusMessage);

        // Get the error code from the code list using the values available
        EDCErrorCode edcErrorCode = getEDCErrorCode(entityID, statusCode, statusMessage);
        /// If matching code is available, then uset
        if (edcErrorCode != null) {
            doRedirect(edcErrorCode, response, relayState);
        } else {
            // No code, redirect to the default url
            log.error("Unhandled Authentication Error, request URL: {}", request.getRequestURI());
            response.sendRedirect(configuration.getB2CSignUpRedirectURL());
        }

    }

    /**
     * getEDCErrorCode loop over the codes and get the one matching
     * 
     * @param entityID
     * @param statusCode
     * @param statusMessage
     * @return EDCErrorCode if found, null otherwise
     */
    private EDCErrorCode getEDCErrorCode(String entityID, String statusCode, String statusMessage) {
        Set<EDCErrorCode> errorCodes = initializeErrorsConstant(configuration);
        EDCErrorCode error = null;
        for (EDCErrorCode thisErrorCode : errorCodes) {
            if (thisErrorCode.getEntityID().equals(entityID) && thisErrorCode.getStatusCode().equals(statusCode)
                    && thisErrorCode.getStatusMessage().equals(statusMessage)) {
                error = thisErrorCode;
                break;
            }
        }
        return error;
    }

    /**
     * Redirect based on the EDCErrorCode found. Handle special case for password
     * change. (en & fr uses english message)
     * 
     * @param error
     * @param response
     * @param relayState
     */
    private void doRedirect(EDCErrorCode error, SlingHttpServletResponse response, String relayState) {
        // Task 221435 squid:S3655
        String redirectURL = error.getRedirectURL();
        if (error.getStatusMessage().equals("The user has forgotten their password.")) {
            //relayState has referral URL after password change
            redirectURL = redirectURL + "?target=" + relayState;
        }
        try {
            response.sendRedirect(redirectURL);
        } catch (IOException e) {
            log.error("ErrorHandler Serlvet doRedirect:", e);
        }
    }

    /**
     * reEncodeChars from ISO_8859_1 to UTF_8
     * 
     * @param value
     * @return re-encoded string, original value otherwise. Could not find a
     *         specific encoding exception to replace generic one as requested on
     *         Task 221435 squid:S2221
     */
    @SuppressWarnings("squid:S2221")
    private String reEncodeChars(String value) {
        try {
            value = new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.info("ErrorHandlerServlet error re-encoding string {} {}", value, e);
        }
        return value;
    }

    /**
     * initializeErrorsConstant read the errors on the configuration files and make
     * it available for processing
     * 
     * @param configuration
     * @return Set<EDCErrorCode> with errors, empty Set if config if not found,
     *         error if config is null
     */
    private Set<EDCErrorCode> initializeErrorsConstant(ErrorHandlerConfigService configuration) {
        Set<EDCErrorCode> errorCodes = new HashSet<>();
        // Change Password
        errorCodes.add(
                new EDCErrorCode(configuration.getB2CChangePassEntityId(), configuration.getB2CChangePassStatusCode(),
                        configuration.getB2CChangePassStatusMessage(), configuration.getB2CChangePassRedirectURL()));
        // Edit Profile
        errorCodes.add(
                new EDCErrorCode(configuration.getB2CEditProfileEntityId(), configuration.getB2CEditProfileStatusCode(),
                        configuration.getB2CEditProfileStatusMessage(), configuration.getB2CEditProfileRedirectURL()));

        // Forget Password
        errorCodes.add(
                new EDCErrorCode(configuration.getB2CForgetPassEntityId(), configuration.getB2CForgetPassStatusCode(),
                        configuration.getB2CForgetPassStatusMessage(), configuration.getB2CForgetPassRedirectURL()));
        // Signup Cancel
        errorCodes.add(new EDCErrorCode(configuration.getB2CSignUpEntityId(), configuration.getB2CSignUpStatusCode(),
                configuration.getB2CSignUpStatusMessage(), configuration.getB2CSignUpRedirectURL()));

        // Reset Password Cancel
        errorCodes.add(
                new EDCErrorCode(configuration.getB2CResetPassEntityId(), configuration.getB2CResetPassStatusCode(),
                        configuration.getB2CResetPassStatusMessage(), configuration.getB2CResetPassRedirectURL()));

        // SignUp Direct Cancel
        errorCodes.add(new EDCErrorCode(configuration.getB2CSignUpDirectEntityId(),
                configuration.getB2CSignUpDirectStatusCode(), configuration.getB2CSignUpDirectStatusMessage(),
                configuration.getB2CSignUpDirectRedirectURL()));

        // French
        errorCodes.add(new EDCErrorCode(configuration.getB2CFRChangePassEntityId(),
                configuration.getB2CFRChangePassStatusCode(), configuration.getB2CFRChangePassStatusMessage(),
                configuration.getB2CFRChangePassRedirectURL()));
        // Edit Profile
        errorCodes.add(new EDCErrorCode(configuration.getB2CFREditProfileEntityId(),
                configuration.getB2CFREditProfileStatusCode(), configuration.getB2CFREditProfileStatusMessage(),
                configuration.getB2CFREditProfileRedirectURL()));

        // Forget Password
        errorCodes.add(new EDCErrorCode(configuration.getB2CFRForgetPassEntityId(),
                configuration.getB2CFRForgetPassStatusCode(), configuration.getB2CFRForgetPassStatusMessage(),
                configuration.getB2CFRForgetPassRedirectURL()));
        // Signup Cancel
        errorCodes
                .add(new EDCErrorCode(configuration.getB2CFRSignUpEntityId(), configuration.getB2CFRSignUpStatusCode(),
                        configuration.getB2CFRSignUpStatusMessage(), configuration.getB2CFRSignUpRedirectURL()));

        // Reset Password Cancel
        errorCodes.add(
                new EDCErrorCode(configuration.getB2CFRResetPassEntityId(), configuration.getB2CFRResetPassStatusCode(),
                        configuration.getB2CFRResetPassStatusMessage(), configuration.getB2CFRResetPassRedirectURL()));

        // SignUp Direct Cancel
        errorCodes.add(new EDCErrorCode(configuration.getB2CFRSignUpDirectEntityId(),
                configuration.getB2CFRSignUpDirectStatusCode(), configuration.getB2CFRSignUpDirectStatusMessage(),
                configuration.getB2CFRSignUpDirectRedirectURL()));

        return errorCodes;
    }

}