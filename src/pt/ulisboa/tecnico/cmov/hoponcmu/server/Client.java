package pt.ulisboa.tecnico.cmov.hoponcmu.server;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Client {
	
	private static final long serialVersionUID = -8807331723807741905L;
	private String _username , _password, _country;
	private SecretKey _sessionID;
	private HashMap<String, Integer> _quizesScore;
	private HashMap<String, Integer> _tourScore;
	
	Client(String username ,String password) throws NoSuchAlgorithmException{
		_username = username;
		_password = password;
		_country = null;
		_quizesScore = new HashMap<String, Integer>();
		_tourScore = new HashMap<String, Integer>();
		_tourScore.put("Lisboa", 0);
		_tourScore.put("Porto" , 0);
		
		_sessionID = createSessionID();
	}	

	
	

	private SecretKey createSessionID() throws NoSuchAlgorithmException {
		KeyGenerator sessionGenerator;
		SecretKey sessionID;
		Writer writer = null;
		//Generate Session ID
		sessionGenerator = KeyGenerator.getInstance("AES"); 	
		sessionGenerator.init(256);
		sessionID = sessionGenerator.generateKey();
		
		//write to file
		try {
			writer = new OutputStreamWriter(new FileOutputStream(_username + ".txt"), "utf-8");
			writer = new BufferedWriter(writer);
			writer.write(Base64.getEncoder().encodeToString(sessionID.getEncoded()));
			writer.close();
			System.out.println("This is " + _username +  " and secret key :     " + sessionID );
			return sessionID;	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sessionID;	
	}

	
	public boolean compareusername(String username) {
		return _username.equals(username);
	}


	public boolean comparepassword(String password) {
		return _password.equals(password);
	}
	
	String putCountry(String country) {
		_country = country;
		return _country;
	}


	public SecretKey getSessionID() {
		return _sessionID;
	}


	public String getCountry() {
		return _country;
	}


	public String getUsername() {
		return _username;
	}
	
	public String getPassword() {
		return _password;
	}




	public int getquizesScore(String monumentName) {
		int quizScore = _quizesScore.get(monumentName);
		return quizScore;
	}

	public void setquizesScore(String monumentName, int score) {
		if (_quizesScore.containsKey(monumentName)){
			return;
		}else {
			_quizesScore.put(monumentName , score);
		}
		return;
	}




	public int gettourScore(String key) {
		return _tourScore.get(key);
	}

	public int settourScore(String key, int points) {
		int tourScore = _tourScore.get(key) + points;
		_tourScore.remove(key);
		_tourScore.put(key, tourScore);
		return tourScore;
		
	}

	public boolean checkquizesScore(String monumentName) {
		return _quizesScore.keySet().contains(monumentName);
	}
	
}
