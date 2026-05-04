/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.xiaoleilu.loServer.action.robot;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.proto.WFCMessage;
import com.google.gson.Gson;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import cn.wildfirechat.pojos.InputOutputUserInfo;
import cn.wildfirechat.pojos.InputGetUserInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import cn.wildfirechat.common.ErrorCode;

@Route(APIPath.Robot_User_Info)
@HttpMethod("POST")
public class GetUserAction extends RobotAction {

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputGetUserInfo inputUserId = getRequestBody(request.getNettyRequest(), InputGetUserInfo.class);
            if (inputUserId != null
                && (!StringUtil.isNullOrEmpty(inputUserId.getUserId()) || !StringUtil.isNullOrEmpty(inputUserId.getName()) || !StringUtil.isNullOrEmpty(inputUserId.getMobile()))) {

                WFCMessage.User user = null;
                if(!StringUtil.isNullOrEmpty(inputUserId.getUserId())) {
                    user = messagesStore.getUserInfo(inputUserId.getUserId());
                } else if(!StringUtil.isNullOrEmpty(inputUserId.getName())) {
                    user = messagesStore.getUserInfoByName(inputUserId.getName());
                } else if(!StringUtil.isNullOrEmpty(inputUserId.getMobile())) {
                    user = messagesStore.getUserInfoByMobile(inputUserId.getMobile());
                }

                RestResult result;
                if (user == null || user.getDeleted() > 0) {
                    result = RestResult.resultOf(ErrorCode.ERROR_CODE_NOT_EXIST);
                } else {
                    InputOutputUserInfo outputUserInfo = InputOutputUserInfo.fromPbUser(user);
                    int mask = messagesStore.getRobotGetUserInfoMask();
                    if((mask & 1) == 0) {
                        outputUserInfo.setName(null);
                    }
                    if((mask & 2) == 0) {
                        outputUserInfo.setMobile(null);
                    }
                    if((mask & 4) == 0) {
                        outputUserInfo.setEmail(null);
                    }
                    if((mask & 8) == 0) {
                        outputUserInfo.setAddress(null);
                    }
                    if((mask & 16) == 0) {
                        outputUserInfo.setCompany(null);
                    }
                    if((mask & 32) == 0) {
                        outputUserInfo.setExtra(null);
                    }
                    if((mask & 64) == 0) {
                        outputUserInfo.setUpdateDt(0);
                    }
                    if((mask & 128) == 0) {
                        outputUserInfo.setGender(0);
                    }
                    if((mask & 256) == 0) {
                        outputUserInfo.setSocial(null);
                    }
                    if((mask & 512) == 0) {
                        outputUserInfo.setType(0);
                    }
                    result = RestResult.ok(outputUserInfo);
                }
                setResponseContent(result, response);
            } else {
                setResponseContent(RestResult.resultOf(ErrorCode.INVALID_PARAMETER), response);
            }

        }
        return true;
    }
}
