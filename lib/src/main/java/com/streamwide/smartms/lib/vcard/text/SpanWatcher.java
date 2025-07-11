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

/**
 * When an object of this type is attached to a Spannable, its methods
 * will be called to notify it that other markup objects have been
 * added, changed, or removed.
 */
public interface SpanWatcher extends NoCopySpan {
    /**
     * This method is called to notify you that the specified object
     * has been attached to the specified range of the text.
     */
    public void onSpanAdded(@NonNull Spannable text, @NonNull Object what, int start, int end);
    /**
     * This method is called to notify you that the specified object
     * has been detached from the specified range of the text.
     */
    public void onSpanRemoved(@NonNull Spannable text, @NonNull Object what, int start, int end);
    /**
     * This method is called to notify you that the specified object
     * has been relocated from the range <code>ostart&hellip;oend</code>
     * to the new range <code>nstart&hellip;nend</code> of the text.
     */
    public void onSpanChanged(@NonNull Spannable text, @NonNull Object what, int ostart, int oend,
                              int nstart, int nend);
}
