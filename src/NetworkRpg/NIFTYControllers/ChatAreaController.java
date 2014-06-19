/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.NIFTYControllers;

/**
 *
 * @author Rebel
 */
import java.util.Properties;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;

import de.lessvoid.nifty.controls.ScrollPanel;
import de.lessvoid.nifty.controls.ScrollPanel.AutoScroll;

import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.xml.xpp3.Attributes;

public class ChatAreaController implements Controller {
	private ScrollPanel scrollPanel;
	private Element textArea;
        private Screen screen;

	@Override
	public void bind(Nifty nifty,
			Screen screen,
			Element element,
			Properties parameter,
			//ControllerEventListener listener,
			Attributes controlDefinitionAttributes) {
                this.screen = screen;
		scrollPanel = element.findNiftyControl("scroll_panel", ScrollPanel.class);
		textArea = element.findElementByName("text_area");
	}

	@Override
	public void onStartScreen() {
	}

	@Override
	public void onFocus(boolean getFocus) {
	}

	@Override
	public boolean inputEvent(NiftyInputEvent inputEvent) {
		return false;
	}
	
	public void setAutoScroll(AutoScroll auto) {
		scrollPanel.setAutoScroll(auto);
	}
	
	public AutoScroll getAutoScroll() {
		return scrollPanel.getAutoScroll();
	}
	
	public void append(String text) {
		setText(getText()+text);
	}
	
	public void setText(String text) {
		textArea.getRenderer(TextRenderer.class).setText(text);
		screen.layoutLayers();
		textArea.setHeight(textArea.getRenderer(TextRenderer.class).getTextHeight());
	}
	
	public String getText() {
		return textArea.getRenderer(TextRenderer.class).getOriginalText();
	}

    

    @Override
    public void init(Properties parameter, Attributes controlDefinitionAttributes) {
        
    }
}
