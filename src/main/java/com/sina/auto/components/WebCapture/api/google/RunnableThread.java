package com.sina.auto.components.WebCapture.api.google;

import java.net.InetAddress;
import java.util.ArrayList;

public class RunnableThread implements Runnable {
	@SuppressWarnings({ "static-access", "unchecked", "rawtypes", "unused" })
	@Override
	public void run() {
        InetAddress ia=null;
        int id=0;
        try {
        	id=ImageOutputController.createPicId();
        	if(id!=0) {
                ia=ia.getLocalHost();
                String localname=ia.getHostName();
                String localip=ia.getHostAddress();
    			String sql="update ac_deposit_url set ip_name=?,thread_name=?,del_flag=? where id=?";
    			ArrayList list=new ArrayList();
    			list.add(localip);
    			list.add(String.valueOf(Thread.currentThread().getName()));
    			list.add(1);
    			list.add(id);
    			int n=SqlBean.updateSales(sql, list);
    			if(n>0) {
    				System.out.println("通过线程池方式线程开启"+Thread.currentThread().getName() + " ");
    			}
                ImageOutputController.createPic(id);
        	}
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	ScheduledService.jsq=0;
			int rolln=SqlBean.updateSales("UPDATE ac_deposit_url SET update_date=NULL,ip_name=NULL,thread_name=NULL,del_flag=0 where id="+id+"");
			if(rolln>=1) {
				System.out.println("回滚成功"+id);
			}else {
				System.out.println("回滚失败"+id);
			}
            e.printStackTrace();
        }
		 
	}
}
