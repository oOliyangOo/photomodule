package com.sina.auto.components.WebCapture;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CreateDock {
    //创建一张图片用于充当二维码的背景图，这个背景图在制作完成后会被接到网页截图的最下方
    //输入需要添加的文字（例如扫描二维码），宽度，高度和一个代表rgb值的整数数组。
    //rgb整数数组用来决定这个背景图的颜色。
    public static BufferedImage createDock(String s, int imageWidth , int imageHeight, int[] rgb) throws IOException {
        //配置字体
        Font font = new Font("微软雅黑", Font.BOLD, imageWidth/50);//25
        //创建一个指定宽高的缓冲图片
        BufferedImage bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        //获得画笔
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        //配置属性
        g2.setBackground(new Color(rgb[0],rgb[1],rgb[2]));
        g2.clearRect(0, 0, imageWidth, imageHeight);
        g2.setPaint(Color.BLACK);
        g2.setFont(font);
        g2.setColor(new Color(255-rgb[0], 255-rgb[1], 255-rgb[2]));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        //设定字符串在图中的位置
        FontRenderContext context = g2.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(s, context);
        double x = ((imageWidth*0.512 - 130) - bounds.getWidth()) / 2;
        double y = (imageHeight - bounds.getHeight()) / 2;
        double ascent = -bounds.getY();
        double baseY = y + ascent;
        //写字
        g2.drawString(s, (int) x, (int) baseY);
        return bi;
    }

}
