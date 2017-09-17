package buaa.act;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.NoRemoteRepositoryException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Init extends Thread {
	
	final static int N = 7;

	private Connection con;
	
	int start, num, id_thread, id_server;
	
	Map<Integer, Integer> mp = new HashMap<Integer, Integer>();
	
	public Init(int id_server_t, int id_thred_t, int start_t, int num_t) throws SQLException {
		start = start_t;
		num = num_t;
		id_thread = id_thred_t;
		id_server = id_server_t;
	}
	private void deleteFile(File file) {  
	    if (file.exists()) {//判断文件是否存在  
	     if (file.isFile()) {//判断是否是文件  
	      file.delete();//删除文件   
	     } else if (file.isDirectory()) {//否则如果它是一个目录  
	      File[] files = file.listFiles();//声明目录下所有的文件 files[];  
	      for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件  
	       this.deleteFile(files[i]);//把每个文件用这个方法进行迭代  
	      }  
	      file.delete();//删除文件夹  
	     }  
	    } else {  
	    	System.out.println("~~~没有所删除的文件");  
	    }  
	}  
	
	public int cloneRepository(String url,String localPath,int projectId)  
	{
	  try{
		  System.out.println("开始下载:"+projectId
		  		+ "......"+id_thread);

		  CloneCommand cc = Git.cloneRepository().setURI(url);
		  
		  UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider("ZhaoYi1031", "zy12345678");                
		  cc.setCredentialsProvider(user);
		  
		  cc.setDirectory(new File(localPath))
		  .setTimeout(60)
		  .call();

		  System.out.println("下载完成:"+projectId
		  		+ "......"+id_thread);
		  
		  
		  deleteFile(new File(localPath+"/.git"));
		  
		  return 1;
	  	}catch (InvalidRemoteException e){//这GitHub项目不存在
//	  		e.printStackTrace();
	  		System.out.println("$$$GitHub项目不存在:"+projectId+"......"+id_thread);
	  		File f = new File(localPath);;
	  		deleteFile(f);
	  		return 0;
	  	}catch (TransportException e){//网络问题
	  		System.out.println("###网络崩了:"+projectId+"......"+id_thread);
	  		e.printStackTrace();
	  		File f = new File(localPath);;
	  		deleteFile(f);
	  		int newvalue = 1;
	  		if (mp.containsKey(projectId)){
		    	 newvalue = mp.get(projectId);
		    	 newvalue++;
		    }
	  		if (newvalue > 5)//跑6次仍然是网络错误，代表是那种503（DMCA）问题例如:https://github.com/Battlecruiser/L2J_Server
	  			return 5;
	  		mp.put(projectId,newvalue);
	  		return 3;
	  	}catch(JGitInternalException e){//该存储的目录已经存在
//	  		e.printStackTrace();
	  		System.out.println("&&&已经保存过了"+projectId+"......"+id_thread);
	  		return 2;
	  	}catch (Exception e){
	  		e.printStackTrace();
	  		return 4;
	  	}
	 }
	 
	public void run(){

		try{
			Init_mysql con = new Init_mysql(0);
			Init_mysql conn = new Init_mysql(1);
			
			String cypher;
			cypher = "select *  from java_repositories limit "
					+Integer.toString(start)+","+Integer.toString(num)+";";
			
//			cypher = "select *  from java_repositories where projectId >= 15032988 limit 5";
					
					
					//"select *  from java_repositories limit 10;";
				 				//"SELECT count(*) FROM java_repositories;";
			String columnName = "name"; 
										//"count(*)";
			JSONArray ans = con.executeCypher(cypher, 3000000);
			con.close();
			
			String name, user, localPath, url;
			int projectId, stars, isForked;
			int isExist;
			int siz = ans.size();
			for (int i=0;i<siz;i++){
				JSONObject job = ans.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
//				System.out.println(columnName+" = "+job.get(columnName));
				name = job.getString("name");
				user = job.getString("user");
				stars = job.getInt("stars");
				projectId = job.getInt("projectId");
				isForked = job.getInt("isForked");
				
				System.out.print(id_thread+" "+projectId + " " + 
									user + " " + name+" "+stars
									+" {"+(i+1)+","+siz);
				System.out.printf(",%.2f%%}\n",100.0*(i+1)/siz);
			
				if (name.length()==6){//"null"
					String subs = (name.substring(1, name.length()-2)).toLowerCase();
				
					if (name.charAt(0)=='\"'&&name.charAt(name.length()-1)=='\"'&&subs=="null"){
						System.out.println("!!!"+i+"  "+user+"  "+name);
						name = subs;
					}
				}
				
				if (stars == 0)
					continue;
				
//				if (stars>=5)
//					continue;
				localPath = "./downloads/"+Integer.toString(id_thread)+"/"+user+"___"+name+"___"+projectId;
		//url = "git@github.com:"+user+"/"+name;
				url = "https://github.com/"
						+user+"/"+name+".git";
				// String url = "https://github.com/XXX/python3-webapp.git";

				isExist = cloneRepository(url,localPath,projectId);
				
				if (isExist==0){
					System.out.println("Not Exist*******");
				}
				
				if (isExist==3){
					i--;
					continue;
				}
				if (isExist==2){//如果是2，代表已经保存过了，就不需要更新数据库了。
					continue;
				}
				
				cypher = "update java_repositories set status = "+Integer.toString(isExist)
						+ " where projectId = "
						+Integer.toString(projectId) +";";  
			    
				conn.add(cypher);
				
				cypher = "update java_repositories set disk = "+Integer.toString(id_server)
				+ " where projectId = "
				+Integer.toString(projectId) +";";  
	    
				conn.add(cypher);
				
			}
			
			conn.close_update();
			return ;
		}catch (Exception e) { e.printStackTrace(); }
	}
}
//String localPath = "./downloads";
//String url = "git@github.com:cocos2d/bindings-generator";

//cloneRepository(url,localPath);