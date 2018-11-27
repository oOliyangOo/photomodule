package com.sina.auto.components.TemplateAddContent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

public class ImageOutput {
    protected static BufferedImage imageoutput(String Content) throws Exception {

        //设定字体大小和行距
        int fontSize = 58;
        int RowSpacing = 16;
        Font font = new Font("黑体", Font.BOLD, fontSize);

        //测量文本字体
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        int FontHeight = fm.getHeight();
        int FontWidth = fm.stringWidth(Content);

        BufferedImage img1 = ImageIO.read(new FileInputStream("template/template1.jpg"));
        BufferedImage img2 = ImageEdit.IMGstretch("template/template3.jpg", 1080, (FontWidth / ((1080 - 220)) + 2) * (FontHeight + RowSpacing) + 30);
        BufferedImage img3 = ImageIO.read(new FileInputStream("template/template2.jpg"));
        BufferedImage[] images = {img1,img2 ,img3};

        return AddString.AddString(RowSpacing, fontSize, ImageEdit.mergeImage(images, 2), Content);

    }

    protected static BufferedImage uploadimageoutput(String Content, BufferedImage[] imgs) throws Exception {
        //设定字体大小和行距
        int fontSize = (int) (imgs[0].getWidth()/18.62);
        int RowSpacing = (int) (imgs[0].getWidth()/67.5);
        Font font = new Font("黑体", Font.BOLD, fontSize);

        //测量文本字体
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        int FontHeight = fm.getHeight();
        int FontWidth = fm.stringWidth(Content);

        BufferedImage stretchimg = imgs[1];
        BufferedImage img2 = ImageEdit.uploadIMGstretch(stretchimg, stretchimg.getWidth(), (FontWidth / ((int) (stretchimg.getWidth() - stretchimg.getWidth()/5.2)) + 2) * (FontHeight + RowSpacing) + 30);
        BufferedImage[] images = {imgs[0],img2 ,imgs[2]};

        System.out.println("完成");
        AddString.AddString(RowSpacing, fontSize, imgs[0],img2, Content);
        return ImageEdit.mergeImage(images,2);
    }

    protected static BufferedImage uploadimageoutput(String Content, String file1, String file2, String file3) throws Exception {
        BufferedImage[] imgs = {ImageIO.read(new File(file1)),ImageIO.read(new File(file2)),ImageIO.read(new File(file3))};
        //设定字体大小和行距
        int fontSize = (int) (imgs[0].getWidth()/18.62);
        int RowSpacing = (int) (imgs[0].getWidth()/67.5);
        Font font = new Font("黑体", Font.BOLD, fontSize);

        //测量文本字体
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        int FontHeight = fm.getHeight();
        int FontWidth = fm.stringWidth(Content);

        BufferedImage stretchimg = imgs[1];
        BufferedImage img2 = ImageEdit.uploadIMGstretch(stretchimg, stretchimg.getWidth(), (FontWidth / ((int) (stretchimg.getWidth() - stretchimg.getWidth()/5.2)) + 2) * (FontHeight + RowSpacing) + 30);
        BufferedImage[] images = {imgs[0],img2 ,imgs[2]};

        System.out.println("完成");
        AddString.AddString(RowSpacing, fontSize, imgs[0],img2, Content);
        return ImageEdit.mergeImage(images,2);
    }


}
