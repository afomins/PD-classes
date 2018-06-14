/*
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.noosa;

import android.graphics.RectF;

public class MovieClip extends Image {

	protected Animation curAnim;
	protected int curFrame;
	protected float frameTimer;
	protected boolean finished;
	
	public boolean paused = false;

	public Listener listener;
	
	public MovieClip() {
		super();
	}
	
	public MovieClip( Object tx ) {
		super( tx );
	}
	
	@Override
	public void update() {
		super.update();
		if (!paused) {
			updateAnimation();
		}
	}
	
	protected void updateAnimation() {
		if (curAnim != null && curAnim.delay > 0 && (curAnim.looped || !finished)) {
			
			int lastFrame = curFrame;
			
			frameTimer += Game.elapsed;
			while (frameTimer > curAnim.delay) {
				frameTimer -= curAnim.delay;

                // Draw only one animation frame
                curFrame = curAnim.frames.length - 1;
				if (curFrame == curAnim.frames.length - 1) {
					if (curAnim.looped) {
						curFrame = 0;
					}
					finished = true;
					if (listener != null) {
						listener.onComplete( curAnim );
						// This check can probably be removed
						if (curAnim == null) {
							return;
						}
					}
					
				} else {
					curFrame++;
				}
			}
			
			if (curFrame != lastFrame) {
				frame( curAnim.frames[curFrame] );
			}
			
		}
	}
	
	public void play( Animation anim ) {
		play( anim, false );
	}

	public void play( Animation anim, boolean force ) {
		
		if (!force && (curAnim != null) && (curAnim == anim) && (curAnim.looped || !finished)) {
			return;
		}
		
		curAnim = anim;
		curFrame = 0;
		finished = false;
		
		frameTimer = 0;
		
		if (anim != null) {
			frame( anim.frames[curFrame] );
		}
	}
	
	public static class Animation {
		
		public float delay;
		public RectF[] frames;
		public boolean looped;

        // PDPD: 
        public int pd3d_fps;
        public TextureFilm pd3d_film;
        public Object[] pd3d_frames;
        public String pd3d_name;

		public Animation( String name, int fps, boolean looped ) {
			this.delay = 1f / fps;
			this.looped = looped;
            pd3d_fps = fps;
            pd3d_name = name;
		}
		
		public Animation frames( RectF... frames ) {
			this.frames = frames;
			return this;
		}
		
		public Animation frames( TextureFilm film, Object... frames ) {
			this.frames = new RectF[frames.length];
			for (int i=0; i < frames.length; i++) {
				this.frames[i] = film.get( frames[i] );
			}
            pd3d_film = film;
            pd3d_frames = frames;
			return this;
		}
		
		public Animation clone(String name) {
            Animation a = new Animation( (name == null) ? pd3d_name : name, 
              Math.round( 1 / delay ), looped ).frames( frames );
            a.pd3d_fps = pd3d_fps;
            a.pd3d_film = pd3d_film;
            a.pd3d_frames = pd3d_frames;
			return a;
		}
	}
	
	public interface Listener {
		void onComplete( Animation anim );
	}
}
