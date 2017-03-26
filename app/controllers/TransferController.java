package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;

import play.libs.ws.WSAPI;
import play.mvc.Controller;

@Singleton
public class TransferController extends Controller {

	private final WSAPI ws;

	@Inject
	public TransferController(final WSAPI ws) {
		this.ws = ws;
	}
}
