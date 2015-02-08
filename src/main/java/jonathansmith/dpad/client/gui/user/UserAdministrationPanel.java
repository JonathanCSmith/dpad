package jonathansmith.dpad.client.gui.user;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import jonathansmith.dpad.api.common.network.session.ISessionData;
import jonathansmith.dpad.api.events.ProgressBarUpdateEvent;
import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;

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
public class UserAdministrationPanel extends DisplayPanel implements IEventListener, ActionListener {

    private static final List<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(UserToolbarEvent.class);
    }

    private final ClientEngine engine;

    private boolean isLoggedIn       = false;
    private boolean isDoingSomething = false;

    private UserToolbarEvent.ToolbarStatus status;

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
    private JButton        submitButton;

    public UserAdministrationPanel(ClientEngine engine) {
        this.engine = engine;

        this.submitButton.addActionListener(this);

        this.usernameTextField.setVisible(true);
        this.userName.setVisible(true);

        this.oldPasswordText.setVisible(false);
        this.oldPassword.setVisible(false);

        this.passwordTextField.setVisible(false);
        this.password.setVisible(false);

        this.confirmPasswordTextField.setVisible(false);
        this.passwordConfirm.setVisible(false);

        this.submitButton.setVisible(false);
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

            if (this.isLoggedIn && !this.isDoingSomething) {
                this.userName.setText(data.getCurrentUserName());
            }

            else if (!this.isDoingSomething) {
                this.userName.setText("");
            }
        }
    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        switch (((UserToolbarEvent) event).getButtonPress()) {
            case NEW_USER:
                this.status = UserToolbarEvent.ToolbarStatus.NEW_USER;
                this.isDoingSomething = true;

                this.textField1.setText("Please enter your new user information below:");

                this.usernameTextField.setVisible(true);
                this.userName.setVisible(true);
                this.userName.setText("");
                this.userName.setEditable(true);

                this.oldPasswordText.setVisible(false);
                this.oldPassword.setVisible(false);

                this.passwordTextField.setVisible(true);
                this.password.setVisible(true);
                this.password.setText("");

                this.confirmPasswordTextField.setVisible(true);
                this.passwordConfirm.setVisible(true);
                this.passwordConfirm.setText("");

                this.submitButton.setVisible(true);
                break;

            case LOGIN:
                this.status = UserToolbarEvent.ToolbarStatus.LOGIN;
                this.isDoingSomething = true;

                this.textField1.setText("Please login using the fields below:");

                this.usernameTextField.setVisible(true);
                this.userName.setVisible(true);
                this.userName.setText("");
                this.userName.setEditable(true);

                this.oldPasswordText.setVisible(false);
                this.oldPassword.setVisible(false);

                this.passwordTextField.setVisible(true);
                this.password.setVisible(true);
                this.password.setText("");

                this.confirmPasswordTextField.setVisible(false);
                this.passwordConfirm.setVisible(false);

                this.submitButton.setVisible(true);
                break;

            case LOGOUT:
                this.engine.setProposedExecutor(new UserServerResponseExecutor(this.engine));
                this.engine.getSession().scheduleOutboundPacket(new UserLogoutPacket(), new GenericFutureListener[]{
                        new GenericFutureListener() {
                            @Override
                            public void operationComplete(Future future) throws Exception {
                                UserAdministrationPanel.this.engine.getEventThread().postEvent(new ProgressBarUpdateEvent("Awaiting Server Response", 0, 3, 1));
                            }
                        }
                });
                break;

            case CHANGE_PASSWORD:
                this.status = UserToolbarEvent.ToolbarStatus.CHANGE_PASSWORD;
                this.isDoingSomething = true;

                this.textField1.setText("Please enter your information below to change your password");

                this.usernameTextField.setVisible(false);
                this.userName.setVisible(false);

                this.oldPasswordText.setVisible(true);
                this.oldPassword.setVisible(true);
                this.oldPassword.setText("");

                this.passwordTextField.setVisible(true);
                this.password.setVisible(true);
                this.password.setText("");

                this.confirmPasswordTextField.setVisible(true);
                this.passwordConfirm.setVisible(true);
                this.passwordConfirm.setText("");

                this.submitButton.setVisible(true);
                break;

            case BACK:
                this.status = null;
                this.isDoingSomething = false;

                this.textField1.setText("Current User Information");

                this.usernameTextField.setVisible(true);
                this.userName.setVisible(true);
                if (this.isLoggedIn) {
                    this.userName.setText(this.engine.getSessionData().getCurrentUserName());
                }
                this.userName.setEditable(false);

                this.oldPasswordText.setVisible(false);
                this.oldPassword.setVisible(false);

                this.passwordTextField.setVisible(false);
                this.password.setVisible(false);

                this.confirmPasswordTextField.setVisible(false);
                this.passwordConfirm.setVisible(false);

                this.submitButton.setVisible(false);
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        switch (this.status) {
            case NEW_USER:
                if (this.userName.getText().contentEquals("")) {
                    this.showModal("Username cannot be empty!");
                    return;
                }

                try {
                    char[] pwd1 = this.password.getPassword();
                    char[] pwd2 = this.passwordConfirm.getPassword();

                    if (pwd1.length == 0 || pwd2.length == 0) {
                        this.showModal("Passwords cannot be null!");
                        return;
                    }

                    if (pwd1.length > 32 || pwd2.length > 32) {
                        this.showModal("Passwords cannot be longer than 32 characters!");
                        return;
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

                this.engine.setProposedExecutor(new UserServerResponseExecutor(this.engine));

                this.engine.getSession().scheduleOutboundPacket(new UserLoginPacket(true, this.userName.getText(), new String(this.password.getPassword())), new GenericFutureListener[]{
                        new GenericFutureListener() {
                            @Override
                            public void operationComplete(Future future) throws Exception {
                                UserAdministrationPanel.this.engine.getEventThread().postEvent(new ProgressBarUpdateEvent("Awaiting Server Response", 0, 3, 1));
                            }
                        }
                });
                break;

            case LOGIN:
                if (this.userName.getText().contentEquals("")) {
                    this.showModal("Username was empty!");
                    return;
                }

                final String pwd = new String(this.password.getPassword());
                if (pwd.contentEquals("")) {
                    this.showModal("Password cannot be empty!");
                    return;
                }

                this.engine.setProposedExecutor(new UserServerResponseExecutor(this.engine));

                this.engine.getSession().scheduleOutboundPacket(new UserLoginPacket(false, this.userName.getText(), pwd), new GenericFutureListener[]{
                        new GenericFutureListener() {
                            @Override
                            public void operationComplete(Future future) throws Exception {
                                UserAdministrationPanel.this.engine.getEventThread().postEvent(new ProgressBarUpdateEvent("Awaiting Server Response", 0, 3, 1));
                            }
                        }
                });
                break;

            case LOGOUT:
                break;

            case CHANGE_PASSWORD:
                if (!this.isLoggedIn) {
                    return; // WTF?!
                }

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

                this.engine.setProposedExecutor(new UserServerResponseExecutor(this.engine));

                this.engine.getSession().scheduleOutboundPacket(new UserChangePasswordPacket(new String(this.oldPassword.getPassword()), new String(pwd2)), new GenericFutureListener[]{
                        new GenericFutureListener() {
                            @Override
                            public void operationComplete(Future future) throws Exception {
                                UserAdministrationPanel.this.engine.getEventThread().postEvent(new ProgressBarUpdateEvent("Awaiting Server Response", 0, 3, 1));
                            }
                        }
                });
                break;

            case BACK:
                break;
        }
    }
}
