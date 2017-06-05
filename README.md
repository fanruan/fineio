# FineIO


FineIO由两部分组成
  - Virtual File System （VFS）  虚拟文件分块
  - DirectMemory (DM)  基于Unsafe实现的堆外内存访问

Connector接口介绍
首先使用FineIO必须先实现Connector，如果没有Connector是无法使用的
下面给一个简单的示例 ：
    
    public static class MemoryConnector extends AbstractConnector {
 
        private Map<FileBlock, byte[]> map = new ConcurrentHashMap<FileBlock, byte[]>();
     
         
        public InputStream read(FileBlock file) {
            byte[] b = map.get(file);
            if(b != null){
                return new ByteArrayInputStream(b);
            }
            return null;
        }
     
         
        public void write(FileBlock file, InputStream inputStream) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] temp = new byte[1024];
            int len = 0;
            try {
                while((len = inputStream.read(temp) )> 0) {
                    byteArrayOutputStream.write(temp, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            map.put(file, byteArrayOutputStream.toByteArray());
        }
     
         
        public boolean delete(FileBlock block) {
            map.remove(block);
            return true;
        }
    }

这是一个MemoryConnector的实现,实际实现的是存储到内存中，继承AbstractConnector必须要实现的是三个接口方法：
    
        /**
         * 读整块的方法
         * @param file
         * @return
         */
        InputStream read(FileBlock file) throws IOException;

传FileBlock 作为参数的读方法 FileBlock提供了getBlockURI方法提供了这个块在Connector中存储的相对URI
根据这个相对的URI从实际文件中获取到输入流
    
        /**
         * 写整快的方法，可以保证通一个块不被同时写
         * @param file
         * @param inputStream
         */
        void write(FileBlock file, InputStream inputStream)  throws IOException;

参数也是作为相对路径的FileBlock 再加上输入流对象inputStream， Buffer已经封装了对写异常的处理，所以这里不需要处理inputStream发生异常的情况

        /**
         * 删除块
         * @param block
         * @return
         */
        boolean delete(FileBlock block);

删除相对URI的方法 参数也是作为相对路径的FileBlock
加两个可选接口

        /*
        输出byte[]
         */
        void write(FileBlock file, byte[] bytes)  throws IOException;

这个 AbstractConnector已经做了byte 2 stream的封装,可以不实现，如果直接继承Connector则必须实现，建议继承AbstractConnector来实现Connector类

        /**
         * 写文件时单个块的最大size偏移量
         * 用1L << value 表示单个块的最大尺寸，不建议超过28 （256M） 不建议小于22 (4M)
         * 可以根据磁盘的读写能力控制这个值的大小介于12-31之间
         * 不支持小于12 4K
         * 不支持大于31 2G
         * @return
         */
        byte getBlockOffset();

同理 这个方法AbstractConnector默认值是22 也就是4M，如果需要调整这个大小，可以重写这个值
可以看下Zip4JConnector的实现 实现了多磁盘目录的输出： Cube多目录多线程压缩输出原理
实现了Connector下面就是怎么来使用FineIO了，缓存部分DM不需要考虑，这是被封装好的，只需要知道怎么使用VFS
FineIO使用
创建虚拟文件
创建文件的时候需要传递三个参数 Connector 相对URI 和 MODEL类型 点击查看MODEL类型列表
示例：
创建一个long类型的写操作文件：

    IOFile<LongBuffer> file = FineIO.createIOFile(connector, uri, MODEL.WRITE_LONG);

创建一个byte类型的读操作文件：

    IOFile<ByteBuffer> file = FineIO.createIOFile(connector, uri, MODEL.READ_BYTE);

写虚拟文件
写操作文仅支持MODEL类型是WRITE或者EDIT开头的文件 READ类型的文件不支持写操作
连续写double值

    IOFile<DoubleBuffer> file = FineIO.createIOFile(connector, uri, MODEL.WRITE_DOUBLE);
     
    for(long i = 0;i < 1000000L;i++){
        FineIO.put(file, (double)i);
    }
    file.close();

或者

    IOFile<DoubleBuffer> file = FineIO.createIOFile(connector, uri, MODEL.WRITE_DOUBLE);
     
    for(long i = 0;i < 1000000L;i++){
        FineIO.put(file, i, (double)i);
    }
    file.close();

两个代码的效果是等同的，需要注意的是写文件操作仅支持连续写入，并且位置也是直接连续的
读虚拟文件
读double方法示例：

    IOFile<DoubleBuffer> file = FineIO.createIOFile(connector, uri, MODEL.READ_DOUBLE);
    double result = 0;
    for(long i = 0; i < rowCount; i++){
        result += FineIO.getDouble(file, i);
    }
    file.close();

注意如果读越界，会抛出BufferIndexOutOfBoundsException的异常
编辑虚拟文件
按double方式编辑一个文件示例：

    IOFile<DoubleBuffer> file = FineIO.createIOFile(connector, uri, MODEL.EDIT_DOUBLE);
    double result = 0;
    for(long i = 0; i < rowCount; i++){
        result += FineIO.getDouble(file, i);
    }
    for(long i = rowCount/2; i < rowCount; i++){
        FineIO.put(file, i, FineIO.getDouble(file, i - rowCount/2)*2);
    }
    file.close();

注意编辑文件的方法支持随机写入和随机读取，但是内存不足情况下容易导致IO频繁进出，Cube场景下不是特别需要
删除文件
删除文件示例：

    IOFile<DoubleBuffer> file = FineIO.createIOFile(connector, uri, MODEL.READ_DOUBLE);
    file.close();
    file.delete();

注意：删除文件需要创建一个读或者编辑的文件，写文件的方法不能正确删除
OK FineIO 用法就是这么简单，还想咋地
此外写文件写完需要close文件，读文件则在完全不需要读的情况下close，不需要每次创建，读文件方法支持多线程访问
实现不同的Connector即可对接不同的存储，想怎么存都可以，实现HDFS就存到HDFS，实现redis就存到redis
