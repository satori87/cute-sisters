package com.bbg.sisters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;

public class Item {
	Game game;

	public int tx = 0;
	public int ty = 0;

	public float x = 0;
	public float y = 0;

	public int type = 0;
	public int item = 0;

	public Body body;
	
	public boolean had = false;

	public boolean active = false;
	public long diesAt = 0;
	public boolean dies = false;

	public Item(Game game, int tx, int ty, int type, int color) {
		this.game = game;
		this.tx = tx;
		this.ty = ty;
		this.type = type;
		this.item = color;
		this.x = tx * 32 + 16;
		this.y = ty * 32+16;
		createBody();
	}

	public void createBody() {

		PolygonShape p;
		Fixture f;

		body = game.createBody(BodyType.DynamicBody, x, y);

		p = new PolygonShape();
		p.setAsBox((14f / Game.physicsScale) / 2f, (14f / Game.physicsScale) / 2f, new Vector2(0, 0), 0);

		f = game.addFixture(body, false, 0.5f, game.fric, 0, p, Game.CAT_ITEM,Game.MASK_ITEM);
		
		f.setUserData(this);

		body.setFixedRotation(true);
		body.setLinearDamping(0f);
		
		
		//body.setGravityScale(0);
	}

	public void pre(long tick) {
		if(dies && tick > diesAt) {
			active = false;
		}
		if (!active) {
			return;
		}
	}

	public void post(long tick) {
		if (!active) {
			return;
		}
		if(had) {
			active = false;
			game.player.give(type,item);
		}
		x = body.getPosition().x * Game.physicsScale;
		y = body.getPosition().y * Game.physicsScale;
	}

	public int coin = 0;
	public long coinStamp = 0;

	public void render() {
		if (!active) {
			return;
		}
		if(game.tick > coinStamp) {
			coinStamp = game.tick + 30;
			coin++;
			if(coin > 7) {
				coin = 0;
			}
		}
		float dx = x - 8;
		float dy = game.height - y - 8;
		switch (type) {
		case 0: // items
			game.draw(AssetLoader.items, dx, dy, item * 16, 0, 16, 16, false, true);
			break;
		case 1: // keys
			game.draw(AssetLoader.keys, dx, dy, item * 16, 0, 16, 16, false, true);
			break;
		case 2: // coins
			game.draw(AssetLoader.coins, dx, dy, coin * 16, item * 16, 16, 16, false, true);
			break;
		}

	}

}
