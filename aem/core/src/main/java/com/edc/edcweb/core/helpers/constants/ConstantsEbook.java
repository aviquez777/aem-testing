package com.edc.edcweb.core.helpers.constants;

import com.edc.edcweb.core.helpers.Constants;

public class ConstantsEbook extends Constants {

    public static final String EBOOK_PERSONA = "persona";
    public static final String EBOOK_CHAPTER = "chapter";

    /**
     * Empty Constructor
     */

    private ConstantsEbook() {
    }


    /**
     * This method returns the Profile labels beng returned from the Components
     * Policy
     *
     * @param none
     * @return String
     *
     */
    public class EbookProperties {
        public static final String PERSONA_CONTENT_TYPE = "edc/components/content/ebook/personabuttons";
        public static final String PROGRESSIVEPROFILING_CONTENT_TYPE = "edc/components/content/premium/progressiveprofiling";
        public static final String CHAPTERNAME = "chapterName";
        public static final String SYNOPSYS = "articlesynopsis";
        public static final String TIMETOREAD = "timeToRead";

        public static final String NEXT_CHAPTER = "nextchapter";
        public static final String CHAPTER = "chapter";
        public static final String OF = "of";
        public static final String MINUTE = "minute";
        public static final String MINUTES = "minutes";
        public static final String READ = "readtime";
    }

    public class SessionAttr {
        public static final String PREVIEW_ONLY_ATTR = "previewonly";
        public static final String PERSONA_ATTR =  EBOOK_PERSONA;
        public static final String DOCID_ATTR = Properties.DOC_ID;
    }

    public class PersonaButtonsProps {
        public static final String BTN = "btn";
        public static final String PERSONA_BTN =  EBOOK_PERSONA + BTN;
        public static final String CHAPTER_BTN =  EBOOK_CHAPTER + BTN;
        public static final String DOCID = "docID";

    }
}
