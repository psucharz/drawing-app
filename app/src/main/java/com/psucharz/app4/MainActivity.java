package com.psucharz.app4;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Paint paint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        DrawingSurface drawingSurface = findViewById(R.id.drawingSurface);
        drawingSurface.setPaint(paint);

        Button redButton = findViewById(R.id.redButton);
        Button yellowButton = findViewById(R.id.yellowButton);
        Button blueButton = findViewById(R.id.blueButton);
        Button greenButton = findViewById(R.id.greenButton);
        Button clearButton = findViewById(R.id.clearButton);
        redButton.setOnClickListener(new ColorButtonListener());
        yellowButton.setOnClickListener(new ColorButtonListener());
        blueButton.setOnClickListener(new ColorButtonListener());
        greenButton.setOnClickListener(new ColorButtonListener());
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingSurface.clear();
            }
        });


    }
    class ColorButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            int color = ((ColorDrawable) v.getBackground()).getColor();
            paint.setColor(color);
        }
    }
}

