package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.time.Instant;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.edc.edcweb.core.helpers.ServletResponseHelper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.formvalidation.FormCleaner;
import com.edc.edcweb.core.service.EloquaService;
import com.edc.edcweb.core.serviceImpl.pojo.MyEDCTransaction;

@Component(
        immediate = true,
        service = Servlet.class,
        property = {"sling.servlet.extensions=html",
                "sling.servlet.methods=post",
                "sling.servlet.paths=" + Constants.Paths.PREMIUMCONTENTTRANSACTIONSERVLET
        },
        configurationPid = "com.edc.edcweb.core.servlets.PremiumContentTransactionServlet")

public class PremiumContentTransactionServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = -3347505221867777197L;
    private static final Logger log = LoggerFactory.getLogger(PremiumContentTransactionServlet.class);

    @Reference
    @Inject
    EloquaService eloquaService;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String email;
        String externalID;
        String path;
        String reponseCode;
        Cookie trafficsrc;

        email = request.getParameter("Email") == null  ? "":request.getParameter("Email");
        externalID  = request.getParameter("ExternalID") == null  ? "":request.getParameter("ExternalID");
        path        = request.getParameter("Path") == null  ? "":request.getParameter("Path");
        path        = URLDecoder.decode(path, "UTF-8");
        trafficsrc = request.getCookie(Constants.Properties.TRAFFIC_SOURCE_COOKIE_NAME);

        // Task 215385 Veracode error report
        externalID = FormCleaner.cleanAll(externalID);
        path = FormCleaner.cleanAll(path);

        if (externalID.isEmpty() || path.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        String uniqueCode = externalID+"|"+path;

        try {

            MyEDCTransaction transaction = this.eloquaService.getMyEDCTransaction(uniqueCode);

            if (transaction.getUniqueCode().isEmpty()) {

                Instant instant = Instant.now();
                long timeStamp = instant.getEpochSecond();
                MyEDCTransaction myEDCTransaction = new MyEDCTransaction();

                myEDCTransaction.setCounter(Integer.toString(1));
                myEDCTransaction.setTimeStamp(Long.toString(timeStamp));
                myEDCTransaction.setExternalID(externalID);
                myEDCTransaction.setAemPath(path);
                myEDCTransaction.setUniqueCode(uniqueCode);
                myEDCTransaction.setEmail(email);
                if (trafficsrc != null) {
                    myEDCTransaction.setTrafficSrc(FormCleaner.cleanAll(trafficsrc.getValue()));
                }
                reponseCode = this.eloquaService.updateMyEDCTransaction(myEDCTransaction, false);
            } else {
                //Update the record
                int counter = Integer.parseInt(transaction.getCounter()) + 1;
                Instant instant = Instant.now();
                long timeStamp = instant.getEpochSecond();
                transaction.setCounter(Integer.toString(counter));
                transaction.setTimeStamp(Long.toString(timeStamp));
                // As per Veronica's instructions 
                // Please implement option #2 - Always update the email on the record
                transaction.setEmail(email);
                if (trafficsrc != null) {
                    transaction.setTrafficSrc(FormCleaner.cleanAll(trafficsrc.getValue()));
                }
                reponseCode = this.eloquaService.updateMyEDCTransaction(transaction, true);
            }

            ServletResponseHelper.writeResponse(response, reponseCode);

            response.setStatus(200);
        } catch (Exception ex) {
            log.error("PremiumContentTransactionServlet POST erorr {}",ex);
            response.setStatus(500);
        }

    }
}