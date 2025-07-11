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

package main.java.com.streamwide.smartms.lib.vcard.syncml.pim;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface VBuilder {
    void start();

    void end();

    /**
     * @param type
     *            VXX <br>
     *            BEGIN:VXX
     */
    void startRecord(@NonNull String type);

    /** END:VXX */
    void endRecord();

    void startProperty();

    void endProperty();

    /**
     * @param group
     */
    void propertyGroup(@NonNull String group);

    /**
     * @param name
     *            N <br>
     *            N
     */
    void propertyName(@NonNull String name);

    /**
     * @param type
     *            LANGUAGE \ ENCODING <br>
     *            ;LANGUage= \ ;ENCODING=
     */
    void propertyParamType(@NonNull String type);

    /**
     * @param value
     *            FR-EN \ GBK <br>
     *            FR-EN \ GBK
     */
    void propertyParamValue(@NonNull String value);

    void propertyValues(@Nullable List<String> values);
}
