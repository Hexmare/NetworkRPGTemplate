/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg;

/**
 *
 * @author Rebel
 */
import NetworkRpg.AppStates.MainMenuState;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.PopupBuilder;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.controls.ConsoleCommands;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;

public class GameGuiController extends AbstractAppState implements ScreenController, KeyInputHandler {

    private Nifty nifty;
    private Main app;
    private Screen screen;
    public boolean signal;
    private Element consolePopup;
    private AppStateManager stateManager;
    private Console console;
    private ConsoleCommands consoleCommands;
    private static final Color HELP_COLOR = new Color("#aaaf");
    /**
     * custom methods
     */
    public GameGuiController() {
        /**
         * You custom constructor, can accept arguments
         */
        signal = false;
    }

    public GameGuiController(AppStateManager asm, Application appin) {
        /**
         * You custom constructor, can accept arguments
         */
        this.stateManager = asm;
        this.app = (Main) appin;
        signal = false;
    }

    public void startGameOld(String nextScreen) {
        nifty.gotoScreen(nextScreen);  // switch to another screen
    }

    public void quitGame() {
        app.stop();
    }

    public String getPlayerName() {
        return System.getProperty("user.name");
    }

    /**
     * Nifty GUI ScreenControl methods
     */
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        this.consolePopup = nifty.createPopup("consolePopup");
        this.console = this.consolePopup.findNiftyControl("console", Console.class);
        //this.consolePopup = nifty.createPopup("consolePopup");

        consoleCommands = new ConsoleCommands(nifty, console);

        ConsoleCommands.ConsoleCommand showCommand = new ShowCommand();
//        consoleCommands.registerCommand("show ListBox", showCommand);
//        consoleCommands.registerCommand("show DropDown", showCommand);
//        consoleCommands.registerCommand("show TextField", showCommand);
//        consoleCommands.registerCommand("show Slider", showCommand);
//        consoleCommands.registerCommand("show ScrollPanel", showCommand);
        consoleCommands.registerCommand("show ChatControl", showCommand);
//        consoleCommands.registerCommand("show DragAndDrop", showCommand);

        NiftyCommand niftyCommand = new NiftyCommand();
        consoleCommands.registerCommand("nifty screen", niftyCommand);

        ConsoleCommands.ConsoleCommand helpCommand = new HelpCommand();
        consoleCommands.registerCommand("help", helpCommand);

        ConsoleCommands.ConsoleCommand clearCommand = new ClearCommand();
        consoleCommands.registerCommand("clear", clearCommand);

        ConsoleCommands.ConsoleCommand exitCommand = new ExitCommand();
        consoleCommands.registerCommand("exit", exitCommand);

        // enable the nifty command line completion
        consoleCommands.enableCommandCompletion(true);

    }

    public void onStartScreen() {
        Element niftyElement = nifty.getCurrentScreen().findElementByName("score");
        System.out.println("On start screen");
    }

    public void onEndScreen() {
    }

    /**
     * jME3 AppState methods
     */
    @Override
    public void initialize(AppStateManager stateManager, Application appin) {
        this.app = (Main) appin;
        this.nifty = this.app.getStateManager().getState(MainMenuState.class).getNifty();
        registerConsolePopup(this.nifty);
        this.stateManager = this.app.getStateManager();
        //registerConsolePopup(this.nifty);

    }

    @Override
    public boolean keyEvent(final NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyInputEvent.ConsoleToggle) {
            if (screen.isActivePopup(consolePopup)) {
                nifty.closePopup(consolePopup.getId());
            } else {
                nifty.showPopup(screen, consolePopup.getId(), null);
            }
            return true;
        }
        return false;
    }

    private static void registerConsolePopup(Nifty nifty) {
        new PopupBuilder("consolePopup") {
            {
                childLayoutAbsolute();
                panel(new PanelBuilder() {
                    {
                        childLayoutCenter();
                        width("100%");
                        height("100%");
                        alignCenter();
                        valignCenter();
                        control(new ConsoleBuilder("console") {
                            {
                                width("80%");
                                lines(25);
                                alignCenter();
                                valignCenter();
                                onStartScreenEffect(new EffectBuilder("move") {
                                    {
                                        length(150);
                                        inherit();
                                        neverStopRendering(true);
                                        effectParameter("mode", "in");
                                        effectParameter("direction", "top");
                                    }
                                });
                                onEndScreenEffect(new EffectBuilder("move") {
                                    {
                                        length(150);
                                        inherit();
                                        neverStopRendering(true);
                                        effectParameter("mode", "out");
                                        effectParameter("direction", "top");
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }.registerPopup(nifty);
    }

    @Override
    public void update(float tpf) {
        try {
            //if (screen.getScreenId().equalsIgnoreCase("hud")) {
            Element niftyElement = nifty.getCurrentScreen().findElementByName("score");
            // Display the time-per-frame -- this field could also display the score etc...
            niftyElement.getRenderer(TextRenderer.class).setText((int) (tpf * 100000) + "");
            //}
        } catch (Exception e) {
        }

        //System.out.println("update");
    }

    public void onClick() {
        //Element niftyElement = nifty.getCurrentScreen().findElementByName("Clickable");

        //NiftyImage image;
        System.out.println("On Clicke event");
        /*
         * This is the stuff for setting and chaning subimage
         * imageMode="subImage:10,10,32,32"
         image = niftyElement.getRenderer(ImageRenderer.class).getImage();
         image.getImageMode().setParameters("subImage:30,30,32,32");
         //screen.findElementByName("elementId");
         System.out.println("Clicked!");
         */
        signal = true;
    }

    public void toggleCrosshairs() {
        System.out.println("Cross Hairs toggle");
        Element niftyElement = nifty.getCurrentScreen().findElementByName("crossHairs");

        if (niftyElement.isVisible()) {
            niftyElement.hide();
        } else {
            niftyElement.show();
        }

    }

    public void startGame() {
//        System.out.println(stateManager);
        this.app.getStateManager().getState(MainMenuState.class).connectToServer();
//        System.out.println("Clicked startgame button");
        this.nifty.gotoScreen("gameHud");
        //this.app.startGame();

    }

    public void screenResize() {
        System.out.println("resize called");
        /*AppSettings settings = new AppSettings(true);;
        
         settings.setResolution(1024, 768);
         app.setSettings(settings);
         app.restart();
         //app.hrm();
         */
    }

    private class ShowCommand implements ConsoleCommands.ConsoleCommand {

        @Override
        public void execute(final String[] args) {
            if (args.length != 2) {
                console.outputError("command argument error");
                return;
            }
            // this really is a hack to get from the command argument, like: "ListBox" to the matching "menuButtonId" 
            String menuButtonId = args[1];
//            if (!buttonToDialogMap.containsKey(menuButtonId)) {
//                console.outputError("'" + menuButtonId + "' is not a registered dialog.");
//                return;
//            }
//
//            // just a gimmick
//            if (menuButtonId.equals(currentMenuButtonId)) {
//                console.outputError("Hah! Already there! I'm smart... :>");
//                return;
//            }

            // finally switch
            changeDialogTo(menuButtonId);
        }
    }

    private class NiftyCommand implements ConsoleCommands.ConsoleCommand {

        @Override
        public void execute(final String[] args) {
            if (args.length != 2) {
                console.outputError("command argument error");
                return;
            }
            String param = args[1];
            if ("screen".equals(param)) {
                String screenDebugOutput = nifty.getCurrentScreen().debugOutput();
                console.output(screenDebugOutput);
                System.out.println(screenDebugOutput);
            } else {
                console.outputError("unknown parameter [" + args[1] + "]");
            }
        }
    }

    private class HelpCommand implements ConsoleCommands.ConsoleCommand {

        @Override
        public void execute(final String[] args) {
            console.output("---------------------------", HELP_COLOR);
            console.output("Supported commands", HELP_COLOR);
            console.output("---------------------------", HELP_COLOR);
            for (String command : consoleCommands.getRegisteredCommands()) {
                console.output(command, HELP_COLOR);
            }
        }
    }

    private class ExitCommand implements ConsoleCommands.ConsoleCommand {

        @Override
        public void execute(final String[] args) {
            console.output("good bye");
            nifty.closePopup(consolePopup.getId());
        }
    }

    private class ClearCommand implements ConsoleCommands.ConsoleCommand {

        @Override
        public void execute(final String[] args) {
            console.clear();
        }
    }

    private void changeDialogTo(final String id) {
        System.out.println(id);
        Element currentElement = screen.findElementByName(id);
        currentElement.show();
//        if (!id.equals(currentMenuButtonId)) {
//            int currentIndex = buttonIdList.indexOf(currentMenuButtonId);
//            int nextIndex = buttonIdList.indexOf(id);
//
//            Element nextElement = screen.findElementByName(buttonToDialogMap.get(id));
//            modifyMoveEffect(EffectEventId.onShow, nextElement, currentIndex < nextIndex ? "right" : "left");
//            nextElement.show();
//
//            Element currentElement = screen.findElementByName(buttonToDialogMap.get(currentMenuButtonId));
//            modifyMoveEffect(EffectEventId.onHide, currentElement, currentIndex < nextIndex ? "left" : "right");
//            currentElement.hide();
//
//            screen.findElementByName(currentMenuButtonId).stopEffect(EffectEventId.onCustom);
//            screen.findElementByName(id).startEffect(EffectEventId.onCustom, null, "selected");
//            currentMenuButtonId = id;
//        }
    }
}