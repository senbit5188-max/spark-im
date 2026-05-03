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
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Route(APIPath.User_Batch_Get_Infos)
@HttpMethod("POST")
public class GetBatchUserAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputStringList inputUserId = getRequestBody(request.getNettyRequest(), InputStringList.class);
            if (inputUserId != null && inputUserId.getList() != null && !inputUserId.getList().isEmpty()) {

                OutputUserInfoList res = new OutputUserInfoList();
                res.userInfos = new ArrayList<>();
                for (String userId : inputUserId.getList()) {
                    WFCMessage.User user = messagesStore.getUserInfo(userId);
                    if(user == null || StringUtil.isNullOrEmpty(user.getName())) {
                        continue;
                    }
                    InputOutputUserInfo inputOutputUserInfo = InputOutputUserInfo.fromPbUser(user);
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
