package jonathansmith.dpad.client.gui.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.engine.event.IEventListener;
import jonathansmith.dpad.api.common.network.session.ISessionData;

import jonathansmith.dpad.common.engine.event.Event;
import jonathansmith.dpad.common.engine.event.gui.ProgressBarUpdateEvent;
import jonathansmith.dpad.common.gui.display.DisplayPanel;
import jonathansmith.dpad.common.network.packet.play.user.UserChangePasswordPacket;
import jonathansmith.dpad.common.network.packet.play.user.UserLoginPacket;
import jonathansmith.dpad.common.network.packet.play.user.UserLogoutPacket;

import jonathansmith.dpad.client.ClientEngine;
import jonathansmith.dpad.client.engine.event.UserToolbarEvent;
import jonathansmith.dpad.client.engine.executor.user.UserServerResponseExecutor;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * User Administration panel. Condition display of user login/logout/administration
 */
public class UserAdministrationPanel extends DisplayPanel implements IEventListener {

    private static final List<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(UserToolbarEvent.class);
    }

    private final ClientEngine engine;

    private boolean isLoggedIn       = false;
    private boolean isAdministration = false;

    private JPanel         contentPane;
    private JTextField     usernameTextField;
    private JTextField     userName;
    private JTextField     passwordTextField;
    private JTextField     confirmPasswordTextField;
    private JPasswordField passwordConfirm;
    private JPasswordField password;
    private JTextField     textField1;
    private JTextField     oldPasswordText;
    private JPasswordField oldPassword;

    public UserAdministrationPanel(ClientEngine engine) {
        this.engine = engine;
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {
        ISessionData data = this.engine.getSessionData();
        if (data.isUserLoggedIn() != this.isLoggedIn) {
            this.isLoggedIn = data.isUserLoggedIn();
            this.switchUserState(data);
        }
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        byte buttonPress = ((UserToolbarEvent) event).getButtonPress();
        if (buttonPress == 0) {
            if (this.isLoggedIn) {
                if (this.isAdministration) {
                    // Change password
                    char[] old = this.oldPassword.getPassword();
                    if (old.length == 0 || old.length > 32) {
                        this.showModal("Your old password is definitely wrong, it is either empty or too long!");
                    }

                    char[] pwd1 = this.password.getPassword();
                    char[] pwd2 = this.passwordConfirm.getPassword();

                    try {
                        if (pwd1.length == 0 || pwd2.length == 0) {
                            this.showModal("One of your new passwords is empty!");
                        }

                        if (pwd1.length > 32 || pwd2.length > 32) {
                            this.showModal("Passwords cannot be longer than 32 characters!");
                        }

                        if (!Arrays.equals(pwd1, pwd2)) {
                            this.showModal("New passwords do not match!");
                            this.password.setText("");
                            this.passwordConfirm.setText("");
                            return;
                        }
                    }

                    catch (NullPointerException ex) {
                        this.showModal("One of your passwords was missing");
                        return;
                    }

                    this.engine.getSession().scheduleOutboundPacket(new UserChangePasswordPacket(new String(this.oldPassword.getPassword()), new String(pwd2)), new GenericFutureListener[]{
                            new GenericFutureListener() {
                                @Override
                                public void operationComplete(Future future) throws Exception {
                                    UserAdministrationPanel.this.engine.getEventThread().postEvent(new ProgressBarUpdateEvent("Awaiting Server Response", 0, 3, 1));
                                }
                            }
                    });
                }

                else {
                    // Logout
                    this.engine.getSession().scheduleOutboundPacket(new UserLogoutPacket(), new GenericFutureListener[]{
                            new GenericFutureListener() {
                                @Override
                                public void operationComplete(Future future) throws Exception {
                                    UserAdministrationPanel.this.engine.getEventThread().postEvent(new ProgressBarUpdateEvent("Awaiting Server Response", 0, 3, 1));
                                }
                            }
                    });
                }
            }

            else {
                if (this.userName.getText().contentEquals("")) {
                    this.showModal("Missing Username!");
                    return;
                }

                if ((this.userName.getText().getBytes().length / 4) > 32) {
                    this.showModal("Username is too long! A maximum of 32 characters is allowed.");
                    return;
                }

                if (this.isAdministration) {
                    // New User
                    try {
                        char[] pwd1 = this.password.getPassword();
                        char[] pwd2 = this.passwordConfirm.getPassword();

                        if (pwd1.length == 0 || pwd2.length == 0) {
                            this.showModal("Passwords cannot be null!");
                        }

                        if (pwd1.length > 32 || pwd2.length > 32) {
                            this.showModal("Passwords cannot be longer than 32 characters!");
                        }

                        if (!Arrays.equals(pwd1, pwd2)) {
                            this.showModal("Passwords do not match!");
                            this.password.setText("");
                            this.passwordConfirm.setText("");
                            return;
                        }
                    }

                    catch (NullPointerException ex) {
                        this.showModal("One of your passwords was missing");
                        return;
                    }

                    this.engine.getSession().scheduleOutboundPacket(new UserLoginPacket(true, this.userName.getText(), new String(this.password.getPassword())), new GenericFutureListener[]{
                            new GenericFutureListener() {
                                @Override
                                public void operationComplete(Future future) throws Exception {
                                    UserAdministrationPanel.this.engine.getEventThread().postEvent(new ProgressBarUpdateEvent("Awaiting Server Response", 0, 3, 1));
                                }
                            }
                    });
                }

                else {
                    // Login
                    final String pwd = new String(this.password.getPassword());
                    if (pwd.contentEquals("")) {
                        this.showModal("Password cannot be empty!");
                        return;
                    }

                    this.engine.getSession().scheduleOutboundPacket(new UserLoginPacket(false, this.userName.getText(), pwd), new GenericFutureListener[]{
                            new GenericFutureListener() {
                                @Override
                                public void operationComplete(Future future) throws Exception {
                                    UserAdministrationPanel.this.engine.getEventThread().postEvent(new ProgressBarUpdateEvent("Awaiting Server Response", 0, 3, 1));
                                }
                            }
                    });
                }
            }

            this.engine.setProposedExecutor(new UserServerResponseExecutor(this.engine));
        }

        else {
            if (buttonPress == 1) {
                this.isAdministration = !this.isAdministration;
            }

            this.switchUserState(this.engine.getSessionData());
        }
    }

    private void switchUserState(ISessionData data) {
        if (this.isLoggedIn) {
            if (this.isAdministration) {
                // Change password
                this.textField1.setText("Please enter the appropriate details to change your password.");

                this.usernameTextField.setEnabled(false);
                this.userName.setEnabled(false);

                this.oldPasswordText.setEnabled(true);
                this.oldPassword.setEnabled(true);
                this.oldPassword.setText("");

                this.passwordTextField.setEnabled(true);
                this.passwordTextField.setText("New Password:");
                this.password.setEnabled(true);
                this.password.setText("");

                this.confirmPasswordTextField.setEnabled(true);
                this.passwordConfirm.setEnabled(true);
                this.passwordConfirm.setText("");
            }

            else {
                // Logout
                this.textField1.setText("Currently logged in as: " + data.getUserName());

                this.usernameTextField.setEnabled(false);
                this.userName.setEnabled(false);

                this.oldPasswordText.setEnabled(false);
                this.oldPassword.setEnabled(false);

                this.passwordTextField.setEnabled(false);
                this.password.setEnabled(false);

                this.confirmPasswordTextField.setEnabled(false);
                this.passwordConfirm.setEnabled(false);
            }
        }

        else {
            if (this.isAdministration) {
                // New User
                this.textField1.setText("Please enter the appropriate information to create a new user:");

                this.usernameTextField.setEnabled(true);
                this.userName.setEnabled(true);
                this.userName.setText("");

                this.oldPasswordText.setEnabled(false);
                this.oldPassword.setEnabled(false);

                this.passwordTextField.setEnabled(true);
                this.passwordTextField.setText("New Password:");
                this.password.setEnabled(true);
                this.password.setText("");

                this.confirmPasswordTextField.setEnabled(true);
                this.passwordConfirm.setEnabled(true);
                this.passwordConfirm.setText("");
            }

            else {
                // Login
                this.textField1.setText("Please enter your login information below:");

                this.usernameTextField.setEnabled(true);
                this.userName.setEnabled(true);
                this.userName.setText("");

                this.oldPasswordText.setEnabled(false);
                this.oldPassword.setEnabled(false);

                this.passwordTextField.setEnabled(true);
                this.passwordTextField.setText("Password");
                this.password.setEnabled(true);
                this.password.setText("");
            }
        }
    }
}
