/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg;

import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyInputMapping;
import de.lessvoid.nifty.input.keyboard.KeyboardInputEvent;
 
public class MenuInputMapping implements NiftyInputMapping {
 
  public NiftyInputEvent convert(final KeyboardInputEvent inputEvent) {
    if (inputEvent.isKeyDown()) {
//        System.out.println(inputEvent.);
        if (inputEvent.getKey() == KeyboardInputEvent.KEY_GRAVE && inputEvent.isControlDown()) {
        return NiftyInputEvent.ConsoleToggle;
      } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_RETURN) {
        return NiftyInputEvent.Activate;
      } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_SPACE) {
        return NiftyInputEvent.Activate;
      } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_TAB) {
        if (inputEvent.isShiftDown()) {
          return NiftyInputEvent.PrevInputElement;
        } else {
          return NiftyInputEvent.NextInputElement;
        }
      } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_UP) {
        return NiftyInputEvent.MoveCursorUp;
      } else if (inputEvent.getKey() == KeyboardInputEvent.KEY_DOWN) {
        return NiftyInputEvent.MoveCursorDown;
      }
    }
    return null;
  }
}
