package com.mysql.service;

import com.mysql.dao.NewsDAO;
import com.mysql.model.News;
import com.mysql.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId,int offset,int limit){
        return newsDAO.selectByUserIdAndOffset(userId,offset,limit);
    }

    public News getById(int newsId){
        return newsDAO.getById(newsId);
    }

    public int addNews(News news){
        newsDAO.addNews(news);
        return news.getId();
    }

    public int updateCommentCount(int id,int count){
        return newsDAO.updateCommentCount(id,count);
    }

    public int updateLikeCount(int id,int count){
        return newsDAO.updateLikeCount(id, count);
    }
    public String saveImage(MultipartFile file) throws IOException{
        int doPos = file.getOriginalFilename().lastIndexOf(".");
        if(doPos < 0){
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(doPos + 1).toLowerCase();
        if(!ToutiaoUtil.isFileAllowed(fileExt)){
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-","") + "." + fileExt;
        Files.copy(file.getInputStream(),new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
    }
}
