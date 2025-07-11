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

package main.java.com.streamwide.smartms.lib.vcard.syncml.pim;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.streamwide.smartms.lib.vcard.content.ContentValues;
import com.streamwide.smartms.lib.vcard.customcommons.Base64;
import com.streamwide.smartms.lib.vcard.customcommons.DecoderException;
import com.streamwide.smartms.lib.vcard.customcommons.QuotedPrintableCodec;
import com.streamwide.smartms.lib.vcard.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * Store the parse result to custom datastruct: VNode, PropertyNode
 * Maybe several vcard instance, so use vNodeList to store.
 * VNode: standy by a vcard instance.
 * PropertyNode: standy by a property line of a card.
 */
public class VDataBuilder implements VBuilder {
    private static final String LOG_TAG = "VDATABuilder";
    private static String mLog="Failed to encode: charset=";

    /**
     * If there's no other information available, this class uses this charset for encoding
     * byte arrays.
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /** type=VNode */
    private List<VNode> mVNodeList = new ArrayList<>();
    private int mNodeListPos = 0;
    private VNode mCurrentVNode;
    private PropertyNode mCurrentPropNode;
    private String mCurrentParamType;
    /**
     * The charset using which VParser parses the text.
     */
    private String mSourceCharset;

    private boolean mStrictLineBreakParsing;

    public @NonNull List<VNode> getVNodeList() {
        return mVNodeList;
    }

    public void setVNodeList(@NonNull List<VNode> vNodeList) {
        this.mVNodeList = vNodeList;
    }

    public VDataBuilder() {
        this(DEFAULT_CHARSET, DEFAULT_CHARSET, false);
    }

    public VDataBuilder(@NonNull String charset, boolean strictLineBreakParsing) {
        this(null, charset, strictLineBreakParsing);
    }

    /**
     * @hide sourceCharset is temporal.
     */
    public VDataBuilder(@Nullable String sourceCharset, @Nullable String targetCharset,
                        boolean strictLineBreakParsing) {
        if (sourceCharset != null) {
            mSourceCharset = sourceCharset;
        } else {
            mSourceCharset = DEFAULT_CHARSET;
        }

        mStrictLineBreakParsing = strictLineBreakParsing;
    }

    public void start() {
        // Start
    }

    public void end() {
        // End
    }

    // Note: I guess that this code assumes the Record may nest like this:
    // START:VPOS
    // ...
    // START:VPOS2
    // ...
    // END:VPOS2
    // ...
    // END:VPOS
    //
    // However the following code has a bug.
    // When error occurs after calling startRecord(), the entry which is probably
    // the cause of the error remains to be in vNodeList, while endRecord() is not called.
    //
    // I leave this code as is since I'm not familiar with vcalendar specification.
    // But I believe we should refactor this code in the future.
    // Until this, the last entry has to be removed when some error occurs.
    public void startRecord(@NonNull String type) {

        VNode vnode = new VNode();
        vnode.setParseStatus(1);
        vnode.setVName(type);
        // I feel this should be done in endRecord(), but it cannot be done because of
        // the reason above.
        mVNodeList.add(vnode);
        mNodeListPos = mVNodeList.size() - 1;
        mCurrentVNode = mVNodeList.get(mNodeListPos);
    }

    public void endRecord() {
        VNode endNode = mVNodeList.get(mNodeListPos);
        endNode.setParseStatus(0);
        while(mNodeListPos > 0){
            mNodeListPos--;
            if((mVNodeList.get(mNodeListPos)).getParseStatus() == 1)
                break;
        }
        mCurrentVNode = mVNodeList.get(mNodeListPos);
    }

    public void startProperty() {
        mCurrentPropNode = new PropertyNode();
    }

    public void endProperty() {
        mCurrentVNode.getPropList().add(mCurrentPropNode);
    }

    public void propertyName(@NonNull String name) {
        mCurrentPropNode.setPropName(name);
    }

    // Used only in VCard.
    public void propertyGroup(@NonNull String group) {
        mCurrentPropNode.getPropGroupSet().add(group);
    }

    public void propertyParamType(@NonNull String type) {
        mCurrentParamType = type;
    }

    public void propertyParamValue(@NonNull String value) {
        if (mCurrentParamType == null ||
                mCurrentParamType.equalsIgnoreCase("TYPE")) {
            mCurrentPropNode.getParamMapType().add(value);
        } else {
            mCurrentPropNode.getParamMap().put(mCurrentParamType, value);
        }

        mCurrentParamType = null;
    }

    private String encodeString(String originalString, String targetCharset) {
        if (mSourceCharset.equalsIgnoreCase(targetCharset)) {
            return originalString;
        }
        Charset charset = Charset.forName(mSourceCharset);
        ByteBuffer byteBuffer = charset.encode(originalString);
        // byteBuffer.array() "may" return byte array which is larger than
        // byteBuffer.remaining(). Here, we keep on the safe side.
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        try {
            return new String(bytes, targetCharset);
        } catch (UnsupportedEncodingException e) {
            Logger.error(LOG_TAG, mLog + targetCharset);
            return new String(bytes);
        }
    }

    private String handleOneValue(String value, String targetCharset, String encoding) {
        if (encoding != null) {
            if (encoding.equals("BASE64") || encoding.equals("B")) {
                // Assume BASE64 is used only when the number of values is 1.
                mCurrentPropNode.setPropValueBytes(Base64.decodeBase64(value.getBytes()));
                return value;
            } else if (encoding.equals("QUOTED-PRINTABLE")) {
                String quotedPrintable = value
                        .replaceAll("= ", " ").replaceAll("=\t", "\t");
                String[] lines;
                if (mStrictLineBreakParsing) {
                    lines = quotedPrintable.split("\r\n");
                } else {
                    StringBuilder builder = new StringBuilder();
                    int length = quotedPrintable.length();
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        char ch = quotedPrintable.charAt(i);
                        if (ch == '\n') {
                            list.add(builder.toString());
                            builder = new StringBuilder();
                        } else if (ch == '\r') {
                            list.add(builder.toString());
                            builder = new StringBuilder();
                            if (i < length - 1) {
                                char nextCh = quotedPrintable.charAt(i + 1);
                                if (nextCh == '\n') {
                                    i++;
                                }
                            }
                        } else {
                            builder.append(ch);
                        }
                    }
                    String finalLine = builder.toString();
                    if (finalLine.length() > 0) {
                        list.add(finalLine);
                    }
                    lines = list.toArray(new String[0]);
                }
                StringBuilder builder = new StringBuilder();
                for (String line : lines) {
                    if (line.endsWith("=")) {
                        line = line.substring(0, line.length() - 1);
                    }
                    builder.append(line);
                }
                byte[] bytes;
                try {
                    bytes = builder.toString().getBytes(mSourceCharset);
                } catch (UnsupportedEncodingException e1) {
                    Logger.error(LOG_TAG, mLog + mSourceCharset);
                    bytes = builder.toString().getBytes();
                }

                try {
                    bytes = QuotedPrintableCodec.decodeQuotedPrintable(bytes);
                } catch (DecoderException e) {
                    Logger.error(LOG_TAG, "Failed to decode quoted-printable: " + e);
                    return "";
                }

                try {
                    return new String(bytes, targetCharset);
                } catch (UnsupportedEncodingException e) {
                    Logger.error(LOG_TAG, mLog + targetCharset);
                    return new String(bytes);
                }
            }
            // Unknown encoding. Fall back to default.
        }
        return encodeString(value, targetCharset);
    }

    public void propertyValues(@Nullable List<String> values) {
        if (values == null || values.isEmpty()) {
            mCurrentPropNode.setPropValueBytes(null);
            mCurrentPropNode.getPropValueVector().clear();
            mCurrentPropNode.getPropValueVector().add("");
            mCurrentPropNode.setPropValue("");
            return;
        }

        ContentValues paramMap = mCurrentPropNode.getParamMap();

        String targetCharset = DEFAULT_CHARSET;
        String encoding = paramMap.getAsString("ENCODING");

        List<String> propValueVector=  mCurrentPropNode.getPropValueVector();
        for (String value : values) {
            String oneValue = handleOneValue(value, targetCharset, encoding);
            propValueVector.add(oneValue);
        }
        mCurrentPropNode.setPropValueVector(propValueVector);
        mCurrentPropNode.setPropValue(listToString(propValueVector));
    }

    private String listToString(List<String> list){
        int size = list.size();
        if (size > 1) {
            StringBuilder typeListB = new StringBuilder();
            for (String type : list) {
                typeListB.append(type).append(";");
            }
            int len = typeListB.length();
            if (len > 0 && typeListB.charAt(len - 1) == ';') {
                return typeListB.substring(0, len - 1);
            }
            return typeListB.toString();
        } else if (size == 1) {
            return list.get(0);
        } else {
            return "";
        }
    }

    public @Nullable String getResult(){
        return null;
    }
}
