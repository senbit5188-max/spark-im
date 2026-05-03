/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.pojos;


/**
 * 预签名上传地址
 * <p>
 * 封装预签名上传地址，包括：
 * <ul>
 * <li>上传地址</li>
 * <li>备选上传地址</li>
 * <li>下载地址</li>
 * <li>类型</li>
 * </ul>
 * </p>
 */
public class OutputPresignedUploadUrl {
    public String uploadUrl;
    public String backupUploadUrl;
    public String downloadUrl;
    public int type;
}
