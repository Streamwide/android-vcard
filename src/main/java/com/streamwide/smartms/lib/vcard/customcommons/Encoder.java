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

public interface Encoder {

    /**
     * Encodes an "Object" and returns the encoded content as an Object. The Objects here may just be
     * <code>byte[]</code> or <code>String</code>s depending on the implementation used.
     *
     * @param source
     *            An object to encode
     * @return An "encoded" Object
     * @throws EncoderException
     *             An encoder exception is thrown if the encoder experiences a failure condition during the encoding
     *             process.
     */
    @Nullable Object encode(@Nullable Object source) throws EncoderException;
}

