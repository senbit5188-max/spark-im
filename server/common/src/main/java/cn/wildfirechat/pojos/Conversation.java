/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.pojos;

/**
 * 会话类
 * <p>
 * 表示一个会话对象，包含会话类型、目标ID和线路信息。
 * 用于标识消息发送或接收的目标会话。
 * </p>
 */
public class Conversation {
    private int type;
    private String target;
    private int line;

    public Conversation() {
    }

    public Conversation(int type, String target, int line) {
        this.type = type;
        this.target = target;
        this.line = line;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
