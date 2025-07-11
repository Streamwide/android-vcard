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

/**
 * This interface should be added to a span object that should not be copied
 * into a new Spenned when performing a slice or copy operation on the original
 * Spanned it was placed in.
 */
public interface NoCopySpan {
    /**
     * Convenience equivalent for when you would just want a new Object() for
     * a span but want it to be no-copy.  Use this instead.
     */
    public class Concrete implements NoCopySpan {
    }
}
