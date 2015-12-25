Simple probabilistic word chain builder in Clojure
===============================================================

Jim Van Donsel
December 2015

Experiment to find a chain of words linking a start and end word, each differing by one letter.

Starting with an initial word, we make single letter changes, checking the
distance to the target word. We try to reduce the distance on each change,
but we may increase the distance with a probability.  If simulated annealing
is enabled, we will use a probability based on temperature, otherwise we will
use a fixed probability.

As an example, after many trials it found a chain of only 16 linked words between
"apple" and "cider" as:

**[apple ample amole anole ankle ankee anker inker inter enter ender eider bider rider hider cider]**

The dictionary used here is Apple's, located at /usr/share/dict/words, but any word list 
can be used.



