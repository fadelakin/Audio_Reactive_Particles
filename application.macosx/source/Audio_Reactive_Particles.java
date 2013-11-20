import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.signals.*; 
import ddf.minim.analysis.*; 
import ddf.minim.effects.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Audio_Reactive_Particles extends PApplet {






Minim minim;
AudioPlayer player;
FFT fft;

Particle[] particles;

boolean fade = false;

public void setup() {
  size(640, 480);
  smooth();
  
  background(0);
  
  colorMode(HSB, 360, 100, 100, 100);
  
  minim = new Minim(this);
  
  player = minim.loadFile("Night.mp3", 1024);
  player.loop();
  
  fft = new FFT(player.bufferSize(), player.sampleRate());
  
  particles = new Particle[fft.specSize()];
  for(int i = 0; i < fft.specSize(); i++) {
    particles[i] = new Particle(i);
  }
}

public void draw() {
  pushStyle();
  colorMode(RGB, 255);
  if(fade) {
    noStroke();
    fill(0, 8);
    rect(0, 0, width, height);
  } else {
    background(0);
  }
  popStyle();
  
  fft.forward(player.mix);
  
  for (int i = 0; i < fft.specSize() - 1; i++) {
    particles[i].update(fft.getBand(i), player.mix.get(i*2));
    particles[i].render();
  }
}

public void keyPressed() {
  if (key == 'f') {
    fade = !fade;
  }
}

public void stop() {
  player.close();
  minim.stop();
  super.stop();
}

class Particle {
  PVector loc;
  PVector vel;
  
  float radius;
  float h;
  float s;
  float b;
  
  Particle(int id) {
    loc = new PVector(map(id, 0, fft.specSize(), 0, width), height/2);
    vel = new PVector(random(-1, 1), random(-1, 1));
    
    h = map(id, 0, fft.specSize(), 0, 360);
    s = 100;
    b = 100;
  }
  
  public void update(float _r, float _b) {
    loc.add(vel);
    
    if (loc.x < 0 || loc.x > width) {
      vel.x *= -1;
    }
    
    if (loc.y < 0 || loc.y > height) {
      vel.y *= -1;
    }
    
    radius = _r;
    radius = constrain(radius, 2, 100);
    
    b = map(_b, -1, 1, 0, 100);
  }
  
  public void render() {
    stroke(h, s, b, 50);
    fill(h, s, b, 20);
    ellipse(loc.x, loc.y, radius*2, radius*2);
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Audio_Reactive_Particles" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
