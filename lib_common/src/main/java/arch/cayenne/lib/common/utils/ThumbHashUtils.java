package arch.cayenne.lib.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * ThumbHash占位图工具
 */
public class ThumbHashUtils {

    /**
     * 将ThumbHash数据转化为Bitmap图片
     */
    public static Bitmap getBitmapFromThumbHash(String thumbHash) {
        byte[] hash = Base64.decode(thumbHash, Base64.DEFAULT);
        Image image = thumbHashToRGBA(hash);
        byte[] rgba = image.rgba;
        int pixelCount = image.width * image.height;
        int[] argb = new int[pixelCount];
        for (int i = 0; i < pixelCount; i++) {
            int r = rgba[i * 4] & 0xFF;
            int g = rgba[i * 4 + 1] & 0xFF;
            int b = rgba[i * 4 + 2] & 0xFF;
            int a = rgba[i * 4 + 3] & 0xFF;
            argb[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
        Bitmap bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(argb, 0, image.width, 0, 0, image.width, image.height);
        return bitmap;
    }

    /**
     * 获取图片的ThumbHash值
     */
    public static String getImageThumbHash(Context context, int res) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), res);
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] rgba = new byte[width * height * 4];
        for (int i = 0; i < pixels.length; i++) {
            int c = pixels[i];
            int j = i * 4;
            rgba[j] = (byte) ((c >> 16) & 0xFF);
            rgba[j + 1] = (byte) ((c >> 8) & 0xFF);
            rgba[j + 2] = (byte) (c & 0xFF);
            rgba[j + 3] = (byte) ((c >> 24) & 0xFF);
        }
        byte[] hash = rgbaToThumbHash(width, height, rgba);
        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }

    public static byte[] rgbaToThumbHash(int w, int h, byte[] rgba) {
        float avg_r = 0, avg_g = 0, avg_b = 0, avg_a = 0;
        for (int i = 0, j = 0; i < w * h; i++, j += 4) {
            float alpha = (rgba[j + 3] & 255) / 255.0f;
            avg_r += alpha / 255.0f * (rgba[j] & 255);
            avg_g += alpha / 255.0f * (rgba[j + 1] & 255);
            avg_b += alpha / 255.0f * (rgba[j + 2] & 255);
            avg_a += alpha;
        }
        if (avg_a > 0) {
            avg_r /= avg_a;
            avg_g /= avg_a;
            avg_b /= avg_a;
        }
        boolean hasAlpha = avg_a < w * h;
        int l_limit = hasAlpha ? 5 : 7; // Use fewer luminance bits if there's alpha
        int lx = Math.max(1, Math.round((float) (l_limit * w) / (float) Math.max(w, h)));
        int ly = Math.max(1, Math.round((float) (l_limit * h) / (float) Math.max(w, h)));
        float[] l = new float[w * h]; // luminance
        float[] p = new float[w * h]; // yellow - blue
        float[] q = new float[w * h]; // red - green
        float[] a = new float[w * h]; // alpha
        for (int i = 0, j = 0; i < w * h; i++, j += 4) {
            float alpha = (rgba[j + 3] & 255) / 255.0f;
            float r = avg_r * (1.0f - alpha) + alpha / 255.0f * (rgba[j] & 255);
            float g = avg_g * (1.0f - alpha) + alpha / 255.0f * (rgba[j + 1] & 255);
            float b = avg_b * (1.0f - alpha) + alpha / 255.0f * (rgba[j + 2] & 255);
            l[i] = (r + g + b) / 3.0f;
            p[i] = (r + g) / 2.0f - b;
            q[i] = r - g;
            a[i] = alpha;
        }
        Channel l_channel = new Channel(Math.max(3, lx), Math.max(3, ly)).encode(w, h, l);
        Channel p_channel = new Channel(3, 3).encode(w, h, p);
        Channel q_channel = new Channel(3, 3).encode(w, h, q);
        Channel a_channel = hasAlpha ? new Channel(5, 5).encode(w, h, a) : null;
        boolean isLandscape = w > h;
        int header24 = Math.round(63.0f * l_channel.dc)
                | (Math.round(31.5f + 31.5f * p_channel.dc) << 6)
                | (Math.round(31.5f + 31.5f * q_channel.dc) << 12)
                | (Math.round(31.0f * l_channel.scale) << 18)
                | (hasAlpha ? 1 << 23 : 0);
        int header16 = (isLandscape ? ly : lx)
                | (Math.round(63.0f * p_channel.scale) << 3)
                | (Math.round(63.0f * q_channel.scale) << 9)
                | (isLandscape ? 1 << 15 : 0);
        int ac_start = hasAlpha ? 6 : 5;
        int ac_count = l_channel.ac.length + p_channel.ac.length + q_channel.ac.length
                + (hasAlpha ? a_channel.ac.length : 0);
        byte[] hash = new byte[ac_start + (ac_count + 1) / 2];
        hash[0] = (byte) header24;
        hash[1] = (byte) (header24 >> 8);
        hash[2] = (byte) (header24 >> 16);
        hash[3] = (byte) header16;
        hash[4] = (byte) (header16 >> 8);
        if (hasAlpha)
            hash[5] = (byte) (Math.round(15.0f * a_channel.dc) | (Math.round(15.0f * a_channel.scale) << 4));
        int ac_index = 0;
        ac_index = l_channel.writeTo(hash, ac_start, ac_index);
        ac_index = p_channel.writeTo(hash, ac_start, ac_index);
        ac_index = q_channel.writeTo(hash, ac_start, ac_index);
        if (hasAlpha) a_channel.writeTo(hash, ac_start, ac_index);
        return hash;
    }

    public static final class Image {
        public int width;
        public int height;
        public byte[] rgba;

        public Image(int width, int height, byte[] rgba) {
            this.width = width;
            this.height = height;
            this.rgba = rgba;
        }
    }

    private static final class Channel {
        int nx;
        int ny;
        float dc;
        float[] ac;
        float scale;

        Channel(int nx, int ny) {
            this.nx = nx;
            this.ny = ny;
            int n = 0;
            for (int cy = 0; cy < ny; cy++)
                for (int cx = cy > 0 ? 0 : 1; cx * ny < nx * (ny - cy); cx++)
                    n++;
            ac = new float[n];
        }

        Channel encode(int w, int h, float[] channel) {
            int n = 0;
            float[] fx = new float[w];
            for (int cy = 0; cy < ny; cy++) {
                for (int cx = 0; cx * ny < nx * (ny - cy); cx++) {
                    float f = 0;
                    for (int x = 0; x < w; x++)
                        fx[x] = (float) Math.cos(Math.PI / w * cx * (x + 0.5f));
                    for (int y = 0; y < h; y++) {
                        float fy = (float) Math.cos(Math.PI / h * cy * (y + 0.5f));
                        for (int x = 0; x < w; x++)
                            f += channel[x + y * w] * fx[x] * fy;
                    }
                    f /= w * h;
                    if (cx > 0 || cy > 0) {
                        ac[n++] = f;
                        scale = Math.max(scale, Math.abs(f));
                    } else {
                        dc = f;
                    }
                }
            }
            if (scale > 0)
                for (int i = 0; i < ac.length; i++)
                    ac[i] = 0.5f + 0.5f / scale * ac[i];
            return this;
        }

        int decode(byte[] hash, int start, int index, float scale) {
            for (int i = 0; i < ac.length; i++) {
                int data = hash[start + (index >> 1)] >> ((index & 1) << 2);
                ac[i] = ((float) (data & 15) / 7.5f - 1.0f) * scale;
                index++;
            }
            return index;
        }

        int writeTo(byte[] hash, int start, int index) {
            for (float v : ac) {
                hash[start + (index >> 1)] |= Math.round(15.0f * v) << ((index & 1) << 2);
                index++;
            }
            return index;
        }
    }

    public static float thumbHashToApproximateAspectRatio(byte[] hash) {
        byte header = hash[3];
        boolean hasAlpha = (hash[2] & 0x80) != 0;
        boolean isLandscape = (hash[4] & 0x80) != 0;
        int lx = isLandscape ? hasAlpha ? 5 : 7 : header & 7;
        int ly = isLandscape ? header & 7 : hasAlpha ? 5 : 7;
        return (float) lx / (float) ly;
    }

    public static Image thumbHashToRGBA(byte[] hash) {
        int header24 = (hash[0] & 255) | ((hash[1] & 255) << 8) | ((hash[2] & 255) << 16);
        int header16 = (hash[3] & 255) | ((hash[4] & 255) << 8);
        float l_dc = (float) (header24 & 63) / 63.0f;
        float p_dc = (float) ((header24 >> 6) & 63) / 31.5f - 1.0f;
        float q_dc = (float) ((header24 >> 12) & 63) / 31.5f - 1.0f;
        float l_scale = (float) ((header24 >> 18) & 31) / 31.0f;
        boolean hasAlpha = (header24 >> 23) != 0;
        float p_scale = (float) ((header16 >> 3) & 63) / 63.0f;
        float q_scale = (float) ((header16 >> 9) & 63) / 63.0f;
        boolean isLandscape = (header16 >> 15) != 0;
        int lx = Math.max(3, isLandscape ? hasAlpha ? 5 : 7 : header16 & 7);
        int ly = Math.max(3, isLandscape ? header16 & 7 : hasAlpha ? 5 : 7);
        float a_dc = hasAlpha ? (float) (hash[5] & 15) / 15.0f : 1.0f;
        float a_scale = (float) ((hash[5] >> 4) & 15) / 15.0f;
        int ac_start = hasAlpha ? 6 : 5;
        int ac_index = 0;
        Channel l_channel = new Channel(lx, ly);
        Channel p_channel = new Channel(3, 3);
        Channel q_channel = new Channel(3, 3);
        Channel a_channel = null;
        ac_index = l_channel.decode(hash, ac_start, ac_index, l_scale);
        ac_index = p_channel.decode(hash, ac_start, ac_index, p_scale * 1.25f);
        ac_index = q_channel.decode(hash, ac_start, ac_index, q_scale * 1.25f);
        if (hasAlpha) {
            a_channel = new Channel(5, 5);
            a_channel.decode(hash, ac_start, ac_index, a_scale);
        }
        float[] l_ac = l_channel.ac;
        float[] p_ac = p_channel.ac;
        float[] q_ac = q_channel.ac;
        float[] a_ac = hasAlpha ? a_channel.ac : null;
        float ratio = thumbHashToApproximateAspectRatio(hash);
        int w = Math.round(ratio > 1.0f ? 32.0f : 32.0f * ratio);
        int h = Math.round(ratio > 1.0f ? 32.0f / ratio : 32.0f);
        byte[] rgba = new byte[w * h * 4];
        int cx_stop = Math.max(lx, hasAlpha ? 5 : 3);
        int cy_stop = Math.max(ly, hasAlpha ? 5 : 3);
        float[] fx = new float[cx_stop];
        float[] fy = new float[cy_stop];
        for (int y = 0, i = 0; y < h; y++) {
            for (int x = 0; x < w; x++, i += 4) {
                float l = l_dc, p = p_dc, q = q_dc, a = a_dc;
                for (int cx = 0; cx < cx_stop; cx++)
                    fx[cx] = (float) Math.cos(Math.PI / w * (x + 0.5f) * cx);
                for (int cy = 0; cy < cy_stop; cy++)
                    fy[cy] = (float) Math.cos(Math.PI / h * (y + 0.5f) * cy);
                for (int cy = 0, j = 0; cy < ly; cy++) {
                    float fy2 = fy[cy] * 2.0f;
                    for (int cx = cy > 0 ? 0 : 1; cx * ly < lx * (ly - cy); cx++, j++)
                        l += l_ac[j] * fx[cx] * fy2;
                }
                for (int cy = 0, j = 0; cy < 3; cy++) {
                    float fy2 = fy[cy] * 2.0f;
                    for (int cx = cy > 0 ? 0 : 1; cx < 3 - cy; cx++, j++) {
                        float f = fx[cx] * fy2;
                        p += p_ac[j] * f;
                        q += q_ac[j] * f;
                    }
                }
                if (hasAlpha)
                    for (int cy = 0, j = 0; cy < 5; cy++) {
                        float fy2 = fy[cy] * 2.0f;
                        for (int cx = cy > 0 ? 0 : 1; cx < 5 - cy; cx++, j++)
                            a += a_ac[j] * fx[cx] * fy2;
                    }
                float b = l - 2.0f / 3.0f * p;
                float r = (3.0f * l - b + q) / 2.0f;
                float g = r - q;
                rgba[i] = (byte) Math.max(0, Math.round(255.0f * Math.min(1, r)));
                rgba[i + 1] = (byte) Math.max(0, Math.round(255.0f * Math.min(1, g)));
                rgba[i + 2] = (byte) Math.max(0, Math.round(255.0f * Math.min(1, b)));
                rgba[i + 3] = (byte) Math.max(0, Math.round(255.0f * Math.min(1, a)));
            }
        }
        return new Image(w, h, rgba);
    }
}
