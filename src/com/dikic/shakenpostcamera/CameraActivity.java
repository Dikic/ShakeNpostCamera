package com.dikic.shakenpostcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends Activity {

    static Camera mCamera;
    private CameraPreview mPreview;
    private static PictureCallback mPicture;
    FrameLayout preview;
    Button captureButton;
    public static int orientation;
	public static final int HORIZONTAL=0;
	public static final int VERTICAL=1;
	public int w,h;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);
        w=h=0;
        // Create an instance of Camera
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
       
        orientation=0;
        captureButton=(Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                	mCamera.takePicture(null, null, mPicture);
                	 
                }
            }
        );
        mPicture = new PictureCallback() {
        	
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
            	new FileSaver().execute(data);
            	
            }
        };
  
//        startActivity(new Intent("com.dikic.shakenpostcamera.BUTTONS"));
    }
    
    private void releaseCamera() {
		// TODO Auto-generated method stub
		 if (mCamera != null){
			 mCamera.release();        // release the camera for other applications
			mCamera = null;
	        }
	} 
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		releaseCamera();
		
	}

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	/** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
   
    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
  }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
      // To be safe, you should check that the SDCard is mounted
      // using Environment.getExternalStorageState() before doing this.

      File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Shake n post");
      // This location works best if you want the created images to be shared
      // between applications and persist after your app has been uninstalled.

      // Create the storage directory if it does not exist
      if (! mediaStorageDir.exists()){
          if (! mediaStorageDir.mkdirs()){
              Log.d("MyCameraApp", "failed to create directory");
              return null;
          }
      }

      // Create a media file name
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      File mediaFile = new File(mediaStorageDir.getPath() + File.separator +"IMG_"+ timeStamp + ".jpeg");
      return mediaFile;
  }
    private class FileSaver extends AsyncTask<byte[], Integer, Boolean>{
    	ProgressDialog dialog;
		@Override
		protected Boolean doInBackground(byte[]... params) {
			// TODO Auto-generated method stub
//			Toast.makeText(CameraActivity.this, "Picture taken - "+orientation, Toast.LENGTH_SHORT).show();
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null){
                Log.d("picture callback", "Error creating media file, check storage permissions: ");
                return false;
            }
            
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(params[0]);
                fos.close();
                MediaScannerConnection.scanFile(CameraActivity.this,
    	                new String[] { pictureFile.toString() }, null,
    	                new MediaScannerConnection.OnScanCompletedListener() {
    				@Override
    				public void onScanCompleted(String path, Uri uri) {
    	                Log.i("ExternalStorage", "Scanned " + path + ":");
    	                Log.i("ExternalStorage", "-> uri=" + uri);
    	            }
    	        });
            } catch (FileNotFoundException e) {
                Log.d("pic callback", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("pick callback", "Error accessing file: " + e.getMessage());
            }
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if(result){
				Log.d("BACKGROUNG PROCES", "DOBAR");
			}else Log.d("BACKGROUNG PROCES", "!!!LOS!!!!");
			dialog.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog=new ProgressDialog(CameraActivity.this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
			dialog.setTitle("Saving...");
			dialog.setMessage("Please wait!");
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
    	
    }
}
