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

package main.java.com.streamwide.smartms.lib.vcard.text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * InputFilters can be attached to {@link Editable}s to constrain the
 * changes that can be made to them.
 */
public interface InputFilter
{
    /**
     * This method is called when the buffer is going to replace the
     * range <code>dstart &hellip; dend</code> of <code>dest</code>
     * with the new text from the range <code>start &hellip; end</code>
     * of <code>source</code>.  Return the CharSequence that you would
     * like to have placed there instead, including an empty string
     * if appropriate, or <code>null</code> to accept the original
     * replacement.  Be careful to not to reject 0-length replacements,
     * as this is what happens when you delete text.  Also beware that
     * you should not attempt to make any changes to <code>dest</code>
     * from this method; you may only examine it for context.
     *
     * Note: If <var>source</var> is an instance of {@link Spanned} or
     * {@link Spannable}, the span objects in the <var>source</var> should be
     * copied into the filtered result (i.e. the non-null return value).
     * {@link TextUtils#copySpansFrom} can be used for convenience.
     */
    public @Nullable CharSequence filter(@NonNull CharSequence source, int start, int end,
                               @NonNull Spanned dest, int dstart, int dend);


    /**
     * This filter will constrain edits not to make the length of the text
     * greater than the specified length.
     */
    public static class LengthFilter implements InputFilter {
        public LengthFilter(int max) {
            mMax = max;
        }

        public @Nullable CharSequence filter(@NonNull CharSequence source, int start, int end,
                                             @NonNull Spanned dest, int dstart, int dend) {
            int keep = mMax - (dest.length() - (dend - dstart));

            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                return null; // keep original
            } else {
                return source.subSequence(start, start + keep);
            }
        }

        private int mMax;
    }
}
