package com.comm.community.provider;

import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.api.object.ObjectConfig;
import cn.ucloud.ufile.auth.ObjectAuthorization;
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization;
import cn.ucloud.ufile.bean.PutObjectResultBean;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;
import com.comm.community.exception.CustomizeErrorCode;
import com.comm.community.exception.CustomizeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class UCloudProvider {
    @Value("{ucloud.ufile.public-key}")
    private String publicKey;
    @Value("{ucloud.ufile.private-key}")
    private String privateKey;

    @Value("{ucloud.ufile.bucket-name}")
    private String bucketName;

    @Value("{ucloud.ufile.region}")
    private String region;

    @Value("{ucloud.ufile.suffix}")
    private String suffix;

    //@Value("{ucloud.ufile.expires}")
    //private Integer expires;

    public String upload(InputStream fileStream, String mimeType, String fileName){
        String generatedFileName;
        String[] filePaths = fileName.split("\\.");
        if (filePaths.length>1){
            generatedFileName = UUID.randomUUID().toString() + "." + filePaths[filePaths.length-1];
        }else {
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }

        try {
            ObjectAuthorization objectAuthorization = new UfileObjectLocalAuthorization(publicKey, privateKey);
            ObjectConfig config = new ObjectConfig(region, suffix);//region
            PutObjectResultBean response = UfileClient.object(objectAuthorization, config)
                    .putObject(fileStream,fileStream.available(), mimeType)
                    .nameAs(generatedFileName)
                    .toBucket("atchn")
                    .setOnProgressListener((bytesWritten, contentLength) -> {

                    })
                    .execute();
            if (response !=null&&response.getRetCode() == 0){//p55 生成具有有效期的图片地址
                String url = UfileClient.object(objectAuthorization, config).getDownloadUrlFromPrivateBucket(generatedFileName, bucketName, 24*60*60*365)//24*60*60*365有效时间是一年,后面可以做优化P56
                        .createUrl();
                return url;
            }else {
                throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
            }
        } catch (UfileClientException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        } catch (UfileServerException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }
    }
}
