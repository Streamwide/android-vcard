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

package com.streamwide.smartms.lib.vcard.syncml.pim.vcard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.streamwide.smartms.lib.vcard.com.android.internal.util.CollectionUtils;
import com.streamwide.smartms.lib.vcard.logger.Logger;
import com.streamwide.smartms.lib.vcard.provider.Contacts;
import com.streamwide.smartms.lib.vcard.syncml.pim.PropertyNode;
import com.streamwide.smartms.lib.vcard.syncml.pim.VNode;
import com.streamwide.smartms.lib.vcard.telephony.PhoneNumberUtils;
import com.streamwide.smartms.lib.vcard.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * The parameter class of VCardComposer.
 * This class standy by the person-contact in
 * Android system, we must use this class instance as parameter to transmit to
 * VCardComposer so that create vCard string.
 */
public class ContactStruct {
    private static final String LOG_TAG = "ContactStruct";

    // Note: phonetic name probably should be "LAST FIRST MIDDLE" for European languages, and
    //       space should be added between each element while it should not be in Japanese.
    //       But unfortunately, we currently do not have the data and are not sure whether we should
    //       support European version of name ordering.
    //
    //        phonetic name handling. Also, adding the appropriate test case of vCard would be
    //        highly appreciated.
    public static final int NAME_ORDER_TYPE_ENGLISH = 0;
    public static final int NAME_ORDER_TYPE_JAPANESE = 1;

    /** MUST exist */
    private String mName;
    private String mPhoneticName;
    /** maybe folding */
    private List<String> mNotes = new ArrayList<>();
    /** maybe folding */
    private String mTitle;
    /** binary bytes of pic. */
    private byte[] mPhotoBytes;
    /** The type of Photo (e.g. JPEG, BMP, etc.) */
    private String mPhotoType;
    /** Only for GET. Use addPhoneList() to PUT. */
    private List<PhoneData> mPhoneList;
    /** Only for GET. Use addContactmethodList() to PUT. */
    private List<ContactMethod> mContactmethodList;
    /** Only for GET. Use addOrgList() to PUT. */
    private List<OrganizationData> mOrganizationList;
    /** Only for GET. Use addExtension() to PUT */
    private Map<String, List<String>> mExtensionMap;
    /** The type of Photo (e.g. JPEG, BMP, etc.) */
    private String mWebsite;

    // Use organizationList instead when handling ORG.
    /**      * @deprecated (when, why, refactoring advice...)      */
    @Deprecated
    public @Nullable String company;

    public static class PhoneData {
        protected int type;
        /** maybe folding */
        protected @Nullable String data;
        protected @Nullable String label;
        protected boolean isPrimary;

        public @Nullable String getData() {
            return data;
        }

        public void setData(@NonNull String data) {
            this.data = data;
        }

        public @Nullable String getLabel() {
            return label;
        }

        public void setLabel(@NonNull String label) {
            this.label = label;
        }

        public boolean isPrimary() {
            return isPrimary;
        }

        public void setPrimary(boolean primary) {
            isPrimary = primary;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public @Nullable String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        this.mName = name;
    }

    public @Nullable String getPhoneticName() {
        return mPhoneticName;
    }

    public void setPhoneticName(@NonNull String phoneticName) {
        this.mPhoneticName = phoneticName;
    }

    public @Nullable List<String> getNotes() {
        return CollectionUtils.copyList(mNotes);
    }

    public void setNotes(@NonNull List<String> notes) {
        this.mNotes = CollectionUtils.copyList(notes);
    }

    public @Nullable String getTitle() {
        return mTitle;
    }

    public void setTitle(@NonNull String title) {
        this.mTitle = title;
    }

    public @Nullable byte[] getPhotoBytes() {
        return mPhotoBytes != null ? mPhotoBytes.clone() : null;
    }

    public void setPhotoBytes(@NonNull byte[] photoBytes) {
        this.mPhotoBytes = photoBytes != null ? photoBytes.clone() : null;
    }

    public @Nullable String getPhotoType() {
        return mPhotoType;
    }

    public void setPhotoType(@NonNull String photoType) {
        this.mPhotoType = photoType;
    }

    public @Nullable List<PhoneData> getPhoneList() {
        return CollectionUtils.copyList(mPhoneList);
    }

    public void setPhoneList(@NonNull List<PhoneData> phoneList) {
        this.mPhoneList = CollectionUtils.copyList(phoneList);
    }

    public @Nullable List<ContactMethod> getContactMethodList() {
        return CollectionUtils.copyList(mContactmethodList);
    }

    public void setContactMethodList(@NonNull List<ContactMethod> contactMethodList) {
        this.mContactmethodList = CollectionUtils.copyList(contactMethodList);
    }

    public@Nullable  List<OrganizationData> getOrganizationList() {
        return CollectionUtils.copyList(mOrganizationList);
    }

    public void setOrganizationList(@NonNull List<OrganizationData> organizationList) {
        this.mOrganizationList = CollectionUtils.copyList(organizationList);
    }

    public @Nullable Map<String, List<String>> getExtensionMap() {
        return mExtensionMap;
    }

    public void setExtensionMap(@NonNull Map<String, List<String>> extensionMap) {
        this.mExtensionMap = extensionMap;
    }

    public @Nullable String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(@NonNull String website) {
        this.mWebsite = website;
    }

    public static class ContactMethod {
        // Contacts.KIND_EMAIL, Contacts.KIND_POSTAL
        protected int kind;
        // e.g. Contacts.ContactMethods.TYPE_HOME, Contacts.PhoneColumns.TYPE_HOME
        // If type == Contacts.PhoneColumns.TYPE_CUSTOM, label is used.
        protected int type;
        protected @Nullable String data;
        // Used only when TYPE is TYPE_CUSTOM.
        protected @Nullable String label;
        protected boolean isPrimary;

        public int getKind() {
            return kind;
        }

        public void setKind(int kind) {
            this.kind = kind;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public @Nullable String getData() {
            return data;
        }

        public void setData(@NonNull String data) {
            this.data = data;
        }

        public @Nullable String getLabel() {
            return label;
        }

        public void setLabel(@NonNull String label) {
            this.label = label;
        }

        public boolean isPrimary() {
            return isPrimary;
        }

        public void setPrimary(boolean primary) {
            isPrimary = primary;
        }
    }

    public static class OrganizationData {
        protected int type;
        protected @Nullable String companyName;
        protected @Nullable String positionName;
        protected boolean isPrimary;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public @Nullable String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(@NonNull String companyName) {
            this.companyName = companyName;
        }

        public @Nullable String getPositionName() {
            return positionName;
        }

        public void setPositionName(@NonNull String positionName) {
            this.positionName = positionName;
        }

        public boolean isPrimary() {
            return isPrimary;
        }

        public void setPrimary(boolean primary) {
            isPrimary = primary;
        }
    }

    /**
     * Add a phone info to phoneList.
     * @param data phone number
     * @param type type col of content://contacts/phones
     * @param label lable col of content://contacts/phones
     */
    public void addPhone(int type, @NonNull String data, @Nullable String label, boolean isPrimary){
        if (mPhoneList == null) {
            mPhoneList = new ArrayList<>();
        }
        PhoneData phoneData = new PhoneData();
        phoneData.type = type;

        StringBuilder builder = new StringBuilder();
        String trimed = data.trim();
        int length = trimed.length();
        for (int i = 0; i < length; i++) {
            char ch = trimed.charAt(i);
            if (('0' <= ch && ch <= '9') || (i == 0 && ch == '+')) {
                builder.append(ch);
            }
        }
        phoneData.data = PhoneNumberUtils.formatNumber(builder.toString());
        phoneData.label = label;
        phoneData.isPrimary = isPrimary;
        mPhoneList.add(phoneData);
    }

    /**
     * Add a contactmethod info to contactmethodList.
     * @param kind integer value defined in Contacts.java
     * (e.g. Contacts.KIND_EMAIL)
     * @param type type col of content://contacts/contact_methods
     * @param data contact data
     * @param label extra string used only when kind is Contacts.KIND_CUSTOM.
     */
    public void addContactmethod(int kind, int type, @NonNull String data,
                                 @Nullable String label, boolean isPrimary){
        if (mContactmethodList == null) {
            mContactmethodList = new ArrayList<>();
        }
        ContactMethod contactMethod = new ContactMethod();
        contactMethod.kind = kind;
        contactMethod.type = type;
        contactMethod.data = data;
        contactMethod.label = label;
        contactMethod.isPrimary = isPrimary;
        mContactmethodList.add(contactMethod);
    }

    /**
     * Add a Organization info to organizationList.
     */
    public void addOrganization(int type, @NonNull String companyName, @Nullable String positionName,
                                boolean isPrimary) {
        if (mOrganizationList == null) {
            mOrganizationList = new ArrayList<>();
        }
        OrganizationData organizationData = new OrganizationData();
        organizationData.type = type;
        organizationData.companyName = companyName;
        organizationData.positionName = positionName;
        organizationData.isPrimary = isPrimary;
        mOrganizationList.add(organizationData);
    }

    /**
     * Set "position" value to the appropriate data. If there's more than one
     * OrganizationData objects, the value is set to the last one. If there's no
     * OrganizationData object, a new OrganizationData is created, whose company name is
     * empty.
     *
     *
     * e.g. This assumes ORG comes earlier, but TITLE may come earlier like this, though we do not
     * know how to handle it in general cases...
     * ----
     * TITLE:Software Engineer
     * ORG:Google
     * ----
     */
    public void setPosition(@NonNull String positionValue) {
        if (mOrganizationList == null) {
            mOrganizationList = new ArrayList<>();
        }
        int size = mOrganizationList.size();
        if (size == 0) {
            addOrganization(Contacts.OrganizationColumns.TYPE_OTHER, "", null, false);
            size = 1;
        }
        OrganizationData lastData = mOrganizationList.get(size - 1);
        lastData.positionName = positionValue;
    }

    public void addExtension(@NonNull PropertyNode propertyNode) {
        if (propertyNode.getPropValue().length() == 0) {
            return;
        }
        // Now store the string into extensionMap.
        List<String> list;
        String pname = propertyNode.getPropName();
        if (mExtensionMap == null) {
            mExtensionMap = new HashMap<>();
        }
        if (!mExtensionMap.containsKey(pname)){
            list = new ArrayList<>();
            mExtensionMap.put(pname, list);
        } else {
            list = mExtensionMap.get(pname);
        }

        list.add(propertyNode.encode());
    }

    private static String getNameFromNProperty(List<String> elems, int nameOrderType) {
        // Family, Given, Middle, Prefix, Suffix. (1 - 5)
        int size = elems.size();
        if (size > 1) {
            StringBuilder builder = new StringBuilder();
            boolean builderIsEmpty = true;
            // Prefix
            if (size > 3 && elems.get(3).length() > 0) {
                builder.append(elems.get(3));
                builderIsEmpty = false;
            }
            String first;
            String second;
            if (nameOrderType == NAME_ORDER_TYPE_JAPANESE) {
                first = elems.get(0);
                second = elems.get(1);
            } else {
                first = elems.get(1);
                second = elems.get(0);
            }
            if (first.length() > 0) {
                if (!builderIsEmpty) {
                    builder.append(' ');
                }
                builder.append(first);
                builderIsEmpty = false;
            }
            // Middle name
            if (size > 2 && elems.get(2).length() > 0) {
                if (!builderIsEmpty) {
                    builder.append(' ');
                }
                builder.append(elems.get(2));
                builderIsEmpty = false;
            }
            if (second.length() > 0) {
                if (!builderIsEmpty) {
                    builder.append(' ');
                }
                builder.append(second);
                builderIsEmpty = false;
            }
            // Suffix
            if (size > 4 && elems.get(4).length() > 0) {
                if (!builderIsEmpty) {
                    builder.append(' ');
                }
                builder.append(elems.get(4));
            }
            return builder.toString();
        } else if (size == 1) {
            return elems.get(0);
        } else {
            return "";
        }
    }

    public static @Nullable ContactStruct constructContactFromVNode(@NonNull VNode node,
                                                          int nameOrderType) {
        if (!node.getVName().equals("VCARD")) {
            // Impossible in current implementation. Just for safety.
            Logger.error(LOG_TAG, "Non VCARD data is inserted.");
            return null;
        }

        // For name, there are three fields in vCard: FN, N, NAME.
        // We prefer FN, which is a required field in vCard 3.0 , but not in vCard 2.1.
        // Next, we prefer NAME, which is defined only in vCard 3.0.
        // Finally, we use N, which is a little difficult to parse.
        String fullName = null;
        String nameFromNProperty = null;

        // Some vCard has "X-PHONETIC-FIRST-NAME", "X-PHONETIC-MIDDLE-NAME", and
        // "X-PHONETIC-LAST-NAME"
        String xPhoneticFirstName = null;
        String xPhoneticMiddleName = null;
        String xPhoneticLastName = null;

        ContactStruct contact = new ContactStruct();

        // Each Column of four properties has ISPRIMARY field
        // (See android.provider.Contacts)
        // If false even after the following loop, we choose the first
        // entry as a "primary" entry.
        boolean prefIsSetAddress = false;
        boolean prefIsSetPhone = false;
        boolean prefIsSetEmail = false;
        boolean prefIsSetOrganization = false;

        for (PropertyNode propertyNode: node.getPropList()) {
            String name = propertyNode.getPropName();

            if (TextUtils.isEmpty(propertyNode.getPropValue())) {
                continue;
            }

            if (name.equals("VERSION")) {
                // vCard version. Ignore this.
            } else if (name.equals("FN")) {
                fullName = propertyNode.getPropValue();
            } else if (name.equals("NAME") && fullName == null) {
                // Only in vCard 3.0. Use this if FN does not exist.
                // Though, note that vCard 3.0 requires FN.
                fullName = propertyNode.getPropValue();
            } else if (name.equals("N")) {
                nameFromNProperty = getNameFromNProperty(propertyNode.getPropValueVector(),
                        nameOrderType);
            } else if (name.equals("SORT-STRING")) {
                contact.mPhoneticName = propertyNode.getPropValue();
            } else if (name.equals("SOUND")) {
                if (propertyNode.getParamMapType().contains("X-IRMC-N") &&
                        contact.mPhoneticName == null) {
                    // Some Japanese mobile phones use this field for phonetic name,
                    // since vCard 2.1 does not have "SORT-STRING" type.
                    // Also, in some cases, the field has some ';' in it.
                    // We remove them.
                    StringBuilder builder = new StringBuilder();
                    String value = propertyNode.getPropValue();
                    int length = value.length();
                    for (int i = 0; i < length; i++) {
                        char ch = value.charAt(i);
                        if (ch != ';') {
                            builder.append(ch);
                        }
                    }
                    contact.mPhoneticName = builder.toString();
                } else {
                    contact.addExtension(propertyNode);
                }
            } else if (name.equals("ADR")) {
                List<String> values = propertyNode.getPropValueVector();
                boolean valuesAreAllEmpty = true;
                for (String value : values) {
                    if (value.length() > 0) {
                        valuesAreAllEmpty = false;
                        break;
                    }
                }
                if (valuesAreAllEmpty) {
                    continue;
                }

                int kind = Contacts.KIND_POSTAL;
                int type = -1;
                String label = "";
                boolean isPrimary = false;
                for (String typeString : propertyNode.getParamMapType()) {
                    if (typeString.equals("PREF") && !prefIsSetAddress) {
                        // Only first "PREF" is considered.
                        prefIsSetAddress = true;
                        isPrimary = true;
                    } else if (typeString.equalsIgnoreCase("HOME")) {
                        type = Contacts.ContactMethodsColumns.TYPE_HOME;
                        label = "";
                    } else if (typeString.equalsIgnoreCase("WORK") ||
                            typeString.equalsIgnoreCase("COMPANY")) {
                        // "COMPANY" seems emitted by Windows Mobile, which is not
                        // specifically supported by vCard 2.1. We assume this is same
                        // as "WORK".
                        type = Contacts.ContactMethodsColumns.TYPE_WORK;
                        label = "";
                    } else if (typeString.equalsIgnoreCase("POSTAL")) {
                        kind = Contacts.KIND_POSTAL;
                    } else if (typeString.equalsIgnoreCase("PARCEL") ||
                            typeString.equalsIgnoreCase("DOM") ||
                            typeString.equalsIgnoreCase("INTL")) {
                        // We do not have a kind or type matching these.
                        // (e.g. entries for KIND_POSTAL and KIND_PERCEL)
                    } else if (typeString.toUpperCase(Locale.ENGLISH).startsWith("X-") &&
                            type < 0) {
                        type = Contacts.ContactMethodsColumns.TYPE_CUSTOM;
                        label = typeString.substring(2);
                    } else if (type < 0) {
                        // vCard 3.0 allows iana-token. Also some vCard 2.1 exporters
                        // emit non-standard types. We do not handle their values now.
                        type = Contacts.ContactMethodsColumns.TYPE_CUSTOM;
                        label = typeString;
                    }
                }
                // We use "HOME" as default
                if (type < 0) {
                    type = Contacts.ContactMethodsColumns.TYPE_HOME;
                }

                // adr-value    = 0*6(text-value ";") text-value
                //              ; PO Box, Extended Address, Street, Locality, Region, Postal
                //              ; Code, Country Name
                String address;
                List<String> list = propertyNode.getPropValueVector();
                int size = list.size();
                if (size > 1) {
                    StringBuilder builder = new StringBuilder();
                    boolean builderIsEmpty = true;
                    if (Locale.getDefault().getCountry().equals(Locale.JAPAN.getCountry())) {
                        // In Japan, the order is reversed.
                        for (int i = size - 1; i >= 0; i--) {
                            String addressPart = list.get(i);
                            if (addressPart.length() > 0) {
                                if (!builderIsEmpty) {
                                    builder.append(' ');
                                }
                                builder.append(addressPart);
                                builderIsEmpty = false;
                            }
                        }
                    } else {
                        for (int i = 0; i < size; i++) {
                            String addressPart = list.get(i);
                            if (addressPart.length() > 0) {
                                if (!builderIsEmpty) {
                                    builder.append(' ');
                                }
                                builder.append(addressPart);
                                builderIsEmpty = false;
                            }
                        }
                    }
                    address = builder.toString().trim();
                } else {
                    address = propertyNode.getPropValue();
                }
                contact.addContactmethod(kind, type, address, label, isPrimary);
            } else if (name.equals("ORG")) {
                // vCard specification does not specify other types.
                int type = Contacts.OrganizationColumns.TYPE_WORK;
                boolean isPrimary = false;

                for (String typeString : propertyNode.getParamMapType()) {
                    if (typeString.equals("PREF") && !prefIsSetOrganization) {
                        // vCard specification officially does not have PREF in ORG.
                        // This is just for safety.
                        prefIsSetOrganization = true;
                        isPrimary = true;
                    }
                    // XXX: Should we cope with X- words?
                }

                List<String> list = propertyNode.getPropValueVector();
                StringBuilder builder = new StringBuilder();
                for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
                    builder.append(iter.next());
                    if (iter.hasNext()) {
                        builder.append(' ');
                    }
                }

                contact.addOrganization(type, builder.toString(), "", isPrimary);
            } else if (name.equals("TITLE")) {
                contact.setPosition(propertyNode.getPropValue());
            } else if (name.equals("ROLE")) {
                contact.setPosition(propertyNode.getPropValue());
            } else if (name.equals("PHOTO")) {
                // We prefer PHOTO to LOGO.

                    // Assume PHOTO is stored in BASE64. In that case,
                    // data is already stored in propValue_bytes in binary form.
                    // It should be automatically done by VBuilder (VDataBuilder/VCardDatabuilder)
                    contact.mPhotoBytes = propertyNode.getPropValueBytes();
                    String type = propertyNode.getParamMap().getAsString("TYPE");
                    if (type != null) {
                        contact.mPhotoType = type;
                    }

            } else if (name.equals("LOGO")) {
                // When PHOTO is not available this is not URL,
                // we use this instead of PHOTO.
                 if (contact.mPhotoBytes == null) {
                    contact.mPhotoBytes = propertyNode.getPropValueBytes();
                    String type = propertyNode.getParamMap().getAsString("TYPE");
                    if (type != null) {
                        contact.mPhotoType = type;
                    }
                }
            } else if (name.equals("EMAIL")) {
                int type = -1;
                String label = null;
                boolean isPrimary = false;
                for (String typeString : propertyNode.getParamMapType()) {
                    if (typeString.equals("PREF") && !prefIsSetEmail) {
                        // Only first "PREF" is considered.
                        prefIsSetEmail = true;
                        isPrimary = true;
                    } else if (typeString.equalsIgnoreCase("HOME")) {
                        type = Contacts.ContactMethodsColumns.TYPE_HOME;
                    } else if (typeString.equalsIgnoreCase("WORK")) {
                        type = Contacts.ContactMethodsColumns.TYPE_WORK;
                    } else if (typeString.equalsIgnoreCase("CELL")) {
                        // We do not have Contacts.ContactMethodsColumns.TYPE_MOBILE yet.
                        type = Contacts.ContactMethodsColumns.TYPE_CUSTOM;
                        label = Contacts.ContactMethodsColumns.MOBILE_EMAIL_TYPE_NAME;
                    } else if (typeString.toUpperCase(Locale.ENGLISH).startsWith("X-") &&
                            type < 0) {
                        type = Contacts.ContactMethodsColumns.TYPE_CUSTOM;
                        label = typeString.substring(2);
                    } else if (type < 0) {
                        // vCard 3.0 allows iana-token.
                        // We may have INTERNET (specified in vCard spec),
                        // SCHOOL, etc.
                        type = Contacts.ContactMethodsColumns.TYPE_CUSTOM;
                        label = typeString;
                    }
                }
                // We use "OTHER" as default.
                if (type < 0) {
                    type = Contacts.ContactMethodsColumns.TYPE_OTHER;
                }
                contact.addContactmethod(Contacts.KIND_EMAIL,
                        type, propertyNode.getPropValue(),label, isPrimary);
            } else if (name.equals("TEL")) {
                int type = -1;
                String label = null;
                boolean isPrimary = false;
                boolean isFax = false;
                for (String typeString : propertyNode.getParamMapType()) {
                    if (typeString.equals("PREF") && !prefIsSetPhone) {
                        // Only first "PREF" is considered.
                        prefIsSetPhone = true;
                        isPrimary = true;
                    } else if (typeString.equalsIgnoreCase("HOME")) {
                        type = Contacts.PhonesColumns.TYPE_HOME;
                    } else if (typeString.equalsIgnoreCase("WORK")) {
                        type = Contacts.PhonesColumns.TYPE_WORK;
                    } else if (typeString.equalsIgnoreCase("CELL")) {
                        type = Contacts.PhonesColumns.TYPE_MOBILE;
                    } else if (typeString.equalsIgnoreCase("PAGER")) {
                        type = Contacts.PhonesColumns.TYPE_PAGER;
                    } else if (typeString.equalsIgnoreCase("FAX")) {
                        isFax = true;
                    } else if (typeString.equalsIgnoreCase("VOICE") ||
                            typeString.equalsIgnoreCase("MSG")) {
                        // Defined in vCard 3.0. Ignore these because they
                        // conflict with "HOME", "WORK", etc.
                        // XXX: do something?
                    } else if (typeString.toUpperCase(Locale.ENGLISH).startsWith("X-") &&
                            type < 0) {
                        type = Contacts.PhonesColumns.TYPE_CUSTOM;
                        label = typeString.substring(2);
                    } else if (type < 0){
                        // We may have MODEM, CAR, ISDN, etc...
                        type = Contacts.PhonesColumns.TYPE_CUSTOM;
                        label = typeString;
                    }
                }
                // We use "HOME" as default
                if (type < 0) {
                    type = Contacts.PhonesColumns.TYPE_HOME;
                }
                if (isFax) {
                    if (type == Contacts.PhonesColumns.TYPE_HOME) {
                        type = Contacts.PhonesColumns.TYPE_FAX_HOME;
                    } else if (type == Contacts.PhonesColumns.TYPE_WORK) {
                        type = Contacts.PhonesColumns.TYPE_FAX_WORK;
                    }
                }

                contact.addPhone(type, propertyNode.getPropValue(), label, isPrimary);
            } else if (name.equals("NOTE")) {
                contact.mNotes.add(propertyNode.getPropValue());
            } else if (name.equals("BDAY")) {
                contact.addExtension(propertyNode);
            } else if (name.equals("URL")) {
                contact.setWebsite(propertyNode.getPropValue());
            } else if (name.equals("REV")) {
                // Revision of this VCard entry. I think we can ignore this.
                contact.addExtension(propertyNode);
            } else if (name.equals("UID")) {
                contact.addExtension(propertyNode);
            } else if (name.equals("KEY")) {
                // Type is X509 or PGP? I don't know how to handle this...
                contact.addExtension(propertyNode);
            } else if (name.equals("MAILER")) {
                contact.addExtension(propertyNode);
            } else if (name.equals("TZ")) {
                contact.addExtension(propertyNode);
            } else if (name.equals("GEO")) {
                contact.addExtension(propertyNode);
            } else if (name.equals("NICKNAME")) {
                // vCard 3.0 only.
                contact.addExtension(propertyNode);
            } else if (name.equals("CLASS")) {
                // vCard 3.0 only.
                // e.g. CLASS:CONFIDENTIAL
                contact.addExtension(propertyNode);
            } else if (name.equals("PROFILE")) {
                // VCard 3.0 only. Must be "VCARD". I think we can ignore this.
                contact.addExtension(propertyNode);
            } else if (name.equals("CATEGORIES")) {
                // VCard 3.0 only.
                // e.g. CATEGORIES:INTERNET,IETF,INDUSTRY,INFORMATION TECHNOLOGY
                contact.addExtension(propertyNode);
            } else if (name.equals("SOURCE")) {
                // VCard 3.0 only.
                contact.addExtension(propertyNode);
            } else if (name.equals("PRODID")) {
                // VCard 3.0 only.
                // To specify the identifier for the product that created
                // the vCard object.
                contact.addExtension(propertyNode);
            } else if (name.equals("X-PHONETIC-FIRST-NAME")) {
                xPhoneticFirstName = propertyNode.getPropValue();
            } else if (name.equals("X-PHONETIC-MIDDLE-NAME")) {
                xPhoneticMiddleName = propertyNode.getPropValue();
            } else if (name.equals("X-PHONETIC-LAST-NAME")) {
                xPhoneticLastName = propertyNode.getPropValue();
            } else {
                // Unknown X- words and IANA token.
                contact.addExtension(propertyNode);
            }
        }

        if (fullName != null) {
            contact.mName = fullName;
        } else if(nameFromNProperty != null) {
            contact.mName = nameFromNProperty;
        } else {
            contact.mName = "";
        }

        if (contact.mPhoneticName == null &&
                (xPhoneticFirstName != null || xPhoneticMiddleName != null ||
                        xPhoneticLastName != null)) {
            // Note: In Europe, this order should be "LAST FIRST MIDDLE". See the comment around
            //       NAME_ORDER_TYPE_* for more detail.
            String first;
            String second;
            if (nameOrderType == NAME_ORDER_TYPE_JAPANESE) {
                first = xPhoneticLastName;
                second = xPhoneticFirstName;
            } else {
                first = xPhoneticFirstName;
                second = xPhoneticLastName;
            }
            StringBuilder builder = new StringBuilder();
            if (first != null) {
                builder.append(first);
            }
            if (xPhoneticMiddleName != null) {
                builder.append(xPhoneticMiddleName);
            }
            if (second != null) {
                builder.append(second);
            }
            contact.mPhoneticName = builder.toString();
        }

        // Remove unnecessary white spaces.
        // It is found that some mobile phone emits  phonetic name with just one white space
        // when a user does not specify one.
        // This logic is effective toward such kind of weird data.
        if (contact.mPhoneticName != null) {
            contact.mPhoneticName = contact.mPhoneticName.trim();
        }

        // If there is no "PREF", we choose the first entries as primary.
        if (!prefIsSetPhone &&
                contact.mPhoneList != null &&
                ! contact.mPhoneList.isEmpty()) {
            contact.mPhoneList.get(0).isPrimary = true;
        }

        if (!prefIsSetAddress && contact.mContactmethodList != null) {
            for (ContactMethod contactMethod : contact.mContactmethodList) {
                if (contactMethod.kind == Contacts.KIND_POSTAL) {
                    contactMethod.isPrimary = true;
                    break;
                }
            }
        }
        if (!prefIsSetEmail && contact.mContactmethodList != null) {
            for (ContactMethod contactMethod : contact.mContactmethodList) {
                if (contactMethod.kind == Contacts.KIND_EMAIL) {
                    contactMethod.isPrimary = true;
                    break;
                }
            }
        }
        if (!prefIsSetOrganization &&
                contact.mOrganizationList != null &&
                ! contact.mOrganizationList.isEmpty()) {
            contact.mOrganizationList.get(0).isPrimary = true;
        }

        return contact;
    }

    public @NonNull String displayString() {
        if (mName.length() > 0) {
            return mName;
        }
        if (mContactmethodList != null && !mContactmethodList.isEmpty()) {
            for (ContactMethod contactMethod : mContactmethodList) {
                if (contactMethod.kind == Contacts.KIND_EMAIL && contactMethod.isPrimary) {
                    return contactMethod.data;
                }
            }
        }
        if (mPhoneList != null && !mPhoneList.isEmpty()) {
            for (PhoneData phoneData : mPhoneList) {
                if (phoneData.isPrimary) {
                    return phoneData.data;
                }
            }
        }
        return "";
    }


    public boolean isIgnorable() {
        return TextUtils.isEmpty(mName) &&
                TextUtils.isEmpty(mPhoneticName) &&
                (mPhoneList == null || mPhoneList.isEmpty()) &&
                (mContactmethodList == null || mContactmethodList.isEmpty());
    }
}
