package pt.ulisboa.tecnico.cmov.hoponcmu.server;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;

final class Server {

	private ServerSocket socket;

	void start() throws Exception {

		final int PORT = 9999;
		final List<Client> clients = new ArrayList<Client>();

		Client client_1 = new Client("songoku", "1234");
		client_1.putCountry("Portugal");
		Client client_2 = new Client("vegeta", "4321");
		client_1.putCountry("Marrocos");

		clients.add(client_1);
		clients.add(client_2);

		socket = new ServerSocket(PORT);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Server now closed.");
				try {
					socket.close();
				} catch (Exception e) {
				}
			}
		});
		System.out.println("Server is accepting connections at " + PORT);

		while (!Thread.currentThread().isInterrupted()) {
			new Thread(new ServerThread(socket.accept(), clients)).start();
		}
	}

	private class ServerThread extends Thread  {

		private Socket _socket;
		private List<Client> _clients;

		private ServerThread(Socket socket, List<Client> clients) {
			_socket = socket;
			_clients = clients;
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

					switch (userRequest) {
					case LOGIN:
						serverAnswer = Client_login(request);
						break;
					case SIGN_UP:
						serverAnswer = Client_SignUp(request);
						break;
					default:
						System.out.println("N�o funcionou");
						break;
					}

					// Send Reply to Client
					byte[] reply = serverAnswer.toString().getBytes();
					DataOutputStream oos = new DataOutputStream(_socket.getOutputStream());
					oos.writeInt(reply.length);
					oos.write(reply);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (_socket != null) {
					try {
						_socket.close();
					} catch (Exception e) {
					}
				}
			}
		}

		private JSONObject Client_SignUp(JSONObject request) throws JSONException {
			JSONObject serverAnswer = new JSONObject();
			String username = request.getString(NetworkKey.USERNAME.toString());
			String password = request.getString(NetworkKey.PASSWORD.toString());
			String country = request.getString(NetworkKey.COUNTRY.toString());

			for (Client client : _clients) {
				if (client.compareusername(username)) {
					ServerReply serverReply = ServerReply.INVALID_USER;
					serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
					System.out.println("Tentativa de Sign UP mas username j� existe");
					return serverAnswer;
				}

				if (client.comparepassword(password)) {
					ServerReply serverReply = ServerReply.INVALID_PASS;
					serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
					System.out.println("Tentativa de Sign UP mas password j� existe");
					return serverAnswer;
				}
			}

			Client nwClient = new Client(username, password);
			nwClient.putCountry(country);
			_clients.add(nwClient);

			System.out.println("SIGN UP com SUCESSO");

			ServerReply serverReply = ServerReply.SUCESS;
			serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());

			return serverAnswer;
		}

		private JSONObject Client_login(JSONObject request) throws JSONException {
			JSONObject serverAnswer = new JSONObject();
			String username = request.getString(NetworkKey.USERNAME.toString());
			String password = request.getString(NetworkKey.PASSWORD.toString());

			for (Client client : _clients) {
				if (client.compareusername(username)) {
					if (client.comparepassword(password)) {
						// Login Efectuado
						ServerReply serverReply = ServerReply.SUCESS;
						serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
						System.out.println("Login Conseguido por " + username + " e a sua pass e" + password);
						return serverAnswer;
					}

					// Password Incorrecta
					ServerReply serverReply = ServerReply.WRONG_PASS;
					serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
					System.out
							.println("Login tentado por " + username + " mas a sua pass est� incorrecta : " + password);
					return serverAnswer;
				}
			}
			// Username nao existe
			ServerReply serverReply = ServerReply.WRONG_USER;
			serverAnswer.put(NetworkKey.REPLY_TYPE.toString(), serverReply.ordinal());
			System.out.println(" Login tentado mas o username nao existe ");
			return serverAnswer;

		}
	}
}