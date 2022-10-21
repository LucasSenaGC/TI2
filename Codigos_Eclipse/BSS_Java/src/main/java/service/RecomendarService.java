package service;

import java.io.IOException;
import dao.RecomendarDAO;
import dao.SiteDAO;
import model.Recomendar;
import model.Site;
import spark.Request;
import spark.Response;

public class RecomendarService {
	private RecomendarDAO recomendacaoDAO;
	private SiteDAO siteDAO;
	
	
	public RecomendarService() {
		try {
			recomendacaoDAO = new RecomendarDAO();
			recomendacaoDAO.conectar();
			siteDAO = new SiteDAO();
			siteDAO.conectar();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public Object add(Request request, Response response) {
		String sites_url = request.queryParams("sites_url");
		String usuario_nome = request.queryParams("usuario_nome");
		int seguro = Integer.parseInt(request.queryParams("seguro"));
		String classificacao = "";
		
		if (siteDAO.getSite(sites_url) != null) {
			Recomendar recomendacao = new Recomendar(sites_url, usuario_nome, seguro);

			recomendacaoDAO.add(recomendacao);
			response.status(201); // 201 Created
			
			if (seguro == 1) {
				classificacao = "Seguro";
			} else {
				classificacao = "Não Seguro";
			}
			
			return usuario_nome + " fez uma recomendação de '" + sites_url + "' como " + classificacao + ".";
		} else {
			return "'" + sites_url + "' ainda não faz parte da lista de sites disponíveis para classificação. Seja o primeiro a adicioná-lo e, depois, tente novamente!";
		}
		
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
            //response.status(404); // 404 Not found
            return "Recomendação de " + usuario_nome + " sobre '" + sites_url + "' não foi encontrada.";
        }
	}

	public Object update(Request request, Response response) {
		String sites_url = request.queryParams("sites_url");
		String usuario_nome = request.queryParams("usuario_nome");
		int seguro = Integer.parseInt(request.queryParams("seguro"));
		
		Recomendar recomendacao = (Recomendar) recomendacaoDAO.getRecomendacao(sites_url,usuario_nome);

        if (recomendacao != null) {
        	Recomendar recomendacaoAtualizada = new Recomendar(sites_url, usuario_nome, seguro);

        	recomendacaoDAO.atualizarRecomendacao(recomendacaoAtualizada);
        	
            return "Recomendação de " + usuario_nome + " sobre '" + sites_url + "' foi atualizada com sucesso.";
        } else {
            //response.status(404); // 404 Not found
            return "Recomendação de " + usuario_nome + " sobre '" + sites_url + "' não foi encontrada.";
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
            //response.status(404); // 404 Not found
            return "Recomendação de " + usuario_nome + " sobre '" + sites_url + "' não foi encontrada.";
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
		Site site = (Site) siteDAO.getSite(sites_url);
			
		if (site != null) {
			Recomendar qtd = (Recomendar) recomendacaoDAO.countUrlAppearences(sites_url);
			Recomendar qtdseguro = (Recomendar) recomendacaoDAO.countUrlSecureAppearences(sites_url);
			
			if (qtd.getSeguro() > 0) {
				porcentagem = Double.valueOf((qtdseguro.getSeguro() / (double) qtd.getSeguro()) * 100);
				resp = String.format("%.2f", porcentagem);
				
				return "'" + sites_url + "' é considerado seguro por " + resp + "%" + " de nossos usuários que o classificaram.";
			} else {
				return "'" + sites_url + "' ainda não foi classificado por nenhum usuário. Seja o primeiro a recomendá-lo e, depois, tente novamente!";
			}
		} else {
			return "'" + sites_url + "' ainda não faz parte da lista de sites disponíveis para classificação. Seja o primeiro a adicioná-lo e, depois, tente novamente!";
		}
	}
}
