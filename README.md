# Giffer

This is a library to encode, decode and create gif images and animations.
It is without dependencies to java.awt or any third party libraries and it
uses java 7 features - making it suitable for e.g. google appengine.

The library is written for comprehension and not speed or resource usage.
It has not been tested for performance as this was not one of my objectives. 

It is a fork of rtyley/animated-gif-lib-for-java brilliant library which 
provided me with a great starting point and in particular some really usefull 
unit tests. That library and (thus this is again) is a re-package of the Animated GIF
processing classes made available by Kevin Weiner.

Usage
=====

To create an animation from two images:
```
        int[] image1, image2; //argb arrays
        Giffer.create()
                .withLoopCount(0)
                .withFrameDelay(40)
                .addFrame(image1, 290, 360, 0, 0)
                .withFrameTransparency(BLUE)
                .addFrame(image2, 290, 360, 0, 0)
                .encode(outputstream);
```

Roadmap
=======
I figured it would be great to make this generic enough to be usefull for others too.
To achieve that the roadmap to a 1.0 version is as follows:

1.0 Bug fixes
0.9 Whatever is needed to publish it to the Central Maven repository
0.8 Tune the Giffer build pattern
0.7 Support Gif87a format
0.6 Comment and plain text extensions
0.5 Transparency and backgrounds

Resources
=========
There are plenty of really good sources for the GIF standard.

* https://www.w3.org/Graphics/GIF/spec-gif89a.txt
* https://en.wikipedia.org/wiki/GIF


Contribution
============

There are some big wholes in this implementation - I've screwed up on background and transparency
handling, and I'm not really thorough on testing. Help welcome!
