package service;

import java.io.IOException;
import dao.UsuarioDAO;
import model.Usuario;
import spark.Request;
import spark.Response;


public class UsuarioService {
	private UsuarioDAO usuarioDAO;
	
	
	public UsuarioService() {
		try {
			usuarioDAO = new UsuarioDAO();
			usuarioDAO.conectar();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public Object add(Request request, Response response) {
		String nome = request.queryParams("login");
		String email = request.queryParams("email");
		String senha = request.queryParams("senha");
		Usuario usuario = new Usuario(nome, senha, email);
		
		System.out.println(nome);
		System.out.println(email);
		System.out.println(senha);

		usuarioDAO.add(usuario);
		
		response.status(201); // 201 Created
		return "Usuário criado com sucesso!  ---  " + "Nome: " + nome + "  ---  Email: " + email + "  ---  Senha: " + senha;
	}

	public Object get(Request request, Response response) {
		String nome = request.params(":nome");
		
		Usuario usuario = (Usuario) usuarioDAO.getUsuario(nome);
		
		if (usuario != null) {
    	    response.header("Content-Type", "application/xml");
    	    response.header("Content-Encoding", "UTF-8");

            return "<usuario>\n" + 
            		"\t<nome>" + usuario.getNome() + "</nome>\n" +
            		"\t<senha>" + usuario.getSenha() + "</senha>\n" +
            		"\t<email>" + usuario.getEmail() + "</email>\n" +
            		"</site>\n";
        } else {
            response.status(404); // 404 Not found
            return "Usuario " + nome + " nao encontrado.";
        }
	}

	public Object update(Request request, Response response) {
		String nome = request.params(":nome");
        
		Usuario usuario = (Usuario) usuarioDAO.getUsuario(nome);

        if (usuario != null) {
        	usuario.setNome(request.queryParams("nome"));
        	usuario.setSenha(request.queryParams("senha"));
        	usuario.setEmail(request.queryParams("email"));

        	usuarioDAO.atualizarUsuario(usuario);
        	
            return nome;
        } else {
            response.status(404); // 404 Not found
            return "Usuario nao encontrado.";
        }

	}

	public Object remove(Request request, Response response) {
		String nome = request.params(":nome");

        Usuario usuario = (Usuario) usuarioDAO.getUsuario(nome);

        if (usuario != null) {

            usuarioDAO.excluirUsuario(nome);

            response.status(200); // success
        	return nome;
        } else {
            response.status(404); // 404 Not found
            return "Usuario nao encontrado.";
        }
	}

	public Object getAll(Request request, Response response) {
		StringBuffer returnValue = new StringBuffer("<usuarios type=\"array\">");
		for (Usuario usuario : usuarioDAO.getUsuarios()) {
			returnValue.append("\n<usuario>\n" + 
					"\t<nome>" + usuario.getNome() + "</nome>\n" +
            		"\t<senha>" + usuario.getNome() + "</senha>\n" +
            		"\t<email>" + usuario.getNome() + "</email>\n" +
            		"</usuario>\n");
		}
		returnValue.append("</usuarios>");
	    response.header("Content-Type", "application/xml");
	    response.header("Content-Encoding", "UTF-8");
		return returnValue.toString();
	}
}
