// Autores: Italo Viegas Silva, Luiza Gomes Cruz, Thomaz Palazzolo Filho

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

// Classe principal da aplicação
public class AplicacaoGrafo {
    private Grafo grafo = new Grafo();  // Objeto do grafo principal
    private String arquivo = "C:\\Users\\thoma\\Desktop\\projeto andre\\untitled\\grafo.txt"; // Caminho do arquivo do grafo
    private Scanner scanner = new Scanner(System.in); // Scanner para entrada do usuário

    public static void main(String[] args) {
        new AplicacaoGrafo().executar(); // Inicia a aplicação
    }

    // Loop principal da aplicação com menu
    public void executar() {
        while (true) {
            exibirMenu();
            String opcao = scanner.nextLine().toLowerCase();

            // Executa a ação com base na opção escolhida
            switch (opcao) {
                case "a": lerArquivo(); break;
                case "b": gravarArquivo(); break;
                case "c": inserirVertice(); break;
                case "d": inserirAresta(); break;
                case "e": removerVertice(); break;
                case "f": removerAresta(); break;
                case "g": mostrarConteudoArquivo(); break;
                case "h": mostrarListaAdjacencia(); break;
                case "i": mostrarMatrizAdjacencia(); break;
                case "j": verificarConexidadeEMostrarReduzido(); break;
                case "k": mostrarComponenteConexa(); break;
                case "l": mostrarArvoreGeradora(); break;
                case "m": verificarEuleriano(); break;
                case "n": verificarCaminhoEuleriano(); break;
                case "o": verificarHamiltoniano(); break;
                case "p": encontrarCicloHamiltoniano(); break;
                case "q": mostrarGrausDosVertices(); break;
                case "r": encontrarMenorCaminho(); break;
                case "s": System.exit(0); // Encerra o programa
                default: System.out.println("Opção inválida!");
            }
        }
    }
    
    // Mostra o menu de opções
    private void exibirMenu() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("a) Ler arquivo grafo.txt");
        System.out.println("b) Gravar grafo no arquivo");
        System.out.println("c) Inserir vértice");
        System.out.println("d) Inserir aresta");
        System.out.println("e) Remover vértice");
        System.out.println("f) Remover aresta");
        System.out.println("g) Mostrar conteúdo do arquivo");
        System.out.println("h) Mostrar lista de adjacencia");
        System.out.println("i) Mostrar matriz de adjacencia");
        System.out.println("j) Verificar conexidade e mostrar grafo reduzido");
        System.out.println("k) Mostrar componente conexa a partir de um vértice");
        System.out.println("l) Mostrar árvore geradora");
        System.out.println("m) Verificar se grafo é euleriano");
        System.out.println("n) Verificar se tem caminho euleriano");
        System.out.println("o) Verificar se pode ser hamiltoniano");
        System.out.println("p) Encontrar ciclo hamiltoniano");
        System.out.println("q) Mostrar graus dos vértices");
        System.out.println("r) Mostrar menor caminho");
        System.out.println("s) Sair");      
        System.out.print("Escolha uma opção: ");
    }

    // Mostra a componente conexa a partir de um vértice
    private void mostrarComponenteConexa() {
        try {
            System.out.print("Digite o vértice inicial para análise: ");
            int vertice = Integer.parseInt(scanner.nextLine());
            grafo.mostrarComponenteConexa(vertice);
        } catch (NumberFormatException e) {
            System.out.println("Erro: Digite um número válido!");
        }
    }

    // Lê os dados do grafo a partir do arquivo
    private void lerArquivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            grafo = new Grafo(); // Reinicia o grafo
            String linha;
            boolean lerVertices = false;
            boolean lerArestas = false;

            // Lê cada linha do arquivo
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;

                // Identifica se está lendo vértices ou arestas
                if (linha.equals("#vertices")) {
                    lerVertices = true;
                    lerArestas = false;
                    continue;
                } else if (linha.equals("#arestas")) {
                    lerVertices = false;
                    lerArestas = true;
                    continue;
                }

                // Lê os vértices
                if (lerVertices && !linha.startsWith("#")) {
                    int vertice = Integer.parseInt(linha);
                    grafo.inserirVertice(vertice);
                }
                // Lê as arestas
                else if (lerArestas && !linha.startsWith("#")) {
                    String[] partes = linha.split("\\s+");
                    if (partes.length >= 2) {
                        grafo.inserirAresta(
                                Integer.parseInt(partes[0]),
                                Integer.parseInt(partes[1])
                        );
                    }
                }
            }
            System.out.println("Grafo carregado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    // Salva o grafo no arquivo
    private void gravarArquivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(arquivo))) {
            pw.println("#tipo\n4\n#vertices");
            grafo.vertices.forEach(pw::println);

            pw.println("#arestas");
            grafo.adjacencias.forEach((origem, destinos) -> {
                destinos.forEach(destino -> pw.println(origem + " " + destino));
            });

            System.out.println("Grafo salvo com sucesso em " + arquivo);
        } catch (Exception e) {
            System.out.println("Erro ao gravar arquivo: " + e.getMessage());
        }
    }

    // Insere um novo vértice
    private void inserirVertice() {
        try {
            System.out.print("Digite o número do vértice: ");
            int vertice = Integer.parseInt(scanner.nextLine());

            if (!grafo.vertices.contains(vertice)) {
                grafo.inserirVertice(vertice);
                System.out.println("Vértice " + vertice + " inserido com sucesso!");
            } else {
                System.out.println("Erro: Vértice já existe!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro: Digite um número válido!");
        }
    }

    // Insere uma nova aresta
    private void inserirAresta() {
        try {
            System.out.print("Vértice de origem: ");
            int origem = Integer.parseInt(scanner.nextLine());
            System.out.print("Vértice de destino: ");
            int destino = Integer.parseInt(scanner.nextLine());

            grafo.inserirAresta(origem, destino);
            System.out.println("Aresta " + origem + "→" + destino + " adicionada com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Erro: Digite números válidos!");
        }
    }

    // Remove um vértice
    public void removerVertice() {
        try {
            System.out.print("Digite o vértice a remover: ");
            int vertice = Integer.parseInt(scanner.nextLine());

            if (grafo.vertices.contains(vertice)) {
                grafo.removerVertice(vertice);
                System.out.println("Vértice " + vertice + " removido com sucesso!");
            } else {
                System.out.println("Erro: Vértice não encontrado!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro: Digite um número válido!");
        }
    }

    // Remove uma aresta
    private void removerAresta() {
        try {
            System.out.println("\nArestas disponíveis:");
            grafo.adjacencias.forEach((origem, destinos) -> {
                destinos.forEach(destino -> System.out.println(origem + " → " + destino));
            });

            System.out.print("\nVértice de origem: ");
            int origem = Integer.parseInt(scanner.nextLine());
            System.out.print("Vértice de destino: ");
            int destino = Integer.parseInt(scanner.nextLine());

            if (grafo.adjacencias.getOrDefault(origem, Collections.emptyList()).contains(destino)) {
                grafo.removerAresta(origem, destino);
                System.out.println("Aresta " + origem + "→" + destino + " removida com sucesso!");
            } else {
                System.out.println("Erro: Aresta não encontrada!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro: Digite números válidos!");
        }
    }

    // Mostra o conteúdo do arquivo
    private void mostrarConteudoArquivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            System.out.println("\nConteúdo do arquivo:");
            br.lines().forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    // Mostra o grafo em forma de lista e matriz de adjacência
// Mostra o grafo em forma de lista e matriz de adjacência
private void mostrarListaAdjacencia() {
    if (grafo.vertices.isEmpty()) {
        System.out.println("\nO grafo está vazio!");
        return;
    }

    System.out.println("\n=== LISTA DE ADJACÊNCIA ===");
    grafo.vertices.stream()
            .sorted()
            .forEach(v -> {
                System.out.print(v + " → ");
                List<Integer> vizinhos = grafo.adjacencias.getOrDefault(v, Collections.emptyList());
                if (vizinhos.isEmpty()) {
                    System.out.print("Nenhum vizinho");
                } else {
                    vizinhos.forEach(d -> System.out.print(d + " "));
                }
                System.out.println();
            });
}

private void mostrarMatrizAdjacencia() {
    if (grafo.vertices.isEmpty()) {
        System.out.println("\nO grafo está vazio!");
        return;
    }

    System.out.println("\n=== MATRIZ DE ADJACÊNCIA ===");
    
    // Determina o tamanho máximo do número de vértice para formatação
    int maxLength = grafo.vertices.stream()
            .mapToInt(v -> String.valueOf(v).length())
            .max()
            .orElse(1);

    // Cabeçalho da matriz
    System.out.printf("%" + (maxLength + 2) + "s", "");
    for (int v : grafo.vertices.stream().sorted().collect(Collectors.toList())) {
        System.out.printf("%" + (maxLength + 1) + "d", v);
    }
    System.out.println();

    // Corpo da matriz
    for (int origem : grafo.vertices.stream().sorted().collect(Collectors.toList())) {
        System.out.printf("%" + (maxLength + 1) + "d ", origem);
        for (int destino : grafo.vertices.stream().sorted().collect(Collectors.toList())) {
            int valor = grafo.adjacencias.getOrDefault(origem, Collections.emptyList())
                    .contains(destino) ? 1 : 0;
            System.out.printf("%" + (maxLength + 1) + "d", valor);
        }
        System.out.println();
    }
}

    // Verifica a conexidade do grafo e mostra o grafo reduzido
    private void verificarConexidadeEMostrarReduzido() {
        if (grafo == null || grafo.vertices.isEmpty()) {
            System.out.println("Grafo vazio ou não carregado!");
            return;
        }

        System.out.println("\n=== ANÁLISE DE CONEXIDADE ===");
        System.out.println("Resultado: " + grafo.determinarCategoriaConexidade());
        System.out.println("Total de vértices: " + grafo.vertices.size());
        System.out.println("Total de arestas: " + grafo.contarArestas());

        // Mostra as componentes fortemente conexas
        List<Set<Integer>> componentes = grafo.encontrarComponentesFortementeConexas();
        System.out.println("\nComponentes Fortemente Conexas:");
        for (int i = 0; i < componentes.size(); i++) {
            System.out.println("Componente " + (i+1) + ": " + componentes.get(i));
        }

        // Mostra o grafo reduzido com base nas componentes
        Grafo reduzido = grafo.construirGrafoReduzido();
        System.out.println("\n=== GRAFO REDUZIDO ===");
        System.out.println("Lista de Adjacência:");
        reduzido.vertices.stream()
                .sorted()
                .forEach(v -> {
                    System.out.print("Componente " + v + " → ");
                    reduzido.adjacencias.getOrDefault(v, Collections.emptyList())
                            .forEach(d -> System.out.print("Componente " + d + " "));
                    System.out.println();
                });
    }

    // Conta o número total de arestas no grafo
    public int contarArestas() {
        int total = 0;
        for (List<Integer> destinos : grafo.adjacencias.values()) {
            total += destinos.size();
        }
        return total;
    }

    // Mostra a árvore geradora
    private void mostrarArvoreGeradora() {
        grafo.mostrarArvoreGeradora();
    }

    // Verifica se o grafo é euleriano
    private void verificarEuleriano() {
        System.out.println("O grafo " + (grafo.ehEuleriano() ? "é" : "não é") + " euleriano.");
    }

    // Verifica se o grafo tem caminho euleriano
    private void verificarCaminhoEuleriano() {
        System.out.println("O grafo " + (grafo.temCaminhoEuleriano() ? "tem" : "não tem") + " caminho euleriano.");
    }

    // Verifica se o grafo pode ser hamiltoniano
    private void verificarHamiltoniano() {
        System.out.println("O grafo " + (grafo.podeSerHamiltoniano() ? "pode ser" : "não atende às condições para ser") + " hamiltoniano.");
    }

    // Mostra os graus dos vértices
    private void mostrarGrausDosVertices() {
        grafo.mostrarGrausDosVertices();
    }

    // Encontra e mostra um ciclo hamiltoniano
    private void encontrarCicloHamiltoniano() {
        List<Integer> ciclo = grafo.encontrarCicloHamiltoniano();
        if (ciclo.isEmpty()) {
            System.out.println("Nenhum ciclo hamiltoniano encontrado.");
        } else {
            System.out.println("Ciclo Hamiltoniano encontrado: " + ciclo);
        }
    }

    // Encontra e mostra o menor caminho entre dois vértices
    private void encontrarMenorCaminho() {
        try {
            System.out.print("Vértice de origem: ");
            int origem = Integer.parseInt(scanner.nextLine());
            System.out.print("Vértice de destino: ");
            int destino = Integer.parseInt(scanner.nextLine());

            List<Integer> caminho = grafo.encontrarMenorCaminho(origem, destino);

            if (caminho.isEmpty()) {
                System.out.println("Não existe caminho de " + origem + " para " + destino + "!");
            } else {
                System.out.println("Menor caminho: " + caminho);
                System.out.println("Distância: " + (caminho.size() - 1) + " arestas");
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro: Digite números válidos!");
        }
    }
}