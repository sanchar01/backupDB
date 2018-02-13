package backDB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * @author hsc
 *
 *         Feb 14, 2018
 *
 */
public class BackAndRecover {

	/*
	 * 备份数据库 1、读取配置文件 2、启动智能查询Mysql安装目录 3、备份数据库为sql文件
	 */
	@SuppressWarnings({ "unused", "static-access" })
	public static void backup(String sql) {

		Properties pros = getPprVue("prop.properties");
		String username = pros.getProperty("username");
		String password = pros.getProperty("password");

		CheckSoftware c = null;
		try {
			System.out.println("MySQL服务安装地址 ：" + c.check().toString());
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		String mysqlpaths;
		try {
			mysqlpaths = c.check().toString() + "\\bin\\";

			String databaseName = pros.getProperty("databaseName");
			String address = pros.getProperty("address");
			String sqlpath = pros.getProperty("sql");
			File backupath = new File(sqlpath);
			if (!backupath.exists()) {
				backupath.mkdir();
			}

			StringBuffer sb = new StringBuffer();

			sb.append(mysqlpaths);
			sb.append("mysqldump ");
			sb.append("--opt ");
			sb.append("-h ");
			sb.append(address);
			sb.append(" ");
			sb.append("--user=");
			sb.append(username);
			sb.append(" ");
			sb.append("--password=");
			sb.append(password);
			sb.append(" ");
			sb.append("--lock-all-tables=true ");
			sb.append("--result-file=");
			sb.append(sqlpath);
			sb.append(sql);
			sb.append(" ");
			sb.append("--default-character-set=utf8 ");
			sb.append(databaseName);
			sb.append(" ");
			sb.append(" ");
			System.out.println("cmd指令 ：" + sb.toString());
			Runtime cmd = Runtime.getRuntime();
			try {
				Process p = cmd.exec(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/*
	 * 表备份  1、读取配置文件 2、启动智能查询Mysql安装目录 3、备份数据库为sql文件
	 */
	@SuppressWarnings({ "unused", "static-access" })
	public static void tableBackup(String sql, String tableName) {

		Properties pros = getPprVue("prop.properties");
		String username = pros.getProperty("username");
		String password = pros.getProperty("password");

		CheckSoftware c = null;
		try {
			System.out.println("MySQL服务安装地址 ：" + c.check().toString());
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		String mysqlpaths;
		try {
			mysqlpaths = c.check().toString() + "\\bin\\";

			String databaseName = pros.getProperty("databaseName");
			String address = pros.getProperty("address");
			String sqlpath = pros.getProperty("sql");
			File backupath = new File(sqlpath);
			if (!backupath.exists()) {
				backupath.mkdir();
			}

			StringBuffer sb = new StringBuffer();

			sb.append(mysqlpaths);
			sb.append("mysqldump ");
			sb.append("--opt ");
			sb.append("-h ");
			sb.append(address);
			sb.append(" ");
			sb.append("--user=");
			sb.append(username);
			sb.append(" ");
			sb.append("--password=");
			sb.append(password);
			sb.append(" ");
			sb.append("--lock-all-tables=true ");
			sb.append("--result-file=");
			sb.append(sqlpath);
			sb.append(sql);
			sb.append(" ");
			sb.append("--default-character-set=utf8 ");
			sb.append(databaseName);
			sb.append(" ");
			sb.append(tableName);
			System.out.println("cmd指令 ：" + sb.toString());
			Runtime cmd = Runtime.getRuntime();
			try {
				Process p = cmd.exec(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/*
	 * 读取属性文件
	 */
	public static Properties getPprVue(String properName) {

		InputStream inputStream = BackAndRecover.class.getClassLoader().getResourceAsStream(properName);
		Properties p = new Properties();

		try {
			p.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return p;

	}

	/*
	 * 根据备份文件恢复数据库
	 */
	@SuppressWarnings("static-access")
	public static void load(String filename) throws Exception {
		Properties pros = getPprVue("prop.properties");
		String root = pros.getProperty("username");
		String pass = pros.getProperty("password");
		CheckSoftware c = null;
		String mysqlpaths = c.check().toString() + "\\bin\\";
		String sqlpath = pros.getProperty("sql");
		String filepath = sqlpath + filename; // 备份的路径地址

		String stmt1 = mysqlpaths + "mysqladmin -u " + root + " -p " + pass + " create finacing"; // -p后面加的是你的密码
		String stmt2 = mysqlpaths + "mysql -u " + root + " -p " + pass + " finacing < " + filepath;
		System.out.println(stmt1 + "\n" + stmt2);
		String[] cmd = { "cmd", "/c", stmt2 };
		try {
			Runtime.getRuntime().exec(stmt1);
			Runtime.getRuntime().exec(cmd);
			System.out.println("数据已从 " + filepath + " 导入到数据库中");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public static void restore(String filename) throws Exception {

		Properties pros = getPprVue("prop.properties");
		String root = pros.getProperty("username");
		String pass = pros.getProperty("password");
		String address = pros.getProperty("address");
		String databaseName = pros.getProperty("databaseName");
		CheckSoftware c = null;
		String mysqlpaths = c.check().toString() + "\\bin\\";
		String sqlpath = pros.getProperty("sql");
		String filepath = sqlpath + filename; // 备份的路径地址
		String stmt = mysqlpaths + "mysql -h" + address + " -u" + root + " -p" + pass + " --default-character-set=utf8 " + databaseName;

		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(stmt);
			OutputStream outputStream = process.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "utf-8"));
			String str = null;
			StringBuffer sb = new StringBuffer();
			while ((str = br.readLine()) != null) {
				sb.append(str + "\r\n");
			}
			str = sb.toString();
			OutputStreamWriter writer = new OutputStreamWriter(outputStream, "utf-8");
			writer.write(str);
			writer.flush();
			outputStream.close();
			br.close();
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Test测试
	 */
	public static void main(String[] args) {
		try {
			restore("t_user.sql");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		tableBackup("t_user.sql","t_user");
	}
}
