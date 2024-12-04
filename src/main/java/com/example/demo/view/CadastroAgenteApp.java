package com.example.demo.view;

import com.example.demo.model.Agente;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CadastroAgenteApp {

    private Stage stage;
    private Agente agente;
    private TableView<Agente> tableView;

    public CadastroAgenteApp(Stage stage, Agente agente, TableView<Agente> tableView) {
        this.stage = stage;
        this.agente = agente;
        this.tableView = tableView;
    }

    public void start(Stage cadastroStage) {
        // Logo reduzido
        Image logoImage = new Image("file:src/main/resources/images/valorant-logo.jpg");
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(120);
        logoView.setPreserveRatio(true);

        // Campos de texto
        Label nomeLabel = new Label("Nome:");
        nomeLabel.getStyleClass().add("label");
        TextField nomeField = new TextField();
        nomeField.setPromptText("Digite o nome");
        if (agente != null) nomeField.setText(agente.getNome());

        Label habilidadeLabel = new Label("Habilidade:");
        habilidadeLabel.getStyleClass().add("label");
        TextField habilidadeField = new TextField();
        habilidadeField.setPromptText("Digite a habilidade");
        if (agente != null) habilidadeField.setText(agente.getHabilidade());

        Label funcaoLabel = new Label("Função:");
        funcaoLabel.getStyleClass().add("label");
        TextField funcaoField = new TextField();
        funcaoField.setPromptText("Digite a função");
        if (agente != null) funcaoField.setText(agente.getFuncao());

        // Botão Salvar
        Button salvarButton = new Button("Salvar");
        salvarButton.getStyleClass().add("button-primary");
        salvarButton.setOnAction(e -> salvarAgente(nomeField, habilidadeField, funcaoField, cadastroStage));

        // Layout do formulário
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(nomeLabel, 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(habilidadeLabel, 0, 1);
        grid.add(habilidadeField, 1, 1);
        grid.add(funcaoLabel, 0, 2);
        grid.add(funcaoField, 1, 2);

        HBox buttonBox = new HBox(10, salvarButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, logoView, grid, buttonBox);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #0F1923;");

        // Configurar cena
        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add("file:src/main/resources/style.css");

        cadastroStage.setScene(scene);
        cadastroStage.setTitle((agente == null) ? "Adicionar Agente" : "Editar Agente");
        cadastroStage.show();
    }

    private void salvarAgente(TextField nomeField, TextField habilidadeField, TextField funcaoField, Stage cadastroStage) {
        try {
            String method = (agente == null) ? "POST" : "PUT";
            String urlStr = (agente == null)
                    ? "http://localhost:8080/api/agentes"
                    : "http://localhost:8080/api/agentes/" + agente.getId();

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String jsonInputString = mapper.writeValueAsString(new Agente(
                    nomeField.getText(),
                    habilidadeField.getText(),
                    funcaoField.getText()
            ));

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                exibirPopup("Sucesso", (agente == null) ? "Agente criado com sucesso!" : "Agente alterado com sucesso!", true);
                cadastroStage.close();
                atualizarTabela();
            } else {
                throw new Exception("Erro ao salvar o agente. Código de resposta: " + responseCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            exibirPopup("Erro", "Erro ao salvar o agente: " + ex.getMessage(), false);
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
        scene.getStylesheets().add("file:src/main/resources/style.css");
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void atualizarTabela() {
        try {
            tableView.getItems().clear();

            URL url = new URL("http://localhost:8080/api/agentes");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            ObjectMapper mapper = new ObjectMapper();
            List<Agente> lista = mapper.readValue(response.toString(), new TypeReference<List<Agente>>() {});
            tableView.getItems().addAll(lista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
