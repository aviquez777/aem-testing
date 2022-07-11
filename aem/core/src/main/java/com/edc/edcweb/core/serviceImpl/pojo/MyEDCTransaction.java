package com.edc.edcweb.core.serviceImpl.pojo;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class MyEDCTransaction {
    private Map<String, String> properties = new HashMap<>();

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getAttr(String attrName) {
        if (properties.containsKey(attrName)) {
            return properties.get(attrName);
        } else {
            return "";
        }
    }

    public void setAttr(String attrName, String value) {
        if (value != null) {
            properties.put(attrName, value);
        }
    }

    public String getID() {
        return getAttr("ID");
    }

    public void setID(String ID) {
        setAttr("ID", ID);
    }

    public String getUniqueCode() {
        return getAttr("uniqueCode");
    }

    public void setUniqueCode(String uniqueCode) {
        setAttr("uniqueCode", uniqueCode);
    }

    public String getExternalID() {
        return getAttr("externalID");
    }

    public void setExternalID(String externalID) {
        setAttr("externalID", externalID);
    }

    public String getTimeStamp() {
        return getAttr("timeStamp");
    }

    public void setTimeStamp(String timeStamp) {
        setAttr("timeStamp", timeStamp);
    }

    public String getAemPath() {
        return getAttr("aemPath");
    }

    public void setAemPath(String aemPath) {
        setAttr("aemPath", aemPath);
    }

    public String getCounter() {
        return getAttr("counter");
    }

    public void setCounter(String counter) {
        setAttr("counter", counter);
    }

    public String getEmail() {
        return getAttr("email");
    }

    public void setEmail(String email) {
        setAttr("email", email);
    }

    public String getTraffcSrc() {
        return getAttr("trafficSrc");
    }

    public void setTrafficSrc(String trafficSrc) {
        setAttr("trafficSrc", trafficSrc);
    }

    public String getPartnersCASL() {
        return getAttr("partnersCASL");
    }

    public void setPartnersCASL(String partnersCASL) {
        setAttr("partnersCASL", partnersCASL);
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("MyEDC Transaction includes");

        if (StringUtils.isNotEmpty(getID())) {
            stringBuffer.append(" : ID - " + getID());
        }
        if (StringUtils.isNotEmpty(getUniqueCode())) {
            stringBuffer.append(" : uniqueCode - " + getUniqueCode());
        }
        if (StringUtils.isNotEmpty(getExternalID())) {
            stringBuffer.append(" : externalID - " + getExternalID());
        }
        if (StringUtils.isNotEmpty(getTimeStamp())) {
            stringBuffer.append(" : timeStamp - " + getTimeStamp());
        }
        if (StringUtils.isNotEmpty(getAemPath())) {
            stringBuffer.append(" : aemPath - " + getAemPath());
        }
        if (StringUtils.isNotEmpty(getCounter())) {
            stringBuffer.append(" : counter - " + getCounter());
        }
        if (StringUtils.isNotEmpty(getEmail())) {
            stringBuffer.append(" : email - " + getEmail());
        }
        if (StringUtils.isNotEmpty(getTraffcSrc())) {
            stringBuffer.append(" : traffcSrc - " + getTraffcSrc());
        }
        if (StringUtils.isNotEmpty(getPartnersCASL())) {
            stringBuffer.append(" : getParnersCASL - " + getPartnersCASL());
        }
        return stringBuffer.toString();
    }
}
