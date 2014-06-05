/*
 * Copyright (c) 2013 Daniel Strong. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of the author nor the names of other contributors
 *   may not be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package strongdk.jme.appstate.console;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

/**
 *
 * Provides some default commands for {@link ConsoleAppState}:
 *
 * <ul>
 * <li>console lines #
 * <li>console size #
 * <li>console large
 * <li>console normal
 * <li>help
 * <li>hide
 * <li>clear
 * <li>exit
 * </ul>
 *
 * This provides a good starting point for someone who wants
 * to incorporate the console into their application.
 *
 * <p>Attaching/detaching this app state adds and removes these commands.
 * Be sure to add this app state AFTER adding the {@link ConsoleAppState.}
 *
 * @author Daniel Strong <strongd@gmx.com> aka icamefromspace
 * @version 0.99
 * @see http://hub.jmonkeyengine.org/forum/topic/console-appstate-plugin/
 */
public class ConsoleDefaultCommandsAppState extends AbstractAppState implements CommandListener {

      private Application app;
      private AppStateManager stateManager;
      private ConsoleAppState console;

      @Override
      public void initialize(AppStateManager pstateManager, Application papp) {
            super.initialize(pstateManager, papp);
            app = papp;
            stateManager = pstateManager;
            console = stateManager.getState(ConsoleAppState.class);

            if (console == null) {
                  throw new IllegalStateException("ConsoleAppState must be added before DefaultConsoleCommandsAppState");
            }
            if (!console.hasCommand("console")) {
                  console.registerCommand("console lines|size #", this);
            }
            if (!console.hasCommand("help")) {
                  console.registerCommand("help", this);
            }
            if (!console.hasCommand("hide")) {
                  console.registerCommand("hide", this);
            }
            if (!console.hasCommand("clear")) {
                  console.registerCommand("clear", this);
            }
            if (!console.hasCommand("exit")) {
                  console.registerCommand("exit", this);
            }
      }

      @Override
      public void cleanup() {
            super.cleanup();
            console.unregisterCommands(this);
      }

      @Override
      public void execute(CommandEvent evt) {
            CommandParser parser = evt.getParser();
            if (parser.isCommand("console")) {
                  if (parser.hasParameter("lines")) {
                        try {
                              Integer numLines = Integer.parseInt(parser.get(1));
                              console.setConsoleNumLines(numLines);
                        } catch (IndexOutOfBoundsException ex1) {
                              console.appendConsoleError("You need to specify the number of lines");
                        } catch (NumberFormatException ex2) {
                              console.appendConsoleError("Number of lines must be an integer");
                        }
                  } else if (parser.hasParameter("size")) {
                        Integer size = parser.getInt(1);
                        if (size == null) {
                              size = 17;
                        } else if (size < 12) {
                              size = 12;
                        }
                        console.setConsoleTextSize(size);
                  } else if (parser.hasParameter("large") || parser.hasParameter("big")) {
                        Integer size = parser.getInt(1);
                        if (size == null) {
                              size = 32;
                        }
                        console.setConsoleLinesAndSize(0, size);
                  } else if (parser.hasParameter("normal")) {
                        console.setConsoleLinesAndSize(11, 17);
                  } else if (parser.hasParameter("help")) {
                        if (console.getConsoleNumLines() == 0) {
                              console.setConsoleLinesAndSize(11, 17);
                        }
                        console.appendConsole("--- Console Commands --- ");
                        console.appendConsole("console lines #");
                        console.appendConsole("console size #");
                        console.appendConsole("console large");
                        console.appendConsole("console normal");
                        console.appendConsole("console help");
                        console.appendConsole("------------------------ ");
                  }

            } else if (parser.isCommand("help")) {
                  if (console.getConsoleNumLines() == 0) {
                        console.setConsoleLinesAndSize(11, 17);
                  }
                  console.appendConsole("----- Command List -----");
                  console.appendConsole("console lines|size #");
                  console.appendConsole("hide");
                  console.appendConsole("clear");
                  console.appendConsole("help");
                  console.appendConsole("exit");
                  console.appendConsole("-------------------------------- ");
            } else if (parser.isCommand("hide")) {
                  console.setVisible(false);
            } else if (parser.isCommand("clear")) {
                  if (console.getConsoleNumLines() == 0) {
                        console.setConsoleLinesAndSize(11, 17);
                  }
                  console.clearBoth();
            } else if (parser.isCommand("exit")) {
                  console.setVisible(false);
                  app.stop();
            } else {
                  throw new AssertionError("Command was not found: " + parser.getCommand());
            }
      }

}
