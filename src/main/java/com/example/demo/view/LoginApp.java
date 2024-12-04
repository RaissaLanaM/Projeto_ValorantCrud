package com.example.demo.view;


import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginApp extends Application {

    @Override
    public void start(Stage stage) {
        // Logo
        Image logoImage = new Image("file:src/main/resources/images/valorant-logo.jpg"); // Substitua pelo caminho da sua imagem
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(150);
        logoView.setPreserveRatio(true);

        // Campos de Login
        TextField usernameField = new TextField();
        usernameField.setPromptText("Digite seu usuário");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Digite sua senha");

        // Botão de Login
        Button loginButton = new Button("Entrar");
        loginButton.getStyleClass().add("button-primary");
        loginButton.setOnAction(e -> autenticarUsuario(usernameField.getText(), passwordField.getText(), stage));

        // Botão de Cadastrar Usuário
        Button cadastrarButton = new Button("Cadastrar Usuário");
        cadastrarButton.getStyleClass().add("button-secondary");
        cadastrarButton.setOnAction(e -> abrirTelaCadastro(stage));

        // Layout
        VBox layout = new VBox(15, logoView, usernameField, passwordField, loginButton, cadastrarButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("root");

        // Cena
        Scene scene = new Scene(layout, 400, 600);
        scene.getStylesheets().add("file:src/main/resources/style.css"); // Adicione o CSS

        stage.setScene(scene);
        stage.setTitle("Login - Valorant App");
        stage.show();
    }

    private void autenticarUsuario(String username, String password, Stage stage) {
        if (username.isEmpty() || password.isEmpty()) {
            exibirPopup("Erro", "Por favor, preencha todos os campos!", false);
            return;
        }

        try {
            // URL do endpoint de autenticação
            URL url = new URL("http://localhost:8080/api/usuarios/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            // Criar objeto JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInputString = objectMapper.writeValueAsString(new Usuario(username, password));

            // Enviar JSON na requisição
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Verificar resposta do servidor
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                exibirPopup("Sucesso", "Login bem-sucedido!", true);
                abrirTelaListagem(stage); // Ir para a tela de gerenciamento de agentes
            } else {
                exibirPopup("Erro", "Credenciais inválidas!", false);
            }
        } catch (Exception ex) {
            exibirPopup("Erro", "Erro ao conectar com o servidor: " + ex.getMessage(), false);
            ex.printStackTrace();
        }
    }

    private void abrirTelaListagem(Stage stage) {
        AgenteApp agenteApp = new AgenteApp();
        agenteApp.start(stage); // Leva para a tela de gerenciamento de agentes
    }

    private void abrirTelaCadastro(Stage stage) {
        CadastroApp cadastroApp = new CadastroApp(stage); // Construtor que aceita o Stage
        cadastroApp.start(new Stage()); // Abre a tela de cadastro de usuário
    }

    private void exibirPopup(String titulo, String mensagem, boolean sucesso) {
        Stage popupStage = new Stage();
        popupStage.setTitle(titulo);

        Label messageLabel = new Label(mensagem);
        messageLabel.getStyleClass().add(sucesso ? "popup-success" : "popup-error");

        Button closeButton = new Button("OK");
        closeButton.getStyleClass().add("button-primary");
        closeButton.setOnAction(e -> popupStage.close());

        VBox layout = new VBox(15, messageLabel, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #0F1923; -fx-border-color: #E61C5D; -fx-border-width: 2px;");

        Scene scene = new Scene(layout, 300, 150);
        scene.getStylesheets().add("file:src/main/resources/style.css"); // Adicione o CSS
        popupStage.setScene(scene);
        popupStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Classe auxiliar para serializar dados de login
    private static class Usuario {
        private String username;
        private String password;

        public Usuario(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

