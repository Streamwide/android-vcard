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

import com.streamwide.smartms.lib.vcard.syncml.pim.VBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class which tries to detects the source of the vCard from its properties.
 * Currently this implementation is very premature.
 * @hide
 */
public class VCardSourceDetector implements VBuilder {
    // Should only be used in package.
    static final int TYPE_UNKNOWN = 0;
    static final int TYPE_APPLE = 1;
    static final int TYPE_JAPANESE_MOBILE_PHONE = 2;  // Used in Japanese mobile phones.
    static final int TYPE_FOMA = 3;  // Used in some Japanese FOMA mobile phones.
    static final int TYPE_WINDOWS_MOBILE_JP = 4;

    private static Set<String> APPLE_SIGNS = new HashSet<>(Arrays.asList(
            "X-PHONETIC-FIRST-NAME", "X-PHONETIC-MIDDLE-NAME", "X-PHONETIC-LAST-NAME",
            "X-ABADR", "X-ABUID"));

    private static Set<String> japaneseMobilePhoneSigns = new HashSet<>(Arrays.asList(
            "X-GNO", "X-GN", "X-REDUCTION"));

    private static Set<String> windowsMobilePhoneSigns = new HashSet<>(Arrays.asList(
            "X-MICROSOFT-ASST_TEL", "X-MICROSOFT-ASSISTANT", "X-MICROSOFT-OFFICELOC"));

    // Note: these signes appears before the signs of the other type (e.g. "X-GN").
    // In other words, Japanese FOMA mobile phones are detected as FOMA, not JAPANESE_MOBILE_PHONES.
    private static Set<String> fomaSigns = new HashSet<>(Arrays.asList(
            "X-SD-VERN", "X-SD-FORMAT_VER", "X-SD-CATEGORIES", "X-SD-CLASS", "X-SD-DCREATED",
            "X-SD-DESCRIPTION"));
    private static String typeFomaCharsetSign = "X-SD-CHAR_CODE";

    private int mType = TYPE_UNKNOWN;
    // Some mobile phones (like FOMA) tells us the charset of the data.
    private boolean mNeedParseSpecifiedCharset;
    private String mSpecifiedCharset;

    public void start() {
        // Start
    }

    public void end() {
        // End
    }

    public void startRecord(@NonNull String type) {
        //Start Record
    }

    public void startProperty() {
        mNeedParseSpecifiedCharset = false;
    }
    public void endProperty() {
        // End Property
    }

    public void endRecord() {
        // End Record
    }

    public void propertyGroup(@NonNull String group) {
        // End propertyGroup
    }

    public void propertyName(@NonNull String name) {
        if (name.equalsIgnoreCase(typeFomaCharsetSign)) {
            mType = TYPE_FOMA;
            mNeedParseSpecifiedCharset = true;
            return;
        }
        if (mType != TYPE_UNKNOWN) {
            return;
        }
        if (windowsMobilePhoneSigns.contains(name)) {
            mType = TYPE_WINDOWS_MOBILE_JP;
        } else if (fomaSigns.contains(name)) {
            mType = TYPE_FOMA;
        } else if (japaneseMobilePhoneSigns.contains(name)) {
            mType = TYPE_JAPANESE_MOBILE_PHONE;
        } else if (APPLE_SIGNS.contains(name)) {
            mType = TYPE_APPLE;
        }
    }

    public void propertyParamType(@NonNull String type) {
        // PropertyParamType for type
    }

    public void propertyParamValue(@NonNull String value) {
        // PropertyParamType for value
    }

    public void propertyValues(@Nullable List<String> values) {
        if (mNeedParseSpecifiedCharset && !values.isEmpty()) {
            mSpecifiedCharset = values.get(0);
        }
    }

    int getType() {
        return mType;
    }

    /**
     * Return charset String guessed from the source's properties.
     * This method must be called after parsing target file(s).
     * @return Charset String. Null is returned if guessing the source fails.
     */
    public @Nullable String getEstimatedCharset() {
        if (mSpecifiedCharset != null) {
            return mSpecifiedCharset;
        }
        switch (mType) {
            case TYPE_WINDOWS_MOBILE_JP:
            case TYPE_FOMA:
            case TYPE_JAPANESE_MOBILE_PHONE:
                return "SHIFT_JIS";
            case TYPE_APPLE:
                return "UTF-8";
            default:
                return null;
        }
    }
}
