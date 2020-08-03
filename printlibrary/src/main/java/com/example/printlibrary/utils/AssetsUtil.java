package com.example.printlibrary.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AssetsUtil {
	private static final String TAG = "AssetsUtil";

	public static List<String> getFileContent(Context context, String filePath){
		List<String> bitmapList = new ArrayList<>();
		try {
			String fileNames[] = context.getAssets().list(filePath);//获取assets目录下的所有文件及目录名
			if(fileNames.length > 0) {
				if(!bitmapList.isEmpty()){
					bitmapList.clear();
				}
				for (String name : fileNames) {
					//tStream is = context.getAssets().open(name);
					//Bitmap bitmap = BitmapFactory.decodeStream(is);
					bitmapList.add(name);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmapList;
	}

	public static void getZipFromAssets(Context context, String fileName) {
		if (fileName.contains(".zip")) {//判断是否为Zip包

			String zipName = fileName;

			AssetManager am = context.getAssets();

			try {

				AssetFileDescriptor zipFileDes = am.openFd(zipName);//Zip包名称

				//String zipSize = zipFileDes.getLength() / (1024 * 1024);//Zip包大小
				long zipSize = zipFileDes.getLength() / (1024 * 1024);//Zip包大小
				Log.i("", "压缩包大小：" + zipSize);
			} catch (IOException e) {

				e.printStackTrace();

			}



		}
	}

	//下面将assets目录下的文件拷贝到一个临时目录下


	public static boolean copyFileFromAssets(Context context, String apkName, String path){

		boolean flag = false;

		int BUFFER = 10240;

		BufferedInputStream in = null;

		BufferedOutputStream out = null;

		AssetFileDescriptor fileDescriptor = null;

		byte b[] = null;

		try {

			fileDescriptor = context.getAssets().openFd(apkName);

			File file = new File(path);

			if (file.exists()) {

				if (fileDescriptor != null

						&& fileDescriptor.getLength() == file.length()) {

					flag = true;

				} else

					file.delete();

			}

			if (!flag) {

				in = new BufferedInputStream(fileDescriptor.createInputStream(),

						BUFFER);

				boolean isOK = file.createNewFile();

				if (in != null && isOK) {

					out = new BufferedOutputStream(new FileOutputStream(file),

							BUFFER);

					b = new byte[BUFFER];

					int read = 0;

					while ((read = in.read(b)) > 0) {

						out.write(b, 0, read);

					}

					flag = true;

				}



			}

		} catch (Exception e) {

			e.printStackTrace();

		}finally{

			if(out != null){

				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			if(in != null){

				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			if(fileDescriptor != null){

				try {
					fileDescriptor.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}

		return flag;

	}



	public static String getTxtFromAssets(Context context, String fileName) {
		String result = "";
		try {
			InputStream is = context.getAssets().open(fileName);
			int lenght = is.available();
			byte[]  buffer = new byte[lenght];
			is.read(buffer);
			result = new String(buffer, "utf8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Bitmap getImgFromAssets(Context context, String fileName) {
		Bitmap bitmap = null;
		try {
			InputStream is = context.getAssets().open(fileName);
			if (is instanceof AssetInputStream) {
				Log.d(TAG, "is instanceof AssetInputStream");
			} else {
				Log.d(TAG, "is not instanceof AssetInputStream");
			}
			bitmap = BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

}
