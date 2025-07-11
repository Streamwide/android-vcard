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

package main.java.com.streamwide.smartms.lib.vcard.customcommons;

import androidx.annotation.Nullable;

public interface StringDecoder extends Decoder {

    /**
     * Decodes a String and returns a String.
     *
     * @param source
     *            the String to decode
     * @return the encoded String
     * @throws DecoderException
     *             thrown if there is an error condition during the Encoding process.
     */
    @Nullable String decode(@Nullable String source) throws DecoderException;
}

