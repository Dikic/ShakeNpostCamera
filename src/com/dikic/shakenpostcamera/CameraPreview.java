package com.dikic.shakenpostcamera;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    CameraActivity camAct;

	public CameraPreview(Context context, Camera camera) {
        super(context);
        camAct = (CameraActivity) context;
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            
        } catch (IOException e) {
            Log.d("surface created", "Error setting camera preview: " + e.getMessage());
        }
    }

   
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }
        
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }
       
        Camera.Parameters param=mCamera.getParameters();
        
       if(w<h){
       	
         	 mCamera.setDisplayOrientation(90);
         	 CameraActivity.orientation=CameraActivity.VERTICAL;
         }else {
         	mCamera.setDisplayOrientation(0);
         	CameraActivity.orientation=CameraActivity.HORIZONTAL;
         }
        mCamera.setParameters(param);
        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
        	
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("surface created changed", "Error starting camera preview: " + e.getMessage());
        }
    }
    
}