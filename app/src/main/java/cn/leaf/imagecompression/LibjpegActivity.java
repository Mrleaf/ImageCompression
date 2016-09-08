package cn.leaf.imagecompression;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.bither.util.NativeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.leaf.imagecompression.util.Util;

/**
 * Created by leaf on 2016/9/7.
 *
 */
public class LibjpegActivity extends Activity {
    private TextView fileSize,imageSize,thumbFileSize,thumbImageSize;
    private ImageView image,thumbImage;
    private Button button,button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libjpeg);
        fileSize = (TextView) findViewById(R.id.file_size);
        imageSize = (TextView) findViewById(R.id.image_size);
        thumbFileSize = (TextView) findViewById(R.id.thumb_file_size);
        thumbImageSize = (TextView) findViewById(R.id.thumb_image_size);
        image = (ImageView) findViewById(R.id.image);
        thumbImage = (ImageView) findViewById(R.id.thumb_image);
        button = (Button)findViewById(R.id.but);
        button1 = (Button)findViewById(R.id.but1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                        .fromFile(new File(MainActivity.dir,MainActivity.cameraFile)));
                startActivityForResult(intent, 2);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
                if (data != null) {
                    Uri uri = data.getData();
                    String path = "";
                    if (!Util.isEmpty(uri.getAuthority())) {
                        Cursor cursor = getContentResolver().query(uri,
                                new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                        if (cursor == null) {
                            return;
                        }
                        cursor.moveToFirst();
                        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        cursor.close();
                    } else {
                        path = uri.getPath();
                    }
                    File imgFile = new File(path);
                    fileSize.setText(imgFile.length() / 1024 + "k");
                    imageSize.setText(Util.getImageSize(imgFile.getPath())[0] + " * " + Util.getImageSize(imgFile.getPath())[1]);
                    NativegetBitmap(50,path,MainActivity.dir + MainActivity.libjpegFile);
                }
                break;
            case 2:
                File temp = new File(MainActivity.dir,MainActivity.cameraFile);
                if (temp != null) {
                    fileSize.setText(temp.length() / 1024 + "k");
                    imageSize.setText(Util.getImageSize(temp.getPath())[0] + " * " + Util.getImageSize(temp.getPath())[1]);
                    NativegetBitmap(50,temp.getAbsolutePath(),MainActivity.dir + MainActivity.libjpegFile);
                }
                break;
            default:
                break;
        }
    }

    /**
     * libjpeg压缩图像
     * @param quality 压缩大小 1-100
     * @param readPath   图片读取路径
     * @param savePath 保存路径
     * @return
     */
    public void NativegetBitmap(int quality,String readPath,final String savePath){
        Glide.with(this).load(readPath).into(image);//原图可能会溢出  Clamp target GC heap from 157MB to 128MB
        try {
            File f = new File(readPath);
            InputStream in = new FileInputStream(f);
//            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//            bitmapOptions.inPurgeable = true;
//            bitmapOptions.inSampleSize = 2;
//            Bitmap bit = BitmapFactory.decodeStream(in, null , bitmapOptions);
            Bitmap bit = BitmapFactory.decodeStream(in);
            NativeUtil.compressBitmap(bit, quality, savePath, true);
            File file = new File(savePath);
            thumbFileSize.setText(file.length() / 1024 + "k");
            thumbImageSize.setText(Util.getImageSize(file.getPath())[0] + " * " + Util.getImageSize(file.getPath())[1]);
            InputStream is = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            thumbImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }
}
