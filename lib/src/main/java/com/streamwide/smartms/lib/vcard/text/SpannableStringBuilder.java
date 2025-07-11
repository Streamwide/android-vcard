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
import androidx.annotation.Nullable;

import com.streamwide.smartms.lib.vcard.com.android.internal.util.ArrayUtils;
import com.streamwide.smartms.lib.vcard.logger.Logger;

import java.lang.reflect.Array;

/**
 * This is the class for text whose content and markup can both be changed.
 */
public class SpannableStringBuilder
        implements /*CharSequence, GetChars,*/ Spannable, Editable//, Appendable,
//           GraphicsOperations
{
    private static final String LOG_TAG = "SpannableStringBuilder";
    /**
     * Create a new SpannableStringBuilder with empty contents
     */
    public SpannableStringBuilder() {
        this("");
    }

    /**
     * Create a new SpannableStringBuilder containing a copy of the
     * specified text, including its spans if any.
     */
    public SpannableStringBuilder(@NonNull CharSequence text) {
        this(text, 0, text.length());
    }

    /**
     * Create a new SpannableStringBuilder containing a copy of the
     * specified slice of the specified text, including its spans if any.
     */
    public SpannableStringBuilder(@NonNull CharSequence text, int start, int end) {
        int srclen = end - start;

        int len = ArrayUtils.idealCharArraySize(srclen + 1);
        mText = new char[len];
        mGapStart = srclen;
        mGapLength = len - srclen;

        TextUtils.getChars(text, start, end, mText, 0);

        mSpanCount = 0;
        int alloc = ArrayUtils.idealIntArraySize(0);
        mSpans = new Object[alloc];
        mSpanStarts = new int[alloc];
        mSpanEnds = new int[alloc];
        mSpanFlags = new int[alloc];

        if (text instanceof Spanned) {
            Spanned sp = (Spanned) text;
            Object[] spans = sp.getSpans(start, end, Object.class);

            for (int i = 0; i < spans.length; i++) {
                if (spans[i] instanceof NoCopySpan) {
                    continue;
                }

                int st = sp.getSpanStart(spans[i]) - start;
                int en = sp.getSpanEnd(spans[i]) - start;
                int fl = sp.getSpanFlags(spans[i]);

                if (st < 0)
                    st = 0;
                if (st > end - start)
                    st = end - start;

                if (en < 0)
                    en = 0;
                if (en > end - start)
                    en = end - start;

                setSpan(spans[i], st, en, fl);
            }
        }
    }

    public static @NonNull SpannableStringBuilder valueOf(@NonNull CharSequence source) {
        if (source instanceof SpannableStringBuilder) {
            return (SpannableStringBuilder) source;
        } else {
            return new SpannableStringBuilder(source);
        }
    }

    /**
     * Return the char at the specified offset within the buffer.
     */
    public char charAt(int where) {
        int len = length();
        if (where < 0) {
            throw new IndexOutOfBoundsException("charAt: " + where + " < 0");
        } else if (where >= len) {
            throw new IndexOutOfBoundsException("charAt: " + where +
                    " >= length " + len);
        }

        if (where >= mGapStart)
            return mText[where + mGapLength];
        else
            return mText[where];
    }

    /**
     * Return the number of chars in the buffer.
     */
    public int length() {
        return mText.length - mGapLength;
    }

    private void resizeFor(int size) throws Exception {
        int newlen = ArrayUtils.idealCharArraySize(size + 1);
        char[] newtext = new char[newlen];

        int after = mText.length - (mGapStart + mGapLength);

        System.arraycopy(mText, 0, newtext, 0, mGapStart);
        System.arraycopy(mText, mText.length - after,
                newtext, newlen - after, after);

        for (int i = 0; i < mSpanCount; i++) {
            if (mSpanStarts[i] > mGapStart)
                mSpanStarts[i] += newlen - mText.length;
            if (mSpanEnds[i] > mGapStart)
                mSpanEnds[i] += newlen - mText.length;
        }

        int oldlen = mText.length;
        mText = newtext;
        mGapLength += mText.length - oldlen;

        if (mGapLength < 1)
            throw new Exception("mGapLength < 1");
    }

    private void moveGapTo(int where) {
        if (where == mGapStart)
            return;

        boolean atend = (where == length());

        if (where < mGapStart) {
            int overlap = mGapStart - where;

            System.arraycopy(mText, where,
                    mText, mGapStart + mGapLength - overlap, overlap);
        } else /* where > mGapStart */ {
            int overlap = where - mGapStart;

            System.arraycopy(mText, where + mGapLength - overlap,
                    mText, mGapStart, overlap);
        }

        // XXX be more clever
        for (int i = 0; i < mSpanCount; i++) {
            int start = mSpanStarts[i];
            int end = mSpanEnds[i];

            if (start > mGapStart)
                start -= mGapLength;
            if (start > where)
                start += mGapLength;
            else if (start == where) {
                int flag = (mSpanFlags[i] & START_MASK) >> START_SHIFT;

                if (flag == POINT || (atend && flag == PARAGRAPH))
                    start += mGapLength;
            }

            if (end > mGapStart)
                end -= mGapLength;
            if (end > where)
                end += mGapLength;
            else if (end == where) {
                int flag = (mSpanFlags[i] & END_MASK);

                if (flag == POINT || (atend && flag == PARAGRAPH))
                    end += mGapLength;
            }

            mSpanStarts[i] = start;
            mSpanEnds[i] = end;
        }

        mGapStart = where;
    }

    // Documentation from interface
    public @NonNull SpannableStringBuilder insert(int where, @NonNull CharSequence tb, int start, int end) {
        return replace(where, where, tb, start, end);
    }

    // Documentation from interface
    public @NonNull SpannableStringBuilder insert(int where, @NonNull CharSequence tb) {
        return replace(where, where, tb, 0, tb.length());
    }

    // Documentation from interface
    public @NonNull SpannableStringBuilder delete(int start, int end) {
        SpannableStringBuilder ret = replace(start, end, "", 0, 0);

        if (mGapLength > 2 * length()) {
            try {
                resizeFor(length());
            } catch (Exception e) {
                Logger.error(LOG_TAG, "Exception ",e);
            }
        }

        return ret; // == this
    }

    // Documentation from interface
    public void clear() {
        replace(0, length(), "", 0, 0);
    }

    // Documentation from interface
    public void clearSpans() {
        for (int i = mSpanCount - 1; i >= 0; i--) {
            Object what = mSpans[i];
            int ostart = mSpanStarts[i];
            int oend = mSpanEnds[i];

            if (ostart > mGapStart)
                ostart -= mGapLength;
            if (oend > mGapStart)
                oend -= mGapLength;

            mSpanCount = i;
            mSpans[i] = null;

            sendSpanRemoved(what, ostart, oend);
        }
    }

    // Documentation from interface
    public @NonNull SpannableStringBuilder append(@NonNull CharSequence text) {
        int length = length();
        return replace(length, length, text, 0, text.length());
    }

    // Documentation from interface
    public @NonNull SpannableStringBuilder append(@NonNull CharSequence text, int start, int end) {
        int length = length();
        return replace(length, length, text, start, end);
    }

    // Documentation from interface
    public @NonNull SpannableStringBuilder append(char text) {
        return append(String.valueOf(text));
    }

    private int change(int start, int end,
                       CharSequence tb, int tbstart, int tbend) throws Exception {
        return change(true, start, end, tb, tbstart, tbend);
    }

    private int change(boolean notify, int start, int end,
                       CharSequence tb, int tbstart, int tbend) throws Exception {
        checkRange("replace", start, end);
        int ret = tbend - tbstart;
        TextWatcher[] recipients = null;

        if (notify)
            recipients = sendTextWillChange(start, end - start,
                    tbend - tbstart);

        for (int i = mSpanCount - 1; i >= 0; i--) {
            if ((mSpanFlags[i] & SPAN_PARAGRAPH) == SPAN_PARAGRAPH) {
                int st = mSpanStarts[i];
                if (st > mGapStart)
                    st -= mGapLength;

                int en = mSpanEnds[i];
                if (en > mGapStart)
                    en -= mGapLength;

                int ost = st;
                int oen = en;
                int clen = length();

                if (st > start && st <= end) {
                    for (st = end; st < clen; st++)
                        if (st > end && charAt(st - 1) == '\n')
                            break;
                }

                if (en > start && en <= end) {
                    for (en = end; en < clen; en++)
                        if (en > end && charAt(en - 1) == '\n')
                            break;
                }

                if (st != ost || en != oen)
                    setSpan(mSpans[i], st, en, mSpanFlags[i]);
            }
        }

        moveGapTo(end);

        if (tbend - tbstart >= mGapLength + (end - start)) {
            try {
                resizeFor(mText.length - mGapLength +
                        tbend - tbstart - (end - start));
            } catch (Exception e) {
                Logger.error(LOG_TAG, "Exception ",e);
            }
        }

        mGapStart += tbend - tbstart - (end - start);
        mGapLength -= tbend - tbstart - (end - start);

        if (mGapLength < 1)
            throw new Exception("mGapLength < 1");

        TextUtils.getChars(tb, tbstart, tbend, mText, start);

        if (tb instanceof Spanned) {
            Spanned sp = (Spanned) tb;
            Object[] spans = sp.getSpans(tbstart, tbend, Object.class);

            for (int i = 0; i < spans.length; i++) {
                int st = sp.getSpanStart(spans[i]);
                int en = sp.getSpanEnd(spans[i]);

                if (st < tbstart)
                    st = tbstart;
                if (en > tbend)
                    en = tbend;

                if (getSpanStart(spans[i]) < 0) {
                    setSpan(false, spans[i],
                            st - tbstart + start,
                            en - tbstart + start,
                            sp.getSpanFlags(spans[i]));
                }
            }
        }

        // no need for span fixup on pure insertion
        if (tbend > tbstart && end - start == 0) {
            if (notify) {
                sendTextChange(recipients, start, end - start, tbend - tbstart);
                sendTextHasChanged(recipients);
            }

            return ret;
        }

        boolean atend = (mGapStart + mGapLength == mText.length);

        for (int i = mSpanCount - 1; i >= 0; i--) {
            if (mSpanStarts[i] >= start &&
                    mSpanStarts[i] < mGapStart + mGapLength) {
                int flag = (mSpanFlags[i] & START_MASK) >> START_SHIFT;

                if (flag == POINT || (flag == PARAGRAPH && atend))
                    mSpanStarts[i] = mGapStart + mGapLength;
                else
                    mSpanStarts[i] = start;
            }

            if (mSpanEnds[i] >= start &&
                    mSpanEnds[i] < mGapStart + mGapLength) {
                int flag = (mSpanFlags[i] & END_MASK);

                if (flag == POINT || (flag == PARAGRAPH && atend))
                    mSpanEnds[i] = mGapStart + mGapLength;
                else
                    mSpanEnds[i] = start;
            }

            // remove 0-length SPAN_EXCLUSIVE_EXCLUSIVE
            // XXX send notification on removal

            if (mSpanEnds[i] < mSpanStarts[i]) {
                System.arraycopy(mSpans, i + 1,
                        mSpans, i, mSpanCount - (i + 1));
                System.arraycopy(mSpanStarts, i + 1,
                        mSpanStarts, i, mSpanCount - (i + 1));
                System.arraycopy(mSpanEnds, i + 1,
                        mSpanEnds, i, mSpanCount - (i + 1));
                System.arraycopy(mSpanFlags, i + 1,
                        mSpanFlags, i, mSpanCount - (i + 1));

                mSpanCount--;
            }
        }

        if (notify) {
            sendTextChange(recipients, start, end - start, tbend - tbstart);
            sendTextHasChanged(recipients);
        }

        return ret;
    }

    // Documentation from interface
    public @NonNull SpannableStringBuilder replace(int start, int end, @NonNull CharSequence tb) {
        return replace(start, end, tb, 0, tb.length());
    }

    // Documentation from interface
    public @NonNull SpannableStringBuilder replace(final int start, final int end,
                                          @NonNull CharSequence tb, int tbstart, int tbend) {
        int filtercount = mFilters.length;
        for (int i = 0; i < filtercount; i++) {
            CharSequence repl = mFilters[i].filter(tb, tbstart, tbend,
                    this, start, end);

            if (repl != null) {
                tb = repl;
                tbstart = 0;
                tbend = repl.length();
            }
        }

        if (end == start && tbstart == tbend) {
            return this;
        }

        if (end == start || tbstart == tbend) {
            try {
                change(start, end, tb, tbstart, tbend);
            } catch (Exception e) {
                Logger.error(LOG_TAG, "Exception ",e);
            }
        } else {
            int selstart = Selection.getSelectionStart(this);
            int selend = Selection.getSelectionEnd(this);

            // XXX just make the span fixups in change() do the right thing
            // instead of this madness!

            checkRange("replace", start, end);
            moveGapTo(end);
            TextWatcher[] recipients;

            recipients = sendTextWillChange(start, end - start,
                    tbend - tbstart);

            int origlen = end - start;

            if (mGapLength < 2) {
                try {
                    resizeFor(length() + 1);
                } catch (Exception e) {
                    Logger.error(LOG_TAG, "Exception ",e);
                }
            }

            for (int i = mSpanCount - 1; i >= 0; i--) {
                if (mSpanStarts[i] == mGapStart)
                    mSpanStarts[i]++;

                if (mSpanEnds[i] == mGapStart)
                    mSpanEnds[i]++;
            }

            mText[mGapStart] = ' ';
            mGapStart++;
            mGapLength--;

            if (mGapLength < 1)
                try {
                    throw new Exception("mGapLength < 1");
                } catch (Exception e) {
                    Logger.error(LOG_TAG, "Exception ",e);
                }

            int oldlen = (end + 1) - start;

            int inserted = 0;
            try {
                inserted = change(false, start + 1, start + 1,
                        tb, tbstart, tbend);

            change(false, start, start + 1, "", 0, 0);
            change(false, start + inserted, start + inserted + oldlen - 1,
                    "", 0, 0);

            } catch (Exception e) {
                Logger.error(LOG_TAG, "Exception ",e);
            }
            /*
             * Special case to keep the cursor in the same position
             * if it was somewhere in the middle of the replaced region.
             * If it was at the start or the end or crossing the whole
             * replacement, it should already be where it belongs.
             * accomplish this?
             */
            if (selstart > start && selstart < end) {
                long off = (long)selstart - start;

                off = off * inserted / (end - start);
                selstart = (int) off + start;

                setSpan(false, Selection.SELECTION_START, selstart, selstart,
                        SPAN_POINT_POINT);
            }
            if (selend > start && selend < end) {
                long off = (long)selend - start;

                off = off * inserted / (end - start);
                selend = (int) off + start;

                setSpan(false, Selection.SELECTION_END, selend, selend,
                        SPAN_POINT_POINT);
            }

            sendTextChange(recipients, start, origlen, inserted);
            sendTextHasChanged(recipients);
        }
        return this;
    }

    /**
     * Mark the specified range of text with the specified object.
     * The flags determine how the span will behave when text is
     * inserted at the start or end of the span's range.
     */
    public void setSpan(@NonNull Object what, int start, int end, int flags) {
        setSpan(true, what, start, end, flags);
    }

    private void setSpan(boolean send,
                         Object what, int start, int end, int flags) {
        int nstart = start;
        int nend = end;

        checkRange("setSpan", start, end);

        if (((flags & START_MASK) == (PARAGRAPH << START_SHIFT))&&(start != 0 && start != length())) {
                char c = charAt(start - 1);

                if (c != '\n')
                    throw new RuntimeException(
                            "PARAGRAPH span must start at paragraph boundary");
        }

        if (((flags & END_MASK) == PARAGRAPH) && end != 0 && end != length()) {

                char c = charAt(end - 1);

                if (c != '\n')
                    throw new RuntimeException(
                            "PARAGRAPH span must end at paragraph boundary");

        }

        if (start > mGapStart)
            start += mGapLength;
        else if (start == mGapStart) {
            int flag = (flags & START_MASK) >> START_SHIFT;

            if (flag == POINT || (flag == PARAGRAPH && start == length()))
                start += mGapLength;
        }

        if (end > mGapStart)
            end += mGapLength;
        else if (end == mGapStart) {
            int flag = (flags & END_MASK);

            if (flag == POINT || (flag == PARAGRAPH && end == length()))
                end += mGapLength;
        }

        int count = mSpanCount;
        Object[] spans = mSpans;

        for (int i = 0; i < count; i++) {
            if (spans[i] == what) {
                int ostart = mSpanStarts[i];
                int oend = mSpanEnds[i];

                if (ostart > mGapStart)
                    ostart -= mGapLength;
                if (oend > mGapStart)
                    oend -= mGapLength;

                mSpanStarts[i] = start;
                mSpanEnds[i] = end;
                mSpanFlags[i] = flags;

                if (send)
                    sendSpanChanged(what, ostart, oend, nstart, nend);

                return;
            }
        }

        if (mSpanCount + 1 >= mSpans.length) {
            int newsize = ArrayUtils.idealIntArraySize(mSpanCount + 1);
            Object[] newspans = new Object[newsize];
            int[] newspanstarts = new int[newsize];
            int[] newspanends = new int[newsize];
            int[] newspanflags = new int[newsize];

            System.arraycopy(mSpans, 0, newspans, 0, mSpanCount);
            System.arraycopy(mSpanStarts, 0, newspanstarts, 0, mSpanCount);
            System.arraycopy(mSpanEnds, 0, newspanends, 0, mSpanCount);
            System.arraycopy(mSpanFlags, 0, newspanflags, 0, mSpanCount);

            mSpans = newspans;
            mSpanStarts = newspanstarts;
            mSpanEnds = newspanends;
            mSpanFlags = newspanflags;
        }

        mSpans[mSpanCount] = what;
        mSpanStarts[mSpanCount] = start;
        mSpanEnds[mSpanCount] = end;
        mSpanFlags[mSpanCount] = flags;
        mSpanCount++;

        if (send)
            sendSpanAdded(what, nstart, nend);
    }

    /**
     * Remove the specified markup object from the buffer.
     */
    public void removeSpan(@NonNull Object what) {
        for (int i = mSpanCount - 1; i >= 0; i--) {
            if (mSpans[i] == what) {
                int ostart = mSpanStarts[i];
                int oend = mSpanEnds[i];

                if (ostart > mGapStart)
                    ostart -= mGapLength;
                if (oend > mGapStart)
                    oend -= mGapLength;

                int count = mSpanCount - (i + 1);

                System.arraycopy(mSpans, i + 1, mSpans, i, count);
                System.arraycopy(mSpanStarts, i + 1, mSpanStarts, i, count);
                System.arraycopy(mSpanEnds, i + 1, mSpanEnds, i, count);
                System.arraycopy(mSpanFlags, i + 1, mSpanFlags, i, count);

                mSpanCount--;
                mSpans[mSpanCount] = null;

                sendSpanRemoved(what, ostart, oend);
                return;
            }
        }
    }

    /**
     * Return the buffer offset of the beginning of the specified
     * markup object, or -1 if it is not attached to this buffer.
     */
    public int getSpanStart(@NonNull Object what) {
        int count = mSpanCount;
        Object[] spans = mSpans;

        for (int i = count - 1; i >= 0; i--) {
            if (spans[i] == what) {
                int where = mSpanStarts[i];

                if (where > mGapStart)
                    where -= mGapLength;

                return where;
            }
        }

        return -1;
    }

    /**
     * Return the buffer offset of the end of the specified
     * markup object, or -1 if it is not attached to this buffer.
     */
    public int getSpanEnd(@NonNull Object what) {
        int count = mSpanCount;
        Object[] spans = mSpans;

        for (int i = count - 1; i >= 0; i--) {
            if (spans[i] == what) {
                int where = mSpanEnds[i];

                if (where > mGapStart)
                    where -= mGapLength;

                return where;
            }
        }

        return -1;
    }

    /**
     * Return the flags of the end of the specified
     * markup object, or 0 if it is not attached to this buffer.
     */
    public int getSpanFlags(@NonNull Object what) {
        int count = mSpanCount;
        Object[] spans = mSpans;

        for (int i = count - 1; i >= 0; i--) {
            if (spans[i] == what) {
                return mSpanFlags[i];
            }
        }

        return 0;
    }

    /**
     * Return an array of the spans of the specified type that overlap
     * the specified range of the buffer.  The kind may be Object.class to get
     * a list of all the spans regardless of type.
     */
    public @NonNull<T> T[] getSpans(int queryStart, int queryEnd, @Nullable Class<T> kind) {
        int spanCount = mSpanCount;
        Object[] spans = mSpans;
        int[] starts = mSpanStarts;
        int[] ends = mSpanEnds;
        int[] flags = mSpanFlags;
        int gapstart = mGapStart;
        int gaplen = mGapLength;

        int count = 0;
        Object[] ret = null;
        Object ret1 = null;

        for (int i = 0; i < spanCount; i++) {
            int spanStart = starts[i];
            int spanEnd = ends[i];

            if (spanStart > gapstart) {
                spanStart -= gaplen;
            }
            if (spanEnd > gapstart) {
                spanEnd -= gaplen;
            }

            if (spanStart > queryEnd || spanEnd < queryStart) {
                continue;
            }

            if (spanStart != spanEnd && queryStart != queryEnd) {
                if (spanStart == queryEnd || spanEnd == queryStart)
                    continue;
            }

            if (kind != null && !kind.isInstance(spans[i])) {
                continue;
            }

            if (count == 0) {
                ret1 = spans[i];
                count++;
            } else {
                if (count == 1) {
                    ret = (Object[]) Array.newInstance(kind, spanCount - i + 1);
                    ret[0] = ret1;
                }

                int prio = flags[i] & SPAN_PRIORITY;
                if (prio != 0 && ret != null) {
                    int j;

                    for (j = 0; j < count; j++) {
                        int flag = ret[j] != null ?
                                getSpanFlags(ret[j]) :
                                0;

                        int p = flag & SPAN_PRIORITY;

                        if (prio > p) {
                            break;
                        }
                    }

                    System.arraycopy(ret, j, ret, j + 1, count - j);
                    ret[j] = spans[i];
                    count++;
                } else {
                    if(ret != null){
                        ret[count++] = spans[i];
                    }
                }
            }
        }

        if (count == 0) {
            return ArrayUtils.emptyArray(kind);
        }
        if (count == 1) {
            ret = (Object[]) Array.newInstance(kind, 1);
            ret[0] = ret1;
            return (T[]) ret;
        }
        if (ret != null && count == ret.length) {
            return (T[]) ret;
        }

        Object[] nret = (Object[]) Array.newInstance(kind, count);
        System.arraycopy(ret, 0, nret, 0, count);
        return (T[]) nret;
    }

    /**
     * Return the next offset after <code>start</code> but less than or
     * equal to <code>limit</code> where a span of the specified type
     * begins or ends.
     */
    public int nextSpanTransition(int start, int limit, @Nullable Class kind) {
        int count = mSpanCount;
        Object[] spans = mSpans;
        int[] starts = mSpanStarts;
        int[] ends = mSpanEnds;
        int gapstart = mGapStart;
        int gaplen = mGapLength;

        if (kind == null) {
            kind = Object.class;
        }

        for (int i = 0; i < count; i++) {
            int st = starts[i];
            int en = ends[i];

            if (st > gapstart)
                st -= gaplen;
            if (en > gapstart)
                en -= gaplen;

            if (st > start && st < limit && kind.isInstance(spans[i]))
                limit = st;
            if (en > start && en < limit && kind.isInstance(spans[i]))
                limit = en;
        }

        return limit;
    }

    /**
     * Return a new CharSequence containing a copy of the specified
     * range of this buffer, including the overlapping spans.
     */
    public @NonNull CharSequence subSequence(int start, int end) {
        return new SpannableStringBuilder(this, start, end);
    }

    /**
     * Copy the specified range of chars from this buffer into the
     * specified array, beginning at the specified offset.
     */
    public void getChars(int start, int end, @NonNull char[] dest, int destoff) {
        checkRange("getChars", start, end);

        if (end <= mGapStart) {
            System.arraycopy(mText, start, dest, destoff, end - start);
        } else if (start >= mGapStart) {
            System.arraycopy(mText, start + mGapLength,
                    dest, destoff, end - start);
        } else {
            System.arraycopy(mText, start, dest, destoff, mGapStart - start);
            System.arraycopy(mText, mGapStart + mGapLength,
                    dest, destoff + (mGapStart - start),
                    end - mGapStart);
        }
    }

    /**
     * Return a String containing a copy of the chars in this buffer.
     */
    public String toString() {
        int len = length();
        char[] buf = new char[len];

        getChars(0, len, buf, 0);
        return new String(buf);
    }

    private TextWatcher[] sendTextWillChange(int start, int before, int after) {
        TextWatcher[] recip = getSpans(start, start + before, TextWatcher.class);
        int n = recip.length;

        for (int i = 0; i < n; i++) {
            recip[i].beforeTextChanged(this, start, before, after);
        }

        return recip;
    }

    private void sendTextChange(TextWatcher[] recip, int start, int before,
                                int after) {
        int n = recip.length;

        for (int i = 0; i < n; i++) {
            recip[i].onTextChanged(this, start, before, after);
        }
    }

    private void sendTextHasChanged(TextWatcher[] recip) {
        int n = recip.length;

        for (int i = 0; i < n; i++) {
            recip[i].afterTextChanged(this);
        }
    }

    private void sendSpanAdded(Object what, int start, int end) {
        SpanWatcher[] recip = getSpans(start, end, SpanWatcher.class);
        int n = recip.length;

        for (int i = 0; i < n; i++) {
            recip[i].onSpanAdded(this, what, start, end);
        }
    }

    private void sendSpanRemoved(Object what, int start, int end) {
        SpanWatcher[] recip = getSpans(start, end, SpanWatcher.class);
        int n = recip.length;

        for (int i = 0; i < n; i++) {
            recip[i].onSpanRemoved(this, what, start, end);
        }
    }

    private void sendSpanChanged(Object what, int s, int e, int st, int en) {
        SpanWatcher[] recip = getSpans(Math.min(s, st), Math.max(e, en),
                SpanWatcher.class);
        int n = recip.length;

        for (int i = 0; i < n; i++) {
            recip[i].onSpanChanged(this, what, s, e, st, en);
        }
    }

    private static String region(int start, int end) {
        return "(" + start + " ... " + end + ")";
    }

    private void checkRange(final String operation, int start, int end) {
        if (end < start) {
            throw new IndexOutOfBoundsException(operation + " " +
                    region(start, end) +
                    " has end before start");
        }

        int len = length();

        if (start > len || end > len) {
            throw new IndexOutOfBoundsException(operation + " " +
                    region(start, end) +
                    " ends beyond length " + len);
        }

        if (start < 0 || end < 0) {
            throw new IndexOutOfBoundsException(operation + " " +
                    region(start, end) +
                    " starts before 0");
        }
    }

    // Documentation from interface
    public void setFilters(@Nullable InputFilter[] filters) {
        if (filters == null) {
            throw new IllegalArgumentException();
        }

        mFilters = filters.clone();
    }

    // Documentation from interface
    public @NonNull InputFilter[] getFilters() {
        return mFilters != null ? mFilters.clone() : NO_FILTERS;
    }

    private static final InputFilter[] NO_FILTERS = new InputFilter[0];
    private InputFilter[] mFilters = NO_FILTERS;

    private char[] mText;
    private int mGapStart;
    private int mGapLength;

    private Object[] mSpans;
    private int[] mSpanStarts;
    private int[] mSpanEnds;
    private int[] mSpanFlags;
    private int mSpanCount;

    private static final int POINT = 2;
    private static final int PARAGRAPH = 3;

    private static final int START_MASK = 0xF0;
    private static final int END_MASK = 0x0F;
    private static final int START_SHIFT = 4;
}
