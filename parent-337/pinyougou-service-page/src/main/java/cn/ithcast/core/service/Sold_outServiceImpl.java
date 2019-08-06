package cn.ithcast.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;

@Service
public class Sold_outServiceImpl implements Sold_outService,ServletContextAware {
    @Override
    public void DeletePage(String id) {

        String path = getPath("/" + id + ".html");

        File file = new File(path);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + path + "成功！");
            } else {
                System.out.println("删除单个文件" + path + "失败！");
            }
        }
    }


    public String getPath(String s){
        return servletContext.getRealPath(s);
    }

    private ServletContext servletContext;
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext=servletContext;
    }
}
