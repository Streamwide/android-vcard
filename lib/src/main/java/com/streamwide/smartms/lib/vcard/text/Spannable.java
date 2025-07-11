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

/**
 * This is the interface for text to which markup objects can be
 * attached and detached.  Not all Spannable classes have mutable text;
 * see {@link Editable} for that.
 */
public interface Spannable
        extends Spanned
{
    /**
     * Attach the specified markup object to the range <code>start&hellip;end</code>
     * of the text, or move the object to that range if it was already
     * attached elsewhere.  See {@link Spanned} for an explanation of
     * what the flags mean.  The object can be one that has meaning only
     * within your application, or it can be one that the text system will
     * use to affect text display or behavior.  Some noteworthy ones are
     * the subclasses of {@link android.text.style.CharacterStyle} and
     * {@link android.text.style.ParagraphStyle}, and
     * {@link android.text.TextWatcher} and
     * {@link android.text.SpanWatcher}.
     */
    public void setSpan(@NonNull Object what, int start, int end, int flags);

    /**
     * Remove the specified object from the range of text to which it
     * was attached, if any.  It is OK to remove an object that was never
     * attached in the first place.
     */
    public void removeSpan(@NonNull Object what);


}
