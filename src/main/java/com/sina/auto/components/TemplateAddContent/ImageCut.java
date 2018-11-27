package com.sina.auto.components.TemplateAddContent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//java图片读取操作：BufferedImage bufferedImage = ImageIO.read(new FileInputStream(filePath));

/**
 * 在处理的过程中发现的另外一个问题是：在一张图像中，（0,0）坐标是在图像的左上角，
 * 往下是height的增方向，往右是weight的增方向，即在图像的右下角的坐标值是（height-1，weight-1）
 * ，这跟传统意义上的坐标轴的方向是不一样的，这点需要特别注意。
 */
public class ImageCut {
    public static BufferedImage[] ImageCut(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis); //把文件读到图片缓冲流中

        int rows = 3; //纵向切块数量
        int cols = 1;//横向切块数量
        int chunks = rows * cols;

        int chunkWidth = image.getWidth() / cols; // 计算每一块小图片的高度和宽度
        int chunkHeight = image.getHeight() / rows;
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //初始化BufferedImage
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

                //画出每一小块图片
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        System.out.println("切分完成");
        System.out.println("小图片创建完成");
        return imgs;
    }
}
