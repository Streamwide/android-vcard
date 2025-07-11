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

package com.streamwide.smartms.lib.vcard.syncml.pim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.streamwide.smartms.lib.vcard.com.android.internal.util.CollectionUtils;
import com.streamwide.smartms.lib.vcard.content.ContentValues;
import com.streamwide.smartms.lib.vcard.customcommons.Base64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;


public class PropertyNode {

    private String mPropName;

    private String mPropValue;

    private List<String> mPropValueVector;

    /** Store value as byte[],after decode.
     * Used when propValue is encoded by something like BASE64, QUOTED-PRINTABLE, etc.
     */
    private byte[] mPropValueBytes;

    /** param store: key=paramType, value=paramValue
     * Note that currently PropertyNode class does not support multiple param-values
     * defined in vCard 3.0 (See also RFC 2426). multiple-values are stored as
     * one String value like "A,B", not ["A", "B"]...
     */
    private ContentValues mParamMap;

    /** Only for TYPE=??? param store. */
    private Set<String> mParamMapType;

    /** Store group values. Used only in VCard. */
    private Set<String> mPropGroupSet;

    public PropertyNode() {
        mPropName = "";
        mPropValue = "";
        mPropValueVector = new ArrayList<>();
        mParamMap = new ContentValues();
        mParamMapType = new HashSet<>();
        mPropGroupSet = new HashSet<>();
    }

    public @NonNull String getPropName() {
        return mPropName;
    }

    public void setPropName(@NonNull String propName) {
        this.mPropName = propName;
    }

    public @NonNull String getPropValue() {
        return mPropValue;
    }

    public void setPropValue(@NonNull String propValue) {
        this.mPropValue = propValue;
    }

    public @NonNull List<String> getPropValueVector() {
        return CollectionUtils.copyList(mPropValueVector);
    }

    public void setPropValueVector(@NonNull List<String> propValueVector) {
        this.mPropValueVector = CollectionUtils.copyList(propValueVector);
    }

    public @Nullable byte[] getPropValueBytes() {
        return mPropValueBytes != null ? mPropValueBytes.clone() : null;
    }

    public void setPropValueBytes(@Nullable byte[] propValueBytes) {
        this.mPropValueBytes = propValueBytes != null ? propValueBytes.clone() : null;
    }

    public @NonNull ContentValues getParamMap() {
        return mParamMap;
    }

    public void setParamMap(@NonNull ContentValues paramMap) {
        this.mParamMap = paramMap;
    }

    public @NonNull Set<String> getParamMapType() {
        return CollectionUtils.copySet(mParamMapType);
    }

    public @NonNull Set<String> getPropGroupSet() {
        return CollectionUtils.copySet(mPropGroupSet);
    }


    public PropertyNode(
            @NonNull String propName, @NonNull String propValue, @NonNull List<String> propValueVector,
            @Nullable byte[] propValueBytes, @NonNull ContentValues paramMap, @NonNull Set<String> paramMapType,
            @NonNull Set<String> propGroupSet) {
        if (propName != null) {
            this.mPropName = propName;
        } else {
            this.mPropName = "";
        }
        if (propValue != null) {
            this.mPropValue = propValue;
        } else {
            this.mPropValue = "";
        }
        if (propValueVector != null) {
            this.mPropValueVector = CollectionUtils.copyList(propValueVector);
        } else {
            this.mPropValueVector = new ArrayList<>();
        }
        this.mPropValueBytes = propValueBytes != null ? propValueBytes.clone() : null;
        if (paramMap != null) {
            this.mParamMap = paramMap;
        } else {
            this.mParamMap = new ContentValues();
        }
        if (paramMapType != null) {
            this.mParamMapType = CollectionUtils.copySet(paramMapType);
        } else {
            this.mParamMapType = new HashSet<>();
        }
        if (propGroupSet != null) {
            this.mPropGroupSet = CollectionUtils.copySet(propGroupSet);
        } else {
            this.mPropGroupSet = new HashSet<>();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        PropertyNode node = (PropertyNode)obj;

            if ( (mPropName == null || !mPropName.equals(node.mPropName)) ||!mParamMap.equals(node.mParamMap) || (!mParamMapType.equals(node.mParamMapType)) || (!mPropGroupSet.equals(node.mPropGroupSet)) )  {
            return false;
        }

        if (mPropValueBytes != null && Arrays.equals(mPropValueBytes, node.mPropValueBytes)) {
            return true;
        } else {
            if (!mPropValue.equals(node.mPropValue)) {
                return false;
            }

            // The value in propValue_vector is not decoded even if it should be
            // decoded by BASE64 or QUOTED-PRINTABLE. When the size of propValue_vector
            // is 1, the encoded value is stored in propValue, so we do not have to
            // check it.
            return (mPropValueVector.equals(node.mPropValueVector) ||
                    mPropValueVector.size() == 1 ||
                    node.mPropValueVector.size() == 1);
        }
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("propName: ");
        builder.append(mPropName);
        builder.append(", paramMap: ");
        builder.append(mParamMap.toString());
        builder.append(", propmMap_TYPE: ");
        builder.append(mParamMapType.toString());
        builder.append(", propGroupSet: ");
        builder.append(mPropGroupSet.toString());
        if (mPropValueVector != null && mPropValueVector.size() > 1) {
            builder.append(", propValue_vector size: ");
            builder.append(mPropValueVector.size());
        }
        if (mPropValueBytes != null) {
            builder.append(", propValue_bytes size: ");
            builder.append(mPropValueBytes.length);
        }
        builder.append(", propValue: ");
        builder.append(mPropValue);
        return builder.toString();
    }

    /**
     * Encode this object into a string which can be decoded.
     */
    public @NonNull String encode() {
        // PropertyNode#toString() is for reading, not for parsing in the future.
        // We construct appropriate String here.
        StringBuilder builder = new StringBuilder();
        if (mPropName.length() > 0) {
            builder.append("propName:[");
            builder.append(mPropName);
            builder.append("],");
        }
        int size = mPropGroupSet.size();
        if (size > 0) {
            Set<String> set = mPropGroupSet;
            builder.append("propGroup:[");
            int i = 0;
            for (String group : set) {
                // We do not need to double quote groups.
                // group        = 1*(ALPHA / DIGIT / "-")
                builder.append(group);
                if (i < size - 1) {
                    builder.append(",");
                }
                i++;
            }
            builder.append("],");
        }

        if (mParamMap.size() > 0 || !mParamMapType.isEmpty()) {
            ContentValues values = mParamMap;
            builder.append("paramMap:[");
            size = mParamMap.size();
            int i = 0;
            for (Entry<String, Object> entry : values.valueSet()) {
                // Assuming param-key does not contain NON-ASCII nor symbols.
                //
                // According to vCard 3.0:
                // param-name   = iana-token / x-name
                builder.append(entry.getKey());

                // param-value may contain any value including NON-ASCIIs.
                // We use the following replacing rule.
                // \ -> \\
                // , -> \,
                // In String#replaceAll(), "\\\\" means a single backslash.
                builder.append("=");
                builder.append(entry.getValue().toString()
                        .replaceAll("\\\\", "\\\\\\\\")
                        .replaceAll(",", "\\\\,"));
                if (i < size -1) {
                    builder.append(",");
                }
                i++;
            }

            Set<String> set = mParamMapType;
            size = mParamMapType.size();
            if (i > 0 && size > 0) {
                builder.append(",");
            }
            i = 0;
            for (String type : set) {
                builder.append("TYPE=");
                builder.append(type
                        .replaceAll("\\\\", "\\\\\\\\")
                        .replaceAll(",", "\\\\,"));
                if (i < size - 1) {
                    builder.append(",");
                }
                i++;
            }
            builder.append("],");
        }

        size = mPropValueVector.size();
        if (size > 0) {
            builder.append("propValue:[");
            List<String> list = mPropValueVector;
            for (int i = 0; i < size; i++) {
                builder.append(list.get(i)
                        .replaceAll("\\\\", "\\\\\\\\")
                        .replaceAll(",", "\\\\,"));
                if (i < size -1) {
                    builder.append(",");
                }
            }
            builder.append("],");
        }

        return builder.toString();
    }

    public static @NonNull PropertyNode decode(@NonNull String encodedString) {
        PropertyNode propertyNode = new PropertyNode();
        String trimed = encodedString.trim();
        if (trimed.length() == 0) {
            return propertyNode;
        }
        String[] elems = trimed.split("],");

        for (String elem : elems) {
            int index = elem.indexOf('[');
            String name = elem.substring(0, index - 1);
            Pattern pattern = Pattern.compile("(?<!\\\\),");
            String[] values = pattern.split(elem.substring(index + 1), -1);
            if (name.equals("propName")) {
                propertyNode.mPropName = values[0];
            } else if (name.equals("propGroupSet")) {
                for (String value : values) {
                    propertyNode.mPropGroupSet.add(value);
                }
            } else if (name.equals("paramMap")) {
                ContentValues paramMap = propertyNode.mParamMap;
                Set<String> paramMapType = propertyNode.mParamMapType;
                for (String value : values) {
                    String[] tmp = value.split("=", 2);
                    String mapKey = tmp[0];
                    // \, -> ,
                    // \\ -> \
                    // In String#replaceAll(), "\\\\" means a single backslash.
                    String mapValue =
                            tmp[1].replaceAll("\\\\,", ",").replaceAll("\\\\\\\\", "\\\\");
                    if (mapKey.equalsIgnoreCase("TYPE")) {
                        paramMapType.add(mapValue);
                    } else {
                        paramMap.put(mapKey, mapValue);
                    }
                }
            } else if (name.equals("propValue")) {
                StringBuilder builder = new StringBuilder();
                List<String> list = propertyNode.mPropValueVector;
                int length = values.length;
                for (int i = 0; i < length; i++) {
                    String normValue = values[i]
                            .replaceAll("\\\\,", ",")
                            .replaceAll("\\\\\\\\", "\\\\");
                    list.add(normValue);
                    builder.append(normValue);
                    if (i < length - 1) {
                        builder.append(";");
                    }
                }
                propertyNode.mPropValue = builder.toString();
            }
        }

        // At this time, QUOTED-PRINTABLE is already decoded to Java String.
        // We just need to decode BASE64 String to binary.
        String encoding = propertyNode.mParamMap.getAsString("ENCODING");
        if (encoding != null &&
                (encoding.equalsIgnoreCase("BASE64") ||
                        encoding.equalsIgnoreCase("B"))) {
            propertyNode.mPropValueBytes =
                    Base64.decodeBase64(propertyNode.mPropValueVector.get(0).getBytes());
        }

        return propertyNode;
    }
}
