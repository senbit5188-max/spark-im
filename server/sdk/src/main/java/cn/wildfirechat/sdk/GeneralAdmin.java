package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;

/**
 * 通用管理类
 * <p>
 * 提供通用的系统管理功能，包括：
 * <ul>
 * <li>系统设置管理</li>
 * <li>文件管理（会话文件、用户文件）</li>
 * <li>用户设置管理（会话置顶等）</li>
 * <li>健康检查</li>
 * <li>频道管理（已废弃，请使用ChannelAdmin）</li>
 * </ul>
 * </p>
 */
public class GeneralAdmin {
    /**
     * 获取系统设置
     * @param id 设置项ID
     * @return 系统设置信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<SystemSettingPojo> getSystemSetting(int id) throws Exception {
        String path = APIPath.Get_System_Setting;
        SystemSettingPojo input = new SystemSettingPojo();
        input.id = id;
        return AdminHttpUtils.httpJsonPost(path, input, SystemSettingPojo.class);
    }

    /**
     * 设置系统设置
     * @param id 设置项ID
     * @param value 设置值
     * @param desc 设置描述
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> setSystemSetting(int id, String value, String desc) throws Exception {
        String path = APIPath.Put_System_Setting;
        SystemSettingPojo input = new SystemSettingPojo();
        input.id = id;
        input.value = value;
        input.desc = desc;
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#createChannel(InputCreateChannel inputCreateChannel)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<OutputCreateChannel> createChannel(InputCreateChannel inputCreateChannel) throws Exception {
        String path = APIPath.Create_Channel;
        return AdminHttpUtils.httpJsonPost(path, inputCreateChannel, OutputCreateChannel.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#destroyChannel(String channelId)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<Void> destroyChannel(String channelId) throws Exception {
        String path = APIPath.Destroy_Channel;
        InputChannelId inputChannelId = new InputChannelId(channelId);
        return AdminHttpUtils.httpJsonPost(path, inputChannelId, Void.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#getChannelInfo(String channelId)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<OutputGetChannelInfo> getChannelInfo(String channelId) throws Exception {
        String path = APIPath.Get_Channel_Info;
        InputChannelId inputChannelId = new InputChannelId(channelId);
        return AdminHttpUtils.httpJsonPost(path, inputChannelId, OutputGetChannelInfo.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#subscribeChannel(String userId, String channelId)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<Void> subscribeChannel(String channelId, String userId) throws Exception {
        String path = APIPath.Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 1);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#unsubscribeChannel(String userId, String channelId)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<Void> unsubscribeChannel(String channelId, String userId) throws Exception {
        String path = APIPath.Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 0);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * @deprecated 请使用 {@link cn.wildfirechat.sdk.ChannelAdmin#isUserSubscribedChannel(String userId, String channelId)} 代替此方法，因为它将在未来的版本中被移除。
     */
    @Deprecated
    public static IMResult<OutputBooleanValue> isUserSubscribedChannel(String userId, String channelId) throws Exception {
        String path = APIPath.Check_User_Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 0);
        return AdminHttpUtils.httpJsonPost(path, input, OutputBooleanValue.class);
    }

    /**
     * 获取会话文件列表（仅专业版支持）
     * <p>
     * 如果是单聊会话，target和userId代表会话的2个用户；如果是其他会话userId无意义。
     * </p>
     * @param conversationType 会话类型
     * @param target 会话目标ID
     * @param line 线路
     * @param userId 用户ID
     * @param offset 偏移量
     * @param desc 是否降序
     * @param count 每页数量
     * @return 文件列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<FilesPojo> getConversationFiles(int conversationType, String target, int line, String userId, int offset, boolean desc, int count) throws Exception {
        String path = APIPath.Get_Conversation_Files;
        GetConversationFilesPojo input = new GetConversationFilesPojo();
        input.conversationType = conversationType;
        input.target = target;
        input.line = line;
        input.userId = userId;
        input.offset = offset;
        input.desc = desc;
        input.count = count;
        return AdminHttpUtils.httpJsonPost(path, input, FilesPojo.class);
    }

    /**
     * 获取用户文件列表
     * @param userId 用户ID
     * @param offset 偏移量
     * @param desc 是否降序
     * @param count 每页数量
     * @return 文件列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<FilesPojo> getUserFiles(String userId, int offset, boolean desc, int count) throws Exception {
        String path = APIPath.Get_User_Files;
        GetUserFilesPojo input = new GetUserFilesPojo();
        input.userId = userId;
        input.offset = offset;
        input.desc = desc;
        input.count = count;
        return AdminHttpUtils.httpJsonPost(path, input, FilesPojo.class);
    }

    /**
     * 根据消息ID获取文件信息
     * @param messageId 消息ID
     * @return 文件信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<FilesPojo.FilePojo> getFile(long messageId) throws Exception {
        String path = APIPath.Get_Message_File;
        LongPojo input = new LongPojo();
        input.value = messageId;
        return AdminHttpUtils.httpJsonPost(path, input, FilesPojo.FilePojo.class);
    }

    /**
     * 设置会话置顶
     * @param userId 用户ID
     * @param conversationType 会话类型
     * @param target 会话目标ID
     * @param line 线路
     * @param isTop true-置顶，false-取消置顶
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> setConversationTop(String userId, int conversationType, String target, int line, boolean isTop) throws Exception {
        String key = conversationType + "-" + line + "-" + target;
        String value = isTop?"1":"0";
        return setUserSetting(userId, 3, key, value);
    }

    /**
     * 获取会话置顶状态
     * @param userId 用户ID
     * @param conversationType 会话类型
     * @param target 会话目标ID
     * @param line 线路
     * @return true-已置顶，false-未置顶
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Boolean> getConversationTop(String userId, int conversationType, String target, int line) throws Exception {
        String key = conversationType + "-" + line + "-" + target;
        IMResult<UserSettingPojo> result = getUserSetting(userId, 3, key);
        IMResult<Boolean> out = new IMResult<Boolean>();
        out.code = result.code;
        out.msg = result.msg;
        out.result = result.result != null && "1".equals(result.result.getValue());
        return out;
    }

    /**
     * 获取用户设置
     * @param userId 用户ID
     * @param scope 设置范围
     * @param key 设置键
     * @return 用户设置信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<UserSettingPojo> getUserSetting(String userId, int scope, String key) throws Exception {
        String path = APIPath.User_Get_Setting;
        UserSettingPojo pojo = new UserSettingPojo();
        pojo.setUserId(userId);
        pojo.setScope(scope);
        pojo.setKey(key);
        return AdminHttpUtils.httpJsonPost(path, pojo, UserSettingPojo.class);
    }

    /**
     * 设置用户设置
     * @param userId 用户ID
     * @param scope 设置范围
     * @param key 设置键
     * @param value 设置值
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> setUserSetting(String userId, int scope, String key, String value) throws Exception {
        String path = APIPath.User_Put_Setting;
        UserSettingPojo pojo = new UserSettingPojo();
        pojo.setUserId(userId);
        pojo.setScope(scope);
        pojo.setKey(key);
        pojo.setValue(value);
        return AdminHttpUtils.httpJsonPost(path, pojo, Void.class);
    }

    /**
     * 健康检查
     * @return 健康检查结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<HealthCheckResult> healthCheck() throws Exception {
        return AdminHttpUtils.httpGet(APIPath.Health, HealthCheckResult.class);
    }

    /**
     * 获取客户信息
     * @return 客户信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<String> getCustomer() throws Exception {
        return AdminHttpUtils.httpJsonPost(APIPath.GET_CUSTOMER, null, String.class);
    }

    public static IMResult<OutputPresignedUploadUrl> getPresignedUploadUrl(String fileName, int/*ProtoConstants.MessageMediaType*/ mediaType, String contentType) throws Exception {
        String path = APIPath.Get_Presigned_Upload_Url;

        InputGetPresignedUploadUrl requestPojo = new InputGetPresignedUploadUrl();
        requestPojo.fileName = fileName;
        requestPojo.mediaType = mediaType;
        requestPojo.contentType = contentType;
        return AdminHttpUtils.httpJsonPost(path, requestPojo, OutputPresignedUploadUrl.class);
    }

    /**
     * 根据文件名获取Content-Type
     *
     * @param fileName 文件名
     * @return Content-Type，如果无法识别则返回 "application/octet-stream"
     */
    private static String getContentTypeByFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "application/octet-stream";
        }

        // 首先尝试使用 URLConnection 猜测
        String contentType = URLConnection.guessContentTypeFromName(fileName);

        // 如果无法识别，使用常见扩展名映射
        if (contentType == null) {
            String lowerName = fileName.toLowerCase();
            if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (lowerName.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerName.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (lowerName.endsWith(".bmp")) {
                contentType = "image/bmp";
            } else if (lowerName.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (lowerName.endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (lowerName.endsWith(".mov")) {
                contentType = "video/quicktime";
            } else if (lowerName.endsWith(".avi")) {
                contentType = "video/x-msvideo";
            } else if (lowerName.endsWith(".mp3")) {
                contentType = "audio/mpeg";
            } else if (lowerName.endsWith(".wav")) {
                contentType = "audio/wav";
            } else if (lowerName.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (lowerName.endsWith(".doc")) {
                contentType = "application/msword";
            } else if (lowerName.endsWith(".docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if (lowerName.endsWith(".xls")) {
                contentType = "application/vnd.ms-excel";
            } else if (lowerName.endsWith(".xlsx")) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if (lowerName.endsWith(".ppt")) {
                contentType = "application/vnd.ms-powerpoint";
            } else if (lowerName.endsWith(".pptx")) {
                contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            } else if (lowerName.endsWith(".txt")) {
                contentType = "text/plain";
            } else if (lowerName.endsWith(".html") || lowerName.endsWith(".htm")) {
                contentType = "text/html";
            } else if (lowerName.endsWith(".json")) {
                contentType = "application/json";
            } else if (lowerName.endsWith(".xml")) {
                contentType = "application/xml";
            } else if (lowerName.endsWith(".zip")) {
                contentType = "application/zip";
            } else if (lowerName.endsWith(".rar")) {
                contentType = "application/x-rar-compressed";
            } else if (lowerName.endsWith(".7z")) {
                contentType = "application/x-7z-compressed";
            } else if (lowerName.endsWith(".tar")) {
                contentType = "application/x-tar";
            } else if (lowerName.endsWith(".gz")) {
                contentType = "application/gzip";
            } else {
                contentType = "application/octet-stream";
            }
        }

        return contentType;
    }


    /**
     * 上传文件
     * <p>
     * 流程：先调用getPresignedUploadUrl获取预签名上传地址，然后直接上传文件。
     * 上传成功后返回文件的下载地址等信息。
     * </p>
     *
     * @param file        要上传的文件
     * @return 上传结果，包含下载地址
     * @throws Exception 上传失败时抛出异常
     */
    public static IMResult<String> uploadFile(File file) throws Exception {
        return uploadFile(file, ProtoConstants.MessageMediaType.FILE, null);
    }
    /**
     * 上传文件
     * <p>
     * 流程：先调用getPresignedUploadUrl获取预签名上传地址，然后直接上传文件。
     * 上传成功后返回文件的下载地址等信息。
     * </p>
     *
     * @param file        要上传的文件
     * @param mediaType   媒体类型，参考{@link cn.wildfirechat.proto.ProtoConstants.MessageMediaType}
     * @param contentType 文件Content-Type，例如 "image/jpeg", "application/octet-stream" 等；
     *                    如果为null或空，则根据文件名自动识别
     * @return 上传结果，包含下载地址
     * @throws Exception 上传失败时抛出异常
     */
    public static IMResult<String> uploadFile(File file, int mediaType, String contentType) throws Exception {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("文件不能为空或不存在");
        }

        // 如果未指定Content-Type，根据文件名自动获取
        if (contentType == null || contentType.isEmpty()) {
            contentType = getContentTypeByFileName(file.getName());
        }

        return doUploadFile(file.getName(), mediaType, contentType, file, null);
    }

    /**
     * 执行文件上传的公共方法
     * <p>
     * 根据服务器类型选择不同的上传方式：
     * <ul>
     * <li>type = 1（七牛云）：使用 POST + multipart/form-data 表单上传</li>
     * <li>type = 其他（阿里云、Minio等）：使用 PUT + 二进制流上传</li>
     * </ul>
     * </p>
     *
     * @param fileName    文件名
     * @param mediaType   媒体类型
     * @param contentType Content-Type
     * @param file        文件对象（用于七牛云上传）
     * @param inputStream 输入流（用于其他类型上传，可为null）
     * @return 上传结果
     * @throws Exception 上传失败时抛出异常
     */
    private static IMResult<String> doUploadFile(String fileName, int mediaType,
                                          String contentType,
                                          File file,
                                          InputStream inputStream) throws Exception {
        // 1. 获取预签名上传地址
        IMResult<OutputPresignedUploadUrl> presignedResult = getPresignedUploadUrl(
            fileName, mediaType, contentType);

        if (presignedResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
            IMResult<String> imResult = new IMResult<>();
            imResult.setCode(presignedResult.code);
            imResult.setMsg(presignedResult.msg);
            return imResult;
        }

        OutputPresignedUploadUrl presignedUrl = presignedResult.getResult();
        if (presignedUrl == null || presignedUrl.uploadUrl == null) {
            throw new Exception("预签名上传地址为空");
        }

        // 2. 根据服务器类型选择上传方式
        if (presignedUrl.type == 1) {
            // 七牛云：使用 POST + multipart/form-data 表单上传
            return uploadToQiniu(presignedUrl, file, inputStream, fileName, contentType);
        } else {
            // 其他（阿里云、Minio、腾讯云、华为云、AWS S3等）：使用 PUT 上传
            return uploadToOther(presignedUrl, file, inputStream, contentType);
        }
    }

    /**
     * 上传到七牛云（type = 1）
     * 使用 POST + multipart/form-data 表单格式
     */
    private static IMResult<String> uploadToQiniu(OutputPresignedUploadUrl presignedUrl, File file,
                                           InputStream inputStream, String fileName,
                                           String contentType) throws Exception {
        // 解析七牛云上传地址：格式为 "https://host?token?key"
        String uploadUrl = presignedUrl.uploadUrl;
        String token;
        String key;

        int tokenStart = uploadUrl.indexOf('?');
        int keyStart = uploadUrl.indexOf('?', tokenStart + 1);

        if (tokenStart == -1 || keyStart == -1) {
            throw new Exception("七牛云上传地址格式错误");
        }

        String baseUrl = uploadUrl.substring(0, tokenStart);
        token = uploadUrl.substring(tokenStart + 1, keyStart);
        key = uploadUrl.substring(keyStart + 1);

        HttpPost httpPost = new HttpPost(baseUrl);
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // 添加 token 和 key 字段
            builder.addTextBody("token", token);
            builder.addTextBody("key", key);

            // 添加文件字段
            if (file != null) {
                builder.addPart("file", new FileBody(file, ContentType.create(contentType), fileName));
            } else if (inputStream != null) {
                builder.addPart("file", new InputStreamBody(inputStream, ContentType.create(contentType), fileName));
            }

            httpPost.setEntity(builder.build());

            HttpResponse response = AdminHttpUtils.getHttpClient().execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            // 消耗响应体
            if (response.getEntity() != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }

            // 七牛云返回 200 表示成功
            if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED) {
                throw new Exception("文件上传到七牛云失败，HTTP状态码：" + statusCode);
            }

            IMResult<String> imResult = new IMResult<>();
            imResult.setCode(0);
            imResult.setResult(presignedUrl.downloadUrl);
            return imResult;
        } finally {
            httpPost.releaseConnection();
        }
    }

    /**
     * 上传到其他对象存储（type != 1）
     * 使用 PUT + 二进制流
     */
    private static IMResult<String> uploadToOther(OutputPresignedUploadUrl presignedUrl, File file,
                                           InputStream inputStream, String contentType) throws Exception {
        HttpPut httpPut = new HttpPut(presignedUrl.uploadUrl);
        try {
            // 设置请求实体
            if (file != null) {
                FileEntity fileEntity = new FileEntity(file);
                fileEntity.setContentType(contentType);
                httpPut.setEntity(fileEntity);
            } else if (inputStream != null) {
                InputStreamEntity streamEntity = new InputStreamEntity(inputStream);
                streamEntity.setContentType(contentType);
                httpPut.setEntity(streamEntity);
            }

            HttpResponse response = AdminHttpUtils.getHttpClient().execute(httpPut);
            int statusCode = response.getStatusLine().getStatusCode();

            // 消耗响应体，确保连接可以被复用
            if (response.getEntity() != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }

            if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED) {
                throw new Exception("文件上传失败，HTTP状态码：" + statusCode);
            }

            IMResult<String> imResult = new IMResult<>();
            imResult.setCode(0);
            imResult.setResult(presignedUrl.downloadUrl);
            return imResult;
        } finally {
            httpPut.releaseConnection();
        }
    }

    /**
     * 上传文件（通过输入流）
     * <p>
     * 流程：先调用getPresignedUploadUrl获取预签名上传地址，然后直接上传文件。
     * 上传成功后返回文件的下载地址等信息。
     * </p>
     *
     * @param inputStream 文件输入流
     * @param fileName    文件名
     * @return 上传结果，包含下载地址
     * @throws Exception 上传失败时抛出异常
     */
    public static IMResult<String> uploadFile(InputStream inputStream, String fileName) throws Exception {
        return uploadFile(inputStream, fileName, ProtoConstants.MessageMediaType.FILE, null);
    }
    /**
     * 上传文件（通过输入流）
     * <p>
     * 流程：先调用getPresignedUploadUrl获取预签名上传地址，然后直接上传文件。
     * 上传成功后返回文件的下载地址等信息。
     * </p>
     * <p>
     * <b>注意：</b>无论上传成功还是失败，此方法都会关闭输入流。
     * </p>
     *
     * @param inputStream 文件输入流（此方法会关闭该流）
     * @param fileName    文件名
     * @param mediaType   媒体类型，参考{@link cn.wildfirechat.proto.ProtoConstants.MessageMediaType}
     * @param contentType 文件Content-Type，例如 "image/jpeg", "application/octet-stream" 等；
     *                    如果为null或空，则根据文件名自动识别
     * @return 上传结果，包含下载地址
     * @throws Exception 上传失败时抛出异常
     */
    public static IMResult<String> uploadFile(InputStream inputStream, String fileName,
                                       int mediaType, String contentType) throws Exception {
        if (inputStream == null) {
            throw new IllegalArgumentException("输入流不能为空");
        }

        // 如果未指定Content-Type，根据文件名自动获取
        if (contentType == null || contentType.isEmpty()) {
            contentType = getContentTypeByFileName(fileName);
        }

        return doUploadFile(fileName, mediaType, contentType, null, inputStream);
        // 注意：正常流程下的流关闭在 uploadToQiniu 或 uploadToOther 的 finally 块中处理
    }
}
