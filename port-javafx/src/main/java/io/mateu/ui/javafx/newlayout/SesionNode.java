package io.mateu.ui.javafx.newlayout;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.javafx.JavafxPort;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.util.Pair;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;
import org.controlsfx.control.action.ActionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import static org.controlsfx.control.action.ActionUtils.ACTION_SEPARATOR;
import static org.controlsfx.control.action.ActionUtils.ACTION_SPAN;

public class SesionNode extends VBox {

    Property<UserData> userDataProperty = new SimpleObjectProperty<>();

    public SesionNode() {

        getStyleClass().add("sesion");

        ChangeListener<UserData> l;
        userDataProperty.addListener(l = new ChangeListener<UserData>() {
            @Override
            public void changed(ObservableValue<? extends UserData> observable, UserData oldValue, UserData newValue) {

                MateuUI.runInUIThread(new Runnable() {
                    @Override
                    public void run() {

                        getChildren().clear();

                        Label lt;
                        StackPane sp;
                        getChildren().add(sp = new StackPane(lt = new Label(JavafxPort.getApp().getName())));
                        lt.getStyleClass().add("tituloapp");

                        sp.setPadding(new Insets(10));

                        if (newValue == null) {

                            Button b;
                            getChildren().add(sp = new StackPane(b = new Button("Login")));
                            sp.setPadding(new Insets(10));
                            b.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {

                                    askForLogin();

                                }
                            });
                            b.getStyleClass().add("botonlogin");


                        } else {

                            Label xx;
                            getChildren().add(sp = new StackPane(xx = new Label("" + newValue.getName())));
                            xx.setStyle("-fx-text-fill: white;");

                            Circle cir2 = new Circle(120,120,50);
                            //cir2.setStroke(Color.SEAGREEN);
                            cir2.setFill(Color.SNOW);
                            //cir2.setEffect(new DropShadow(+25d, 0d, +2d, Color.DARKSEAGREEN));
                            cir2.setFill(new ImagePattern((newValue.getPhoto() != null)?new Image(newValue.getPhoto()):new Image(getClass().getResourceAsStream("/img/profile-pic-300px.jpg"))));
                            getChildren().add(sp = new StackPane(cir2));
                            sp.setPadding(new Insets(10, 10, 0, 10));

                            //getChildren().add(new ImageView((newValue.getPhoto() != null)?new Image(newValue.getPhoto()):new Image(getClass().getResourceAsStream("/img/profile-pic-300px.jpg"))));



                            HBox hb;
                            getChildren().add(hb = new HBox());
                            FontAwesomeIconView iv;
                            hb.getChildren().add(sp = new StackPane(iv = new FontAwesomeIconView(FontAwesomeIcon.COG)));
                            sp.setAlignment(Pos.CENTER_RIGHT); // actúa dentro del sp
                            sp.setPrefWidth(170);
                            sp.setPadding(new Insets(0));
                            //sp.setStyle("-fx-border-color: white;");

                            iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {

                                    MenuItem mi0;
                                    MenuItem mi1;
                                    MenuItem mi2;
                                    MenuItem mi3;
                                    ContextMenu m = new ContextMenu(
                                            mi0 = new MenuItem("Edit profile"),
                                            mi1 = new MenuItem("Change password"),
                                            mi2 = new MenuItem("Change photo"),
                                            mi3 = new MenuItem("Sign out"));
                                    m.getStyleClass().add("menuopcionesperfil");

                                    mi0.setOnAction((e) -> editProfile());
                                    mi1.setOnAction((e) -> changePassword());
                                    mi2.setOnAction((e) -> changePhoto());
                                    mi3.setOnAction((e) -> logout());


                                    Bounds boundsInScreen = iv.localToScreen(iv.getBoundsInLocal());

                                    m.show(iv, boundsInScreen.getMinX(), boundsInScreen.getMaxY());

                                }
                            });


                        }

                        getChildren().add(sp = new StackPane(new MenuNode(newValue != null)));
                        sp.setPadding(new Insets(30));

                    }
                });

            }
        });

        l.changed(null, null, null);

    }

    private Pane crearMenuOpcionesPerfil() {
        StackPane sp = new StackPane();
        sp.setPrefWidth(100);
        sp.setPrefHeight(100);
        sp.setStyle("-fx-background-color: red;");
        return sp;
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


    public void askForLogin() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Authentication");
        dialog.setHeaderText("Enter username and password");

// Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        ButtonType forgotPasswordButtonType = new ButtonType("Forgot password", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(forgotPasswordButtonType, loginButtonType, ButtonType.CANCEL);

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
        Node forgotPasswordButton = dialog.getDialogPane().lookupButton(forgotPasswordButtonType);
        forgotPasswordButton.setDisable(true);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
            forgotPasswordButton.setDisable(newValue.trim().isEmpty());
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

        forgotPasswordButton.addEventFilter(ActionEvent.ACTION, ae -> {
            ae.consume(); //not valid
            forgotPasswordButton.setDisable(true);
            ((Button)forgotPasswordButton).setText("Checking...");

            MateuUI.getBaseService().forgotPassword(username.getText(), new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    errorLabel.setText("You will receive an email to recover your password");
                    forgotPasswordButton.setDisable(false);
                    ((Button)forgotPasswordButton).setText("Forgot password");
                }

                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                    errorLabel.setText("" + caught.getClass().getName() + ": " + caught.getMessage());
                    forgotPasswordButton.setDisable(false);
                    ((Button)forgotPasswordButton).setText("Forgot password");
                }
            });

        });

        loginButton.addEventFilter(ActionEvent.ACTION, ae -> {
            ae.consume(); //not valid
            loginButton.setDisable(true);
            ((Button)loginButton).setText("Checking...");

            MateuUI.getBaseService().authenticate(username.getText(), password.getText(), new Callback<UserData>() {
                @Override
                public void onSuccess(UserData result) {
                    JavafxPort.getApp().setUserData(result);


                    /*
                    actionLogout.setText(result.getName());
                    getItems().remove(botonLogin);
                    getItems().add(botonLogout);
                    buildMenuBar();
                    */

                    userDataProperty.setValue(result);


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

    public void logout() {
        JavafxPort.getApp().setUserData(null);
        userDataProperty.setValue(null);
    }

    public void changePhoto() {

    }

}
