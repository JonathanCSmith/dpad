package jonathansmith.jdpad.common.gui;

import javax.swing.*;

import jonathansmith.jdpad.api.common.engine.event.IEventListener;
import jonathansmith.jdpad.api.common.engine.util.log.ILogDisplay;

import jonathansmith.jdpad.common.engine.Engine;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Generic parent class for tabs that can be displayed within the J-DPAD GUI Container
 */
public abstract class EngineTabController implements ITabController, ILogDisplay, IEventListener {

    protected Engine engine;

    protected JPanel     display;
    protected JSplitPane coreDisplaySplitPane;
    protected JSplitPane logSplitPane;
    protected JTextArea  logArea;

    private int[] logAreaLength     = new int[1024];
    private int   currentTextLength = 0;

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    public JPanel getPanel() {
        return this.display;
    }

    @Override
    public void init() {
        this.logSplitPane.setDividerLocation(this.logSplitPane.getMaximumDividerLocation() - 50);
    }

    @Override
    public void update() {
    }

    @Override
    public void onWindowClosing() {
    }

    @Override
    public void onWindowClosed() {
    }

    @Override
    public void shutdown(boolean force) {
        this.display.setVisible(false);
    }

    @Override
    public void appendLog(String message) {
        this.logArea.append(message);

        int oldLength = this.logArea.getDocument().getLength();
        this.logArea.setCaretPosition(oldLength);
        int addedSize = this.logArea.getDocument().getLength() - oldLength;

        if (this.logAreaLength[this.currentTextLength] != 0) {
            this.logArea.replaceRange("", 0, this.logAreaLength[this.currentTextLength]);
        }

        this.logAreaLength[this.currentTextLength] = addedSize;
        this.currentTextLength = (this.currentTextLength + 1) % 1024;
    }
}
