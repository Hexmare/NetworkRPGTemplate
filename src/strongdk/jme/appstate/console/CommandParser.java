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

/**
 *
 * For use with the {@link ConsoleAppState} system to
 * quickly and easily parse the user's input. This
 * class is purely a string parser and could be used with
 * any string really.
 *
 * <p>Main thing to remember is that the "command" is the first
 * portion of the inputString, but when you do get(0) that
 * returns the first parameter (the first element after the
 * command). In order to get the command use getCommand().
 *
 * @author Daniel Strong <strongd@gmx.com> aka icamefromspace
 * @version 0.99
 * @see http://hub.jmonkeyengine.org/forum/topic/console-appstate-plugin/
 */
public class CommandParser {

      /**
       * Used by the CommandParser to detrmine what portion of
       * an input string is the "Command" portion.
       *
       * <p>This method is protected so the command registration
       * functions can use the same method for filtering
       * inputs.
       *
       * @param input
       * @return
       */
      protected static String getFirstElement(String input) {
            if (input == null) {
                  return null;
            } else if (input.length() == 0) {
                  return "";
            }

            String[] split = input.split(separator);
            return split[0];
      }

      private final static String separator = "\\s+";
      private final String[] inputArray; // By definition, an array with atleast 1 element.

      public CommandParser(String inputString) {
            if (inputString == null) {
                  throw new IllegalArgumentException("inputString can not be null");
            } else if (inputString.isEmpty()) {
                  throw new IllegalArgumentException("inputString can not be empty");
            }
            inputArray = inputString.split(separator);
      }

      public CommandParser(String[] inputArray) {
            if (inputArray == null) {
                  throw new IllegalArgumentException("inputArray can not be null");
            } else if (inputArray.length == 0) {
                  throw new IllegalArgumentException("inputArray can not be empty");
            }
            this.inputArray = inputArray;
      }

      /**
       * Reconstructs the original inputString of the parser.
       * <p>Though if extra spaces were removed when parsing it
       * then those spaces will still be gone.
       *
       * @return
       */
      public String getCommandWithAllParameters() {
            if (this.hasNoParameters()) {
                  return getCommand();
            }
            return getCommand() + " " + this.getAllParameters();
      }

      /**
       * Returns just the command portion of the
       * input string.
       *
       * @return
       */
      public String getCommand() {
            return inputArray[0];
      }

      /**
       * Easier to type than getCommand().equals(testCommand).
       *
       * @param testCommand
       *
       * @return
       */
      public boolean isCommand(String testCommand) {
            return getCommand().equals(testCommand);
      }

      /**
       * Gets the input string minus the command,
       * each parameter is seperated by a space.
       *
       * <p>retuns null if there is no parameters.
       *
       * <p>Same as getAllParametes(0);
       *
       * @return
       */
      public String getAllParameters() {
            return getAllParameters(0);
      }

      /**
       * Gets the input string minus the command, and starting at a specified
       * parameter index.
       *
       * <p>Each parameter is seperated by a space.
       *
       * <p>retuns null if there is no parameters that start after the supplied index.
       *
       * @return
       */
      public String getAllParameters(int startingIndex) {
            startingIndex++;
            if (inputArray.length == 1) {
                  return null;
            } else if (startingIndex < 1) {
                  throw new IllegalArgumentException("index must be greater than or equal to 0");
            } else if (startingIndex >= inputArray.length) {
                  return null;
            }
            String parameters = "";
            for (int i = startingIndex; i < inputArray.length; i++) {
                  if (i < inputArray.length - 1) {
                        parameters += inputArray[i] + " ";
                  } else {
                        parameters += inputArray[i];
                  }
            }
            return parameters;
      }

      /**
       * Get certain parameter. The first parameter (index 0) is the first
       * element after the command of the input string. or returns null if the
       * parameter does not exist.
       *
       * <p>For example. if the input string is "connect localhost" then getParameter(0)
       * will return "localhost".
       *
       * @param index
       * @return the parameter at the index, or null if the parameter does not exist
       * @throws IllegalArgumentException if index is < 0
       */
      public String get(int index) {
            index++;
            if (index < 1) {
                  throw new IllegalArgumentException("index must be greater than or equal to 0");
            } else if (index >= inputArray.length) {
                  return null;
            }

            return inputArray[index];
      }

      /**
       * Alias for get(int).
       *
       * @param index
       * @return
       * @throws IllegalArgumentException if index is < 0
       */
      public String getString(int index) {
            return get(index);
      }

      /**
       * same as get() but will automatically parse to the
       * desired datatype.
       *
       * <p>Returns null if the parameter doesnt exist OR if the
       * value cant be parsed.
       *
       * <p>NOTE: get(0) might return a value but getInt(0)
       * might return null if the value cant be parsed
       * to a integer.
       *
       * @param index
       * @return
       * @throws IllegalArgumentException if index < 0
       */
      public Integer getInt(int index) {
            String val = get(index);
            if (val == null) {
                  return null;
            }

            try {
                  int parsedval = Integer.parseInt(val);
                  return parsedval;
            } catch (NumberFormatException e) {
                  return null;
            }
      }

      /**
       * same as get() but will automatically parse to the
       * desired datatype.
       *
       * <p>Returns null if the parameter doesnt exist OR if the
       * value cant be parsed.
       *
       * <p>NOTE: get(0) might return a value but getFloat(0)
       * might return null if the value cant be parsed
       * to a float.
       *
       * @param index
       * @return
       * @throws IllegalArgumentException if index <0
       */
      public Float getFloat(int index) {
            String val = get(index);
            if (val == null) {
                  return null;
            }

            try {
                  float parsedval = Float.parseFloat(val);
                  return parsedval;
            } catch (NumberFormatException e) {
                  return null;
            }
      }

      /**
       * same as get() but will automatically parse to the
       * desired datatype.
       *
       * <p>Returns null if the parameter doesnt exist OR if the
       * value cant be parsed.
       *
       * <p>NOTE: get(0) might return a value but getDouble(0)
       * might return null if the value cant be parsed
       * to a double.
       *
       * @param index
       * @return
       * @throws IllegalArgumentException if index < 0
       */
      public Double getDouble(int index) {
            String val = get(index);
            if (val == null) {
                  return null;
            }

            try {
                  double parsedval = Double.parseDouble(val);
                  return parsedval;
            } catch (NumberFormatException e) {
                  return null;
            }
      }

      /**
       * same as get() but will automatically parse to the
       * desired datatype.
       *
       * <p>Returns null if the parameter doesnt exist OR if the
       * value cant be parsed.
       *
       * <p>NOTE: get(0) might return a value but getBoolean(0)
       * might return null if the value cant be parsed
       * to a boolean.
       *
       * @param index
       * @return
       * @throws IllegalArgumentException id index < 0
       */
      public Boolean getBoolean(int index) {
            String val = get(index);
            if (val == null) {
                  return null;
            }

            try {
                  boolean parsedval = Boolean.parseBoolean(val);
                  return parsedval;
            } catch (NumberFormatException e) {
                  return null;
            }
      }

      /**
       * gets the number of parameters after the command.
       *
       * <p>Note: the command itself (first part of the input string)
       * is not considered a parameter.
       *
       * @return
       */
      public int getNumParameters() {
            return inputArray.length - 1;
      }

      /**
       * gets the index of the first occurance of the
       * parameter.
       *
       * <p>returns null if the parameter does not exist.
       *
       * @param param
       * @return
       */
      public Integer getParameterIndex(String param) {
            if (param == null) {
                  return null;
            }
            for (int i = 1; i < inputArray.length; i++) {
                  if (inputArray[i].equals(param)) {
                        return i - 1;
                  }
            }
            return null;
      }

      /**
       * Returns true if this one specfic param is in
       * the parameter list. This is slightly more
       * efficient than using containsParams() but not
       * as flexible.
       *
       * <p>NOTE: do not use multiple parameters as the
       * input. This is for testing against one
       * single parameter.
       *
       * @param param
       * @return
       */
      public boolean hasParameter(String param) {
            if (param == null) {
                  return true;
            }
            for (int i = 1; i < inputArray.length; i++) {
                  if (inputArray[i].equals(param)) {
                        return true;
                  }
            }
            return false;
      }

      /**
       * returns true if getNumParameters() == 0.
       *
       * @return
       */
      public boolean hasNoParameters() {
            return inputArray.length == 1;
      }

      /**
       * Convenience method.
       *
       * @param testParser
       * @return
       */
      public boolean contains(CommandParser testParser) {
            return contains(testParser.getCommandWithAllParameters());
      }

      /**
       * contains("color blue") == (isCommand("color") && containsParams("blue"))
       *
       * @param testString
       * @return
       */
      public boolean contains(String testString) {
            if (testString == null || testString.isEmpty()) {
                  return false;
            }

            String[] testArray = testString.split(separator);
            if (testArray.length == 1) {
                  // only testing a string with a command
                  return inputArray[0].equals(testArray[0]);
            } else if (!inputArray[0].equals(testArray[0])) {
                  // the test string contains parameters,
                  //  but we'll only check those if the command
                  //  itself is a match.
                  return false;
            }

            for (int i = 1; i < testArray.length; i++) {
                  if (!this.hasParameter(testArray[i])) {
                        return false;
                  }
            }
            return true;
      }

      /**
       * Returns true if all parameters are in the input
       * string parameter list..
       *
       * <p>This can be a single string with spaces, or an array
       * of strings, or seperate string parameters
       * (or a combination of all 3) it does not matter.
       *
       * <p>NOTE: do NOT include the "command" portion of
       * the input in this method. If you want to test
       * against the command portion plus the parameter
       * portion then use the contains() method instead!
       *
       * @param testParameters
       * @return
       */
      public boolean containsParams(String... testParameters) {
            for (String s : testParameters) {
                  String[] params = s.split(separator);
                  for (String s2 : params) {
                        if (!hasParameter(s2)) {
                              return false;
                        }
                  }
            }
            return true;
      }

}
