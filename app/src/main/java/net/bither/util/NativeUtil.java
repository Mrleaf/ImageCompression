/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bither.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class NativeUtil {
	private static int DEFAULT_QUALITY = 95;
	static {
		System.loadLibrary("jpegbither");
		System.loadLibrary("bitherjni");

	}
	public static void compressBitmap(Bitmap bit, String fileName,
			boolean optimize) {
		compressBitmap(bit, DEFAULT_QUALITY, fileName, optimize);

	}

	public static void compressBitmap(Bitmap bit, int quality, String fileName,
			boolean optimize) {
		Log.d("native", "compress of native");
		// if (bit.getConfig() != Config.ARGB_8888) {
		Bitmap result = null;
		int be = multiple(bit);
		result = Bitmap.createBitmap(bit.getWidth() / be, bit.getHeight() / be,
				Config.ARGB_8888);// 缩小3倍
		Canvas canvas = new Canvas(result);
		Rect rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());// original
		rect = new Rect(0, 0, bit.getWidth() / be, bit.getHeight() / be);// 缩小3倍
		canvas.drawBitmap(bit, null, rect, null);
		saveBitmap(result, quality, fileName, optimize);
		result.recycle();
	}

	private static int multiple(Bitmap bit){
		int w = bit.getWidth();
		int h = bit.getHeight();
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 1280f;//这里设置高度为800f
		float ww = 720f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (bit.getWidth() / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (bit.getHeight() / hh);
		}
		if (be <= 0)
			be = 1;
		return be;
	}

	public static void saveBitmap(Bitmap bit, int quality, String fileName,
			boolean optimize) {

		compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality,
				fileName.getBytes(), optimize);

	}
	private static native String compressBitmap(Bitmap bit, int w, int h,
												int quality, byte[] fileNameBytes, boolean optimize);




}
