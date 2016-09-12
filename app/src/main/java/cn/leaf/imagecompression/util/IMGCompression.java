package cn.leaf.imagecompression.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片压缩
 * Created by leaf on 2016/9/8.
 */
public class IMGCompression {
    private static final String TAG = "IMGCompression";
    private OnCompressionListener mListener;
    private File mFile;
    private String mSavePath;
    private static IMGCompression IMG;
    public static IMGCompression get(Context context){
        if(IMG==null)
            IMG = new IMGCompression();
        return IMG;
    }

    /**
     * 要压缩的文件
     * @param file
     * @return
     */
    public IMGCompression loadFile(File file){
        this.mFile = file;
        return this;
    }

    /**
     * 监听
     * @param listener
     * @return
     */
    public IMGCompression setListener(OnCompressionListener listener){
        this.mListener = listener;
        return this;
    }

    /**
     * 保存路径
     * @param savePath
     * @return
     */
    public IMGCompression setSavePath(String savePath){
        this.mSavePath = savePath;
        return this;
    }

    /**
     * 开始压缩
     * @return
     */
    public IMGCompression start(){
        try {
            File file =  compress(mFile,mSavePath);
            if(mListener!=null)
                mListener.onSuccess(file);
        }catch (Exception e){
            if(mListener!=null)
                mListener.onError(e);
        }
        return this;
    }
//    private static File getPhotoCacheDir(Context context, String cacheName) {
//        File cacheDir = context.getCacheDir();
//        if (cacheDir != null) {
//            File result = new File(cacheDir, cacheName);
//            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
//                return null;
//            }
//            return result;
//        }
//
//        return null;
//    }
    /**
     * 图片压缩
     * @param file
     * @param thumb
     * @return
     */
    private File compress(@NonNull File file,@NonNull String thumb) {
        double size;
        String filePath = file.getAbsolutePath();
        int angle = getImageSpinAngle(filePath);
        int width = getImageSize(filePath)[0];
        int height = getImageSize(filePath)[1];
        int thumbW = width;
        int thumbH = height;
        width = thumbW > thumbH ? thumbH : thumbW;
        height = thumbW > thumbH ? thumbW : thumbH;
        double scale = ((double) width / height);
        //        常见照片比例
        //        1:1--1
        //        4:5--0.8
        //        3:4--0.75
        //        13:17--0.7647058823529411
        //        11:15--0.7333333333333333
        //        8:11--0.7272727272727273
        //        5:7--0.7142857142857143
        //        2:3--0.6666666666666666
        //        5:8--0.625
        //        3:5--0.6
        //        9:16--0.5625
        //        1:2--0.5
        //        3:7--0.428571429

        //QQ  960px
        //Wechat 1280px
        if(scale<=1 && scale > 0.5625){
            //包含大部分常见比例图片
            if (file.length() / 1024 < 150) return file;
            if(height <= 1280){
                size = (width * height) / Math.pow(1280, 2) * 150;
                size = size < 60 ? 60 : size;
            }else{
                double multiple = height / 1280.0;
                thumbW = (int)(width / multiple);
                thumbH = 1280;
                size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
                size = size < 60 ? 60 : size;
            }
        }
//        else if(scale<=5625 && scale > 0.4285){
//
//        }
        else{
            //长图
            if(file.length()/1024<300)return file;
            int multiple = (int) Math.ceil(height / (1280.0 / scale));
            thumbW = width / multiple;
            thumbH = height / multiple;
            size = ((thumbW * thumbH) / (1280.0 * (1280 / scale))) * 300;
            size = size < 100 ? 100 : size;
        }

//        if (scale <= 1 && scale > 0.5625) {
//            if (height < 1664) {
//                if (file.length() / 1024 < 150) return file;
//
//                size = (width * height) / Math.pow(1664, 2) * 150;
//                size = size < 60 ? 60 : size;
//            } else if (height >= 1664 && height < 4990) {
//                thumbW = width / 2;
//                thumbH = height / 2;
//                size = (thumbW * thumbH) / Math.pow(2495, 2) * 300;
//                size = size < 60 ? 60 : size;
//            } else if (height >= 4990 && height < 10240) {
//                thumbW = width / 4;
//                thumbH = height / 4;
//                size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
//                size = size < 100 ? 100 : size;
//            } else {
//                int multiple = height / 1280 == 0 ? 1 : height / 1280;
//                thumbW = width / multiple;
//                thumbH = height / multiple;
//                size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
//                size = size < 100 ? 100 : size;
//            }
//        } else if (scale <= 0.5625 && scale > 0.5) {
//            if (height < 1280 && file.length() / 1024 < 200) return file;
//
//            int multiple = height / 1280 == 0 ? 1 : height / 1280;
//            thumbW = width / multiple;
//            thumbH = height / multiple;
//            size = (thumbW * thumbH) / (1440.0 * 2560.0) * 400;
//            size = size < 100 ? 100 : size;
//        } else {
//            int multiple = (int) Math.ceil(height / (1280.0 / scale));
//            thumbW = width / multiple;
//            thumbH = height / multiple;
//            size = ((thumbW * thumbH) / (1280.0 * (1280 / scale))) * 500;
//            size = size < 100 ? 100 : size;
//        }


        return compress(filePath, thumb, thumbW, thumbH, angle, (long) size);
    }



    /**
     * 获取图像长宽
     *
     * @param imagePath the path of image
     */
    private int[] getImageSize(String imagePath) {
        int[] res = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(imagePath, options);

        res[0] = options.outWidth;
        res[1] = options.outHeight;

        return res;
    }

    /**
     * 获取指定大小的图像
     *
     * @param imagePath
     * @param width
     * @param height
     * @return {@link Bitmap}
     */
    private Bitmap compress(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int outH = options.outHeight;
        int outW = options.outWidth;
        int inSampleSize = 1;

        if (outH > height || outW > width) {
            int halfH = outH / 2;
            int halfW = outW / 2;

            while ((halfH / inSampleSize) > height && (halfW / inSampleSize) > width) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;

        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }
        options.inJustDecodeBounds = false;
        Bitmap bit =  BitmapFactory.decodeFile(imagePath, options);
        return Bitmap.createScaledBitmap(bit, width, height, true);
    }


    /**
     * 获得图像旋转角
     *
     * @param path
     */
    private static int getImageSpinAngle(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 指定参数压缩图片
     *
     * @param largeImagePath 原图路径
     * @param thumbFilePath  保存临时路径
     * @param width
     * @param height
     * @param angle          旋转角度
     * @param size           压缩大小
     */
    private File compress(String largeImagePath, String thumbFilePath, int width, int height, int angle, long size) {
        Bitmap thbBitmap = compress(largeImagePath, width, height);

        thbBitmap = rotatingImage(angle, thbBitmap);

        return saveImage(thumbFilePath, thbBitmap, size);
    }

    /**
     * 旋转图片
     *
     * @param angle  旋转的角度
     * @param bitmap 图片
     */
    private static Bitmap rotatingImage(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 保存图片到指定路径
     *
     *
     * @param filePath  储存路径
     * @param bitmap    图片
     * @param size      期望大小
     */
    private File saveImage(String filePath, Bitmap bitmap, long size) {
        if(Util.isEmpty(bitmap)){
            Log.e(TAG,"bitmap 不能为空");
            return null;
        }
        File result = new File(filePath.substring(0, filePath.lastIndexOf("/")));

        if (!result.exists() && !result.mkdirs()) return null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);

        while (stream.toByteArray().length / 1024 > size && options > 6) {
            stream.reset();
            options -= 6;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);
        }

        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(filePath);
    }
}
