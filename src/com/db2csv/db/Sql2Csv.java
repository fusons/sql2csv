package com.db2csv.db;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;

public class Sql2Csv {
	private String DRIVER;
	private String URL;
	private String USERNAME;
	private String PASSWORD;
	private String FILEPATH;
	
	/**
	 * @param tl  要生成csv文件的table名字列表
	 * @return 生成的文件的路径列表
	 * @throws Exception
	 */
	public List<String> startTableToCSV(String ConfFile, List<String> tl) throws Exception{
		Properties prop = new Properties();
		List<String> fileList = new ArrayList<String>();
		Connection conn = null;
		Statement stmt = null;
		try {

            //读取数据库连接配置
            InputStream in = new BufferedInputStream (new FileInputStream(ConfFile + "jdbc.properties"));
            prop.load(in);     ///加载属性列表

            this.DRIVER=prop.getProperty("driver");
            this.URL=prop.getProperty("url");
            this.USERNAME=prop.getProperty("username");
            this.PASSWORD=prop.getProperty("password");
            this.FILEPATH=prop.getProperty("filepath");
            in.close();

            //conn = new DBConnections.getConn();
            conn = new DBConnections(this.DRIVER, this.URL, this.USERNAME, this.PASSWORD).getConn();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
			for (String t : tl) {
				int count = 0;
				String filename = FILEPATH + generateFilename(t);
				File file = createEmptyFile(filename);
				FileWriter fw = new FileWriter(file, true);
				//
				String sql = "select * from " + t;
				ResultSet rs = stmt.executeQuery(sql);
				writeToFile(fw, rs, ++count);
				fw.close();
				fileList.add(file.getAbsolutePath());
			}
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return fileList;
	} 
	


	//将文件数据库记录写入文件
	private void writeToFile(FileWriter fw, ResultSet rs, int count) throws Exception {
		try {
			ResultSetMetaData rd = rs.getMetaData();
			int fields = rd.getColumnCount();
			if (rd.getColumnName(fields).equals("RN")) {
				fields--;
			}
			if (count == 1) {
				for (int i = 1; i <= fields; i++) {
					fw.write(rd.getColumnName(i));
					if (i == fields)
						fw.write("\n");
					else
						fw.write(",");
				}
	            fw.flush();
			}
			writeToFile(fields, fw, rs);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}


	//将数据记录写入文件
	private void writeToFile(int fields, FileWriter fw, ResultSet rs) throws Exception {
		try {
	        while (rs.next()) {
	        	for (int i = 1; i <= fields; i++) {
	        		String temp = rs.getString(i);
	        		if (!rs.wasNull()) {
	        			//这里将记录里面的特殊符号进行替换， 假定数据中不包含替换后的特殊字串
                        if (temp.indexOf(",")!=-1) {
                           temp = "\""+temp+"\"";
                        }
						//temp = temp.replaceAll(",", "&%&");
						//temp = temp.replaceAll("\n\r|\r|\n|\r\n", "&#&");
	        			fw.write(temp);
	        		}
	        		if (i == fields) 
	        			fw.write("\r\n");
	        		else
	        			fw.write(",");
	        	}
	        	
	            fw.flush();
	        }
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	//创建一个空文件
	private File createEmptyFile(String filename) throws Exception {
	
		File file = new File(filename);
		try {
			if (file.exists()) {	
				file.delete();
				file.createNewFile();
			} else {
				file.createNewFile();
			}
		}catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return file;
	}
	private String generateFilename(String t) {
		String filename = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		filename += t;
		//filename += "_";
		//filename += sdf.format(new Date());
		filename += ".csv";
		return filename;
	}

	//测试
	public static void main(String args[]) {
		String FPath="";
        Options opt = new Options();
        opt.addOption("d", "dir",false, "properties file directory");
        //opt.addOption("v", "verbose", false, "explain what is being done.");
        opt.addOption("h", "help",  false, "print help for the command.");

        String formatstr = "sql2csv [-d/--dir][-h/--help] DirectoryName";
        
        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine cl = null;

        try {
            // 处理Options和参数
            cl = parser.parse( opt, args );
        } catch (ParseException e) {
            formatter.printHelp( formatstr, opt ); // 如果发生异常，则打印出帮助信息
        }

        // 如果包含有-h或--help，则打印出帮助信息
        if (cl.hasOption("h")) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp(formatstr, "", opt, "");
            return;
        }

        // 判断是否有-d参数
        if (cl.hasOption("d")) {
            //System.out.println("has p");
            //System.out.println("dir=" + cl.getOptionValue("d"));
            String[] str = cl.getArgs();
            int length = str.length;
            //System.out.println("length="+length);
            //System.out.println("Str[0]="+str[0]);
            
            FPath=str[0];
        }

		File file = new File(FPath+"tab.properties");
        BufferedReader reader = null;

		//Connection conn = null;
		//Statement stmt = null;
		//String sql = "select table_name from user_tables";
		List<String> tabList = new ArrayList<String>();
		try {

            //读取需要导出的表
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
				tabList.add(tempString);
                System.out.println("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
			
			System.out.println(new Sql2Csv().startTableToCSV(FPath, tabList));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}