package pt.ulisboa.tecnico.cmov.hoponcmu.server;

public class Client {
	
	
	private static final long serialVersionUID = -8807331723807741905L;
	private String _username , _password, _country;
	// private sessionKey;
	
	
	Client(String username ,String password){
		_username = username;
		_password = password;
		_country = null;
		
		System.out.println(" Eu sou o utilizador " + username + " e a minha pass e " + password);
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
	
}
