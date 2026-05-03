/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.xiaoleilu.loServer.action.admin;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.proto.WFCMessage;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;

import java.util.*;

@Route(APIPath.User_Get_All_Robots)
@HttpMethod("POST")
public class GetAllRobotAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputGetUserList input = getRequestBody(request.getNettyRequest(), InputGetUserList.class);
            if (input != null && input.count > 0 && input.offset >= 0) {
                List<WFCMessage.Robot> robots = messagesStore.getRobotInfoList(input.count, input.offset);

                OutputGetRobotList outputGetUserList = new OutputGetRobotList();
                outputGetUserList.robotInfoList = new ArrayList<>();
                robots.forEach(robotData -> {
                    WFCMessage.User userInfoData = messagesStore.getUserInfo(robotData.getUid());
                    if (userInfoData != null && !StringUtil.isNullOrEmpty(userInfoData.getName()) && userInfoData.getDeleted() ==0) {
                        OutputRobot outputRobot = new OutputRobot();
                        outputRobot.fromUser(userInfoData);
                        outputRobot.fromRobot(robotData, true);
                        outputGetUserList.robotInfoList.add(outputRobot);
                    }
                });

                RestResult result = RestResult.ok(outputGetUserList);
                setResponseContent(result, response);
            } else {
                setResponseContent(RestResult.resultOf(ErrorCode.INVALID_PARAMETER), response);
            }

        }
        return true;
    }
}
