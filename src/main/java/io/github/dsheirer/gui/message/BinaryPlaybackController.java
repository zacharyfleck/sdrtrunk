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

import io.github.dsheirer.gui.instrument.decoder.AbstractDecoderPane;
import io.github.dsheirer.record.binary.BinaryReader;
import io.github.dsheirer.sample.Listener;
import io.github.dsheirer.sample.buffer.ReusableByteBuffer;
import io.github.dsheirer.source.IFrameLocationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class BinaryPlaybackController extends HBox implements IFrameLocationListener
{
    private final static Logger mLog = LoggerFactory.getLogger(BinaryPlaybackController.class);

    private Button mRewindButton;
    private TextField mPlaybackPositionText;
    private Button mPlay100Button;
    private Button mPlay500Button;
    private Button mPlay1000Button;
    private Button mPlay2000Button;
    private Button mPlay5000Button;
    private HBox mControlsBox;
    private Label mFileLabel;
    private EventHandler<ActionEvent> mPlayEventHandler;
    private AbstractDecoderPane mSampleRateListener;
    private BinaryReader mBinaryReader = new BinaryReader();

    public BinaryPlaybackController()
    {
        super(10);
        mBinaryReader.setFrameLocationListener(this);

        getChildren().addAll(getControlsBox(), getFileLabel());

        disableControls();
    }

    public void load(File file)
    {
        disableControls();

        if(file != null && file.isFile())
        {
            try
            {
                mBinaryReader.setPath(file.toPath());
            }
            catch(IOException ioe)
            {
                mLog.error("Error opening file [" + (file != null ? file.getAbsolutePath() : "null") + "]", ioe);

                Alert alert = new Alert(Alert.AlertType.ERROR, "There was an error opening the file",
                    ButtonType.OK);
                alert.show();
                return;
            }

            enableControls();

            getFileLabel().setText(file.getName());
        }
    }

    public void close()
    {
        mBinaryReader.close();
        disableControls();
    }

    /**
     * Adds listener to receive complex buffers from this playback
     */
    public void addListener(Listener<ReusableByteBuffer> listener)
    {
        mBinaryReader.addListener(listener);
    }

    /**
     * Removes the listener from receiving complex buffers from this playback
     */
    public void removeListener(Listener<ReusableByteBuffer> listener)
    {
        mBinaryReader.removeListener(listener);
    }

    /**
     * Sets all of the playback controls to the specified enabled state.
     */
    private void disableControls()
    {
        getRewindButton().setDisable(true);
        getPlaybackPositionText().setDisable(true);
        getPlay100Button().setDisable(true);
        getPlay1000Button().setDisable(true);
        getPlay2000Button().setDisable(true);
        getPlay5000Button().setDisable(true);
    }

    /**
     * Sets all of the playback controls to the specified enabled state.
     */
    private void enableControls()
    {
        getRewindButton().setDisable(false);
        getPlaybackPositionText().setDisable(false);
        getPlay100Button().setDisable(false);
        getPlay1000Button().setDisable(false);
        getPlay2000Button().setDisable(false);
        getPlay5000Button().setDisable(false);
    }

    private HBox getControlsBox()
    {
        if(mControlsBox == null)
        {
            mControlsBox = new HBox();

            mControlsBox.getChildren().addAll(getRewindButton(), getPlaybackPositionText(), getPlay100Button(),
                getPlay500Button(), getPlay1000Button(), getPlay2000Button(), getPlay5000Button());
        }

        return mControlsBox;
    }


    private  Button getRewindButton()
    {
        if(mRewindButton == null)
        {
            mRewindButton = new Button();
            mRewindButton.setGraphic(new FontIcon(FontAwesome.FAST_BACKWARD));
        }

        return mRewindButton;
    }

    private  Button getPlay100Button()
    {
        if(mPlay100Button == null)
        {
            mPlay100Button = new Button("100");
            mPlay100Button.setUserData(100);
            mPlay100Button.setOnAction(getPlaybackEventHandler());
            mPlay100Button.setGraphic(new FontIcon(FontAwesome.FAST_FORWARD));
        }

        return mPlay100Button;
    }

    private  Button getPlay500Button()
    {
        if(mPlay500Button == null)
        {
            mPlay500Button = new Button("500");
            mPlay500Button.setUserData(500);
            mPlay500Button.setOnAction(getPlaybackEventHandler());
            mPlay500Button.setGraphic(new FontIcon(FontAwesome.FAST_FORWARD));
        }

        return mPlay500Button;
    }

    private  Button getPlay1000Button()
    {
        if(mPlay1000Button == null)
        {
            mPlay1000Button = new Button("1000");
            mPlay1000Button.setUserData(1000);
            mPlay1000Button.setOnAction(getPlaybackEventHandler());
            mPlay1000Button.setGraphic(new FontIcon(FontAwesome.FAST_FORWARD));
        }

        return mPlay1000Button;
    }

    private  Button getPlay2000Button()
    {
        if(mPlay2000Button == null)
        {
            mPlay2000Button = new Button("2000");
            mPlay2000Button.setUserData(2000);
            mPlay2000Button.setOnAction(getPlaybackEventHandler());
            mPlay2000Button.setGraphic(new FontIcon(FontAwesome.FAST_FORWARD));
        }

        return mPlay2000Button;
    }

    private  Button getPlay5000Button()
    {
        if(mPlay5000Button == null)
        {
            mPlay5000Button = new Button("5000");
            mPlay5000Button.setUserData(5000);
            mPlay5000Button.setOnAction(getPlaybackEventHandler());
            mPlay5000Button.setGraphic(new FontIcon(FontAwesome.FAST_FORWARD));
        }

        return mPlay5000Button;
    }

    private EventHandler<ActionEvent> getPlaybackEventHandler()
    {
        if(mPlayEventHandler == null)
        {
            mPlayEventHandler = new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    int count = (int)((Button)event.getSource()).getUserData();

                    try
                    {
                        mBinaryReader.next(count);
                    }
                    catch(IOException ioe)
                    {
                        mLog.error("Error while playing bytes");
                    }
                }
            };
        }

        return mPlayEventHandler;
    }

    private TextField getPlaybackPositionText()
    {
        if(mPlaybackPositionText == null)
        {
            mPlaybackPositionText = new TextField("0");
            mPlaybackPositionText.setAlignment(Pos.CENTER);
            mPlaybackPositionText.setPrefWidth(80);
        }

        return mPlaybackPositionText;
    }

    private  Label getFileLabel()
    {
        if(mFileLabel == null)
        {
            mFileLabel = new Label();
        }

        return mFileLabel;
    }

    @Override
    public void frameLocationUpdated(int location)
    {
        getPlaybackPositionText().setText(String.valueOf(location));
    }

    @Override
    public void frameLocationReset()
    {
        mLog.info("Frame location reset ... ignoring?");
    }
}
