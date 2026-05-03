/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.xiaoleilu.loServer.action.robot;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.InputGetPresignedUploadUrl;
import cn.wildfirechat.pojos.InputGetUserInfo;
import cn.wildfirechat.pojos.InputOutputUserInfo;
import cn.wildfirechat.pojos.OutputPresignedUploadUrl;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import com.qiniu.util.Auth;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.moquette.persistence.MemorySessionStore;
import io.moquette.server.config.MediaServerConfig;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.*;

@Route(APIPath.Robot_Get_Presigned_Upload_Url)
@HttpMethod("POST")
public class GetUploadUrlAction extends RobotAction {

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            if(!MediaServerConfig.USER_QINIU) {
                setResponseContent(RestResult.resultOf(ErrorCode.INVALID_PARAMETER), response);
                return true;
            }

            InputGetPresignedUploadUrl inputUserId = getRequestBody(request.getNettyRequest(), InputGetPresignedUploadUrl.class);

            if (StringUtil.isNullOrEmpty(inputUserId.contentType)) {
                inputUserId.contentType = "application/octet-stream";
            }
            String uuid = UUID.randomUUID().toString().replace("-", "");
            inputUserId.fileName = uuid + inputUserId.fileName;

            int type = inputUserId.mediaType;
            String bucketName;
            String bucketDomain;
            switch (type) {
                case 0:
                default:
                    bucketName = MediaServerConfig.QINIU_BUCKET_GENERAL_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_GENERAL_DOMAIN;
                    break;
                case 1:
                    bucketName = MediaServerConfig.QINIU_BUCKET_IMAGE_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_IMAGE_DOMAIN;
                    break;
                case 2:
                    bucketName = MediaServerConfig.QINIU_BUCKET_VOICE_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_VOICE_DOMAIN;
                    break;
                case 3:
                    bucketName = MediaServerConfig.QINIU_BUCKET_VIDEO_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_VIDEO_DOMAIN;
                    break;
                case 4:
                    bucketName = MediaServerConfig.QINIU_BUCKET_FILE_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_FILE_DOMAIN;
                    break;
                case 5:
                    bucketName = MediaServerConfig.QINIU_BUCKET_PORTRAIT_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_PORTRAIT_DOMAIN;
                    break;
                case 6:
                    bucketName = MediaServerConfig.QINIU_BUCKET_FAVORITE_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_FAVORITE_DOMAIN;
                    break;
                case 7:
                    bucketName = MediaServerConfig.QINIU_BUCKET_STICKER_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_STICKER_DOMAIN;
                    break;
                case 8:
                    bucketName = MediaServerConfig.QINIU_BUCKET_MOMENTS_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_MOMENTS_DOMAIN;
                    break;
                case 9:
                    bucketName = MediaServerConfig.QINIU_BUCKET_CUSTOM1_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_CUSTOM1_DOMAIN;
                    break;
                case 10:
                    bucketName = MediaServerConfig.QINIU_BUCKET_CUSTOM2_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_CUSTOM2_DOMAIN;
                    break;
                case 11:
                    bucketName = MediaServerConfig.QINIU_BUCKET_CUSTOM3_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_CUSTOM3_DOMAIN;
                    break;
                case 12:
                    bucketName = MediaServerConfig.QINIU_BUCKET_PAN_NAME;
                    bucketDomain = MediaServerConfig.QINIU_BUCKET_PAN_DOMAIN;
                    break;
            }

            if(StringUtil.isNullOrEmpty(bucketName)) {
                bucketName = MediaServerConfig.QINIU_BUCKET_GENERAL_NAME;
                bucketDomain = MediaServerConfig.QINIU_BUCKET_GENERAL_DOMAIN;
            }

            String key = inputUserId.fileName;
            Auth auth = Auth.create(MediaServerConfig.QINIU_ACCESS_KEY, MediaServerConfig.QINIU_SECRET_KEY);
            String token = auth.uploadToken(bucketName, key);
            String downloadUrl = bucketDomain + "/" + key;
            String uploadUrl = "https://" + MediaServerConfig.QINIU_SERVER_URL + "?" + token + "?" + key;

            OutputPresignedUploadUrl outputPresignedUploadUrl = new OutputPresignedUploadUrl();
            outputPresignedUploadUrl.uploadUrl = uploadUrl;
            outputPresignedUploadUrl.downloadUrl = downloadUrl;
            outputPresignedUploadUrl.type = 1;
            setResponseContent(RestResult.ok(outputPresignedUploadUrl), response);
        }
        return true;
    }
}
