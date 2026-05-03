package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

/**
 * 会议管理类
 * <p>
 * 提供音视频会议管理相关的功能，包括：
 * <ul>
 * <li>会议列表查询</li>
 * <li>会议创建和销毁</li>
 * <li>会议参与者管理</li>
 * <li>会议录制控制</li>
 * <li>RTP转发管理</li>
 * </ul>
 * </p>
 */
public class ConferenceAdmin {
    /**
     * 获取会议列表（分页）
     * @param count 每页数量
     * @param offset 偏移量
     * @return 会议信息列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<PojoConferenceInfoList> listConferences(int count, int offset) throws Exception {
        String path = APIPath.Conference_List;
        InputCountOffset inputCountOffset = new InputCountOffset();
        inputCountOffset.count = count;
        inputCountOffset.offset = offset;
        return AdminHttpUtils.httpJsonPost(path, inputCountOffset, PojoConferenceInfoList.class);
    }

    /**
     * 检查会议是否存在
     * @param conferenceId 会议ID
     * @return true-存在，false-不存在
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Boolean> existsConferences(String conferenceId) throws Exception {
        String path = APIPath.Conference_Exist;
        PojoConferenceRoomId data = new PojoConferenceRoomId(conferenceId, false);
        return AdminHttpUtils.httpJsonPost(path, data, Boolean.class);
    }

    /**
     * 获取会议参与者列表
     * @param roomId 房间ID
     * @param advance 是否使用高级模式
     * @return 参与者列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<PojoConferenceParticipantList> listParticipants(String roomId, boolean advance) throws Exception {
        String path = APIPath.Conference_List_Participant;
        PojoConferenceRoomId data = new PojoConferenceRoomId(roomId, advance);
        return AdminHttpUtils.httpJsonPost(path, data, PojoConferenceParticipantList.class);
    }

    /**
     * 创建会议房间
     * @param roomId 房间ID
     * @param description 描述
     * @param pin 房间密码
     * @param maxPublisher 最大推流数
     * @param advance 是否使用高级模式
     * @param bitrate 比特率
     * @param recording 是否录制
     * @param permanent 是否永久房间
     * @return 创建结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> createRoom(String roomId, String description, String pin, int maxPublisher, boolean advance, int bitrate, boolean recording, boolean permanent) throws Exception {
        String path = APIPath.Conference_Create;
        PojoConferenceCreate create = new PojoConferenceCreate();
        create.roomId = roomId;
        create.description = description;
        create.pin = pin;
        create.max_publishers = maxPublisher;
        create.advance = advance;
        create.bitrate = bitrate;
        create.recording = recording;
        create.permanent = permanent;
        return AdminHttpUtils.httpJsonPost(path, create, Void.class);
    }

    /**
     * 启用或禁用会议录制
     * @param roomId 房间ID
     * @param advance 是否使用高级模式
     * @param recording true-启用录制，false-禁用录制
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> enableRecording(String roomId, boolean advance, boolean recording) throws Exception {
        String path = APIPath.Conference_Recording;
        PojoConferenceRecording create = new PojoConferenceRecording();
        create.roomId = roomId;
        create.recording = recording;
        create.advance = advance;
        return AdminHttpUtils.httpJsonPost(path, create, Void.class);
    }

    /**
     * 销毁会议房间
     * @param roomId 房间ID
     * @param advance 是否使用高级模式
     * @return 销毁结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> destroy(String roomId, boolean advance) throws Exception {
        String path = APIPath.Conference_Destroy;
        PojoConferenceRoomId conferenceRoomId = new PojoConferenceRoomId(roomId, advance);
        return AdminHttpUtils.httpJsonPost(path, conferenceRoomId, Void.class);
    }

    /**
     * RTP转发
     * @param roomId 房间ID
     * @param userId 用户ID
     * @param rtpHost RTP主机地址
     * @param audioPort 音频端口
     * @param audioPt 音频Payload类型
     * @param audioSSRC 音频SSRC
     * @param videoPort 视频端口
     * @param videoPt 视频Payload类型
     * @param videoSSRC 视频SSRC
     * @return 转发结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> rtpForward(String roomId, String userId, String rtpHost, int audioPort, int audioPt, long audioSSRC, int videoPort, int videoPt, long videoSSRC) throws Exception {
        String path = APIPath.Conference_Rtp_Forward;
        PojoConferenceRtpForwardReq req = new PojoConferenceRtpForwardReq(roomId, userId, rtpHost, audioPort, audioPt, audioSSRC, videoPort, videoPt, videoSSRC);
        return AdminHttpUtils.httpJsonPost(path, req, Void.class);
    }

    /**
     * 停止RTP转发
     * @param roomId 房间ID
     * @param userId 用户ID
     * @param streamId 流ID
     * @return 停止结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> stopRtpForward(String roomId, String userId, long streamId) throws Exception {
        String path = APIPath.Conference_Stop_Rtp_Forward;
        PojoConferenceStopRtpForwardReq req = new PojoConferenceStopRtpForwardReq(roomId, userId, streamId);
        return AdminHttpUtils.httpJsonPost(path, req, Void.class);
    }

    /**
     * 获取RTP转发列表
     * @param roomId 房间ID
     * @return RTP转发列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<PojoConferenceRtpForwarders> listRtpForwarders(String roomId) throws Exception {
        String path = APIPath.Conference_List_Rtp_Forward;
        PojoConferenceRoomId req = new PojoConferenceRoomId();
        req.roomId = roomId;
        return AdminHttpUtils.httpJsonPost(path, req, PojoConferenceRtpForwarders.class);
    }
}
