package com.fineio.transfer;

import com.fineio.accessor.Block;
import com.fineio.base.Bits;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.FileConstants;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.v3.Connector;
import com.fineio.v3.file.DirectoryBlock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author yee
 * @date 2019-08-08
 */
public class Transfer {
    public static void upgrade(Connector from, TransferProgressListener listener) throws IOException {
        transfer(from, null, listener);
    }

    public static void upgrade(Connector from, Connector to, TransferProgressListener listener) throws IOException {
        final Block list = from.list("/");
        upgradeBlock(from, to, list, listener);
    }

    public static void transfer(Connector from, Connector to, TransferProgressListener listener) throws IOException {
        final Block list = from.list("/");
        transferBlock(from, to, list, listener);
    }

    public static void downgrade(Connector from, TransferProgressListener listener) throws IOException {
        downgrade(from, null, listener);
    }

    public static void downgrade(Connector from, Connector to, TransferProgressListener listener) throws IOException {
        downgradeBlock(from, to, from.list("/"), listener);
    }

    private static void downgradeBlock(Connector from, Connector to, Block block, TransferProgressListener listener) throws IOException {
        if (block instanceof DirectoryBlock) {
            DirectoryBlock directoryBlock = (DirectoryBlock) block;
            final List<Block> files = directoryBlock.getFiles();
            final int size = files.size();
            for (int i = 0; i < size; i++) {
                final Block file = files.get(i);
                downgradeBlock(from, to, file, listener);
                listener.progress((i + 1) / size, file.getPath());
            }
        } else {
            if (FileConstants.META.equals(block.getName())) {
                Connector target = null == to ? from : to;
                final FileBlock file = (FileBlock) block;
                FileBlock head = new FileBlock(file.getDir(), FileConstants.HEAD);
                try (final InputStream read = from.read(file)) {
                    byte[] bytes = new byte[5];
                    read.read(bytes);
                    final ByteBuffer wrap = ByteBuffer.wrap(bytes);
                    byte offset = wrap.get();
                    final int step = 1 << offset;
                    final int byteCount = wrap.getInt();
                    int blockSize = byteCount / step;
                    blockSize += byteCount % step > 0 ? 1 : 0;
                    bytes = new byte[MemoryConstants.STEP_LONG + 1];
                    Bits.putInt(bytes, 0, blockSize);
                    bytes[MemoryConstants.STEP_LONG] = offset;
                    final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                    target.write(head, inputStream);
                    inputStream.close();
                    target.delete(block);
                }
            } else {
                transfer(from, to, (FileBlock) block);
            }
        }
    }

    private static void transferBlock(Connector from, Connector to, Block block, TransferProgressListener listener) throws IOException {
        if (block instanceof DirectoryBlock) {
            DirectoryBlock directoryBlock = (DirectoryBlock) block;
            final List<Block> files = directoryBlock.getFiles();
            final int size = files.size();
            for (int i = 0; i < size; i++) {
                final Block file = files.get(i);
                transferBlock(from, to, file, listener);
                listener.progress((i + 1) / size, file.getPath());
            }
        } else {
            transfer(from, to, (FileBlock) block);
        }
    }

    private static void upgradeBlock(Connector from, Connector to, Block block, TransferProgressListener listener) throws IOException {
        if (block instanceof DirectoryBlock) {
            DirectoryBlock directoryBlock = (DirectoryBlock) block;
            final List<Block> files = directoryBlock.getFiles();
            final int size = files.size();
            for (int i = 0; i < size; i++) {
                final Block file = files.get(i);
                upgradeBlock(from, to, file, listener);
                listener.progress((i + 1) / size, file.getPath());
            }
        } else {
            if (FileConstants.HEAD.equals(block.getName())) {
                Connector target = null == to ? from : to;
                final FileBlock file = (FileBlock) block;
                FileBlock meta = new FileBlock(file.getDir(), FileConstants.META);
                try (final InputStream read = from.read(file)) {
                    byte[] header = new byte[9];
                    read.read(header);
                    int p = 0;
                    final int blocks = Bits.getInt(header, p);
                    //先空个long的位置
                    p += MemoryConstants.STEP_LONG;
                    final byte offset = header[p];
                    final int lastPos = blocks * (1 << offset);
                    final ByteBuffer buf = ByteBuffer.allocate(5);
                    buf.put(offset);
                    buf.putInt(lastPos);
                    final ByteArrayInputStream inputStream = new ByteArrayInputStream(buf.array());
                    target.write(meta, inputStream);
                    inputStream.close();
                    target.delete(block);
                }
            } else {
                transfer(from, to, (FileBlock) block);
            }
        }
    }

    private static void transfer(Connector from, Connector to, FileBlock block) throws IOException {
        if (null != from && null != to && null != block) {
            try (final InputStream read = from.read(block)) {
                to.write(block, read);
            }
        }
    }

//    public static void main(String[] args) throws IOException {
//        downgrade(LZ4Connector.newInstance("/Users/yee/lz4/testInt"), FileConnector.newInstance("/Users/yee/normal/testInt"), (a, b) -> {});
//    }
}
