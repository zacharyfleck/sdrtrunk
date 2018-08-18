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
package io.github.dsheirer.gui.message;

import javafx.scene.layout.BorderPane;

import java.io.File;

public class MessageViewerDesktop extends BorderPane
{
    private BinaryPlaybackController mPlaybackController;
    private AbstractMessagePane mMessagePane = MessagePaneFactory.getDefaultPane();

    public MessageViewerDesktop()
    {
        setBottom(getPlaybackController());
        setCenter(getMessagePane());
        getPlaybackController().addListener(getMessagePane());
    }

    public void load(File file)
    {
        close();
        getPlaybackController().load(file);
    }

    public void setProtocol(MessageProtocol messageProtocol)
    {
        setMessagePane(MessagePaneFactory.getMessagePane(messageProtocol));
    }

    private void setMessagePane(AbstractMessagePane decoderPane)
    {
        getPlaybackController().removeListener(getMessagePane());
        getChildren().remove(getMessagePane());
        mMessagePane = decoderPane;
        setCenter(getMessagePane());
        getPlaybackController().addListener(getMessagePane());
    }

    /**
     * Message pane
     */
    public AbstractMessagePane getMessagePane()
    {
        if(mMessagePane == null)
        {
            mMessagePane = MessagePaneFactory.getDefaultPane();
        }

        return mMessagePane;
    }

    public void close()
    {
        getPlaybackController().close();
    }

    private BinaryPlaybackController getPlaybackController()
    {
        if(mPlaybackController == null)
        {
            mPlaybackController = new BinaryPlaybackController();
        }

        return mPlaybackController;
    }
}
