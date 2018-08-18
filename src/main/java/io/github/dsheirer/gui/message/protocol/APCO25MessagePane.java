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
package io.github.dsheirer.gui.message.protocol;

import io.github.dsheirer.gui.message.AbstractMessagePane;
import io.github.dsheirer.sample.buffer.ReusableByteBuffer;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APCO25MessagePane extends AbstractMessagePane<ReusableByteBuffer>
{
    private final static Logger mLog = LoggerFactory.getLogger(APCO25MessagePane.class);

    public APCO25MessagePane()
    {
        getChildren().add(new Text("APCO-25"));
    }

    @Override
    public void receive(ReusableByteBuffer reusableByteBuffer)
    {
        mLog.debug("Received buffer - length: " + reusableByteBuffer.getBytes().length);
        reusableByteBuffer.decrementUserCount();
    }
}
