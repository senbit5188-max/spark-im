/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.pojos;


/**
 * 请求预签名上传地址
 * <p>
 * 请求封装预签名上传地址，包括：
 * <ul>
 * <li>文件名</li>
 * <li>文件类型</li>
 * <li>媒体类型</li>
 * </ul>
 * </p>
 */
public class InputGetPresignedUploadUrl {
    public String fileName;
    public String contentType;
    public int mediaType;
}
