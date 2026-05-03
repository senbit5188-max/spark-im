/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.pojos;

import java.util.ArrayList;
import java.util.List;

public class FilesPojo {
    public int total;
    public List<FilePojo> files;
    public static class FilePojo {
        public long messageId;
        public String sender;
        public int conversationType;
        public String target;
        public int line;
        public String name;
        public String url;
        public int size;
        public int downloadCount;
        public long timestamp;
    }
}

