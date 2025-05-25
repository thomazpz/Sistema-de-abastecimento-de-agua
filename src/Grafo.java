// Autores: Italo Viegas Silva, Luiza Gomes Cruz, Thomaz Palazzolo Filho

import java.util.*;
import java.util.stream.*;

public class Grafo {
    // Lista de vértices do grafo
    public List<Integer> vertices = new ArrayList<>();
    // Mapa de adjacência: cada vértice aponta para uma lista de seus vizinhos (arestas de saída)
    public Map<Integer, List<Integer>> adjacencias = new HashMap<>();

    // Adiciona um vértice ao grafo, se ainda não existir
    public void inserirVertice(int vertice) {
        if (!vertices.contains(vertice)) {
            vertices.add(vertice);
            adjacencias.putIfAbsent(vertice, new ArrayList<>());
        }
    }

    // Adiciona uma aresta do vértice origem para o vértice destino
    public void inserirAresta(int origem, int destino) {
        inserirVertice(origem); // Garante que o vértice de origem existe
        inserirVertice(destino); // Garante que o vértice de destino existe
        adjacencias.computeIfAbsent(origem, k -> new ArrayList<>()).add(destino);
    }

    // Remove um vértice do grafo, junto com todas as arestas relacionadas
    public void removerVertice(int vertice) {
        vertices.remove(Integer.valueOf(vertice));
        adjacencias.remove(vertice);

        // Remove arestas que apontam para o vértice removido
        adjacencias.values().forEach(list -> list.remove(Integer.valueOf(vertice)));
    }

    // Remove uma aresta do vértice origem para o destino
    public void removerAresta(int origem, int destino) {
        if (adjacencias.containsKey(origem)) {
            adjacencias.get(origem).remove(Integer.valueOf(destino));
        }
    }

    // Determina a categoria de conexidade do grafo
    public String determinarCategoriaConexidade() {
        if (vertices.isEmpty()) return "C0 (Desconexo)";

        if (ehFortementeConexo()) return "C3 (Fortemente conexo)";
        if (ehFracoConexo()) return "C2 (Fraco conexo)";
        return "C0 (Desconexo)";
    }

    // Verifica se o grafo é fortemente conexo (todos os vértices alcançam todos os outros)
    private boolean ehFortementeConexo() {
        return vertices.stream()
                .allMatch(v -> dfs(v).size() == vertices.size());
    }

    // Verifica se o grafo é fracamente conexo (conectado se considerar arestas como não-direcionadas)
    private boolean ehFracoConexo() {
        Grafo naoDirecionado = new Grafo();
        naoDirecionado.vertices = new ArrayList<>(this.vertices);

        // Adiciona arestas nos dois sentidos (transforma em grafo não-direcionado)
        this.adjacencias.forEach((origem, destinos) -> {
            destinos.forEach(destino -> {
                naoDirecionado.inserirAresta(origem, destino);
                naoDirecionado.inserirAresta(destino, origem);
            });
        });

        return naoDirecionado.ehConexoNaoDirecionado();
    }

    // Verifica se um grafo não-direcionado é conexo
    private boolean ehConexoNaoDirecionado() {
        if (vertices.isEmpty()) return false;
        Set<Integer> visitados = dfs(vertices.get(0));
        return visitados.size() == vertices.size();
    }

    // Encontra todas as componentes fortemente conexas do grafo
    public List<Set<Integer>> encontrarComponentesFortementeConexas() {
        List<Set<Integer>> componentes = new ArrayList<>();
        Set<Integer> visitados = new HashSet<>();

        // Faz uma DFS em cada vértice não visitado
        for (int vertice : vertices) {
            if (!visitados.contains(vertice)) {
                Set<Integer> componente = new HashSet<>();
                Stack<Integer> pilha = new Stack<>();
                pilha.push(vertice);

                while (!pilha.isEmpty()) {
                    int atual = pilha.pop();
                    if (!visitados.contains(atual)) {
                        visitados.add(atual);
                        componente.add(atual);
                        adjacencias.getOrDefault(atual, Collections.emptyList())
                                .forEach(pilha::push);
                    }
                }
                componentes.add(componente);
            }
        }
        return componentes;
    }

    // Constrói o grafo reduzido a partir das componentes fortemente conexas
    public Grafo construirGrafoReduzido() {
        List<Set<Integer>> componentes = encontrarComponentesFortementeConexas();
        Grafo reduzido = new Grafo();

        // Associa cada vértice a um identificador de componente
        Map<Integer, Integer> verticeParaComponente = new HashMap<>();
        for (int i = 0; i < componentes.size(); i++) {
            for (int vertice : componentes.get(i)) {
                verticeParaComponente.put(vertice, i);
                reduzido.inserirVertice(i); // Cada componente vira um vértice no grafo reduzido
            }
        }

        // Cria arestas entre componentes (de acordo com as arestas do grafo original)
        for (int origem : vertices) {
            for (int destino : adjacencias.getOrDefault(origem, Collections.emptyList())) {
                int compOrigem = verticeParaComponente.get(origem);
                int compDestino = verticeParaComponente.get(destino);
                if (compOrigem != compDestino) {
                    reduzido.inserirAresta(compOrigem, compDestino);
                }
            }
        }

        return reduzido;
    }

    // Conta o total de arestas no grafo
    public int contarArestas() {
        return adjacencias.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    // Realiza uma busca em profundidade a partir de um vértice
    private Set<Integer> dfs(int vertice) {
        Set<Integer> visitados = new HashSet<>();
        Stack<Integer> pilha = new Stack<>();
        pilha.push(vertice);

        while (!pilha.isEmpty()) {
            int atual = pilha.pop();
            if (!visitados.contains(atual)) {
                visitados.add(atual);
                adjacencias.getOrDefault(atual, Collections.emptyList())
                        .forEach(pilha::push);
            }
        }
        return visitados;
    }

    // Calcula as distâncias de um vértice inicial para todos os outros vértices
    public Map<Integer, Integer> calcularDistanciasAPartirDe(int verticeInicial) {
        Map<Integer, Integer> distancias = new HashMap<>();
        Queue<Integer> fila = new LinkedList<>();
        
        // Inicialização
        vertices.forEach(v -> distancias.put(v, -1)); // -1 significa não alcançável
        distancias.put(verticeInicial, 0);
        fila.add(verticeInicial);
        
        while (!fila.isEmpty()) {
            int atual = fila.poll();
            
            for (int vizinho : adjacencias.getOrDefault(atual, Collections.emptyList())) {
                if (distancias.get(vizinho) == -1) { // Não visitado
                    distancias.put(vizinho, distancias.get(atual) + 1);
                    fila.add(vizinho);
                }
            }
        }
        
        return distancias;
    }

    // Mostra a componente conexa a partir de um vértice inicial
    public void mostrarComponenteConexa(int verticeInicial) {
        Map<Integer, Integer> distancias = calcularDistanciasAPartirDe(verticeInicial);
        
        System.out.println("\n=== COMPONENTE CONEXA A PARTIR DE " + verticeInicial + " ===");
        System.out.println("Vértice | Distância");
        
        distancias.entrySet().stream()
            .filter(entry -> entry.getValue() != -1)
            .sorted(Map.Entry.comparingByValue())
            .forEach(entry -> 
                System.out.printf("%6d  | %8d%n", entry.getKey(), entry.getValue()));
        
        long inalcançaveis = distancias.values().stream().filter(d -> d == -1).count();
        if (inalcançaveis > 0) {
            System.out.println("\nVértices inalcançáveis: " + inalcançaveis);
        }
    }

    // Encontra uma árvore geradora usando BFS
    public Grafo encontrarArvoreGeradoraBFS() {
        if (vertices.isEmpty()) return new Grafo();
        
        Grafo arvore = new Grafo();
        Set<Integer> visitados = new HashSet<>();
        Queue<Integer> fila = new LinkedList<>();
        
        int raiz = vertices.get(0);
        fila.add(raiz);
        visitados.add(raiz);
        arvore.inserirVertice(raiz);
        
        while (!fila.isEmpty()) {
            int atual = fila.poll();
            
            for (int vizinho : adjacencias.getOrDefault(atual, Collections.emptyList())) {
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    arvore.inserirVertice(vizinho);
                    arvore.inserirAresta(atual, vizinho);
                    fila.add(vizinho);
                }
            }
        }
        
        return arvore;
    }

    // Mostra a árvore geradora
    public void mostrarArvoreGeradora() {
        Grafo arvore = encontrarArvoreGeradoraBFS();
        
        System.out.println("\n=== ÁRVORE GERADORA ===");
        System.out.println("Arestas da árvore:");
        
        arvore.adjacencias.forEach((origem, destinos) -> {
            destinos.forEach(destino -> 
                System.out.println(origem + " — " + destino));
        });
        
        System.out.println("\nTotal de vértices: " + arvore.vertices.size());
        System.out.println("Total de arestas: " + arvore.contarArestas());
    }

    // Verifica se o grafo é euleriano (contém ciclo euleriano)
    public boolean ehEuleriano() {
        if (!ehFracoConexo()) return false;
        
        // Verifica o balanceamento de graus para grafos direcionados
        Map<Integer, Integer> grausEntrada = new HashMap<>();
        Map<Integer, Integer> grausSaida = new HashMap<>();
        
        vertices.forEach(v -> {
            grausSaida.put(v, adjacencias.getOrDefault(v, Collections.emptyList()).size());
            grausEntrada.put(v, 0);
        });
        
        adjacencias.values().forEach(destinos -> 
            destinos.forEach(d -> grausEntrada.put(d, grausEntrada.get(d) + 1)));
        
        for (int v : vertices) {
            if (grausEntrada.get(v) != grausSaida.get(v)) {
                return false;
            }
        }
        return true;
    }

    // Verifica se o grafo tem caminho euleriano
    public boolean temCaminhoEuleriano() {
        if (!ehFracoConexo()) return false;
        
        Map<Integer, Integer> grausEntrada = new HashMap<>();
        Map<Integer, Integer> grausSaida = new HashMap<>();
        
        vertices.forEach(v -> {
            grausSaida.put(v, adjacencias.getOrDefault(v, Collections.emptyList()).size());
            grausEntrada.put(v, 0);
        });
        
        adjacencias.values().forEach(destinos -> 
            destinos.forEach(d -> grausEntrada.put(d, grausEntrada.get(d) + 1)));
        
        int inicio = 0, fim = 0;
        for (int v : vertices) {
            int diff = grausSaida.get(v) - grausEntrada.get(v);
            if (Math.abs(diff) > 1) return false;
            if (diff == 1) inicio++;
            else if (diff == -1) fim++;
        }
        
        return (inicio == 0 && fim == 0) || (inicio == 1 && fim == 1);
    }

    // Verifica se o grafo pode ser hamiltoniano
    public boolean podeSerHamiltoniano() {
        if (vertices.size() < 2) return false;
        if (!ehFortementeConexo()) return false; // Precisa ser fortemente conexo
        
        // Heurística: vértices não podem ter grau de saída zero
        for (int v : vertices) {
            if (adjacencias.getOrDefault(v, Collections.emptyList()).isEmpty()) {
                return false;
            }
        }
        return true; // Pode ser (não garantido)
    }

    // Busca por ciclo hamiltoniano em grafo direcionado (usando backtracking)
    public List<Integer> encontrarCicloHamiltoniano() {
        List<Integer> ciclo = new ArrayList<>();
        if (vertices.isEmpty()) return ciclo;
        
        // Começa pelo primeiro vértice
        int verticeInicial = vertices.get(0);
        ciclo.add(verticeInicial);
        
        Set<Integer> visitados = new HashSet<>();
        visitados.add(verticeInicial);
        
        if (encontrarCicloHamiltonianoUtil(verticeInicial, visitados, ciclo)) {
            return ciclo;
        }
        return Collections.emptyList(); // Retorna lista vazia se não encontrar
    }

    // Método auxiliar para encontrar ciclo hamiltoniano
    private boolean encontrarCicloHamiltonianoUtil(int atual, Set<Integer> visitados, List<Integer> ciclo) {
        // Se visitamos todos os vértices
        if (ciclo.size() == vertices.size()) {
            // Verifica se o último vértice tem aresta para o primeiro (completa o ciclo)
            return adjacencias.getOrDefault(atual, Collections.emptyList())
                    .contains(ciclo.get(0));
        }
        
        // Percorre todos os vizinhos
        for (int vizinho : adjacencias.getOrDefault(atual, Collections.emptyList())) {
            if (!visitados.contains(vizinho)) {
                // Tenta adicionar o vizinho ao ciclo
                ciclo.add(vizinho);
                visitados.add(vizinho);
                
                if (encontrarCicloHamiltonianoUtil(vizinho, visitados, ciclo)) {
                    return true;
                }
                
                // Backtrack - remove o vizinho se não levar a solução
                ciclo.remove(ciclo.size() - 1);
                visitados.remove(vizinho);
            }
        }
        return false;
    }

    // Mostra graus de saída, entrada e total para cada vértice
    public void mostrarGrausDosVertices() {
        Map<Integer, Integer> grausSaida = new HashMap<>();
        Map<Integer, Integer> grausEntrada = new HashMap<>();
        
        vertices.forEach(v -> {
            grausSaida.put(v, adjacencias.getOrDefault(v, Collections.emptyList()).size());
            grausEntrada.put(v, 0);
        });
        
        adjacencias.values().forEach(destinos -> 
            destinos.forEach(d -> grausEntrada.put(d, grausEntrada.get(d) + 1)));
        
        System.out.println("\n=== GRAUS DOS VÉRTICES ===");
        System.out.println("Vértice | Grau Saída | Grau Entrada | Grau Total");
        
        vertices.stream().sorted().forEach(v -> {
            int total = grausSaida.get(v) + grausEntrada.get(v);
            System.out.printf("%6d  | %10d | %11d | %10d%n", 
                v, grausSaida.get(v), grausEntrada.get(v), total);
        });
    }

    // Encontra o menor caminho entre dois vértices (usando BFS para grafos não ponderados)
    public List<Integer> encontrarMenorCaminho(int origem, int destino) {
        if (!vertices.contains(origem) || !vertices.contains(destino)) {
            return Collections.emptyList(); // Vértices inválidos
        }

        // Estruturas para busca
        Map<Integer, Integer> predecessores = new HashMap<>();
        Queue<Integer> fila = new LinkedList<>();
        Set<Integer> visitados = new HashSet<>();

        // Inicialização
        predecessores.put(origem, -1); // -1 indica fim do caminho
        fila.add(origem);
        visitados.add(origem);

        // BFS
        while (!fila.isEmpty()) {
            int atual = fila.poll();

            // Se encontrou o destino, reconstrói o caminho
            if (atual == destino) {
                return reconstruirCaminho(predecessores, destino);
            }

            // Explora vizinhos
            for (int vizinho : adjacencias.getOrDefault(atual, Collections.emptyList())) {
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    predecessores.put(vizinho, atual);
                    fila.add(vizinho);
                }
            }
        }

        return Collections.emptyList(); // Sem caminho
    }

    // Método auxiliar para reconstruir o caminho a partir dos predecessores
    private List<Integer> reconstruirCaminho(Map<Integer, Integer> predecessores, int destino) {
        List<Integer> caminho = new ArrayList<>();
        int atual = destino;

        while (atual != -1) {
            caminho.add(atual);
            atual = predecessores.get(atual);
        }

        Collections.reverse(caminho); // Inverte para começar da origem
        return caminho;
    }
}