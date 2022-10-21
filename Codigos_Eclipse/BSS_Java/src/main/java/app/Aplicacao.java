package app;

import static spark.Spark.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import service.SiteService;
import service.UsuarioService;
import spark.Request;
import spark.Response;
import service.RecomendarService;


public class Aplicacao {
	private static SiteService siteService = new SiteService();
	private static UsuarioService usuarioService = new UsuarioService();
	private static RecomendarService recomendarService = new RecomendarService();
	
	public static int temMaiuscula(String senha){
		int resp = 0;
		
		for(int i = 0; i < senha.length() && resp == 0; i++){
			if(senha.charAt(i)>='A' && senha.charAt(i)<='Z')
				resp = 1;
		}
		
		return resp;
	}	
	public static int temNumero(String senha){
		int resp = 0;
		
		for(int i = 0; i < senha.length() && resp == 0;i++){
			if(senha.charAt(i)>='0' && senha.charAt(i)<='9')
				resp = 1;
		}
		
		return resp;
	}
	public static int temEspecial(String senha){
		int resp = 0;
		
		for(int i = 0; i < senha.length() && resp == 0; i++){
			if((senha.charAt(i)>='!' && senha.charAt(i)<='/')||(senha.charAt(i)>=':' && senha.charAt(i)<='@')  )
				resp = 1;
		}
		
		return resp;
	}
	
	public static Object passwordClassifier(Request request, Response response) throws Exception{
		String password = request.queryParams("password");
		String result = "";
		int length = 0;
		char strength = ' ';
		
		if (password.length() >= 8) {
			length = 1;
		}
		
		String passwordParameters = "[{\"specialCharacter\": " + temEspecial(password) + ",\"uppercase\": " + temMaiuscula(password) +",\">=8\": " + length + ",\"number\": " + temNumero(password) + ",\"strength\": " + 0 + "}]";
		
		String MODEL_URL = "http://04d5c46e-d2f8-4a68-8b73-738523fc89dd.southcentralus.azurecontainer.io/score";//encontrada na pagina de detalhes da API criada
		
		HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestAPI = HttpRequest.newBuilder().uri(URI.create(MODEL_URL))
        .headers("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(passwordParameters))
        .build();
		                
		HttpResponse<String> resp = client.send(requestAPI,HttpResponse.BodyHandlers.ofString());
		
		strength = resp.body().charAt(resp.body().indexOf("Scored Labels") + 17);
		
		if (strength == '0') {
			result = "A senha inserida é fraca. Tente adotar, caso já não tenha feito, caracteres especiais, números, letras maiúsculas e/ou aumentar o tamanho de sua senha.";
		} else if (strength == '1') {
			result = "A senha inserida é média. Tente adotar, caso já não tenha feito, caracteres especiais, números, letras maiúsculas e/ou aumentar o tamanho de sua senha.";
		} else if (strength == '2') {
			result = "A senha inserida é forte.";
		}
		
		return result;
	}
	
    public static void main(String[] args) throws IOException {
    	port(6789);

        post("/site", (request, response) -> siteService.add(request, response));

        get("/site/:url", (request, response) -> siteService.get(request, response));

        get("/site/update/:url", (request, response) -> siteService.update(request, response));

        get("/site/delete/:url", (request, response) -> siteService.remove(request, response));

        get("/site", (request, response) -> siteService.getAll(request, response));
        
        
        post("/usuario/create", (request, response) -> usuarioService.add(request, response));

        get("/usuario/:nome", (request, response) -> usuarioService.get(request, response));

        get("/usuario/update/:nome", (request, response) -> usuarioService.update(request, response));

        get("/usuario/delete/:nome", (request, response) -> usuarioService.remove(request, response));

        get("/usuario", (request, response) -> usuarioService.getAll(request, response));
        
        
        post("/recomendar", (request, response) -> recomendarService.add(request, response));

        get("/recomendar/:sites_url", (request, response) -> recomendarService.get(request, response));

        get("/recomendar/update/:sites_url", (request, response) -> recomendarService.update(request, response));

        get("/recomendar/delete/:sites_url", (request, response) -> recomendarService.remove(request, response));

        get("/recomendar", (request, response) -> recomendarService.getAll(request, response));
        
        get("/recomendar/count/:sites_url", (request, response) -> recomendarService.count(request, response));
        
        
        post("/password", (request, response) -> passwordClassifier(request, response));
    }
}