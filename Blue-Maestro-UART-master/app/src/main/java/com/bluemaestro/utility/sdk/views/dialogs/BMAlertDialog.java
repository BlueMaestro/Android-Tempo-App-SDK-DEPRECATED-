/*
 * Copyright (c) 2016, Blue Maestro
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.bluemaestro.utility.sdk.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.TextView;

import com.bluemaestro.utility.sdk.StyleOverride;
import com.bluemaestro.utility.sdk.views.generic.BMTextView;

/**
 * Created by Garrett on 18/08/2016.
 */
public class BMAlertDialog {

    private final AlertDialog.Builder builder;
    private AlertDialog dialog;

    public BMAlertDialog(Context context, String title, String message){
        final BMTextView tvtitle = new BMTextView(context);
        tvtitle.setText(title);
        tvtitle.setTextSize(20);
        this.builder = new AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setCustomTitle(tvtitle)
            .setMessage(message);
    }

    public void setPositiveButton(String message, DialogInterface.OnClickListener clickListener){
        this.builder.setPositiveButton(message, clickListener);
    }

    public void setNegativeButton(String message, DialogInterface.OnClickListener clickListener){
        this.builder.setNegativeButton(message, clickListener);
    }

    public void show(){
        this.dialog = builder.show();
    }

    public void dismiss(){
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }

    public void applyFont(Context context, String typeface){
        if(dialog == null) return;
        TextView message = (TextView) dialog.findViewById(android.R.id.message);
        Button yes = dialog.getButton(Dialog.BUTTON_POSITIVE);
        Button no = dialog.getButton(Dialog.BUTTON_NEGATIVE);

        StyleOverride.setDefaultFont(message, context, typeface);
        if(yes != null) StyleOverride.setDefaultFont(yes, context, typeface);
        if(no != null)  StyleOverride.setDefaultFont(no, context, typeface);
    }
}
