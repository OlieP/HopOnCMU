package pt.ulisboa.tecnico.cmov.hoponcmu.server;

import java.io.DataOutputStream;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.hoponcmu.server.Client;
import pt.ulisboa.tecnico.cmov.hoponcmu.server.NetworkKey;
import pt.ulisboa.tecnico.cmov.hoponcmu.server.ServerReply;
import pt.ulisboa.tecnico.cmov.hoponcmu.server.UserRequest;


import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

final class Server {

	void start() throws Exception {
		
		final int PORT = 9999;
		final List<Client> clients = new ArrayList<>();
	
		// HashMap <Tours (Ex.Lisboa) , monumentos(Ex. Convento de mafra....)>
		final HashMap<String, ArrayList<String>> Tours = new HashMap<>();
		// HashMap <Monumentos (Ex.Convento de mafra) , Perguntas(Ex. Qual foi?....)>
		final HashMap<String, ArrayList<String>> Questions = new HashMap<>();
		// HashMap <Monumentos (Ex.Convento de mafra) , Respostas para os Quizes(Ex. Qual foi?....)>
		final HashMap<String, JSONArray> Answers = new HashMap<>();
 
		
		
		Client client_1 = new Client("songoku", "1234");
		client_1.putCountry("Portugal");
		Client client_2 = new Client("vegeta", "4321");
		client_2.putCountry("Marrocos");
		
		clients.add(client_1);
		clients.add(client_2);
		ArrayList _Tours1 = new ArrayList<>();
		_Tours1.add("convento de mafra");
        _Tours1.add("torre de belem");
        _Tours1.add("estadio da luz");
        _Tours1.add("marques de pombal");
		Tours.put("Lisboa", _Tours1);
        
		ArrayList _Tours2 = new ArrayList<>();
        _Tours2.add("torre dos clerigos");
        _Tours2.add("palacio da bolsa");
        _Tours2.add("igreja dos congregados");
        _Tours2.add("estadio do dragao");
        Tours.put("Porto", _Tours2);
        
        ArrayList perguntas1 = new ArrayList<>();
        perguntas1.add(" 1 pergunta Este monumento reside em que cidade ");
        perguntas1.add(" Lisboa ");
        perguntas1.add(" Porto ");
        perguntas1.add(" Algarve ");
        perguntas1.add(" 2 pergunta Este monumento reside em que cidade ");
        perguntas1.add(" Lisboa ");
        perguntas1.add(" Porto ");
        perguntas1.add(" Algarve ");
        perguntas1.add(" 3 pergunta Este monumento reside em que cidade ");
        perguntas1.add(" Lisboa ");
        perguntas1.add(" Porto ");
        perguntas1.add(" Algarve ");
                
        Questions.put("convento de mafra", perguntas1 );
        Questions.put("torre de belem", perguntas1);
        Questions.put("estadio da luz", perguntas1);
        Questions.put("marques de pombal", perguntas1 );
        
        Questions.put("torre dos clerigos", perguntas1 );
        Questions.put("palacio da bolsa", perguntas1);
        Questions.put("igreja dos congregados", perguntas1);
        Questions.put("estadio do dragao", perguntas1 );
        
        JSONArray AnsInitializer = new JSONArray();
        AnsInitializer.put( 0, " Lisboa ");
        AnsInitializer.put(1, " Lisboa ");
        AnsInitializer.put(2, " Lisboa ");
        Answers.put("convento de mafra",  AnsInitializer );
        Answers.put("torre de belem",  AnsInitializer );
        Answers.put("estadio da luz",  AnsInitializer );
        Answers.put("marques de pombal",   AnsInitializer);
        
        JSONArray AnsInitializer2 = new JSONArray();
        AnsInitializer2.put( 0, " Porto ");
        AnsInitializer2.put(1, " Porto ");
        AnsInitializer2.put(2, " Porto ");
        Answers.put("torre dos clerigos", AnsInitializer2 );
        Answers.put("palacio da bolsa", AnsInitializer2);
        Answers.put("igreja dos congregados", AnsInitializer2);
        Answers.put("estadio do dragao", AnsInitializer2 );
        
		ServerSocket socket = new ServerSocket(PORT);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Server now closed.");
				try { socket.close(); }
				catch (Exception e) { }
			}
		});
		System.out.println("Server is accepting connections at " + PORT);	
		
		while (!Thread.currentThread().isInterrupted()) {
			new Thread(new ServerThread(socket.accept() , clients, Tours, Questions, Answers)).start();
		}
	}
	
	
	
	
	private class ServerThread implements Runnable {
		
		private Socket _socket;
		private List<Client> _clients;
		private  HashMap<String, ArrayList<String>> _tours;
		private HashMap<String, ArrayList<String>> _questions;
		private HashMap<String, JSONArray> _answers;
		
		private ServerThread(Socket socket, List<Client> clients,  HashMap<String, ArrayList<String>> tours, HashMap<String, ArrayList<String>> Questions, HashMap<String, JSONArray> Answers ) {
			_socket = socket;
			_clients = clients;
			_tours = tours;
			_questions = Questions;
			_answers = Answers;
		}
		

		@Override
		public void run() {
			
				JSONObject request, serverAnswer;
				try {

				
				// Receive Request from Client
				DataInputStream ois = new DataInputStream(_socket.getInputStream());
				int lengh = ois.readInt();
				byte[] message = new byte[lengh];
				ois.readFully(message, 0, lengh);
							
				request = new JSONObject(new String(message));
				System.out.println("Received: " + request);
			
				serverAnswer = new JSONObject();
				int requestTypeString;
				try {
					requestTypeString = request.getInt(NetworkKey.REQUEST_TYPE.toString());
					UserRequest userRequest = UserRequest.values()[requestTypeString];
					System.out.println(request);
					switch (userRequest) {
						case LOGIN:
							 serverAnswer = Client_login(request);
							break;
						case SIGN_UP:
							serverAnswer = Client_SignUp(request);
							break;
						case GET_MONUMENT_LIST:
							serverAnswer= Client_getMonumentList(request);
							break;
						case GET_QUIZ:
							serverAnswer = Client_getQuiz(request);
							break;
						case SUBMIT_QUIZ:
							serverAnswer = Client_submitQuiz(request);
							break;
						default:
							System.out.println("Não funcionou" + request);
							break;
					}
				
				 System.out.println(serverAnswer);
				 //Send Reply to Client
				 byte[] reply = serverAnswer.toString().getBytes();
		         DataOutputStream oos = new DataOutputStream(_socket.getOutputStream());
		         oos.writeInt(reply.length);
		         oos.write(reply);
						
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (_socket != null) {
						try { _socket.close(); }
						catch (Exception e) {}
					}
				}
			}
				
		private JSONObject Client_submitQuiz(JSONObject request) {
			JSONObject serverAnswer = new JSONObject();
			
			try {
				String username = request.getString(NetworkKey.USERNAME.toString());
				String monumentName = request.getString(NetworkKey.MONUMENT_NAME.toString());
				JSONArray clientAnswers = request.getJSONArray(NetworkKey.USER_ANSWERS.toString());
				int points = 0;
				
				if(_answers.containsKey(monumentName)) {
					if(_answers.get(monumentName).get(0).equals(clientAnswers.get(0))) {
						points = points + 3;
					}
					if(_answers.get(monumentName).get(1).equals(clientAnswers.get(1))) {
						points = points + 3;
					}
					if(_answers.get(monumentName).get(2).equals(clientAnswers.get(2))) {
						points = points + 3;
					}
				}
								
				System.out.println(" O utilizador "+ username + " Teve estes pontos : " + points);
				
				
				for (Client client : _clients) {
					if ( client.compareusername(username)) {
						
						
						for ( String key : _tours.keySet()) {
							if (_tours.get(key).contains(monumentName)){
								
								if (!client.checkquizesScore(monumentName)){
										client.setquizesScore( monumentName, points);
										client.settourScore( key, points);
										
										ServerReply serverReply = ServerReply.SUCESS;
										serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
										serverAnswer.put(NetworkKey.MONUMENT_NAME.toString(), monumentName.toString());
										serverAnswer.put(NetworkKey.MONUMENT_SCORE.toString(), client.getquizesScore(monumentName) );
										serverAnswer.put(NetworkKey.TOUR_NAME.toString(), key.toString() );
										serverAnswer.put(NetworkKey.TOUR_SCORE.toString(), client.gettourScore(key));
										return serverAnswer;
								}
								ServerReply serverReply = ServerReply.QUIZ_SUBMITED;
								serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
								return serverAnswer;
							}
								
						}
						
					}
				}
				ServerReply serverReply = ServerReply.ERROR;
				serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
				return serverAnswer;
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return serverAnswer;
		}


		private JSONObject Client_getQuiz(JSONObject request) {
			JSONObject serverAnswer = new JSONObject();
			try {
				String selectedMonument = request.getString(NetworkKey.MONUMENT_NAME.toString());
				if (_questions.containsKey(selectedMonument)) {
					ArrayList<String> questions= _questions.get(selectedMonument);
					
					ServerReply serverReply = ServerReply.SUCESS;
					serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
					serverAnswer.put(NetworkKey.MONUMENT_NAME.toString(), selectedMonument );
					serverAnswer.put(NetworkKey.QUIZ_INFO.toString(), questions.toArray());
					return serverAnswer;
				}
				ServerReply serverReply = ServerReply.ERROR;
				return serverAnswer;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return serverAnswer;
		}
		
		
		
		private JSONObject Client_getMonumentList(JSONObject request) {
			JSONObject serverAnswer = new JSONObject();
			try {
				
				
				if (_tours.containsKey(request.getString(NetworkKey.TOUR_NAME.toString()))){
				//Object[] monuments = _tours.get(NetworkKey.TOUR_NAME.toString());
				
				
				ArrayList<String> monuments = _tours.get(request.getString(NetworkKey.TOUR_NAME.toString()));
				
				ServerReply serverReply = ServerReply.SUCESS;
				serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
				serverAnswer.put(NetworkKey.MONUMENT_LIST.toString(), monuments.toArray());
				serverAnswer.put(NetworkKey.TOUR_NAME.toString(), request.getString(NetworkKey.TOUR_NAME.toString()));
				
				}
				return serverAnswer;
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return serverAnswer;
			
		}
	

		private JSONObject Client_SignUp(JSONObject request) throws JSONException {
			JSONObject serverAnswer = new JSONObject();
			String username = request.getString( NetworkKey.USERNAME.toString());
			String password = request.getString( NetworkKey.PASSWORD.toString());
			String country = request.getString(NetworkKey.COUNTRY.toString());
			
			for(Client client : _clients) {
				if ( client.compareusername(username)) {
					ServerReply serverReply = ServerReply.INVALID_USER;
					serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
					System.out.println("Tentativa de Sign UP mas username já existe");
					return serverAnswer;}
				
				if (client.comparepassword(password)) {
					ServerReply serverReply = ServerReply.INVALID_PASS;
					serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
					System.out.println("Tentativa de Sign UP mas password já existe");
					return serverAnswer;
					}
			}
			
			
			Client nwClient;
			try {
				nwClient = new Client(username , password);
				nwClient.putCountry(country);
				_clients.add(nwClient);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			
			
			System.out.println("SIGN UP com SUCESSO");
			
			ServerReply serverReply = ServerReply.SUCESS;
			serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
			serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());	
			serverAnswer.put(NetworkKey.TOUR_LIST.toString(), _tours.keySet().toArray());
		
			return serverAnswer;
		}
	
	
		private JSONObject Client_login(JSONObject request) throws JSONException {
			JSONObject serverAnswer = new JSONObject();
			String username = request.getString( NetworkKey.USERNAME.toString());
			String password = request.getString( NetworkKey.PASSWORD.toString());
			
			for(Client client : _clients) {
				if ( client.compareusername(username)) {
						if (client.comparepassword(password)) {
							// Login Efectuado
							System.out.println("Login Conseguido por " + username + " e a sua pass e" + password );
							
							ServerReply serverReply = ServerReply.SUCESS;
							serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());		
							serverAnswer.put(NetworkKey.SESSION_ID.toString(), client.getSessionID());
							serverAnswer.put(NetworkKey.COUNTRY.toString(), client.getCountry());
							serverAnswer.put(NetworkKey.TOUR_LIST.toString(), _tours.keySet().toArray());
							return serverAnswer;
							}
						
						// Password Incorrecta
						ServerReply serverReply = ServerReply.WRONG_PASS;
						serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
						System.out.println("Login tentado por " + username + " mas a sua pass está incorrecta : " + password );
						return serverAnswer;
				}
			}
			// Username nao existe
			ServerReply serverReply = ServerReply.WRONG_USER;
			serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
			System.out.println(" Login tentado mas o username nao existe " );
			return serverAnswer;
			
		}	
}
}