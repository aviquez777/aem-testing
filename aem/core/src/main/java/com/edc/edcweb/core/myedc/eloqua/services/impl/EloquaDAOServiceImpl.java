package com.edc.edcweb.core.myedc.eloqua.services.impl;

import java.io.IOException;

import com.edc.edcweb.core.service.EloquaService;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.edc.edcweb.core.exception.EDCEloquaSystemException;
import com.edc.edcweb.core.myedc.eloqua.EloquaConnectionManager;
import com.edc.edcweb.core.myedc.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcweb.core.myedc.eloqua.services.EloquaDAOService;


@Component(immediate = true, service = EloquaDAOService.class)
public class EloquaDAOServiceImpl implements EloquaDAOService {

    @Reference
    private EloquaService eloquaService;

    @Override
    public EloquaUserProfileDO getUserProfileByExternalId(String externalId) throws EDCEloquaSystemException {
        // Create an empty object
        EloquaUserProfileDO eloquaUserProfileDO = new EloquaUserProfileDO();
        try {
            eloquaUserProfileDO = EloquaConnectionManager.getRecordByExternalId(externalId, eloquaService);
        } catch (JSONException | IOException e) {
            throw new EDCEloquaSystemException("externalId: " + externalId, this.getClass().getName(), "getUserProfileByExternalId", e.toString());
        }
        return eloquaUserProfileDO;
    }

    @Override
    public EloquaService getEloquaService() {
        return eloquaService;
    }

}
