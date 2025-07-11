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

import androidx.annotation.NonNull;

/**
 * Thrown when there is a failure condition during the decoding process. This exception is thrown when a {@link Decoder}
 * encounters a decoding specific exception such as invalid data, or characters outside of the expected range.
 *
 * @version $Id$
 */
public class DecoderException extends Exception {

    /**
     * Declares the Serial Version Uid.
     *
     * @see <a href="http://c2.com/cgi/wiki?AlwaysDeclareSerialVersionUid">Always Declare Serial Version Uid</a>
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with <code>null</code> as its detail message. The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause}.
     *
     * @since 1.4
     */
    public DecoderException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently
     * be initialized by a call to {@link #initCause}.
     *
     * @param message
     *            The detail message which is saved for later retrieval by the {@link #getMessage()} method.
     */
    public DecoderException(@NonNull final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with <code>cause</code> is not automatically incorporated into this
     * exception's detail message.
     *
     * @param message
     *            The detail message which is saved for later retrieval by the {@link #getMessage()} method.
     * @param cause
     *            The cause which is saved for later retrieval by the {@link #getCause()} method. A <code>null</code>
     *            value is permitted, and indicates that the cause is nonexistent or unknown.
     * @since 1.4
     */
    public DecoderException(@NonNull final String message, @NonNull final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of <code>(cause==null ?
     * null : cause.toString())</code> (which typically contains the class and detail message of <code>cause</code>).
     * This constructor is useful for exceptions that are little more than wrappers for other throwables.
     *
     * @param cause
     *            The cause which is saved for later retrieval by the {@link #getCause()} method. A <code>null</code>
     *            value is permitted, and indicates that the cause is nonexistent or unknown.
     * @since 1.4
     */
    public DecoderException(@NonNull final Throwable cause) {
        super(cause);
    }
}
