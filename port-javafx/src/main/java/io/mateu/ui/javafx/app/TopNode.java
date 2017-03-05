package io.mateu.ui.javafx.app;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.javafx.JavafxPort;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;
import org.controlsfx.control.action.ActionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

import static org.controlsfx.control.action.ActionUtils.ACTION_SEPARATOR;
import static org.controlsfx.control.action.ActionUtils.ACTION_SPAN;

/**
 * Created by miguel on 9/8/16.
 */
public class TopNode extends ToolBar {

    private final Button botonLogin;
    private final ActionGroup actionLogout;
    private final MenuButton botonLogout;
    private MenuBar bar;

    public TopNode() {

        setPadding(new Insets(0, 10, 1, 10));

        setPrefWidth(5000);

        /*
        setStyle("-fx-background-color: #eaffea;");
        setPrefHeight(30);

        setPadding(new Insets(8));
        setSpacing(10);
        setAlignment(Pos.CENTER);

        getChildren().add(new Label("Welcome to " + JavafxPort.getApp().getName() + "!"));
        */

        getItems().add(bar = new MenuBar());

        //bar.setUseSystemMenuBar(true);
        bar.setStyle("-fx-background-insets: 0, 0 0 0 0;");

        buildMenuBar();

        final Pane spacer = new Pane();
        HBox.setHgrow(
                spacer,
                Priority.SOMETIMES
        );
        getItems().add(spacer);

        //getItems().add(new Separator());

        //setHgrow(areas, Priority.ALWAYS);

        getItems().add(new TextField("Search in " + JavafxPort.getApp().getName()));
        botonLogin = new Button("Login");
        actionLogout = new ActionGroup("Aquí el nombre del usuario", new Action("Profile", new Consumer<ActionEvent>() {
            @Override
            public void accept(ActionEvent actionEvent) {
                editProfile();
            }
        }), new Action("Change password", new Consumer<ActionEvent>() {
            @Override
            public void accept(ActionEvent actionEvent) {
                changePassword();
            }
        }), ACTION_SEPARATOR, new Action("Logout", new Consumer<ActionEvent>() {
            @Override
            public void accept(ActionEvent actionEvent) {
                JavafxPort.getApp().setUserData(null);
                getItems().remove(botonLogout);
                getItems().add(botonLogin);
                buildMenuBar();
            }
        }));

        botonLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                askForLogin();
            }
        });

        botonLogout = ActionUtils.createMenuButton(actionLogout, ActionUtils.ActionTextBehavior.SHOW);

        //MenuButton button = createMenuButton(action, textBehavior);
        //botonLogout.setFocusTraversable(false);
        botonLogout.getItems().addAll(toMenuItems(actionLogout.getActions()));

        if (JavafxPort.getApp().isAuthenticationNeeded()) getItems().add(botonLogin);
    }

    private void changePassword() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Change password");
        dialog.setHeaderText("Enter present and new password");

// Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Change it", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField oldPassword = new PasswordField();
        oldPassword.setPromptText("Old password");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New password");
        PasswordField newPasswordRepeated = new PasswordField();
        newPasswordRepeated.setPromptText("Repeat new password");

        grid.add(new Label("Old password:"), 0, 0);
        grid.add(oldPassword, 1, 0);
        grid.add(new Label("New password:"), 0, 1);
        grid.add(newPassword, 1, 1);
        grid.add(new Label("Repeat new password:"), 0, 2);
        grid.add(newPasswordRepeated, 1, 2);
        Label errorLabel;
        grid.add(errorLabel = new Label(), 0, 3, 3, 1);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setPrefWidth(300);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        oldPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean ko = false;
            ko |= oldPassword.getText() == null || "".equals(oldPassword.getText().trim());
            ko |= newPassword.getText() == null || "".equals(newPassword.getText().trim());
            ko |= newPasswordRepeated.getText() == null || "".equals(newPasswordRepeated.getText().trim());
            ko |= newPassword.getText() != null && !newPassword.getText().equals(newPasswordRepeated.getText());
            loginButton.setDisable(ko);
        });
        newPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean ko = false;
            ko |= oldPassword.getText() == null || "".equals(oldPassword.getText().trim());
            ko |= newPassword.getText() == null || "".equals(newPassword.getText().trim());
            ko |= newPasswordRepeated.getText() == null || "".equals(newPasswordRepeated.getText().trim());
            ko |= newPassword.getText() != null && !newPassword.getText().equals(newPasswordRepeated.getText());
            loginButton.setDisable(ko);
        });
        newPasswordRepeated.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean ko = false;
            ko |= oldPassword.getText() == null || "".equals(oldPassword.getText().trim());
            ko |= newPassword.getText() == null || "".equals(newPassword.getText().trim());
            ko |= newPasswordRepeated.getText() == null || "".equals(newPasswordRepeated.getText().trim());
            ko |= newPassword.getText() != null && !newPassword.getText().equals(newPasswordRepeated.getText());
            loginButton.setDisable(ko);
        });

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> oldPassword.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {

                return new Pair<>(oldPassword.getText(), newPassword.getText());
            }
            return null;
        });

        loginButton.addEventFilter(ActionEvent.ACTION, ae -> {
            ae.consume(); //not valid
            loginButton.setDisable(true);
            ((Button)loginButton).setText("Updating...");

            MateuUI.getBaseService().changePassword(JavafxPort.getApp().getUserData().getLogin(), oldPassword.getText(), newPassword.getText(), new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    dialog.close();
                }

                @Override
                public void onFailure(Throwable caught) {
                    errorLabel.setText("" + caught.getClass().getName() + ": " + caught.getMessage());
                    loginButton.setDisable(false);
                    ((Button)loginButton).setText("Change it");
                }
            });

        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
        });
    }

    private void editProfile() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Profile");
        dialog.setHeaderText("My profile");

// Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Update it", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Name");
        name.setText(JavafxPort.getApp().getUserData().getName());
        TextField email = new TextField();
        email.setPromptText("Email");
        email.setText(JavafxPort.getApp().getUserData().getEmail());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(email, 1, 1);
        Label errorLabel;
        grid.add(errorLabel = new Label(), 0, 3, 3, 1);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setPrefWidth(300);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        //loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).


        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> name.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {

                return new Pair<>(name.getText(), email.getText());
            }
            return null;
        });

        loginButton.addEventFilter(ActionEvent.ACTION, ae -> {
            ae.consume(); //not valid
            loginButton.setDisable(true);
            ((Button)loginButton).setText("Updating...");

            MateuUI.getBaseService().updateProfile(JavafxPort.getApp().getUserData().getLogin(), name.getText(), email.getText(), null, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    dialog.close();
                }

                @Override
                public void onFailure(Throwable caught) {
                    errorLabel.setText("" + caught.getClass().getName() + ": " + caught.getMessage());
                    loginButton.setDisable(false);
                    ((Button)loginButton).setText("Update it");
                }
            });

        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
        });
    }

    private static Collection<MenuItem> toMenuItems(Collection<? extends Action> actions) {
        ArrayList items = new ArrayList();
        Iterator var2 = actions.iterator();

        while(var2.hasNext()) {
            Action action = (Action)var2.next();
            if(action instanceof ActionGroup) {
                Menu menu = ActionUtils.createMenu(action);
                menu.getItems().addAll(toMenuItems(((ActionGroup)action).getActions()));
                items.add(menu);
            } else if(action == ACTION_SEPARATOR) {
                items.add(new SeparatorMenuItem());
            } else if(action != null && action != ACTION_SPAN) {
                items.add(ActionUtils.createMenuItem(action));
            }
        }

        return items;
    }


    private void askForLogin() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Authentication");
        dialog.setHeaderText("Enter username and password");

// Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        Label errorLabel;
        grid.add(errorLabel = new Label(), 0, 2, 3, 1);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setPrefWidth(300);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {

                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        loginButton.addEventFilter(ActionEvent.ACTION, ae -> {
            ae.consume(); //not valid
            loginButton.setDisable(true);
            ((Button)loginButton).setText("Checking...");

            MateuUI.getBaseService().authenticate(username.getText(), password.getText(), new Callback<UserData>() {
                @Override
                public void onSuccess(UserData result) {
                    JavafxPort.getApp().setUserData(result);
                    actionLogout.setText(result.getName());
                    getItems().remove(botonLogin);
                    getItems().add(botonLogout);
                    buildMenuBar();
                    dialog.close();
                }

                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                    errorLabel.setText("" + caught.getClass().getName() + ": " + caught.getMessage());
                    loginButton.setDisable(false);
                    ((Button)loginButton).setText("Login");
                }
            });

        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
        });
    }

    private void buildMenuBar() {

        AppNode.get().clearTabs();

        bar.getMenus().clear();

        for (AbstractArea a : JavafxPort.getApp().getAreas()) {

            boolean isPublic = a.isPublicAccess();
            if (!JavafxPort.getApp().isAuthenticationNeeded() || (isPublic && JavafxPort.getApp().getUserData() == null) || (!isPublic && JavafxPort.getApp().getUserData() != null)) {

                Menu m = new Menu(a.getName());

                for (AbstractModule mod : a.getModules()) {
                    buildMenu(m, mod);
                }

                if (m.getItems().size() > 0) bar.getMenus().add(m);

            }

        }

        AbstractView h = (JavafxPort.getApp().getUserData() != null)?JavafxPort.getApp().getPrivateHome():JavafxPort.getApp().getPublicHome();
        if (h != null) MateuUI.openView(JavafxPort.getApp().getPublicHome());

    }

    private void buildMenu(Menu m, AbstractModule mod) {
        for (MenuEntry e : mod.getMenu()) {
            buildMenu(m, e);
        }
    }

    private void buildMenu(Menu m, MenuEntry e) {
        if (e instanceof AbstractMenu) {
            Menu s = new Menu(e.getName());
            for (MenuEntry ee : ((AbstractMenu)e).getEntries()) {
                buildMenu(s, ee);
            }
            if (s.getItems().size() > 0) m.getItems().add(s);
        } else {
            MenuItem i;
            m.getItems().add(i = new MenuItem(e.getName()));
            i.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (e instanceof AbstractAction) {
                        ((AbstractAction)e).run();
                    }
                }
            });
        }
    }

}
