package buaa.act;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


public class Init_mysql {
	Connection con;
	Connection conn; 
	Statement st;  
	 
	String driver = "com.mysql.jdbc.Driver"; 
    String url = "jdbc:mysql://192.168.7.118:3306/tianyf";
    
    String user = "root";
    String password = "cooper2017";//"123456";
	
    public Init_mysql(int choose) throws ClassNotFoundException, SQLException, Exception{
    	if (choose == 0)
    		build();
    	else
    		build_update();
    }
    
    
    public void build_update() throws ClassNotFoundException, SQLException, Exception{
    	st = getConnection().createStatement();  
	}
    
	public void build() throws ClassNotFoundException, SQLException, Exception{
		Class.forName(driver);
		this.con = DriverManager.getConnection(url, user, password);
	}
	
	public void close() throws SQLException{
		this.con.close();
	}
	
	public void close_update() throws SQLException{
		// 4、遍历结果集：此处插入记录不需要  
	    // 5、关闭资源对象  
		 st.close();  
	     getConnection().close();  
	}
	
	
	 public Connection getConnection() throws SQLException{  
	        conn=DriverManager.getConnection(url,user,password);  
	        return conn;  
	    }  
	
	 public  void add(String cypher) throws ClassNotFoundException, SQLException {  
		    // 定义sql语句  
//		     String sql1="update java_repositories set status = 2 where projectId = 5;";  
//		     String sql2="insert into Table2(id,name,grade) values('20121115','体育',2)";  
//		     String sql3="insert into Table2(id,name,grade) values('20121116','马克思',3)";  
		    // 3、创建语句对象  
//		    st = getConnection().createStatement();  
		    st.executeUpdate(cypher);  
		     
	}  
	
	
	
	public JSONArray executeCypher(String cypher, int length) throws ClassNotFoundException, Exception{
		
		try(Statement statement = this.con.createStatement() ){
			JSONArray array = new JSONArray();
			
			//System.out.println(cypher);
			ResultSet rs = statement.executeQuery(cypher);
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			int count = 0;
			
			//System.out.println(columnCount);
			
			while (rs.next()) {        
		        JSONObject jsonObj = new JSONObject();
		        
		        for (int i = 1; i <= columnCount; i++) {  
		            String columnName =md.getColumnLabel(i);
		            //System.out.println("columnName "+columnName);
		            String value = rs.getString(columnName); 
		            //System.out.println("value "+value);
		            jsonObj.put(columnName, value);  
	            } 
		        count ++;
				if(count > length) break; 
		        array.add(jsonObj);
		    }  
			
			close();
			//System.out.println(array);
			return array;
		}
	}
	
}
