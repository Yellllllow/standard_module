package com.zhiding.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webbuilder.common.Var;
import com.webbuilder.utils.DateUtil;
import com.webbuilder.utils.FileUtil;
import com.webbuilder.utils.MD5FileUtil;
import com.webbuilder.utils.StringUtil;

public class ZipFiles {
	private static final Logger log = LoggerFactory.getLogger(ZipFiles.class);

    private ZipFiles(){};
    /**
     * 创建内容包
     * @param request
     * @param contentId 内容ID
     * @param title 标题
     * @param time 发布时间
     * @param aurth 作者
     * @param summary 摘要
     * @param content 内容
     * @return 返回ZIP文件的MD5值,资源包下载地址
     */
    public static String createContentPack(HttpServletRequest request,String contentId,String title,String time,
    		String aurth,String summary,String content) throws Exception{
    	String file_md5="";
    	//模版路径------------------------------路径待修改-----------------------------------------
    	String tpl_dir=request.getRealPath(Var.get("api.zip.tpl_dir"))+"/template";
    	tpl_dir=StringUtil.replace(tpl_dir,"\\","/");
    	//文件保存地址
    	String files_dir=request.getRealPath(Var.get("api.zip.tpl_dir"))+"/"+contentId;
    	files_dir=StringUtil.replace(files_dir,"\\","/");
    	//资源保存路径
    	String css_dir=request.getRealPath(Var.get("api.zip.tpl_dir"))+"/"+contentId+"/css";
    	String img_dir=request.getRealPath(Var.get("api.zip.tpl_dir"))+"/"+contentId+"/images";
    	//内容包保存地址
    	String zip_dir=request.getRealPath(Var.get("api.zip.zip_dir"));
    	zip_dir=StringUtil.replace(zip_dir,"\\","/");
    	//-----------------------------------------------------------------------------------------------
    	//创建文件目录
    	FileUtil.directoryCreate(files_dir);
    	FileUtil.directoryCreate(css_dir);
    	FileUtil.directoryCreate(img_dir);
    	FileUtil.directoryCreate(zip_dir);
    	//拷贝模版文件
    	FileUtil.copyFolder(new File(tpl_dir+"/css"), new File(css_dir), false, false);
    	FileUtil.copyFolder(new File(tpl_dir+"/images"), new File(img_dir), false, false);
    	
    	//解析HTML代码,替换内容
    	BufferedReader br = null;  
        String line = null;  
        StringBuffer buf = new StringBuffer();
        OutputStream os =null;
        InputStream is=null;
    	try {
    		//图片处理
        	List imglist=getImgStr(content);
        	for (Object object : imglist) {
    			String src=object.toString();
    			String name=src.substring(src.lastIndexOf("/")+1);
    			if(src.indexOf("http://")!=-1){
    				// 构造URL
    				URL url = new URL(src);
    				// 打开连接
    				URLConnection con = url.openConnection();
    				// 设置请求超时为20s
    				con.setConnectTimeout(20 * 1000);
    				// 输入流
    				is = con.getInputStream();
    				// 1K的数据缓冲
    				byte[] bs = new byte[1024];
    				// 读取到的数据长度
    				int len;
    				// 输出的文件流
    				os = new FileOutputStream(new File(img_dir + "/" + name));
    				// 开始读取
    				while ((len = is.read(bs)) != -1) {
    					os.write(bs, 0, len);
    				}
    			}
    			//替换内容文本
    			content=content.replaceAll(src, "images/"+name);
    		}
    		// 根据文件路径创建缓冲输入流  
            br = new BufferedReader(new FileReader(tpl_dir+"/content.html"));  
            // 循环读取文件的每一行, 对需要修改的行进行修改, 放入缓冲对象中  
            while ((line = br.readLine()) != null) {  
                // 此处根据实际需要修改某些行的内容  
                if (line.indexOf("#title#")!=-1) {  
                    buf.append(line.replaceAll("#title#", title));  
                }  
                else if (line.indexOf("#time#")!=-1) {  
                	buf.append(line.replaceAll("#time#",time.substring(0,11))); 
                }
                else if (line.indexOf("#aurth#")!=-1) {  
               	 	buf.append(line.replaceAll("#aurth#", aurth)); 
                }  
                else if (line.indexOf("#summary#")!=-1) {  
                	buf.append(line.replaceAll("#summary#", summary)); 
                }  
                else if (line.indexOf("#content#")!=-1) {  
               	 	buf.append(line.replaceAll("#content#", content)); 
                }  
                // 如果不用修改, 则按原来的内容回写  
                else {  
                    buf.append(line);  
                }  
            }  
		} catch (Exception e) {
			e.printStackTrace();
		}finally {  
			if (os != null) {  
                try {  
                	os.close();  
                } catch (IOException e) {  
                	os = null;  
                }  
            } 
			if (is != null) {  
                try {  
                	is.close();  
                } catch (IOException e) {  
                	is = null;  
                }  
            } 
            // 关闭流  
            if (br != null) {  
                try {  
                    br.close();  
                } catch (IOException e) {  
                    br = null;  
                }  
            }  
        }  
    	String html=buf.toString();
    	//保存HTML文件
    	BufferedWriter bw = null;  
        try {  
            // 根据文件路径创建缓冲输出流  
            bw = new BufferedWriter(new FileWriter(files_dir+"/content.html"));  
            // 将内容写入文件中  
            bw.write(html);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭流  
            if (bw != null) {  
                try {  
                    bw.close();  
                } catch (IOException e) {  
                    bw = null;  
                }  
            }  
        }  
        //判断ZIP文件是否存在，存在则删除
        File zipFile=new File(zip_dir+"/"+contentId+".zip");
        if  (zipFile.exists()){
        	zipFile.delete();
		}
    	//创建ZIP
        createZip(files_dir,zipFile);
        //获取文件MD5
        file_md5=MD5FileUtil.getFileMd5(zipFile);
    	return file_md5;
    }
    
    /**
     * 获取HTML内容中的所有图片路径
     * @param htmlStr
     * @return
     */
	public static List getImgStr(String htmlStr) {
		String regex;
		List<String> list = new ArrayList<String>();
		// 提取字符串中的img标签
		regex = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
		Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
		Matcher ma = pa.matcher(htmlStr);
		while (ma.find()) {
			// 提取字符串中的src路径
			Matcher m = Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(
					ma.group());
			while (m.find()) {
				list.add(m.group(1));
			}
		}
		return list;
	}

   /**
     * 创建ZIP文件
     * @param sourcePath 文件或文件夹路径
     * @param zipPath 生成的zip文件存在路径（包括文件名）
     */
    public static void createZip(String sourcePath, File zipFile) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos,Charset.forName("UTF-8"));
            writeZip(new File(sourcePath), "", zos);
        } catch (FileNotFoundException e) {
            log.error("创建ZIP文件失败",e);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
                if (fos != null) {
                	fos.close();
                }
            } catch (IOException e) {
                log.error("创建ZIP文件失败",e);
            }

        }
    }

    private static void writeZip(File file, String parentPath, ZipOutputStream zos) {
        if(file.exists()){
            if(file.isDirectory()){//处理文件夹
                parentPath+=file.getName()+File.separator;
                File [] files=file.listFiles();
                for(File f:files){
                    writeZip(f, parentPath, zos);
                }
            }else{
                FileInputStream fis=null;
                try {
                    fis=new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte [] content=new byte[1024];
                    int len;
                    while((len=fis.read(content))!=-1){
                        zos.write(content,0,len);
                        zos.flush();
                    }

                } catch (FileNotFoundException e) {
                    log.error("创建ZIP文件失败",e);
                } catch (IOException e) {
                    log.error("创建ZIP文件失败",e);
                }finally{
                    try {
                        if(fis!=null){
                            fis.close();
                        }
                    }catch(IOException e){
                        log.error("创建ZIP文件失败",e);
                    }
                }
            }
        }
    }    
    public static void main(String[] args) {
    	String path=Thread.currentThread().getContextClassLoader().getResource(".").getPath();
    	System.out.println(path);
        //ZipFiles.createZip("C:/Users/chenpy/Desktop/page", "C:/Users/chenpy/Desktop/backup.zip");

    }
	
}
