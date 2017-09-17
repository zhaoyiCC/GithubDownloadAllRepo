package buaa.act;

import java.io.File;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class App {

 public static void main(String[] args) throws ClassNotFoundException, Exception{
	 
	 	
	    int tot = 2691654;
		int each_tot = tot/8;//8台服务器一起跑
		int id_server = 1;
		int offset = (id_server-1) * each_tot;
		
	    int core = 9;//实验室的电脑CPU核心数是16
		int threads = 2*core+2;//合适的线程的数量: CPU核心数量*2 +2 个线程 
		int each = each_tot/threads;
		int start, end, j;
		
		Init con[] = new Init[threads+1];
		
		for (int i = 1; i <= threads; ++i){
			start = (i-1)*each;
			end = i*each-1;
			
			start+=offset;
			end+=offset;
			
			if (i==threads){
				if (id_server==8)
					end = tot;
				else
					end = id_server*each_tot-1;
			}
			j = threads*(id_server-1)+i;
			System.out.println(j+" "+start+" "+end);
			con[i] = new Init(id_server,j,start,(end-start+1));
		}
		
		
		
		for (int i = 1; i <= threads; ++i){
			con[i].start();
		}
	 
 	}
}

