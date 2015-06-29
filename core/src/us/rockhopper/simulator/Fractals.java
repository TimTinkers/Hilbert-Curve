package us.rockhopper.simulator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Fractals implements ApplicationListener {

	OrthographicCamera camera;
	ShapeRenderer shapeRenderer;

	// Initial shape for seeding the curve
	int[][] hilbert = { { 0, 3 }, { 4, 1 } };
	int step = 1;
	int scale = 30;

	// Debounce for camera movement
	boolean up, down, left, right = false;

	/**
	 * Rotates the hilbert shape represented by the given array.
	 * 
	 * @param array
	 *            The array representation of the hilbert curve to rotate.
	 * @return The rotated array.
	 */
	public int[][] rotateClockwise(int[][] array) {
		int size = (int) Math.pow(2, step);
		int[][] newHilbert = new int[size][size];

		// Transpose
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				newHilbert[i][j] = array[j][i];
				newHilbert[j][i] = array[i][j];
			}
		}

		// Reverse every row
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				newHilbert[i][size - 1 - j] = array[j][i];
			}
		}

		// Update directions
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				int pos = newHilbert[i][j];
				switch (pos) {
				case 0:
					newHilbert[i][j] = 3;
					break;
				case 1:
					newHilbert[i][j] = 2;
					break;
				case 2:
					newHilbert[i][j] = 0;
					break;
				case 3:
					newHilbert[i][j] = 1;
					break;
				case 4:
					break;
				case 5:
					newHilbert[i][j] = 6;
					break;
				case 6:
					newHilbert[i][j] = 5;
					break;
				case 7:
					newHilbert[i][j] = 9;
					break;
				case 8:
					newHilbert[i][j] = 7;
					break;
				case 9:
					newHilbert[i][j] = 10;
					break;
				case 10:
					newHilbert[i][j] = 8;
					break;
				}
			}
		}

		return newHilbert;
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glDisable(GL20.GL_BLEND);

		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);

		// Camera movement
		if (up) {
			camera.position.set(camera.position.x, camera.position.y + 1f, 0);
		}
		if (down) {
			camera.position.set(camera.position.x, camera.position.y - 1f, 0);
		}
		if (left) {
			camera.position.set(camera.position.x - 1f, camera.position.y, 0);
		}
		if (right) {
			camera.position.set(camera.position.x + 1f, camera.position.y, 0);
		}

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, 1);
		for (int i = 0; i < Math.pow(2, step); ++i) {
			for (int j = 0; j < Math.pow(2, step); ++j) {
				int dat = hilbert[i][j];
				if (dat == 0) { // UP
					shapeRenderer.line(scale * i, scale * j, scale * i, scale
							* (j + 1));
				} else if (dat == 1) { // DOWN
					shapeRenderer.line(scale * i, scale * j, scale * i, scale
							* (j - 1));
				} else if (dat == 2) { // LEFT
					shapeRenderer.line(scale * i, scale * j, scale * (i - 1),
							scale * j);
				} else if (dat == 3) { // RIGHT
					shapeRenderer.line(scale * i, scale * j, scale * (i + 1),
							scale * j);
				} else if (dat == 5) { // UP-DOWN
					shapeRenderer.line(scale * i, scale * j, scale * i, scale
							* (j + 1));
					shapeRenderer.line(scale * i, scale * j, scale * i, scale
							* (j - 1));
				} else if (dat == 6) { // LEFT-RIGHT
					shapeRenderer.line(scale * i, scale * j, scale * (i - 1),
							scale * j);
					shapeRenderer.line(scale * i, scale * j, scale * (i + 1),
							scale * j);
				} else if (dat == 7) { // DOWN-LEFT
					shapeRenderer.line(scale * i, scale * j, scale * i, scale
							* (j - 1));
					shapeRenderer.line(scale * i, scale * j, scale * (i - 1),
							scale * j);
				} else if (dat == 8) { // DOWN-RIGHT
					shapeRenderer.line(scale * i, scale * j, scale * i, scale
							* (j - 1));
					shapeRenderer.line(scale * i, scale * j, scale * (i + 1),
							scale * j);
				} else if (dat == 9) { // UP-LEFT
					shapeRenderer.line(scale * i, scale * j, scale * i, scale
							* (j + 1));
					shapeRenderer.line(scale * i, scale * j, scale * (i - 1),
							scale * j);
				} else if (dat == 10) { // UP-RIGHT
					shapeRenderer.line(scale * i, scale * j, scale * i, scale
							* (j + 1));
					shapeRenderer.line(scale * i, scale * j, scale * (i + 1),
							scale * j);
				}
			}
		}
		shapeRenderer.end();
	}

	@Override
	public void create() {
		shapeRenderer = new ShapeRenderer();

		camera = new OrthographicCamera(scale * 2, scale * 2);
		camera.position.set(scale / 2f, scale / 2f, 0);

		// Initialize input processing
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(new InputAdapter() {

			@Override
			public boolean scrolled(int amount) {
				camera.zoom += amount / 20f;
				return true;
			}

			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.W) {
					up = true;
				}
				if (keycode == Keys.A) {
					left = true;
				}
				if (keycode == Keys.S) {
					down = true;
				}
				if (keycode == Keys.D) {
					right = true;
				}
				if (keycode == Keys.T) {
					fractal();
				}
				if (keycode == Keys.Q) {
					hilbert = rotateClockwise(hilbert);
				}
				return true;
			}

			@Override
			public boolean keyUp(int keycode) {
				if (keycode == Keys.W) {
					up = false;
				}
				if (keycode == Keys.A) {
					left = false;
				}
				if (keycode == Keys.S) {
					down = false;
				}
				if (keycode == Keys.D) {
					right = false;
				}
				return true;
			}
		});
		Gdx.input.setInputProcessor(multiplexer);
	}

	private void fractal() {
		int size = (int) Math.pow(2, step);
		int newSize = (int) Math.pow(2, step + 1);

		// Get rotated portions
		int[][] bottomLeft = rotateClockwise(hilbert);
		int[][] bottomRight = rotateClockwise(rotateClockwise(rotateClockwise(hilbert)));

		// Construct the new Hilbert
		int[][] newHilbert = new int[newSize][newSize];

		// Top left
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				newHilbert[i][j + size] = hilbert[i][j];
			}
		}

		// Top right
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				newHilbert[i + size][j + size] = hilbert[i][j];
			}
		}

		// Bottom left
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				newHilbert[i][j] = bottomLeft[i][j];
			}
		}

		// Bottom right
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				newHilbert[i + size][j] = bottomRight[i][j];
			}
		}

		// Connect the new Hilbert pieces
		int add_down = newHilbert[0][size];
		if (add_down == 0) {
			newHilbert[0][size] = 5; // UP-DOWN
		} else if (add_down == 4) {
			newHilbert[0][size] = 1; // DOWN
		} else if (add_down == 2) {
			newHilbert[0][size] = 7; // DOWN-LEFT
		} else if (add_down == 3) {
			newHilbert[0][size] = 9; // DOWN-RIGHT
		}

		int add_up = newHilbert[newSize - 1][size - 1];
		if (add_up == 1) {
			newHilbert[newSize - 1][size - 1] = 5;
		} else if (add_up == 2) {
			newHilbert[newSize - 1][size - 1] = 9;
		} else if (add_up == 3) {
			newHilbert[newSize - 1][size - 1] = 10;
		} else if (add_up == 4) {
			newHilbert[newSize - 1][size - 1] = 0;
		}

		int add_bridge = newHilbert[size - 1][size];
		if (add_bridge == 0) {
			newHilbert[size - 1][size] = 9;
		} else if (add_bridge == 1) {
			newHilbert[size - 1][size] = 10;
		} else if (add_bridge == 2) {
			newHilbert[size - 1][size] = 6;
		} else if (add_bridge == 4) {
			newHilbert[size - 1][size] = 3;
		}

		camera.position.set(scale / 2f * newSize, scale / 2f * newSize, 0);

		step++;
		scale /= 1.4f;
		hilbert = newHilbert;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}
}
