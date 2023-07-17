package com.xcjh.app.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 打开 assets 指定文件
 */
public class OpenAssets {

    public static String openAsFile(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        StringBuffer sb = new StringBuffer();
        try {
            inputStream = assetManager.open(fileName);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            sb.append(bufferedReader.readLine());
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append("\n" + line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
           e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
