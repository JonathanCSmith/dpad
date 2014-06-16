package jonathansmith.dpad.common.gui;

import javax.swing.*;

import jonathansmith.dpad.api.common.engine.event.IEventListener;
import jonathansmith.dpad.api.common.gui.ITabController;

import jonathansmith.dpad.common.engine.Engine;
import jonathansmith.dpad.common.engine.util.log.ILogDisplay;
import jonathansmith.dpad.common.gui.display.Display;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Generic parent class for tabs that can be displayed within the DPAD GUI Container
 */
public abstract class EngineTabController<T extends Display> implements ITabController, ILogDisplay, IEventListener {

    protected Engine engine;

    protected JPanel     display;
    protected JSplitPane coreDisplaySplitPane;
    protected JSplitPane logSplitPane;
    protected JTextArea  logArea;

    private int[]   logAreaLength     = new int[1024];
    private int     currentTextLength = 0;
    private boolean isSetup           = false;
    private T       currentDisplay    = null;
    private T       oldDisplay        = null;

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setCurrentDisplay(T targetDisplay) {
        this.currentDisplay = targetDisplay;
    }

    @Override
    public JPanel getPanel() {
        return this.display;
    }

    @Override
    public void update() {
        if (this.logSplitPane != null && !this.isSetup) {
            if (this.shouldShowLog()) {
                this.logSplitPane.setDividerLocation(0.8D);
                this.logSplitPane.setResizeWeight(0.8D);
                this.isSetup = true;
            }

            else {
                this.logSplitPane.setDividerLocation(1.0D);
                this.logSplitPane.setResizeWeight(1.0D);
                this.logSplitPane.setEnabled(false);
                this.logArea.setEnabled(false);
//                Dimension tooSmall = new Dimension(-1, 0);
//                this.logArea.setPreferredSize(tooSmall);
//                this.logArea.setMinimumSize(tooSmall);
//                this.logArea.setMaximumSize(tooSmall);
//                this.logSplitPane.setDividerLocation(0);
//                this.logSplitPane.setEnabled(false);
                this.isSetup = true;
            }
        }

        if (this.currentDisplay != this.oldDisplay) {
            if (this.oldDisplay != null) {
                this.oldDisplay.onDestroy(this.engine);
            }

            int dividerLocation = this.coreDisplaySplitPane.getDividerLocation();
            this.coreDisplaySplitPane.setLeftComponent(this.currentDisplay.getToolbarComponent().getContentPane());
            this.coreDisplaySplitPane.setRightComponent(this.currentDisplay.getDisplayComponent().getContentPane());
            this.coreDisplaySplitPane.setDividerLocation(dividerLocation);
            this.oldDisplay = this.currentDisplay;
        }

        if (this.currentDisplay != null) {
            this.currentDisplay.update();
        }
    }

    protected abstract boolean shouldShowLog();

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
        if (!this.shouldShowLog()) {
            return;
        }

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
