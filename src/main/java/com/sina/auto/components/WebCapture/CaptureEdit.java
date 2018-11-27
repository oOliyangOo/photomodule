package com.sina.auto.components.WebCapture;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CaptureEdit {

    //将一张图片放在另一张图片上
    public static BufferedImage overlapImage(BufferedImage bigPath, BufferedImage smallPath) {
        try {
            BufferedImage big = bigPath;
            BufferedImage small = smallPath;
            Graphics2D g = big.createGraphics();
            g.drawImage(small, big.getWidth()/2 - small.getWidth()/2, big.getHeight()/2 - small.getHeight()/2, small.getWidth(), small.getHeight(), null);
            g.dispose();
            return big;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将两张缓冲图片垂直拼接
     * @param file1
     * @param file2
     * @return
     */
    public static BufferedImage mergeImage(BufferedImage file1, BufferedImage file2) {
            BufferedImage src1 = file1;
            BufferedImage src2 = file2;

            //获取图片的宽度
            int width = src1.getWidth();
            //获取各个图像的高度
            int height1 = src1.getHeight();
            int height2 = src2.getHeight();

            //构造一个类型为预定义图像类型之一的 BufferedImage。 宽度为第一只的宽度，高度为各个图片高度之和
            BufferedImage tag = new BufferedImage(width, height1 + height2, BufferedImage.TYPE_INT_RGB);
            //绘制合成图像
            Graphics g = tag.createGraphics();
            g.drawImage(src1, 0, 0, width, height1, null);
            g.drawImage(src2, 0, height1, width, height2, null);
            // 释放此图形的上下文以及它使用的所有系统资源。
            g.dispose();
            System.out.println("完成");
        return tag;
    }

    /**
     * 裁剪图片
     * @param bufferedImage
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @return
     */
    public static BufferedImage cropImage(BufferedImage bufferedImage, int startX, int startY, int endX, int endY) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        if (startX == -1) {
            startX = 0;
        }
        if (startY == -1) {
            startY = 0;
        }
        if (endX == -1) {
            endX = width - 1;
        }
        if (endY == -1) {
            endY = height - 1;
        }
        BufferedImage result = new BufferedImage(endX - startX, endY - startY, 4);
        for (int x = startX; x < endX; ++x) {
            for (int y = startY; y < endY; ++y) {
                int rgb = bufferedImage.getRGB(x, y);
                result.setRGB(x - startX, y - startY, rgb);
            }
        }
        return result;
    }

    /**
     * 获取一个位置的rgb值
     * @param image
     * @return
     * @throws Exception
     */
    public static int[] checkRGB(BufferedImage image) throws Exception{
        //创建缓冲图片
        BufferedImage bi = image;
        int rgbR;
        int rgbG;
        int rgbB;
        //取图片中一像素点的颜色值
        int pixel = bi.getRGB(0, bi.getHeight()-1);
        // 下面三行代码将一个数字转换为RGB数字
        rgbR = (pixel & 0xff0000) >> 16;
        rgbG = (pixel & 0xff00) >> 8;
        rgbB = (pixel & 0xff);
        int[] rgb = new int[]{rgbR,rgbG,rgbB};
        return rgb;
    }

}
