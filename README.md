Simple word chain builder in Clojure using simulated annealing
===============================================================

Jim Van Donsel
December 2015

Simple and naive simulated annealing experiment to find a chain of words
linking a start and end word, each differing by one letter.

Starting with an initial word, we make single letter changes, checking the
distance to the target word.  We try to reduce the distance on each change,
but we may increase the distance with a probability related to the temperature.

This is a work in progress an needs some improvements still.

As an example, after many trials it found a chain of 16 linked words between
"apple" and "cider" as:

[apple ample amole anole ankle ankee anker inker inter enter ender eider bider rider hider cider]

The dictionary used here is Apple's, located at /usr/share/dict/words, but any word list 
can be used.



