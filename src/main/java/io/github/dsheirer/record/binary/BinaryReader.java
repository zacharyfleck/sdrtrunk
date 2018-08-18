/*******************************************************************************
 * sdr-trunk
 * Copyright (C) 2014-2018 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by  the Free Software Foundation, either version 3 of the License, or  (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful,  but WITHOUT ANY WARRANTY; without even the implied
 * warranty of  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License  along with this program.
 * If not, see <http://www.gnu.org/licenses/>
 *
 ******************************************************************************/
package io.github.dsheirer.record.binary;

import io.github.dsheirer.sample.Broadcaster;
import io.github.dsheirer.sample.Listener;
import io.github.dsheirer.sample.buffer.ReusableByteBuffer;
import io.github.dsheirer.sample.buffer.ReusableByteBufferQueue;
import io.github.dsheirer.source.IFrameLocationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class BinaryReader
{
    private final static Logger mLog = LoggerFactory.getLogger(BinaryReader.class);

    private ReusableByteBufferQueue mBufferQueue = new ReusableByteBufferQueue("Binary Reader");
    private Broadcaster<ReusableByteBuffer> mBufferBroadcaster = new Broadcaster<>();
    private IFrameLocationListener mFrameLocationListener;
    private Path mCurrentPath;
    private int mCurrentOffset;
    private ReadableByteChannel mReadableByteChannel;

    public BinaryReader()
    {
    }

    public void setPath(Path path) throws IOException
    {
        close();

        mCurrentPath = path;
        mCurrentOffset = 0;
        mReadableByteChannel = Files.newByteChannel(mCurrentPath, EnumSet.of(StandardOpenOption.READ));
        mLog.info("Binary (bitstream) recording opened: " + mCurrentPath.toString());
    }

    public void close()
    {
        if(mReadableByteChannel != null)
        {
            try
            {
                mReadableByteChannel.close();
            }
            catch(IOException ioe)
            {
                mLog.error("Error closing binary recording file: " + (mCurrentPath != null ? mCurrentPath.toString() : "(null)"));
            }

            mReadableByteChannel = null;
            mCurrentPath = null;
        }
    }

    /**
     * Reads a new buffer from the recording file of byte count length.
     * @param byteCount to read into a buffer
     */
    public void next(int byteCount) throws IOException
    {
        if(mReadableByteChannel != null)
        {
            ReusableByteBuffer buffer = mBufferQueue.getBuffer(byteCount);
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer.getBytes());
            mCurrentOffset += mReadableByteChannel.read(byteBuffer);
            if(mFrameLocationListener != null)
            {
                mFrameLocationListener.frameLocationUpdated(mCurrentOffset);
            }

            mBufferBroadcaster.broadcast(buffer);
        }
    }

    /**
     * Adds the listener to receive byte buffers
     */
    public void addListener(Listener<ReusableByteBuffer> listener)
    {
        mBufferBroadcaster.addListener(listener);
    }

    /**
     * Removes the listener from receiving byte buffers
     */
    public void removeListener(Listener<ReusableByteBuffer> listener)
    {
        mBufferBroadcaster.removeListener(listener);
    }

    /**
     * Sets the listener to receive recording frame location updates
     */
    public void setFrameLocationListener(IFrameLocationListener listener)
    {
        mFrameLocationListener = listener;
    }

    /**
     * Clears the listener
     */
    public void removeFrameLocationListener()
    {
        mFrameLocationListener = null;
    }
}
