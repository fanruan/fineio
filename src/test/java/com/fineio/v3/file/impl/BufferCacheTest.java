package com.fineio.v3.file.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.BufferAcquireFailedException;
import com.fineio.v3.buffer.DirectBuffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.concurrent.Callable;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * This class created on 2020/3/2
 *
 * @author Kuifang.Liu
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class BufferCacheTest {

    @Before
    public void setUp() {
        BufferCache.get().start();
    }

    @Test
    public void testPut() {
        FileBlock block = new FileBlock("/testPath/testBlock");
        DirectBuffer buffer = mock(DirectBuffer.class);
        BufferCache.get().put(block, buffer);
        Assert.assertEquals(BufferCache.get().get(block, new Callable<DirectBuffer>() {
            @Override
            public DirectBuffer call() throws Exception {
                return null;
            }
        }), buffer);
    }

    @Test
    public void testGet() {
        FileBlock block = new FileBlock("/testPath/testBlock");
        final DirectBuffer buffer = mock(DirectBuffer.class);

        //buffer absent
        DirectBuffer result = BufferCache.get().get(block, new Callable<DirectBuffer>() {
            @Override
            public DirectBuffer call() throws Exception {
                return buffer;
            }
        });
        Assert.assertEquals(result, buffer);

        BufferCache.get().invalidate(block);
        try {
            BufferCache.get().get(block, new Callable<DirectBuffer>() {
                @Override
                public DirectBuffer call() throws Exception {
                    return null;
                }
            });
            fail();
        } catch (BufferAcquireFailedException ignore) {
        }


        // buffer exist
        BufferCache.get().put(block, buffer);
        Assert.assertEquals(BufferCache.get().get(block, new Callable<DirectBuffer>() {
            @Override
            public DirectBuffer call() throws Exception {
                return null;
            }
        }), buffer);
    }
}
