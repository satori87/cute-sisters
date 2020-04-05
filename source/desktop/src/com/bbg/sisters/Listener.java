package com.bbg.sisters;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class Listener implements ContactListener {

	Game game;

	public Listener(Game game) {
		this.game = game;
	}

	@Override
	public void beginContact(Contact contact) {

		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();
		Object ad = a.getUserData();
		Object bd = b.getUserData();
		if (ad instanceof Integer && (int) ad == 4 && bd instanceof Item) {
			Item i = (Item) bd;
			i.had = true;
		} else if (bd instanceof Integer && (int) bd == 4 && ad instanceof Item) {
			Item i = (Item) ad;
			i.had = true;
		}
		if (ad != null && ad instanceof Integer && (int) ad == 3) {
			game.player.footCount++;
		} else if (bd != null && bd instanceof Integer && (int) bd == 3) {
			game.player.footCount++;
		}
		Object od = null;
		Block k = null;
		if (ad != null && ad instanceof Block) {
			k = (Block) ad;
			od = bd;
		}
		if (bd != null && bd instanceof Block) {
			k = (Block) bd;
			od = ad;
		}
		if (k != null) {
			if (od != null && od instanceof Player) {
				
				k.hit();
			}
		}
		if (ad != null && ad instanceof Integer && (int) ad == 5) {
			if(bd != null && bd instanceof Player) {
				game.player.spike++;
			}
		} else if (bd != null && bd instanceof Integer && (int) bd == 5) {
			if(ad != null && bd instanceof Player) {
				game.player.spike++;
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();
		Object ad = a.getUserData();
		Object bd = b.getUserData();
		if (ad != null && ad instanceof Integer && (int) ad == 3) {
			game.player.footCount--;
		} else if (bd != null && bd instanceof Integer && (int) bd == 3) {
			game.player.footCount--;
		}
		if (ad != null && ad instanceof Integer && (int) ad == 5) {
			if(bd != null && bd instanceof Player) {
				game.player.spike--;
			}
		} else if (bd != null && bd instanceof Integer && (int) bd == 5) {
			if(ad != null && bd instanceof Player) {
				game.player.spike--;
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

	public static class CollisionData {

	}

};
