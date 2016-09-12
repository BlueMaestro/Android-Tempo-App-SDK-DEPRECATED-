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

package com.bluemaestro.utility.sdk;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Garrett on 05/08/2016.
 */
public final class StyleOverride {

    private StyleOverride(){

    }
    public static void setDefaultFont(final View view, final Context context, String fontAsset){
        try{
            if(view instanceof ViewGroup){
                ViewGroup group = (ViewGroup) view;
                for(int i = 0; i < group.getChildCount(); i++){
                    View child = group.getChildAt(i);
                    setDefaultFont(child, context, fontAsset);
                }
            } else if(view instanceof TextView){
                TextView textView = (TextView) view;
                textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + fontAsset));
            }
        } catch(Exception exception){
            exception.printStackTrace();
        }
    }

    public static void setDefaultTextColor(final View view, int color){
        try{
            String name = view.toString();
            if(view instanceof ViewGroup){
                ViewGroup group = (ViewGroup) view;
                for(int i = 0; i < group.getChildCount(); i++){
                    View child = group.getChildAt(i);
                    setDefaultTextColor(child, color);
                }
            } else if(view instanceof TextView){
                TextView textView = (TextView) view;
                textView.setTextColor(color);
            }
        } catch(Exception exception){
            exception.printStackTrace();
        }
    }

    public static void setDefaultBackgroundColor(final View view, int color){
        try{
            String name = view.toString();
            if(view instanceof ViewGroup){
                ViewGroup group = (ViewGroup) view;
                for(int i = 0; i < group.getChildCount(); i++){
                    View child = group.getChildAt(i);
                    setDefaultBackgroundColor(child, color);
                }
            }
            view.setBackgroundColor(color);
        } catch(Exception exception){
            exception.printStackTrace();
        }
    }
}
