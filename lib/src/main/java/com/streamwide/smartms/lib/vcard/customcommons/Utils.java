/*
 *
 * 	StreamWIDE (Team on The Run)
 *
 * @createdBy  AndroidTeam on Thu, 16 May 2024 09:41:42 +0100
 * @copyright  Copyright (c) 2024 StreamWIDE UK Ltd (Team on the Run)
 * @email      support@teamontherun.com
 *
 * 	© Copyright 2024 StreamWIDE UK Ltd (Team on the Run). StreamWIDE is the copyright holder
 * 	of all code contained in this file. Do not redistribute or
 *  	re-use without permission.
 *
 * @lastModifiedOn Thu, 2 May 2024 20:52:37 +0100
 */

package com.streamwide.smartms.lib.vcard.customcommons;

/**
 * Utility methods for this package.
 *
 * <p>This class is immutable and thread-safe.</p>
 *
 * @version $Id$
 * @since 1.4
 */
class Utils {

    /**
     * Radix used in encoding and decoding.
     */
    private static final int RADIX = 16;

    /**
     * Returns the numeric value of the character <code>b</code> in radix 16.
     *
     * @param b
     *            The byte to be converted.
     * @return The numeric value represented by the character in radix 16.
     *
     * @throws DecoderException
     *             Thrown when the byte is not valid per {@link Character#digit(char,int)}
     */
    static int digit16(final byte b) throws DecoderException {
        final int i = Character.digit((char) b, RADIX);
        if (i == -1) {
            throw new DecoderException("Invalid URL encoding: not a valid digit (radix " + RADIX + "): " + b);
        }
        return i;
    }

    /**
     * Returns the upper case hex digit of the lower 4 bits of the int.
     *
     * @param b the input int
     * @return the upper case hex digit of the lower 4 bits of the int.
     */
    static char hexDigit(final int b) {
        return Character.toUpperCase(Character.forDigit(b & 0xF, RADIX));
    }

}
