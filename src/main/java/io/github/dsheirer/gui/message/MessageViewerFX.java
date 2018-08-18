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

import io.github.dsheirer.gui.instrument.RecentFilesMenu;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Application for viewing and working with binary recording files and decoded messages.
 */
public class MessageViewerFX extends Application
{
    private final static Logger mLog = LoggerFactory.getLogger(MessageViewerFX.class);

    private Stage mStage;
    private MenuBar mMenuBar;
    private RecentFilesMenu mRecentFilesMenu;
    private MessageViewerDesktop mMessageViewerDesktop;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        mStage = primaryStage;
        setTitle();

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(getMenuBar());
        borderPane.setCenter(getMessageViewerDesktop());

        Scene scene = new Scene(borderPane, 1500, 900);

        mStage.setScene(scene);
        mStage.show();
    }

    private void setTitle()
    {
        mStage.setTitle("Message Viewer FX");
    }

    private void setTitle(MessageProtocol messageProtocol)
    {
        mStage.setTitle("Message Viewer FX - " + messageProtocol);
    }

    private MessageViewerDesktop getMessageViewerDesktop()
    {
        if(mMessageViewerDesktop == null)
        {
            mMessageViewerDesktop = new MessageViewerDesktop();
        }

        return mMessageViewerDesktop;
    }

    private MenuBar getMenuBar()
    {
        if(mMenuBar == null)
        {
            mMenuBar = new MenuBar();

            Menu fileMenu = new Menu("File");
            MenuItem openMenuItem = new MenuItem("Open ...");
            openMenuItem.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Open a binary recording file");
                    File file = fileChooser.showOpenDialog(mStage);
                    getMessageViewerDesktop().load(file);
                    getRecentFilesMenu().add(file);
                }
            });

            MenuItem closeMenuItem = new MenuItem("Close");
            closeMenuItem.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    getMessageViewerDesktop().close();
                }
            });

            MenuItem exitMenuItem = new MenuItem("Exit");
            exitMenuItem.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    Platform.exit();
                }
            });

            fileMenu.getItems().addAll(openMenuItem, getRecentFilesMenu(), closeMenuItem, new SeparatorMenuItem(),
                    exitMenuItem);

            mMenuBar.getMenus().add(fileMenu);

            Menu protocolMenu = new Menu("Protocol");

            for(MessageProtocol protocol: MessageProtocol.values())
            {
                if(MessagePaneFactory.isSupported(protocol))
                {
                    MenuItem decoderMenuItem = new MenuItem(protocol.toString());

                    decoderMenuItem.setOnAction(new EventHandler<ActionEvent>()
                    {
                        @Override
                        public void handle(ActionEvent event)
                        {
                            getMessageViewerDesktop().setProtocol(protocol);
                            setTitle(protocol);
                        }
                    });

                    protocolMenu.getItems().add(decoderMenuItem);
                }
            }

            mMenuBar.getMenus().add(protocolMenu);
        }

        return mMenuBar;
    }

    private RecentFilesMenu getRecentFilesMenu()
    {
        if(mRecentFilesMenu == null)
        {
            mRecentFilesMenu = new RecentFilesMenu("Recent","message.viewer", 5);
            mRecentFilesMenu.setFileSelectionListener(new RecentFilesMenu.IFileSelectionListener()
            {
                @Override
                public void fileSelected(File file)
                {
                    getMessageViewerDesktop().load(file);
                }
            });
        }

        return mRecentFilesMenu;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
