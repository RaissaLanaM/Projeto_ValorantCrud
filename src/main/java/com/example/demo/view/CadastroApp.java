package com.example.demo.view;

import com.example.demo.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CadastroApp {

    private Stage loginStage;

    public CadastroApp(Stage loginStage) {
        this.loginStage = loginStage; // Referência à tela de login
    }

    public void start(Stage cadastroStage) {
        // Logo no topo
        Image logoImage = new Image("file:src/main/resources/images/valorant-logo.jpg"); // Atualize o caminho se necessário
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(150);
        logoView.setPreserveRatio(true);

        // Layout da tela de cadastro
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        // Campos de entrada
        Label usernameLabel = new Label("Usuário:");
        usernameLabel.getStyleClass().add("label");
        GridPane.setConstraints(usernameLabel, 0, 0);

        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Digite seu nome de usuário");
        GridPane.setConstraints(usernameInput, 1, 0);

        Label passwordLabel = new Label("Senha:");
        passwordLabel.getStyleClass().add("label");
        GridPane.setConstraints(passwordLabel, 0, 1);

        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Digite sua senha");
        GridPane.setConstraints(passwordInput, 1, 1);

        // Botão de cadastro
        Button registerButton = new Button("Cadastrar");
        registerButton.getStyleClass().add("button-primary");
        GridPane.setConstraints(registerButton, 1, 2);

        // Botão de voltar
        Button backButton = new Button("Voltar");
        backButton.getStyleClass().add("button-secondary");
        GridPane.setConstraints(backButton, 1, 3);

        // Ação do botão de cadastro
        registerButton.setOnAction(e -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();

            if (!username.isEmpty() && !password.isEmpty()) {
                // Chamar método para registrar o usuário
                registrarUsuario(username, password);
                usernameInput.clear();
                passwordInput.clear();
            } else {
                // Mostra popup personalizado de erro
                exibirPopup("Erro", "Por favor, preencha todos os campos!", false);
            }
        });

        // Ação do botão de voltar
        backButton.setOnAction(e -> {
            loginStage.show(); // Mostra a tela de login novamente
            cadastroStage.close(); // Fecha a tela de cadastro
        });

        // Adicionar elementos ao layout
        grid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, registerButton, backButton);

        // Agrupar logo e formulário
        VBox layout = new VBox(20, logoView, grid);
        layout.setAlignment(Pos.CENTER);

        // Configurar cena e aplicar estilo
        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add("file:src/main/resources/style.css"); // Adicione o CSS

        cadastroStage.setTitle("Cadastro de Usuário");
        cadastroStage.setScene(scene);
        cadastroStage.show();
    }

    private void registrarUsuario(String username, String password) {
        try {
            URL url = new URL("http://localhost:8080/api/usuarios/registrar");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            // Criar objeto Usuario
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(password);

            // Converter objeto em JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInputString = objectMapper.writeValueAsString(usuario);

            // Enviar JSON na requisição
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Obter resposta do servidor
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                exibirPopup("Cadastro Concluído", "Usuário cadastrado com sucesso!", true);
            } else {
                exibirPopup("Erro", "Erro ao cadastrar usuário. Código: " + responseCode, false);
            }
        } catch (Exception ex) {
            exibirPopup("Erro", "Erro ao conectar com o servidor: " + ex.getMessage(), false);
            ex.printStackTrace();
        }
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
}
