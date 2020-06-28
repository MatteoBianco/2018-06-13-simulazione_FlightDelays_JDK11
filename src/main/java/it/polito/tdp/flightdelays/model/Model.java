package it.polito.tdp.flightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.flightdelays.db.FlightDelaysDAO;

public class Model {
	
	private FlightDelaysDAO dao;
	private Map<String, Airport> idMapAirports;
	private Map<String, Airline> idMapAirlines;
	private Graph<Airport, DefaultWeightedEdge> graph;
	private List<AirportPair> airportPairs;
	
	private Simulator simulator;
	
	public Model() {
		this.dao = new FlightDelaysDAO();
		this.idMapAirlines = new HashMap<>();
		this.idMapAirports = new HashMap<>();
		this.dao.loadAllAirlines(idMapAirlines);
		this.dao.loadAllAirports(idMapAirports);
	}
	
	public void createGraph(Airline airline) {
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.graph, this.idMapAirports.values());
		this.airportPairs = this.dao.getAllPairs(this.idMapAirports, airline);
		for(AirportPair ap : this.airportPairs) {
			if(! this.graph.containsEdge(ap.getA1(), ap.getA2()))
				Graphs.addEdge(this.graph, ap.getA1(), ap.getA2(), ap.getWeight());
		}
	}
	
	public List<AirportPair> worstCases() {
		List<AirportPair> output = new ArrayList<>(this.airportPairs);
		if(this.airportPairs.size() < 10) {
			Collections.sort(output, new Comparator<AirportPair>() {

				@Override
				public int compare(AirportPair o1, AirportPair o2) {
					return -(o1.getWeight().compareTo(o2.getWeight()));
				}
				
			});
		}
		else {
			List<AirportPair> temp = new ArrayList<>();
			Collections.sort(output, new Comparator<AirportPair>() {

				@Override
				public int compare(AirportPair o1, AirportPair o2) {
					return -(o1.getWeight().compareTo(o2.getWeight()));
				}
				
			});
			for(int i = 0; i < 10; i++) {
				temp.add(output.get(i));
			}
			output = new ArrayList<>(temp);
		}
			
		return output;
	}
	
	public List<Airline> getAllAirlines() {
		List<Airline> airlines = new ArrayList<>(this.idMapAirlines.values());
		Collections.sort(airlines, new Comparator<Airline>() {

			@Override
			public int compare(Airline o1, Airline o2) {
				return o1.getId().compareTo(o2.getId());
			}
			
		});
		return airlines;
	}

	public void simulate(Integer K, Integer V) {
		this.simulator = new Simulator();
		this.simulator.init(this.idMapAirports, this.dao.loadAllFlights(), K, V);
		this.simulator.run();
	}
	
	public Map<Integer, Integer> getDelays() {
		return this.simulator.getDelays();		
	}

}
