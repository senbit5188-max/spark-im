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
import cn.wildfirechat.pojos.InputRobotId;
import cn.wildfirechat.pojos.InputUserId;
import cn.wildfirechat.pojos.OutputRobot;
import cn.wildfirechat.pojos.OutputStringList;
import cn.wildfirechat.proto.WFCMessage;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;

@Route(APIPath.User_Get_User_Robots)
@HttpMethod("POST")
public class GetUserRobotsAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputUserId inputUserId = getRequestBody(request.getNettyRequest(), InputUserId.class);
            if (inputUserId != null
                && (!StringUtil.isNullOrEmpty(inputUserId.getUserId()))) {
                RestResult result = RestResult.ok(new OutputStringList(messagesStore.getUserRobotIds(inputUserId.getUserId())));
                setResponseContent(result, response);
            } else {
                setResponseContent(RestResult.resultOf(ErrorCode.INVALID_PARAMETER), response);
            }
        }
        return true;
    }
}
