package service;

import java.io.IOException;
import dao.RecomendarDAO;
import model.Recomendar;
import spark.Request;
import spark.Response;

public class RecomendarService {
	private RecomendarDAO recomendacaoDAO;
	
	
	public RecomendarService() {
		try {
			recomendacaoDAO = new RecomendarDAO();
			recomendacaoDAO.conectar();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public Object add(Request request, Response response) {
		String sites_url = request.queryParams("sites_url");
		String usuario_nome = request.queryParams("usuario_nome");
		int seguro = Integer.parseInt(request.queryParams("seguro"));
		String classificacao = "";
		Recomendar recomendacao = new Recomendar(sites_url, usuario_nome, seguro);

		recomendacaoDAO.add(recomendacao);
		response.status(201); // 201 Created
		
		if (seguro == 1) {
			classificacao = "Seguro";
		} else {
			classificacao = "Não Seguro";
		}
		
		return usuario_nome + " fez uma recomendação de '" + sites_url + "' como " + classificacao + ".";
	}

	public Object get(Request request, Response response) {
		String sites_url = request.params(":sites_url");
		String usuario_nome = request.params(":usuario_nome");
		
		Recomendar recomendacao = (Recomendar) recomendacaoDAO.getRecomendacao(sites_url,usuario_nome);
		
		if (recomendacao != null) {
    	    response.header("Content-Type", "application/xml");
    	    response.header("Content-Encoding", "UTF-8");

            return "<recomendacao>\n" + 
            		"\t<nome>" + recomendacao.getUsuarioNome() + "</nome>\n" +
            		"\t<url>" + recomendacao.getSitesUrl() + "</url>\n" +
            		"\t<seguro>" + recomendacao.getSeguro() + "</seguro>\n" +
            		"</recomendacao>\n";
        } else {
            response.status(404); // 404 Not found
            return "Recomendacao " + sites_url + " nao encontrada.";
        }
	}

	public Object update(Request request, Response response) {
		String sites_url = request.params(":sites_url");
		String usuario_nome = request.params(":usuario_nome");
		
		Recomendar recomendacao = (Recomendar) recomendacaoDAO.getRecomendacao(sites_url,usuario_nome);

        if (recomendacao != null) {
        	//recomendacao.setSitesUrl(request.queryParams("sites_url"));
        	//recomendacao.setUsuarioNome(request.queryParams("usuario_nome"));
        	recomendacao.setSeguro(Integer.parseInt(request.queryParams("seguro")));

        	recomendacaoDAO.atualizarRecomendacao(recomendacao);
        	
            return sites_url;
        } else {
            response.status(404); // 404 Not found
            return "Recomendacao nao encontrada.";
        }

	}

	public Object remove(Request request, Response response) {
		String sites_url = request.params(":sites_url");
		String usuario_nome = request.params(":usuario_nome");

        Recomendar recomendacao = (Recomendar) recomendacaoDAO.getRecomendacao(sites_url,usuario_nome);

        if (recomendacao != null) {

            recomendacaoDAO.excluirRecomendacao(sites_url,usuario_nome);

            response.status(200); // success
        	return sites_url;
        } else {
            response.status(404); // 404 Not found
            return "Recomendacao nao encontrado.";
        }
	}

	public Object getAll(Request request, Response response) {
		StringBuffer returnValue = new StringBuffer("<recomendacoes type=\"array\">");
		for (Recomendar recomendacao : recomendacaoDAO.getRecomendacoes()) {
			returnValue.append("\n<recomendacao>\n" + 		
            		//"\t<nome>" + recomendacao.getUsuarioNome() + "</nome>\n" +
            		"\t<url>" + recomendacao.getSitesUrl() + "</url>\n" +
            		//"\t<seguro>" + recomendacao.getSeguro() + "</seguro>\n" +
            		"</recomendacao>\n");
		}
		returnValue.append("</recomendacoes>");
	    response.header("Content-Type", "application/xml");
	    response.header("Content-Encoding", "UTF-8");
		return returnValue.toString();
	}
	
	public Object count(Request request, Response response) {
		double porcentagem = 0.0;
		String resp = "";
		String sites_url = request.params(":sites_url");
		Recomendar qtd = (Recomendar) recomendacaoDAO.countUrlAppearences(sites_url);
		Recomendar qtdseguro = (Recomendar) recomendacaoDAO.countUrlSecureAppearences(sites_url);
		
		porcentagem = Double.valueOf((qtdseguro.getSeguro() / (double) qtd.getSeguro()) * 100);
		resp = String.format("%.2f", porcentagem);
		
		return "'" + sites_url + "' é considerado seguro por " + resp + "%" + " de nossos usuários que o classificaram.";
	}
}
