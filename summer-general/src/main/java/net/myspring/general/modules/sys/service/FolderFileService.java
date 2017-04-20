package net.myspring.general.modules.sys.service;

import com.google.common.collect.Lists;
import net.myspring.general.common.exception.ServiceException;
import net.myspring.general.common.utils.CacheUtils;
import net.myspring.general.common.utils.SecurityUtils;
import net.myspring.general.modules.sys.domain.FolderFile;
import net.myspring.general.modules.sys.dto.FolderFileDto;
import net.myspring.general.modules.sys.manager.FolderFileManager;
import net.myspring.general.modules.sys.mapper.FolderFileMapper;
import net.myspring.general.modules.sys.web.query.FolderFileQuery;
import net.myspring.util.io.FileUtil;
import net.myspring.util.mapper.BeanUtil;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FolderFileService {
    @Value("${upload.root-path}")
    private String uploadRootPath;
    @Value("${upload.preview-size}")
    private Integer uploadPreviewSize;

    @Autowired
    private FolderFileManager folderFileManager;
    @Autowired
    private FolderFileMapper folderFileMapper;

    @Autowired
    private CacheUtils cacheUtils;

    @Transactional
    public List<FolderFile> save(String folderId, Map<String, MultipartFile> fileMap) {
        List<FolderFile> list = Lists.newArrayList();
        try {
            for (MultipartFile multipartFile : fileMap.values()) {
                if (multipartFile.getSize() > 0) {
                    // 保存到数据库
                    FolderFileDto folderFileDto = new FolderFileDto();
                    folderFileDto.setFolderId(folderId);
                    folderFileDto.setContentType(multipartFile.getContentType());
                    folderFileDto.setName(multipartFile.getOriginalFilename().replaceAll("/","."));
                    folderFileDto.setSize(Integer.valueOf(String.valueOf(multipartFile.getSize())));
                    folderFileDto.setPhysicalName(UUID.randomUUID().toString() + "." + folderFileDto.getExtendType());
                    FolderFile folderFile = BeanUtil.map(folderFileDto,FolderFile.class);
                    folderFileManager.save(folderFile);
                    list.add(folderFile);
                    // 保存文件
                    String uploadPath = getUploadPath(folderFile);
                    FileUtil.makesureDirExists(uploadPath);
                    FileCopyUtils.copy(multipartFile.getBytes(), new FileOutputStream(uploadPath));
                    if (folderFileDto.isImage()) {
                        String previewUploadPath = getPreviewUploadPath(folderFile);
                        FileUtil.makesureDirExists(previewUploadPath);
                        File file = new File(uploadPath);
                        BufferedImage image;
                        image = Scalr.resize(ImageIO.read(file), uploadPreviewSize);
                        File pngFile = new File(previewUploadPath);
                        ImageIO.write(image, "png", pngFile);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException("文件上传失败" + e.getMessage());
        }

        return list;
    }

    public FolderFile findOne(String id) {
        return folderFileManager.findOne(id);
    }


    public List<FolderFile> findByIds(List<String> ids) {
        return folderFileMapper.findByIds(ids);
    }

    public String getUploadPath(FolderFile folderFile) {
        return uploadRootPath + SecurityUtils.getCompanyId() + "\\upload\\" + folderFile.getPhysicalName();
    }

    public String getPreviewUploadPath(FolderFile folderFile) {
        return uploadRootPath + SecurityUtils.getCompanyId()+ "\\convert\\" + folderFile.getPhysicalName().substring(0, folderFile.getPhysicalName().lastIndexOf(".")) + ".png";
    }

    public Page<FolderFileDto> findPage(Pageable pageable, FolderFileQuery folderFileQuery) {
        Page<FolderFileDto> FolderFileDtoPage= folderFileMapper.findPage(pageable, folderFileQuery);
        cacheUtils.initCacheInput(FolderFileDtoPage.getContent());
        return FolderFileDtoPage;
    }
}