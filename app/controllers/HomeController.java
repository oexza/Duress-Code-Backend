package controllers;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.libs.ws.WSAPI;
import play.libs.ws.WSResponse;
import play.mvc.*;
import play.mvc.Http.Request;
import play.mvc.Http.RequestBody;
import views.html.*;

/**
 * This controller contains an action to handle HTTP requests to the
 * application's home page.
 */
@Singleton
public class HomeController extends Controller {

	private final WSAPI ws;

	@Inject
	public HomeController(final WSAPI ws) {
		this.ws = ws;
	}

	/**
	 * An action that renders an HTML page with a welcome message. The
	 * configuration in the <code>routes</code> file means that this method will
	 * be called when the application receives a <code>GET</code> request with a
	 * path of <code>/</code>.
	 */
	public Result index() {
		return ok(index.render("Your new application is ready."));
	}

	public CompletionStage<Result> duress() {
		final Request req = ctx().request();
		final RequestBody body = req.body();

		final Map<String, String[]> bodyParams = body.asFormUrlEncoded();
		
		final String accountNumber = bodyParams.get("accountNumber")[0];
		getAccountDetailsFromAccessBankApi(accountNumber);

		return getAccountDetailsFromAccessBankApi(accountNumber).thenApply(res -> ok(res));

	}

	public CompletionStage<JsonNode> getAccountDetailsFromAccessBankApi(final String accountNumber) {

		final ObjectNode body = Json.newObject();
		body.put("client_id", "58d688838d3cc0100040cba3");
		body.put("client_secret",
				"xhh7MKewKYcsH7TpkKLQ9EACAeMuCO5AYZUrAs27hUnXD0YHBOTrfByVcneD9E9so7UxpgpBRkDK8LZq6R2a9AsoBjA6Ai4mRXT0");
		body.put("grant_type", "client_credentials");

		final ObjectNode accountEnquiryBody = Json.newObject();
		accountEnquiryBody.put("accountnumber", accountNumber);
		accountEnquiryBody.put("bankcode", "044");

		final CompletionStage<WSResponse> accessTokenRequestPromise = ws.url("https://pwcstaging.herokuapp.com/oauth/token")
				.post(accountEnquiryBody);
		final CompletionStage<WSResponse> accountEnquiryPromise = ws
				.url("https://pwcstaging.herokuapp.com/account/validation")
				.setHeader("Authorization", "Bearer 37ef5c3f7c89bf6538d00e7661102ef63121f12d")
				.post(accountEnquiryBody);

		return accountEnquiryPromise.thenApply(response -> {
			final double accountBalance = response.asJson().get("data").get("availablebalance").asDouble();

			final ObjectNode result = Json.newObject();
			result.put("balance", accountBalance / 10);
			return result;
		});
	}

}
