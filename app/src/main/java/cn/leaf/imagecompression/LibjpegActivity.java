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
import android.util.Log;
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
                if(hasPermissionInManifest(LibjpegActivity.this,MediaStore.ACTION_IMAGE_CAPTURE)){

                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                        .fromFile(new File(Environment.getExternalStorageDirectory(), "background.jpg")));
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
    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
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
                        Log.e("---0", "--" + data.getData());
                        Cursor cursor = getContentResolver().query(uri,
                                new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                        if (cursor == null) {
                            Log.e("---7", data.getData() + "");
                            return;
                        }
                        cursor.moveToFirst();
                        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        cursor.close();
                    } else {
                        Log.e("---1", data.getData() + "");
                        path = uri.getPath();
                    }
                    Log.e("---2", path);
                    File imgFile = new File(path);
                    Log.e("---2", imgFile.getAbsolutePath());
                    Log.e("---2", imgFile.getPath());
                    fileSize.setText(imgFile.length() / 1024 + "k");
                    imageSize.setText(Util.getImageSize(imgFile.getPath())[0] + " * " + Util.getImageSize(imgFile.getPath())[1]);
                    NativegetBitmap(50,path, Environment.getExternalStorageDirectory() + "/libjpeg.jpg");
                }
                break;
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory() + "/background.jpg");
                Log.e("---3", temp + "");
                if (temp != null) {
                    Log.e("---4", temp + "");
                    fileSize.setText(temp.length() / 1024 + "k");
                    imageSize.setText(Util.getImageSize(temp.getPath())[0] + " * " + Util.getImageSize(temp.getPath())[1]);
                    NativegetBitmap(50,temp.getAbsolutePath(),Environment.getExternalStorageDirectory() + "/libjpeg.jpg");
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
        Glide.with(this).load(readPath).into(image);
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
