package com.edc.edcweb.core.helpers.formvalidation;

import org.apache.commons.io.FilenameUtils;

import com.edc.edcweb.core.gps.helpers.SrfConstants;
import com.edc.edcweb.core.lovapi.helpers.LovHelper;
import com.edc.edcweb.core.lovapi.pojo.SingleLovDO;
import com.edc.edcweb.core.lovapi.service.LovApiDAOService;

public class FileValidationHelper {

    private FileValidationHelper() {
        // SonarQube
    }

    public static boolean checkFileSizeExceeds(long fileSize, long maxFileSize) {
        return (fileSize > maxFileSize);
    }

    public static boolean checkSRFValidFileExtension(String fileName, LovApiDAOService lovApiDAOService) {
        String ext = FilenameUtils.getExtension(fileName);
        boolean validfile = false;
        SingleLovDO singleLovDO = LovHelper.getSingleLovDO(SrfConstants.SRF_FILE_TYPE_API_QUERY, lovApiDAOService);
        String[] allowedFileTypes = singleLovDO.getResult().getEnName().split("\\|");
        for (String validExt : allowedFileTypes) {
            validfile = validExt.equalsIgnoreCase(ext);
            if (validfile) {
                break;
            }
        }
        return validfile;
    }
}
