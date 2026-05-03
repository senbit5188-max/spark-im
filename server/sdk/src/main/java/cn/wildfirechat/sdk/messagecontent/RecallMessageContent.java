package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import org.apache.http.util.TextUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 撤回消息内容类
 * <p>
 * 表示消息撤回通知，包含被撤回消息的原始信息。
 * </p>
 */
public class RecallMessageContent extends MessageContent {
    private long messageId;
    private String operatorId;


    private String originalSender;

    private int originalContentType;
    private String originalSearchableContent;
    private String originalContent;
    private String originalExtra;
    private byte[] originalBinaryContent;
    private int originalMediaType;
    private String originalMediaUrl;
    private long originalMessageTimestamp;

    //必须有个空的构造函数
    public RecallMessageContent() {
    }

    public long getMessageId() {
        return messageId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public int getOriginalContentType() {
        return originalContentType;
    }

    public String getOriginalSearchableContent() {
        return originalSearchableContent;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public String getOriginalExtra() {
        return originalExtra;
    }

    public long getOriginalMessageTimestamp() {
        return originalMessageTimestamp;
    }

    public byte[] getOriginalBinaryContent() {
        return originalBinaryContent;
    }

    public int getOriginalMediaType() {
        return originalMediaType;
    }

    public String getOriginalMediaUrl() {
        return originalMediaUrl;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Recall;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist;
    }

    @Override
    public MessagePayload encode() {
        throw new RuntimeException("Recall message cannot encode");
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decode(payload);
        operatorId = payload.getContent();
        messageId = Long.parseLong(new String(Base64.getDecoder().decode(payload.getBase64edData()), StandardCharsets.UTF_8));
        if(!TextUtils.isEmpty(payload.getExtra())) {
            try {
                JSONObject dictionary = (JSONObject) new JSONParser().parse(payload.getExtra());
                originalSender = (String) dictionary.get("s");
                if (dictionary.containsKey("t")) {
                    originalContentType = ((Number) dictionary.get("t")).intValue();
                }
                originalSearchableContent = (String) dictionary.get("sc");
                originalContent = (String) dictionary.get("c");
                originalExtra = (String) dictionary.get("e");
                if (dictionary.containsKey("ts")) {
                    originalMessageTimestamp = ((Number) dictionary.get("ts")).longValue();
                }
                if (dictionary.containsKey("mt")) {
                    originalMediaType = ((Number) dictionary.get("mt")).intValue();
                }
                originalMediaUrl = (String) dictionary.get("mu");
                if (dictionary.containsKey("mb")) {
                    originalBinaryContent = Base64.getDecoder().decode((String) dictionary.get("mb"));
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
