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

package main.java.com.streamwide.smartms.lib.vcard.syncml.pim.vcard;


import androidx.annotation.NonNull;

import com.streamwide.smartms.lib.vcard.customcommons.Base64;
import com.streamwide.smartms.lib.vcard.provider.Contacts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Compose VCard string
 */
public class VCardComposer {
    public static final  int VERSION_VCARD21_INT = 1;

    public static final  int VERSION_VCARD30_INT = 2;

    /**
     * A new line
     */
    private String mNewline;

    /**
     * The composed string
     */
    private StringBuilder mResult;

    /**
     * The email's type
     */
    private static final  HashSet<String> emailTypes = new HashSet<>(
            Arrays.asList("CELL", "AOL", "APPLELINK", "ATTMAIL", "CIS",
                    "EWORLD", "INTERNET", "IBMMAIL", "MCIMAIL", "POWERSHARE",
                    "PRODIGY", "TLX", "X400"));

    private static final  HashSet<String> phoneTypes = new HashSet<>(
            Arrays.asList("PREF", "WORK", "HOME", "VOICE", "FAX", "MSG",
                    "CELL", "PAGER", "BBS", "MODEM", "CAR", "ISDN", "VIDEO"));


    public VCardComposer() {
        // Default Constructor
    }

    private static final HashMap<Integer, String> phoneTypeMap = new HashMap<>();

    private static final HashMap<Integer, String> emailTypeMap = new HashMap<>();

    static {
        phoneTypeMap.put(Contacts.Phones.TYPE_HOME, "HOME");
        phoneTypeMap.put(Contacts.Phones.TYPE_MOBILE, "CELL");
        phoneTypeMap.put(Contacts.Phones.TYPE_WORK, "WORK");
        // FAX_WORK not exist in vcard spec. The approximate is the combine of
        // WORK and FAX, here only map to FAX
        phoneTypeMap.put(Contacts.Phones.TYPE_FAX_WORK, "WORK;FAX");
        phoneTypeMap.put(Contacts.Phones.TYPE_FAX_HOME, "HOME;FAX");
        phoneTypeMap.put(Contacts.Phones.TYPE_PAGER, "PAGER");
        phoneTypeMap.put(Contacts.Phones.TYPE_OTHER, "X-OTHER");
        emailTypeMap.put(Contacts.ContactMethods.TYPE_HOME, "HOME");
        emailTypeMap.put(Contacts.ContactMethods.TYPE_WORK, "WORK");
    }

    /**
     * Create a vCard String.
     *
     * @param struct
     *            see more from ContactStruct class
     * @param vcardversion
     *            MUST be VERSION_VCARD21 /VERSION_VCARD30
     * @return vCard string
     * @throws VCardException
     *             struct.name is null /vcardversion not match
     */
    public @NonNull String createVCard(@NonNull ContactStruct struct, int vcardversion)
            throws VCardException {

        mResult = new StringBuilder();
        // check exception:
        if (struct.getName() == null || struct.getName().trim().equals("")) {
            throw new VCardException(" struct.name MUST have value.");
        }
        if (vcardversion == VERSION_VCARD21_INT) {
            mNewline = "\r\n";
        } else if (vcardversion == VERSION_VCARD30_INT) {
            mNewline = "\n";
        } else {
            throw new VCardException(
                    " version not match VERSION_VCARD21 or VERSION_VCARD30.");
        }
        // build vcard:
        mResult.append("BEGIN:VCARD").append(mNewline);

        if (vcardversion == VERSION_VCARD21_INT) {
            mResult.append("VERSION:2.1").append(mNewline);
        } else {
            mResult.append("VERSION:3.0").append(mNewline);
        }

        if (!isNull(struct.getName())) {
            appendNameStr(struct.getName());
        }

        if (!isNull(struct.company)) {
            mResult.append("ORG:").append(struct.company).append(mNewline);
        }

        if (!struct.getNotes().isEmpty() && !isNull(struct.getNotes().get(0))) {
            mResult.append("NOTE:").append(
                    foldingString(struct.getNotes().get(0), vcardversion)).append(mNewline);
        }

        if (!isNull(struct.getTitle())) {
            mResult.append("TITLE:").append(
                    foldingString(struct.getTitle(), vcardversion)).append(mNewline);
        }

        if (struct.getPhotoBytes() != null) {
            appendPhotoStr(struct.getPhotoBytes(), struct.getPhotoType(), vcardversion);
        }

        if (struct.getPhoneList() != null) {
            appendPhoneStr(struct.getPhoneList(), vcardversion);
        }

        if (struct.getContactMethodList() != null) {
            appendContactMethodStr(struct.getContactMethodList(), vcardversion);
        }

        if (!isNull(struct.getWebsite())) {
            mResult.append("URL:").append(
                    foldingString(struct.getWebsite(), vcardversion)).append(mNewline);
        }

        mResult.append("END:VCARD").append(mNewline);
        return mResult.toString();
    }

    /**
     * Alter str to folding supported format.
     *
     * @param str
     *            the string to be folded
     * @param version
     *            the vcard version
     * @return the folded string
     */
    private String foldingString(String str, int version) {
        if (str.endsWith("\r\n")) {
            str = str.substring(0, str.length() - 2);
        } else if (str.endsWith("\n")) {
            str = str.substring(0, str.length() - 1);
        } else {
            return null;
        }

        str = str.replaceAll("\r\n", "\n");
        if (version == VERSION_VCARD21_INT) {
            return str.replaceAll("\n", "\r\n ");
        } else if (version == VERSION_VCARD30_INT) {
            return str.replaceAll("\n", "\n ");
        } else {
            return null;
        }
    }

    /**
     * Build LOGO property. format LOGO's param and encode value as base64.
     *
     * @param bytes
     *            the binary string to be converted
     * @param type
     *            the type of the content
     * @param version
     *            the version of vcard
     */
    private void appendPhotoStr(byte[] bytes, String type, int version)
            throws VCardException {
        String value;
        String encodingStr;
        try {
            value = foldingString(new String(Base64.encodeBase64(bytes, true)),
                    version);
        } catch (Exception e) {
            throw new VCardException(e.getMessage());
        }

        if (isNull(type) || type.toUpperCase(Locale.ENGLISH).indexOf("JPEG") >= 0) {
            type = "JPEG";
        } else if (type.toUpperCase(Locale.ENGLISH).indexOf("GIF") >= 0) {
            type = "GIF";
        } else if (type.toUpperCase(Locale.ENGLISH).indexOf("BMP") >= 0) {
            type = "BMP";
        } else {
            // Handle the string like "image/tiff".
            int indexOfSlash = type.indexOf('/');
            if (indexOfSlash >= 0) {
                type = type.substring(indexOfSlash + 1).toUpperCase(Locale.ENGLISH);
            } else {
                type = type.toUpperCase(Locale.ENGLISH);
            }
        }

        mResult.append("LOGO;TYPE=").append(type);
        if (version == VERSION_VCARD21_INT) {
            encodingStr = ";ENCODING=BASE64:";
            value = value + mNewline;
        } else if (version == VERSION_VCARD30_INT) {
            encodingStr = ";ENCODING=b:";
        } else {
            return;
        }
        mResult.append(encodingStr).append(value).append(mNewline);
    }

    private boolean isNull(String str) {
        if (str == null || str.trim().equals("")) {
            return true;
        }
            return false;
    }

    /**
     * Build FN and N property. format N's value.
     *
     * @param name
     *            the name of the contact
     */
    private void appendNameStr(String name) {
        mResult.append("FN:").append(name).append(mNewline);
        mResult.append("N:").append(name).append(mNewline);

    }

    /** Loop append TEL property. */
    private void appendPhoneStr(List<ContactStruct.PhoneData> phoneList,
                                int version) {
        HashMap<String, String> numMap = new HashMap<>();
        String joinMark = version == VERSION_VCARD21_INT ? ";" : ",";

        for (ContactStruct.PhoneData phone : phoneList) {
            String type;
            if (!isNull(phone.getData())) {
                type = getPhoneTypeStr(phone);
                if (version == VERSION_VCARD30_INT && type.indexOf(';') != -1) {
                    type = type.replace(";", ",");
                }
                if (numMap.containsKey(phone.getData())) {
                    type = numMap.get(phone.getData()) + joinMark + type;
                }
                numMap.put(phone.getData(), type);
            }
        }

        for (Map.Entry<String, String> num : numMap.entrySet()) {
            if (version == VERSION_VCARD21_INT) {
                mResult.append("TEL;");
            } else { // vcard3.0
                mResult.append("TEL;TYPE=");
            }
            mResult.append(num.getValue()).append(":").append(num.getKey())
                    .append(mNewline);
        }
    }

    private String getPhoneTypeStr(ContactStruct.PhoneData phone) {

        int phoneType = phone.getType();
        String typeStr, label;

        if (phoneTypeMap.containsKey(phoneType)) {
            typeStr = phoneTypeMap.get(phoneType);
        } else if (phoneType == Contacts.Phones.TYPE_CUSTOM) {
            label = phone.getLabel().toUpperCase(Locale.ENGLISH);
            if (phoneTypes.contains(label) || label.startsWith("X-")) {
                typeStr = label;
            } else {
                typeStr = "X-CUSTOM-" + label;
            }
        } else {
            typeStr = "VOICE"; // the default type is VOICE in spec.
        }
        return typeStr;
    }

    /** Loop append ADR / EMAIL property. */
    private void appendContactMethodStr(
            List<ContactStruct.ContactMethod> contactMList, int version) {

        HashMap<String, String> emailMap = new HashMap<>();
        String joinMark = version == VERSION_VCARD21_INT ? ";" : ",";
        for (ContactStruct.ContactMethod contactMethod : contactMList) {
            // same with v2.1 and v3.0
            switch (contactMethod.getKind()) {
                case Contacts.KIND_EMAIL:
                    String mailType = "INTERNET";
                    if (!isNull(contactMethod.getData())) {
                        int methodType = contactMethod.getType();
                        if (emailTypeMap.containsKey(methodType)) {
                            mailType = emailTypeMap.get(methodType);
                        } else if (emailTypes.contains(contactMethod.getLabel()
                                .toUpperCase(Locale.ENGLISH))) {
                            mailType = contactMethod.getLabel().toUpperCase(Locale.ENGLISH);
                        }
                        if (emailMap.containsKey(contactMethod.getData())) {
                            mailType = emailMap.get(contactMethod.getData()) + joinMark
                                    + mailType;
                        }
                        emailMap.put(contactMethod.getData(), mailType);
                    }
                    break;
                case Contacts.KIND_POSTAL:
                    if (!isNull(contactMethod.getData())) {
                        mResult.append("ADR;TYPE=POSTAL:").append(
                                foldingString(contactMethod.getData(), version)).append(
                                mNewline);
                    }
                    break;
                default:
                    break;
            }
        }
        for (Map.Entry<String, String> email : emailMap.entrySet()) {
            if (version == VERSION_VCARD21_INT) {
                mResult.append("EMAIL;");
            } else {
                mResult.append("EMAIL;TYPE=");
            }
            mResult.append(email.getValue()).append(":").append(email.getKey())
                    .append(mNewline);
        }
    }
}
