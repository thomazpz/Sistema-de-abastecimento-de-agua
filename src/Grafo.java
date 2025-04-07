//Italo Viegas Silva 10418393
//Luiza Gomes Cruz 10416544
//Thomaz Palazzolo Filho 10417108

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
}
