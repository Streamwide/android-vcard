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

public class Selection {
    private Selection() { /* cannot be instantiated */ }

    /*
     * Retrieving the selection
     */

    /**
     * Return the offset of the selection anchor or cursor, or -1 if
     * there is no selection or cursor.
     */
    public static final int getSelectionStart(@NonNull CharSequence text) {
        if (text instanceof Spanned)
            return ((Spanned) text).getSpanStart(SELECTION_START);
        else
            return -1;
    }

    /**
     * Return the offset of the selection edge or cursor, or -1 if
     * there is no selection or cursor.
     */
    public static final int getSelectionEnd(@NonNull CharSequence text) {
        if (text instanceof Spanned)
            return ((Spanned) text).getSpanStart(SELECTION_END);
        else
            return -1;
    }

    /*
     * Setting the selection
     */



    /**
     * Set the selection anchor to <code>start</code> and the selection edge
     * to <code>stop</code>.
     */
    public static void setSelection(@NonNull Spannable text, int start, int stop) {

        int ostart = getSelectionStart(text);
        int oend = getSelectionEnd(text);

        if (ostart != start || oend != stop) {
            text.setSpan(SELECTION_START, start, start,
                    Spanned.SPAN_POINT_POINT|Spanned.SPAN_INTERMEDIATE);
            text.setSpan(SELECTION_END, stop, stop,
                    Spanned.SPAN_POINT_POINT);
        }
    }

    /**
     * Move the cursor to offset <code>index</code>.
     */
    public static final void setSelection(@NonNull Spannable text, int index) {
        setSelection(text, index, index);
    }

    /**
     * Select the entire text.
     */
    public static final void selectAll(@NonNull Spannable text) {
        setSelection(text, 0, text.length());
    }

    /**
     * Move the selection edge to offset <code>index</code>.
     */
    public static final void extendSelection(@NonNull Spannable text, int index) {
        if (text.getSpanStart(SELECTION_END) != index)
            text.setSpan(SELECTION_END, index, index, Spanned.SPAN_POINT_POINT);
    }

    /**
     * Remove the selection or cursor, if any, from the text.
     */
    public static final void removeSelection(@NonNull Spannable text) {
        text.removeSpan(SELECTION_START);
        text.removeSpan(SELECTION_END);
    }

    /*
     * Moving the selection within the layout
     */

    private static final class START implements NoCopySpan { START(){} }
    private static final class END implements NoCopySpan { END(){} }

    /*
     * Public constants
     */

    public static final Object SELECTION_START = new START();
    public static final Object SELECTION_END = new END();
}
