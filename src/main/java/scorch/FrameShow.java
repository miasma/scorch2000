package scorch;

/*
  Any class that can be used as scorch animation must implement this interface
*/

public interface FrameShow {
    // Returns false after the last frame of animation
    // has been drawn to let the animation
    // container know when to stop calling this method
    // update specifies whether or not newPixels should be called
    boolean drawNextFrame(boolean update);
}

