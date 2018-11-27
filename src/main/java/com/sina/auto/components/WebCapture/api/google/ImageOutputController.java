package com.sina.auto.components.WebCapture.api.google;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.openqa.selenium.os.WindowsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.sina.auto.components.WebCapture.CaptureEdit;
import com.sina.auto.components.WebCapture.CapturePC;
import com.sina.auto.components.WebCapture.CreateDock;
import com.sina.auto.components.WebCapture.CreateQRcode;

import io.swagger.annotations.ApiParam;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jc.jcbd.ts.TrustStore;

@Controller
@RequestMapping(value = "/imageOutputApi")
public class ImageOutputController implements ImageOutputApi{
	private final static String ENCODE = "GBK"; 
    @SuppressWarnings({ "unchecked", "rawtypes" })
	/**
	 * 对外开放接口
	 * @param url,appid
	 * @return
	 */
	@Override
    @ResponseBody
    @RequestMapping(value = "/getPicture", method = RequestMethod.GET)
	public void getPicture(@ApiParam(value = "url", required = true) @RequestParam(required = true) String url,@ApiParam(value = "appId", required = true) @RequestParam(required = true) String appid) {
    	//appid=spider
		// TODO Auto-generated method stub
        if (null == url) {
            System.out.println();
        }
        try {
        	url = java.net.URLDecoder.decode(url, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        long unixTimestamp = SqlBean.unixTimestamp();
		ArrayList list=new ArrayList();
		list.add(url);
		list.add(String.valueOf(unixTimestamp));
		list.add(0);
		list.add(appid);
		int n=SqlBean.updateSales("insert into ac_deposit_url(url,create_date,del_flag,appid) values(?,?,?,?)", list);
		if(n>0) {
			System.out.println("加入队列");
		}
	}
    @SuppressWarnings("rawtypes")
	/**
	 * 提取id值
	 * @param
	 * @return id
	 */
	public static int createPicId() throws SQLException{
		String sql="SELECT * FROM ac_deposit_url WHERE del_flag!=1 ORDER BY id LIMIT 0,1";
		List list=SqlBean.selSales(sql);
		int id=0;
		int del_flag=-1;
		int len=list.size();
		int i=0;
		for(;i<len;i++) {
			Map map=(Map) list.get(i);
			id=(int) map.get("id");
			del_flag=(int)map.get("del_flag");
			
			id=ImageOutputController.susess_id(id,del_flag,map);
		}
		System.out.println(id);
		return id;
			
    }
    @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	/**
	 * 生成图片流文件
	 * @param id
	 * @return id
	 */
    public static int createPic(int id) throws SQLException {//byte[]
		String sql="SELECT * FROM ac_deposit_url WHERE id='"+id+"' ORDER BY id LIMIT 0,1";
		List list=SqlBean.selSales(sql);
		int len=list.size();
		int i=0;
		for(;i<len;i++) {
			Map map1=(Map) list.get(i);
			try {
				String url=String.valueOf(map1.get("url"));
				String appId=String.valueOf(map1.get("appid"));
				Map<String, Object> map = CapturePC.CaptureFullPage(url,"2", false);
				if(map==null) {
					return 0;
				}
				if(map.get("bi")!=null) {
					BufferedImage web=(BufferedImage) map.get("bi");
					BufferedImage bi = CaptureEdit.mergeImage(web, CaptureEdit.overlapImage(CreateDock.createDock("扫描二维码",web.getWidth(),150, CaptureEdit.checkRGB(web)), CreateQRcode.QRcode(url)));//400
					ByteArrayOutputStream out=new ByteArrayOutputStream(1000);
					ImageIO.write(bi,"png",out);
					byte[] buffer = null;  
					buffer=out.toByteArray();
					out.close();
					long unixTimestamp = SqlBean.unixTimestamp();
					ArrayList lists=new ArrayList();
					lists.add(String.valueOf(unixTimestamp));
					lists.add(id);
					int n=SqlBean.updateSales("update ac_deposit_url set del_flag=1,update_date=? where id=?", lists);
					if(n>0) {
						System.out.println("处理成功");
					}
					//return buffer;
					try {
						//存入ipfs
						String timestamp=SqlBean.timestamp();
						StringBuffer file_name= new StringBuffer();
						file_name.append(appId);
						file_name.append("_");
						file_name.append(timestamp);
						file_name.append(".png");
						String ipfshash=TrustStore.trustUploadToIPFS_pre(String.valueOf(file_name), buffer);
						Map map2=(Map) ImageOutputController.json2Java(ipfshash, Map.class);
						ipfshash=String.valueOf(map2.get("Hash"));
						int Pren=ImageOutputController.Preservation(ipfshash,id,appId,url,String.valueOf(file_name));
						if(n!=0) {
							System.out.println("固化成功");
						}else {
							System.out.println("固化失败");
						}
					} catch (Exception e) {
						// TODO: handle exception
						ScheduledService.jsq=0;
						int rolln=SqlBean.updateSales("UPDATE ac_deposit_url SET update_date=NULL,ip_name=NULL,thread_name=NULL,del_flag='0' where id="+id+"");
						if(rolln>=1) {
							System.out.println("回滚成功"+id);
						}else {
							System.out.println("回滚失败"+id);
						}
						e.printStackTrace();
					}

					return id;
				}else if(map.get("image")!=null){
					byte[] b=(byte[]) map.get("image");
					long unixTimestamp = SqlBean.unixTimestamp();
					ArrayList lists=new ArrayList();
					lists.add(String.valueOf(unixTimestamp));
					lists.add(id);
					int n=SqlBean.updateSales("update ac_deposit_url set del_flag=1,update_date=? where id=?", lists);
					if(n>0) {
						System.out.println("处理成功");
					}
					try {
						//存入ipfs
						String timestamp=SqlBean.timestamp();
						String ipfshash=TrustStore.trustUploadToIPFS_pre(""+appId+timestamp+".pdf", b);
						Map map2=(Map) ImageOutputController.json2Java(ipfshash, Map.class);
						ipfshash=String.valueOf(map2.get("Hash"));
						int Pren=ImageOutputController.Preservation(ipfshash,id,appId,url,""+appId+timestamp+".png");
						if(n!=0) {
							System.out.println("固化成功");
						}else {
							System.out.println("固化失败");
						}
					} catch (Exception e) {
						// TODO: handle exception
						ScheduledService.jsq=0;
						int rolln=SqlBean.updateSales("UPDATE ac_deposit_url SET update_date=NULL,ip_name=NULL,thread_name=NULL,del_flag=0 where id="+id+"");
						if(rolln>=1) {
							System.out.println("回滚成功"+id);
						}else {
							System.out.println("回滚失败"+id);
						}
						e.printStackTrace();
					}
					return id;
				}else {
					int rolln=SqlBean.updateSales("UPDATE ac_deposit_url SET update_date=NULL,ip_name=NULL,thread_name=NULL,del_flag=0 where id="+id+"");
					if(rolln>=1) {
						System.out.println("回滚成功"+id);
					}else {
						System.out.println("回滚失败"+id);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				ScheduledService.jsq=0;
				String reset_data=String.valueOf(Calendar.getInstance().getTimeInMillis());
				int rolln=SqlBean.updateSales("UPDATE ac_deposit_url SET update_date=NULL,ip_name=NULL,thread_name=NULL,del_flag=2,reset_date='"+reset_data+"' where id="+id+"");
				if(rolln>=1) {
					System.out.println("回滚成功"+id);
				}else {
					System.out.println("回滚失败"+id);
				}
				e.printStackTrace();
				return 0;
			}
		}
		return 0;
    }
    @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	/**
	 * 存入ipfs
	 * @param ipfshash,id,appId,url,fileName
	 * @return n
	 */
	private static int Preservation(String ipfshash,int id,String appId,String url,String fileName) {
    	if(ipfshash!=null && id!=0 && appId!=null && url!=null && fileName!=null) {
			long unixTimestamp = SqlBean.unixTimestamp();
			ArrayList lists=new ArrayList();
			lists.add(String.valueOf(unixTimestamp));
			lists.add(appId);
			lists.add(ipfshash);
			lists.add(id);
			lists.add(url);
			lists.add(fileName);
			lists.add("0");
			int n=0;
			try {
				n=SqlBean.updateSales("insert into ac_deposit_hash(create_date,app_id,ipfs_hash,queue_id,url,file_name,status) values(?,?,?,?,?,?,?)", lists);
			} catch (Exception e) {
				// TODO: handle exception
				ScheduledService.jsq=0;
				int rolln=SqlBean.updateSales("UPDATE ac_deposit_url SET update_date=NULL,ip_name=NULL,thread_name=NULL,del_flag=0 where id="+id+"");
				if(rolln>=1) {
					System.out.println("回滚成功"+id);
				}else {
					System.out.println("回滚失败"+id);
				}
				return 0;
			}
			if(n>0) {
				System.out.println("处理成功");
			}
			return n;
    	}
    	return 0;
    }
    public static <T> Object json2Java(String json, Class<T> type) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        Object jacksonToBean = null;
        if(json != null && !"".equals(json)) {
            try {
                jacksonToBean = mapper.readValue(json, type);
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        return jacksonToBean;
    }
	/**
	 * 判断当前id是否可用
	 * @param
	 * @return id
	 */
    public static int susess_id(int id,int del_flag,Map map){
		if(del_flag==2) {
			String reset_date=String.valueOf(map.get("reset_date"));
			String date=String.valueOf(Calendar.getInstance().getTimeInMillis());
			if(reset_date!=null && !reset_date.equalsIgnoreCase("null") && !reset_date.equalsIgnoreCase("NULL") &&Long.valueOf(date)-Long.valueOf(String.valueOf(reset_date))<=1800000) {
				String sql2="SELECT * FROM ac_deposit_url WHERE del_flag!=1 and del_flag!=2 ORDER BY id LIMIT 0,1";
				List list2=SqlBean.selSales(sql2);
				for(int i=0;i<list2.size();i++) {
					Map map2=(Map) list2.get(i);
					id=(int) map2.get("id");
				}
				return id;
			}else {
				return id;
			}
		}else if(del_flag==1 || del_flag==0) {
			return id;
		}else{
			return 0;
		}
	}
}