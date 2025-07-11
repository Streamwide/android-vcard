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

package main.java.com.streamwide.smartms.lib.vcard.com.android.internal.util;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CollectionUtils {

    /**
     * private constructor to hide the implicit public one.
     */
    private CollectionUtils() {
        // do nothing...
    }


    @Nullable
    public static <E> List<E> copyList(@Nullable List<E> sourceList) {
        return sourceList != null ? new ArrayList<>(sourceList) : null;
    }


    @Nullable
    public static <E> Set<E> copySet(@Nullable Set<E> sourceList) {
        return sourceList != null ? new HashSet<>(sourceList) : null;
    }

}
