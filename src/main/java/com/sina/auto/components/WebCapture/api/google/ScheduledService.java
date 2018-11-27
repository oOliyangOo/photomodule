package com.sina.auto.components.WebCapture.api.google;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.jc.jcbd.ts.TrustStore;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.os.WindowsUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
@Component
public class ScheduledService {
	private static int POOL_NUM = 0;
	public static int jsq = 0;
	
	/**
     * @param args
     * @throws InterruptedException 
     */  
	 @Scheduled(fixedRate=80000L)
	 public void scheduled()
	  {
		System.out.println("jsq==============================="+jsq);
		//if(jsq==0) {
			WindowsUtils.killByName("chrome.exe");
			WindowsUtils.killByName("chromedriver.exe");
		//}
	    String sql = "select count(id)as k from ac_deposit_url where ip_name is null and thread_name is null and del_flag=0";
	    List list = SqlBean.selSales(sql);
	    for (int i = 0; i < list.size(); i++)
	    {
	      Map map = (Map)list.get(i);
	      int k = Integer.parseInt(String.valueOf(map.get("k")));
	      if (k >= 5) {
	        POOL_NUM = 5;
	      } else {
	        POOL_NUM = k;
	      }
	    }
	    if (jsq == 0)
	    {
	      ExecutorService executorService = Executors.newFixedThreadPool(5);
	      //jsq = 1;
	      if (POOL_NUM > 0)
	      {
	        try
	        {
	          for (int i = 0; i < POOL_NUM; i++)
	          {
	            RunnableThread thread = new RunnableThread();
	            executorService.execute(thread);
	            Thread.currentThread();
	            Thread.sleep(2000L);
	          }
	        }
	        catch (InterruptedException e)
	        {
	        	jsq=0;
	        	Thread.currentThread().interrupt();
	        	e.printStackTrace();
	        }
	        executorService.shutdown();
	        System.out.println("========================启动，开始处理=======================");
	        try
	        {
	          if (executorService.awaitTermination(80000L, TimeUnit.MILLISECONDS)) {
	            jsq = 0;
	          } else {
	        	  jsq = 0;
	        	  WindowsUtils.killByName("chrome.exe");
	          }
			}
	        catch (InterruptedException e)
	        {
	        	jsq = 0;
	        	WindowsUtils.killByName("chrome.exe");
	        	e.printStackTrace();
	        }
	      }
	      else
	      {
	        String sql_reset = "update ac_deposit_url set del_flag=0,ip_name=null,thread_name=null where del_flag=2";
	        SqlBean.updateSales(sql_reset);
	        String sql_reset1 = "UPDATE ac_deposit_url SET del_flag=0,ip_name=NULL,thread_name=NULL WHERE del_flag=1 AND update_date IS NULL";
	        SqlBean.updateSales(sql_reset1);
	        jsq = 0;
	        System.out.println("========================启动定时任务=======================");
	      }
	    }
	  }

	/***
	 * 处理 ac_deposit_hash_confirm 获取 transaction_id 定时任务
	 */
	@SuppressWarnings("unchecked")
	@Scheduled(fixedRate = 1000*60*20)
	public void saveFile(){
		System.out.println("执行saveFile方法======================================");
		String sql="select ac.id as id ,ac.app_id as app_id, ac.url as url,ac.temporary_id as temporary_id, ah.ipfs_hash as ipfs_hash, ah.file_name as file_name" +
				" from ac_deposit_hash_confirm ac" +
				"       left join ac_deposit_hash ah on ac.temporary_id = ah.id" +
				" where ac.status = '0'" +
				" order by ac.id" +
				" limit 1";
		List list=SqlBean.selSales(sql);
		if(list !=null && list.size()>0){
			System.out.println("saveFile方法list集合");
			Map<String,Object> map =(Map<String, Object>) list.get(0);
			String id =map.get("id")+"";
			String temporary_id =map.get("temporary_id")+"";
			String ipfsHash = map.get("ipfs_hash")+"";
			String fileName = map.get("file_name")+"";
			byte[] data = TrustStore.trustDownloadFromIPFS_pre(ipfsHash);
			System.out.println("data======"+data==null?"data为null":data.length);
			//将临时IPFS存储到正式IPFS 七牛 返回transactionId
			long startTime = System.currentTimeMillis();    //获取开始时间
			String transactionId= TrustStore.trustUpload(fileName, data);
			long endTime = System.currentTimeMillis();    //获取结束时间
			System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
			//封装为json数据
			Map mapIPFS = new HashMap();
			mapIPFS.put("Hash", ipfsHash);
			mapIPFS.put("Links", new String[0]);
			mapIPFS.put("Name", fileName);
			
			Map mapPrd = new HashMap();
			mapPrd.put("QiNiu", "");
			mapPrd.put("IPFS", mapIPFS);
			Map mapJson = new HashMap();
			mapJson.put("transaction_id", transactionId);
			mapJson.put("prd", mapPrd);
			
			//存入正式表
			String updateSql ="update ac_deposit_hash_confirm set block_info = '"+JSONObject.fromObject(mapJson)+"',status = '1' where id = '"+id+"'";
			//String updateSql ="update ac_deposit_hash_confirm set transaction_id = '"+transactionId+"',status = '1' where id = '"+id+"'";
			SqlBean.updateSales(updateSql);
			System.out.println("saveFile方法updateSales1执行");
			//修改临时IPFS表
			updateSql ="UPDATE ac_deposit_hash SET status = '1' where id = '"+temporary_id+"'";
			SqlBean.updateSales(updateSql);
			System.out.println("saveFile方法updateSales2执行");
		}
	}


	/***
	 * 验证区块链是否正常
	 */
	@Scheduled(fixedRate = 1000*60*20)
	public void checkMemo(){
		System.out.println("进入checkMemo");
		String sql="select ac.block_info,ac.status,ac.id from ac_deposit_hash_confirm ac where ac.status='1' and ac.status limit 3";
		List list=SqlBean.selSales(sql);
		if(list !=null && list.size()>0){
			System.out.println("list不为null");
			Map<String,Object> map =(Map<String, Object>) list.get(0);
			String id =map.get("id")+"";
			String block_info =String.valueOf(map.get("block_info"));
			JSONObject jsonObj = JSONObject.fromObject(block_info);
			String transaction_id = jsonObj.get("transaction_id")+"";
			
			try{
				//根据tarnsaction_id 获取memo 如果不是null 或者null 字符串表示成功
				String memo = TrustStore.getMemo(transaction_id);
				if(StringUtils.isNotBlank(memo)){
					String updateSql ="update ac_deposit_hash_confirm set status = '2' where id = '"+id+"'";
					SqlBean.updateSales(updateSql);
					System.out.println("memo          true");
				}else {
					//验证失败！更改状态为3
					String updateSql ="update ac_deposit_hash_confirm set status = '3' where id = '"+id+"'";
					SqlBean.updateSales(updateSql);
					System.out.println("memo          false");
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
