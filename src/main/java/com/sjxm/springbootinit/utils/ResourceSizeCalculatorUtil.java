package com.sjxm.springbootinit.utils;

/**
 * @Author: 四季夏目
 * @Date: 2024/12/1
 * @Description:
 */
import java.net.HttpURLConnection;
import java.net.URL;

public class ResourceSizeCalculatorUtil {
    public static long calculateTotalSize(String resources) {
        if (resources == null || resources.isEmpty()) {
            return 0L;
        }

        // 分割资源字符串
        String[] urls = resources.split(",");
        long totalSize = 0;

        for (String urlStr : urls) {
            try {
                URL url = new URL(urlStr.trim());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("HEAD"); // 只获取头信息，不下载文件

                // 获取文件大小
                long size = conn.getContentLengthLong();
                if (size > 0) {
                    totalSize += size;
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                // 处理错误，可以选择继续处理其他URL或者抛出异常
            }
        }

        return totalSize;
    }

    // 可选：格式化文件大小显示
    public static String formatFileSize(long size) {
        if (size <= 0) return "0 B";

        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return String.format("%.2f %s",
                size / Math.pow(1024, digitGroups),
                units[digitGroups]);
    }
}