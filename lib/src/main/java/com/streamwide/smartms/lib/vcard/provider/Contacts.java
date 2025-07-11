/*
 *
 * 	StreamWIDE (Team on The Run)
 *
 * @createdBy  AndroidTeam on Thu, 16 May 2024 09:41:20 +0100
 * @copyright  Copyright (c) 2024 StreamWIDE UK Ltd (Team on the Run)
 * @email      support@teamontherun.com
 *
 * 	© Copyright 2024 StreamWIDE UK Ltd (Team on the Run). StreamWIDE is the copyright holder
 * 	of all code contained in this file. Do not redistribute or
 *  	re-use without permission.
 *
 * @lastModifiedOn Thu, 2 May 2024 20:52:37 +0100
 */

package com.streamwide.smartms.lib.vcard.provider;

/**
 * The Contacts provider stores all information about contacts.
 */
public class Contacts {

    public static final String AUTHORITY = "contacts";


    /** Signifies an email address row that is stored in the ContactMethods table */
    public static final int KIND_EMAIL = 1;
    /** Signifies a postal address row that is stored in the ContactMethods table */
    public static final int KIND_POSTAL = 2;
    /** Signifies an IM address row that is stored in the ContactMethods table */
    public static final int KIND_IM = 3;
    /** Signifies an Organization row that is stored in the Organizations table */
    public static final int KIND_ORGANIZATION = 4;
    /** Signifies an Phone row that is stored in the Phones table */
    public static final int KIND_PHONE = 5;

    /**
     * no public constructor since this is a utility class
     */
    private Contacts() {}

    /**
     * A sub directory of a single person that contains all of their Phones.
     */
    public static final class Phones implements BaseColumns, PhonesColumns {
        /**
         * no public constructor since this is a utility class
         */
        private Phones() {}

        /**
         * The directory twig for this sub-table
         */
        public static final String CONTENT_DIRECTORY = "phones";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "number ASC";
    }

    /**
     * A subdirectory of a single person that contains all of their
     * ContactMethods.
     */
    public static final class ContactMethods
            implements BaseColumns, ContactMethodsColumns {
        /**
         * no public constructor since this is a utility class
         */
        private ContactMethods() {}

        /**
         * The directory twig for this sub-table
         */
        public static final String CONTENT_DIRECTORY = "contact_methods";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "data ASC";
    }

    public interface PhonesColumns {
        /**
         * The type of the the phone number.
         * <P>Type: INTEGER (one of the constants below)</P>
         */
        public static final String TYPE = "type";

        public static final int TYPE_CUSTOM = 0;
        public static final int TYPE_HOME = 1;
        public static final int TYPE_MOBILE = 2;
        public static final int TYPE_WORK = 3;
        public static final int TYPE_FAX_WORK = 4;
        public static final int TYPE_FAX_HOME = 5;
        public static final int TYPE_PAGER = 6;
        public static final int TYPE_OTHER = 7;

        /**
         * The user provided label for the phone number, only used if TYPE is TYPE_CUSTOM.
         * <P>Type: TEXT</P>
         */
        public static final String LABEL = "label";

        /**
         * The phone number as the user entered it.
         * <P>Type: TEXT</P>
         */
        public static final String NUMBER = "number";

        /**
         * The normalized phone number
         * <P>Type: TEXT</P>
         */
        public static final String NORMALIZED_NUMBER = "number_key";

        /**
         * Whether this is the primary phone number
         * <P>Type: INTEGER (if set, non-0 means true)</P>
         */
        public static final String ISPRIMARY = "isprimary";
    }
    /**
     * Columns from the ContactMethods table that other tables join into
     * themseleves.
     */
    public interface ContactMethodsColumns {
        /**
         * The kind of the the contact method. For example, email address,
         * postal address, etc.
         * <P>Type: INTEGER (one of the values below)</P>
         */
        public static final String KIND = "kind";

        /**
         * The type of the contact method, must be one of the types below.
         * <P>Type: INTEGER (one of the values below)</P>
         */
        public static final String TYPE = "type";
        public static final int TYPE_CUSTOM = 0;
        public static final int TYPE_HOME = 1;
        public static final int TYPE_WORK = 2;
        public static final int TYPE_OTHER = 3;

        /**
         * @hide This is temporal. TYPE_MOBILE should be added to TYPE in the future.
         */
        public static final int MOBILE_EMAIL_TYPE_INDEX = 2;

        /**
         * @hide This is temporal. TYPE_MOBILE should be added to TYPE in the future.
         * This is not "mobile" but "CELL" since vCard uses it for identifying mobile phone.
         */
        public static final String MOBILE_EMAIL_TYPE_NAME = "_AUTO_CELL";

        /**
         * The user defined label for the the contact method.
         * <P>Type: TEXT</P>
         */
        public static final String LABEL = "label";

        /**
         * The data for the contact method.
         * <P>Type: TEXT</P>
         */
        public static final String DATA = "data";

        /**
         * Auxiliary data for the contact method.
         * <P>Type: TEXT</P>
         */
        public static final String AUX_DATA = "aux_data";

        /**
         * Whether this is the primary organization
         * <P>Type: INTEGER (if set, non-0 means true)</P>
         */
        public static final String ISPRIMARY = "isprimary";
    }
    /**
     * Columns from the Organizations table that other columns join into themselves.
     */
    public interface OrganizationColumns {
        /**
         * The type of the organizations.
         * <P>Type: INTEGER (one of the constants below)</P>
         */
        public static final String TYPE = "type";

        public static final int TYPE_CUSTOM = 0;
        public static final int TYPE_WORK = 1;
        public static final int TYPE_OTHER = 2;

        /**
         * The user provided label, only used if TYPE is TYPE_CUSTOM.
         * <P>Type: TEXT</P>
         */
        public static final String LABEL = "label";

        /**
         * The name of the company for this organization.
         * <P>Type: TEXT</P>
         */
        public static final String COMPANY = "company";

        /**
         * The title within this organization.
         * <P>Type: TEXT</P>
         */
        public static final String TITLE = "title";

        /**
         * The person this organization is tied to.
         * <P>Type: TEXT</P>
         */
        public static final String PERSON_ID = "person";

        /**
         * Whether this is the primary organization
         * <P>Type: INTEGER (if set, non-0 means true)</P>
         */
        public static final String ISPRIMARY = "isprimary";
    }

}
