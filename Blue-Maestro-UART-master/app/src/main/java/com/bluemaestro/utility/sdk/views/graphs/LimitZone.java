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

package com.bluemaestro.utility.sdk.views.graphs;


import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Garrett on 02/09/2016.
 *
 * A zone to show the area of consideration around a limit.
 */
public class LimitZone {

    protected Paint zone;

    protected float threshold;
    protected String op;

    protected ViewPortHandler viewPortHandler;
    protected Transformer axisTransformer;
    protected int color;

    public LimitZone(float threshold, String op){
        this.zone = new Paint();
        this.threshold = threshold;
        this.op = op;
    }

    public void setViewPortHandler(ViewPortHandler viewPortHandler){
        this.viewPortHandler = viewPortHandler;
    }

    public void setAxisTransformer(Transformer axisTransformer){
        this.axisTransformer = axisTransformer;
    }

    public void setColor(int color){
        this.color = color;
    }

    public void onDraw(Canvas canvas){
        float[] points = new float[4];
        points[1] = threshold;
        axisTransformer.pointValuesToPixel(points);

        float top, bottom;
        if(op.equals(">") || op.equals(">=")){
            top = 76;
            bottom = points[1];
        } else if(op.equals("<") || op.equals("<=")){
            top = points[1];
            bottom = viewPortHandler.contentBottom();
        } else{
            top = points[1];
            bottom = points[1];
        }

        zone.setColor(color);
        canvas.drawRect(
                viewPortHandler.contentLeft(),
                top,
                viewPortHandler.contentRight(),
                bottom,
                zone
        );
    }
}
