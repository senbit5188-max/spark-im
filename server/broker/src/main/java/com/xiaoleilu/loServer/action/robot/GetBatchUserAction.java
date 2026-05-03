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
import cn.wildfirechat.pojos.InputOutputUserInfo;
import cn.wildfirechat.pojos.InputStringList;
import cn.wildfirechat.pojos.OutputUserInfoList;
import cn.wildfirechat.proto.WFCMessage;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

@Route(APIPath.Robot_User_Batch_Get_Infos)
@HttpMethod("POST")
public class GetBatchUserAction extends RobotAction {

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputStringList inputUserId = getRequestBody(request.getNettyRequest(), InputStringList.class);
            if (inputUserId != null && inputUserId.getList() != null && !inputUserId.getList().isEmpty()) {

                OutputUserInfoList res = new OutputUserInfoList();
                res.userInfos = new ArrayList<>();
                int mask = messagesStore.getRobotGetUserInfoMask();
                for (String userId : inputUserId.getList()) {
                    WFCMessage.User user = messagesStore.getUserInfo(userId);
                    if(user == null || StringUtil.isNullOrEmpty(user.getName())) {
                        continue;
                    }
                    InputOutputUserInfo inputOutputUserInfo = InputOutputUserInfo.fromPbUser(user);
                    if((mask & 1) == 0) {
                        inputOutputUserInfo.setName(null);
                    }
                    if((mask & 2) == 0) {
                        inputOutputUserInfo.setMobile(null);
                    }
                    if((mask & 4) == 0) {
                        inputOutputUserInfo.setEmail(null);
                    }
                    if((mask & 8) == 0) {
                        inputOutputUserInfo.setAddress(null);
                    }
                    if((mask & 16) == 0) {
                        inputOutputUserInfo.setCompany(null);
                    }
                    if((mask & 32) == 0) {
                        inputOutputUserInfo.setExtra(null);
                    }
                    if((mask & 64) == 0) {
                        inputOutputUserInfo.setUpdateDt(0);
                    }
                    if((mask & 128) == 0) {
                        inputOutputUserInfo.setGender(0);
                    }
                    if((mask & 256) == 0) {
                        inputOutputUserInfo.setSocial(null);
                    }
                    if((mask & 512) == 0) {
                        inputOutputUserInfo.setType(0);
                    }
                    res.userInfos.add(inputOutputUserInfo);
                }
                RestResult result = RestResult.ok(res);
                setResponseContent(result, response);
            } else {
                setResponseContent(RestResult.resultOf(ErrorCode.INVALID_PARAMETER), response);
            }
        }
        return true;
    }
}
