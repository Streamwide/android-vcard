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

import java.util.ArrayList;
import java.util.List;

public class VNode {

    private String mVName;

    private List<PropertyNode> mPropList = new ArrayList<>();

    /** 0:parse over. 1:parsing. */
    private int mParseStatus = 1;

    public @Nullable String getVName(){
        return mVName;
    }

     void setVName(String name){
         mVName = name ;
    }

    int getParseStatus(){
        return mParseStatus;
    }

    void setParseStatus(int parseStatus) {
        this.mParseStatus = parseStatus;
    }

    public @NonNull List<PropertyNode> getPropList() {
        return mPropList;
    }

}
