package com.example.demo.view;

import com.example.demo.model.Agente;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AgenteApp {

    private TableView<Agente> tableView;

    public void start(Stage stage) {
        // Logo no topo
        Image logoImage = new Image("file:src/main/resources/images/valorant-logo.jpg"); // Atualize o caminho se necessário
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(200);
        logoView.setPreserveRatio(true);

        // Configuração da tabela
        tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");

        // Configurar colunas da tabela (Nome, Habilidade, Função)
        TableColumn<Agente, String> nomeColumn = new TableColumn<>("Nome");
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Agente, String> habilidadeColumn = new TableColumn<>("Habilidade");
        habilidadeColumn.setCellValueFactory(new PropertyValueFactory<>("habilidade"));

        TableColumn<Agente, String> funcaoColumn = new TableColumn<>("Função");
        funcaoColumn.setCellValueFactory(new PropertyValueFactory<>("funcao"));

        tableView.getColumns().addAll(nomeColumn, habilidadeColumn, funcaoColumn);

        // Atualizar tabela ao iniciar
        atualizarTabela();

        // Layout principal
        VBox layout = new VBox(20, logoView, tableView, botoesCrud());
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #0F1923;");

        Scene scene = new Scene(layout, 700, 500);
        scene.getStylesheets().add("file:src/main/resources/style.css"); // Adicione o CSS

        stage.setScene(scene);
        stage.setTitle("Gerenciar Agentes");
        stage.show();
    }

    private void abrirTelaCadastro(Agente agente) {
        Stage cadastroStage = new Stage();
        CadastroAgenteApp cadastroApp = new CadastroAgenteApp(cadastroStage, agente, tableView);
        cadastroApp.start(cadastroStage);
    }

    private void atualizarTabela() {
        try {
            tableView.getItems().clear(); // Limpar tabela

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

            // Parsear JSON e adicionar agentes à tabela usando Jackson
            ObjectMapper mapper = new ObjectMapper();
            List<Agente> lista = mapper.readValue(response.toString(), new TypeReference<List<Agente>>() {});
            tableView.getItems().addAll(lista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox botoesCrud() {
        Button adicionarButton = new Button("Adicionar");
        adicionarButton.getStyleClass().add("button-primary");

        Button editarButton = new Button("Editar");
        editarButton.getStyleClass().add("button-secondary");

        Button excluirButton = new Button("Excluir");
        excluirButton.getStyleClass().add("button-danger");

        adicionarButton.setOnAction(e -> abrirTelaCadastro(null)); // Passa null para criar um novo agente
        editarButton.setOnAction(e -> abrirTelaCadastro(tableView.getSelectionModel().getSelectedItem())); // Passa o agente selecionado
        excluirButton.setOnAction(e -> excluirAgente());

        HBox hbox = new HBox(10, adicionarButton, editarButton, excluirButton);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    private void excluirAgente() {
        Agente agenteSelecionado = tableView.getSelectionModel().getSelectedItem();
        if (agenteSelecionado != null) {
            try {
                URL url = new URL("http://localhost:8080/api/agentes/" + agenteSelecionado.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200 || responseCode == 204) {
                    System.out.println("Agente excluído com sucesso.");
                    atualizarTabela(); // Atualizar tabela após exclusão
                } else {
                    throw new Exception("Erro ao excluir o agente. Código de resposta: " + responseCode);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro ao excluir o agente: " + ex.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecione um agente para excluir.");
            alert.showAndWait();
        }
    }
}
