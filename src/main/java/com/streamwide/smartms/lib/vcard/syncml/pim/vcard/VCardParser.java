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

import com.streamwide.smartms.lib.vcard.syncml.pim.VDataBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class VCardParser {

    VCardParserV21 mParser = null;

    public static final  String VERSION_VCARD21 = "vcard2.1";

    public static final  String VERSION_VCARD30 = "vcard3.0";

    public static final   int VERSION_VCARD21_INT = 1;

    public static final   int VERSION_VCARD30_INT = 2;

    String mVersion = null;


    public VCardParser() {
        //Public Vcard Parser
    }

    /**
     * If version not given. Search from vcard string of the VERSION property.
     * Then instance mParser to appropriate parser.
     *
     * @param vcardStr
     *            the content of vcard data
     */
    private void judgeVersion(String vcardStr) {
        if (mVersion == null) {// auto judge
            int verIdx = vcardStr.indexOf("\nVERSION:");
            if (verIdx == -1) // if not have VERSION, v2.1 default
                mVersion = VERSION_VCARD21;
            else {
                String verStr = vcardStr.substring(verIdx, vcardStr.indexOf(
                        '\n', verIdx + 1));
                if (verStr.indexOf("2.1") > 0)
                    mVersion = VERSION_VCARD21;
                else if (verStr.indexOf("3.0") > 0)
                    mVersion = VERSION_VCARD30;
                else
                    mVersion = VERSION_VCARD21;
            }
        }
        if (mVersion.equals(VERSION_VCARD21))
            mParser = new VCardParserV21();
        if (mVersion.equals(VERSION_VCARD30))
            mParser = new VCardParserV30();
    }

    /**
     * To make sure the vcard string has proper wrap character
     *
     * @param vcardStr
     *            the string to be checked
     * @return string after verified
     */
    private String verifyVCard(String vcardStr) {
        this.judgeVersion(vcardStr);
        // -- indent line:
        vcardStr = vcardStr.replaceAll("\r\n", "\n");
        String[] strlist = vcardStr.split("\n");
        StringBuilder v21str = new StringBuilder("");
        for (int i = 0; i < strlist.length; i++) {
            if (strlist[i].indexOf(':') < 0) {
                if (strlist[i].length() == 0 && strlist[i + 1].indexOf(':') > 0)
                    v21str.append(strlist[i]).append("\r\n");
                else
                    v21str.append(" ").append(strlist[i]).append("\r\n");
            } else
                v21str.append(strlist[i]).append("\r\n");
        }
        return v21str.toString();
    }

    /**
     * Set current version
     *
     * @param version
     *            the new version
     */
    private void setVersion(String version) {
        this.mVersion = version;
    }

    /**
     * Parse the given vcard string
     *
     * @param vcardStr
     *            to content to be parsed
     * @param encoding
     *            encoding of vcardStr
     * @param builder
     *            the data builder to hold data
     * @return true if the string is successfully parsed, else return false
     * @throws VCardException
     * @throws IOException
     */
    public boolean parse(@NonNull String vcardStr, @NonNull String encoding, @NonNull VDataBuilder builder)
            throws VCardException, IOException {

        vcardStr = this.verifyVCard(vcardStr);

        boolean isSuccess = mParser.parse(new ByteArrayInputStream(vcardStr
                .getBytes(encoding)), encoding, builder);
        if (!isSuccess) {
            if (mVersion.equals(VERSION_VCARD21)) {


                this.setVersion(VERSION_VCARD30);

                return this.parse(vcardStr, builder);
            }
            throw new VCardException("parse failed.(even use 3.0 parser)");
        }
        return true;
    }

    /**
     * Parse the given vcard string with US-ASCII encoding
     *
     * @param vcardStr
     *            to content to be parsed
     * @param builder
     *            the data builder to hold data
     * @return true if the string is successfully parsed, else return false
     * @throws VCardException
     * @throws IOException
     */
    public boolean parse(@NonNull String vcardStr, @NonNull VDataBuilder builder)
            throws VCardException, IOException {
        return parse(vcardStr, "US-ASCII", builder);
    }
}
