package com.edc.edcportal.core.arkadin.pojo;

import javax.xml.bind.annotation.XmlAttribute;

public class OpCodeResult {

    private String opCode;
    private String status;
    private String message;

    public String getOpCode() {
        return opCode;
    }

    @XmlAttribute(name = "OpCode")
    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public String getStatus() {
        return status;
    }

    @XmlAttribute(name = "Status")
    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    @XmlAttribute(name = "Message")
    public void setMessage(String message) {
        this.message = message;
    }

}
