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

import com.streamwide.smartms.lib.vcard.logger.Logger;
import com.streamwide.smartms.lib.vcard.syncml.pim.VBuilder;
import com.streamwide.smartms.lib.vcard.syncml.pim.VDataBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

/**
 * This class is used to parse vcard. Please refer to vCard Specification 2.1.
 */
public class VCardParserV21 {
    private static final String LOG_TAG = "VCardParser_V21";

    public static final String DEFAULT_CHARSET = "UTF-8";

    /** Store the known-type */
    private static final HashSet<String> sKnownTypeSet = new HashSet<>(
            Arrays.asList("DOM", "INTL", "POSTAL", "PARCEL", "HOME", "WORK",
                    "PREF", "VOICE", "FAX", "MSG", "CELL", "PAGER", "BBS",
                    "MODEM", "CAR", "ISDN", "VIDEO", "AOL", "APPLELINK",
                    "ATTMAIL", "CIS", "EWORLD", "INTERNET", "IBMMAIL",
                    "MCIMAIL", "POWERSHARE", "PRODIGY", "TLX", "X400", "GIF",
                    "CGM", "WMF", "BMP", "MET", "PMB", "DIB", "PICT", "TIFF",
                    "PDF", "PS", "JPEG", "QTIME", "MPEG", "MPEG2", "AVI",
                    "WAVE", "AIFF", "PCM", "X509", "PGP"));

    /** Store the known-value */
    private static final HashSet<String> sKnownValueSet = new HashSet<>(
            Arrays.asList("INLINE", "URL", "CONTENT-ID", "CID"));

    /** Store the property names available in vCard 2.1 */
    private static final HashSet<String> sAvailablePropertyNameV21 =
            new HashSet<>(Arrays.asList(
                    "BEGIN", "LOGO", "PHOTO", "LABEL", "FN", "TITLE", "SOUND",
                    "VERSION", "TEL", "EMAIL", "TZ", "GEO", "NOTE", "URL",
                    "BDAY", "ROLE", "REV", "UID", "KEY", "MAILER"));

    // Though vCard 2.1 specification does not allow "B" encoding, some data may have it.
    // We allow it for safety...
    private static final HashSet<String> sAvailableEncodingV21 =
            new HashSet<>(Arrays.asList(
                    "7BIT", "8BIT", "QUOTED-PRINTABLE", "BASE64", "B"));

    // Used only for parsing END:VCARD.
    private String mPreviousLine;

    /** The builder to build parsed data */
    protected @Nullable VBuilder mBuilder = null;

    /** The encoding type */
    protected @Nullable String mEncoding = null;

    protected final String sDefaultEncoding = "8BIT";

    // Should not directly read a line from this. Use getLine() instead.
    protected @Nullable BufferedReader mReader;

    private boolean mCanceled;

    // In some cases, vCard is nested. Currently, we only consider the most interior vCard data.
    // See v21_foma_1.vcf in test directory for more information.
    private int mNestCount;

    // In order to reduce warning message as much as possible, we hold the value which made Logger
    // emit a warning message.
    protected @NonNull HashSet<String> mWarningValueMap = new HashSet<>();

    // Just for debugging
    private long mTimeTotal;
    private long mTimeStartRecord;
    private long mTimeEndRecord;
    private long mTimeParseItem1;
    private long mTimeParseItem2;
    private long mTimeParseItem3;
    private long mTimeHandlePropertyValue1;
    private long mTimeHandlePropertyValue2;
    private long mTimeHandlePropertyValue3;

    /**
     * Create a new VCard parser.
     */
    public VCardParserV21() {
        super();
    }

    public VCardParserV21(@Nullable VCardSourceDetector detector) {
        super();
        if (detector != null && detector.getType() == VCardSourceDetector.TYPE_FOMA) {
            mNestCount = 1;
        }
    }

    /**
     * Parse the file at the given position
     * vcard_file   = [wsls] vcard [wsls]
     */
    protected void parseVCardFile() throws IOException, VCardException {
        boolean firstReading = true;
        while (true) {
            if (mCanceled || !parseOneVCard(firstReading)) {
                break;
            }
            firstReading = false;
        }

        if (mNestCount > 0) {
            boolean useCache = true;
            for (int i = 0; i < mNestCount; i++) {
                readEndVCard(useCache, true);
                useCache = false;
            }
        }
    }

    protected @NonNull String getVersion() {
        return "2.1";
    }

    /**
     * @return true when the propertyName is a valid property name.
     */
    protected boolean isValidPropertyName(@NonNull String propertyName) {
        if (!(sAvailablePropertyNameV21.contains(propertyName.toUpperCase(Locale.ENGLISH)) ||
                propertyName.startsWith("X-")) &&
                !mWarningValueMap.contains(propertyName)) {
            mWarningValueMap.add(propertyName);
            Logger.error(LOG_TAG, "Property name unsupported by vCard 2.1: " + propertyName);
        }
        return true;
    }

    /**
     * @return true when the encoding is a valid encoding.
     */
    protected boolean isValidEncoding(@NonNull String encoding) {
        return sAvailableEncodingV21.contains(encoding.toUpperCase(Locale.ENGLISH));
    }

    /**
     * @return String. It may be null, or its length may be 0
     * @throws IOException
     */
    protected @Nullable String getLine() throws IOException {
        return mReader.readLine();
    }

    /**
     * @return String with it's length > 0
     * @throws IOException
     * @throws VCardException when the stream reached end of line
     */
    protected @NonNull String getNonEmptyLine() throws IOException, VCardException {
        String line;
        while (true) {
            line = getLine();
            if (line == null) {
                throw new VCardException("Reached end of buffer.");
            } else if (line.trim().length() > 0) {
                return line;
            }
        }
    }

    /**
     *  vcard        = "BEGIN" [ws] ":" [ws] "VCARD" [ws] 1*CRLF
     *                 items *CRLF
     *                 "END" [ws] ":" [ws] "VCARD"
     */
    private boolean parseOneVCard(boolean firstReading) throws IOException, VCardException {
        boolean allowGarbage = false;
        if (firstReading && mNestCount > 0) {

                for (int i = 0; i < mNestCount; i++) {
                    if (!readBeginVCard(allowGarbage)) {
                        return false;
                    }
                    allowGarbage = true;
                }

        }

        if (!readBeginVCard(allowGarbage)) {
            return false;
        }
        long start;
        if (mBuilder != null) {
            start = System.currentTimeMillis();
            mBuilder.startRecord("VCARD");
            mTimeStartRecord += System.currentTimeMillis() - start;
        }
        start = System.currentTimeMillis();
        parseItems();
        readEndVCard(true, false);
        if (mBuilder != null) {
            start = System.currentTimeMillis();
            mBuilder.endRecord();
            mTimeEndRecord += System.currentTimeMillis() - start;
        }
        return true;
    }

    /**
     * @return True when successful. False when reaching the end of line
     * @throws IOException
     * @throws VCardException
     */
    protected boolean readBeginVCard(boolean allowGarbage)
            throws IOException, VCardException {
        String line;
        do {
            while (true) {
                line = getLine();
                if (line == null) {
                    return false;
                } else if (line.trim().length() > 0) {
                    break;
                }
            }
            String[] strArray = line.split(":", 2);
            int length = strArray.length;

            // Though vCard 2.1/3.0 specification does not allow lower cases,
            // some data may have them, so we allow it (Actually, previous code
            // had explicitly allowed "BEGIN:vCard" though there's no example).
            //
            // XXX: Not sure, but according to VDataBuilder.java, vcalendar
            // entry
            // may be nested. Just seeking "END:SOMETHING" may not be enough.
            // e.g.
            // BEGIN:VCARD
            // ... (Valid. Must parse this)
            // END:VCARD
            // BEGIN:VSOMETHING
            // ... (Must ignore this)
            // BEGIN:VSOMETHING2
            // ... (Must ignore this)
            // END:VSOMETHING2
            // ... (Must ignore this!)
            // END:VSOMETHING
            // BEGIN:VCARD
            // ... (Valid. Must parse this)
            // END:VCARD
            // INVALID_STRING (VCardException should be thrown)
            if (length == 2 &&
                    strArray[0].trim().equalsIgnoreCase("BEGIN") &&
                    strArray[1].trim().equalsIgnoreCase("VCARD")) {
                return true;
            } else if (!allowGarbage) {
                if (mNestCount > 0) {
                    mPreviousLine = line;
                    return false;
                } else {
                    throw new VCardException(
                            "Expected String \"BEGIN:VCARD\" did not come "
                                    + "(Instead, \"" + line + "\" came)");
                }
            }
        } while(allowGarbage);

        throw new VCardException("Reached where must not be reached.");
    }

    /**
     * The arguments useCache and allowGarbase are usually true and false accordingly when
     * this function is called outside this function itself.
     *
     * @param useCache When true, line is obtained from mPreviousline. Otherwise, getLine()
     * is used.
     * @param allowGarbage When true, ignore non "END:VCARD" line.
     * @throws IOException
     * @throws VCardException
     */
    protected void readEndVCard(boolean useCache, boolean allowGarbage)
            throws IOException, VCardException {
        String line;
        do {
            if (useCache) {
                // Though vCard specification does not allow lower cases,
                // some data may have them, so we allow it.
                line = mPreviousLine;
            } else {
                while (true) {
                    line = getLine();
                    if (line == null) {
                        throw new VCardException("Expected END:VCARD was not found.");
                    } else if (line.trim().length() > 0) {
                        break;
                    }
                }
            }

            String[] strArray = line.split(":", 2);
            if (strArray.length == 2 &&
                    strArray[0].trim().equalsIgnoreCase("END") &&
                    strArray[1].trim().equalsIgnoreCase("VCARD")) {
                return;
            } else if (!allowGarbage) {
                throw new VCardException("END:VCARD != \"" + mPreviousLine + "\"");
            }
            useCache = false;
        } while (allowGarbage);
    }

    /**
     * items = *CRLF item
     *       / item
     */
    protected void parseItems() throws IOException, VCardException {
        /* items *CRLF item / item */
        boolean ended = false;

        if (mBuilder != null) {
            mBuilder.startProperty();
        }
        ended = parseItem();
        if (mBuilder != null && !ended) {
            mBuilder.endProperty();
        }

        while (!ended) {
            // follow VCARD ,it wont reach endProperty
            if (mBuilder != null) {
                mBuilder.startProperty();
            }
            ended = parseItem();
            if (mBuilder != null && !ended) {
                mBuilder.endProperty();
            }
        }
    }

    /**
     * item      = [groups "."] name    [params] ":" value CRLF
     *           / [groups "."] "ADR"   [params] ":" addressparts CRLF
     *           / [groups "."] "ORG"   [params] ":" orgparts CRLF
     *           / [groups "."] "N"     [params] ":" nameparts CRLF
     *           / [groups "."] "AGENT" [params] ":" vcard CRLF
     */
    protected boolean parseItem() throws IOException, VCardException {
        mEncoding = sDefaultEncoding;

        String line = getNonEmptyLine();
        long start = System.currentTimeMillis();

        String[] propertyNameAndValue = separateLineAndHandleGroup(line);
        if (propertyNameAndValue == null) {
            return true;
        }
        if (propertyNameAndValue.length != 2) {
            throw new VCardException("Invalid line \"" + line + "\"");
        }
        String propertyName = propertyNameAndValue[0].toUpperCase(Locale.ENGLISH);
        String propertyValue = propertyNameAndValue[1];

        mTimeParseItem1 += System.currentTimeMillis() - start;

        if (propertyName.equals("ADR") ||
                propertyName.equals("ORG") ||
                propertyName.equals("N")) {
            start = System.currentTimeMillis();
            handleMultiplePropertyValue(propertyName, propertyValue);
            mTimeParseItem3 += System.currentTimeMillis() - start;
            return false;
        } else if (propertyName.equals("AGENT")) {
            handleAgent(propertyValue);
            return false;
        } else if (isValidPropertyName(propertyName)) {
            if (propertyName.equals("BEGIN")) {
                if (propertyValue.equals("VCARD")) {
                    throw new VCardNestedException("This vCard has nested vCard data in it.");
                } else {
                    throw new VCardException("Unknown BEGIN type: " + propertyValue);
                }
            }
            start = System.currentTimeMillis();
            handlePropertyValue(propertyName, propertyValue);
            mTimeParseItem2 += System.currentTimeMillis() - start;
            return false;
        }

        throw new VCardException("Unknown property name: \"" +
                propertyName + "\"");
    }

    private static final int STATE_GROUP_OR_PROPNAME = 0;
    private static  final int STATE_PARAMS = 1;
    // vCard 3.1 specification allows double-quoted param-value, while vCard 2.1 does not.
    // This is just for safety.
    private static final int STATE_PARAMS_IN_DQUOTE = 2;

    protected @Nullable String[] separateLineAndHandleGroup(@NonNull String line) throws VCardException {
        int length = line.length();
        int state = STATE_GROUP_OR_PROPNAME;
        int nameIndex = 0;

        String[] propertyNameAndValue = new String[2];

        for (int i = 0; i < length; i++) {
            char ch = line.charAt(i);
            switch (state) {
                case STATE_GROUP_OR_PROPNAME:
                    if (ch == ':') {
                        String propertyName = line.substring(nameIndex, i);
                        if (propertyName.equalsIgnoreCase("END")) {
                            mPreviousLine = line;
                            return null;
                        }
                        if (mBuilder != null) {
                            mBuilder.propertyName(propertyName);
                        }
                        propertyNameAndValue[0] = propertyName;
                        if (i < length - 1) {
                            propertyNameAndValue[1] = line.substring(i + 1);
                        } else {
                            propertyNameAndValue[1] = "";
                        }
                        return propertyNameAndValue;
                    } else if (ch == '.') {
                        String groupName = line.substring(nameIndex, i);
                        if (mBuilder != null) {
                            mBuilder.propertyGroup(groupName);
                        }
                        nameIndex = i + 1;
                    } else if (ch == ';') {
                        String propertyName = line.substring(nameIndex, i);
                        if (propertyName.equalsIgnoreCase("END")) {
                            mPreviousLine = line;
                            return null;
                        }
                        if (mBuilder != null) {
                            mBuilder.propertyName(propertyName);
                        }
                        propertyNameAndValue[0] = propertyName;
                        nameIndex = i + 1;
                        state = STATE_PARAMS;
                    }
                    break;
                case STATE_PARAMS:
                    if (ch == '"') {
                        state = STATE_PARAMS_IN_DQUOTE;
                    } else if (ch == ';') {
                        handleParams(line.substring(nameIndex, i));
                        nameIndex = i + 1;
                    } else if (ch == ':') {
                        handleParams(line.substring(nameIndex, i));
                        if (i < length - 1) {
                            propertyNameAndValue[1] = line.substring(i + 1);
                        } else {
                            propertyNameAndValue[1] = "";
                        }
                        return propertyNameAndValue;
                    }
                    break;
                case STATE_PARAMS_IN_DQUOTE:
                    if (ch == '"') {
                        state = STATE_PARAMS;
                    }
                    break;

                    default:
            }
        }

        throw new VCardException("Invalid line: \"" + line + "\"");
    }


    /**
     * params      = ";" [ws] paramlist
     * paramlist   = paramlist [ws] ";" [ws] param
     *             / param
     * param       = "TYPE" [ws] "=" [ws] ptypeval
     *             / "VALUE" [ws] "=" [ws] pvalueval
     *             / "ENCODING" [ws] "=" [ws] pencodingval
     *             / "CHARSET" [ws] "=" [ws] charsetval
     *             / "LANGUAGE" [ws] "=" [ws] langval
     *             / "X-" word [ws] "=" [ws] word
     *             / knowntype
     */
    protected void handleParams(@NonNull String params) throws VCardException {
        String[] strArray = params.split("=", 2);
        if (strArray.length == 2) {
            String paramName = strArray[0].trim();
            String paramValue = strArray[1].trim();
            if (paramName.equals("TYPE")) {
                handleType(paramValue);
            } else if (paramName.equals("VALUE")) {
                handleValue(paramValue);
            } else if (paramName.equals("ENCODING")) {
                handleEncoding(paramValue);
            } else if (paramName.equals("CHARSET")) {
                handleCharset(paramValue);
            } else if (paramName.equals("LANGUAGE")) {
                handleLanguage(paramValue);
            } else if (paramName.startsWith("X-")) {
                handleAnyParam(paramName, paramValue);
            } else {
                throw new VCardException("Unknown type \"" + paramName + "\"");
            }
        } else {
            handleType(strArray[0]);
        }
    }

    /**
     * ptypeval  = knowntype / "X-" word
     */
    protected void handleType(@NonNull String ptypeval) {
        String upperTypeValue = ptypeval;
        if (!(sKnownTypeSet.contains(upperTypeValue) || upperTypeValue.startsWith("X-")) &&
                !mWarningValueMap.contains(ptypeval)) {
            mWarningValueMap.add(ptypeval);
            Logger.error(LOG_TAG, "Type unsupported by vCard 2.1: " + ptypeval);
        }
        if (mBuilder != null) {
            mBuilder.propertyParamType("TYPE");
            mBuilder.propertyParamValue(upperTypeValue);
        }
    }

    /**
     * pvalueval = "INLINE" / "URL" / "CONTENT-ID" / "CID" / "X-" word
     */
    protected void handleValue(@NonNull String pvalueval) throws VCardException {
        if (sKnownValueSet.contains(pvalueval.toUpperCase(Locale.ENGLISH)) ||
                pvalueval.startsWith("X-")) {
            if (mBuilder != null) {
                mBuilder.propertyParamType("VALUE");
                mBuilder.propertyParamValue(pvalueval);
            }
        } else {
            throw new VCardException("Unknown value \"" + pvalueval + "\"");
        }
    }

    /**
     * pencodingval = "7BIT" / "8BIT" / "QUOTED-PRINTABLE" / "BASE64" / "X-" word
     */
    protected void handleEncoding(@NonNull String pencodingval) throws VCardException {
        if (isValidEncoding(pencodingval) ||
                pencodingval.startsWith("X-")) {
            if (mBuilder != null) {
                mBuilder.propertyParamType("ENCODING");
                mBuilder.propertyParamValue(pencodingval);
            }
            mEncoding = pencodingval;
        } else {
            throw new VCardException("Unknown encoding \"" + pencodingval + "\"");
        }
    }

    /**
     * vCard specification only allows us-ascii and iso-8859-xxx (See RFC 1521),
     * but some vCard contains other charset, so we allow them.
     */
    protected void handleCharset(@NonNull String charsetval) {
        if (mBuilder != null) {
            mBuilder.propertyParamType("CHARSET");
            mBuilder.propertyParamValue(charsetval);
        }
    }

    /**
     * See also Section 7.1 of RFC 1521
     */
    protected void handleLanguage(@NonNull String langval) throws VCardException {
        //modified to handle LANGUAGE codes properly - it crashed on vcards
        //from MS Outlook
        //see http://code.google.com/p/android-vcard/issues/detail?id=3
        final String[] strArray = langval.split("-");
        if (strArray.length > 2) {
            throw new VCardException("Invalid Language: \"" + langval + "\"");
        }
        String tmp = strArray[0];
        int length = tmp.length();
        for (int i = 0; i < length; i++) {
            if (!isLetter(tmp.charAt(i))) {
                throw new VCardException("Invalid Language: \"" + langval + "\"");
            }
        }

        if (strArray.length > 1) {
            tmp = strArray[1];
            length = tmp.length();
            for (int i = 0; i < length; i++) {
                if (!isLetter(tmp.charAt(i))) {
                    throw new VCardException("Invalid Language: \"" + langval + "\"");
                }
            }
        }
        if (mBuilder != null) {
            mBuilder.propertyParamType("LANGUAGE");
            mBuilder.propertyParamValue(langval);
        }
    }

    /**
     * Mainly for "X-" type. This accepts any kind of type without check.
     */
    protected void handleAnyParam(@NonNull String paramName, @NonNull String paramValue) {
        if (mBuilder != null) {
            mBuilder.propertyParamType(paramName);
            mBuilder.propertyParamValue(paramValue);
        }
    }

    protected void handlePropertyValue(
            @NonNull String propertyName, @NonNull String propertyValue) throws
            IOException, VCardException {
        if (mEncoding.equalsIgnoreCase("QUOTED-PRINTABLE")) {
            long start = System.currentTimeMillis();
            String result = getQuotedPrintable(propertyValue);
            if (mBuilder != null) {
                ArrayList<String> v = new ArrayList<>();
                v.add(result);
                mBuilder.propertyValues(v);
            }
            mTimeHandlePropertyValue2 += System.currentTimeMillis() - start;
        } else if (mEncoding.equalsIgnoreCase("BASE64") ||
                mEncoding.equalsIgnoreCase("B")) {
            long start = System.currentTimeMillis();
            // It is very rare, but some BASE64 data may be so big that
            // OutOfMemoryError occurs. To ignore such cases, use try-catch.
            try {
                String result = getBase64(propertyValue);
                if (mBuilder != null) {
                    ArrayList<String> v = new ArrayList<>();
                    v.add(result);
                    mBuilder.propertyValues(v);
                }
            } catch (OutOfMemoryError error) {
                Logger.error(LOG_TAG, "OutOfMemoryError happened during parsing BASE64 data!");
                if (mBuilder != null) {
                    mBuilder.propertyValues(null);
                }
            }
            mTimeHandlePropertyValue3 += System.currentTimeMillis() - start;
        } else {
            if (!(mEncoding == null || mEncoding.equalsIgnoreCase("7BIT")
                    || mEncoding.equalsIgnoreCase("8BIT")
                    || mEncoding.toUpperCase(Locale.ENGLISH).startsWith("X-"))) {
                Logger.error(LOG_TAG, "The encoding unsupported by vCard spec: \"" + mEncoding + "\".");
            }

            long start = System.currentTimeMillis();
            if (mBuilder != null) {
                ArrayList<String> v = new ArrayList<>();
                v.add(maybeUnescapeText(propertyValue));
                mBuilder.propertyValues(v);
            }
            mTimeHandlePropertyValue1 += System.currentTimeMillis() - start;
        }
    }

    protected @NonNull String getQuotedPrintable(@NonNull String firstString) throws IOException, VCardException {
        // Specifically, there may be some padding between = and CRLF.
        // See the following:
        //
        // qp-line := *(qp-segment transport-padding CRLF)
        //            qp-part transport-padding
        // qp-segment := qp-section *(SPACE / TAB) "="
        //             ; Maximum length of 76 characters
        //
        // e.g. (from RFC 2045)
        // Now's the time =
        // for all folk to come=
        //  to the aid of their country.
        if (firstString.trim().endsWith("=")) {
            // remove "transport-padding"
            int pos = firstString.length() - 1;
            StringBuilder builder = new StringBuilder();
            builder.append(firstString.substring(0, pos + 1));
            builder.append("\r\n");
            String line;
            while (true) {
                line = getLine();
                if (line == null) {
                    throw new VCardException(
                            "File ended during parsing quoted-printable String");
                }
                if (line.trim().endsWith("=")) {
                    // remove "transport-padding"
                    pos = line.length() - 1;
                    builder.append(line.substring(0, pos + 1));
                    builder.append("\r\n");
                } else {
                    builder.append(line);
                    break;
                }
            }
            return builder.toString();
        } else {
            return firstString;
        }
    }

    protected @NonNull String getBase64(@NonNull String firstString) throws IOException, VCardException {
        StringBuilder builder = new StringBuilder();
        builder.append(firstString);

        while (true) {
            String line = getLine();
            if (line == null) {
                throw new VCardException(
                        "File ended during parsing BASE64 binary");
            }
            if (line.length() == 0) {
                break;
            }
            builder.append(line);
        }

        return builder.toString();
    }

    /**
     * Mainly for "ADR", "ORG", and "N"
     * We do not care the number of strnosemi here.
     *
     * addressparts = 0*6(strnosemi ";") strnosemi
     *              ; PO Box, Extended Addr, Street, Locality, Region,
     *                Postal Code, Country Name
     * orgparts     = *(strnosemi ";") strnosemi
     *              ; First is Organization Name,
     *                remainder are Organization Units.
     * nameparts    = 0*4(strnosemi ";") strnosemi
     *              ; Family, Given, Middle, Prefix, Suffix.
     *              ; Example:Public;John;Q.;Reverend Dr.;III, Esq.
     * strnosemi    = *(*nonsemi ("\;" / "\" CRLF)) *nonsemi
     *              ; To include a semicolon in this string, it must be escaped
     *              ; with a "\" character.
     *
     * We are not sure whether we should add "\" CRLF to each value.
     * For now, we exclude them.
     */
    protected void handleMultiplePropertyValue(
            @NonNull String propertyName, @NonNull String propertyValue) throws IOException, VCardException {
        // vCard 2.1 does not allow QUOTED-PRINTABLE here, but some data have it.
        if (mEncoding.equalsIgnoreCase("QUOTED-PRINTABLE")) {
            propertyValue = getQuotedPrintable(propertyValue);
        }

        if (mBuilder != null) {
            StringBuilder builder = new StringBuilder();
            ArrayList<String> list = new ArrayList<>();
            int length = propertyValue.length();
            for (int i = 0; i < length; i++) {
                char ch = propertyValue.charAt(i);
                if (ch == '\\' && i < length - 1) {
                    char nextCh = propertyValue.charAt(i + 1);
                    String unescapedString = maybeUnescape(nextCh);
                    if (unescapedString != null) {
                        builder.append(unescapedString);
                        i++;
                    } else {
                        builder.append(ch);
                    }
                } else if (ch == ';') {
                    list.add(builder.toString());
                    builder = new StringBuilder();
                } else {
                    builder.append(ch);
                }
            }
            list.add(builder.toString());
            mBuilder.propertyValues(list);
        }
    }

    /**
     * vCard 2.1 specifies AGENT allows one vcard entry. It is not encoded at all.
     *
     * item     = ...
     *          / [groups "."] "AGENT"
     *            [params] ":" vcard CRLF
     * vcard    = "BEGIN" [ws] ":" [ws] "VCARD" [ws] 1*CRLF
     *            items *CRLF "END" [ws] ":" [ws] "VCARD"
     *
     */
    protected void handleAgent(@NonNull String propertyValue) throws VCardException {
        throw new VCardException("AGENT Property is not supported.");
     }

    /**
     * For vCard 3.0.
     */
    protected @NonNull String maybeUnescapeText(@NonNull String text) {
        return text;
    }

    /**
     * Returns unescaped String if the character should be unescaped. Return null otherwise.
     * e.g. In vCard 2.1, "\;" should be unescaped into ";" while "\x" should not be.
     */
    protected @Nullable String maybeUnescape(char ch) {
        // Original vCard 2.1 specification does not allow transformation
        // "\:" -> ":", "\," -> ",", and "\\" -> "\", but previous implementation of
        // this class allowed them, so keep it as is.
        if (ch == '\\' || ch == ';' || ch == ':' || ch == ',') {
            return String.valueOf(ch);
        } else {
            return null;
        }
    }

    /**
     * Parse the given stream and constructs VCardDataBuilder object.
     * Note that vCard 2.1 specification allows "CHARSET" parameter, and some career sets
     * local encoding to it. For example, Japanese phone career uses Shift_JIS, which
     * is not formally allowed in vCard specification.
     * As a result, there is a case where the encoding given here does not do well with
     * the "CHARSET".
     *
     * In order to avoid such cases, It may be fine to use "ISO-8859-1" as an encoding,
     * and to encode each localized String afterward.
     *
     * RFC 2426 "recommends" (not forces) to use UTF-8, so it may be OK to use
     * UTF-8 as an encoding when parsing vCard 3.0. But note that some Japanese
     * phone uses Shift_JIS as a charset (e.g. W61SH), and another uses
     * "CHARSET=SHIFT_JIS", which is explicitly prohibited in vCard 3.0 specification
     * (e.g. W53K).
     *
     * @param is
     *            The source to parse.
     * @param charset
     *            The charset.
     * @param builder
     *            The v builder which used to construct data.
     * @return Return true for success, otherwise false.
     * @throws IOException
     */
    public boolean parse(@NonNull InputStream is, @NonNull String charset, @Nullable VDataBuilder builder)
            throws IOException, VCardException {
        mReader = new CustomBufferedReader(new InputStreamReader(is, charset));

        mBuilder = builder;

        long start = System.currentTimeMillis();
        if (mBuilder != null) {
            mBuilder.start();
        }
        parseVCardFile();
        if (mBuilder != null) {
            mBuilder.end();
        }
        mTimeTotal += System.currentTimeMillis() - start;

        return true;
    }

    public boolean parse(@NonNull InputStream is, @Nullable VDataBuilder builder) throws IOException, VCardException {
        return parse(is, DEFAULT_CHARSET, builder);
    }

    /**
     * Cancel parsing.
     * Actual cancel is done after the end of the current one vcard entry parsing.
     */
    public void cancel() {
        mCanceled = true;
    }

    /**
     * It is very, very rare case, but there is a case where
     * canceled may be already true outside this object.
     * @hide
     */
    public void parse(@NonNull InputStream is, @NonNull String charset, @Nullable VBuilder builder, boolean canceled)
            throws IOException, VCardException {
        mCanceled = canceled;
        parse(is, charset, (VDataBuilder) builder);
    }

    public void showDebugInfo() {
        Logger.debug(LOG_TAG, "total parsing time:  " + mTimeTotal + " ms");
        if (mReader instanceof CustomBufferedReader) {
            Logger.debug(LOG_TAG, "total readLine time: " +
                    ((CustomBufferedReader)mReader).getTotalmillisecond() + " ms");
        }
        Logger.debug(LOG_TAG, "mTimeStartRecord: " + mTimeStartRecord + " ms");
        Logger.debug(LOG_TAG, "mTimeEndRecord: " + mTimeEndRecord + " ms");
        Logger.debug(LOG_TAG, "mTimeParseItem1: " + mTimeParseItem1 + " ms");
        Logger.debug(LOG_TAG, "mTimeParseItem2: " + mTimeParseItem2 + " ms");
        Logger.debug(LOG_TAG, "mTimeParseItem3: " + mTimeParseItem3 + " ms");
        Logger.debug(LOG_TAG, "mTimeHandlePropertyValue1: " + mTimeHandlePropertyValue1 + " ms");
        Logger.debug(LOG_TAG, "mTimeHandlePropertyValue2: " + mTimeHandlePropertyValue2 + " ms");
        Logger.debug(LOG_TAG, "mTimeHandlePropertyValue3: " + mTimeHandlePropertyValue3 + " ms");
    }

    private boolean isLetter(char ch)
    {
        return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'));
    }
}

class CustomBufferedReader extends BufferedReader {
    private long mTime;

    public CustomBufferedReader(Reader in) {
        super(in);
    }

    @Override
    public String readLine() throws IOException {
        long start = System.currentTimeMillis();
        String ret = super.readLine();
        long end = System.currentTimeMillis();
        mTime += end - start;
        return ret;
    }

    public long getTotalmillisecond() {
        return mTime;
    }
}
