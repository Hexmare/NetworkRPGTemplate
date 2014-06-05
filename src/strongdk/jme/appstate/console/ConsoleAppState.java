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
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This AppState creates a Console made out of some basic Spatials and attaches
 * it to the gui root node. A console gives the user a place to type in debug
 * commands. Use {@link ConsoleDefaultCommandsAppState} to add default
 * command mappings to the console. Use the {@link CommandListener} interface to
 * create your own commands and add them to the console.
 *
 * <p>This AppState is completely threadsafe. Howver when exeucting code
 * in your own {@link CommandListener} remember that you arent working from
 * the main game thread. Be sure to use {@code app.enqueue()} if your command
 * modifies the scenegraph.
 *
 * <p>example usage:
 * <br>{@code stateManager.attach(new ConsoleAppState());}
 * <br>{@code stateManager.attach(new ConsoleDefaultCommandsAppState()); // optional}
 *
 * <p>Basic Controls:
 * <br>{@code Grave: shows and hides the console}
 * <br>{@code Up/Down Arrows: used to recall command input history.}
 * <br>{@code Shift + Backspace: clears the input line}
 * <br>{@code Shift + Esc: closes the app through app.stop();}
 *
 * <p>Created by Daniel Strong for use with jME3 RC2, August 2013.
 *
 * @author Daniel Strong <strongd@gmx.com> aka icamefromspace
 * @version 0.99
 * @see http://hub.jmonkeyengine.org/forum/topic/console-appstate-plugin/
 */
public class ConsoleAppState implements AppState {

      // AppState management variables.
      private boolean initialized = false;
      private boolean visible = false;
      // commonly used variables throughout the state
      private SimpleApplication app;
      private AssetManager assetManager;
      private InputManager inputManager;
      private ViewPort guiViewPort;
      private Node guiNode;
      private Node consoleBaseNode;
      // main display spatials of the console and their relevant variables.
      private boolean isConsoleUsesFullViewPortWidth = true;
      private boolean isConsoleUsesFullViewPortHeight = false;
      private BlendMode materialBlendMode = BlendMode.Alpha;
      private Geometry bgQuad;
      private Geometry bgInputQuad;
      private Geometry bgScrollingQuad;
      private String enqBitmapFontAssetName = "Interface/Fonts/Default.fnt"; // this variable is reset to null after being consumed by applyViewportChange().
      private Float enqConsoleTextSize = null;  // this variable is reset to null after being consumed by applyViewportChange(). Ultimately determines the overall size of the console.
      private BitmapText scrollingBitmapText;
      private final ConcurrentLinkedQueue<String> enqScrollingText = new ConcurrentLinkedQueue<String>(); // each element is a line to be added to the console output. The queue gets emptied and applied to the screen by the update() method.
      private int numConsoleLines = 11;
      private BitmapText inputBitmapText;
      private String inputText = ""; //This is the "true value" of the input text, actually setting the text is enqueued, this can only be set through setInputText() so that it shows up on the screen.
      private BitmapText inputCursorBitmapText;
      private float blinkCount = 0;
      private float blinkDuration = .4f;
      // RawInputListener for controlling the console with keyboard.
      private ConsoleRawInputListener rawInputListener;
      // variables for tracking which commands are registered. uses synchronized blocks for concurrency
      private final HashMap<String, CommandListener> registeredCommands = new HashMap<String, CommandListener>(8);
      // variables for keeping track of input history
      private final ConcurrentLinkedQueue<String> inputHistory = new ConcurrentLinkedQueue<String>();
      private int currentInputHistoryLevel = 0;
      private String currentInputLineStored = ""; //if the input history is explored with the up arrow. the current input line is stored here in case the user wants to return to that line

      public ConsoleAppState() {
      }

      @Override
      final public void initialize(AppStateManager pstateManager, Application papp) {
            initialized = true;
            app = (SimpleApplication) papp;
            assetManager = app.getAssetManager();
            inputManager = app.getInputManager();
            guiViewPort = app.getGuiViewPort();
            guiNode = app.getGuiNode();

            if (consoleBaseNode == null) {
                  consoleBaseNode = new Node("Console");
                  //  Load the Console background geoms.
                  //ConsoleAppState INFO   : Color[0.0, 0.0, 0.0, 0.2]
                  //ConsoleAppState INFO   : Color[0.2, 0.2, 0.2, 0.7]

                  Material darkGrayMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                  darkGrayMat.setColor("Color", new ColorRGBA(0, 0, 0, 0.2f));
                  darkGrayMat.getAdditionalRenderState().setBlendMode(materialBlendMode);

                  Material lightGrayMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                  lightGrayMat.setColor("Color", new ColorRGBA(0.2f, 0.2f, 0.2f, 0.7f));
                  lightGrayMat.getAdditionalRenderState().setBlendMode(materialBlendMode);

                  bgQuad = new Geometry("BgQuad");
                  bgQuad.setMaterial(darkGrayMat);

                  bgInputQuad = new Geometry("BgInputQuad");
                  bgInputQuad.setMaterial(lightGrayMat);

                  bgScrollingQuad = new Geometry("BgScrollingQuad");
                  bgScrollingQuad.setMaterial(lightGrayMat);

                  rawInputListener = new ConsoleRawInputListener();

                  consoleBaseNode.attachChild(bgQuad);
                  consoleBaseNode.attachChild(bgInputQuad);
                  consoleBaseNode.attachChild(bgScrollingQuad);

            }

            applyViewPortChangeNotThreadSafe();

            //text was possibly set before the state was added, lets make sure this gets applied
            setInputText(inputText);

            inputManager.addRawInputListener(rawInputListener);

            if (visible) {
                  //setVisible(true) was called before the state was added, lets do this now
                  setVisible(true);
            }


      }

      /**
       * removes the key mappings, hides the console, and clears the text
       * elements. However the listeners remain registered.
       *
       * <p>This shouldn't be called directly. instead use
       * {@code stateManager.detach(consoleAppState);}
       */
      @Override
      public final void cleanup() {
            inputManager.removeRawInputListener(rawInputListener);

            if (visible) {
                  setVisible(false);
            }

            inputText = "";
            rawInputListener.leftShiftFlag = false;
            inputHistory.clear();
            currentInputHistoryLevel = 0;
            currentInputLineStored = "";

            initialized = false;
      }

      private final Callable<Void> setVisibleCallable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                  if (visible) {
                        if (consoleBaseNode.getParent() == null) {
                              guiNode.attachChild(consoleBaseNode);
                        }
                  } else {
                        consoleBaseNode.removeFromParent();
                  }
                  return null;
            }

      };



      /**
       * This gets called automatically when:
       * <ul compact>
       * <li>The AppState is initialzed
       * <li>num console lines changes
       * <li>Text size changed
       * <li>Text font changed
       * </ul>
       *
       * This method typically would never need to be called. though if somehow
       * the screen dimensions change and the console isn't automatically compensating for
       * it, then call this method to force it to re-initialize its screen elements.
       */
      public void applyViewPortChange() {
            if (this.isInitialized()) {
                  app.enqueue(applyViewPortChangeCallable);
            }
      }

      private final Callable<Void> applyViewPortChangeCallable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                  applyViewPortChangeNotThreadSafe();
                  return null;
            }

      };

      private void applyViewPortChangeNotThreadSafe() {
            if (enqBitmapFontAssetName != null) {
                  if (scrollingBitmapText != null) {
                        scrollingBitmapText.removeFromParent();
                        inputBitmapText.removeFromParent();
                        inputCursorBitmapText.removeFromParent();
                  }
                  BitmapFont guiFont = assetManager.loadFont(enqBitmapFontAssetName); //Interface/Fonts/Default.fnt
                  enqBitmapFontAssetName = null;
                  scrollingBitmapText = new BitmapText(guiFont, false);
                  scrollingBitmapText.setName("scrollingBitmapText");
                  scrollingBitmapText.setLineWrapMode(LineWrapMode.Word);
                  inputBitmapText = new BitmapText(guiFont, false);
                  inputBitmapText.setName("inputBitmapText");
                  inputCursorBitmapText = new BitmapText(guiFont, false);
                  inputCursorBitmapText.setName("inputCursorBitmapText");
                  consoleBaseNode.attachChild(scrollingBitmapText);
                  consoleBaseNode.attachChild(inputBitmapText);
                  consoleBaseNode.attachChild(inputCursorBitmapText);
            } else if (enqBitmapFontAssetName == null && scrollingBitmapText == null) {
                  throw new AssertionError("Major internal malfunction of the console. Can't create text labels.");
            }

            if (enqConsoleTextSize != null) {
                  // enqConsoleTextSize gets set by setConsoleSize(), make use of it here
                  this.inputBitmapText.setSize(enqConsoleTextSize);
                  this.scrollingBitmapText.setSize(enqConsoleTextSize);
                  this.inputCursorBitmapText.setSize(enqConsoleTextSize);
                  enqConsoleTextSize = null; //reset the enqueue variable because next time theres a viewport change we wont need to change the size.
            }
            inputCursorBitmapText.setText("|");
            blinkCount = 0;
            int viewPortHeight = guiViewPort.getCamera().getHeight();
            int viewPortWidth = guiViewPort.getCamera().getWidth();
            float lineHeight = inputCursorBitmapText.getLineHeight();
            float lineWidth = inputCursorBitmapText.getLineWidth();
            float bgQuadWidth;
            if (isConsoleUsesFullViewPortWidth) {
                  bgQuadWidth = viewPortWidth;
            } else {
                  bgQuadWidth = lineWidth * 95;
                  if (bgQuadWidth > viewPortWidth) {
                        bgQuadWidth = viewPortWidth;
                  } else if (bgQuadWidth < viewPortWidth / 2) {
                        bgQuadWidth = viewPortWidth / 2;
                  }
            }
            float verticalPadding = .15f * lineHeight;
            float horizontalPadding = verticalPadding; //float horizontalPadding = (bgQuadWidth * .007f);

            //TODO: this is a lazy way to calculate the max number of console lines
            //      I should be able to reverse the bgQuadHeight equation and solve for
            //      numConsoleLines.
            if (isConsoleUsesFullViewPortHeight) {
                  numConsoleLines = 60;
            }
            boolean reducedConsoleLinesToFit = false;
            float bgQuadHeight = (lineHeight * (numConsoleLines + 1)) + (verticalPadding * 3);
            while (bgQuadHeight > viewPortHeight) {
                  reducedConsoleLinesToFit = true;
                  numConsoleLines--;// yeh this is kind of lazy of me.
                  bgQuadHeight = (lineHeight * (numConsoleLines + 1)) + (verticalPadding * 3);
            }
            float bgFieldQuadWidth = bgQuadWidth - (horizontalPadding * 2);

            //input text
            inputBitmapText.setLocalTranslation(horizontalPadding * 2f, viewPortHeight - (lineHeight * numConsoleLines) - (verticalPadding * 1.5f), 1); // TODO: using 1.5 instead of 2 means this isnt totally based on the text and viewport sizes. might want to recheck my math later.
            // TODO: inputBitmapText has an issue of not wrapping text, but setting a box messes up the blinking cursor.
            //inputText.setBox(new Rectangle(0, 0, bgFieldQuadWidth, lineHeight));,

            //scrolling text
            scrollingBitmapText.setLocalTranslation(horizontalPadding * 2f, viewPortHeight, 1); // subtract 1 vertical padding?
            scrollingBitmapText.setBox(new Rectangle(0, 0, bgFieldQuadWidth * .98f, lineHeight * numConsoleLines));
            scrollingBitmapText.setVerticalAlignment(BitmapFont.VAlign.Bottom);

            if (numConsoleLines <= 0) {
                  scrollingBitmapText.setCullHint(Spatial.CullHint.Always); //user doesnt even want to show the console apparently.
            } else {
                  scrollingBitmapText.setCullHint(Spatial.CullHint.Dynamic);
            }

            //background geom
            if (reducedConsoleLinesToFit) {
                  //This means the console is the full height of the screen.
                  //there might be a small thin line left at the bottom of the screen
                  //so lets just use the whole screen height to make it look nice
                  bgQuad.setMesh(new Quad(bgQuadWidth, viewPortHeight));
                  bgQuad.setLocalTranslation(0, 0, .8f);
            } else {
                  bgQuad.setMesh(new Quad(bgQuadWidth, bgQuadHeight));
                  bgQuad.setLocalTranslation(0, viewPortHeight - bgQuadHeight, .8f);
            }


            //background input text geom
            bgInputQuad.setMesh(new Quad(bgFieldQuadWidth, lineHeight));
            bgInputQuad.setLocalTranslation(horizontalPadding, viewPortHeight - (lineHeight * (numConsoleLines + 1)) - (verticalPadding * 2f), .9f);

            //background scrolling text geom
            bgScrollingQuad.setMesh(new Quad(bgFieldQuadWidth, lineHeight * numConsoleLines));
            bgScrollingQuad.setLocalTranslation(horizontalPadding, viewPortHeight - (lineHeight * numConsoleLines) - verticalPadding, .9f);

      }

      /**
       * Returns the number of console lines being used.
       * Note that after {@link setConsoleNumLines()} is called the
       * console reinitializes itself on the next update(),
       * if the number of lines is too large for the screen
       * the console will automatically reduce the number of lines
       * to fit the screen.
       *
       * <p>Just keep in mind that this value is subject to change
       * when the console is reinitialized.
       *
       * @return
       */
      public int getConsoleNumLines() {
            return numConsoleLines;
      }

      /**
       * If a custom {@code consoleTextSize} is set it returns that value,
       * otherwise it returns the {@code getFont().getPreferredSize()}.
       *
       * @return
       */
      public float getConsoleTextSize() {
            if (enqConsoleTextSize != null) {
                  return enqConsoleTextSize;
            } else if (this.isInitialized()) {
                  return inputBitmapText.getFont().getPreferredSize();
            }
            throw new IllegalStateException("Sorry the console text size cant be known until after it the console is initiated unless you set a custom text size.");
      }

      /**
       * Sets the desired number of console lines. the actual
       * number of lines will automatically be reduced so that the whole
       * console fits on the screen.
       *
       * @param numLines default is 11
       */
      public void setConsoleNumLines(int numLines) {
            this.numConsoleLines = (numLines < 0 ? 0 : numLines);
            this.applyViewPortChange();
      }

      /**
       * Sets the console font (note: will override any previously set TextSize).
       * Use null for the {@code fontAssetName} to return to the default font.
       *
       * @param fontAssetName path in assets folder to the font, eg: "Interface/Fonts/Default.fnt"
       */
      public void setConsoleFont(String fontAssetName) {
            enqBitmapFontAssetName = (fontAssetName != null ? fontAssetName : "Interface/Fonts/Default.fnt");
            enqConsoleTextSize = null;
            applyViewPortChange();
      }

      /**
       * Sets the console font, and uses a custom text size. Use null
       * for the {@co fontAssetName} to return to the default font.
       *
       * @param fontAssetName path in assets folder to the font, eg: "Interface/Fonts/Default.fnt"
       * @param size          default is is determined by font (default size of default font is 17)
       */
      public void setConsoleFont(String fontAssetName, float size) {
            enqBitmapFontAssetName = (fontAssetName != null ? fontAssetName : "Interface/Fonts/Default.fnt");
            enqConsoleTextSize = (size < 12 ? 12 : size);
            applyViewPortChange();
      }

      /**
       * @param size default is is determined by font (default size of default font is 17)
       */
      public void setConsoleTextSize(float size) {
            enqConsoleTextSize = (size < 12 ? 12 : size);
            applyViewPortChange();
      }

      /**
       * Sets the console text size to the default/preferred size of the font.
       * For the default font this value is 17.
       */
      public void setConsoleTextSizeToDefault() {
            if (isInitialized()) {
                  enqConsoleTextSize = inputBitmapText.getFont().getPreferredSize();
                  applyViewPortChange();
            } else {
                  enqConsoleTextSize = null;
            }
      }

      /**
       * Sets the numbers of lines of the console and custom text size.
       *
       * @param numLines default is 11
       * @param size     default is is determined by font (default size of default font is 17)
       */
      public void setConsoleLinesAndSize(int numLines, float size) {
            numConsoleLines = (numLines < 0 ? 0 : numLines);
            enqConsoleTextSize = (size < 12 ? 12 : size);
            applyViewPortChange();
      }

      /**
       * Sets the number of lines of the console, and the font to use.
       *
       * @param numLines      default is 11
       * @param fontAssetName path in assets folder to the font, eg: "Interface/Fonts/Default.fnt"
       */
      public void setConsoleLinesAndFont(int numLines, String fontAssetName) {
            numConsoleLines = (numLines < 0 ? 0 : numLines);
            enqBitmapFontAssetName = (fontAssetName != null ? fontAssetName : "Interface/Fonts/Default.fnt");
            enqConsoleTextSize = null;
            applyViewPortChange();
      }

      /**
       * Sets the number of lines of the console, and the font to use, and a custom text size.
       *
       * @param numLines      default is 11
       * @param fontAssetName path in assets folder to the font, eg: "Interface/Fonts/Default.fnt"
       * @param size          default is is determined by font (default size of default font is 17)
       */
      public void setConsoleLinesAndFont(int numLines, String fontAssetName, float size) {
            numConsoleLines = (numLines < 0 ? 0 : numLines);
            enqBitmapFontAssetName = (fontAssetName != null ? fontAssetName : "Interface/Fonts/Default.fnt");
            enqConsoleTextSize = (size < 12 ? 12 : size);
            applyViewPortChange();
      }

      /**
       * Returns the flag value that was set by setConsoleUsesFullViewPortWidth()
       *
       * @return
       */
      public boolean isConsoleUsesFullViewPortWidth() {
            return isConsoleUsesFullViewPortWidth;
      }

      /**
       * Returns the flag value that was set by setConsoleUsesFullViewPortHeight()
       *
       * @return
       */
      public boolean isConsoleUsesFullViewPortHeight() {
            return isConsoleUsesFullViewPortHeight;
      }

      /**
       * forces the console width to be the width of the viewport.
       *
       * @param isFullViewPortWidth default is false.
       */
      public void setConsoleUsesFullViewPortWidth(boolean isFullViewPortWidth) {
            this.isConsoleUsesFullViewPortWidth = isFullViewPortWidth;
            applyViewPortChange();
      }

      /**
       * forces the console height to be the height of the viewport.
       *
       * <p>will modify the number of console lines to keep the height.
       *
       * @param isFullViewPortHeight default is false
       */
      public void setConsoleUsesFullViewPortHeight(boolean isFullViewPortHeight) {
            this.isConsoleUsesFullViewPortHeight = isFullViewPortHeight;
            applyViewPortChange();
      }

      /**
       * forces the console width and number of lines to take up
       * the entire screen space.
       *
       * <p>same as using both setConsoleUsesFullViewPortHeight() and
       * setConsoleUsesFullViewPortWidth().
       *
       * @param isFullViewPortWidth default is false.
       */
      public void setConsoleUsesFullViewPort(boolean isFullViewPort) {
            this.isConsoleUsesFullViewPortWidth = isFullViewPort;
            this.isConsoleUsesFullViewPortHeight = isFullViewPort;
            applyViewPortChange();
      }

      /**
       * switches the alpha on and off on the console background
       * quads.
       *
       * @param useAlpha
       */
      public void setUseAlphaOnConsoleGeoms(boolean useAlpha) {
            if (useAlpha) {
                  materialBlendMode = BlendMode.Alpha;
            } else {
                  materialBlendMode = BlendMode.Off;
            }

            if (this.isInitialized()) {
                  app.enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                              bgQuad.getMaterial().getAdditionalRenderState().setBlendMode(materialBlendMode);
                              bgInputQuad.getMaterial().getAdditionalRenderState().setBlendMode(materialBlendMode);
                              //bgScrollingQuad uses the same material as bgInputQuad
                              return null;
                        }
                  });
            }
      }

      //
      // Command Registration Functions
      //
      /**
       * Checks to see if the command is already registered.
       *
       * @param command
       * @return
       */
      public boolean hasCommand(String command) {
            synchronized (registeredCommands) {
                  return registeredCommands.containsKey(CommandParser.getFirstElement(command));
            }
      }

      /**
       * Registers a command and the executor that processes the command.
       *
       * <p>Only one listener can be assigned to a command, attempting to assign
       * multiple listener to the same command will result in
       * IllegalStateException being thrown.
       *
       * <p>commands must not contain spaces in them, if there is a space
       * everything after the space will be considered "command info", and can
       * be retreived by using getRegisteredCommandInfo(String).
       *
       * @param command
       * @param listener
       * @throws IllegalArgumentException if command is null or an empty String
       * @throws IllegalStateException    if command is already registered
       */
      public void registerCommand(String command, CommandListener listener) throws IllegalArgumentException, IllegalStateException {
            command = CommandParser.getFirstElement(command);
            if (command == null) {
                  throw new IllegalArgumentException("command can not be null");
            } else if (command.length() == 0) {
                  throw new IllegalArgumentException("command can not be an empty string");
            }

            synchronized (registeredCommands) {
                  if (!registeredCommands.containsKey(command)) {
                        registeredCommands.put(command, listener);
                  } else {
                        throw new IllegalStateException(String.format("can't register this command (%s), it is already registered.", command));
                  }
            }
      }

      /**
       * Removes specific command
       *
       * @param command
       * @throws IllegalArgumentException if command is null or empty
       * @throws IllegalStateException    if the command is not registered.
       */
      public void unregisterCommand(String command) throws IllegalArgumentException, IllegalStateException {
            command = CommandParser.getFirstElement(command);
            if (command == null || command.length() == 0) {
                  throw new IllegalArgumentException("command can not be null");
            }

            synchronized (registeredCommands) {
                  if (registeredCommands.containsKey(command)) {
                        registeredCommands.remove(command);
                  } else {
                        throw new IllegalStateException(String.format("can't unregister this command (%s), it isn't registered.", command));
                  }
            }
      }

      /**
       * Removes all commands tied to this listener.
       *
       * @param listener
       * @throws IllegalArgumentException if the listener is null
       */
      public void unregisterCommands(CommandListener listener) throws IllegalArgumentException {
            if (listener == null) {
                  throw new IllegalArgumentException("executor can not be null");
            }

            synchronized (registeredCommands) {
                  Iterator<Entry<String, CommandListener>> i = registeredCommands.entrySet().iterator();
                  while (i.hasNext()) {
                        Entry<String, CommandListener> entry = i.next();
                        if (entry.getValue() == listener) {
                              i.remove();
                        }
                  }
            }
      }

      /**
       * Returns a list of all registered commands.
       *
       * <p>The returned list is a copy of the internally registered
       * commands and it will not get updated when the Console is, adding
       * or removing commands from this list doesn't impact anything
       * internally.
       *
       * @return
       */
      public List<String> getRegisteredCommands() {
            LinkedList<String> commands = new LinkedList<String>();
            synchronized (registeredCommands) {
                  commands.addAll(registeredCommands.keySet());
            }
            Collections.sort(commands);
            return commands;
      }

      /**
       * Returns a list of all registered commands that are executed by
       * this listener.
       *
       * <p>The returned list is a copy of the internally registered
       * commands and it will not get updated when the Console is, adding
       * or removing commands from this list doesn't impact anything
       * internally.
       *
       * @return
       * @param belongingToListener
       */
      public List<String> getRegisteredCommands(CommandListener belongingToListener) {
            if (belongingToListener == null) {
                  return getRegisteredCommands();
            }
            LinkedList<String> commands = new LinkedList<String>();

            synchronized (registeredCommands) {
                  for (Entry<String, CommandListener> entry : registeredCommands.entrySet()) {
                        if (entry.getValue() == belongingToListener) {
                              commands.add(entry.getKey());
                        }
                  }
            }
            Collections.sort(commands);
            return commands;
      }

      private void stopApp() {
            if (isInitialized()) {
                  app.stop();
            } else {
                  System.out.print("Console was not initialized, so called System.exit(0) instead of app.stop()\n");
                  System.exit(0);
            }
      }

      //
      // Methods related to manipulate the input line and execute it.
      //
      /**
       * Gets the command from the command history by its index.
       *
       * <p>It automatically returns the command the user was typing on the
       * inputText line that index is selected. (index 0)
       *
       * @param inputHistoryIndex
       * @return the input string at the supplied index.
       */
      private String getInputHistoryByIndex(int inputHistoryIndex) {
            int level = inputHistory.size() - inputHistoryIndex;
            if (level < 0) {
                  level = 0;
            }
            int current = 0;
            for (String s : inputHistory) {
                  if (current == level) {
                        return s;
                  }
                  current++;
            }
            return currentInputLineStored;
      }

      /**
       * sets the input text line to the previous command in the history (pushing up on keyboard)
       */
      private void setInputTextPrevHistory() {
            if (currentInputHistoryLevel == 0) {
                  currentInputLineStored = inputText;
            }
            if (currentInputHistoryLevel < inputHistory.size()) {
                  currentInputHistoryLevel++;
            }
            this.setInputText(getInputHistoryByIndex(currentInputHistoryLevel));
      }

      /**
       * sets the input text line to the next command in the history (pushing down on keyboard)
       */
      private void setInputTextNextHistory() {
            if (currentInputHistoryLevel > 0) {
                  currentInputHistoryLevel--;
            }
            this.setInputText(getInputHistoryByIndex(currentInputHistoryLevel));
      }

      /**
       * sends the input from the input line to be processed
       * and the output will then be shown in the console
       * output window.
       *
       * <p>Will return false if the input line is empty.
       *
       * @return true if input line was processed, false otherwise
       *
       */
      private boolean returnInput() {
            String input = inputText;
            if (input.length() == 0) {
                  return false;
            }
            // add input to the history
            inputHistory.offer(input);
            currentInputHistoryLevel = 0;
            if (inputHistory.size() > 20) {
                  inputHistory.poll();
            }
            //show the input on the console
            this.appendConsole("> " + input);
            this.clearInput();
            //actually do the input command
            executeInput(input);
            return true;
      }

      /**
       * Executes the specified input parameter as if the user
       * types it in (however it doesnt go into the user's
       * command history).
       *
       * <p>Use this method to manually execute commands
       * rather than calling the {@link CommandListener} directly.
       *
       * <p>What happens as a result to calling this method depends on
       * what listeners are registered and what the listeners choose
       * to do with the input.
       *
       * @param input The command to be processed
       * @throws IllegalArgumentException if the input string is null or empty
       * @throws IllegalStateException    if the ConsoleAppState isnt initialized yet
       */
      public void executeInput(String input) {
            if (input == null || input.isEmpty()) {
                  throw new IllegalArgumentException("input can not be null or empty.");
            } else if (!this.isInitialized()) {
                  //Note: techincally initialization isnt required, however
                  // its unlikely that the ConsoleAppState doesnt have any
                  // listeners yet, and if it does theres a good chance
                  // that the listener requires initializaton (as its likely
                  // an AppState). So I'm just going to throw an IllegalStateException
                  // off the bat to avoid confusion with using this method.
                  throw new IllegalStateException("can't execute input before console is initialized.");
            }

            final CommandParser parser = new CommandParser(input);
            CommandListener listener;
            synchronized (registeredCommands) {
                  listener = registeredCommands.get(parser.getCommand());
            }
            if (listener != null) {
                  listener.execute(new CommandEvent(parser));
            } else {
                  appendConsoleError("Unknown command: " + parser.getCommand());
                  appendConsoleError("Type 'help' to get list of available commands ");
            }
      }

      //
      // Begin methods for modifying the input text and console text.
      //
      /**
       * Erases the input line. similiar to uses Shift + Backspace when the console is open.
       */
      public void clearInput() {
            setInputText("");
            currentInputHistoryLevel = 0;
            currentInputLineStored = "";
      }

      /**
       * Erases all text in the console.
       */
      public void clearConsole() {
            if (this.isInitialized()) {
                  //TODO: This implementation is a little ugly. I think instead
                  //      enqScrollingText should just have a "clear" command added
                  //      to the queue that the update() loop can recognize
                  //      and process.
                  app.enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                              scrollingBitmapText.setText("");
                              enqScrollingText.clear();
                              return null;
                        }

                  });
            } else {
                  enqScrollingText.clear();
            }
      }

      /**
       * Same as calling clearInput() and clearConsole().
       */
      public void clearBoth() {
            clearInput();
            clearConsole();
      }

      private void setInputText(String text) {
            inputText = text;
            if (this.isInitialized()) {
                  app.enqueue(setInputTextCallable);
            }
      }

      private void appendInputText(String text) {
            inputText += text;
            if (this.isInitialized()) {
                  app.enqueue(setInputTextCallable);
            }
      }

      private void appendInputTextBackspace() {
            try {
                  inputText = inputText.substring(0, inputText.length() - 1);
                  if (this.isInitialized()) {
                        app.enqueue(setInputTextCallable);
                  }
            } catch (StringIndexOutOfBoundsException ex) {
                  //This is expected and normal (if you push backspace too much)
            }
      }

      /**
       * Appends a line of text to the console.
       *
       * @param text
       */
      public void appendConsole(String text) {
            enqScrollingText.offer(text);
      }

      /**
       * Appends a line of text to the console with styling to show
       * that it is an error (eg. shows up red)
       *
       * <p>TODO: Haven't actually figured out how to make the text red yet.
       *
       * @param text
       */
      public void appendConsoleError(String text) {
            enqScrollingText.offer(text);
      }

      @Override
      public void update(float tpf) {
            //TODO: this isVisible() is redundant since isEnabled() is an alias for isVisible()
            //      however I'm about to start working on transition effects..
            if (isVisible()) {
                  //Make the cursor blink.
                  blinkCount += tpf;
                  if (blinkCount >= blinkDuration) {
                        if (this.inputCursorBitmapText.getText().isEmpty()) {
                              inputCursorBitmapText.setText("|");
                              blinkDuration = .5f;
                        } else {
                              inputCursorBitmapText.setText("");
                              blinkDuration = .4f;
                        }
                        blinkCount = 0;
                  }
                  //If there is anything in the scrolling text queue lets show it.
                  appendConsoleNotThreadSafe(enqScrollingText.poll());
            }
      }

      private final Callable<Void> setInputTextCallable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                  inputBitmapText.setText(inputText);
                  updateCursorLocationNotThreadSafe();
                  return null;
            }

      };

      private void updateCursorLocationNotThreadSafe() {
            float x = inputBitmapText.getLineWidth();
            Vector3f inputTextLocation = inputBitmapText.getLocalTranslation();
            inputCursorBitmapText.setLocalTranslation((inputTextLocation.x / 1.6f) + x, inputTextLocation.y, 1);
            inputCursorBitmapText.setText("|");
            blinkCount = 0;
      }

      private void appendConsoleNotThreadSafe(String newLine) {
            if (newLine == null || newLine.length() == 0) {
                  return;
            }
            scrollingBitmapText.setText(scrollingBitmapText.getText() + "\n" + newLine);

            //Now remove overflow lines
            int linesToRemove = scrollingBitmapText.getLineCount() - numConsoleLines;
            if (linesToRemove > 0) {
                  String oldText = scrollingBitmapText.getText();
                  String[] textAsArray = oldText.split("\r\n|\r|\n");
                  oldText = "";
                  for (int i = linesToRemove; i < textAsArray.length; i++) {
                        if (!textAsArray[i].equals("") && !textAsArray[i].equals(" ")) {
                              oldText += textAsArray[i];
                              if (i != textAsArray.length - 1) {
                                    oldText += "\n";
                              }
                        }
                  }
                  scrollingBitmapText.setText(oldText);
            }
      }

      @Override
      public void stateAttached(AppStateManager stateManager) {
      }

      @Override
      public void stateDetached(AppStateManager stateManager) {
      }

      @Override
      public void render(RenderManager rm) {
      }

      @Override
      public void postRender() {
      }

      /**
       * Shows or hides the console on the screen.
       *
       * <p>The grave key is mapped to call this method, but feel
       * free to do it too if you want to for some reason.
       *
       * @param setVisible
       */
      public final void setVisible(boolean setVisible) {
            visible = setVisible;

            if (isInitialized()) {
                  app.enqueue(setVisibleCallable);
            }
      }

      /**
       * setVisible(!isVisible());
       */
      public final void toggleVisible() {
            setVisible(!isVisible());
      }

      /**
       * Returns true if the console is showing.
       *
       * <p>(or if its not initialized, returns true
       * if it is enqueued to show once the console is initialized).
       *
       * @return
       */
      public final boolean isVisible() {
            return visible;
      }

      /**
       * sorry to say this method is a lie. You can't disable this AppState.
       *
       * <p>If you want to remove the key mappings then detach this AppState.
       *
       * <p>If you dont want to remove the console, but just hide it, then use
       * setVisible(false).
       *
       * <p>under the hood, setVisible() actually controls the enabled state of
       * this AppState... The method name setVisible() is used to better explain
       * what enabling/disabling the appstate does. In the future I might make
       * this an alias for setVisible() but that might be confusing.
       *
       * @param setEnabled
       * @throws UnsupportedOperationException if you call this method at all.
       * @deprecated use setVisible() to hide and show the console, attach and detach the app state to add and remove the RawInputListener.
       */
      @Override
      public final void setEnabled(boolean setEnabled) {
            //enabled = setEnabled;
            throw new UnsupportedOperationException("use setVisible(true) and setVisible(false) to control hiding the console. use stateManager.detach(stateManager.getState(ConsoleAppState.class)) to remove the RawInputListener.");
      }

      /**
       * alias for isVisible(), see javadoc for setEnabled()
       * for explanation.
       *
       * @return
       */
      @Override
      public final boolean isEnabled() {
            return visible;
      }

      /**
       * returns true if the appState is initialized and is ready
       * to start receiving/processing commands with executeCommand().
       *
       * <p>Note: you can add commands to the Console before it is initialized,
       * you just cant executive them until after.
       *
       * @return
       */
      @Override
      public final boolean isInitialized() {
            return initialized;
      }

      private class ConsoleRawInputListener implements RawInputListener {

            private boolean leftShiftFlag = false;

            private ConsoleRawInputListener() {
            }

            @Override
            public void beginInput() {
            }

            @Override
            public void endInput() {
            }

            @Override
            public void onKeyEvent(KeyInputEvent evt) {
                  if (!evt.isPressed()) {
                        switch (evt.getKeyCode()) {
                              case KeyInput.KEY_LSHIFT:
                                    leftShiftFlag = false;
                                    break;
                        }
                        return;
                  }

                  switch (evt.getKeyCode()) {
                        case KeyInput.KEY_ESCAPE:
                              if (leftShiftFlag) {
                                    stopApp();
                                    evt.setConsumed();
                              } else if (isVisible()) {
                                    setVisible(false);
                                    evt.setConsumed();
                              }
                              break;
                        case KeyInput.KEY_LSHIFT:
                              leftShiftFlag = true;
                              break;
                        case KeyInput.KEY_GRAVE:
                        case 0:
                              //On testing on a mac I found that the grave key registered
                              // as 0 instead of KEY_GRAVE. So instead of using the KeyCode
                              // for the grave key I use the KeyChar to identify that
                              // grave has been pressed.
                              if (evt.getKeyChar() == '`') {
                                    toggleVisible();
                                    evt.setConsumed();
                              }
                              break;
                        case KeyInput.KEY_BACK:
                              if (isVisible()) {
                                    if (leftShiftFlag) {
                                          clearInput();
                                    } else {
                                          appendInputTextBackspace();
                                    }
                                    evt.setConsumed();
                              }
                              break;
                        case KeyInput.KEY_RETURN:
                              if (isVisible()) {
                                    //if you push enter when the input is empty then
                                    //it becomes a shortcut to close the console.
                                    if (!returnInput()) {
                                          setVisible(false);
                                    }
                                    evt.setConsumed();
                              }
                              break;
                        case KeyInput.KEY_UP:
                              if (isVisible()) {
                                    setInputTextPrevHistory();
                                    evt.setConsumed();
                              }
                              break;
                        case KeyInput.KEY_DOWN:
                              if (isVisible()) {
                                    setInputTextNextHistory();
                                    evt.setConsumed();
                              }
                              break;
                        default:
                              if (isVisible()) {
                                    char keyChar = evt.getKeyChar();
                                    if (keyChar != 0) {
                                          appendInputText(String.valueOf(keyChar));
                                    }
                                    evt.setConsumed();
                              }
                              break;
                  }

            }

            @Override
            public void onJoyAxisEvent(JoyAxisEvent evt) {
            }

            @Override
            public void onJoyButtonEvent(JoyButtonEvent evt) {
            }

            @Override
            public void onMouseMotionEvent(MouseMotionEvent evt) {
            }

            @Override
            public void onMouseButtonEvent(MouseButtonEvent evt) {
            }

            @Override
            public void onTouchEvent(TouchEvent evt) {
            }

      }
}
