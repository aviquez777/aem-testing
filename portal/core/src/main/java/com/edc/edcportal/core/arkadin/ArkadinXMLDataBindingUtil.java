package com.edc.edcportal.core.arkadin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.edc.edcportal.core.arkadin.pojo.ArkadinRegistrationDO;
import com.edc.edcportal.core.arkadin.pojo.ArkadinUserActivityPO;

/**
 * Utilities functions to parse JSON and retrieve data
 * 
 *
 */
public class ArkadinXMLDataBindingUtil {

    private ArkadinXMLDataBindingUtil() {
        // sonar lint
    }

    private static String inputStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    public static ArkadinRegistrationDO xmlToArkadinRegistrationDO(InputStream is) throws IOException {
        String inputString = inputStreamToString(is);
        ArkadinRegistrationDO arkadinRegistrationDO = new ArkadinRegistrationDO();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ArkadinRegistrationDO.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            arkadinRegistrationDO = (ArkadinRegistrationDO) jaxbUnmarshaller.unmarshal(new StringReader(inputString));

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return arkadinRegistrationDO;
    }

    public static ArkadinUserActivityPO xmlToArkadinUserActivityPO(InputStream is) throws IOException {
        String inputString = inputStreamToString(is);
        ArkadinUserActivityPO arkadinUserActivityPO = new ArkadinUserActivityPO();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ArkadinUserActivityPO.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            arkadinUserActivityPO = (ArkadinUserActivityPO) jaxbUnmarshaller.unmarshal(new StringReader(inputString));

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return arkadinUserActivityPO;
    }

}
