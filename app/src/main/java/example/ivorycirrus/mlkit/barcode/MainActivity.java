package example.ivorycirrus.mlkit.barcode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // 앱에서 필요한 권한 목록
    private String[] REQUEST_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    // 권한승인 요청 코드
    private int RESULT_PERMISSIONS = 0x9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_show_preview_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPermissionGranted()) startCameraPreviewActivity();
            }
        });
    }

    /** 카메라 프리뷰 화면으로 이동 */
    private void startCameraPreviewActivity(){
        Intent i = new Intent(this, CameraPreviewActivity.class);
        startActivityForResult(i, CameraPreviewActivity.RESULT_OK);
    }

    /** 퍼미션 권한요청 및 승인상태 확인 */
    private boolean isPermissionGranted(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {
            //Android6.0(Marshmallow) 이상인 경우 사용자 권한 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, REQUEST_PERMISSIONS, RESULT_PERMISSIONS);
                return false;
            } else {
                return true;
            }
        }else{
            //Android6.0(Marshmallow) 이하는 권한확인 안함
            return true;
        }
    }

    /** 퍼미션 권한요청 결과 처리 */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (RESULT_PERMISSIONS == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허가시
                startCameraPreviewActivity();
            } else {
                // 권한 거부시
                Toast.makeText(this, R.string.err_permission_not_granted, Toast.LENGTH_SHORT).show();
            }
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TextView rsltView = findViewById(R.id.txt_isbn);
        if(requestCode == CameraPreviewActivity.RESULT_OK && resultCode == CameraPreviewActivity.RESULT_OK) {
            rsltView.setText("ISBN : "+data.getStringExtra("isbn"));
        } else {
            rsltView.setText(R.string.result_isbn_not_detected);
        }
    }
}
