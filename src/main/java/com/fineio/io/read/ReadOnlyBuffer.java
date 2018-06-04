package com.fineio.io.read;

import com.fineio.exception.BlockNotFoundException;
import com.fineio.io.Buffer;
import com.fineio.io.base.BufferKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2018/5/30
 */
public interface ReadOnlyBuffer extends Buffer {
    boolean isLoad();

    void access();

    void clear();

    abstract class Accessor {
        protected byte[] bytes;
        protected int off;
        protected BufferKey bufferKey;

        public Accessor(BufferKey bufferKey) {
            this.bufferKey = bufferKey;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public int getOff() {
            return off;
        }

        public abstract void invoke() throws BlockNotFoundException;
    }

    final class VirtualAccessor extends Accessor {
        private int maxByteLen;

        public VirtualAccessor(BufferKey bufferKey, int maxByteLen) {
            super(bufferKey);
            this.maxByteLen = maxByteLen;
        }

        public void invoke() {
            bytes = new byte[maxByteLen];
            off = 0;
            int len = 0;
            InputStream is = null;
            try {
                is = bufferKey.getConnector().read(bufferKey.getBlock());
                if (null == is) {
                    throw new BlockNotFoundException("block:" + bufferKey.getBlock().toString() + " not found!");
                }
                while ((len = is.read(bytes, off, maxByteLen - off)) > 0) {
                    off += len;
                }
            } catch (Throwable e) {
                throw new BlockNotFoundException("block:" + bufferKey.getBlock().toString() + " not found!", e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }


    final class DirectAccessor extends Accessor {
        public DirectAccessor(BufferKey bufferKey) {
            super(bufferKey);
        }

        public void invoke() {
            byte[] b = new byte[1024];
            int len = 0;
            InputStream is = null;
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            try {
                is = bufferKey.getConnector().read(bufferKey.getBlock());
                while ((len = is.read(b, 0, b.length)) > 0) {
                    ba.write(b, 0, len);
                }
                bytes = ba.toByteArray();
                off = bytes.length;
            } catch (Throwable e) {
                //文件不存在新建一个不loaddata了
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}
