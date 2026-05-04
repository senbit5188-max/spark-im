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
import cn.wildfirechat.pojos.InputUpdateFriendExtra;
import cn.wildfirechat.proto.WFCMessage;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;
import win.liyufan.im.IMTopic;

@Route(APIPath.Friend_Set_Extra)
@HttpMethod("POST")
public class FriendExtraPutAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputUpdateFriendExtra input = getRequestBody(request.getNettyRequest(), InputUpdateFriendExtra.class);
            if (input != null && !StringUtil.isNullOrEmpty(input.getOperator()) && !StringUtil.isNullOrEmpty(input.getTargetId())) {
                sendApiMessage(response, input.getOperator(), IMTopic.SetFriendExtraTopic, WFCMessage.StringPair.newBuilder().setKey(input.getTargetId()).setValue(input.getExtra()==null?"":input.getExtra()).build().toByteArray(), result -> {
                    ByteBuf byteBuf = Unpooled.buffer();
                    byteBuf.writeBytes(result);
                    ErrorCode errorCode = ErrorCode.fromCode(byteBuf.readByte());
                    return new Result(errorCode);
                });
                return false;
            } else {
                setResponseContent(RestResult.resultOf(ErrorCode.INVALID_PARAMETER), response);
            }
        }
        return true;
    }
}
