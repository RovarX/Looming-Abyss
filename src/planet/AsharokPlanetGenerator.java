package planet;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.Vars;
import mindustry.ai.Astar;
import mindustry.content.Blocks;
import mindustry.content.Loadouts;
import mindustry.game.Schematics;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.SteamVent;
import mindustry.world.blocks.environment.TallBlock;
import mindustry.world.meta.Attribute;

public class AsharokPlanetGenerator extends PlanetGenerator {

	public float heightScl = 0.7F;
	public float octaves = 7.0F;
	public float persistence = 0.65F;
	public float heightPow = 2.8F;
	public float heightMult = 1.8F;
	public static float arkThresh = 0.32F;
	public static float arkScl = 0.9F;
	public static int arkSeed = 11;
	public static int arkOct = 2;
	public static float liqThresh = 0.6F;
	public static float liqScl = 75.0F;
	public static float redThresh = 2.9F;
	public static float noArkThresh = 0.25F;
	public static int crystalSeed = 9;
	public static int crystalOct = 2;
	public static float crystalScl = 0.85F;
	public static float crystalMag = 0.25F;
	public static float airThresh = 0.14F;
	public static float airScl = 12.0F;

	Block[] terrain;

    public AsharokPlanetGenerator() {
        
		this.terrain = new Block[]{Blocks.regolith, Blocks.regolith, Blocks.rhyolite, Blocks.yellowStone, Blocks.redStone, Blocks.denseRedStone, Blocks.carbonStone, Blocks.carbonStone};
		this.baseSeed = 42; // 不同于 Erekir
		this.defaultLoadout = Loadouts.basicBastion;
	}

	public float getHeight(Vec3 position) {
		return Mathf.pow(this.rawHeight(position), this.heightPow) * this.heightMult;
	}

	public void getColor(Vec3 position, Color out) {
		Block block = this.getBlock(position);
		if (block == Blocks.crystallineStone) {
			block = Blocks.crystalFloor;
		}

		out.set(block.mapColor).a(1.0F - block.albedo);
	}

	public float getSizeScl() {
		return 2400.0F;
	}

	float rawHeight(Vec3 position) {
		return Simplex.noise3d(this.seed, (double)this.octaves, (double)this.persistence, (double)(1.0F / this.heightScl), (double)(10.0F + position.x), (double)(10.0F + position.y), (double)(10.0F + position.z));
	}

	float rawTemp(Vec3 position) {
		return position.dst(0.0F, 0.0F, 1.0F) * 2.0F - Simplex.noise3d(this.seed, (double)8.0F, (double)0.5F, (double)1.4F, (double)(10.0F + position.x), (double)(10.0F + position.y), (double)(10.0F + position.z)) * 2.5F;
	}

	Block getBlock(Vec3 position) {
		float px = position.x;
		float py = position.y;
		float pz = position.z;
		float heat = this.rawTemp(position);
		float height = this.rawHeight(position);
		height *= 1.15F;
		height = Mathf.clamp(height);
		Block result = this.terrain[Mathf.clamp((int)(height * (float)this.terrain.length), 0, this.terrain.length - 1)];

		if ((double)heat < 0.3 + (double)(Math.abs(Ridged.noise3d(this.seed + crystalSeed, (double)(px + 4.0F), (double)(py + 8.0F), (double)(pz + 1.0F), crystalOct, crystalScl)) * crystalMag)) {
			return Blocks.crystallineStone;
		} else if (!((double)heat < 0.6) || result != Blocks.rhyolite && result != Blocks.yellowStone && result != Blocks.regolith) {
			if (heat < redThresh - noArkThresh && Ridged.noise3d(this.seed + arkSeed, (double)(px + 2.0F), (double)(py + 8.0F), (double)(pz + 1.0F), arkOct, arkScl) > arkThresh) {
				result = Blocks.beryllicStone;
			}

			if (heat > redThresh) {
				result = Blocks.redStone;
			} else if (heat > redThresh - 0.35F) {
				result = Blocks.regolith;
			}

			return result;
		} else {
			return Blocks.carbonStone;
		}
	}

	public void genTile(Vec3 position, TileGen tile) {
		tile.floor = this.getBlock(position);
		if (tile.floor == Blocks.rhyolite && this.rand.chance(0.015)) {
			tile.floor = Blocks.rhyoliteCrater;
		}

		tile.block = tile.floor.asFloor().wall;
		if (Ridged.noise3d(this.seed + 1, (double)position.x, (double)position.y, (double)position.z, 2, airScl) > airThresh) {
			tile.block = Blocks.air;
		}

		if ((double)Ridged.noise3d(this.seed + 2, (double)position.x, (double)(position.y + 4.0F), (double)position.z, 3, 6.0F) > 0.6) {
			tile.floor = Blocks.carbonStone;
		}

	}

	protected void generate() {
		float temp = this.rawTemp(this.sector.tile.v);
		if ((double)temp > 0.6) {
			this.pass((xx, yx) -> {
				if (this.floor != Blocks.redIce) {
					float noise = this.noise((float)(xx + 512), (float)yx, (double)6.0F, (double)0.75F, (double)220.0F, (double)1.0F);
					if (noise > 0.6F) {
						if (noise > 0.625F) {
							this.floor = Blocks.slag;
						} else {
							this.floor = Blocks.yellowStone;
						}

						this.ore = Blocks.air;
					}

					if (noise > 0.53F && this.floor == Blocks.beryllicStone) {
						this.floor = Blocks.yellowStone;
					}
				}

			});
		}

		this.cells(4);
		this.pass((xx, yx) -> {
			if (this.floor == Blocks.regolith && this.noise((float)xx, (float)yx, (double)3.0F, (double)0.45F, (double)11.0F, (double)1.0F) > 0.58F) {
				this.block = Blocks.regolithWall;
			}

		});

		float length = (float)this.width / 2.4F;
		Vec2 trns = Tmp.v1.trns(this.rand.random(360.0F), length);
		int spawnX = (int)(trns.x + (float)this.width / 2.0F);
		int spawnY = (int)(trns.y + (float)this.height / 2.0F);
		int endX = (int)(-trns.x + (float)this.width / 2.0F);
		int endY = (int)(-trns.y + (float)this.height / 2.0F);
		float maxd = Mathf.dst((float)this.width / 2.0F, (float)this.height / 2.0F);
		this.erase(spawnX, spawnY, 15);
		this.brush(this.pathfind(spawnX, spawnY, endX, endY, (tilex) -> (tilex.solid() ? 300.0F : 0.0F) + maxd - tilex.dst((float)this.width / 2.0F, (float)this.height / 2.0F) / 10.0F, Astar.manhattan), 9);
		this.erase(endX, endY, 15);
		this.pass((xx, yx) -> {
			if (this.floor == Blocks.beryllicStone) {
				if (Math.abs(this.noise((float)xx, (float)yx + 400.0F, (double)5.0F, (double)0.6F, (double)36.0F, (double)1.0F) - 0.5F) < 0.09F) {
					this.floor = Blocks.arkyicStone;
				}

				if (!this.nearWall(xx, yx)) {
					float noise = this.noise((float)(xx + 280), (float)yx - (float)xx * 1.4F + 80.0F, (double)4.0F, (double)0.8F, (double)liqScl, (double)1.0F);
					if (noise > liqThresh) {
						this.floor = Blocks.arkyciteFloor;
					}

				}
			}
		});

		this.median(2, 0.6, Blocks.arkyciteFloor);
		this.blend(Blocks.arkyciteFloor, Blocks.arkyicStone, 4.0F);
		this.blend(Blocks.slag, Blocks.yellowStonePlates, 4.0F);
		this.distort(9.0F, 11.0F);
		this.distort(4.0F, 6.0F);
		this.median(2, 0.6, Blocks.arkyciteFloor);
		this.median(3, 0.6, Blocks.slag);
		this.pass((xx, yx) -> {
			if (this.noise((float)xx, (float)(yx + 550 + xx), (double)5.0F, (double)0.86F, (double)60.0F, (double)1.0F) < 0.41F && this.floor == Blocks.rhyolite) {
				this.floor = Blocks.roughRhyolite;
			}

			if (this.floor == Blocks.slag && Mathf.within((float)xx, (float)yx, (float)spawnX, (float)spawnY, 30.0F + this.noise((float)xx, (float)yx, (double)2.0F, (double)0.8F, (double)9.0F, (double)15.0F))) {
				this.floor = Blocks.yellowStonePlates;
			}

			if ((this.floor == Blocks.arkyciteFloor || this.floor == Blocks.arkyicStone) && this.block.isStatic()) {
				this.block = Blocks.arkyicWall;
			}

			float max = 0.0F;

			for(Point2 p : Geometry.d8) {
				max = Math.max(max, Vars.world.getDarkness(xx + p.x, yx + p.y));
			}

			if (max > 0.0F) {
				this.block = this.floor.asFloor().wall;
				if (this.block == Blocks.air) {
					this.block = Blocks.yellowStoneWall;
				}
			}

			if (this.floor == Blocks.yellowStonePlates && this.noise((float)(xx + 78 + yx), (float)yx, (double)3.0F, (double)0.8F, (double)6.0F, (double)1.0F) > 0.44F) {
				this.floor = Blocks.yellowStone;
			}

			if (this.floor == Blocks.redStone && this.noise((float)(xx + 78 - yx), (float)yx, (double)4.0F, (double)0.73F, (double)19.0F, (double)1.0F) > 0.63F) {
				this.floor = Blocks.denseRedStone;
			}

		});
		this.inverseFloodFill(this.tiles.getn(spawnX, spawnY));
		this.blend(Blocks.redStoneWall, Blocks.denseRedStone, 4.0F);
		this.erase(endX, endY, 6);
		this.tiles.getn(endX, endY).setOverlay(Blocks.spawn);
		this.pass((xx, yx) -> {
			if (this.block != Blocks.air) {
				if (this.nearAir(xx, yx)) {
					if (this.block == Blocks.carbonWall && this.noise((float)(xx + 78), (float)yx, (double)4.0F, (double)0.7F, (double)33.0F, (double)1.0F) > 0.52F) {
						this.block = Blocks.graphiticWall;
					} else if (this.block != Blocks.carbonWall && this.noise((float)(xx + 782), (float)yx, (double)4.0F, (double)0.8F, (double)38.0F, (double)1.0F) > 0.665F) {
						this.ore = Blocks.wallOreBeryllium;
					}
				}
			} else if (!this.nearWall(xx, yx)) {
				if (this.noise((float)(xx + 150), (float)(yx + xx * 2 + 100), (double)4.0F, (double)0.8F, (double)55.0F, (double)1.0F) > 0.76F) {
					this.ore = Blocks.oreTungsten;
				}

				if (this.noise((float)(xx + 999), (float)(yx + 600 - xx), (double)4.0F, (double)0.63F, (double)45.0F, (double)1.0F) < 0.27F && this.floor == Blocks.crystallineStone) {
					this.ore = Blocks.oreCrystalThorium;
				}
			}

			if (this.noise((float)(xx + 999), (float)(yx + 600 - xx), (double)5.0F, (double)0.8F, (double)45.0F, (double)1.0F) < 0.44F && this.floor == Blocks.crystallineStone) {
				this.floor = Blocks.crystalFloor;
			}

			if (this.block == Blocks.air && (this.floor == Blocks.crystallineStone || this.floor == Blocks.crystalFloor) && this.rand.chance(0.09) && this.nearWall(xx, yx) && !this.near(xx, yx, 4, Blocks.crystalCluster) && !this.near(xx, yx, 4, Blocks.vibrantCrystalCluster)) {
				this.block = this.floor == Blocks.crystalFloor ? Blocks.vibrantCrystalCluster : Blocks.crystalCluster;
				this.ore = Blocks.air;
			}

			if (this.block == Blocks.arkyicWall && this.rand.chance(0.23) && this.nearAir(xx, yx) && !this.near(xx, yx, 3, Blocks.crystalOrbs)) {
				this.block = Blocks.crystalOrbs;
				this.ore = Blocks.air;
			}

			if (this.block == Blocks.regolithWall && this.rand.chance(0.3) && this.nearAir(xx, yx) && !this.near(xx, yx, 3, Blocks.crystalBlocks)) {
				this.block = Blocks.crystalBlocks;
				this.ore = Blocks.air;
			}

		});
		this.pass((xx, yx) -> {
			if (this.ore.asFloor().wallOre || this.block.itemDrop != null || this.block == Blocks.air && this.ore != Blocks.air) {
				this.removeWall(xx, yx, 3, (b) -> b instanceof TallBlock);
			}

		});
		this.trimDark();
		int minVents = this.rand.random(6, 9);
		int ventCount = 0;

		label225:
		for(Tile tile : this.tiles) {
			Floor floor = tile.floor();
			if ((floor == Blocks.rhyolite || floor == Blocks.roughRhyolite) && this.rand.chance(0.002)) {
				int radius = 2;

				for(int x = -radius; x <= radius; ++x) {
					for(int y = -radius; y <= radius; ++y) {
						Tile other = this.tiles.get(x + tile.x, y + tile.y);
						if (other == null || other.floor() != Blocks.rhyolite && other.floor() != Blocks.roughRhyolite || other.block().solid) {
							continue label225;
						}
					}
				}

				++ventCount;

				for(Point2 pos : SteamVent.offsets) {
					Tile other = this.tiles.get(pos.x + tile.x + 1, pos.y + tile.y + 1);
					other.setFloor(Blocks.rhyoliteVent.asFloor());
				}
			}
		}

		int iterations = 0;
		int maxIterations = 5;

		while(ventCount < minVents && iterations++ < maxIterations) {
			label183:
			for(Tile tile : this.tiles) {
				if (this.rand.chance(1.8E-4 * (double)(1 + iterations)) && !Mathf.within((float)tile.x, (float)tile.y, (float)spawnX, (float)spawnY, 5.0F) && tile.floor() != Blocks.crystallineStone && tile.floor() != Blocks.crystalFloor) {
					int radius = 1;

					for(int x = -radius; x <= radius; ++x) {
						for(int y = -radius; y <= radius; ++y) {
							Tile other = this.tiles.get(x + tile.x, y + tile.y);
							if (other == null || other.block().solid || other.floor().attributes.get(Attribute.steam) != 0.0F || other.floor() == Blocks.slag || other.floor() == Blocks.arkyciteFloor) {
								continue label183;
							}
						}
					}

					Block floor = Blocks.rhyolite;
					Block secondFloor = Blocks.rhyoliteCrater;
					Block vent = Blocks.rhyoliteVent;
					int xDir = 1;
					if (tile.floor() != Blocks.beryllicStone && tile.floor() != Blocks.arkyicStone) {
						if (tile.floor() != Blocks.yellowStone && tile.floor() != Blocks.yellowStonePlates && tile.floor() != Blocks.regolith) {
							if (tile.floor() != Blocks.redStone && tile.floor() != Blocks.denseRedStone) {
								if (tile.floor() == Blocks.carbonStone) {
									floor = secondFloor = Blocks.carbonStone;
									vent = Blocks.carbonVent;
								}
							} else {
								floor = Blocks.denseRedStone;
								secondFloor = Blocks.redStone;
								vent = Blocks.redStoneVent;
								xDir = -1;
							}
						} else {
							floor = Blocks.yellowStone;
							secondFloor = Blocks.yellowStonePlates;
							vent = Blocks.yellowStoneVent;
						}
					} else {
						floor = secondFloor = Blocks.arkyicStone;
						vent = Blocks.arkyicVent;
					}

					++ventCount;

					for(Point2 pos : SteamVent.offsets) {
						Tile other = this.tiles.get(pos.x + tile.x + 1, pos.y + tile.y + 1);
						other.setFloor(vent.asFloor());
					}

					int crad = this.rand.random(6, 14);
					int crad2 = crad * crad;

					for(int cx = -crad; cx <= crad; ++cx) {
						for(int cy = -crad; cy <= crad; ++cy) {
							int rx = cx + tile.x;
							int ry = cy + tile.y;
							float rcy = (float)cy + (float)cx * 0.9F;
							if ((float)(cx * cx) + rcy * rcy <= (float)crad2 - this.noise((float)rx, (float)ry + (float)rx * 2.0F * (float)xDir, (double)2.0F, (double)0.7F, (double)8.0F, (double)((float)crad2 * 1.1F))) {
								Tile dest = this.tiles.get(rx, ry);
								if (dest != null && dest.floor().attributes.get(Attribute.steam) == 0.0F && dest.floor() != Blocks.roughRhyolite && dest.floor() != Blocks.arkyciteFloor && dest.floor() != Blocks.slag) {
									dest.setFloor(this.rand.chance(0.08) ? secondFloor.asFloor() : floor.asFloor());
									if (dest.block().isStatic()) {
										dest.setBlock(floor.asFloor().wall);
									}
								}
							}
						}
					}
				}
			}
		}

		for(Tile tile : this.tiles) {
			if (tile.overlay().needsSurface && !tile.floor().hasSurface()) {
				tile.setOverlay(Blocks.air);
			}
		}

		this.decoration(0.02F);
		Vars.state.rules.env = this.sector.planet.defaultEnv;
		Vars.state.rules.placeRangeCheck = true;
		Schematics.placeLaunchLoadout(spawnX, spawnY);
		Vars.state.rules.waves = false;
		Vars.state.rules.showSpawns = true;
	}
}
