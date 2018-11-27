package com.sina.auto.components.TemplateAddContent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

class ImageEdit {

    /**
     *
     * @param files 获取一个缓冲图片数组
     * @param type 拼接方式，1为水平，2为垂直
     * @return
     */
    static BufferedImage mergeImage(BufferedImage[] files, int type) {
        int len = files.length;
        if (len < 1) {
            throw new RuntimeException("图片数量小于1");
        }
        //File[] src = new File[len];
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            images[i] = files[i];
            int width = images[i].getWidth();
            int height = images[i].getHeight();
            ImageArrays[i] = new int[width * height];
            ImageArrays[i] = images[i].getRGB(0, 0, width, height, ImageArrays[i], 0, width);
        }
        int newHeight = 0;
        int newWidth = 0;
        for (int i = 0; i < images.length; i++) {
            // 横向
            if (type == 1) {
                newHeight = newHeight > images[i].getHeight() ? newHeight : images[i].getHeight();
                newWidth += images[i].getWidth();
            } else if (type == 2) {// 纵向
                newWidth = newWidth > images[i].getWidth() ? newWidth : images[i].getWidth();
                newHeight += images[i].getHeight();
            }
        }
        if (type == 1 && newWidth < 1) {

        }
        if (type == 2 && newHeight < 1) {

        }

        // 生成新图片
        try {
            BufferedImage ImageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            int height_i = 0;
            int width_i = 0;
            for (int i = 0; i < images.length; i++) {
                if (type == 1) {
                    ImageNew.setRGB(width_i, 0, images[i].getWidth(), newHeight, ImageArrays[i], 0,
                            images[i].getWidth());
                    width_i += images[i].getWidth();
                } else if (type == 2) {
                    ImageNew.setRGB(0, height_i, newWidth, images[i].getHeight(), ImageArrays[i], 0, newWidth);
                    height_i += images[i].getHeight();
                }
            }
            //输出想要的图片
            return ImageNew;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 图片拉伸：将模板中间文本区拉伸，与模板上沿和模板下沿拼接。读取本地的一张图片并处理。
     * @param filePath
     * @param x
     * @param y
     * @return
     * @throws Exception
     */
    static BufferedImage IMGstretch(String filePath, int x, int y) throws Exception{
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            BufferedImage img = ImageIO.read(fis);
            BufferedImage newIMG = new BufferedImage(x, y, img.getType());
            Graphics g = newIMG.getGraphics();
            //获取指定尺寸的图片
            g.drawImage(img,0,0,x,y,null);
            g.dispose();
            img = newIMG;
            return img;


        }

        //传入一个缓冲图片对象，进行拉伸处理
        static BufferedImage uploadIMGstretch(BufferedImage bufferedImage, int x, int y)throws Exception{
            BufferedImage img = bufferedImage;
            BufferedImage newIMG = new BufferedImage(x, y, img.getType());
            Graphics g = newIMG.getGraphics();
            //获取指定尺寸的图片
            g.drawImage(img,0,0,x,y,null);
            g.dispose();
            img = newIMG;
            return img;

    }
}
