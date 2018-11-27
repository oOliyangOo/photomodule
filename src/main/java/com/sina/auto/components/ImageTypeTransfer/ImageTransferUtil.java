package com.sina.auto.components.ImageTypeTransfer;

import com.jc.jcbd.ts.TrustStore;
import com.sina.auto.components.WebCapture.api.google.SqlBean;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageTransferUtil {

    public static String ImageToBase64(BufferedImage bufferedImage) throws IOException {
        //创建二进制输出流和缓冲区图片
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", bao);
        //二进制输出流转为二进制数组
        byte[] img = bao.toByteArray();
        //二进制数组转为base64字符串
        return DatatypeConverter.printBase64Binary(img);
    }

    public static BufferedImage Base64ToImage(String b64) throws IOException {
        //创建base64解码器
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes1 = decoder.decodeBuffer(b64);
        //创建二进制输入流
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
        //二进制输入流转换为缓冲图片
        return ImageIO.read(bais);
    }

    public static void main(String args[]) throws IOException {
        FileInputStream fileInputStream = new FileInputStream("E:\\baidu2.png");
        BufferedImage bufferedImage = ImageIO.read(fileInputStream);
        String base = ImageToBase64(bufferedImage);
        BufferedImage bi = Base64ToImage(base);
        //ImageIO.write(bi,"png",new File("C:/Users/Jing/Desktop/heihei.png"));
        ByteArrayOutputStream out=new ByteArrayOutputStream(1000);
        ImageIO.write(bi,"png",out);
        byte[] buffer = null;
        buffer=out.toByteArray();
        out.close();
        String timestamp= SqlBean.timestamp();
        String ipfshash= TrustStore.trustUploadToIPFS_pre(timestamp+".png", buffer);
        System.out.println(ipfshash);
    }
}
