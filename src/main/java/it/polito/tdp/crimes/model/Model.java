package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private SimpleWeightedGraph<String,DefaultWeightedEdge> grafo;
	private EventsDao dao;
	private List<String> percorsoMigliore;
	
	public Model() {
		dao = new EventsDao();
	}
	
	public List<String> getCategorie(){
		return dao.getCategorie();
	}
	
	public void creaGrafo(String categoria, int mese) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiunta dei vertici
		
		Graphs.addAllVertices(grafo, dao.getVertici(categoria, mese));
		
		// aggiunta degli archi
		
		for(Adiacenza a : dao.getAdiacenze(categoria, mese)) {
			if(this.grafo.getEdge(a.getV1(), a.getV2())==null) {
				Graphs.addEdgeWithVertices(grafo, a.getV1(), a.getV2(),a.getPeso());
			}
		}
		
		System.out.println("#Vertici: "+this.grafo.vertexSet().size());
		System.out.println("#Archi: "+this.grafo.edgeSet().size());
	}
	
	public List<Adiacenza> getArchi(){
		// calcolo il peso medio degli archi presenti nel grafo 
		
		double pesoMedio = 0.0;
		
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			pesoMedio += this.grafo.getEdgeWeight(e);
		}
		
		pesoMedio = pesoMedio/grafo.edgeSet().size();
		
		// filtro gli archi tenendo solo quelli che hanno peso maggiore del peso medio
		
		List<Adiacenza> result = new LinkedList<>();
		
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(grafo.getEdgeWeight(e)>pesoMedio) {
				result.add(new Adiacenza(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), grafo.getEdgeWeight(e)));
			}
		}
		
		return result;
		
	}
	
	public List<String> trovaPercorso(String sorgente, String destinazione){
		
		this.percorsoMigliore = new ArrayList<>();
		List<String> parziale = new ArrayList<>();
		parziale.add(sorgente);
		
		cerca(destinazione,parziale,0);
		
		return this.percorsoMigliore;
	}
	
	private void cerca(String destinazione, List<String> parziale, int livello) {
		// caso terminale
		
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			if(parziale.size()>percorsoMigliore.size()) {
				percorsoMigliore = new LinkedList<>(parziale);
			}
			return;
		}
		
		for(String vicino : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			
			if(!parziale.contains(vicino)) {
				parziale.add(vicino);
				cerca(destinazione,parziale,livello+1);
				parziale.remove(parziale.size()-1);
			}
		}
	}

	public SimpleWeightedGraph<String, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public List<String> getPercorsoMigliore() {
		return percorsoMigliore;
	}

	public void setPercorsoMigliore(List<String> percorsoMigliore) {
		this.percorsoMigliore = percorsoMigliore;
	}
	
	
	
	
	
}

