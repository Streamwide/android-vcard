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

package com.streamwide.smartms.lib.vcard.syncml.pim.vcard;

import androidx.annotation.NonNull;

public class VCardException extends Exception{
    // constructors

    /**
     * Constructs a VCardException object
     */

    public VCardException()
    {
    }

    /**
     * Constructs a VCardException object
     *
     * @param message the error message
     */

    public VCardException( @NonNull String message )
    {
        super( message );
    }

}
