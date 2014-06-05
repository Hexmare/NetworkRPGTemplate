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
/**
 * This package contains a family of classes that are used
 * for incorporating a "Console" gui element into a jMonkey game.
 *
 * <p>The primary class in this package is {@link strongdk.jme.appstate.console.ConsoleAppState}. All thats
 * needed to add a Console to your game is:
 * <br>{@code stateManager.attach(new ConsoleAppState());}
 * <br>{@code stateManager.attach(new ConsoleDefaultCommandsAppState()); // optional}
 *
 * <p>Be sure to check out {@link strongdk.examples.TestConsoleAppState} to see a full usage example
 * of how to add the Console to a game and register commands with the console.
 *
 * @author Daniel Strong <strongd@gmx.com> aka icamefromspace
 * @version 0.99
 * @see http://hub.jmonkeyengine.org/forum/topic/console-appstate-plugin/
 *
 */
package strongdk.jme.appstate.console;
