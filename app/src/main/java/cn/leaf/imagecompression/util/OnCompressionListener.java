package cn.leaf.imagecompression.util;

import java.io.File;

/**
 * Created by lenovo on 2016/9/8.
 */
public interface OnCompressionListener {
    /**
     * 开始
     */
    void onStart();
    /**
     * 压缩成功
     * @param file
     */
    void onSuccess(File file);

    /**
     * 压缩失败
     * @param e
     */
    void onError(Throwable e);
}
