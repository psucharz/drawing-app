package com.psucharz.app4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder holder;
    private Thread drawingThread;
    private boolean threadWorking = false;
    private Object blockade = new Object();
    private Bitmap bitmap;
    private Canvas bufferCanvas;
    private Paint paint;
    private Path path;

    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        path = new Path();
    }

    public void setStrokeColor(int color){
        paint.setColor(color);
    }

    public void setPaint(Paint paint){
        this.paint = paint;
    }

    public void clear() {
        path.reset();
        bufferCanvas.drawARGB(255,255,255,255);
    }

    public void continueDrawing() {
        drawingThread = new Thread(this);
        threadWorking = true;
        drawingThread.start();
    }

    public void pauseDrawing() {
        threadWorking = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        synchronized (blockade) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.reset();
                    path.moveTo(x, y);
                    bufferCanvas.drawCircle(x, y, paint.getStrokeWidth(), paint);
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y);
                    path.moveTo(x,y);
                    paint.setStyle(Paint.Style.STROKE);
                    bufferCanvas.drawPath(path, paint);
                    paint.setStyle(Paint.Style.FILL);
                    break;
                case MotionEvent.ACTION_UP:
                    path.moveTo(x,y);
                    paint.setStyle(Paint.Style.STROKE);
                    bufferCanvas.drawPath(path,paint);
                    paint.setStyle(Paint.Style.FILL);
                    bufferCanvas.drawCircle(x, y, paint.getStrokeWidth(), paint);
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void run() {
        while (threadWorking) {
            Canvas surfaceCanvas = null;
            try {
                synchronized (holder) {
                    if (!holder.getSurface().isValid()) continue;
                    surfaceCanvas = holder.lockCanvas(null);
                    synchronized (blockade) {
                        if (threadWorking) {
                            surfaceCanvas.drawBitmap(bitmap, 0, 0, null);
                        }
                    }
                }
            }
            finally {
                if (surfaceCanvas != null) {
                    holder.getSurface().unlockCanvasAndPost(surfaceCanvas);
                }
            }
            try {
                Thread.sleep(1000 / 25);
            }
            catch (InterruptedException e) { }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bufferCanvas = new Canvas(bitmap);
        bufferCanvas.drawARGB(255,255,255,255);
        continueDrawing();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pauseDrawing();
        boolean retry = true;
        while (retry) {
            try {
                drawingThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
        threadWorking = false;
    }
}

