package it.polito.tdp.flightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.flightdelays.model.Airline;
import it.polito.tdp.flightdelays.model.Airport;
import it.polito.tdp.flightdelays.model.AirportPair;
import it.polito.tdp.flightdelays.model.Flight;

public class FlightDelaysDAO {

	public void loadAllAirlines(Map<String, Airline> idMapAirlines) {
		String sql = "SELECT id, airline from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(! idMapAirlines.containsKey(rs.getString("ID"))) {
					idMapAirlines.put(rs.getString("ID"), new Airline(rs.getString("ID"), rs.getString("airline")));
				}
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public void loadAllAirports(Map<String, Airport> idMapAirports) {
		String sql = "SELECT id, airport, city, state, country, latitude, longitude FROM airports";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(! idMapAirports.containsKey(rs.getString("id"))) {
					Airport airport = new Airport(rs.getString("id"), rs.getString("airport"), rs.getString("city"),
						rs.getString("state"), rs.getString("country"), rs.getDouble("latitude"), rs.getDouble("longitude"));
					idMapAirports.put(rs.getString("id"), airport);
				}
			}
			
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<AirportPair> getAllPairs(Map<String, Airport> idMapAirports, Airline airline) {
		String sql = "SELECT f.ORIGIN_AIRPORT_ID AS a1, f.DESTINATION_AIRPORT_ID AS a2, " + 
				"AVG(f.ARRIVAL_DELAY) AS delays " + 
				"FROM flights AS f, airports AS a1, airports AS a2 " + 
				"WHERE f.AIRLINE = ? AND f.ORIGIN_AIRPORT_ID = a1.ID AND f.ORIGIN_AIRPORT_ID = a2.ID " + 
				"GROUP BY f.ORIGIN_AIRPORT_ID, f.DESTINATION_AIRPORT_ID";
		
		List<AirportPair> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, airline.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(idMapAirports.containsKey(rs.getString("a1")) && idMapAirports.containsKey(rs.getString("a2"))) {
					result.add(new AirportPair(idMapAirports.get(rs.getString("a1")), 
							idMapAirports.get(rs.getString("a2")), 
							rs.getDouble("delays")));
				}
				else throw new RuntimeException("Errore, riempire l'idMap prima di generare il grafo.");
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public List<Flight> loadAllFlights() {
		String sql = "SELECT id, airline, flight_number, origin_airport_id, destination_airport_id, scheduled_dep_date, "
				+ "arrival_date, departure_delay, arrival_delay, air_time, distance FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("id"), rs.getString("airline"), rs.getInt("flight_number"),
						rs.getString("origin_airport_id"), rs.getString("destination_airport_id"),
						rs.getTimestamp("scheduled_dep_date").toLocalDateTime(),
						rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getInt("departure_delay"),
						rs.getInt("arrival_delay"), rs.getInt("air_time"), rs.getInt("distance"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}

