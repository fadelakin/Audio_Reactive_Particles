import ddf.minim.*;
import ddf.minim.signals.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;

Minim minim;
AudioPlayer player;
FFT fft;
PFont f;

Particle[] particles;

boolean fade = true;

void setup() {
  // visualization is screensize
  // size(800, 480);
  
  f = createFont("Arial", 48, true); // Arial, 16 point, anti-aliasing on
  
  size(displayWidth, displayHeight);
  smooth();
  
  background(0);
  
  colorMode(HSB, 360, 60, 60, 60);
  
  minim = new Minim(this);
  
  // Change "*.mp3" to your own mp3 file in the root folder or use any of the mp3 files I have provided.
  player = minim.loadFile("Levels.mp3", 512);
  player.loop();
  
  fft = new FFT(player.bufferSize(), player.sampleRate());
  
  particles = new Particle[fft.specSize()];
  for(int i = 0; i < fft.specSize(); i++) {
    particles[i] = new Particle(i);
  }
}

void draw() {
  pushStyle();
  colorMode(RGB, 360);
  fill(255);
  textAlign(CENTER);
  textFont(f, 82);
  text("Welcome to CodeDay!", width/2, 400);
  text("We'll be starting shortly.", width/2, 500);
  if(fade) {
    noStroke();
    fill(0, 25);
    rect(0, 0, width, height);
  } else {
    background(0);
    fill(255);
    textAlign(CENTER);
    textFont(f, 82);
    text("Welcome to CodeDay!", width/2, 400);
    text("We'll be starting shortly.", width/2, 500);
  }
  popStyle();
  
  fft.forward(player.mix);
  
  for (int i = 0; i < fft.specSize() - 1; i++) {
    particles[i].update(fft.getBand(i), player.mix.get(i*2));
    particles[i].render();
  }
}

// fades all particles and gives them tails
void keyPressed() {
  if (key == 'f') {
    fade = !fade;
  }
}

// stops program when player is closed
void stop() {
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
  
  void update(float _r, float _b) {
    loc.add(vel);
    
    if (loc.x < 0 || loc.x > width) {
      vel.x *= -1;
    }
    
    if (loc.y < 0 || loc.y > height) {
      vel.y *= -1;
    }
    
    radius = _r;
    radius = constrain(radius, 2, 100);
    
    b = map(_b, -1, 1, 0, 200);
  }
  
  void render() {
    stroke(h, s, b, 50);
    fill(h, s, b, 50);
    ellipse(loc.x, loc.y, radius*2, radius*2);
  }
}
