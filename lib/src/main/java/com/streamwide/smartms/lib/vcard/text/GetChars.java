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

package com.streamwide.smartms.lib.vcard.text;

import androidx.annotation.NonNull;

public interface GetChars
        extends CharSequence
{
    /**
     * Exactly like String.getChars(): copy chars <code>start</code>
     * through <code>end - 1</code> from this CharSequence into <code>dest</code>
     * beginning at offset <code>destoff</code>.
     */
    public void getChars(int start, int end, @NonNull char[] dest, int destoff);
}
