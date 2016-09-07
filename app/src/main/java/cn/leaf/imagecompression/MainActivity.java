package cn.leaf.imagecompression;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.File;

/**
 * Created by leaf on 2016/9/7.
 */
public class MainActivity extends Activity {
    public static final String dir = Environment.getExternalStorageDirectory()+ "/AA";
    public static final String cameraFile = "/background.jpg";
    public static final String nativeFile = "/native.jpg";
    public static final String libjpegFile = "/libjpeg.jpg";
    private Button button1,button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file = new File(dir);
        if (!file.exists()){
            file.mkdirs();
        }
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,NativeActivity.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //libjpeg 压缩
                //直接使用so文件注意路径net.bither.util
                Intent intent = new Intent(MainActivity.this,LibjpegActivity.class);
                startActivity(intent);
            }
        });
    }
}
