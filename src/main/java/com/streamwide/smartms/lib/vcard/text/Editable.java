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
 * This is the interface for text whose content and markup
 * can be changed (as opposed
 * to immutable text like Strings).  If you make a {@link DynamicLayout}
 * of an Editable, the layout will be reflowed as the text is changed.
 */
public interface Editable
        extends CharSequence, GetChars, /*Spannable,*/ Appendable
{
    /**
     * Replaces the specified range (<code>st&hellip;en</code>) of text in this
     * Editable with a copy of the slice <code>start&hellip;end</code> from
     * <code>source</code>.  The destination slice may be empty, in which case
     * the operation is an insertion, or the source slice may be empty,
     * in which case the operation is a deletion.
     * <p>
     * Before the change is committed, each filter that was set with
     * {@link #setFilters} is given the opportunity to modify the
     * <code>source</code> text.
     * <p>
     * If <code>source</code>
     * is Spanned, the spans from it are preserved into the Editable.
     * Existing spans within the Editable that entirely cover the replaced
     * range are retained, but any that were strictly within the range
     * that was replaced are removed.  As a special case, the cursor
     * position is preserved even when the entire range where it is
     * located is replaced.
     * @return  a reference to this object.
     */
    public @NonNull Editable replace(int st, int en, @NonNull CharSequence source, int start, int end);

    /**
     * Convenience for replace(st, en, text, 0, text.length())
     * @see #replace(int, int, CharSequence, int, int)
     */
    public @NonNull Editable replace(int st, int en, @NonNull CharSequence text);

    /**
     * Convenience for replace(where, where, text, start, end)
     * @see #replace(int, int, CharSequence, int, int)
     */
    public @NonNull Editable insert(int where, @NonNull CharSequence text, int start, int end);

    /**
     * Convenience for replace(where, where, text, 0, text.length());
     * @see #replace(int, int, CharSequence, int, int)
     */
    public @NonNull Editable insert(int where, @NonNull CharSequence text);

    /**
     * Convenience for replace(st, en, "", 0, 0)
     * @see #replace(int, int, CharSequence, int, int)
     */
    public @NonNull Editable delete(int st, int en);

    /**
     * Convenience for replace(length(), length(), text, 0, text.length())
     * @see #replace(int, int, CharSequence, int, int)
     */
    public @NonNull Editable append(@NonNull CharSequence text);

    /**
     * Convenience for replace(length(), length(), text, start, end)
     * @see #replace(int, int, CharSequence, int, int)
     */
    public @NonNull Editable append(@NonNull CharSequence text, int start, int end);

    /**
     * Convenience for append(String.valueOf(text)).
     * @see #replace(int, int, CharSequence, int, int)
     */
    public @NonNull Editable append(char text);

    /**
     * Convenience for replace(0, length(), "", 0, 0)
     * @see #replace(int, int, CharSequence, int, int)
     * Note that this clears the text, not the spans;
     * use {@link #clearSpans} if you need that.
     */
    public void clear();

    /**
     * Removes all spans from the Editable, as if by calling
     * {@link #removeSpan} on each of them.
     */
    public void clearSpans();

    /**
     * Sets the series of filters that will be called in succession
     * whenever the text of this Editable is changed, each of which has
     * the opportunity to limit or transform the text that is being inserted.
     */
    public void setFilters(@Nullable InputFilter[] filters);

    /**
     * Returns the array of input filters that are currently applied
     * to changes to this Editable.
     */
    public @NonNull InputFilter[] getFilters();

    /**
     * Factory used by TextView to create new Editables.  You can subclass
     * it to provide something other than SpannableStringBuilder.
     */
    public static class Factory {
        private static Factory sInstance = new Factory();

        /**
         * Returns the standard Editable Factory.
         */
        public static @NonNull Factory getInstance() {
            return sInstance;
        }

        /**
         * Returns a new SpannedStringBuilder from the specified
         * CharSequence.  You can override this to provide
         * a different kind of Spanned.
         */
        public @NonNull Editable newEditable(@NonNull CharSequence source) {
            return new SpannableStringBuilder(source);
        }
    }
}
