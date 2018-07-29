package example.ivorycirrus.mlkit.barcode;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.util.List;

public class CameraPreviewActivity extends AppCompatActivity {

    public static final int RESULT_OK = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Full Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Fix orientation : portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Set layout
        setContentView(R.layout.activity_camera_preview);

        // Set ui button actions
        findViewById(R.id.btn_finish_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraPreviewActivity.this.finish();
            }
        });

        // Initialize Camera
        Camera cam = getCameraInstance();

        // Set-up preview screen
        if(cam != null) {
            // Create barcode processor for ISBN
            CustomPreviewCallback camCallback = new CustomPreviewCallback(CameraView.PREVIEW_WIDTH, CameraView.PREVIEW_HEIGHT);
            camCallback.setBarcodeDetectedListener(new OnBarcodeListener() {
                @Override
                public void onIsbnDetected(FirebaseVisionBarcode barcode) {
                    Intent result = new Intent();
                    result.putExtra("isbn", barcode.getRawValue());
                    setResult(RESULT_OK, result);
                    finish();
                }
            });

            // Create view
            CameraView camView = new CameraView(this, cam);
            camView.setPreviewCallback(camCallback);

            // Add view to UI
            FrameLayout preview = findViewById(R.id.frm_preview);
            preview.addView(camView);
        }
    }

    /** Get facing back camera instance */
    public static Camera getCameraInstance()
    {
        int camId = -1;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                camId = i;
                break;
            }
        }

        if(camId == -1) return null;

        Camera c=null;
        try{
            c=Camera.open(camId);
        }catch(Exception e){
            e.printStackTrace();
        }
        return c;
    }

    private interface OnBarcodeListener {
        void onIsbnDetected(FirebaseVisionBarcode barcode);
    }

    /** Post-processor for preview image streams */
    private class CustomPreviewCallback implements Camera.PreviewCallback, OnSuccessListener<List<FirebaseVisionBarcode>>, OnFailureListener {

        private FirebaseVisionBarcodeDetectorOptions options;
        private FirebaseVisionBarcodeDetector detector;
        private FirebaseVisionImageMetadata metadata;

        private OnBarcodeListener mBarcodeDetectedListener = null;
        public void setBarcodeDetectedListener(OnBarcodeListener mBarcodeDetectedListener) {
            this.mBarcodeDetectedListener = mBarcodeDetectedListener;
        }

        private int mImageWidth, mImageHeight;
        CustomPreviewCallback(int imageWidth, int imageHeight){
            mImageWidth = imageWidth;
            mImageHeight = imageHeight;

            options =new FirebaseVisionBarcodeDetectorOptions.Builder()
                    .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_EAN_13)
                    .build();

            detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

            metadata = new FirebaseVisionImageMetadata.Builder()
                    .setFormat(ImageFormat.NV21)
                    .setWidth(mImageWidth)
                    .setHeight(mImageHeight)
                    .setRotation(FirebaseVisionImageMetadata.ROTATION_90)
                    .build();
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            try {
                detector.detectInImage(FirebaseVisionImage.fromByteArray(data, metadata))
                        .addOnSuccessListener(this)
                        .addOnFailureListener(this);
            } catch (Exception e) {
                Log.d("CameraView", "parse error");
            }
        }

        @Override
        public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
            // Task completed successfully
            for (FirebaseVisionBarcode barcode: barcodes) {
                Log.d("Barcode", "value : "+barcode.getRawValue());

                int valueType = barcode.getValueType();
                if (valueType == FirebaseVisionBarcode.TYPE_ISBN) {
                    mBarcodeDetectedListener.onIsbnDetected(barcode);
                    return;
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception e) {
            // Task failed with an exception
            Log.i("Barcode", "read fail");
        }
    }
}
