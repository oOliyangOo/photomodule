package com.sina.auto.components.WebCapture.api.google;

import com.jc.jcbd.ts.TrustStore;
import com.mysql.jdbc.StringUtils;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static com.sina.auto.components.ImageTypeTransfer.ImageTransferUtil.Base64ToImage;
import static com.sina.auto.components.ImageTypeTransfer.ImageTransferUtil.ImageToBase64;

@Controller
@RequestMapping(value = "/file")
public class ImageSubmitController{
	/***
	 * 案源提交调用方法：
	 * 		1.获取临时案源附IPFS。
	 * 		2.将临时案源附件转移到正式案源IPFS，七牛
	 * 		3.更新临时案源表 status = 1  表示已经同步次案源
	 * @param url 案源url
	 * @param app_id 接入系统id
	 * @return 系统文件名，文件
	 */
	@ResponseBody
	@RequestMapping(value = "/sub", method = RequestMethod.GET)
	public void submit(@RequestParam(required = true) String url,@RequestParam(required = true) String app_id){
		String sql="SELECT * FROM ac_deposit_hash WHERE status = '0' and app_id='"+app_id+"' and url='"+url+"'  ORDER BY id ";
		List list=SqlBean.selSales(sql);
		if(list !=null && list.size()>0){
			ArrayList<String> ids = new ArrayList<>();
			ArrayList<String> transactionIdList = new ArrayList<>();
			for(int i=0;i<list.size();i++){
				Map<String,Object> map =(Map<String, Object>) list.get(i);
				String temporary_id =map.get("id")+"";
				String fileName = map.get("file_name")+"";
				ArrayList insertParas = new ArrayList();
				insertParas.add(app_id);
				insertParas.add(url);
				insertParas.add(fileName);
				insertParas.add(temporary_id);
				insertParas.add(new Date().getTime());
				insertParas.add("0");
				String insertSql ="insert into ac_deposit_hash_confirm(app_id,url,file_name,temporary_id,create_date,status) values(?,?,?,?,?,?)";
				SqlBean.updateSales(insertSql,insertParas);
				String updateSql ="UPDATE ac_deposit_hash SET status = '1' where id ='"+temporary_id+"'";
				SqlBean.updateSales(updateSql);
			}
		}
	}

	@ResponseBody
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public Map get(@RequestParam(required = true) String url,@RequestParam(required = true) String app_id){
		Map result = new HashMap();
		result.put("status","0");
		String sql="SELECT * FROM ac_deposit_hash_confirm WHERE status = '1' and app_id='"+app_id+"' and url='"+url+"'  ORDER BY id ";
		List list=SqlBean.selSales(sql);
		if(list !=null && list.size()>0){
			result.put("status","1");
			List<Map<String,Object>> dataList = new ArrayList<>();
			for(int i=0;i<list.size();i++){
				Map<String,Object> dataMap = new HashMap<>();
				Map<String,Object> map =(Map<String, Object>) list.get(i);
				String block_info =String.valueOf(map.get("block_info"));
				JSONObject jsonObj = JSONObject.fromObject(block_info);
				String transactionId = jsonObj.get("transaction_id")+"";
				//String transactionId =map.get("transaction_id")+"";
				String fileName =map.get("file_name")+"";
				byte[] data =TrustStore.trustDownload(transactionId);
				dataMap.put("fileName",fileName);
				dataMap.put("data",data);
				dataList.add(dataMap);
			}
			result.put("dataList",dataList);
		}
		return result;
	}
	
	/*
	 * 对外接口，用于返显图片
	 * 
	 * 
	 * */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/getTransactionId", method = RequestMethod.GET)
	public Object getTransactionId(@RequestParam(required = true) String url, @RequestParam(required = true) String app_id, @RequestParam(required = false) String callback  ){
		Map result = new HashMap();
		result.put("status","0");
		String sql="SELECT * FROM ac_deposit_hash_confirm WHERE status = '2' and app_id='"+app_id+"' and url='"+url+"'  ORDER BY id ";
		List list=SqlBean.selSales(sql);
		if(list !=null && list.size()>0){
			result.put("status","1");
			List<String> dataList = new ArrayList<>();
			for(int i=0;i<list.size();i++){
				Map<String,Object> map =(Map<String, Object>) list.get(i);
				String block_info =String.valueOf(map.get("block_info"));
				//String transactionId =map.get("transaction_id")+"";
				String ipfsUrl = TrustStore.getIPFSUrl(block_info);
				dataList.add(ipfsUrl);
			}
			result.put("dataList",dataList);
		}

		return callback +"("+ JSONObject.fromObject(result)+")";//JSONObject.toJSON(result)
	}

	/***
	 * 测试上传方法临时IPFS
	 */
	@ResponseBody
	@RequestMapping(value = "/upload",method = RequestMethod.GET)
	public void upload(){
		try {
			FileInputStream fileInputStream = new FileInputStream("E:\\4.jpg");
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
			String ipfshash= TrustStore.trustUpload(timestamp+".png", buffer);
			System.out.println(ipfshash);
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	@ResponseBody
	@RequestMapping(value = "/uploadFile",method = RequestMethod.GET)
	public void uploadFile(){
		try {
			FileInputStream fileInputStream = new FileInputStream("E:\\4.rar");

			byte[] buffer = new byte[fileInputStream.available()];
			fileInputStream.read(buffer);
			fileInputStream.close();
			String timestamp= SqlBean.timestamp();
			String ipfshash= TrustStore.trustUploadToIPFS_pre(timestamp+".rar", buffer);
			System.out.println(ipfshash);
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	/***
	 * 测试下载临时IPFS
	 */
	@ResponseBody
	@RequestMapping(value = "/download",method = RequestMethod.GET)
	public void download(){
		try {

			//byte[] data =TrustStore.trustDownloadFromIPFS_pre("QmTDst8CRnKwXceqv8uytc2gJ2q1Ck9MtLyYwZfErD7noz");
			byte[] data =TrustStore.trustDownload("bea72960aafc214380f183a5d9e36c8a876a8bf9ef45fd15555c4ccb3e0b8151");
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			//二进制输入流转换为缓冲图片
			BufferedImage bi=ImageIO.read(bais);
			ImageIO.write(bi,"jpg",new File("E:\\download.jpg"));

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/***
	 * 测试下载临时IPFS
	 */
	@ResponseBody
	@RequestMapping(value = "/downloadFile",method = RequestMethod.GET)
	public void downloadFile(){
		try {

			byte[] data =TrustStore.trustDownloadFromIPFS_pre("QmZiFoarqn7ug7PAxjA7JdduZ7vQA51ZkDMvx8h8SkCJtf");
			//byte[] data =TrustStore.trustDownload("bea72960aafc214380f183a5d9e36c8a876a8bf9ef45fd15555c4ccb3e0b8151");
			File file = new File("E:\\download.rar");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();

		}catch (Exception e){
			e.printStackTrace();
		}
	}
}