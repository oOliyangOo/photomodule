package com.sina.auto.components.TemplateAddContent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.awt.Color.black;

public class AddString{
    static BufferedImage AddString(int RowSpacing, int FontSize, BufferedImage img, String content) throws Exception {
        BufferedImage bi = ImageIO.read(new File("template/template1.jpg"));
        Font font = new Font("黑体", Font.BOLD, FontSize);
        int width = img.getWidth(null) == -1 ? 200 : img.getWidth(null);
        System.out.println(width);
        int height = img.getHeight(null) == -1 ? 200 : img.getHeight(null);

        //获得画笔
        Graphics2D g2 = img.createGraphics();
        Graphics2D DrawTime = img.createGraphics();

        DrawTime.setFont(new Font("黑体", Font.BOLD, width / 27));
        DrawTime.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        DrawTime.setColor(black);
        DrawTime.drawImage(img, 0, 0, null);
        //打印时间
        DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        String s = df.format(new Date());
        int strWidth = DrawTime.getFontMetrics().stringWidth(s);
        DrawTime.drawString(s, (width - strWidth) / 2, bi.getHeight() + 70);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(black);
        g2.setBackground(Color.red);
        g2.drawImage(img, 0, 0, null);
        g2.setFont(font);

        FontMetrics metrics = g2.getFontMetrics();
        int contentPixelWidth = metrics.stringWidth(content);
        int contentPixelHeight = metrics.getHeight();
        System.out.println(contentPixelWidth);
        int realWidth = (int) (width - (width / 5.2));
        int lineNum = (int) Math.ceil(contentPixelWidth * 1.0 / realWidth);
        //System.out.println(contentPixelWidth);
        //System.out.println(content.length());
        StringBuilder sb = new StringBuilder();
        int j = 0;
        int tempStart = 0;
        String tempStrings[] = new String[lineNum];//存储换行之后每一行的字符串
        String ExtendTemp[] = new String[lineNum + 1];
        System.out.println(lineNum);
        DrawString(content, metrics, realWidth, sb, j, tempStart, tempStrings, ExtendTemp);
        if (ExtendTemp[0] == null) {
            for (int count = 0; count < tempStrings.length; count++) {
                g2.drawString(tempStrings[count], (int) (width / 9.8), 500 + (contentPixelHeight + RowSpacing) * count);
            }
        } else {
            for (int count = 0; count < ExtendTemp.length; count++) {
                g2.drawString(ExtendTemp[count], (int) (width / 9.8), 500 + (contentPixelHeight + RowSpacing) * count);
            }
        }
        g2.dispose();
        return img;
    }

    static BufferedImage AddString(int RowSpacing, int FontSize, BufferedImage parameter, BufferedImage img, String content) {
        Font font = new Font("黑体", Font.BOLD, FontSize);
        int width = img.getWidth(null) == -1 ? 200 : img.getWidth(null);
        System.out.println(width);
        int height = img.getHeight(null) == -1 ? 200 : img.getHeight(null);

        //获得画笔
        Graphics2D g2 = img.createGraphics();
        Graphics2D DrawTime = img.createGraphics();

        DrawTime.setFont(new Font("黑体", Font.BOLD, width / 27));
        DrawTime.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        DrawTime.setColor(black);
        DrawTime.drawImage(img, 0, 0, null);
        //打印时间
        DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        String s = df.format(new Date());
        int strWidth = DrawTime.getFontMetrics().stringWidth(s);
        DrawTime.drawString(s, (width - strWidth) / 2, 70);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(black);
        g2.setBackground(Color.red);
        g2.drawImage(img, 0, 0, null);
        g2.setFont(font);

        FontMetrics metrics = g2.getFontMetrics();
        int contentPixelWidth = metrics.stringWidth(content);
        int contentPixelHeight = metrics.getHeight();
        System.out.println(contentPixelWidth);
        int realWidth = (int) (width - (width / 5.2));
        int lineNum = (int) Math.ceil(contentPixelWidth * 1.0 / realWidth);
        //System.out.println(contentPixelWidth);
        //System.out.println(content.length());
        StringBuilder sb = new StringBuilder();
        int j = 0;
        int tempStart = 0;
        String tempStrings[] = new String[lineNum];//存储换行之后每一行的字符串
        String ExtendTemp[] = new String[lineNum + 1];
        System.out.println(lineNum);
        DrawString(content, metrics, realWidth, sb, j, tempStart, tempStrings, ExtendTemp);
        if (ExtendTemp[0] == null) {
            for (int count = 0; count < tempStrings.length; count++) {
                g2.drawString(tempStrings[count], (int) (width / 9.8), RowSpacing * 9 + (contentPixelHeight + RowSpacing) * count);
            }
        } else {
            for (int count = 0; count < ExtendTemp.length; count++) {
                g2.drawString(ExtendTemp[count], (int) (width / 9.8), RowSpacing * 9 + (contentPixelHeight + RowSpacing) * count);
            }
        }

        g2.dispose();
        return img;
    }

    /**
     *
     * @param content 获取的文字
     * @param metrics 文字的字体
     * @param realWidth 文字字符串的总像素长度
     * @param sb 拼接文字
     * @param j 循环绘制文字
     * @param tempStart 字符串换行开始的位置
     * @param tempStrings
     * @param extendTemp
     */
    public static void DrawString(String content, FontMetrics metrics, int realWidth, StringBuilder sb, int j, int tempStart, String[] tempStrings, String[] extendTemp) {
        for (int i1 = 0; i1 < content.length(); i1++) {
            char ch = content.charAt(i1);
            sb.append(ch);
            Rectangle2D bounds2 = metrics.getStringBounds(sb.toString(), null);
            int tempStrPi1exlWi1dth = (int) bounds2.getWidth();
            if (tempStrPi1exlWi1dth > realWidth) {
                tempStrings[j++] = content.substring(tempStart, i1);
                tempStart = i1;
                sb.delete(0, sb.length());
                sb.append(ch);
            }
            if (i1 == content.length() - 1) {
                //最后一行
                if (j == tempStrings.length) {
                    for (int k = 0; k < tempStrings.length; k++) {
                        extendTemp[k] = tempStrings[k];
                    }
                    extendTemp[j] = content.substring(tempStart);
                } else {
                    tempStrings[j] = content.substring(tempStart);
                    break;
                }
            }
        }
    }
}
